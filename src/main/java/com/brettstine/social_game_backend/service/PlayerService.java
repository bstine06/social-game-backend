package com.brettstine.social_game_backend.service;

import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.repository.PlayerRepository;
import com.brettstine.social_game_backend.websocket.WatchPlayersWebSocketHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    private static final Logger logger = LoggerFactory.getLogger(PlayerService.class);

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public PlayerModel createPlayer(GameModel game, String name) {
        PlayerModel player = new PlayerModel(game, name);
        player = playerRepository.save(player);
        return player;
    }

    public PlayerModel getPlayerById(String playerId) {
        return playerRepository.findById(playerId).orElseThrow(() -> new IllegalArgumentException("Player not found with ID: " + playerId));
    }

    public void deletePlayerById(String playerId) {
        playerRepository.deleteById(playerId);
    }

    public PlayerModel setName(String playerId, String name) {
        PlayerModel player = getPlayerById(playerId);
        player.setName(name);
        return playerRepository.save(player);
    }

    public void setReady(String playerId, boolean ready) {
        PlayerModel player = getPlayerById(playerId);
        player.setReady(ready);
        playerRepository.save(player);
    }

    public List<PlayerModel> getAllPlayersByGame(GameModel game) {
        return playerRepository.findByGame(game);
    }

    public List<PlayerModel> getAllPlayers() {
        return playerRepository.findAll();
    }

    public void incrementScoreForPlayer(PlayerModel player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        player.incrementScore();
        playerRepository.save(player);
    }

    public void unreadyAllPlayersInGame(GameModel game) {
        List<PlayerModel> players = getAllPlayersByGame(game);
        players.stream()
                .forEach(p -> {
                    p.setReady(false);
                    playerRepository.save(p);
                });
    }
}
