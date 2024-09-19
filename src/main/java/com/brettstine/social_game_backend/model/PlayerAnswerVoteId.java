package com.brettstine.social_game_backend.model;

import java.io.Serializable;
import java.util.Objects;

public class PlayerAnswerVoteId implements Serializable {
    private String player;
    private String answer;

    // Default constructor
    public PlayerAnswerVoteId() {
    }

    public PlayerAnswerVoteId(String player, String answer) {
        this.player = player;
        this.answer = answer;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerAnswerVoteId that = (PlayerAnswerVoteId) o;
        return player.equals(that.player) &&
               answer.equals(that.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, answer);
    }
}
