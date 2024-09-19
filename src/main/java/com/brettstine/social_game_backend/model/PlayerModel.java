package com.brettstine.social_game_backend.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "player")
public class PlayerModel {

    @Id
    @Column(name = "player_id", nullable = false, unique = true)
    private String playerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    @JsonIgnore
    private GameModel game;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "creation_time", nullable = false)
    @JsonIgnore
    private LocalDateTime creationTime;

    public PlayerModel() {
    }

    public PlayerModel(GameModel game, String name) {
        this.playerId = UUID.randomUUID().toString();
        this.game = game;
        this.name = name;
        this.creationTime = LocalDateTime.now();
    }

    // Getters and Setters
    public GameModel getGame() {
        return game;
    }

    public void setGame(GameModel game) {
        this.game = game;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }
}
