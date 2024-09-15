package com.brettstine.social_game_backend.model;

import java.util.UUID;

public class AnswerModel {

  private String answerId;
  private String playerId;
  private String content;
  private String questionId;

  public AnswerModel(String playerId, String questionId, String content) {
    this.answerId = UUID.randomUUID().toString();
    this.playerId = playerId;
    this.questionId = questionId;
    this.content = content;
  }

  public String getAnswerId() {
    return answerId;
  }

  public String getPlayerId() {
    return playerId;
  }

  public String getQuestionId() {
    return questionId;
  }

  public String getContent() {
    return content;
  }

}
