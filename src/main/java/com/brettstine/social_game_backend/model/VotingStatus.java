package com.brettstine.social_game_backend.model;

public enum VotingStatus {
    NOT_VOTED,
    IN_PROGRESS,
    COMPLETE;

    // Static method to convert a string to a VotingStatus enum, with error handling
    public static VotingStatus fromString(String votingStatusString) {
        if (votingStatusString == null) {
            throw new IllegalArgumentException("Voting status cannot be null");
        }
        try {
            return VotingStatus.valueOf(votingStatusString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid voting status: " + votingStatusString);
        }
    }
}
