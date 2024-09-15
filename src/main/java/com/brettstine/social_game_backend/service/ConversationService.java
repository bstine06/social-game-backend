package com.brettstine.social_game_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.AnswerModel;
import com.brettstine.social_game_backend.model.GameState;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.QuestionModel;
import com.brettstine.social_game_backend.repository.AnswerDatabase;
import com.brettstine.social_game_backend.repository.QuestionAnswerDatabase;
import com.brettstine.social_game_backend.repository.QuestionDatabase;

@Service
public class ConversationService {

    private final QuestionDatabase questionDatabase;
    private final AnswerDatabase answerDatabase;
    private final QuestionAnswerDatabase questionAnswerDatabase;
    private final GameService gameService;
    private final PlayerService playerService;

    public ConversationService(AnswerDatabase answerDatabase,
            QuestionDatabase questionDatabase,
            QuestionAnswerDatabase questionAnswerDatabase,
            GameService gameService,
            PlayerService playerService) {
        this.questionDatabase = questionDatabase;
        this.answerDatabase = answerDatabase;
        this.questionAnswerDatabase = questionAnswerDatabase;
        this.gameService = gameService;
        this.playerService = playerService;
    }

    public void submitQuestion(String gameId, String playerId, String content) {
        if (!gameService.confirmGameState(gameId, GameState.QUESTION)) {
            throw new IllegalStateException("gameState must be 'QUESTION' to submit a question");
        }
        QuestionModel question = new QuestionModel(playerId, content);
        questionDatabase.addQuestion(question);
    }

    public void submitAnswer(String gameId, String playerId, String questionId, String content) {
        if (!gameService.confirmGameState(gameId, GameState.ANSWER)) {
            throw new IllegalStateException("gameState must be 'ANSWER' to submit an answer");
        }
        AnswerModel answer = new AnswerModel(playerId, questionId, content);
        answerDatabase.addAnswer(answer);
        questionAnswerDatabase.addQuestionAnswer(questionId, answer.getAnswerId());
    }

    public QuestionModel getQuestionById(String questionId) {
        return questionDatabase.getQuestionById(questionId);
    }

    public AnswerModel getAnswerById(String answerId) {
        return answerDatabase.getAnswerById(answerId);
    }

    public List<QuestionModel> getQuestionsForPlayer(String playerId) {
        List<String> questionIds = playerService.getQuestionIdsToAnswer(playerId);
        List<QuestionModel> questions = questionIds.stream()
                .map(questionId -> getQuestionById(questionId))
                .collect(Collectors.toList());
        return questions;
    }

    public List<AnswerModel> getAnswersForQuestion(String questionId) {
        List<String> answerIds = questionAnswerDatabase.getAnswersForQuestion(questionId);
        List<AnswerModel> answers = answerIds.stream()
                .map(answerId -> getAnswerById(answerId))
                .collect(Collectors.toList());
        return answers;
    }

    // Assign questions to players in a circular manner
    public void assignQuestionsToPlayers(String gameId) {
        List<PlayerModel> players = playerService.getAllPlayersByGameId(gameId); // Fetch all players by gameId
        int numPlayers = players.size();

        if (numPlayers < 2) {
            throw new IllegalStateException("Not enough players to assign questions.");
        }

        for (int i = 0; i < numPlayers; i++) {
            PlayerModel currentPlayer = players.get(i);
            PlayerModel nextPlayer = players.get((i + 1) % numPlayers);
            PlayerModel prevPlayer = players.get((i + numPlayers - 1) % numPlayers);

            String currentPlayerQuestionId = currentPlayer.getSubmittedQuestionId();

            // Add the current player's question to the list of the next and previous
            // players
            nextPlayer.addQuestionIdToAnswer(currentPlayerQuestionId);
            prevPlayer.addQuestionIdToAnswer(currentPlayerQuestionId);
        }
    }

}
