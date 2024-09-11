package com.brettstine.social_game_backend.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PlayerModel {
    private SessionModel session;
    private List<String> submittedQuestions = new ArrayList<>();
    private List<String> questionsToAnswer = new ArrayList<>();
    private Map<String, String> answers = new HashMap<>(); // question -> answer

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

    public List<String> getSubmittedQuestions() {
        return submittedQuestions;
    }

    @JsonIgnore
    public String getOneSubmittedQuestion() {
      if (submittedQuestions.size() < 1) {
        throw new IllegalArgumentException("No questions to get.");
      }
      String question = submittedQuestions.get(0);
      submittedQuestions.remove(0);
      return question;
    }

    public void setSubmittedQuestions(List<String> submittedQuestions) {
        this.submittedQuestions.clear();
        this.submittedQuestions = new ArrayList<String>(submittedQuestions);
    }

    public List<String> getQuestionsToAnswer() {
        return questionsToAnswer;
    }

    public void setQuestionsToAnswer(List<String> questionsToAnswer) {
        this.questionsToAnswer = questionsToAnswer;
    }

    public Map<String, String> getAnswers() {
        return answers;
    }

    public void setAnswers(Map<String, String> answers) {
        this.answers = answers;
    }

    // Additional Methods
    public void addAnswer(String question, String answer) {
        this.answers.put(question, answer);
    }

    public String getAnswer(String question) {
        return this.answers.get(question);
    }
}
