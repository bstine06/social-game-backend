package com.brettstine.social_game_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brettstine.social_game_backend.model.QuestionAnswerId;
import com.brettstine.social_game_backend.model.QuestionAnswerModel;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswerModel, QuestionAnswerId> {
    
    List<QuestionAnswerModel> findAllByQuestionId(String questionId);

}
