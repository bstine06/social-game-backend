package com.brettstine.social_game_backend.model;

public class SessionModel {
    private String sessionId;
    private String sessionData;

    // Constructors
    public SessionModel() {}

    public SessionModel(String sessionId, String sessionData) {
        this.sessionId = sessionId;
        this.sessionData = sessionData;
    }

    // Getters and Setters
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionData() {
        return sessionData;
    }

    public void setSessionData(String sessionData) {
        this.sessionData = sessionData;
    }
}
