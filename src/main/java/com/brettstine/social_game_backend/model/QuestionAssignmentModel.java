package com.brettstine.social_game_backend.model;

import java.time.Instant;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "question_assignment")
@IdClass(QuestionAssignmentId.class)
public class QuestionAssignmentModel {

    @Id
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private QuestionModel question;

    @Id
    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    @JsonIgnore
    private PlayerModel player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    @JsonIgnore
    private GameModel game;

    @Column(name = "creation_time", nullable = false)
    @JsonIgnore
    private Instant creationTime;

    public QuestionAssignmentModel() {
    }

    public QuestionAssignmentModel(QuestionModel question, PlayerModel player, GameModel game) {
        this.question = question;
        this.player = player;
        this.game = game;
        this.creationTime = Instant.now();
    }

    @JsonProperty("questionId")
    public String getQuestionId() {
        return question.getQuestionId();
    }

    @JsonProperty("playerId")
    public String getPlayerId() {
        return player.getPlayerId();
    }

    // Getters and Setters
    public QuestionModel getQuestion() {
        return question;
    }

    public void setQuestion(QuestionModel question) {
        this.question = question;
    }

    public PlayerModel getPlayer() {
        return player;
    }

    public void setPlayer(PlayerModel player) {
        this.player = player;
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
        QuestionAssignmentModel that = (QuestionAssignmentModel) o;
        return question.equals(that.question) &&
               player.equals(that.player) &&
               game.equals(that.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question, player, game);
    }
}

