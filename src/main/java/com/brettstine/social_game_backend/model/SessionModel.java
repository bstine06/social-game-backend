package com.brettstine.social_game_backend.model;

import com.brettstine.social_game_backend.model.PlayerModel;

public class SessionModel {
    private String sessionId;
    private PlayerModel playerModel;

    // Constructors
    public SessionModel() {}

    public SessionModel(String sessionId) {
        this.sessionId = sessionId;
    }

    public SessionModel(String sessionId, PlayerModel player) {
        this.sessionId = sessionId;
        this.playerModel = player;
    }

    // Getters and Setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public PlayerModel getPlayer() {
        return playerModel;
    }

    public void setPlayer(PlayerModel playerModel) {
        this.playerModel = playerModel;
    }
}
