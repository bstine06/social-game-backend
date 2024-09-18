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
import com.brettstine.social_game_backend.model.GameState;
import com.brettstine.social_game_backend.model.QuestionModel;
import com.brettstine.social_game_backend.service.ConversationService;
import com.brettstine.social_game_backend.service.GameFlowService;
import com.brettstine.social_game_backend.utils.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
@RequestMapping("/conversation")
public class ConversationController {

    private static final Logger logger = LoggerFactory.getLogger(ConversationController.class);

    private final ConversationService conversationService;
    private final GameFlowService gameFlowService;

    public ConversationController(ConversationService conversationService, GameFlowService gameFlowService) {
        this.conversationService = conversationService;
        this.gameFlowService = gameFlowService;
    }

    @GetMapping("/get-question")
    public ResponseEntity<?> getQuestionFromId(@RequestBody Map<String, String> payload) {
        String questionId = payload.get("questionId");
        try {
            QuestionModel questionModel = conversationService.getQuestionById(questionId);
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

    @GetMapping("/get-answer")
    public ResponseEntity<?> getAnswerFromId(@RequestBody Map<String, String> payload) {
        String answerId = payload.get("answerId");
        try {
            AnswerModel answerModel = conversationService.getAnswerById(answerId);
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

    @DeleteMapping("/delete-question")
    public ResponseEntity<?> deleteQuestion(@RequestBody Map<String, String> payload) {
        String questionId = payload.get("questionId");
        try {
            conversationService.deleteQuestion(questionId);
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

    @DeleteMapping("/delete-answer")
    public ResponseEntity<?> deleteAnswer(@RequestBody Map<String, String> payload) {
        String answerId = payload.get("answerId");
        try {
            conversationService.deleteAnswer(answerId);
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
            List<AnswerModel> answers = conversationService.getAnswersForQuestion(questionId);
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

    @PostMapping("/submit-question")
    public ResponseEntity<?> submitQuestion(HttpServletRequest request, @RequestBody Map<String, String> payload) {
        String gameId = payload.get("gameId");
        String questionContent = payload.get("question");
        String playerId = CookieUtil.getDataFromCookie(request, "playerId");
        try {
            gameFlowService.ensureGameState(gameId, GameState.QUESTION);
            gameFlowService.validateGame(gameId);
            gameFlowService.validatePlayer(playerId, gameId);
            gameFlowService.validatePlayerCanSubmitQuestion(playerId);
            QuestionModel question = conversationService.submitQuestion(gameId, playerId, questionContent);
            logger.info("Game: {} : Successfully submitted question with id: {}", gameId, question.getQuestionId());
            gameFlowService.tryAdvanceGameState(gameId);
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
    public ResponseEntity<?> getQuestionsForPlayer(HttpServletRequest request) {
        String playerId = CookieUtil.getDataFromCookie(request, "playerId");
        try {
            gameFlowService.validatePlayerExists(playerId);
            List<QuestionModel> questions = conversationService.getQuestionsForPlayer(playerId);
            logger.info("Successfully retrieved questions for player with id: {}", playerId);
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

    @PostMapping("/submit-answer")
    public ResponseEntity<?> submitAnswer(HttpServletRequest request, @RequestBody Map<String, String> payload) {
        String gameId = payload.get("gameId");
        String questionId = payload.get("questionId");
        String answerContent = payload.get("answer");
        String playerId = CookieUtil.getDataFromCookie(request, "playerId");
        try {
            gameFlowService.ensureGameState(gameId, GameState.ANSWER);
            gameFlowService.validateGame(gameId);
            gameFlowService.validatePlayer(playerId, gameId);
            gameFlowService.validateQuestion(questionId);
            gameFlowService.validatePlayerCanAnswerThisQuestion(playerId, questionId);
            AnswerModel answer = conversationService.submitAnswer(gameId, playerId, questionId, answerContent);
            logger.info("Game: {} : Successfully submitted answer with id: {}", gameId, answer.getAnswerId());
            gameFlowService.tryAdvanceGameState(gameId);
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

    @GetMapping("/get-all-questions")
    public ResponseEntity<?> getAllQuestionsInGame() {
        try {
            List<QuestionModel> questions = conversationService.getAllQuestions();
            logger.info("Successfully retrieved all questions");
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            logger.error("Error retrieving all questions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "error fetching questions", "message", e.getMessage()));
        }
    }

    @GetMapping("/get-all-answers")
    public ResponseEntity<?> getAllAnswers() {
        try {
            List<AnswerModel> answers = conversationService.getAllAnswers();
            logger.info("Successfully retrieved all answers");
            return ResponseEntity.ok(answers);
        } catch (Exception e) {
            logger.error("Error retrieving all answers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "error fetching answers", "message", e.getMessage()));
        }
    }

}
