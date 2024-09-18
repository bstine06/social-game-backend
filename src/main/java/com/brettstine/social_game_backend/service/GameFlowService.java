package com.brettstine.social_game_backend.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.GameState;
import com.brettstine.social_game_backend.model.PlayerModel;
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

    public void validateGame(String gameId) {
        gameService.getGame(gameId);  // Validate game exists
    }

    public void validatePlayerExists(String playerId) {
        playerService.getPlayer(playerId);
    }

    public void validatePlayer(String playerId, String gameId) {
        PlayerModel player = playerService.getPlayer(playerId);
        if (!player.getGameId().equals(gameId)) {
            throw new IllegalArgumentException("Incorrect gameId for this player. Expected:"+ player.getGameId() + ", Specified:" + gameId);
        }
    }

    public void validateQuestion(String questionId) {
        conversationService.getQuestionById(questionId);
    }

    public void ensureGameState(String gameId, GameState requiredState) {
        if (!gameService.confirmGameState(gameId, requiredState)) {
            throw new IllegalStateException("Invalid game state: Game must be in " + requiredState + " state");
        }
    }

    // New method to ensure at least 3 players exist before advancing from LOBBY to QUESTION
    public void checkMinimumPlayersForQuestionState(String gameId) {
        List<PlayerModel> players = playerService.getAllPlayersByGameId(gameId);
        if (players.size() < 3) {
            throw new IllegalStateException("At least 3 players are required to start the game");
        }
    }

    // method to advance game state only when certain conditions are met
    public void tryAdvanceGameState(String gameId) {
        GameModel game = gameService.getGame(gameId);

        if (game.getGameState() == GameState.LOBBY) {
            checkMinimumPlayersForQuestionState(gameId);  // Ensure there are enough players before transitioning
            gameService.setGameState(gameId, GameState.QUESTION);
            logger.info("Game: {} : Advanced gameState to: {}", gameId, game.getGameState());
        } else 
        if (game.getGameState() == GameState.QUESTION) {
            // If all players have submitted their questions, advance to the ANSWER phase
            List<PlayerModel> players = playerService.getAllPlayersByGameId(gameId);
            boolean allPlayersSubmittedQuestions = players.stream()
                .allMatch(player -> conversationService.hasSubmittedQuestion(player.getPlayerId()));
            if (allPlayersSubmittedQuestions) {
                gameService.setGameState(gameId, GameState.ASSIGN);
                logger.info("Game: {} : All players submitted questions, advanced gameState to: {}", gameId, game.getGameState());
                assignQuestionsToPlayers(gameId);
                logger.info("Game: {} : Successfully assigned questions", gameId);
                gameService.setGameState(gameId, GameState.ANSWER);
                logger.info("Game: {} : Advanced gameState to : {}", gameId, game.getGameState());
            }
        } else 
        if (game.getGameState() == GameState.ANSWER) {
            // If all questions have recieved two answers, advance to the VOTE phase
            List<QuestionModel> questions = conversationService.getAllQuestionsByGameId(gameId);
            boolean allQuestionsHaveTwoAnswers = questions.stream()
                .allMatch(question -> conversationService.hasTwoAnswers(question.getQuestionId()));
            if (allQuestionsHaveTwoAnswers) {
                gameService.setGameState(gameId, GameState.VOTE);
                logger.info("Game: {} : All questions received two answers, advanced gameState to : {}", gameId, game.getGameState());
            }
        }
    }

    public void assignQuestionsToPlayers(String gameId) {
        List<PlayerModel> players = playerService.getAllPlayersByGameId(gameId); // Fetch all players by gameId
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
            QuestionModel nextPlayer1Question = conversationService.getQuestionByPlayerId(nextPlayer1.getPlayerId());
            QuestionModel nextPlayer2Question = conversationService.getQuestionByPlayerId(nextPlayer2.getPlayerId());
    
            // Add both questions to the current player
            conversationService.addQuestionForPlayer(gameId, currentPlayer.getPlayerId(), nextPlayer1Question.getQuestionId());
            conversationService.addQuestionForPlayer(gameId, currentPlayer.getPlayerId(), nextPlayer2Question.getQuestionId());
        }
    }
    
}

