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
import com.brettstine.social_game_backend.service.FetchService;
import com.brettstine.social_game_backend.service.GameFlowService;
import com.brettstine.social_game_backend.service.QuestionService;
import com.brettstine.social_game_backend.service.ValidationService;
import com.brettstine.social_game_backend.utils.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
@RequestMapping("/question")
public class QuestionController {

    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    private final QuestionService questionService;
    private final GameFlowService gameFlowService;
    private final FetchService fetchService;
    private final ValidationService validationService;

    public QuestionController(QuestionService questionService, GameFlowService gameFlowService, FetchService fetchService, ValidationService validationService) {
        this.questionService = questionService;
        this.gameFlowService = gameFlowService;
        this.fetchService = fetchService;
        this.validationService = validationService;
    }

    @GetMapping("/get-question")
    public ResponseEntity<?> getQuestionFromId(@RequestBody Map<String, String> payload) {
        String questionId = payload.get("questionId");
        try {
            QuestionModel questionModel = questionService.getQuestionById(questionId);
            logger.info("Successfully executed getQuestion with id: {}", questionId);
            return ResponseEntity.ok(questionModel);
        } catch (IllegalArgumentException e) {
            logger.error("Error getting question with id: {}", questionId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Could not get question", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error getting question with id: {}", questionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not get question", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/delete-question")
    public ResponseEntity<?> deleteQuestion(@RequestBody Map<String, String> payload) {
        String questionId = payload.get("questionId");
        try {
            questionService.deleteQuestion(questionId);
            logger.info("Successfully deleted question with id: {}", questionId);
            return ResponseEntity.ok(Map.of("message", "Successfully deleted question", "questionId", questionId));
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting question with id: {}", questionId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "could not delete question", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error deleting question with id: {}", questionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "could not delete question", "message", e.getMessage()));
        }
    }

    @PostMapping("/submit-question")
    public ResponseEntity<?> submitQuestion(HttpServletRequest request, @RequestBody Map<String, String> payload) {
        String gameId = payload.get("gameId");
        String questionContent = payload.get("question");
        String playerId = CookieUtil.getDataFromCookie(request, "playerId");
        try {
            GameModel game = fetchService.getGameById(gameId);
            PlayerModel player = fetchService.getPlayerById(playerId);
            validationService.ensureGameState(game, GameState.QUESTION);
            validationService.validatePlayerCanSubmitQuestion(player);
            QuestionModel question = questionService.submitQuestion(game, player, questionContent);
            logger.info("Game: {} : Successfully submitted question with id: {}", gameId, question.getQuestionId());
            gameFlowService.tryAdvanceGameState(game);
            return ResponseEntity.ok(question);
        } catch (IllegalArgumentException e) {
            logger.error("Game: {} : Error submitting question", gameId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "error submitting question", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Game: {} : Error submitting question", gameId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "error submitting question", "message", e.getMessage()));
        }
    }

    @GetMapping("/get-questions-for-player")
    public ResponseEntity<?> getQuestionsForSession(HttpServletRequest request) {
        String playerId = CookieUtil.getDataFromCookie(request, "playerId");
        try {
            PlayerModel player = fetchService.getPlayerById(playerId);
            List<QuestionModel> questions = questionService.getQuestionsForPlayer(player);
            logger.info("Successfully retrieved questions for player with id: {}", player.getPlayerId());
            return ResponseEntity.ok(questions);
        } catch (IllegalArgumentException e) {
            logger.error("Error retrieving questions for player with id: {}", playerId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "error submitting question", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving questions for player with id: {}", playerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "error submitting question", "message", e.getMessage()));
        }
    }

    @GetMapping("/get-all-questions")
    public ResponseEntity<?> getAllQuestionsInGame() {
        try {
            List<QuestionModel> questions = questionService.getAllQuestions();
            logger.info("Successfully retrieved all questions");
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            logger.error("Error retrieving all questions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "error fetching questions", "message", e.getMessage()));
        }
    }

}
