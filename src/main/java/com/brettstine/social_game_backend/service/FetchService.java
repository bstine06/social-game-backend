package com.brettstine.social_game_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.AnswerModel;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.QuestionModel;
import com.brettstine.social_game_backend.model.SessionModel;

@Service
public class FetchService {

    private final GameService gameService;
    private final PlayerService playerService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final VoteService voteService;
    private final SessionService sessionService;

    public FetchService(GameService gameService, PlayerService playerService, QuestionService questionService, AnswerService answerService, VoteService voteService, SessionService sessionService) {
        this.gameService = gameService;
        this.playerService = playerService;
        this.questionService = questionService;
        this.answerService = answerService;
        this.voteService = voteService;
        this.sessionService = sessionService;
    }

    public GameModel getGameById(String gameId) {
        return gameService.getGameById(gameId);
    }

    public PlayerModel getPlayerById(String playerId) {
        return playerService.getPlayerById(playerId);
    }

    public PlayerModel getPlayerBySessionId(String sessionId) {
        return playerService.getPlayerBySessionId(sessionId);
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

    public SessionModel getSessionById(String sessionId) {
        SessionModel session = sessionService.getSessionById(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Session not found with Id " + sessionId);
        }
        return session;
    }
}

