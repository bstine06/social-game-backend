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
        if (players.isEmpty()) {
            this.players.add(new PlayerStatusDTO(player, readyStatus, true));
        } else {
            this.players.add(new PlayerStatusDTO(player, readyStatus, false));
        }
    }

    public void addPlayer(PlayerDTO player, boolean readyStatus, String hostPlayerId) {
        this.players.add(new PlayerStatusDTO(player, readyStatus, player.getPlayerId().equals(hostPlayerId)));
    }

    // Nested DTO to represent Player and Ready status
    public static class PlayerStatusDTO {
        private PlayerDTO player;
        private boolean ready;
        private boolean isLeader = false;

        public PlayerStatusDTO(PlayerDTO player, boolean ready) {
            this.player = player;
            this.ready = ready;
        }

        public PlayerStatusDTO(PlayerDTO player, boolean ready, boolean isLeader) {
            this.player = player;
            this.ready = ready;
            this.isLeader = isLeader;
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

        public void setLeader(boolean isLeader) {
            this.isLeader = isLeader;
        }

        public boolean isLeader() {
            return isLeader;
        }
    }
}

