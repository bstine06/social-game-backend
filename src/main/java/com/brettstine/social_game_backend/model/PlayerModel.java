package com.brettstine.social_game_backend.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "player")
public class PlayerModel {

    @Id
    @Column(name = "player_id", nullable = false, unique = true)
    private String playerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    @JsonBackReference
    private GameModel game;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "shape")
    private int shape;

    @Column(name = "color")
    private String color;

    @Column(name = "ready", nullable = false)
    private boolean ready;

    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private QuestionModel question;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<AnswerModel> answers;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<QuestionAssignmentModel> questionAssignments;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PlayerAnswerVoteModel> playerAnswerVotes;

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "creation_time", nullable = false)
    @JsonIgnore
    private Instant creationTime;

    public PlayerModel() {
    }

    public PlayerModel(GameModel game, String name, int shape, String color) {
        this.playerId = UUID.randomUUID().toString();
        this.game = game;
        this.name = name;
        this.shape = shape;
        this.color = color;
        this.creationTime = Instant.now();
        this.score = 0;
        this.ready = false;
    }

    public PlayerModel(GameModel game, String name, int shape, String color, String playerId) {
        this.playerId = playerId;
        this.game = game;
        this.name = name;
        this.shape = shape;
        this.color = color;
        this.creationTime = Instant.now();
        this.score = 0;
        this.ready = false;
    }

    // Getters and Setters
    public GameModel getGame() {
        return game;
    }

    public void setGame(GameModel game) {
        this.game = game;
    }

    @JsonProperty("gameId")
    public String getGameId() {
        return game != null ? game.getGameId() : null;  // Only return the gameId
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public int getShape() {
        return shape;
    }

    public String getColor() {
        return color;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public QuestionModel getQuestion() {
        return question;
    }

    public List<AnswerModel> getAnswers() {
        return answers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore() {
        score++;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;  // Check if the references are the same
        if (o == null || getClass() != o.getClass()) return false;  // Ensure the object is not null and of the same class
        PlayerModel that = (PlayerModel) o;  // Cast the object to PlayerModel
        return Objects.equals(playerId, that.playerId);  // Compare based on the playerId (or other fields if needed)
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);  // Generate hashCode based on playerId (or other fields)
    }
}
