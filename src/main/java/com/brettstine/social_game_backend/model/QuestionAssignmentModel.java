package com.brettstine.social_game_backend.model;

import java.time.LocalDateTime;
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

    @Column(name = "game_id", nullable = false)
    private String gameId;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    public QuestionAssignmentModel() {
    }

    public QuestionAssignmentModel(String questionId, String playerId, String gameId) {
        this.questionId = questionId;
        this.playerId = playerId;
        this.gameId = gameId;
        this.creationTime = LocalDateTime.now();
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

    public String getGameId() {
        return gameId;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionAssignmentModel that = (QuestionAssignmentModel) o;
        return questionId.equals(that.questionId) && playerId.equals(that.playerId) && gameId.equals(that.gameId) && creationTime.equals(that.creationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, playerId, gameId, creationTime);
    }

}
