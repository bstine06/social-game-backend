package com.brettstine.social_game_backend.dto;

import com.brettstine.social_game_backend.model.PlayerModel;

public class AnswerDTO {
    
    private String content;
    private String answerId;
    private PlayerDTO player;
    private boolean userSubmitted;

    public AnswerDTO(String content, String answerId, PlayerModel playerModel) {
        this.content = content;
        this.answerId = answerId;
        this.player = new PlayerDTO(playerModel);
        this.userSubmitted = true;
    }

    public AnswerDTO(PlayerModel player, boolean userSubmitted) {
        this.player = new PlayerDTO(player);
        this.userSubmitted = userSubmitted;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAnswerId() {
        return this.answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public PlayerDTO getPlayer() {
        return this.player;
    }

    public void setPlayer(PlayerModel playerModel) {
        this.player = new PlayerDTO(playerModel);
    }

    public boolean isUserSubmitted() {
        return this.userSubmitted;
    }

    public void setUserSubmitted(boolean userSubmitted) {
        this.userSubmitted = userSubmitted;
    }

}