package com.brettstine.social_game_backend.model;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "question_assignment")
@IdClass(QuestionAssignmentId.class)
public class QuestionAssignmentModel {
    
    @Id
    @Column(name = "question_id", nullable = false)
    private String questionId;

    @Id
    @Column(name = "player_id", nullable = false)
    private String playerId;

    public QuestionAssignmentModel() {
    }

    public QuestionAssignmentModel(String questionId, String playerId) {
        this.questionId = questionId;
        this.playerId = playerId;
    }

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
        QuestionAssignmentModel that = (QuestionAssignmentModel) o;
        return questionId.equals(that.questionId) && playerId.equals(that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, playerId);
    }

}
