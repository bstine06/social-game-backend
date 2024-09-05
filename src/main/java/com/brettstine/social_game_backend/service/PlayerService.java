package com.brettstine.social_game_backend.service;

import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.SessionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlayerService {

  private static final Map<String, PlayerModel> players = new ConcurrentHashMap<>();

  @Autowired
  private SessionService sessionService;

  public PlayerModel addPlayer(String playerName, String sessionId) {
    // Check if the session ID already has a player associated
    if (players.containsKey(sessionId)) {
      throw new IllegalStateException("Player already exists for this session ID");
    }

    // Retrieve session to ensure it's valid
    SessionModel session = sessionService.getSession(sessionId);
    if (session == null) {
      throw new IllegalArgumentException("Session not found");
    }

    // Create a new player
    PlayerModel player = new PlayerModel(playerName, sessionId);
    players.put(sessionId, player);
    return player;
  }

  public PlayerModel getPlayerBySessionId(String sessionId) {
    return players.get(sessionId);
  }

  public Collection<PlayerModel> getAllPlayers() {
    return players.values();
  }

  public void removeAllPlayers() {
    // remove every player from players Map
    players.clear();
    // reset playerCounter to 0
    PlayerModel.resetPlayerCounter();
  }

}
