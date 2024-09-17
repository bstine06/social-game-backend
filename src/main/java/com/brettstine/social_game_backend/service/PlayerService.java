package com.brettstine.social_game_backend.service;

import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.repository.PlayerRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository, GameService gameService) {
        this.playerRepository = playerRepository;
    }

    public PlayerModel createPlayer(String gameId, String name) {
        PlayerModel player = new PlayerModel(gameId, name);
        return playerRepository.save(player);
    }

    public PlayerModel getPlayer(String playerId) {
        return playerRepository.findById(playerId).orElseThrow(() -> new IllegalArgumentException("Player not found with ID: " + playerId));
    }

    public void deletePlayer(String playerId) {
        if (!playerRepository.existsById(playerId)) {
            throw new IllegalArgumentException("Player not found for ID: " + playerId);
        }
        playerRepository.deleteById(playerId);
    }

    public PlayerModel setName(String playerId, String name) {
        PlayerModel player = getPlayer(playerId);
        player.setName(name);
        return playerRepository.save(player);
    }

    public List<PlayerModel> getAllPlayersByGameId(String gameId) {
        return playerRepository.findByGameId(gameId);
    }

    public List<PlayerModel> getAllPlayers() {
        return playerRepository.findAll();
    }
}
