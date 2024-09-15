package com.brettstine.social_game_backend.service;

import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.repository.PlayerDatabase;
import com.brettstine.social_game_backend.repository.PlayerQuestionDatabase;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerDatabase playerDatabase;
    private final PlayerQuestionDatabase playerQuestionDatabase;
    private final GameService gameService;

    public PlayerService(PlayerDatabase playerDatabase, PlayerQuestionDatabase playerQuestionDatabase, GameService gameService) {
        this.playerDatabase = playerDatabase;
        this.playerQuestionDatabase = playerQuestionDatabase;
        this.gameService = gameService;
    }

    public PlayerModel createPlayer(String gameId, String name) {
        PlayerModel player = new PlayerModel(gameId, name);
        playerDatabase.addPlayer(player);
        return player;
    }

    public PlayerModel getPlayer(String playerId) {
        return playerDatabase.getPlayerById(playerId);
    }

    public void deletePlayer(String playerId) {
        playerDatabase.deletePlayer(playerId);
    }

    public PlayerModel setName(String playerId, String name) {
        PlayerModel player = playerDatabase.setNameOfPlayer(playerId, name);
        return player;
    }

    public List<String> getQuestionIdsToAnswer(String playerId) {
        List<String> questionIds = playerQuestionDatabase.getQuestionsForPlayer(playerId);
        return questionIds;
    }

    public List<PlayerModel> getAllPlayersByGameId(String gameId) {
        try {
            gameService.getGame(gameId);
            return playerDatabase.getAllPlayersByGameId(gameId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Game not found for ID: " + gameId);
        }       
    }
}
