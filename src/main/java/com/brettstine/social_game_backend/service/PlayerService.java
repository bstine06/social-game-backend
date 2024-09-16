package com.brettstine.social_game_backend.service;

import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.repository.PlayerDatabase;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerDatabase playerDatabase;
    
    private final GameService gameService;

    public PlayerService(PlayerDatabase playerDatabase, GameService gameService) {
        this.playerDatabase = playerDatabase;
        this.gameService = gameService;
    }

    public PlayerModel createPlayer(String gameId, String name) {
        try {
            gameService.getGame(gameId);
            PlayerModel player = new PlayerModel(gameId, name);
            playerDatabase.addPlayer(player);
            return player;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public PlayerModel getPlayer(String playerId) {
        return playerDatabase.getPlayerById(playerId);
    }

    public void deletePlayer(String playerId) {
        playerDatabase.deletePlayer(playerId);
    }

    public PlayerModel setName(String playerId, String name) {
        PlayerModel player = playerDatabase.setNameOfPlayer(playerId, name);
        return player;
    }

    public List<PlayerModel> getAllPlayersByGameId(String gameId) {
        try {
            gameService.getGame(gameId);
            return playerDatabase.getAllPlayersByGameId(gameId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public List<PlayerModel> getAllPlayers() {
        return playerDatabase.getAllPlayers();
    }
}
