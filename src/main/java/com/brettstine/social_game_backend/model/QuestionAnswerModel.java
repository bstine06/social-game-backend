package com.brettstine.social_game_backend.model;

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

    public QuestionAnswerModel() {
    }

    public QuestionAnswerModel(String questionId, String answerId) {
        this.questionId = questionId;
        this.answerId = answerId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionAnswerModel that = (QuestionAnswerModel) o;
        return questionId.equals(that.questionId) && answerId.equals(that.answerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId, answerId);
    }

}
