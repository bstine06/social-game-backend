package com.brettstine.social_game_backend.model;

import java.util.UUID;

public class GameModel {

    private String gameId;
    private GameState gameState;

    public GameModel() {
        this.gameId = UUID.randomUUID().toString();
        this.gameState = GameState.LOBBY;
    }

    public String getGameId() {
        return gameId;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
