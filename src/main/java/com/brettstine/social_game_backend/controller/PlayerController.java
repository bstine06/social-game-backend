package com.brettstine.social_game_backend.controller;

import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.service.PlayerService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
public class PlayerController {

  @Autowired
  private PlayerService playerService;

  private static final Logger logger = LoggerFactory.getLogger(PlayerController.class);

  @PostMapping("/add-player")
  public ResponseEntity<Map<String, String>> addPlayer(HttpServletRequest request,
      @RequestBody Map<String, String> payload) {
    String playerName = payload.get("playerName");
    // Retrieve session ID from cookie
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

    try {
      PlayerModel player = playerService.addPlayer(playerName, sessionId);
      logger.info("Player created: " + player.getPlayerName());
      return ResponseEntity.ok(Map.of("message", "Player created: " + player.getPlayerName()));
    } catch (IllegalStateException e) {
      return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
    }
  }
}
