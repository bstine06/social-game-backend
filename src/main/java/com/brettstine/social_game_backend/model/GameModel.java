package com.brettstine.social_game_backend.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "game")
public class GameModel {

    @Id
    @Column(name = "game_id", nullable = false, unique = true)
    private String gameId;

    @Column(name = "game_state", nullable = false)
    private GameState gameState;

    @Column(name = "host_id", nullable = false, unique = true)
    private String hostId;

    @Column(name = "creation_time", nullable = false)
    private Instant creationTime;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<PlayerModel> players;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<QuestionModel> questions;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<AnswerModel> answers;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<QuestionAssignmentModel> questionAssignments;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<PlayerAnswerVoteModel> playerAnswerVotes;

    @Column(name = "timer_end")
    private Instant timerEnd;

    public GameModel() {
    }

    public GameModel(String gameId) {
        this.gameState = GameState.LOBBY;
        this.gameId = gameId;
        this.creationTime = Instant.now();
        this.hostId = UUID.randomUUID().toString();
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

    public String getHostId() {
        return hostId;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public List<PlayerModel> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerModel> players) {
        this.players = players;
    }

    public void addPlayer(PlayerModel player) {
        player.setGame(this);
        this.players.add(player);
    }

    public List<QuestionModel> getQuestions() {
        return questions;
    }

    public List<AnswerModel> getAnswers() {
        return answers;
    }

    public List<QuestionAssignmentModel> getQuestionAssignments() {
        return questionAssignments;
    }

    public void setQuestionAssignments(List<QuestionAssignmentModel> questionAssignments) {
        this.questionAssignments = questionAssignments;
    }

    public List<PlayerAnswerVoteModel> getPlayerAnswerVotes() {
        return playerAnswerVotes;
    }

    public void setPlayerAnswerVotes(List<PlayerAnswerVoteModel> playerAnswerVotes) {
        this.playerAnswerVotes = playerAnswerVotes;
    }

    public Instant getTimerEnd() {
        return timerEnd;
    }

    public void setTimerEnd(Instant time) {
        this.timerEnd = time;
    }
}
