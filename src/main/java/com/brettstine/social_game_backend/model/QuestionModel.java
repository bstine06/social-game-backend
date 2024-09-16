package com.brettstine.social_game_backend.model;

import java.util.UUID;

public class QuestionModel {
    private String gameId;
    private String playerId;
    private String questionId;
    private String content;

    public QuestionModel(String gameId, String playerId, String content) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.questionId = UUID.randomUUID().toString();
        this.content = content;
    }

    public String getGameId() {
        return gameId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getContent() {
        return content;
    }

}