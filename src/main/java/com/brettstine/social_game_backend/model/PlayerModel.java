package com.brettstine.social_game_backend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerModel {
    private String playerId;
    private String gameId;
    private String name;
    private String submittedQuestionId;
    private List<String> questionIdsToAnswer;

    public PlayerModel(String gameId, String name) {
        this.playerId = UUID.randomUUID().toString();
        this.gameId = gameId;
        this.name = null;
        this.submittedQuestionId = null;
        this.questionIdsToAnswer = new ArrayList<>();
    }

    // Getters and Setters
    public String getGameId() {
        return gameId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubmittedQuestionId() {
        return submittedQuestionId;
    }

    public void setSubmittedQuestionId(String questionId) {
        this.submittedQuestionId = questionId;
    }

    public List<String> getQuestionIdsToAnswer() {
        return questionIdsToAnswer;
    }

    public void addQuestionIdToAnswer(String questionId) {
        this.questionIdsToAnswer.add(questionId);
    }
}
