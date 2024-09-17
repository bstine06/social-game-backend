package com.brettstine.social_game_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brettstine.social_game_backend.model.QuestionModel;

public interface QuestionRepository extends JpaRepository<QuestionModel, String> {

    Optional<QuestionModel> findByPlayerId(String playerId);

    List<QuestionModel> findAllByGameId(String gameId);

    boolean existsByPlayerId(String playerId);

}
