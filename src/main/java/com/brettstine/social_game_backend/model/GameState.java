package com.brettstine.social_game_backend.model;

public enum GameState {
    LOBBY,
    QUESTION,
    ASSIGN, 
    ANSWER,
    DISPLAY_ANSWERS,
    VOTE, 
    DISPLAY_VOTES,
    SCORE, 
    POSTGAME;

    // Static method to convert a string to a GameState enum, with error handling
    public static GameState fromString(String gameStateString) {
        if (gameStateString == null) {
            throw new IllegalArgumentException("Game state cannot be null");
        }
        try {
            return GameState.valueOf(gameStateString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid game state: " + gameStateString);
        }
    }
}
