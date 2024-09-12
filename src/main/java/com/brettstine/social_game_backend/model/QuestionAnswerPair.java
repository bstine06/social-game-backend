package com.brettstine.social_game_backend.model;

public class QuestionAnswerPair {
  private String questionId;
  private String answerId;

  public QuestionAnswerPair(String questionId, String answerId) {
      this.questionId = questionId;
      this.answerId = answerId;
  }

  // Getters and setters
  public String getQuestionId() {
      return questionId;
  }

  public void setQuestionId(String questionId) {
      this.questionId = questionId;
  }

  public String getAnswerId() {
      return answerId;
  }

  public void setAnswerId(String answerId) {
      this.answerId = answerId;
  }
}
