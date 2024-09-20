package com.brettstine.social_game_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
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

    public FetchService(GameService gameService, PlayerService playerService, QuestionService questionService, AnswerService answerService) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.questionService = questionService;
        this.answerService = answerService;
    }

    public GameModel getGameById(String gameId) {
        return gameService.getGameById(gameId);
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
}

