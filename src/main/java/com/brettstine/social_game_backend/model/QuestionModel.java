package com.brettstine.social_game_backend.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "question")
public class QuestionModel {

    @Id
    @Column(name = "question_id", nullable = false, unique = true)
    private String questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    @JsonIgnore
    private GameModel game;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    @JsonIgnore
    private PlayerModel player;

    @Column(name = "content")
    private String content;

    @Column(name = "creation_time", nullable = false)
    @JsonIgnore
    private LocalDateTime creationTime;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<AnswerModel> answers;

    public QuestionModel() {
    }

    public QuestionModel(GameModel game, PlayerModel player, String content) {
        this.questionId = UUID.randomUUID().toString();
        this.game = game;
        this.player = player;
        this.content = content;
        this.creationTime = LocalDateTime.now();
    }

    public String getQuestionId() {
        return questionId;
    }

    public GameModel getGame() {
        return game;
    }

    @JsonProperty("gameId")
    public String getGameId() {
        return game != null ? game.getGameId() : null;  // Only return the gameId
    }

    public PlayerModel getPlayer() {
        return player;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public List<AnswerModel> getAnswers() {
        return answers;
    }

    public void setGame(GameModel game) {
        this.game = game;
    }

    public void setPlayer(PlayerModel player) {
        this.player = player;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public void setAnswers(List<AnswerModel> answers) {
        this.answers = answers;
    }
}
