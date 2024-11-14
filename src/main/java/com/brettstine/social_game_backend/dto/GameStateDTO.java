package com.brettstine.social_game_backend.dto;

public class GameStateDTO {

    private String gameId;
    private String gameState;
    private String timerEnd;
    

    public GameStateDTO(String gameId, String gameState, String timerEnd) {
        this.gameId = gameId;
        this.gameState = gameState;
        this.timerEnd = timerEnd;
    }

    public String getGameId() {
        return this.gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameState() {
        return this.gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public String getTimerEnd() {
        return this.timerEnd;
    }

    public void setTimerEnd(String timerEnd) {
        this.timerEnd = timerEnd;
    }

    
}
