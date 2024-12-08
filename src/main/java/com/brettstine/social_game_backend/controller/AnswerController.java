package com.brettstine.social_game_backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brettstine.social_game_backend.model.AnswerModel;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.GameState;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.QuestionModel;
import com.brettstine.social_game_backend.service.AnswerService;
import com.brettstine.social_game_backend.service.FetchService;
import com.brettstine.social_game_backend.service.GameFlowService;
import com.brettstine.social_game_backend.service.SanitizationService;
import com.brettstine.social_game_backend.service.ValidationService;
import com.brettstine.social_game_backend.utils.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
@RequestMapping("/answer")
public class AnswerController {

    private static final Logger logger = LoggerFactory.getLogger(AnswerController.class);

    private final AnswerService answerService;
    private final GameFlowService gameFlowService;
    private final FetchService fetchService;
    private final ValidationService validationService;
    private final SanitizationService sanitizationService;

    public AnswerController(AnswerService answerService, GameFlowService gameFlowService, FetchService fetchService,
            ValidationService validationService, SanitizationService sanitizationService) {
        this.answerService = answerService;
        this.gameFlowService = gameFlowService;
        this.fetchService = fetchService;
        this.validationService = validationService;
        this.sanitizationService = sanitizationService;
    }

    @GetMapping("/get-answer")
    public ResponseEntity<?> getAnswerFromId(@RequestBody Map<String, String> payload) {
        String answerId = payload.get("answerId");
        try {
            AnswerModel answerModel = answerService.getAnswerById(answerId);
            logger.info("Successfully executed getAnswer with id: {}", answerId);
            return ResponseEntity.ok(answerModel);
        } catch (IllegalArgumentException e) {
            logger.error("Error getting answer with id: {}", answerId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Could not get answer", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting answer with id: {}", answerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not get answer", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/delete-answer")
    public ResponseEntity<?> deleteAnswer(@RequestBody Map<String, String> payload) {
        String answerId = payload.get("answerId");
        try {
            answerService.deleteAnswer(answerId);
            logger.info("Successfully deleted answer with id: {}", answerId);
            return ResponseEntity.ok(Map.of("message", "Successfully deleted answer", "answerId", answerId));
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting answer with id: {}", answerId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "could not delete answer", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting answer with id: {}", answerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "could not delete answer", "message", e.getMessage()));
        }
    }

    @GetMapping("/get-answers-for-question")
    public ResponseEntity<?> getAnswersForQuestion(@RequestBody Map<String, String> payload) {
        String questionId = payload.get("questionId");
        try {
            QuestionModel question = fetchService.getQuestionById(questionId);
            List<AnswerModel> answers = answerService.getAnswersForQuestion(question);
            logger.info("Successfully retrieved answers for question with id: {}", questionId);
            return ResponseEntity.ok(answers);
        } catch (IllegalArgumentException e) {
            logger.error("Error retrieving answers for question with id: {}", questionId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "error fetching answers for question", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving answers for question with id: {}", questionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "error fetching answers for question", "message", e.getMessage()));
        }
    }

    @PostMapping("/submit-answer")
    public ResponseEntity<?> submitAnswer(HttpServletRequest request, @RequestBody Map<String, String> payload) {
        String gameId = payload.get("gameId");
        String questionId = payload.get("questionId");
        String answerContent = payload.get("answer");
        String playerId = CookieUtil.getDataFromCookie(request, "playerId");
        try {
            GameModel game = fetchService.getGameById(gameId);
            PlayerModel player = fetchService.getPlayerById(playerId);
            QuestionModel question = fetchService.getQuestionById(questionId);
            validationService.ensureGameState(game, GameState.ANSWER);
            validationService.validatePlayer(player, game);
            validationService.validateConversationInputLength(answerContent);
            validationService.validatePlayerCanAnswerThisQuestion(player, question);
            String sanitizedAnswerContent = sanitizationService.sanitizeForHtml(answerContent);
            AnswerModel answer = answerService.submitAnswer(game, player, question, sanitizedAnswerContent);
            logger.info("Game: {} : Successfully submitted answer with id: {}", gameId, answer.getAnswerId());
            if (!validationService.doesPlayerHaveUnansweredQuestions(player)) {
                gameFlowService.setPlayerReady(player, true);
            }
            gameFlowService.tryAdvanceGameState(game);
            return ResponseEntity.ok(answer);
        } catch (IllegalArgumentException e) {
            logger.error("Game: {} : Error submitting answer", gameId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "error submitting answer", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Game: {} : Error submitting answer", gameId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "error submitting answer", "message", e.getMessage()));
        }
    }

    @GetMapping("/get-all-answers")
    public ResponseEntity<?> getAllAnswers() {
        try {
            List<AnswerModel> answers = answerService.getAllAnswers();
            logger.info("Successfully retrieved all answers");
            return ResponseEntity.ok(answers);
        } catch (Exception e) {
            logger.error("Error retrieving all answers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "error fetching answers", "message", e.getMessage()));
        }
    }

}
