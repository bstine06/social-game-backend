package com.brettstine.social_game_backend.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.controller.ConversationController;
import com.brettstine.social_game_backend.repository.AnswerRepository;
import com.brettstine.social_game_backend.repository.GameRepository;
import com.brettstine.social_game_backend.repository.PlayerRepository;
import com.brettstine.social_game_backend.repository.QuestionAnswerRepository;
import com.brettstine.social_game_backend.repository.QuestionAssignmentRepository;
import com.brettstine.social_game_backend.repository.QuestionRepository;

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
    private final QuestionAnswerRepository questionAnswerRepository;

    public CleanupService(GameRepository gameRepository, PlayerRepository playerRepository,
            QuestionRepository questionRepository,
            AnswerRepository answerRepository,
            QuestionAssignmentRepository questionAssignmentRepository,
            QuestionAnswerRepository questionAnswerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.questionAssignmentRepository = questionAssignmentRepository;
        this.questionAnswerRepository = questionAnswerRepository;
    }

    // Runs every hour (can adjust the cron expression if needed)
    @Scheduled(cron = "0 0 * * * ?") // Every hour at minute 0
    // @Scheduled(cron = "0 */2 * * * ?") // Every even minute (00:00, 00:02, 00:04, etc). Using only for debugging
    public void cleanUp() {
        logger.info("Performing scheduled clean up:");
        LocalDateTime cutoffTime = LocalDateTime.now().minusHours(1); // Delete everything older than 1 hour.
        //LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(2); // Delete everything older than 2 minutes. Use only for debugging
        try {
            // Get the IDs of games to be deleted
            List<String> oldGameIds = gameRepository.findOldGameIds(cutoffTime);

            if (!oldGameIds.isEmpty()) {
                // Perform the deletion
                int deletedGames = gameRepository.deleteOldGames(cutoffTime);
                logger.info("Clean up: deleted {} old games", deletedGames);

                // Delete associated records
                playerRepository.deleteByGameIds(oldGameIds);
                questionRepository.deleteByGameIds(oldGameIds);
                answerRepository.deleteByGameIds(oldGameIds);
                questionAssignmentRepository.deleteByGameIds(oldGameIds);
                questionAnswerRepository.deleteByGameIds(oldGameIds);
                
                logger.info("Clean up: deleted associated records for games with IDs: {}", oldGameIds);
            } else {
                logger.info("No old games found for deletion.");
            }

        } catch (Exception e) {
            logger.error("Error occurred during game cleanup: " + e.getMessage(), e);
        }
    }
}
