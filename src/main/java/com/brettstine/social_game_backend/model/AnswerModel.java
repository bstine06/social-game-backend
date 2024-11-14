package com.brettstine.social_game_backend.model;

import java.time.Instant;
import java.util.UUID;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
import jakarta.persistence.Table;

@Entity
@Table(name = "answer")
public class AnswerModel {

    @Id
    @Column(name = "answer_id", nullable = false, unique = true)
    private String answerId;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    @JsonIgnore
    private GameModel game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    @JsonBackReference
    private PlayerModel player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonBackReference
    private QuestionModel question;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PlayerAnswerVoteModel> playerAnswerVotes;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "creation_time", nullable = false)
    @JsonIgnore
    private Instant creationTime;

    public AnswerModel() {
    }

    public AnswerModel(GameModel game, PlayerModel player, QuestionModel question, String content) {
        this.answerId = UUID.randomUUID().toString();
        this.game = game;
        this.player = player;
        this.question = question;
        this.content = content;
        this.creationTime = Instant.now();
    }

    // Getters and Setters
    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    @JsonProperty("questionId")
    public String getQuestionId() {
        return question != null ? question.getQuestionId() : null;
    }

    @JsonProperty("playerId")
    public String getPlayerId() {
        return player != null ? player.getPlayerId() : null;
    }

    public GameModel getGame() {
        return game;
    }

    @JsonProperty("gameId")
    public String getGameId() {
        return game != null ? game.getGameId() : null;  // Only return the gameId
    }

    public void setGame(GameModel game) {
        this.game = game;
    }

    public PlayerModel getPlayer() {
        return player;
    }

    public void setPlayer(PlayerModel player) {
        this.player = player;
    }

    public QuestionModel getQuestion() {
        return question;
    }

    public void setQuestion(QuestionModel question) {
        this.question = question;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Instant creationTime) {
        this.creationTime = creationTime;
    }
}
