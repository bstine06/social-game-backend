package com.brettstine.social_game_backend.model;

import java.util.concurrent.atomic.AtomicBoolean;

public class PlayerModel {
  private String playerName;
  private boolean hostPlayer;

  private static final AtomicBoolean hostPlayerFlag = new AtomicBoolean(true);

  // Constructors
  public PlayerModel() {
  }

  public PlayerModel(String playerName) {
    this.playerName = playerName;
    this.hostPlayer = hostPlayerFlag.getAndSet(false);
  }

  // Getters and Setters
  public String getPlayerName() {
    return playerName;
  }

  public void setPlayerName(String playerName) {
    this.playerName = playerName;
  }

  public boolean isHostPlayer() {
    return hostPlayer;
  }

  public static void resetHostPlayerFlag() {
    hostPlayerFlag.set(true);
  }
}
