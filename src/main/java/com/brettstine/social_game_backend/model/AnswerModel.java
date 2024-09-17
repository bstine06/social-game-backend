package com.brettstine.social_game_backend.model;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "answer")
public class AnswerModel {

  @Column(name = "game_id", nullable = false)
  private String gameId;

  @Column(name = "player_id", nullable = false)
  private String playerId;

  @Id
  @Column(name = "answer_id", nullable = false, unique = true)
  private String answerId;

  @Column(name = "question_id", nullable = false)
  private String questionId;

  @Column(name = "content")
  private String content;

  public AnswerModel() {
  }

  public AnswerModel(String gameId, String playerId, String questionId, String content) {
    this.gameId = gameId;
    this.playerId = playerId;
    this.answerId = UUID.randomUUID().toString();
    this.questionId = questionId;
    this.content = content;
  }

  public String getGameId() {
    return gameId;
  }

  public String getPlayerId() {
    return playerId;
  }

  public String getAnswerId() {
    return answerId;
  }

  public String getQuestionId() {
    return questionId;
  }

  public String getContent() {
    return content;
  }

}
