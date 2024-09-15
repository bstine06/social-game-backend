package com.brettstine.social_game_backend.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class QuestionAnswerDatabase {

    /* 
        This is not a perfect mock implementation of the database Schema.
        The QuestionAnswer table will have 2 columns per row:
            questionId  |   answerId
        And will use a composite primary key of both columns
    
        CREATE TABLE QuestionAnswer (
            questionId VARCHAR(255),
            answerId VARCHAR(255),
            PRIMARY KEY (questionId, answerId),
            FOREIGN KEY (questionId) REFERENCES Questions(questionId),
            FOREIGN KEY (answerId) REFERENCES Answers(answerId)
        );
    */

    private final Map<String, Set<String>> questionAnswerStore;

    public QuestionAnswerDatabase() {
        questionAnswerStore = new ConcurrentHashMap<>();
    }

    public void addQuestionAnswer(String questionId, String answerId) {
        questionAnswerStore
            .computeIfAbsent(questionId, k -> new HashSet<>())
            .add(answerId);
    }

    public void removeQuestionAnswer(String questionId, String answerId) {
        Set<String> answers = questionAnswerStore.get(questionId);
        if (answers != null) {
            answers.remove(answerId);
            if (answers.isEmpty()) {
                questionAnswerStore.remove(questionId);
            }
        } else {
            throw new IllegalArgumentException("No answers found for question ID: " + questionId);
        }
    }

    public List<String> getAnswersForQuestion(String questionId) {
        Set<String> answers = questionAnswerStore.get(questionId);
        return answers != null ? new ArrayList<>(answers) : Collections.emptyList();
    }

    public boolean hasExactlyTwoAnswers(String questionId) {
        Set<String> answers = questionAnswerStore.get(questionId);
        return answers != null && answers.size() == 2;
    }
}

