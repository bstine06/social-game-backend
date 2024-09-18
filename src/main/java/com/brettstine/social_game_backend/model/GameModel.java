package com.brettstine.social_game_backend.model;

import java.time.LocalDateTime;

import org.springframework.cglib.core.Local;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "game")
public class GameModel {

    @Id
    @Column(name = "game_id", nullable = false, unique = true)
    private String gameId;

    @Column(name = "game_state", nullable = false)
    private GameState gameState;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    public GameModel() {
    }

    public GameModel(String gameId) {
        this.gameState = GameState.LOBBY;
        this.gameId = gameId;
        this.creationTime = LocalDateTime.now();
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }
}
