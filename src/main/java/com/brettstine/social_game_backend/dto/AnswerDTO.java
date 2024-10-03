package com.brettstine.social_game_backend.dto;

public class AnswerDTO {
    
    private String content;
    private String answerId;
    private String playerName;

    public AnswerDTO(String content, String answerId, String playerName) {
        this.content = content;
        this.answerId = answerId;
        this.playerName = playerName;
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

    public String getPlayerName() {
        return this.playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

}