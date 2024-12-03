package com.brettstine.social_game_backend.controller;

import java.util.Map;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.brettstine.social_game_backend.dto.GameOptionsDTO;
import com.brettstine.social_game_backend.model.GameDeletionReason;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.service.GameFlowService;
import com.brettstine.social_game_backend.service.GameService;
import com.brettstine.social_game_backend.utils.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.brettstine.social_game_backend.model.GameState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@CrossOrigin(origins = "${frontend.url}")
@RequestMapping("/game")
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    private final GameService gameService;
    private final GameFlowService gameFlowService;

    public GameController(GameService gameService, GameFlowService gameFlowService) {
        this.gameService = gameService;
        this.gameFlowService = gameFlowService;
    }

    @PostMapping
    public ResponseEntity<?> createGame(HttpServletResponse response) {
        try {
            GameModel game = gameService.createGame();
            logger.info("Game: {} : Successfully created game", game.getGameId());

            String hostId = game.getHostId();
            CookieUtil.setHttpCookie(response, "hostId", hostId, 7200);
            logger.info("Host cookie set with ID: {}", hostId);
            
            return ResponseEntity.ok(game);
        } catch (Exception e) {
            logger.error("Error creating game: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "could not create game", "message", e.getMessage()));
        }
    }

    @PostMapping("/custom")
    public ResponseEntity<?> createGame(HttpServletResponse response, @RequestBody GameOptionsDTO gameOptions) {
        try {
            long timerDuration = gameOptions.getTimerDuration();
            GameModel game = gameService.createGame(timerDuration);
            logger.info("Game: {} : Successfully created game", game.getGameId());

            String hostId = game.getHostId();
            CookieUtil.setHttpCookie(response, "hostId", hostId, 7200);
            logger.info("Host cookie set with ID: {}", hostId);
            
            return ResponseEntity.ok(game);
        } catch (Exception e) {
            logger.error("Error creating game: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "could not create game", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<?> deleteGame(@PathVariable String gameId, HttpServletRequest request, HttpServletResponse response) {
        try {
            gameService.deleteGame(gameId);
            logger.info("Game: {} : Successfully deleted game", gameId);
            String hostId = CookieUtil.getDataFromCookie(request, "hostId");
            CookieUtil.deleteCookie(response, "hostId");
            logger.info("Deleted host cookie with id: {}", hostId);

            gameFlowService.closeWebsocketsOnGameDeletion(gameId, GameDeletionReason.DELETED_BY_HOST);

            return ResponseEntity.ok(Map.of("message", "Successfully deleted game", "gameId", gameId));
        } catch (IllegalArgumentException e) {
            logger.error("Game: {} : Error deleting game", gameId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "could not delete game", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Game: {} : Error deleting game", gameId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "could not delete game", "message", e.getMessage()));
        }
    }

    @GetMapping("/{gameId}/state")
    public ResponseEntity<?> getGameState(@PathVariable String gameId) {
        try {
            GameModel game = gameService.getGameById(gameId);
            GameState gameState = gameService.getGameState(game);
            logger.info("Game: {} : Successfully executed getState", gameId);
            return ResponseEntity.ok(Map.of("gameState", gameState));
        } catch (IllegalArgumentException e) {
            logger.error("Game: {} : Error executing getState", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Could not get gameState", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Game: {} : Error executing getState", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not get gameState", "message", e.getMessage()));
        }
    }

    @PatchMapping("/{gameId}/state")
    public ResponseEntity<?> advanceState(@PathVariable String gameId) {
        try {
            GameModel game = gameService.getGameById(gameId);
            gameFlowService.tryAdvanceGameState(game);
            // log successful game state advancement inside of gameFlowService

            return ResponseEntity.ok(game);
        } catch (IllegalArgumentException e) {
            logger.error("Game: {} : Error advancing state", gameId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Could not advance gameState", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Game: {} : Error advancing state", gameId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not advance gameState", "message", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllGames() {
        try {
            List<GameModel> allGames = gameService.getAllGames();
            logger.info("Successfully executed getAllGames");
            return ResponseEntity.ok(allGames);
        } catch (Exception e) {
            logger.error("Error while getting all games", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not get all games", "message", e.getMessage()));
        }
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<?> getGameById(@PathVariable String gameId) {
        try {
            GameModel game = gameService.getGameById(gameId);
            logger.info("Successfully retrieved game by id: {}", gameId);
            return ResponseEntity.ok(game);
        } catch (IllegalArgumentException e) {
            logger.error("Game: {} : Error retrieving game", gameId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Could not retrieve game", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Game: {} : Error retrieving game", gameId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not retrieve game", "message", e.getMessage()));
        }
    }

    @GetMapping("/get-by-host-id")
    public ResponseEntity<?> getGameByHostId(HttpServletRequest request) {
        String hostId = CookieUtil.getDataFromCookie(request, "hostId");
        try {
            GameModel game = gameService.getGameByHostId(hostId);
            logger.info("Successfully retrieved game with host id: {}", hostId);
            return ResponseEntity.ok(game);
        } catch (IllegalArgumentException e) {
            logger.error("Error retrieving game", e.getMessage()); 
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Could not retrieve game", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving game", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not retrieve game", "message", e.getMessage()));
        }
    }
}
