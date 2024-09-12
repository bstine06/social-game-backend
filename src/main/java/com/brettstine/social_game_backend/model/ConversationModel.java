package com.brettstine.social_game_backend.model;

public class ConversationModel {
  
  private SessionModel session;
  private String text;

  public ConversationModel(SessionModel session, String text) {
    this.session = session;
    this.text = text;
  }

  public ConversationModel(SessionModel session) {
    this.session = session;
  }

  public SessionModel getSession() {
    return this.session;
  }

  public String getText() {
    return this.text;
  }

  public void setText(String text) {
    this.text = text;
  }

}
