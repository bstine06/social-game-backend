package com.brettstine.social_game_backend.controller;

import org.springframework.http.ResponseEntity;
// GameController.java
import org.springframework.web.bind.annotation.*;

import com.brettstine.social_game_backend.model.ConversationModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.SessionModel;
import com.brettstine.social_game_backend.service.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
@RequestMapping("/game")
public class GameController {

  private final GameService gameService;

  public GameController(GameService gameService) {
    this.gameService = gameService;
  }

  // Creates all player models based on registered sessions
  @PostMapping("/initialize")
  public ResponseEntity<?> initialize() {
    try {
      gameService.initialize();
      return ResponseEntity.ok(Map.of("success", "Game Initialized"));
    } catch (Exception e) {
      return ResponseEntity.status(422).body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/submit-question")
  public ResponseEntity<?> submitQuestions(HttpServletRequest request, @RequestBody Map<String, String> payload) {
    if (!gameService.confirmGameState("asking")) {
      return ResponseEntity.status(409).body(Map.of("error", "Incorrect state for question submit"));
    }
    String question = payload.get("question");
    String sessionId = getSessionIdFromCookie(request);
    if (sessionId == null) {
      return ResponseEntity.status(400).body(Map.of("error", "No session found"));
    }
    gameService.submitQuestion(sessionId, question);
    return ResponseEntity.ok(Map.of("success", "Question submitted"));
  }

  @GetMapping("/players")
  public ResponseEntity<?> getPlayers() {
    List<PlayerModel> players = gameService.getPlayers();
    return ResponseEntity.ok(players);
  }

  @GetMapping("/questions")
  public ResponseEntity<?> getQuestions(HttpServletRequest request) {
    String sessionId = getSessionIdFromCookie(request);
    if (sessionId == null) {
      return ResponseEntity.status(400).body(Map.of("error", "No session found"));
    }

    List<ConversationModel> responseConversationModels = gameService.getQuestionsForPlayer(sessionId);
    return ResponseEntity.ok(responseConversationModels);
  }

  //Expects a JSON of {"questionUUID": String, "answer": String}
  @PostMapping("/submit-answer")
  public ResponseEntity<?> submitAnswer(HttpServletRequest request, @RequestBody Map<String, String> payload) {
    if (!gameService.confirmGameState("answering")) {
      return ResponseEntity.status(409).body(Map.of("error", "Incorrect state for answer submit"));
    }
    String sessionId = getSessionIdFromCookie(request);

    if (sessionId == null) {
      return ResponseEntity.status(400).body(Map.of("error", "No session found"));
    }

    // Convert the question part to ConversationModel
    String questionId = payload.get("questionId");
    String answer = (String) payload.get("answer");
    gameService.submitAnswer(sessionId, questionId, answer);
    return ResponseEntity.ok(Map.of("success", "Answer submitted"));
  }

  private String getSessionIdFromCookie(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (Cookie cookie : cookies) {
        if ("sessionId".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }

}
