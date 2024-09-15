package com.brettstine.social_game_backend.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.brettstine.social_game_backend.model.AnswerModel;

@Component
public class AnswerDatabase {

  private Map<String, AnswerModel> answerStore;

  public AnswerDatabase() {
    answerStore = new ConcurrentHashMap<>();
  }

  public void addAnswer(AnswerModel answerModel) {
    answerStore.put(answerModel.getAnswerId(), answerModel);
  }

  public void deleteAnswer(String answerId) {
    if (!answerStore.containsKey(answerId)) {
      throw new IllegalArgumentException("Answer not found for ID: " + answerId);
    }
    answerStore.remove(answerId);
  }

  public AnswerModel getAnswerById(String answerId) {
    if (!answerStore.containsKey(answerId)) {
      throw new IllegalArgumentException("Answer not found for ID: " + answerId);
    }
    return answerStore.get(answerId);
  }
}
