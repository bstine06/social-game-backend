package com.brettstine.social_game_backend.repository;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.brettstine.social_game_backend.model.PlayerModel;

@Component
public class PlayerDatabase {

  private Map<String, PlayerModel> playerStore;

  public PlayerDatabase() {
    playerStore = new ConcurrentHashMap<>();
  }

  public void addPlayer(PlayerModel player) {
    String playerId = player.getPlayerId();
    if (playerStore.containsKey(playerId)) {
      throw new IllegalArgumentException("Player already exists with ID: " + playerId);
    }
    playerStore.put(playerId, player);
  }

  public void deletePlayer(String playerId) {
    if (!playerStore.containsKey(playerId)) {
      throw new IllegalArgumentException("Player not found for ID: " + playerId);
    }
    playerStore.remove(playerId);
  }

  public PlayerModel getPlayerById(String playerId) {
    if (!playerStore.containsKey(playerId)) {
      throw new IllegalArgumentException("Player not found for ID: " + playerId);
    }
    return playerStore.get(playerId);
  }

  public List<PlayerModel> getAllPlayersByGameId(String gameId) {
    return playerStore.values()
                      .stream()
                      .filter(player -> gameId.equals(player.getGameId()))
                      .collect(Collectors.toList());
  }

  public List<PlayerModel> getAllPlayers() {
    List<PlayerModel> allPlayers = new ArrayList<>(playerStore.values());
    return allPlayers;
  }

  public PlayerModel setNameOfPlayer(String playerId, String name) {
    if (!playerStore.containsKey(playerId)) {
      throw new IllegalArgumentException("Player not found for ID: " + playerId);
    }
    PlayerModel player = playerStore.get(playerId);
    player.setName(name);
    playerStore.put(playerId, player);
    return player;
  }

  public String getSubmittedQuestionId(String playerId) {
    if (!playerStore.containsKey(playerId)) {
      throw new IllegalArgumentException("Player not found for ID: " + playerId);
    }
    PlayerModel player = playerStore.get(playerId);
    return player.getSubmittedQuestionId();
  }

  public PlayerModel setSubmittedQuestionId(String playerId, String questionId) {
    if (!playerStore.containsKey(playerId)) {
      throw new IllegalArgumentException("Player not found for ID: " + playerId);
    }
    PlayerModel player = playerStore.get(playerId);
    player.setSubmittedQuestionId(questionId);
    playerStore.put(playerId, player);
    return player;
  }
}