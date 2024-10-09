package com.brettstine.social_game_backend.dto;

import java.util.List;
import java.util.ArrayList;

public class WatchPlayersDTO {
    
    private List<PlayerStatusDTO> players = new ArrayList<>();

    public WatchPlayersDTO() {
    }

    public List<PlayerStatusDTO> getPlayers() {
        return this.players;
    }

    public void setPlayers(List<PlayerStatusDTO> players) {
        this.players = players;
    }

    public void addPlayer(PlayerDTO player, boolean readyStatus) {
        this.players.add(new PlayerStatusDTO(player, readyStatus));
    }

    // Nested DTO to represent Player and Ready status
    public static class PlayerStatusDTO {
        private PlayerDTO player;
        private boolean ready;

        public PlayerStatusDTO(PlayerDTO player, boolean ready) {
            this.player = player;
            this.ready = ready;
        }

        public PlayerDTO getPlayer() {
            return player;
        }

        public void setPlayer(PlayerDTO player) {
            this.player = player;
        }

        public boolean isReady() {
            return ready;
        }

        public void setReady(boolean ready) {
            this.ready = ready;
        }
    }
}

