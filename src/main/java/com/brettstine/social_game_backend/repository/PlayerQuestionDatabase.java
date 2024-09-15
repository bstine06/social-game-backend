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
public class PlayerQuestionDatabase {
    
    /* 
        This is not a perfect mock implementation of the database Schema.
        The PlayerQuestion table will have 2 columns per row:
            playerId  |   questionId
        And will use a composite primary key of both columns.
    
        CREATE TABLE PlayerQuestion (
            playerId VARCHAR(255),
            questionId VARCHAR(255),
            PRIMARY KEY (playerId, questionId),
            FOREIGN KEY (playerId) REFERENCES Players(playerId),
            FOREIGN KEY (questionId) REFERENCES Questions(questionId)
        );
    */

    // Map to track questions associated with each player
    private final Map<String, Set<String>> playerToQuestionsStore;

    // Map to track players associated with each question
    private final Map<String, Set<String>> questionToPlayersStore;

    public PlayerQuestionDatabase() {
        playerToQuestionsStore = new ConcurrentHashMap<>();
        questionToPlayersStore = new ConcurrentHashMap<>();
    }

    // Add a mapping between player and question
    public void addPlayerQuestion(String playerId, String questionId) {
        playerToQuestionsStore
            .computeIfAbsent(playerId, k -> new HashSet<>())
            .add(questionId);

        questionToPlayersStore
            .computeIfAbsent(questionId, k -> new HashSet<>())
            .add(playerId);
    }

    // Remove a mapping between player and question
    public void removePlayerQuestion(String playerId, String questionId) {
        Set<String> questions = playerToQuestionsStore.get(playerId);
        if (questions != null) {
            questions.remove(questionId);
            if (questions.isEmpty()) {
                playerToQuestionsStore.remove(playerId);
            }
        } else {
            throw new IllegalArgumentException("No questions found for player ID: " + playerId);
        }

        Set<String> players = questionToPlayersStore.get(questionId);
        if (players != null) {
            players.remove(playerId);
            if (players.isEmpty()) {
                questionToPlayersStore.remove(questionId);
            }
        } else {
            throw new IllegalArgumentException("No players found for question ID: " + questionId);
        }
    }

    // Get all questions for a specific player
    public List<String> getQuestionsForPlayer(String playerId) {
        Set<String> questions = playerToQuestionsStore.get(playerId);
        return questions != null ? new ArrayList<>(questions) : Collections.emptyList();
    }

    // Get all players for a specific question
    public List<String> getPlayersForQuestion(String questionId) {
        Set<String> players = questionToPlayersStore.get(questionId);
        return players != null ? new ArrayList<>(players) : Collections.emptyList();
    }
}
