package com.brettstine.social_game_backend.dto;

import com.brettstine.social_game_backend.model.PlayerModel;

public class VoteDTO {
    
    private PlayerDTO player;
    private String answerId;

    public VoteDTO(PlayerModel playerModel, String answerId) {
        this.player = new PlayerDTO(playerModel);
        this.answerId = answerId;
    }

    public PlayerDTO getPlayer() {
        return this.player;
    }

    public void setPlayer(PlayerDTO playerModel) {
        this.player = playerModel;
    }

    public String getAnswerId() {
        return this.answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

}
