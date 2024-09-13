package com.brettstine.social_game_backend.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ConversationStoreModel {

    // Stores all conversation models (questions and answers) by Id
    private Map<String, ConversationModel> conversationStore = new HashMap<>();

    // Stores the mapping between question Ids and their answers (as Ids)
    private Map<String, List<String>> questionAnswerMap = new HashMap<>();

    public ConversationStoreModel() {
    }

    public void addConversationModel(ConversationModel conversationModel) {
        conversationStore.put(conversationModel.getConversationId(), conversationModel);
    }

    public ConversationModel getConversationModelFromId(String id) {
        return conversationStore.get(id);
    }

    // Method to associate answers with a given question
    public void addAnswerToQuestion(String questionId, String answerId) {
        questionAnswerMap.computeIfAbsent(questionId, k -> new ArrayList<>()).add(answerId);
    }

    // Get answers for a given question by Id
    public List<ConversationModel> getAnswersForQuestion(String questionId) {
        List<String> answerIds = questionAnswerMap.getOrDefault(questionId, Collections.emptyList());
        List<ConversationModel> answers = new ArrayList<>();
        for (String Id : answerIds) {
            ConversationModel answer = conversationStore.get(Id);
            if (answer != null) {
                answers.add(answer);
            }
        }
        return answers;
    }

    // Validate if a question has exactly 2 answers
    public boolean hasTwoAnswers(String questionId) {
        return questionAnswerMap.containsKey(questionId) && questionAnswerMap.get(questionId).size() == 2;
    }
}

