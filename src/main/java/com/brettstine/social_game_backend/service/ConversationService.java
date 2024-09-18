package com.brettstine.social_game_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.AnswerModel;
import com.brettstine.social_game_backend.model.QuestionAnswerModel;
import com.brettstine.social_game_backend.model.QuestionAssignmentModel;
import com.brettstine.social_game_backend.model.QuestionModel;
import com.brettstine.social_game_backend.repository.AnswerRepository;
import com.brettstine.social_game_backend.repository.QuestionAnswerRepository;
import com.brettstine.social_game_backend.repository.QuestionAssignmentRepository;
import com.brettstine.social_game_backend.repository.QuestionRepository;

@Service
public class ConversationService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuestionAnswerRepository questionAnswerRepository;
    private final QuestionAssignmentRepository questionAssignmentRepository;

    public ConversationService(AnswerRepository answerRepository,
            QuestionRepository questionRepository,
            QuestionAnswerRepository questionAnswerRepository,
            QuestionAssignmentRepository questionAssignmentRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.questionAnswerRepository = questionAnswerRepository;
        this.questionAssignmentRepository = questionAssignmentRepository;
    }

    public QuestionModel submitQuestion(String gameId, String playerId, String content) {
        QuestionModel question = new QuestionModel(gameId, playerId, content);
        return questionRepository.save(question);
    }

    public boolean hasSubmittedQuestion(String playerId) {
        return questionRepository.existsByPlayerId(playerId);
    }

    public AnswerModel submitAnswer(String gameId, String playerId, String questionId, String content) {
        AnswerModel answer = new AnswerModel(gameId, playerId, questionId, content);
        answer = answerRepository.save(answer);
        QuestionAnswerModel questionAnswer = new QuestionAnswerModel(questionId, answer.getAnswerId(), gameId);
        questionAnswerRepository.save(questionAnswer);
        return answer;
    }

    public boolean hasTwoAnswers(String questionId) {
        List<QuestionAnswerModel> existingAnswers = questionAnswerRepository.findAllByQuestionId(questionId);
        if (existingAnswers.size() == 2) {
            return true;
        } else if (existingAnswers.size() > 2) {
            throw new IllegalStateException("Question with id " + questionId + " has more than 2 answers!");
        }
        return false;
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

    public QuestionModel getQuestionByPlayerId(String playerId) {
        return questionRepository.findByPlayerId(playerId).orElseThrow(() -> new IllegalArgumentException("Question not found with player ID: " + playerId));
    }

    public List<QuestionModel> getQuestionsForPlayer(String playerId) {
        List<QuestionAssignmentModel> questionAssignments = questionAssignmentRepository.findAllByPlayerId(playerId);
        List<QuestionModel> questions = questionAssignments.stream()
                .map(questionAssignment -> getQuestionById(questionAssignment.getQuestionId()))
                .collect(Collectors.toList());
        return questions;
    }

    public void addQuestionForPlayer(String gameId, String playerId, String questionId) {
        // Validate that the player has not been assigned more than 2 questions
        List<QuestionAssignmentModel> existingAssignments = questionAssignmentRepository.findAllByPlayerId(playerId);
        if (existingAssignments.size() >= 2) {
            throw new IllegalStateException("Player with ID " + playerId + " has already been assigned 2 questions.");
        }
        QuestionAssignmentModel questionAssignment = new QuestionAssignmentModel(questionId, playerId, gameId);
        questionAssignmentRepository.save(questionAssignment);
    }

    public List<AnswerModel> getAnswersForQuestion(String questionId) {
        List<QuestionAnswerModel> questionAnswers = questionAnswerRepository.findAllByQuestionId(questionId);
        List<AnswerModel> answers = questionAnswers.stream()
                .map(questionAnswer -> getAnswerById(questionAnswer.getAnswerId()))
                .collect(Collectors.toList());
        return answers;
    }

    public List<QuestionModel> getAllQuestions() {
        return questionRepository.findAll();
    }

    public List<QuestionModel> getAllQuestionsByGameId(String gameId) {
        return questionRepository.findAllByGameId(gameId);
    }

    public List<AnswerModel> getAllAnswers() {
        return answerRepository.findAll();
    }

    public List<AnswerModel> getAllAnswersByGameId(String gameId) {
        return answerRepository.findAllByGameId(gameId);
    }

}
