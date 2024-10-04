package com.brettstine.social_game_backend.dto;

public class VoteDTO {
    
    private String playerId;
    private String playerName;
    private String answerId;

    public VoteDTO(String playerId, String playerName, String answerId) {
        this.playerId = playerId;
        this.playerName = playerName;
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

    public String getAnswerId() {
        return this.answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

}
