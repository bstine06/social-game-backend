package com.brettstine.social_game_backend.model;

public enum GameDeletionReason {
    DELETED_BY_INSUFFICIENT_PLAYERS,
    DELETED_BY_HOST,
    DELETED_BY_CLEAN_UP;

    // Static method to convert a string to a GameDeletionReason enum, with error handling
    public static GameDeletionReason fromString(String gameDeletionReasonString) {
        if (gameDeletionReasonString == null) {
            throw new IllegalArgumentException("Game deletion reason cannot be null");
        }
        try {
            return GameDeletionReason.valueOf(gameDeletionReasonString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Game Deletion Reason: " + gameDeletionReasonString);
        }
    }
}
