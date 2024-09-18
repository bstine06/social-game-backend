package com.brettstine.social_game_backend.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "player")
public class PlayerModel {

    @Id
    @Column(name = "player_id", nullable = false, unique = true)
    private String playerId;

    @Column(name = "game_id", nullable = false)
    private String gameId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    public PlayerModel() {
    }

    public PlayerModel(String gameId, String name) {
        this.playerId = UUID.randomUUID().toString();
        this.gameId = gameId;
        this.name = name;
        this.creationTime = LocalDateTime.now();
    }

    // Getters and Setters
    public String getGameId() {
        return gameId;
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
