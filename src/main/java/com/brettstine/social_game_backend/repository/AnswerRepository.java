package com.brettstine.social_game_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import com.brettstine.social_game_backend.model.AnswerModel;

public interface AnswerRepository extends JpaRepository<AnswerModel, String> {
    
    List<AnswerModel> findAllByGameId(String gameId);

    @Modifying
    @Transactional
    @Query("DELETE FROM AnswerModel a WHERE a.gameId IN :gameIds")
    void deleteByGameIds(List<String> gameIds);

    @Query("SELECT COUNT(a) > 0 FROM AnswerModel a WHERE a.playerId = :playerId AND a.questionId = :questionId")
    boolean hasPlayerAnsweredQuestion(@Param("playerId") String playerId, @Param("questionId") String questionId);

}
