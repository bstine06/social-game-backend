package com.brettstine.social_game_backend.repository;

import com.brettstine.social_game_backend.model.QuestionAssignmentModel;
import com.brettstine.social_game_backend.model.QuestionAssignmentId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

public interface QuestionAssignmentRepository extends JpaRepository<QuestionAssignmentModel, QuestionAssignmentId> {
    
    List<QuestionAssignmentModel> findAllByPlayerId(String playerId);

    @Modifying
    @Transactional
    @Query("DELETE FROM QuestionAssignmentModel qAss WHERE qAss.gameId IN :gameIds")
    void deleteByGameIds(List<String> gameIds);

}
