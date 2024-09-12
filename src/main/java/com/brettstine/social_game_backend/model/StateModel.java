package com.brettstine.social_game_backend.model;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class StateModel {
    /* Possible values: 
     *  pregame
     *  game
     *  postgame
    */
    private String appState;
    /* Possible values:
     *  inactive
     *  asking
     *  assigning
     *  answering
     *  voting
     *  scoring
     */
    private String gameState;

    // Constructor, getters, and setters
    public StateModel() {
        this.appState = "pregame"; // Default state
        this.gameState = "inactive";
    }

    public String getAppState() {
        return appState;
    }

    public void setAppState(String appState) {
        this.appState = appState;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }
}
