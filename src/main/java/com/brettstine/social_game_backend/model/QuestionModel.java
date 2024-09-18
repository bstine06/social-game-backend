package com.brettstine.social_game_backend.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "question")
public class QuestionModel {

    @Column(name = "game_id", nullable = false)
    private String gameId;

    @Column(name = "player_id", nullable = false)
    private String playerId;

    @Id
    @Column(name = "question_id", nullable = false, unique = true)
    private String questionId;

    @Column(name = "content")
    private String content;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    public QuestionModel() {
    }

    public QuestionModel(String gameId, String playerId, String content) {
        this.gameId = gameId;
        this.playerId = playerId;
        this.questionId = UUID.randomUUID().toString();
        this.content = content;
        this.creationTime = LocalDateTime.now();
    }

    public String getGameId() {
        return gameId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

}