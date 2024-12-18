package com.brettstine.social_game_backend.dto;

import com.brettstine.social_game_backend.model.PlayerModel;

public class PlayerDTO {

    private String name;
    private String playerId;
    private int shape;
    private String color;
    private int score;

    public PlayerDTO(String name, String playerId, int shape, String color, int score) {
        this.name = name;
        this.playerId = playerId;
        this.shape = shape;
        this.color = color;
        this.score = score;
    }

    public PlayerDTO(PlayerModel player) {
        this.name = player.getName();
        this.playerId = player.getPlayerId();
        this.shape = player.getShape();
        this.color = player.getColor();
        this.score = player.getScore();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getShape() {
        return this.shape;
    }

    public void setShape(int shape) {
        this.shape = shape;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
