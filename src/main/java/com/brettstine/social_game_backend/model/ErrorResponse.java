package com.brettstine.social_game_backend.model;

public class ErrorResponse {
  private String errorMessage;

  public ErrorResponse(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
