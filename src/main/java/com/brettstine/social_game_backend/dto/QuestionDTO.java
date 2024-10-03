package com.brettstine.social_game_backend.dto;

public class QuestionDTO {
    
    private String content;
    private String questionId;
    private String playerName;

    public QuestionDTO(String content, String questionId, String playerName) {
        this.content = content;
        this.questionId = questionId;
        this.playerName = playerName;
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

    public String getPlayerName() {
        return this.playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

}
