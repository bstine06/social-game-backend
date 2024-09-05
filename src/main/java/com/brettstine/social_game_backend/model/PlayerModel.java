package com.brettstine.social_game_backend.model;

import java.util.concurrent.atomic.AtomicInteger;

public class PlayerModel {
  private String playerName;
  private int playerId;
  private String sessionId;

  private static final AtomicInteger playerCounter = new AtomicInteger(0);

  // Constructors
  public PlayerModel() {
  }

  public PlayerModel(String playerName, String sessionId) {
    this.playerName = playerName;
    this.sessionId = sessionId;
    this.playerId = playerCounter.incrementAndGet();
  }

  // Getters and Setters
  public String getPlayerName() {
    return playerName;
  }

  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public int getPlayerId() {
    return playerId;
  }

  public static void resetPlayerCounter() {
    playerCounter.set(0);
  }
}
