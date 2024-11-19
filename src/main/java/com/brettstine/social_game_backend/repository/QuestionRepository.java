package com.brettstine.social_game_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import com.brettstine.social_game_backend.model.AnswerModel;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.QuestionModel;
import com.brettstine.social_game_backend.model.VotingStatus;

public interface QuestionRepository extends JpaRepository<QuestionModel, String> {

    Optional<QuestionModel> findByPlayer(PlayerModel player);

    List<QuestionModel> findAllByGame(GameModel game);

    boolean existsByPlayer(PlayerModel player);

    // Delete answers by game
    @Modifying
    @Transactional
    @Query("DELETE FROM QuestionModel q WHERE q.game IN :games")
    void deleteByGames(@Param("games") List<GameModel> games);

    List<QuestionModel> findByGameAndVotingStatus(GameModel game, VotingStatus votingStatus);

    @Query("SELECT COUNT(p) FROM QuestionModel q " +
       "JOIN q.game g " +
       "JOIN g.players p " +
       "WHERE q.id = :id")
    int countPlayersForGameOfQuestion(@Param("id") String questionId);

    @Query("SELECT a FROM AnswerModel a WHERE a.question.id = :questionId")
    List<AnswerModel> findAnswersByQuestionId(@Param("questionId") String questionId);

}

