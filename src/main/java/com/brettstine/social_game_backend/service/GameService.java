package com.brettstine.social_game_backend.service;

import java.util.List;
import java.time.LocalDateTime;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.repository.GameRepository;
import com.brettstine.social_game_backend.model.GameState;
import com.brettstine.social_game_backend.utils.GameCodeGenerator;

import jakarta.transaction.Transactional;

@Service
public class GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameFlowService.class);

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public GameModel createGame() {
        String gameCode = GameCodeGenerator.generateGameCode();
        int attempts = 0;
        int maxAttempts = 100; // Limit number of tries to find a game code.
        // If this ever becomes a problem, we can scale the game codes to be longer.

        logger.info("Checking for existence of game with randomly generated id: {}", gameCode);
        while (gameRepository.existsById(gameCode)) {
            if (attempts >= maxAttempts) {
                throw new IllegalStateException(
                        "Unable to generate a unique game code after " + maxAttempts + " attempts");
            }
            gameCode = GameCodeGenerator.generateGameCode();
            attempts++;
        }

        GameModel game = new GameModel(gameCode);

        logger.info("Storing a new game record with id: {}", gameCode);
        return gameRepository.save(game);
    }

    public void deleteGame(String gameId) {
        logger.info("Checking for existence of game with randomly generated id: {}", gameId);
        if (!gameRepository.existsById(gameId)) {
            throw new IllegalArgumentException("Game not found with ID: " + gameId);
        }
        logger.info("Deleting game record with id: {}", gameId);
        gameRepository.deleteById(gameId);
    }

    @Transactional
    public GameModel getGameById(String gameId) {
        GameModel game = gameRepository.findById(gameId).orElseThrow(() -> new IllegalArgumentException("Game not found with ID: " + gameId));
        Hibernate.initialize(game.getPlayers());
        return game;
    }

    public GameModel setGameState(GameModel game, GameState gameState) {
        game.setGameState(gameState);
        return gameRepository.save(game);
    }

    public GameState getGameState(GameModel game) {
        return game.getGameState();
    }

    public boolean confirmGameState(GameModel game, GameState expectedGameState) {
        GameState actualGameState = game.getGameState();
        return (expectedGameState == actualGameState);
    }

    public List<GameModel> getAllGames() {
        List<GameModel> allGames = gameRepository.findAll();
        return allGames;
    }

    public LocalDateTime getTimerEnd(GameModel game) {
        return game.getTimerEnd();
    }

    public void setTimerEnd(GameModel game, LocalDateTime time) {
        game.setTimerEnd(time);
    }
}
