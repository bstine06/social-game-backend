package com.brettstine.social_game_backend.model;

import org.springframework.stereotype.Component;

@Component
public class AppStateModel {
    private String appState;

    // Constructor, getters, and setters
    public AppStateModel() {
        this.appState = "pregame"; // Default state
    }

    public String getAppState() {
        return appState;
    }

    public void setAppState(String appState) {
        this.appState = appState;
    }
}
