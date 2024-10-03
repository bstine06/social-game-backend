package com.brettstine.social_game_backend.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "question")
public class QuestionModel {

    @Id
    @Column(name = "question_id", nullable = false, unique = true)
    private String questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    @JsonBackReference
    private GameModel game;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    @JsonBackReference
    private PlayerModel player;

    @Column(name = "content")
    private String content;

    @Column(name = "creation_time", nullable = false)
    @JsonIgnore
    private LocalDateTime creationTime;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<AnswerModel> answers;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<QuestionAssignmentModel> questionAssignments;

    @Column(name = "voting_status")
    private VotingStatus votingStatus;

    public QuestionModel() {
    }

    public QuestionModel(GameModel game, PlayerModel player, String content) {
        this.questionId = UUID.randomUUID().toString();
        this.game = game;
        this.player = player;
        this.content = content;
        this.creationTime = LocalDateTime.now();
        this.votingStatus = VotingStatus.NOT_VOTED;
    }

    public String getQuestionId() {
        return questionId;
    }

    public GameModel getGame() {
        return game;
    }

    @JsonProperty("gameId")
    public String getGameId() {
        return game != null ? game.getGameId() : null;  // Only return the gameId
    }

    public PlayerModel getPlayer() {
        return player;
    }

    @JsonProperty("playerId")
    public String getPlayerId() {
        return player.getPlayerId();
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public List<AnswerModel> getAnswers() {
        return answers;
    }

    public void setGame(GameModel game) {
        this.game = game;
    }

    public void setPlayer(PlayerModel player) {
        this.player = player;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

    public void setAnswers(List<AnswerModel> answers) {
        this.answers = answers;
    }

    public VotingStatus getVotingStatus() {
        return votingStatus;
    }

    public void setVotingStatus(VotingStatus votingStatus) {
        this.votingStatus = votingStatus;
    }
}
