package com.brettstine.social_game_backend.dto;

public class GameStateDTO {

    private String gameId;
    private String gameState;
    private String timerEnd;
    private int roundCount;
    

    public GameStateDTO(String gameId, String gameState, String timerEnd, int roundCount) {
        this.gameId = gameId;
        this.gameState = gameState;
        this.timerEnd = timerEnd;
        this.roundCount = roundCount;
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

    public int getRoundCount() {
        return this.roundCount;
    }

    public void setRoundCount(int roundCount) {
        this.roundCount = roundCount;
    }
    
}
