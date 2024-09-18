package com.brettstine.social_game_backend.model;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "question_answer")
@IdClass(QuestionAnswerId.class)
public class QuestionAnswerModel {
    
    @Id
    @Column(name = "question_id", nullable = false)
    private String questionId;

    @Id
    @Column(name = "answer_id", nullable = false)
    private String answerId;

    @Column(name = "game_id", nullable = false)
    private String gameId;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    public QuestionAnswerModel() {
    }

    public QuestionAnswerModel(String questionId, String answerId, String gameId) {
        this.questionId = questionId;
        this.answerId = answerId;
        this.gameId = gameId;
        this.creationTime = LocalDateTime.now();
    }

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
        QuestionAnswerModel that = (QuestionAnswerModel) o;
        return questionId.equals(that.questionId) && answerId.equals(that.answerId) && gameId.equals(that.gameId) && creationTime.equals(that.creationTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, answerId, gameId, creationTime);
    }

}
