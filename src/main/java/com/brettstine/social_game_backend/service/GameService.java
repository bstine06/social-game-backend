package com.brettstine.social_game_backend.service;

import java.util.List;
import java.time.Instant;

import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.repository.GameRepository;
import com.brettstine.social_game_backend.model.GameState;
import com.brettstine.social_game_backend.utils.GameCodeGenerator;

import org.springframework.transaction.annotation.Transactional;

@Service
public class GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public GameModel createGame() {
        return createGame(90, false);
    }

    public GameModel createGame(long timerDuration, boolean isHostPlayer) {
        
        String gameCode = GameCodeGenerator.generateGameCode();
        int attempts = 0;
        int maxAttempts = 100; // Limit number of tries to find a game code.
        // If this ever becomes a problem, we can scale the game codes to be longer.

        logger.info("Checking for existence of game with randomly generated id: {}", gameCode);
        while (gameRepository.existsById(gameCode)) {
            if (attempts >= maxAttempts) {
                throw new IllegalStateException(
                        "Unable to generate a unique game code after " + maxAttempts + " attempts");
            }
            gameCode = GameCodeGenerator.generateGameCode();
            attempts++;
        }

        GameModel game = new GameModel(gameCode, timerDuration, isHostPlayer);

        logger.info("Storing a new game record with id: {}", gameCode);
        return gameRepository.save(game);
    }

    public void deleteGame(String gameId) {
        logger.info("Delete requested: Checking for existence of game with id: {}", gameId);
        if (!gameRepository.existsById(gameId)) {
            throw new IllegalArgumentException("Game not found with ID: " + gameId);
        }
        logger.info("Deleting game record with id: {}", gameId);
        gameRepository.deleteById(gameId);
    }

    @Transactional
    public GameModel getGameById(String gameId) {
        GameModel game = gameRepository.findById(gameId).orElseThrow(() -> new IllegalArgumentException("Game not found with ID: " + gameId));
        Hibernate.initialize(game.getPlayers());
        return game;
    }

    @Transactional
    public GameModel getGameByHostId(String hostId) {
        GameModel game = gameRepository.findByHostId(hostId)
                .orElseThrow(() -> new IllegalArgumentException("No game found with host ID: " + hostId));
        Hibernate.initialize(game.getPlayers());
        return game;
    }

    public GameModel setGameState(GameModel game, GameState gameState) {
        game.setGameState(gameState);
        // increment the round counter iff we're starting a new round
        if (gameState == GameState.PRE_QUESTION) {
            game.setRoundCount(game.getRoundCount() + 1);
        }
        GameModel savedGame = gameRepository.save(game);
        return savedGame;
    }

    public GameState getGameState(GameModel game) {
        return game.getGameState();
    }

    public boolean confirmGameState(GameModel game, GameState expectedGameState) {
        GameState actualGameState = game.getGameState();
        return (expectedGameState == actualGameState);
    }

    public List<GameModel> getAllGames() {
        List<GameModel> allGames = gameRepository.findAll();
        return allGames;
    }

    public Instant getTimerEnd(GameModel game) {
        return game.getTimerEnd();
    }

    public GameModel resetTimer(GameModel game) {
        return resetTimerWithSeconds(game, game.getTimerDuration());
    }

    public GameModel resetTimerWithSeconds(GameModel game, long seconds) {
            // Update the timer end in the game model
            Instant newTime = Instant.now().plusSeconds(seconds);
            return setTimerEnd(game, newTime);
    }

    public GameModel disableTimer(GameModel game) {
        return setTimerEnd(game, null);
    }

    @Transactional
    private GameModel setTimerEnd(GameModel game, Instant time) {
        GameModel managedGame = gameRepository.findById(game.getGameId())
            .orElseThrow(() -> new IllegalArgumentException("No game found with ID: " + game.getGameId()));
        managedGame.setTimerEnd(time);
        gameRepository.saveAndFlush(managedGame);
        logger.info("Game: {} : Timer set and saved in the database. timerEnd is: {}", game.getGameId(), managedGame.getTimerEnd());
        return managedGame;
    }
}
