package com.brettstine.social_game_backend.model;

import java.util.UUID;

public class QuestionModel {
    private String questionId;
    private String playerId;
    private String content;

    public QuestionModel(String playerId, String content) {
        this.questionId = UUID.randomUUID().toString();
        this.playerId = playerId;
        this.content = content;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getContent() {
        return content;
    }

}