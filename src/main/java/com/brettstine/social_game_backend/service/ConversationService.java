package com.brettstine.social_game_backend.service;

import java.util.List;

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

  public ConversationModel getConversationModelFromId(String Id) {
    // Call the non-static method from the injected instance
    return conversationStoreModel.getConversationModelFromId(Id);
  }

  public void addConversationModel(ConversationModel conversationModel) {
    conversationStoreModel.addConversationModel(conversationModel);
  }

  public void addAnswerToQuestion(String questionId, String answerId) {
    conversationStoreModel.addAnswerToQuestion(questionId, answerId);
  }

  public List<ConversationModel> getAnswersForQuestion(String questionId) {
    return conversationStoreModel.getAnswersForQuestion(questionId);
  }

}
