package com.brettstine.social_game_backend.model;

public class SessionModel {
    private String sessionId;
    private String name;

    // Constructors
    public SessionModel() {}

    public SessionModel(String sessionId) {
        this.sessionId = sessionId;
    }

    public SessionModel(String sessionId, String name) {
        this.sessionId = sessionId;
        this.name = name;
    }

    // Getters and Setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
