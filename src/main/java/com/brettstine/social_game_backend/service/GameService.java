package com.brettstine.social_game_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.SessionModel;
import com.brettstine.social_game_backend.model.ConversationModel;

import java.lang.IllegalStateException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class GameService {

  private List<PlayerModel> players = new ArrayList<>();

  private static final Logger logger = LoggerFactory.getLogger(GameService.class);

  @Autowired
  SessionService sessionService;

  @Autowired
  StateService stateService;

  @Autowired
  ConversationService conversationService;

  public List<PlayerModel> initialize() {
    Collection<SessionModel> namedSessions = sessionService.getAllNamedSessions();
    if (namedSessions.size() < 3) {
      throw new IllegalStateException("There must be at least 3 names submitted to initialize the game");
    }
    players.clear();
    logger.info("Initalizing game: Cleared player list: {}", players);
    namedSessions.forEach(session -> {
      PlayerModel player = new PlayerModel(session.getSessionId(), session.getName());
      players.add(player);
      logger.info("Initalizing game: Added player: {}", player.getName());
    });
    stateService.updateGameState("asking");
    stateService.updateAppState("game");
    return players;
  }

  public void submitQuestion(String sessionId, String questionString) {
    PlayerModel player = findPlayerBySessionId(sessionId);
    if (player != null) {
      // Create a conversationModel to store question in memory
      ConversationModel questionModel = new ConversationModel(sessionId, questionString);

      // Add conversation Model to the conversation store, then add the uuid reference to the player who submitted it
      conversationService.addConversationModel(questionModel);
      player.setSubmittedQuestionId(questionModel.getConversationId());
    }

    //check if each player holds a reference to a question theyve submitted
    // I.E. "Has everyone submitted a question now, after we just submitted that last one?"
    for (PlayerModel p : players) {
      if (p.getSubmittedQuestionId() == null) {
          // Exit the submitQuestion method if anyone hasn't submitted a question yet
          return;
      }
    }

    //if this is the last player to submit a question -> everyone has submitted a question, update the game
    assignQuestions();
  }

  public void assignQuestions() {
    int numPlayers = players.size();
    if (numPlayers < 3) {
      throw new IllegalArgumentException("Not enough players for the game to work.");
    }
    players.forEach((player) -> {
      if (player.getSubmittedQuestionId() == null) {
        throw new IllegalArgumentException(
          "Session " + player.getSessionId() + "(" + player.getName() + ") did not submit a question."
        );
      }
    });

    for (int i = 0; i < numPlayers; i++) {
      PlayerModel currentPlayer = players.get(i);
      PlayerModel nextPlayer = players.get((i + 1) % numPlayers);
      PlayerModel prevPlayer = players.get((i + numPlayers - 1) % numPlayers);

      // Each player's question will be added to two other players' list of questions to answer
      nextPlayer.addQuestionIdToAnswer(currentPlayer.getSubmittedQuestionId());
      prevPlayer.addQuestionIdToAnswer(currentPlayer.getSubmittedQuestionId());
    }
    stateService.updateGameState("answering");
  }

  public List<ConversationModel> getQuestionsForPlayer(String sessionId) {
    PlayerModel player = findPlayerBySessionId(sessionId);
    if (player != null) {
      List<ConversationModel> questions = new ArrayList<>();
      player.getQuestionIdsToAnswer().forEach((questionId) -> {
        questions.add(conversationService.getConversationModelFromUUID(questionId));
      });
      return questions;
    }
    return null;
  }

  public void submitAnswer(String sessionId, String questionUUID, String answer) {
    PlayerModel player = findPlayerBySessionId(sessionId);
    if (player != null) {
      ConversationModel answerModel = new ConversationModel(sessionId, answer);
      conversationService.addConversationModel(answerModel);
      player.addAnswerIdforQuestionId(questionUUID, answerModel.getConversationId());
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

  public boolean confirmGameState(String expectedGameState) {
    String actualGameState = stateService.getState().get("gameState");
    return (expectedGameState.equals(actualGameState));
  }
}
