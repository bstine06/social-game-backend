package com.brettstine.social_game_backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.GameState;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.QuestionModel;

@Service
public class ValidationService {
    
    private static final Logger logger = LoggerFactory.getLogger(GameFlowService.class);

    private final GameService gameService;
    private final PlayerService playerService;
    private final QuestionService questionService;
    private final AnswerService answerService;

    public ValidationService(GameService gameService, PlayerService playerService, QuestionService questionService, AnswerService answerService) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.questionService = questionService;
        this.answerService = answerService;
    }

    public void validatePlayer(PlayerModel player, GameModel game) {
        if (!player.getGame().getGameId().equals(game.getGameId())) {
            throw new IllegalArgumentException("Incorrect gameId for this player. Expected:"+ player.getGame().getGameId() + ", Specified:" + game.getGameId());
        }
    }

    public void validateQuestion(String questionId) {
        questionService.getQuestionById(questionId);
    }

    public void validateQuestionCanReceiveAnswer(QuestionModel question) {
        if (answerService.hasTwoAnswers(question)) {
            throw new IllegalStateException("Each question can only recieve two answers");
        }
    }

    public void validatePlayerCanSubmitQuestion(PlayerModel player) {
        if (questionService.hasSubmittedQuestion(player)) {
            throw new IllegalStateException("Only one question per player can be submitted");
        }
    }

    public void validatePlayerCanAnswerThisQuestion(PlayerModel player, QuestionModel question) {
        validatePlayerWasAssignedThisQuestion(player, question);
        validatePlayerHasNotAlreadyAnsweredQuestion(player, question);
    }

    public void validatePlayerWasAssignedThisQuestion(PlayerModel player, QuestionModel question) {
        if (!questionService.isQuestionAssignedToPlayer(player, question)) {
            throw new IllegalArgumentException("The player was not assigned the specified question.");
        }
    }

    public void validatePlayerHasNotAlreadyAnsweredQuestion(PlayerModel player, QuestionModel question) {
        if (answerService.hasPlayerAnsweredQuestion(player, question)) {
            throw new IllegalArgumentException("The player has already submitted an answer to this question");
        }
    }

    public void ensureGameState(GameModel game, GameState requiredState) {
        if (!game.getGameState().equals(requiredState)) {
            throw new IllegalStateException("Invalid game state: Game must be in " + requiredState + " state");
        }
    }
}
