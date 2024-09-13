package com.brettstine.social_game_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.brettstine.social_game_backend.model.ConversationModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.service.GameplayService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
@RequestMapping("/gameplay")
public class GameplayController {

    private final GameplayService gameplayService;

    public GameplayController(GameplayService gameplayService) {
        this.gameplayService = gameplayService;
    }

    // Creates all player models based on registered sessions
    @PostMapping("/initialize")
    public ResponseEntity<?> initialize() {
        try {
            gameplayService.initialize();
            return ResponseEntity.ok(Map.of("success", "Game Initialized"));
        } catch (Exception e) {
            return ResponseEntity.status(422).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/submit-question")
    public ResponseEntity<?> submitQuestion(HttpServletRequest request, @RequestBody Map<String, String> payload) {
        if (!gameplayService.confirmGameState("asking")) {
            return ResponseEntity.status(409).body(Map.of("error", "Incorrect state for question submit"));
        }
        String question = payload.get("question");
        String sessionId = getSessionIdFromCookie(request);
        if (sessionId == null) {
            return ResponseEntity.status(400).body(Map.of("error", "No session found"));
        }
        gameplayService.submitQuestion(sessionId, question);
        return ResponseEntity.ok(Map.of("success", "Question submitted"));
    }

    @GetMapping("/players")
    public ResponseEntity<?> getPlayers() {
        List<PlayerModel> players = gameplayService.getPlayers();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/questions")
    public ResponseEntity<?> getQuestions(HttpServletRequest request) {
        String sessionId = getSessionIdFromCookie(request);
        if (sessionId == null) {
            return ResponseEntity.status(400).body(Map.of("error", "No session found"));
        }

        List<ConversationModel> responseConversationModels = gameplayService.getQuestionsForPlayer(sessionId);
        return ResponseEntity.ok(responseConversationModels);
    }

    @PostMapping("/submit-answer")
    public ResponseEntity<?> submitAnswer(HttpServletRequest request, @RequestBody Map<String, String> payload) {
        if (!gameplayService.confirmGameState("answering")) {
            return ResponseEntity.status(409).body(Map.of("error", "Incorrect state for answer submit"));
        }
        String sessionId = getSessionIdFromCookie(request);

        if (sessionId == null) {
            return ResponseEntity.status(400).body(Map.of("error", "No session found"));
        }

        String questionId = payload.get("questionId");
        String answer = payload.get("answer");
        gameplayService.submitAnswer(sessionId, questionId, answer);
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
