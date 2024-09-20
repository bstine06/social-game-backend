package com.brettstine.social_game_backend.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brettstine.social_game_backend.controller.ConversationController;
import com.brettstine.social_game_backend.repository.AnswerRepository;
import com.brettstine.social_game_backend.repository.GameRepository;
import com.brettstine.social_game_backend.repository.PlayerAnswerVoteRepository;
import com.brettstine.social_game_backend.repository.PlayerRepository;
import com.brettstine.social_game_backend.repository.QuestionAssignmentRepository;
import com.brettstine.social_game_backend.repository.QuestionRepository;
import com.brettstine.social_game_backend.model.GameModel;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CleanupService {

    private static final Logger logger = LoggerFactory.getLogger(CleanupService.class);

    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuestionAssignmentRepository questionAssignmentRepository;
    private final PlayerAnswerVoteRepository playerAnswerVoteRepository;

    public CleanupService(GameRepository gameRepository, PlayerRepository playerRepository,
            QuestionRepository questionRepository,
            AnswerRepository answerRepository,
            QuestionAssignmentRepository questionAssignmentRepository,
            PlayerAnswerVoteRepository playerAnswerVoteRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.questionAssignmentRepository = questionAssignmentRepository;
        this.playerAnswerVoteRepository = playerAnswerVoteRepository;
    }

    // Runs every hour (can adjust the cron expression if needed)
    // @Scheduled(cron = "0 0 * * * ?") // Every hour at minute 0
    @Transactional
    @Scheduled(cron = "0 */3 * * * ?") // Using only for debugging
    public void cleanUp() {
        logger.info("Performing scheduled clean up:");
        //LocalDateTime cutoffTime = LocalDateTime.now().minusHours(1); // Delete everything older than 1 hour.
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(3); // Use only for debugging
        try {
            // Get the IDs of games to be deleted
            List<GameModel> oldGames = gameRepository.findOldGames(cutoffTime);

            if (!oldGames.isEmpty()) {
                // Perform the deletion
                // Delete associated records
                questionAssignmentRepository.deleteByGames(oldGames);
                playerAnswerVoteRepository.deleteByGames(oldGames);
                answerRepository.deleteByGames(oldGames);
                questionRepository.deleteByGames(oldGames);
                playerRepository.deleteByGames(oldGames);
                int deletedGames = gameRepository.deleteOldGames(cutoffTime);
                logger.info("Clean up: deleted {} old games", deletedGames);

                // Extract game IDs for logging
                List<String> oldGameIds = oldGames.stream()
                        .map(GameModel::getGameId)
                        .toList();
                
                logger.info("Clean up: deleted associated records for games with IDs: {}", oldGameIds);
            } else {
                logger.info("No old games found for deletion.");
            }

        } catch (Exception e) {
            logger.error("Error occurred during game cleanup: " + e.getMessage(), e);
        }
    }
}
