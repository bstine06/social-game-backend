package com.brettstine.social_game_backend.model;

import java.util.UUID;

public class ConversationModel {
  
  private String sessionId;
  private String conversationId;
  private String text;

  public ConversationModel() {
    this.conversationId = UUID.randomUUID().toString();
  }

  public ConversationModel(String sessionId, String text) {
    this.sessionId = sessionId;
    this.conversationId = UUID.randomUUID().toString();
    this.text = text;
  }

  public ConversationModel(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getSessionId() {
    return this.sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public String getConversationId() {
    return this.conversationId;
  }

  public void setConversationId(String conversationId) {
    this.conversationId = conversationId;
  }

  public String getText() {
    return this.text;
  }

  public void setText(String text) {
    this.text = text;
  }

}
