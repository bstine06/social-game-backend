package com.brettstine.social_game_backend.model;

import java.time.Instant;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

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
    @JsonIgnore
    private PlayerModel player;

    @Id
    @ManyToOne
    @JoinColumn(name = "answer_id", nullable = false)
    @JsonIgnore
    private AnswerModel answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    @JsonBackReference
    private GameModel game;

    @Column(name = "creation_time", nullable = false)
    @JsonIgnore
    private Instant creationTime;

    public PlayerAnswerVoteModel() {
    }

    public PlayerAnswerVoteModel(PlayerModel player, AnswerModel answer, GameModel game) {
        this.player = player;
        this.answer = answer;
        this.game = game;
        this.creationTime = Instant.now();
    }

    // Getters and Setters
    public PlayerModel getPlayer() {
        return player;
    }

    @JsonProperty("playerId")
    public String getPlayerId() {
        return player.getPlayerId();
    }

    public void setPlayer(PlayerModel player) {
        this.player = player;
    }

    public AnswerModel getAnswer() {
        return answer;
    }

    @JsonProperty("answerId")
    public String getAnswerId() {
        return answer.getAnswerId();
    }

    public void setAnswer(AnswerModel answer) {
        this.answer = answer;
    }

    public GameModel getGame() {
        return game;
    }

    @JsonIgnore
    public String getGameId() {
        return game != null ? game.getGameId() : null;  // Only return the gameId
    }

    public void setGame(GameModel game) {
        this.game = game;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Instant creationTime) {
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


