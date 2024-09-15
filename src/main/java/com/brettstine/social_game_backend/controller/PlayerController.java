package com.brettstine.social_game_backend.controller;

import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.service.PlayerService;
import com.brettstine.social_game_backend.utils.CookieUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
@RequestMapping("/player")
public class PlayerController {

    private static final Logger logger = LoggerFactory.getLogger(PlayerController.class);

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping("/set-player")
    public ResponseEntity<?> setPlayer(HttpServletResponse response, @RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        if (name == null) {
            return ResponseEntity.status(400).body(Map.of("error", "no name provided"));
        }
        String gameId = payload.get("gameId");
        if (gameId == null) {
            return ResponseEntity.status(400).body(Map.of("error", "no gameId provided"));
        }

        try {
            PlayerModel player = playerService.createPlayer(gameId, name);
            String playerId = player.getPlayerId();
            Cookie cookie = new Cookie("playerId", playerId);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60); // 1 hour
            cookie.setSecure(false); // Use this line for HTTP requests only
            response.addCookie(cookie);
            logger.info("Player cookie set with ID: {}", playerId);

            // Create a JSON response with player information
            return ResponseEntity.ok(player);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "could not set player", "message", e.getMessage()));
        }
        
    }

    @GetMapping("/get-player")
    public ResponseEntity<?> getPlayer(HttpServletRequest request) {
        String playerId = CookieUtil.getDataFromCookie(request, "playerId");
        if (playerId == null) {
            return ResponseEntity.status(400).body(Map.of("error", "No playerId found"));
        }
        
        try {
            PlayerModel player = playerService.getPlayer(playerId);
            return ResponseEntity.ok(player);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "could not set player", "message", e.getMessage()));
        }
    }

    @PostMapping("/set-name")
    public ResponseEntity<?> setName(HttpServletRequest request, @RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        if (name == null) {
            return ResponseEntity.status(400).body(Map.of("error", "no name provided"));
        }
        String playerId = CookieUtil.getDataFromCookie(request, "playerId");
        if (playerId == null) {
            return ResponseEntity.status(400).body(Map.of("error", "No playerId found"));
        }

        try {
            PlayerModel player = playerService.setName(playerId, name);
            return ResponseEntity.ok(player);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "could not set player", "message", e.getMessage()));
        }
    }

    @GetMapping("/get-all-players")
    public ResponseEntity<?> getAllPlayers(@RequestBody Map<String, String> payload) {
        String gameId = payload.get("gameId");
        if (gameId == null) {
            return ResponseEntity.status(400).body(Map.of("error", "no gameId provided"));
        }

        try {
            List<PlayerModel> allPlayers = playerService.getAllPlayersByGameId(gameId);
            return ResponseEntity.ok(allPlayers);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "could not get players", "message", e.getMessage()));
        }
        
    }
}
