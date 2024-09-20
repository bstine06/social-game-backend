package com.brettstine.social_game_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.AnswerModel;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.QuestionModel;
import com.brettstine.social_game_backend.repository.AnswerRepository;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public AnswerService(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    public AnswerModel submitAnswer(GameModel game, PlayerModel player, QuestionModel question, String content) {
        AnswerModel answer = new AnswerModel(game, player, question, content);
        answer = answerRepository.save(answer);
        return answer;
    }

    public boolean hasTwoAnswers(QuestionModel question) {
        return answerRepository.hasQuestionReceivedTwoAnswers(question);
    }

    public void deleteAnswer(String answerId) {
        answerRepository.deleteById(answerId);
    }

    public AnswerModel getAnswerById(String answerId) {
        return answerRepository.findById(answerId).orElseThrow(() -> new IllegalArgumentException("Answer not found with ID: " + answerId));
    }

    public List<AnswerModel> getAnswersForQuestion(QuestionModel question) {
        List<AnswerModel> answers = answerRepository.findAllByQuestion(question);
        return answers;
    }

    public List<AnswerModel> getAllAnswers() {
        return answerRepository.findAll();
    }

    public List<AnswerModel> getAllAnswersByGame(GameModel game) {
        return answerRepository.findAllByGame(game);
    }

    public boolean hasPlayerAnsweredQuestion(PlayerModel player, QuestionModel question) {
        return answerRepository.hasPlayerAnsweredQuestion(player, question);
    }

}
