package com.brettstine.social_game_backend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerModel {
    private String playerId;
    private String gameId;
    private String name;

    public PlayerModel(String gameId, String name) {
        this.playerId = UUID.randomUUID().toString();
        this.gameId = gameId;
        this.name = name;
    }

    // Getters and Setters
    public String getGameId() {
        return gameId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
