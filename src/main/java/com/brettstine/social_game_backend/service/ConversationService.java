package com.brettstine.social_game_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.AnswerModel;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.QuestionAssignmentModel;
import com.brettstine.social_game_backend.model.QuestionModel;
import com.brettstine.social_game_backend.repository.AnswerRepository;
import com.brettstine.social_game_backend.repository.QuestionAssignmentRepository;
import com.brettstine.social_game_backend.repository.QuestionRepository;

@Service
public class ConversationService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuestionAssignmentRepository questionAssignmentRepository;

    public ConversationService(AnswerRepository answerRepository,
            QuestionRepository questionRepository,
            QuestionAssignmentRepository questionAssignmentRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.questionAssignmentRepository = questionAssignmentRepository;
    }

    public QuestionModel submitQuestion(GameModel game, PlayerModel player, String content) {
        QuestionModel question = new QuestionModel(game, player, content);
        return questionRepository.save(question);
    }

    public boolean hasSubmittedQuestion(PlayerModel player) {
        return questionRepository.existsByPlayer(player);
    }

    public AnswerModel submitAnswer(GameModel game, PlayerModel player, QuestionModel question, String content) {
        AnswerModel answer = new AnswerModel(game, player, question, content);
        answer = answerRepository.save(answer);
        return answer;
    }

    public boolean hasTwoAnswers(QuestionModel question) {
        return answerRepository.hasQuestionReceivedTwoAnswers(question);
    }

    public void deleteQuestion(String questionId) {
        questionRepository.deleteById(questionId);
    }

    public void deleteAnswer(String answerId) {
        answerRepository.deleteById(answerId);
    }

    public QuestionModel getQuestionById(String questionId) {
        return questionRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + questionId));
    }

    public AnswerModel getAnswerById(String answerId) {
        return answerRepository.findById(answerId).orElseThrow(() -> new IllegalArgumentException("Answer not found with ID: " + answerId));
    }

    public QuestionModel getQuestionByPlayer(PlayerModel player) {
        return questionRepository.findByPlayer(player).orElseThrow(() -> new IllegalArgumentException("Question not found with player ID: " + player.getPlayerId()));
    }

    public List<QuestionModel> getQuestionsForPlayer(PlayerModel player) {
        List<QuestionModel> questions = questionAssignmentRepository.findQuestionsAssignedToPlayer(player);
        return questions;
    }

    public void addQuestionForPlayer(GameModel game, PlayerModel player, QuestionModel question) {
        List<QuestionAssignmentModel> existingAssignments = questionAssignmentRepository.findAllByPlayer(player);
        if (existingAssignments.size() >= 2) {
            throw new IllegalStateException("Player with ID " + player.getPlayerId() + " has already been assigned 2 questions.");
        }
        QuestionAssignmentModel questionAssignment = new QuestionAssignmentModel(question, player, game);
        questionAssignmentRepository.save(questionAssignment);
    }

    public List<AnswerModel> getAnswersForQuestion(QuestionModel question) {
        List<AnswerModel> answers = answerRepository.findAllByQuestion(question);
        return answers;
    }

    public List<QuestionModel> getAllQuestions() {
        return questionRepository.findAll();
    }

    public List<QuestionModel> getAllQuestionsByGame(GameModel game) {
        return questionRepository.findAllByGame(game);
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

    public boolean isQuestionAssignedToPlayer(PlayerModel player, QuestionModel question) {
        return questionAssignmentRepository.isQuestionAssignedToPlayer(player, question);
    }

}
