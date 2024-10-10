package com.brettstine.social_game_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.GameState;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.QuestionModel;
import com.brettstine.social_game_backend.model.VotingStatus;

@Service
public class ValidationService {

    private final QuestionService questionService;
    private final AnswerService answerService;

    public ValidationService(QuestionService questionService, AnswerService answerService) {
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
        if (hasPlayerAnsweredQuestion(player, question)) {
            throw new IllegalArgumentException("The player has already submitted an answer to this question");
        }
    }

    public boolean hasPlayerAnsweredQuestion(PlayerModel player, QuestionModel question) {
        return answerService.hasPlayerAnsweredQuestion(player, question);
    }

    public boolean doesPlayerHaveUnansweredQuestions(PlayerModel player) {
        List<String> answeredQuestionIds = player.getAnswers().stream()
                    .map((answer) -> answer.getQuestionId())
                    .collect(Collectors.toList());
        return questionService.getUnansweredQuestionsForPlayer(player, answeredQuestionIds).size() > 0;
    }

    public void ensureGameState(GameModel game, GameState requiredState) {
        if (!game.getGameState().equals(requiredState)) {
            throw new IllegalStateException("Invalid game state: Game must be in " + requiredState + " state");
        }
    }

    public void ensureVotingIsInProgress(QuestionModel question) {
        // ensure that the question is open for voting
        if (question.getVotingStatus() != VotingStatus.IN_PROGRESS) {
            throw new IllegalStateException("This resource is not active for voting");
        }
    }

    public void ensurePlayerIsntActivelyCompeting(PlayerModel player, QuestionModel question) {
        // get players who are actively competing (those who have answers in the running)
        question.getAnswers().stream()
                .map(answer -> answer.getPlayerId())
                .forEach((playerId) -> {
                    if (playerId.equals(player.getPlayerId())) {
                        throw new IllegalStateException("A player who is actively competing may not cast votes");
                    }
                });
    }
}
