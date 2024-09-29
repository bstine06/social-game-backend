package com.brettstine.social_game_backend.controller;

import com.brettstine.social_game_backend.model.GameState;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.SessionModel;
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
import org.springframework.web.bind.annotation.PathVariable;
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

    @PostMapping
    public ResponseEntity<?> createPlayerAndAddToGame(HttpServletRequest request, @RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String gameId = payload.get("gameId");
        String sessionId = CookieUtil.getDataFromCookie(request, "sessionId");

        try {
            GameModel game = fetchService.getGameById(gameId);
            SessionModel session = fetchService.getSessionById(sessionId);
            gameFlowService.checkMaximumPlayersForGame(game);
            validationService.ensureGameState(game, GameState.LOBBY);

            PlayerModel player = playerService.createPlayer(game, session, name);
            String playerId = player.getPlayerId();
            logger.info("Game: {} : Player created with ID: {}, name: {}", gameId, playerId, name);

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

    @GetMapping("/{playerId}")
    public ResponseEntity<?> getPlayer(@PathVariable String playerId) {
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

    @GetMapping("/{gameId}")
    public ResponseEntity<?> getAllPlayersInGame(@PathVariable String gameId) {
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

    @GetMapping("/all")
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
