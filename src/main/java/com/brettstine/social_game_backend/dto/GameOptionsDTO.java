package com.brettstine.social_game_backend.dto;

public class GameOptionsDTO {
    
    private long timerDuration;
    private boolean isHostPlayer;

    public GameOptionsDTO() {
        this.timerDuration = 90;
        this.isHostPlayer = false;
    }

    public GameOptionsDTO(long timerDuration, boolean isHostPlayer) {
        this.timerDuration = timerDuration;
        this.isHostPlayer = isHostPlayer;
    }

    public long getTimerDuration() {
        return this.timerDuration;
    }

    public void setTimerDuration(long timerDuration) {
        this.timerDuration = timerDuration;
    }

    public boolean isHostPlayer() {
        return this.isHostPlayer;
    }

    public void setIsHostPlayer(boolean isHostPlayer) {
        this.isHostPlayer = isHostPlayer;
    }

}
