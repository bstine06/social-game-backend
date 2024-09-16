package com.brettstine.social_game_backend.model;

import java.util.UUID;

public class AnswerModel {

  private String gameId;
  private String playerId;
  private String answerId;
  private String questionId;
  private String content;

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
