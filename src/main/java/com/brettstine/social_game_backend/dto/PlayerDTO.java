package com.brettstine.social_game_backend.dto;

import com.brettstine.social_game_backend.model.PlayerModel;

public class PlayerDTO {

    private String name;
    private String playerId;

    public PlayerDTO(String name, String playerId) {
        this.name = name;
        this.playerId = playerId;
    }

    public PlayerDTO(PlayerModel player) {
        this.name = player.getName();
        this.playerId = player.getPlayerId();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
    
}
