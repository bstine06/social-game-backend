package com.brettstine.social_game_backend.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerModel {
    private String sessionId;
    private String name;
    private String submittedQuestionId = null;
    private List<String> questionIdsToAnswer = new ArrayList<>();
    private List<QuestionAnswerPair> answerIdsForQuestionIds = new ArrayList<>();

    // Constructor
    public PlayerModel(String sessionId, String name) {
        this.sessionId = sessionId;
        this.name = name;
    }

    // Getters and Setters
    public String getSessionId() {
        return sessionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubmittedQuestionId() {
        return submittedQuestionId;
    }

    public void setSubmittedQuestionId(String questionId) {
        this.submittedQuestionId = questionId;
    }

    public List<String> getQuestionIdsToAnswer() {
        return questionIdsToAnswer;
    }

    public void addQuestionIdToAnswer(String questionId) {
        this.questionIdsToAnswer.add(questionId);
    }

    public List<QuestionAnswerPair> getAnswerIdsForQuestionIds() {
        return answerIdsForQuestionIds;
    }

    public void setAnswerIdsForQuestionIds(List<QuestionAnswerPair> answerIdsForQuestionIds) {
        this.answerIdsForQuestionIds = answerIdsForQuestionIds;
    }

    // Additional Methods
    public void addAnswerIdForQuestionId(String questionId, String answerId) {
        this.answerIdsForQuestionIds.add(new QuestionAnswerPair(questionId, answerId));
    }

    // Method to find the associated answer for a given question
    public String getAnswerIdForQuestionId(String questionId) {
        for (QuestionAnswerPair pair : answerIdsForQuestionIds) {
            if (pair.getQuestionId().equals(questionId)) {
                return pair.getAnswerId();
            }
        }
        return null; // or throw new NoSuchElementException("No answer found for the given question");
    }
}
