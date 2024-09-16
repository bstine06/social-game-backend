package com.brettstine.social_game_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.GameState;
import com.brettstine.social_game_backend.model.PlayerModel;

@Service
public class GameFlowService {

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

    public void validatePlayer(String playerId) {
        playerService.getPlayer(playerId);
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
        } else 
        if (game.getGameState() == GameState.QUESTION) {
            // If all players have submitted their questions, advance to the ANSWER phase
            List<PlayerModel> players = playerService.getAllPlayersByGameId(gameId);
            boolean allPlayersSubmittedQuestions = players.stream()
                .allMatch(player -> conversationService.hasSubmittedQuestion(player.getPlayerId()));
            if (allPlayersSubmittedQuestions) {
                gameService.setGameState(gameId, GameState.ANSWER);
            }
        }
    }
}

