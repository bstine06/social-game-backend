package com.brettstine.social_game_backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brettstine.social_game_backend.model.AnswerModel;
import com.brettstine.social_game_backend.model.QuestionModel;
import com.brettstine.social_game_backend.service.ConversationService;
import com.brettstine.social_game_backend.utils.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
@RequestMapping("/conversation")
public class ConversationController {

  private final ConversationService conversationService;

  public ConversationController(ConversationService conversationService) {
    this.conversationService = conversationService;
  }

  @GetMapping("/get-question")
  public ResponseEntity<?> getQuestionFromId(@RequestBody Map<String, String> payload) {
    String questionId = payload.get("questionId");
    if (questionId == null) {
      return ResponseEntity.status(400).body(Map.of("error", "no questionId provided"));
    }
    QuestionModel questionModel = conversationService.getQuestionById(questionId);
    return ResponseEntity.ok(questionModel);
  }

  @GetMapping("/get-answer")
  public ResponseEntity<?> getAnswerFromId(@RequestBody Map<String, String> payload) {
    String answerId = payload.get("answerId");
    if (answerId == null) {
      return ResponseEntity.status(400).body(Map.of("error", "no answerId provided"));
    }
    AnswerModel answerModel = conversationService.getAnswerById(answerId);
    return ResponseEntity.ok(answerModel);
  }

  @GetMapping("/get-answers-for-question")
  public ResponseEntity<?> getAnswersForQuestion(@RequestBody Map<String, String> payload) {
    String questionId = payload.get("questionId");
    if (questionId == null) {
      return ResponseEntity.status(400).body(Map.of("error", "no questionId provided"));
    }

    try {
      List<AnswerModel> answers = conversationService.getAnswersForQuestion(questionId);
      return ResponseEntity.ok(answers);
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", "error fetching answers for question", "message", e.getMessage()));
    }
  }

  @PostMapping("/submit-question")
  public ResponseEntity<?> submitQuestion(HttpServletRequest request, @RequestBody Map<String, String> payload) {

    String gameId = payload.get("gameId");
    if (gameId == null) {
      return ResponseEntity.status(400).body(Map.of("error", "no gameId provided"));
    }
    String questionContent = payload.get("question");
    if (questionContent == null) {
      return ResponseEntity.status(400).body(Map.of("error", "no question provided"));
    }
    String playerId = CookieUtil.getDataFromCookie(request, "playerId");
    if (playerId == null) {
      return ResponseEntity.status(400).body(Map.of("error", "No playerId found"));
    }

    try {
      QuestionModel question = conversationService.submitQuestion(gameId, playerId, questionContent);
      return ResponseEntity.ok(question);
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", "error submitting question", "message", e.getMessage()));
    }
  }

  @GetMapping("/get-questions-for-player")
  public ResponseEntity<?> getQuestionsForPlayer(HttpServletRequest request) {

    String playerId = CookieUtil.getDataFromCookie(request, "playerId");
    if (playerId == null) {
      return ResponseEntity.status(400).body(Map.of("error", "No playerId found"));
    }

    try {
      List<QuestionModel> questions = conversationService.getQuestionsForPlayer(playerId);
      return ResponseEntity.ok(questions);
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", "error fetching questions", "message", e.getMessage()));
    }
  }

  @PostMapping("/submit-answer")
  public ResponseEntity<?> submitAnswer(HttpServletRequest request, @RequestBody Map<String, String> payload) {

    // gameId needs to be checked to ensure that the game state is correct for submitting answers
    String gameId = payload.get("gameId");
    if (gameId == null) {
      return ResponseEntity.status(400).body(Map.of("error", "no gameId provided"));
    }
    String questionId = payload.get("questionId");
    if (questionId == null) {
      return ResponseEntity.status(400).body(Map.of("error", "no questionId provided"));
    }
    String answerContent = payload.get("answer");
    if (answerContent == null) {
      return ResponseEntity.status(400).body(Map.of("error", "no answer provided"));
    }
    String playerId = CookieUtil.getDataFromCookie(request, "playerId");
    if (playerId == null) {
      return ResponseEntity.status(400).body(Map.of("error", "No playerId found"));
    }

    try {
      AnswerModel answer = conversationService.submitAnswer(gameId, playerId, questionId, answerContent);
      return ResponseEntity.ok(answer);
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", "error submitting answer", "message", e.getMessage()));
    }
  }

  @GetMapping("/get-all-questions")
  public ResponseEntity<?> getAllQuestions() {
    try {
      List<QuestionModel> questions = conversationService.getAllQuestions();
      return ResponseEntity.ok(questions);
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", "error fetching questions", "message", e.getMessage()));
    }
  }
  

}
