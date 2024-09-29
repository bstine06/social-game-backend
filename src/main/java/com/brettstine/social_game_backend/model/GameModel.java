package com.brettstine.social_game_backend.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "game")
public class GameModel {

    @Id
    @Column(name = "game_id", nullable = false, unique = true)
    private String gameId;

    @Column(name = "game_state", nullable = false)
    private GameState gameState;

    // Reference to actual SessionModel for the host
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "host_session_id", nullable = true)
    private SessionModel hostSession;

    @Column(name = "creation_time", nullable = false)
    @JsonIgnore
    private LocalDateTime creationTime;

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
    private LocalDateTime timerEnd;

    public GameModel() {
    }

    public GameModel(String gameId, SessionModel hostSession) {
        this.gameState = GameState.LOBBY;
        this.gameId = gameId;
        this.creationTime = LocalDateTime.now();
        this.hostSession = hostSession;
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

    public SessionModel getHostSession() {
        return hostSession;
    }

    public void setHostSession(SessionModel hostSession) {
        this.hostSession = hostSession;
    }

    public LocalDateTime getCreationTime() {
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

    public LocalDateTime getTimerEnd() {
        return timerEnd;
    }

    public void setTimerEnd(LocalDateTime time) {
        this.timerEnd = time;
    }
}
