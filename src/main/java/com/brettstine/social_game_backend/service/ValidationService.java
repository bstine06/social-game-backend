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

    private final int maxNameInputLength = 15;
    private final int maxConversationInputLength = 80;

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final PlayerService playerService;

    public ValidationService(QuestionService questionService, AnswerService answerService, PlayerService playerService) {
        this.questionService = questionService;
        this.answerService = answerService;
        this.playerService = playerService;
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

    public void validateNameLength(String nameInput) {
        if (nameInput.length() > maxNameInputLength) {
            throw new IllegalArgumentException("Name input length may not exceed " + maxNameInputLength + " characters");
        } else if (nameInput.length() <= 0) {
            throw new IllegalArgumentException("Name input must be longer than 0 characters");
        }
    }

    public void validateConversationInputLength(String conversationInput) {
        if (conversationInput.length() > maxConversationInputLength) {
            throw new IllegalArgumentException("Conversation input length may not exceed " + maxConversationInputLength + " characters");
        } else if (conversationInput.length() <= 0) {
            throw new IllegalArgumentException("Conversation input must be longer than 0 characters");
        }
    }

    public void ensureGameCanAcceptPlayer(GameModel game, String optionalHostId) {
        // return early if theres still room for anyone to join
        if (game.getPlayers().size() <= 6) return;
        // check the game isn't already full. It the host is a player, make sure the host is in before adding the last player
        if (game.getPlayers().size() >= 8) {
            throw new IllegalStateException("This game already has the maximum amount of players");
        }
        // if the host is meant to be a player in this game...
        if (game.isHostPlayer()) {
            // if the player we're checking can join is NOT the host...
            if (optionalHostId == null) {
                // and the game only has one open slot left...
                if (game.getPlayers().size() == 7) {
                    // check if the host is a player already
                    // this will throw an error if the host is not a player yet -> player cannot join
                    try {
                        playerService.getPlayerById(game.getHostId());  // Check if the host exists in the system
                    } catch (IllegalArgumentException e) {
                        throw new IllegalStateException("The host has not joined as a player yet and there is only one slot left. Another player cannot join.");
                    }
                }
            }
        }
    }
}
