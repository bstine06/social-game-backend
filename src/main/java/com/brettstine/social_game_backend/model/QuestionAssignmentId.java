package com.brettstine.social_game_backend.model;

import java.io.Serializable;
import java.util.Objects;

public class QuestionAssignmentId implements Serializable {
    private String questionId;
    private String playerId;

    public QuestionAssignmentId() {
    }

    public QuestionAssignmentId(String questionId, String playerId) {
        this.questionId = questionId;
        this.playerId = playerId;
    }

    // Getters and Setters
    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionAssignmentId that = (QuestionAssignmentId) o;
        return questionId.equals(that.questionId) && playerId.equals(that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, playerId);
    }
}

