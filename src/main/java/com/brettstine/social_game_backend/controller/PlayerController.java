package com.brettstine.social_game_backend.controller;

import com.brettstine.social_game_backend.model.GameState;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.service.FetchService;
import com.brettstine.social_game_backend.service.GameFlowService;
import com.brettstine.social_game_backend.service.PlayerService;
import com.brettstine.social_game_backend.service.ValidationService;
import com.brettstine.social_game_backend.utils.CookieUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
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
    private final GameFlowService gameFlowService;
    private final FetchService fetchService;
    private final ValidationService validationService;

    public PlayerController(PlayerService playerService, GameFlowService gameFlowService, FetchService fetchService, ValidationService validationService) {
        this.playerService = playerService;
        this.gameFlowService = gameFlowService;
        this.fetchService = fetchService;
        this.validationService = validationService;
    }

    @PostMapping("/create-player-and-add-to-game")
    public ResponseEntity<?> createPlayerAndAddToGame(HttpServletResponse response, @RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String gameId = payload.get("gameId");

        try {
            GameModel game = fetchService.getGameById(gameId);
            validationService.ensureGameState(game, GameState.LOBBY);

            PlayerModel player = playerService.createPlayer(game, name);
            String playerId = player.getPlayerId();
            logger.info("Game: {} : Player created with ID: {}, name: {}", gameId, playerId, name);

            Cookie cookie = new Cookie("playerId", playerId);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60); // 1 hour
            cookie.setSecure(false); // Use this line for HTTP requests only
            response.addCookie(cookie);
            logger.info("Player cookie set with ID: {}", playerId);

            return ResponseEntity.ok(player);
        } catch (IllegalArgumentException e) {
            logger.error("Error creating player", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "could not create player", "message", e.getMessage()));
        } catch (IllegalStateException e) {
            logger.error("Error creating player", e);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "could not create player", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating player", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "could not create player", "message", e.getMessage()));
        }
    }

    @GetMapping("/get-player")
    public ResponseEntity<?> getPlayer(HttpServletRequest request) {
        String playerId = CookieUtil.getDataFromCookie(request, "playerId");
        try {
            PlayerModel player = playerService.getPlayerById(playerId);
            logger.info("Successfully retrieved player with id: {}", playerId);
            return ResponseEntity.ok(player);
        } catch (IllegalArgumentException e) {
            logger.error("Error retrieving player with id: {}", playerId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "could not set player", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving player with id: {}", playerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "could not set player", "message", e.getMessage()));
        }
    }

    @PostMapping("/set-name")
    public ResponseEntity<?> setName(HttpServletRequest request, @RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String playerId = CookieUtil.getDataFromCookie(request, "playerId");
        try {
            PlayerModel player = playerService.setName(playerId, name);
            logger.info("Successfully set name for player with id: {}, name: {}", playerId, name);
            return ResponseEntity.ok(player);
        } catch (IllegalArgumentException e) {
            logger.error("Error setting name of player with id: {}", playerId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "could not set player", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error setting name of player with id: {}", playerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "could not set player", "message", e.getMessage()));
        }
    }

    @GetMapping("/get-all-players-in-game")
    public ResponseEntity<?> getAllPlayersInGame(@RequestBody Map<String, String> payload) {
        String gameId = payload.get("gameId");
        try {
            GameModel game = fetchService.getGameById(gameId);
            List<PlayerModel> allPlayers = game.getPlayers();
            logger.info("Game: {} : Successfully retrieved all players", gameId);
            return ResponseEntity.ok(allPlayers);
        } catch (IllegalArgumentException e) {
            logger.error("Game: {} : Error retrieving all players", gameId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "could not get players", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Game: {} : Error retrieving all players", gameId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "could not get players", "message", e.getMessage()));
        }
    }

    @GetMapping("/get-all-players")
    public ResponseEntity<?> getAllPlayers() {
        try {
            List<PlayerModel> allPlayers = playerService.getAllPlayers();
            logger.info("Successfully retrieved all players");
            return ResponseEntity.ok(allPlayers);
        } catch (Exception e) {
            logger.error("Error retrieving all players", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "could not get players", "message", e.getMessage()));
        }
    }
}
