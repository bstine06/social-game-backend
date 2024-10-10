package com.brettstine.social_game_backend.service;

import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.AnswerModel;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.QuestionModel;

@Service
public class FetchService {

    private final GameService gameService;
    private final PlayerService playerService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final VoteService voteService;

    public FetchService(GameService gameService, PlayerService playerService, QuestionService questionService, AnswerService answerService, VoteService voteService) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.questionService = questionService;
        this.answerService = answerService;
        this.voteService = voteService;
    }

    public GameModel getGameById(String gameId) {
        return gameService.getGameById(gameId);
    }

    public GameModel getGameByHostId(String hostId) {
        return gameService.getGameByHostId(hostId);
    }

    public PlayerModel getPlayerById(String playerId) {
        return playerService.getPlayerById(playerId);
    }

    public QuestionModel getQuestionById(String questionId) {
        return questionService.getQuestionById(questionId);
    }

    public AnswerModel getAnswerById(String answerId) {
        return answerService.getAnswerById(answerId);
    }

    public QuestionModel getCurrentQuestion(GameModel game) {
        return voteService.getCurrentQuestion(game);
    }
}

