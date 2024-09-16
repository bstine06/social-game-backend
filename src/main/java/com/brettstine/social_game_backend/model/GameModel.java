package com.brettstine.social_game_backend.model;

public class GameModel {

    private String gameId;
    private GameState gameState;

    public GameModel(String gameId) {
        this.gameState = GameState.LOBBY;
        this.gameId = gameId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
