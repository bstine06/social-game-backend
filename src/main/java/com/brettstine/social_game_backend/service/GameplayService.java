package com.brettstine.social_game_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.GameplayModel;
import com.brettstine.social_game_backend.model.ConversationModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.SessionModel;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GameplayService {

    private final GameplayModel gameplayModel = new GameplayModel();
    private static final Logger logger = LoggerFactory.getLogger(GameplayService.class);

    @Autowired
    private SessionService sessionService;

    @Autowired
    private StateService stateService;

    @Autowired
    private ConversationService conversationService;

    public List<PlayerModel> initialize() {
        Collection<SessionModel> namedSessions = sessionService.getAllNamedSessions();
        if (namedSessions.size() < 3) {
            throw new IllegalStateException("There must be at least 3 names submitted to initialize the game");
        }
        gameplayModel.getPlayers().clear();
        logger.info("Initializing game: Cleared player list: {}", gameplayModel.getPlayers());
        namedSessions.forEach(session -> {
            PlayerModel player = new PlayerModel(session.getSessionId(), session.getName());
            gameplayModel.addPlayer(player);
            logger.info("Initializing game: Added player: {}", player.getName());
        });
        stateService.updateGameState("asking");
        stateService.updateAppState("game");
        return gameplayModel.getPlayers();
    }

    public void submitQuestion(String sessionId, String questionString) {
        Optional<PlayerModel> playerOpt = gameplayModel.findPlayerBySessionId(sessionId);
        if (playerOpt.isPresent()) {
            PlayerModel player = playerOpt.get();
            // Create a ConversationModel to store the question
            ConversationModel questionModel = new ConversationModel(sessionId, questionString);

            // Add ConversationModel to the conversation store and update the player
            conversationService.addConversationModel(questionModel);
            player.setSubmittedQuestionId(questionModel.getConversationId());
        }

        // Check if all players have submitted a question
        if (gameplayModel.allPlayersHaveSubmittedQuestions()) {
            // All players have submitted questions, proceed to assign questions
            gameplayModel.assignQuestionsToPlayers();
            stateService.updateGameState("answering");
        }
    }

    public List<ConversationModel> getQuestionsForPlayer(String sessionId) {
        Optional<PlayerModel> playerOpt = gameplayModel.findPlayerBySessionId(sessionId);
        if (playerOpt.isPresent()) {
            PlayerModel player = playerOpt.get();
            List<ConversationModel> questions = player.getQuestionIdsToAnswer().stream()
                    .map(conversationService::getConversationModelFromId)
                    .toList();
            return questions;
        }
        return null;
    }

    public void submitAnswer(String sessionId, String questionId, String answer) {
        Optional<PlayerModel> playerOpt = gameplayModel.findPlayerBySessionId(sessionId);
        if (playerOpt.isPresent()) {
            PlayerModel player = playerOpt.get();
            ConversationModel answerModel = new ConversationModel(sessionId, answer);
            conversationService.addConversationModel(answerModel);
            String answerId = answerModel.getConversationId();
            conversationService.addAnswerToQuestion(questionId, answerId);
            player.addAnswerIdForQuestionId(questionId, answerId);
        }
    }

    public List<PlayerModel> getPlayers() {
        return gameplayModel.getPlayers();
    }

    public boolean confirmGameState(String expectedGameState) {
        String actualGameState = stateService.getState().get("gameState");
        return expectedGameState.equals(actualGameState);
    }
}
