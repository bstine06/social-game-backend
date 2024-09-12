package com.brettstine.social_game_backend.model;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ConversationStoreModel {
  Map<String, ConversationModel> conversationStore = new HashMap<>();

  public ConversationStoreModel() {
  }

  public void addConversationModel(ConversationModel conversationModel) {
    conversationStore.put(conversationModel.getConversationId(), conversationModel);
  }

  public ConversationModel getConversationModelFromUUID(String uuid) {
    return conversationStore.get(uuid);
  }
}
