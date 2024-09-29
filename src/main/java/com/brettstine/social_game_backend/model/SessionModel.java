package com.brettstine.social_game_backend.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "session")
public class SessionModel {
    
    @Id
    @Column(name = "session_id", nullable = false, unique = true)
    private String sessionId;

    // Many sessions can be associated with one game
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = true)
    @JsonIgnore
    private GameModel game;

    // Each player can only have one session
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = true, unique = true)
    private PlayerModel player;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name= "creation_time", nullable = false)
    private LocalDateTime creationTime;

    public SessionModel() {
        this.sessionId = UUID.randomUUID().toString();
        this.role = Role.UNASSIGNED;
        this.creationTime = LocalDateTime.now();
    }

    // Getters and Setters

    public String getSessionId() {
        return this.sessionId;
    }

    public GameModel getGame() {
        return this.game;
    }

    @JsonProperty("gameId")
    public String getGameId() {
        return this.game.getGameId();
    }

    public void setGame(GameModel game) {
        this.game = game;
    }

    public PlayerModel getPlayer() {
        return this.player;
    }

    public void setPlayer(PlayerModel player) {
        this.player = player;
    }

    public Role getRole() {
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getCreationTime() {
        return this.creationTime;
    }
}
