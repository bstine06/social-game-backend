package com.brettstine.social_game_backend.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.AnswerModel;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.GameState;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.QuestionAssignmentModel;
import com.brettstine.social_game_backend.model.QuestionModel;

@Service
public class GameFlowService {

    private static final Logger logger = LoggerFactory.getLogger(GameFlowService.class);

    private final GameService gameService;
    private final PlayerService playerService;
    private final ConversationService conversationService;

    public GameFlowService(GameService gameService, PlayerService playerService, ConversationService conversationService) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.conversationService = conversationService;
    }

    // New method to ensure at least 3 players exist before advancing from LOBBY to QUESTION
    public void checkMinimumPlayersForQuestionState(GameModel game) {
        List<PlayerModel> players = playerService.getAllPlayersByGame(game);
        if (players.size() < 3) {
            throw new IllegalStateException("At least 3 players are required to start the game");
        }
    }

    // method to advance game state only when certain conditions are met
    public void tryAdvanceGameState(GameModel game) {

        if (game.getGameState() == GameState.LOBBY) {
            checkMinimumPlayersForQuestionState(game);  // Ensure there are enough players before transitioning
            gameService.setGameState(game, GameState.QUESTION);
            logger.info("Game: {} : Advanced gameState to: {}", game.getGameId(), game.getGameState());
        } else 
        if (game.getGameState() == GameState.QUESTION) {
            // If all players have submitted their questions, advance to the ANSWER phase
            List<PlayerModel> players = playerService.getAllPlayersByGame(game);
            boolean allPlayersSubmittedQuestions = players.stream()
                .allMatch(player -> conversationService.hasSubmittedQuestion(player));
            if (allPlayersSubmittedQuestions) {
                gameService.setGameState(game, GameState.ASSIGN);
                logger.info("Game: {} : All players submitted questions, advanced gameState to: {}", game.getGameId(), game.getGameState());
                assignQuestionsToPlayers(game);
                logger.info("Game: {} : Successfully assigned questions", game.getGameId());
                gameService.setGameState(game, GameState.ANSWER);
                logger.info("Game: {} : Advanced gameState to : {}", game.getGameId(), game.getGameState());
            }
        } else 
        if (game.getGameState() == GameState.ANSWER) {
            // If all questions have recieved two answers, advance to the VOTE phase
            List<QuestionModel> questions = conversationService.getAllQuestionsByGame(game);
            boolean allQuestionsHaveTwoAnswers = questions.stream()
                .allMatch(question -> conversationService.hasTwoAnswers(question));
            if (allQuestionsHaveTwoAnswers) {
                gameService.setGameState(game, GameState.VOTE);
                logger.info("Game: {} : All questions received two answers, advanced gameState to : {}", game.getGameId(), game.getGameState());
            }
        }
    }

    public void assignQuestionsToPlayers(GameModel game) {
        List<PlayerModel> players = playerService.getAllPlayersByGame(game);
        int numPlayers = players.size();
    
        if (numPlayers < 3) {
            throw new IllegalStateException("Not enough players to assign questions uniquely.");
        }
    
        // Loop through each player and assign them a unique set of 2 questions
        for (int i = 0; i < numPlayers; i++) {
            PlayerModel currentPlayer = players.get(i);
    
            // Assign the next 2 players' questions to the current player
            PlayerModel nextPlayer1 = players.get((i + 1) % numPlayers); // Next player in the list
            PlayerModel nextPlayer2 = players.get((i + 2) % numPlayers); // Player after the next
            QuestionModel nextPlayer1Question = conversationService.getQuestionByPlayer(nextPlayer1);
            QuestionModel nextPlayer2Question = conversationService.getQuestionByPlayer(nextPlayer2);
    
            // Add both questions to the current player
            conversationService.addQuestionForPlayer(game, currentPlayer, nextPlayer1Question);
            conversationService.addQuestionForPlayer(game, currentPlayer, nextPlayer2Question);
        }
    }
    
}

