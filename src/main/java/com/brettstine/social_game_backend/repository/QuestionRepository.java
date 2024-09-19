package com.brettstine.social_game_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.QuestionModel;

public interface QuestionRepository extends JpaRepository<QuestionModel, String> {

    Optional<QuestionModel> findByPlayer(PlayerModel player);

    List<QuestionModel> findAllByGame(GameModel game);

    boolean existsByPlayer(PlayerModel player);

    // Delete answers by game
    @Modifying
    @Transactional
    @Query("DELETE FROM QuestionModel q WHERE q.game IN :games")
    void deleteByGames(@Param("games") List<GameModel> games);

}

