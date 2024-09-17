package com.brettstine.social_game_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brettstine.social_game_backend.model.AnswerModel;

public interface AnswerRepository extends JpaRepository<AnswerModel, String> {
    
    List<AnswerModel> findAllByGameId(String gameId);

}
