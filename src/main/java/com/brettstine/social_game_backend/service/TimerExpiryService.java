package com.brettstine.social_game_backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.brettstine.social_game_backend.model.GameModel;

import java.time.Instant;
import java.util.List;

@Component
public class TimerExpiryService {

    private static final Logger logger = LoggerFactory.getLogger(GameFlowService.class);

    private final GameService gameService;
    private final GameFlowService gameFlowService;

    public TimerExpiryService(GameService gameService, GameFlowService gameFlowService) {
        this.gameService = gameService;
        this.gameFlowService = gameFlowService;
    }

    // This method will run every second to check if any game's timer has expired
    @Scheduled(fixedRate = 1000)  // Run every second
    public void checkForExpiredTimers() {
        List<GameModel> games = gameService.getAllGames();  // Fetch all games
        for (GameModel game : games) {
            Instant timerEnd = game.getTimerEnd();
            if (timerEnd != null && timerEnd.isBefore(Instant.now())) {
                // If the timer has expired, update the game state
                logger.info("Game: {} : timer has expired, prompting advance game state...", game.getGameId());
                gameFlowService.tryAdvanceGameState(game);
            }
        }
    }
}
