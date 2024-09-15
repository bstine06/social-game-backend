package com.brettstine.social_game_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.repository.GameDatabase;
import com.brettstine.social_game_backend.model.GameState;

@Service
public class GameService {
    private final GameDatabase gameDatabase;

    public GameService(GameDatabase gameDatabase) {
        this.gameDatabase = gameDatabase;
    }

    public GameModel createGame() {
        GameModel game = new GameModel();
        gameDatabase.addGame(game);
        return game;
    }

    public void deleteGame(String gameId) {
        gameDatabase.deleteGame(gameId);
    }

    public GameModel getGame(String gameId) {
        return gameDatabase.getGame(gameId);
    }

    public GameModel setGameState(String gameId, GameState gameState) {
        GameModel game = gameDatabase.getGame(gameId);
        game.setGameState(gameState);
        gameDatabase.updateGame(gameId, game);
        return game;
    }

    public GameState getGameState(String gameId) {
        return gameDatabase.getGame(gameId).getGameState();
    }

    public boolean confirmGameState(String gameId, GameState expectedGameState) {
        GameState actualGameState = gameDatabase.getGame(gameId).getGameState();
        return (expectedGameState == actualGameState);
    }

    public List<GameModel> getAllGames() {
        List<GameModel> allGames = gameDatabase.getAllGames();
        return allGames;
    }
}
