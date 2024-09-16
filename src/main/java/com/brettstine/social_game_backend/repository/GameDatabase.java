package com.brettstine.social_game_backend.repository;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.brettstine.social_game_backend.model.GameModel;

@Component
public class GameDatabase {

    private Map<String, GameModel> gameStore;

    public GameDatabase() {
        this.gameStore = new ConcurrentHashMap<>();
    }

    public void addGame(GameModel game) {
        String gameId = game.getGameId();
        if (gameStore.containsKey(gameId)) {
            throw new IllegalArgumentException("Game already exists with ID: " + gameId);
        }
        gameStore.put(gameId, game);
    }

    public GameModel getGame(String gameId) {
        if (!gameStore.containsKey(gameId)) {
            throw new IllegalArgumentException("Game not found for ID: " + gameId);
        }
        return gameStore.get(gameId);
    }

    public boolean hasGameByGameId(String gameId) {
        return gameStore.values().stream()
            .anyMatch(question -> question.getGameId().equals(gameId));
    }   

    public GameModel updateGame(String gameId, GameModel game) {
        if (!gameStore.containsKey(gameId)) {
            throw new IllegalArgumentException("Game not found for ID: " + gameId);
        }
        return gameStore.replace(gameId, game);
    }

    public void deleteGame(String gameId) {
        if (!gameStore.containsKey(gameId)) {
            throw new IllegalArgumentException("Game not found for ID: " + gameId);
        }
        gameStore.remove(gameId);
    }

    public List<GameModel> getAllGames() {
        List<GameModel> allGames = new ArrayList<>(gameStore.values());
        return allGames;
    }
    
}
