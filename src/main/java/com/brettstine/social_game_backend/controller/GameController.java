package com.brettstine.social_game_backend.controller;

import java.util.Map;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.service.GameFlowService;
import com.brettstine.social_game_backend.service.GameService;

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

    @PostMapping("/create-game")
    public ResponseEntity<?> createGame() {
        try {
            GameModel game = gameService.createGame();
            logger.info("Game: {} : Successfully created game", game.getGameId());
            return ResponseEntity.ok(game);
        } catch (Exception e) {
            logger.error("Error creating game: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "could not create game", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/delete-game")
    public ResponseEntity<?> deleteGame(@RequestBody Map<String, String> payload) {
        String gameId = payload.get("gameId");
        try {
            gameService.deleteGame(gameId);
            logger.info("Game: {} : Successfully deleted game", gameId);
            return ResponseEntity.ok(Map.of("message", "Successfully deleted game", "gameId", gameId));
        } catch (IllegalArgumentException e) {
            logger.error("Game: {} : Error deleting game", gameId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "could not delete game", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Game: {} : Error deleting game", gameId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "could not delete game", "message", e.getMessage()));
        }
    }

    @GetMapping("/get-state")
    public ResponseEntity<?> getState(@RequestBody Map<String, String> payload) {
        String gameId = payload.get("gameId");
        try {
            GameModel game = gameService.getGameById(gameId);
            GameState gameState = gameService.getGameState(game);
            logger.info("Game: {} : Successfully executed getState", gameId);
            return ResponseEntity.ok(Map.of("gameState", gameState));
        } catch (IllegalArgumentException e) {
            logger.error("Game: {} : Error executing getState", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Could not get gameState", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Game: {} : Error executing getState", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not get gameState", "message", e.getMessage()));
        }
    }

    @PostMapping("/advance-state")
    public ResponseEntity<?> advanceState(@RequestBody Map<String, String> payload) {
        String gameId = payload.get("gameId");
        try {
            GameModel game = gameService.getGameById(gameId);
            gameFlowService.tryAdvanceGameState(game);
            // log successful game state advancement inside of gameFlowService
            return ResponseEntity.ok(game);
        } catch (IllegalArgumentException e) {
            logger.error("Game: {} : Error advancing state", gameId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Could not advance gameState", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Game: {} : Error advancing state", gameId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not advance gameState", "message", e.getMessage()));
        }
    }

    @PostMapping("/set-state")
    public ResponseEntity<?> setState(@RequestBody Map<String, String> payload) {
        String gameId = payload.get("gameId");
        String gameState = payload.get("gameState");
        try {
            GameModel game = gameService.getGameById(gameId);
            gameFlowService.checkMinimumPlayersForQuestionState(game);
            GameModel updatedGame = gameService.setGameState(game, GameState.fromString(gameState));
            logger.info("Game: {} : Successfully updated gameState to {}", gameId, gameState);
            return ResponseEntity.ok(updatedGame);
        } catch (IllegalArgumentException e) {
            logger.error("Game: {} : Error while updating gameState", gameId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Could not update gameState", "message", e.getMessage()));
        } catch (IllegalStateException e) {
            logger.error("Game: {} : Error while updating gameState", gameId, e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Could not update gameState", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Game: {} : Error while updating gameState", gameId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not update gameState", "message", e.getMessage()));
        }
    }

    @GetMapping("/get-all-games")
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
}
