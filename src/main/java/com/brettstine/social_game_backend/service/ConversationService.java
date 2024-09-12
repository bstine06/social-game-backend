package com.brettstine.social_game_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.ConversationModel;
import com.brettstine.social_game_backend.model.ConversationStoreModel;
import com.brettstine.social_game_backend.model.StateModel;

@Service
public class ConversationService {
  
  private final ConversationStoreModel conversationStoreModel;

  @Autowired
  public ConversationService(ConversationStoreModel conversationStoreModel) {
    // Use the injected ConversationStoreModel instance
    this.conversationStoreModel = conversationStoreModel;
  }

  public ConversationModel getConversationModelFromUUID(String uuid) {
    // Call the non-static method from the injected instance
    return conversationStoreModel.getConversationModelFromUUID(uuid);
  }

  public void addConversationModel(ConversationModel conversationModel) {
    conversationStoreModel.addConversationModel(conversationModel);
  }

}
