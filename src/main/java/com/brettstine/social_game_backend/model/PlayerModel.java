package com.brettstine.social_game_backend.model;

public class PlayerModel {
  private String playerName;
  private String sessionId;

  // Constructors
  public PlayerModel() {
  }

  public PlayerModel(String playerName, String sessionId) {
    this.playerName = playerName;
    this.sessionId = sessionId;
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
}
