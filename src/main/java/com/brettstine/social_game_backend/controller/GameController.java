package com.brettstine.social_game_backend.controller;

import java.util.Map;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.service.GameFlowService;
import com.brettstine.social_game_backend.service.GameService;

import jakarta.ws.rs.core.Response;

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
      return ResponseEntity.ok(game);
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", "could not create game", "message", e.getMessage()));
    }
  }

  @DeleteMapping("/delete-game")
  public ResponseEntity<?> deleteGame(@RequestBody Map<String, String> payload) {
    String gameId = payload.get("gameId");
    if (gameId == null) {
      return ResponseEntity.status(400).body(Map.of("error", "no gameId provided"));
    }
    try {
      gameService.deleteGame(gameId);
      return ResponseEntity.ok(Map.of("successfully deleted game", gameId));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", "could not delete game", "message", e.getMessage()));
    }
  }

  @GetMapping("/get-state")
    public ResponseEntity<?> getState(@RequestBody Map<String, String> payload) {
        String gameId = payload.get("gameId");
        if (gameId == null) {
          return ResponseEntity.status(400).body(Map.of("error", "No gameId provided"));
        }
        try {
            GameState gameState = gameService.getGameState(gameId);
            return ResponseEntity.ok(Map.of("gameState", gameState));
        } catch (Exception e) {
            logger.error("Error while executing getState", e);
            return ResponseEntity.status(400).body(Map.of("error", "Could not get gameState", "message", e.getMessage()));
        }
    }

    @PostMapping("/advance-state")
    public ResponseEntity<?> advanceState(@RequestBody Map<String,String> payload) {
      String gameId = payload.get("gameId");
      if (gameId == null) {
        return ResponseEntity.status(400).body(Map.of("error", "No gameId provided"));
      }
      try {
          gameFlowService.tryAdvanceGameState(gameId);
          GameModel game = gameService.getGame(gameId);
          return ResponseEntity.ok(game);
      } catch (Exception e) {
          logger.error("Error while executing advanceState", e);
          return ResponseEntity.status(400).body(Map.of("error", "Could not advance gameState", "message", e.getMessage()));
      }
    }
    

    @PostMapping("/set-state")
    public ResponseEntity<?> setState(@RequestBody Map<String, String> payload) {
        String gameId = payload.get("gameId");
        if (gameId == null) {
          return ResponseEntity.status(400).body(Map.of("error", "No gameId provided"));
        }
        String gameState = payload.get("gameState");
        if (gameState == null) {
          return ResponseEntity.status(400).body(Map.of("error", "No gameState provided"));
        }
        logger.info("Game: {} : Received request to update gameState to: {}", gameId, gameState);
        
        try {
            gameFlowService.checkMinimumPlayersForQuestionState(gameId);
            GameModel updatedGame = gameService.setGameState(gameId, GameState.fromString(gameState));
            logger.info("Game: {} : Successfully updated gameState to {}", gameId, gameState);
            return ResponseEntity.ok(updatedGame);
        } catch (IllegalArgumentException e) {
            logger.error("Error while updating gameState", e.getMessage());
            return ResponseEntity.status(400).body(Map.of("error", "Could not update gameState", "message", e.getMessage()));
        } catch (IllegalStateException e) {
            logger.error("Error while updating gameState", e.getMessage());
            return ResponseEntity.status(400).body(Map.of("error", "Could not update gameState", "message", e.getMessage()));
        }
    }

    @GetMapping("/get-all-games")
    public ResponseEntity<?> getAllGames() {
      try {
        List<GameModel> allGames = gameService.getAllGames();
        return ResponseEntity.ok(allGames);
      } catch (Exception e) {
        return ResponseEntity.status(500).body(Map.of("error", "Could not get all games", "message", e.getMessage()));
      }
    }
}
