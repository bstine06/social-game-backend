package com.brettstine.social_game_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import com.brettstine.social_game_backend.model.QuestionModel;

public interface QuestionRepository extends JpaRepository<QuestionModel, String> {

    Optional<QuestionModel> findByPlayerId(String playerId);

    List<QuestionModel> findAllByGameId(String gameId);

    boolean existsByPlayerId(String playerId);

    @Modifying
    @Transactional
    @Query("DELETE FROM QuestionModel q WHERE q.gameId IN :gameIds")
    void deleteByGameIds(List<String> gameIds);

}
