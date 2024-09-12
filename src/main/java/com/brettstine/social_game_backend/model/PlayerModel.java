package com.brettstine.social_game_backend.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.brettstine.social_game_backend.model.ConversationModel;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PlayerModel {
    private SessionModel session;
    private ConversationModel submittedQuestion = new ConversationModel(session);
    private List<ConversationModel> questionsToAnswer = new ArrayList<>();
    private Map<ConversationModel, ConversationModel> answers = new HashMap<>(); // question -> answer

    // Constructor
    public PlayerModel(SessionModel session) {
        this.session = session;
    }

    // Getters and Setters
    public SessionModel getSession() {
        return session;
    }

    @JsonIgnore
    public String getSessionId() {
        return session.getSessionId();
    }

    @JsonIgnore
    public String getName() {
      return session.getName();
    }

    public ConversationModel getSubmittedQuestion() {
        return submittedQuestion;
    }

    public void setSubmittedQuestion(ConversationModel submittedQuestion) {
        this.submittedQuestion = submittedQuestion;
    }

    public List<ConversationModel> getQuestionsToAnswer() {
        return questionsToAnswer;
    }

    public void addQuestionToAnswer(ConversationModel questionToAnswer) {
        this.questionsToAnswer.add(questionToAnswer);
    }

    public Map<ConversationModel, ConversationModel> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<ConversationModel, ConversationModel> answers) {
        this.answers = answers;
    }

    // Additional Methods
    public void addAnswer(ConversationModel question, ConversationModel answer) {
        this.answers.put(question, answer);
    }

    public ConversationModel getAnswer(ConversationModel question) {
        return this.answers.get(question);
    }
}
