package com.brettstine.social_game_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
// GameService.java
import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.SessionModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class GameService {

  private List<PlayerModel> players = new ArrayList<>();

  @Autowired
  SessionService sessionService;

  public List<PlayerModel> initialize() {
    Collection<SessionModel> sessions = sessionService.getAllSessions();
    sessions.forEach(session -> {
      if (session.getName() != null) {
        String sessionId = session.getSessionId();

        // Check if the player list already contains a player with this sessionId
        boolean playerExists = players.stream()
            .anyMatch(player -> player.getSessionId().equals(sessionId));

        // Add player if it doesn't already exist
        if (!playerExists) {
          PlayerModel player = new PlayerModel(session);
          players.add(player);
        }
      }
    });
    return players;
  }

  public void submitQuestions(String sessionId, List<String> questions) {
    PlayerModel player = findPlayerBySessionId(sessionId);
    if (player != null) {
      player.setSubmittedQuestions(questions);
    }
  }

  public void assignQuestions() {
    int numPlayers = players.size();
    if (numPlayers < 3) {
      throw new IllegalArgumentException("Not enough players to start the game.");
    }
    players.forEach((player) -> {
      if (player.getSubmittedQuestions().size() < 1) {
        throw new IllegalArgumentException(player.getName() + " did not submit questions.");
      }
    });

    for (int i = 0; i < numPlayers; i++) {
      PlayerModel currentPlayer = players.get(i);
      PlayerModel nextPlayer = players.get((i + 1) % numPlayers);
      PlayerModel prevPlayer = players.get((i + numPlayers - 1) % numPlayers);

      // Each player is asked two questions
      nextPlayer.getQuestionsToAnswer().add(currentPlayer.getOneSubmittedQuestion());
      prevPlayer.getQuestionsToAnswer().add(currentPlayer.getOneSubmittedQuestion());
    }
  }

  public List<String> getQuestionsForPlayer(String sessionId) {
    PlayerModel player = findPlayerBySessionId(sessionId);
    return player != null ? player.getQuestionsToAnswer() : null;
  }

  public void submitAnswer(String sessionId, String question, String answer) {
    PlayerModel player = findPlayerBySessionId(sessionId);
    if (player != null) {
      player.addAnswer(question, answer);
    }
  }

  private PlayerModel findPlayerBySessionId(String sessionId) {
    return players.stream()
        .filter(player -> player.getSessionId().equals(sessionId))
        .findFirst()
        .orElse(null);
  }

  public List<PlayerModel> getPlayers() {
    return players;
  }
}
