package com.brettstine.social_game_backend.dto;

public class AnswerDTO {
    
    private String content;
    private String answerId;
    private String playerId;
    private String playerName;

    public AnswerDTO(String content, String answerId, String playerId, String playerName) {
        this.content = content;
        this.answerId = answerId;
        this.playerId = playerId;
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

    public String getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

}