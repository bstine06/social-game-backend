package com.brettstine.social_game_backend.model;

public class SessionModel {
    private String sessionId;

    // Constructors
    public SessionModel() {}

    public SessionModel(String sessionId) {
        this.sessionId = sessionId;
    }

    // Getters and Setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
