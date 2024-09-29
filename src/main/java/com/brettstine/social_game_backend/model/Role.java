package com.brettstine.social_game_backend.model;

public enum Role {
    UNASSIGNED,
    HOST,
    PLAYER;

    // Static method to convert a string to a GameState enum, with error handling
    public static Role fromString(String roleString) {
        if (roleString == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        try {
            return Role.valueOf(roleString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role: " + roleString);
        }
    }
}
