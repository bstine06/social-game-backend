package com.brettstine.social_game_backend.repository;

import com.brettstine.social_game_backend.model.QuestionAssignmentModel;
import com.brettstine.social_game_backend.model.QuestionAssignmentId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionAssignmentRepository extends JpaRepository<QuestionAssignmentModel, QuestionAssignmentId> {
    
    List<QuestionAssignmentModel> findAllByPlayerId(String playerId);

}
