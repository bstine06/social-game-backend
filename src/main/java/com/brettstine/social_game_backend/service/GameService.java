package com.brettstine.social_game_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.repository.GameRepository;
import com.brettstine.social_game_backend.model.GameState;
import com.brettstine.social_game_backend.utils.GameCodeGenerator;

@Service
public class GameService {

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public GameModel createGame() {
        String gameCode = GameCodeGenerator.generateGameCode();
        int attempts = 0;
        int maxAttempts = 100; // Limit number of tries to find a game code.
        // If this ever becomes a problem, we can scale the game codes to be longer.

        while (gameRepository.existsById(gameCode)) {
            if (attempts >= maxAttempts) {
                throw new IllegalStateException(
                        "Unable to generate a unique game code after " + maxAttempts + " attempts");
            }
            gameCode = GameCodeGenerator.generateGameCode();
            attempts++;
        }

        GameModel game = new GameModel(gameCode);
        return gameRepository.save(game);
    }

    public void deleteGame(String gameId) {
        if (!gameRepository.existsById(gameId)) {
            throw new IllegalArgumentException("Game not found with ID: " + gameId);
        }
        gameRepository.deleteById(gameId);
    }

    public GameModel getGameById(String gameId) {
        return gameRepository.findById(gameId).orElseThrow(() -> new IllegalArgumentException("Game not found with ID: " + gameId));
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
}
