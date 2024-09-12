package com.brettstine.social_game_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
// GameService.java
import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.SessionModel;
import com.brettstine.social_game_backend.model.ConversationModel;

import java.lang.IllegalStateException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
public class GameService {

  private List<PlayerModel> players = new ArrayList<>();

  @Autowired
  SessionService sessionService;

  @Autowired
  StateService stateService;

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
    if (players.size() >= 3) {
      stateService.updateGameState("asking");
      stateService.updateAppState("game");
      return players;
    }
    throw new IllegalStateException("There must be at least 3 players registered.");
    
  }

  public void submitQuestion(String sessionId, String question) {
    PlayerModel player = findPlayerBySessionId(sessionId);
    if (player != null) {
      SessionModel session = sessionService.getSession(sessionId);
      ConversationModel conversationModel = new ConversationModel(session, question);
      player.setSubmittedQuestion(conversationModel);
    }

    //check if each player has submitted a question
    for (PlayerModel p : players) {
      if (p.getSubmittedQuestion().getText() == null) {
          // Exit the submitQuestion method if anyone hasn't submitted a question yet
          return;
      }
    }

    //if this is the last player to submit a question -> everyone has submitted a question, update the game
    assignQuestions();
    stateService.updateGameState("assigning");
  }

  public void assignQuestions() {
    int numPlayers = players.size();
    if (numPlayers < 3) {
      throw new IllegalArgumentException("Not enough players to start the game.");
    }
    players.forEach((player) -> {
      if (player.getSubmittedQuestion().getText().isEmpty()) {
        throw new IllegalArgumentException(player.getName() + " did not submit a question.");
      }
    });

    for (int i = 0; i < numPlayers; i++) {
      PlayerModel currentPlayer = players.get(i);
      PlayerModel nextPlayer = players.get((i + 1) % numPlayers);
      PlayerModel prevPlayer = players.get((i + numPlayers - 1) % numPlayers);

      // Each player is asked two questions
      nextPlayer.addQuestionToAnswer(currentPlayer.getSubmittedQuestion());
      prevPlayer.addQuestionToAnswer(currentPlayer.getSubmittedQuestion());
    }
    stateService.updateGameState("answering");
  }

  public List<ConversationModel> getQuestionsForPlayer(String sessionId) {
    PlayerModel player = findPlayerBySessionId(sessionId);
    return player != null ? player.getQuestionsToAnswer() : null;
  }

  public void submitAnswer(String sessionId, String question, String answer) {
    PlayerModel player = findPlayerBySessionId(sessionId);
    if (player != null) {
      SessionModel session = sessionService.getSession(sessionId);
      ConversationModel questionConversationModel = new ConversationModel(session, question);
      ConversationModel answerConversationModel = new ConversationModel(session, answer);
      player.addAnswer(questionConversationModel, answerConversationModel);
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
