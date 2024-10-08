package com.brettstine.social_game_backend.model;

import java.io.Serializable;
import java.util.Objects;

public class QuestionAssignmentId implements Serializable {
    private String question;
    private String player;

    // Default constructor
    public QuestionAssignmentId() {
    }

    public QuestionAssignmentId(String question, String player) {
        this.player = player;
        this.question = question;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QuestionAssignmentId that = (QuestionAssignmentId) o;
        return player.equals(that.player) &&
               question.equals(that.question);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, question);
    }
}