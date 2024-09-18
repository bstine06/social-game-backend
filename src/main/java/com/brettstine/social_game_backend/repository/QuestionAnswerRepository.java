package com.brettstine.social_game_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import com.brettstine.social_game_backend.model.QuestionAnswerId;
import com.brettstine.social_game_backend.model.QuestionAnswerModel;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswerModel, QuestionAnswerId> {
    
    List<QuestionAnswerModel> findAllByQuestionId(String questionId);

    @Modifying
    @Transactional
    @Query("DELETE FROM QuestionAnswerModel qAns WHERE qAns.gameId IN :gameIds")
    void deleteByGameIds(List<String> gameIds);

}
