package com.brettstine.social_game_backend.model;

import java.io.Serializable;
import java.util.Objects;

public class QuestionAnswerId implements Serializable {
    private String questionId;
    private String answerId;

    public QuestionAnswerId() {
    }

    public QuestionAnswerId(String questionId, String answerId) {
        this.questionId = questionId;
        this.answerId = answerId;
    }

    // Getters and Setters
    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionAnswerId that = (QuestionAnswerId) o;
        return questionId.equals(that.questionId) && answerId.equals(that.answerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, answerId);
    }
}