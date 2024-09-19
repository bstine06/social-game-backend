package com.brettstine.social_game_backend.repository;

import com.brettstine.social_game_backend.model.QuestionAssignmentModel;
import com.brettstine.social_game_backend.model.QuestionModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.QuestionAssignmentId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

public interface QuestionAssignmentRepository extends JpaRepository<QuestionAssignmentModel, QuestionAssignmentId> {

    List<QuestionAssignmentModel> findAllByPlayer(PlayerModel player);

    List<QuestionAssignmentModel> findAllByQuestion(QuestionModel question);

    List<QuestionAssignmentModel> findAllByGame(GameModel game);

    @Modifying
    @Transactional
    @Query("DELETE FROM QuestionAssignmentModel qAss WHERE qAss.game IN :games")
    void deleteByGames(@Param("games") List<GameModel> games);

    @Query("SELECT COUNT(q) > 0 FROM QuestionAssignmentModel q WHERE q.player = :player AND q.question = :question")
    boolean isQuestionAssignedToPlayer(@Param("player") PlayerModel player, @Param("question") QuestionModel question);

    @Query("SELECT qAss.question FROM QuestionAssignmentModel qAss WHERE qAss.player = :player")
    List<QuestionModel> findQuestionsAssignedToPlayer(@Param("player") PlayerModel player);
}

