package com.brettstine.social_game_backend.controller;

import com.brettstine.social_game_backend.model.GameState;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.service.FetchService;
import com.brettstine.social_game_backend.service.GameFlowService;
import com.brettstine.social_game_backend.service.PlayerService;
import com.brettstine.social_game_backend.service.SanitizationService;
import com.brettstine.social_game_backend.service.ValidationService;
import com.brettstine.social_game_backend.utils.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private final SanitizationService sanitizationService;

    public PlayerController(PlayerService playerService, GameFlowService gameFlowService, FetchService fetchService,
            ValidationService validationService, SanitizationService sanitizationService) {
        this.playerService = playerService;
        this.gameFlowService = gameFlowService;
        this.fetchService = fetchService;
        this.validationService = validationService;
        this.sanitizationService = sanitizationService;
    }

    @PostMapping
    public ResponseEntity<?> createPlayerAndAddToGame(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, String> payload) {
        String name = payload.get("name");
        String gameId = payload.get("gameId");
        String shape = payload.get("shape");
        String color = payload.get("color");

        String optionalHostId = CookieUtil.getDataFromCookie(request, "hostId");

        try {
            String sanitizedName = sanitizationService.sanitizeForHtml(name);
            String sanitizedShape = sanitizationService.sanitizeForHtml(shape);
            String sanitizedColor = sanitizationService.sanitizeForHtml(color);
            GameModel game = fetchService.getGameById(gameId);
            gameFlowService.checkMaximumPlayersForGame(game);
            validationService.ensureGameState(game, GameState.LOBBY);
            validationService.validateNameLength(name);
            validationService.ensureGameCanAcceptPlayer(game, optionalHostId);

            if (shape == null || !sanitizedShape.equals(shape)) shape = "1";
            if (color == null || !sanitizedColor.equals(color)) color = "1";

            // if this request is coming from a host, create a player with id identical to host id
            PlayerModel player;
            if (optionalHostId != null) {
                player = playerService.createPlayer(game, sanitizedName, Integer.parseInt(sanitizedShape), sanitizedColor, optionalHostId);
            } else {
                player = playerService.createPlayer(game, sanitizedName, Integer.parseInt(sanitizedShape), sanitizedColor);
            }
            String playerId = player.getPlayerId();
            logger.info("Game: {} : Player created with ID: {}, name: {}", gameId, playerId, sanitizedName);

            if (CookieUtil.getDataFromCookie(request, "hostPlayerCreation") != null) {
                CookieUtil.deleteCookie(response, "hostPlayerCreation");
                logger.info("Deleted hostPlayerCreation cookie");
            }
            CookieUtil.setHttpCookie(response, "playerId", playerId, 7200);
            logger.info("Player cookie set with ID: {}", playerId);

            // websocket broadcast update
            gameFlowService.broadcastPlayersList(game);

            return ResponseEntity.ok(player);
        } catch (IllegalArgumentException e) {
            logger.warn("Error creating player: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "could not create player", "message", e.getMessage()));
        } catch (IllegalStateException e) {
            logger.warn("Error creating player: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "could not create player", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error creating player", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "could not create player", "message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getPlayer(HttpServletRequest request) {
        String playerId = CookieUtil.getDataFromCookie(request, "playerId");
        try {
            PlayerModel player = playerService.getPlayerById(playerId);
            logger.info("Successfully retrieved player with id: {}", playerId);
            return ResponseEntity.ok(player);
        } catch (IllegalArgumentException e) {
            logger.error("Error retrieving player with id: {}", playerId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "could not get player", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving player with id: {}", playerId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "could not get player", "message", e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<Map<String, String>> deletePlayer(HttpServletRequest request, HttpServletResponse response) {
        String playerId = CookieUtil.getDataFromCookie(request, "playerId");
        try {
            PlayerModel player = playerService.getPlayerById(playerId);
            playerService.deletePlayerById(playerId);
            logger.info("Game: {} : Successfully removed player with id: {}", player.getGameId(), playerId);
            CookieUtil.deleteCookie(response, "playerId");
            logger.info("Deleted player cookie with id: {}", playerId);

            // websocket broadcast update
            gameFlowService.broadcastPlayersList(player.getGame());

            gameFlowService.terminateGameIfPlayerDeletionIsGameBreaking(player.getGame());
            return ResponseEntity.ok(Map.of("success", "deleted player"));
        } catch (IllegalArgumentException e) {
            logger.error("error deleting player", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "could not delete player", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("error deleting player", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "could not delete player", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/id/{playerId}")
    public ResponseEntity<Map<String, String>> deletePlayerById(@NonNull @PathVariable String playerId) {
        try {
            PlayerModel player = playerService.getPlayerById(playerId);
            playerService.deletePlayerById(playerId);
            logger.info("Game: {} : Successfully removed player with id: {}", player.getGameId(), playerId);

            // websocket broadcast update
            gameFlowService.broadcastPlayersList(player.getGame());
            gameFlowService.terminateGameIfPlayerDeletionIsGameBreaking(player.getGame());
            return ResponseEntity.ok(Map.of("success", "deleted player"));
        } catch (IllegalArgumentException e) {
            logger.error("error deleting player", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "could not delete player", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("error deleting player", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "could not delete player", "message", e.getMessage()));
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
