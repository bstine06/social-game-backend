package com.brettstine.social_game_backend.model;

import java.time.LocalDateTime;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "player_answer_vote")
@IdClass(PlayerAnswerVoteId.class)
public class PlayerAnswerVoteModel {

    @Id
    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private PlayerModel player;

    @Id
    @ManyToOne
    @JoinColumn(name = "answer_id", nullable = false)
    private AnswerModel answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private GameModel game;

    @Column(name = "creation_time", nullable = false)
    @JsonIgnore
    private LocalDateTime creationTime;

    public PlayerAnswerVoteModel() {
    }

    public PlayerAnswerVoteModel(PlayerModel player, AnswerModel answer, GameModel game) {
        this.player = player;
        this.answer = answer;
        this.game = game;
        this.creationTime = LocalDateTime.now();
    }

    // Getters and Setters
    public PlayerModel getPlayer() {
        return player;
    }

    public void setPlayer(PlayerModel player) {
        this.player = player;
    }

    public AnswerModel getAnswer() {
        return answer;
    }

    public void setAnswer(AnswerModel answer) {
        this.answer = answer;
    }

    public GameModel getGame() {
        return game;
    }

    public void setGame(GameModel game) {
        this.game = game;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerAnswerVoteModel that = (PlayerAnswerVoteModel) o;
        return player.equals(that.player) &&
               answer.equals(that.answer) &&
               game.equals(that.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, answer, game);
    }
}


