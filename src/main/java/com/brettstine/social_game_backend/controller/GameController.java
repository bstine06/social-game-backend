package com.brettstine.social_game_backend.controller;

import org.springframework.http.ResponseEntity;
// GameController.java
import org.springframework.web.bind.annotation.*;

import com.brettstine.social_game_backend.model.ConversationModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.SessionModel;
import com.brettstine.social_game_backend.service.GameService;

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
      List<PlayerModel> players;
      try {
        players = gameService.initialize();
        return ResponseEntity.ok(players);
      } catch (Exception e) {
        return ResponseEntity.status(422).body(Map.of("error", e.getMessage()));
      }
    }

    @PostMapping("/submit-question")
    public ResponseEntity<?> submitQuestions(HttpServletRequest request, @RequestBody Map<String, String> payload) {
      String question = payload.get("question");
      Cookie[] cookies = request.getCookies();
      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if ("sessionId".equals(cookie.getName())) {
            String sessionId = cookie.getValue();
            gameService.submitQuestion(sessionId, question);
            return ResponseEntity.ok(Map.of("success", "Question submitted"));
          }
        }
      }
      return ResponseEntity.status(400).body(Map.of("error", "No session found"));
    }

    @GetMapping("/players")
    public ResponseEntity<?> getPlayers() {
      List<PlayerModel> players = gameService.getPlayers();
      return ResponseEntity.ok(players);
    }

    @GetMapping("/assign-questions")
    public ResponseEntity<?> assignQuestions() {
      try {
        gameService.assignQuestions();
        List<PlayerModel> players = gameService.getPlayers();
        return ResponseEntity.ok(players);
      } catch (Exception e) {
        return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
      }
    }

    @GetMapping("/questions")
    public ResponseEntity<?> getQuestions(HttpServletRequest request) {
      String sessionId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("sessionId".equals(cookie.getName())) {
                    sessionId = cookie.getValue();
                    break;
                }
            }
        }

        if (sessionId == null) {
          return ResponseEntity.status(400).body(Map.of("error", "No session found"));
      }

      List<ConversationModel> responseConversationModels = gameService.getQuestionsForPlayer(sessionId);
        return ResponseEntity.ok(responseConversationModels);
    }

    @PostMapping("/submitAnswer")
    public void submitAnswer(@RequestParam String playerId, @RequestParam String question, @RequestParam String answer) {
        gameService.submitAnswer(playerId, question, answer);
    }
}
