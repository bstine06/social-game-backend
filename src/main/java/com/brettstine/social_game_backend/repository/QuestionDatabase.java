package com.brettstine.social_game_backend.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.brettstine.social_game_backend.model.QuestionModel;

@Component
public class QuestionDatabase {

    private Map<String, QuestionModel> questionStore;

    public QuestionDatabase() {
        questionStore = new ConcurrentHashMap<>();
    }

    public void addQuestion(QuestionModel questionModel) {
        questionStore.put(questionModel.getQuestionId(), questionModel);
    }

    public void deleteQuestion(String questionId) {
        if (!questionStore.containsKey(questionId)) {
            throw new IllegalArgumentException("Question not found for ID: " + questionId);
        }
        questionStore.remove(questionId);
    }

    public QuestionModel getQuestionById(String questionId) {
        if (!questionStore.containsKey(questionId)) {
            throw new IllegalArgumentException("Answer not found for ID: " + questionId);
        }
        return questionStore.get(questionId);
    }

    public QuestionModel getQuestionByPlayerId(String playerId) {
        for (QuestionModel question : questionStore.values()) {
            if (question.getPlayerId().equals(playerId)) {
                return question;
            }
        }
        throw new IllegalArgumentException("Question not found with playerId: " + playerId);
    }

    public List<QuestionModel> getAllQuestions() {
        List<QuestionModel> questions = new ArrayList<>(questionStore.values());
        return questions;
    }
}