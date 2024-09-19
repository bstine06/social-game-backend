package com.brettstine.social_game_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import com.brettstine.social_game_backend.model.AnswerModel;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.QuestionModel;

public interface AnswerRepository extends JpaRepository<AnswerModel, String> {
    
    // Find answers by game
    List<AnswerModel> findAllByGame(GameModel game);

    // Find all answers to a question
    List<AnswerModel> findAllByQuestion(QuestionModel question);

    // Delete answers by games
    @Modifying
    @Transactional
    @Query("DELETE FROM AnswerModel a WHERE a.game IN :games")
    void deleteByGames(@Param("games") List<GameModel> games);

    // Check if a player has answered a question
    @Query("SELECT COUNT(a) > 0 FROM AnswerModel a WHERE a.player = :player AND a.question = :question")
    boolean hasPlayerAnsweredQuestion(@Param("player") PlayerModel player, @Param("question") QuestionModel question);

    // Check if at least 2 answers exist for a question
    @Query("SELECT COUNT(a) >= 2 FROM AnswerModel a WHERE a.question = :question")
    boolean hasQuestionReceivedTwoAnswers(@Param("question") QuestionModel question);
}

