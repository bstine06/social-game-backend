package com.brettstine.social_game_backend.dto;

import com.brettstine.social_game_backend.model.PlayerModel;

public class QuestionDTO {
    
    private String content;
    private String questionId;
    private PlayerDTO player;

    public QuestionDTO(String content, String questionId, PlayerModel playerModel) {
        this.content = content;
        this.questionId = questionId;
        this.player = new PlayerDTO(playerModel);
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getQuestionId() {
        return this.questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public PlayerDTO getPlayer() {
        return this.player;
    }

    public void setPlayer(PlayerModel playerModel) {
        this.player = new PlayerDTO(playerModel);
    }

}
