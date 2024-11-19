package com.brettstine.social_game_backend.service;

import java.util.List;
import java.io.IOException;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brettstine.social_game_backend.model.AnswerModel;
import com.brettstine.social_game_backend.model.GameDeletionReason;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.GameState;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.QuestionModel;
import com.brettstine.social_game_backend.websocket.GameStateWebSocketHandler;
import com.brettstine.social_game_backend.websocket.WatchPlayersWebSocketHandler;

@Service
public class GameFlowService {

    private static final Logger logger = LoggerFactory.getLogger(GameFlowService.class);

    private final long TIME_DURATION_DISPLAY_BALLOT = 6;
    private final long TIME_DURATION_VOTE = 15;
    private final long TIME_DURATION_DISPLAY_VOTES = 8;

    private final GameService gameService;
    private final PlayerService playerService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final VoteService voteService;
    private final GameStateWebSocketHandler gameStateWebSocketHandler;
    private final WatchPlayersWebSocketHandler watchPlayersWebSocketHandler;

    public GameFlowService(GameService gameService,
            PlayerService playerService,
            QuestionService questionService,
            AnswerService answerService,
            VoteService voteService,
            GameStateWebSocketHandler gameStateWebSocketHandler,
            WatchPlayersWebSocketHandler watchPlayersWebSocketHandler) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.questionService = questionService;
        this.answerService = answerService;
        this.voteService = voteService;
        this.gameStateWebSocketHandler = gameStateWebSocketHandler;
        this.watchPlayersWebSocketHandler = watchPlayersWebSocketHandler;
    }

    public GameModel createGame() {
        return gameService.createGame();
    }

    // method to ensure no more than 10 players are added to a game
    public void checkMaximumPlayersForGame(GameModel game) {
        List<PlayerModel> players = playerService.getAllPlayersByGame(game);
        if (players.size() >= 10) {
            throw new IllegalStateException("No more than 10 players can be added to a game");
        }
    }

    // New method to ensure at least 3 players exist before advancing from LOBBY to
    // QUESTION
    public void checkMinimumPlayersForQuestionState(GameModel game) {
        List<PlayerModel> players = playerService.getAllPlayersByGame(game);
        if (players.size() < 3) {
            throw new IllegalStateException("At least 3 players are required to start the game");
        }
    }

    public void setPlayerReady(PlayerModel player, boolean ready) {
        playerService.setReady(player.getPlayerId(), ready);
        broadcastPlayersList(player.getGame());
    }

    public void broadcastPlayersList(GameModel game) {
        try {
            watchPlayersWebSocketHandler.broadcastPlayersList(game);
        } catch (IOException e) {
            logger.error("GAME: {} : Error broadcasting players list", game.getGameId(), e);
        }
    }

    public void broadcastGameState(GameModel game) {
        try {
            gameStateWebSocketHandler.broadcastGameState(game);
        } catch (IOException e) {
            logger.error("GAME: {} : Error broadcasting game state", game.getGameId(), e);
        }
    }

    public void closeWebsocketsOnGameDeletion(String gameId, GameDeletionReason reason) {
        gameStateWebSocketHandler.closeConnectionsByGameId(gameId, reason);
        watchPlayersWebSocketHandler.closeConnectionsByGameId(gameId);
    }

    private void setGameState(GameModel game, GameState gameState) {
        playerService.unreadyAllPlayersInGame(game);
        gameService.setGameState(game, gameState);
        broadcastGameState(game);
        broadcastPlayersList(game);
    }

    // method to advance game state only when certain conditions are met
    public void tryAdvanceGameState(GameModel game) {
        logger.info("Game: {} : Attempting to advance game state from: {}", game.getGameId(), game.getGameState());

        if (game.getGameState() == GameState.LOBBY) {
            checkMinimumPlayersForQuestionState(game); // Ensure there are enough players before transitioning
            gameService.resetTimer(game);
            setGameState(game, GameState.QUESTION);
            logger.info("Game: {} : Advanced gameState to: {}", game.getGameId(), game.getGameState());
            return;
        }
        if (game.getGameState() == GameState.QUESTION) {
            // If all players have submitted their questions, advance to the ANSWER phase
            List<PlayerModel> players = playerService.getAllPlayersByGame(game);
            boolean allPlayersSubmittedQuestions = players.stream()
                    .allMatch(player -> questionService.hasSubmittedQuestion(player));
            if (allPlayersSubmittedQuestions) {
                setGameState(game, GameState.ASSIGN);
                logger.info("Game: {} : All players submitted questions, Advanced gameState to: {}", game.getGameId(),
                        game.getGameState());
                assignQuestionsToPlayers(game);
                logger.info("Game: {} : Successfully assigned questions", game.getGameId());
                GameModel gameWithUpdatedTimer = gameService.resetTimer(game);
                setGameState(gameWithUpdatedTimer, GameState.ANSWER);
                logger.info("Game: {} : Advanced gameState to : {}", game.getGameId(), game.getGameState());
                return;
            } else if (game.getTimerEnd().isBefore(Instant.now())) {
                setGameState(game, GameState.ASSIGN);
                logger.info("Game: {} : Timer expired before all players could submit questions, Advanced gameState to: {}", game.getGameId(), game.getGameState());
                submitDefaultQuestions(game);
                logger.info("Game: {} : auto-submitted questions for players who didn't submit", game.getGameId());
                assignQuestionsToPlayers(game);
                logger.info("Game: {} : Successfully assigned questions", game.getGameId());
                GameModel gameWithUpdatedTimer = gameService.resetTimer(game);
                setGameState(gameWithUpdatedTimer, GameState.ANSWER);
                logger.info("Game: {} : Advanced gameState to : {}", game.getGameId(), game.getGameState());
                return;
            }
        }
        if (game.getGameState() == GameState.ANSWER) {
            // If all questions have recieved two answers, advance to the PRESENT phase
            List<QuestionModel> questions = questionService.getAllQuestionsByGame(game);
            boolean allQuestionsHaveTwoAnswers = questions.stream()
                    .allMatch(question -> answerService.hasTwoAnswers(question));
            if (allQuestionsHaveTwoAnswers) {
                setGameState(game, GameState.FIND_BALLOT);
                logger.info("Game: {} : All questions received two answers, advanced gameState to : {}",
                        game.getGameId(), game.getGameState());
                tryAdvanceGameState(game);
            } else if (game.getTimerEnd().isBefore(Instant.now())) {
                setGameState(game, GameState.FIND_BALLOT);
                logger.info("Game: {} : Timer expired before all players could submit answers, advanced gameState to: {}", game.getGameId(), game.getGameState());
                tryAdvanceGameState(game);
            }
            return;
        }
        if (game.getGameState() == GameState.FIND_BALLOT) {
            // Get a question that hasn't had a voting session for its answers yet
            QuestionModel unvotedQuestion = voteService.getOneUnvotedQuestionInGame(game);
            if (unvotedQuestion == null) {
                // This means every ballot has been voted on
                // Advance game to the scoring phase
                setGameState(game, GameState.SCORE);
                return;
            }
            voteService.openVotingForQuestion(unvotedQuestion);
            gameService.resetTimer(game);
            GameModel gameWithUpdatedTimer = gameService.resetTimerWithSeconds(game, TIME_DURATION_DISPLAY_BALLOT);
            setGameState(gameWithUpdatedTimer, GameState.DISPLAY_BALLOT);
            return;
        }
        if (game.getGameState() == GameState.DISPLAY_BALLOT) {
            GameModel gameWithUpdatedTimer = gameService.resetTimerWithSeconds(game, TIME_DURATION_VOTE);
            setGameState(gameWithUpdatedTimer, GameState.VOTE);
            return;
        }
        if (game.getGameState() == GameState.VOTE) {
            // Check is all possible votes have been submitted
            if (voteService.hasQuestionReceivedAllPossibleVotes(voteService.getCurrentQuestion(game))) {
                GameModel gameWithUpdatedTimer = gameService.resetTimerWithSeconds(game, TIME_DURATION_DISPLAY_VOTES);
                setGameState(gameWithUpdatedTimer, GameState.DISPLAY_VOTES);
            } else if (game.getTimerEnd().isBefore(Instant.now())) {
                GameModel gameWithUpdatedTimer = gameService.resetTimerWithSeconds(game, TIME_DURATION_DISPLAY_VOTES);
                setGameState(gameWithUpdatedTimer, GameState.DISPLAY_VOTES);
            }
            return;
        }
        if (game.getGameState() == GameState.DISPLAY_VOTES) {
            // Get the active voting question and close its voting
            QuestionModel currentQuestion = voteService.getCurrentQuestion(game);
            voteService.closeVotingForQuestion(currentQuestion);
            setGameState(game, GameState.FIND_BALLOT);
            tryAdvanceGameState(game);
            return;
        }
    }

    @Transactional
    public void submitDefaultQuestions(GameModel game) {
        playerService.getAllPlayersByGame(game).stream().forEach((PlayerModel p) -> {
            if (!questionService.hasSubmittedQuestion(p)) {
                questionService.submitQuestion(game, p, p.getName() + " was too slow to submit a question because _____");
            }
        });
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
            QuestionModel nextPlayer1Question = questionService.getQuestionByPlayer(nextPlayer1);
            QuestionModel nextPlayer2Question = questionService.getQuestionByPlayer(nextPlayer2);

            // Add both questions to the current player
            questionService.addQuestionForPlayer(game, currentPlayer, nextPlayer1Question);
            questionService.addQuestionForPlayer(game, currentPlayer, nextPlayer2Question);
        }
    }

    public void grantPointForVote(AnswerModel answer) {
        // get player who submitted the answer
        PlayerModel player = answer.getPlayer();
        playerService.incrementScoreForPlayer(player);
        logger.info("Incremented score for player: {}", player.getPlayerId());
    }

    public void terminateGameIfPlayerDeletionIsGameBreaking(GameModel game) {
        List<PlayerModel> players = playerService.getAllPlayersByGame(game);
        if (players.size() < 3 && game.getGameState() != GameState.LOBBY) {
            gameService.deleteGame(game.getGameId());
            closeWebsocketsOnGameDeletion(game.getGameId(), GameDeletionReason.DELETED_BY_INSUFFICIENT_PLAYERS);
        }
    }
}
