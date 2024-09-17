package com.brettstine.social_game_backend.model;

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

    public GameModel() {
    }

    public GameModel(String gameId) {
        this.gameState = GameState.LOBBY;
        this.gameId = gameId;
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
}
