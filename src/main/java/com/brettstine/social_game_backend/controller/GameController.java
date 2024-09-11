package com.brettstine.social_game_backend.controller;

import org.springframework.http.ResponseEntity;
// GameController.java
import org.springframework.web.bind.annotation.*;

import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.SessionModel;
import com.brettstine.social_game_backend.service.GameService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    // Creates all player models based on registered sessions
    @PostMapping("/initialize")
    public ResponseEntity<?> initialize() {
      List<PlayerModel> players = gameService.initialize();
      return ResponseEntity.ok(players);
    }

    @PostMapping("/submit-questions")
    public ResponseEntity<?> submitQuestions(HttpServletRequest request, @RequestBody List<String> questions) {
      Cookie[] cookies = request.getCookies();
      if (cookies != null) {
        for (Cookie cookie : cookies) {
          if ("sessionId".equals(cookie.getName())) {
            String sessionId = cookie.getValue();
            gameService.submitQuestions(sessionId, questions);
            return ResponseEntity.ok("Questions submitted");
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
    public List<PlayerModel> assignQuestions() {
        gameService.assignQuestions();
        return gameService.getPlayers(); // Return the players with updated question assignments
    }

    @GetMapping("/questions/{playerId}")
    public List<String> getQuestions(@PathVariable String playerId) {
        return gameService.getQuestionsForPlayer(playerId);
    }

    @PostMapping("/submitAnswer")
    public void submitAnswer(@RequestParam String playerId, @RequestParam String question, @RequestParam String answer) {
        gameService.submitAnswer(playerId, question, answer);
    }
}
