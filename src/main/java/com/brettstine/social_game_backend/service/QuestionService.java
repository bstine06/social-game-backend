package com.brettstine.social_game_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.AnswerModel;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.QuestionAssignmentModel;
import com.brettstine.social_game_backend.model.QuestionModel;
import com.brettstine.social_game_backend.model.VotingStatus;
import com.brettstine.social_game_backend.repository.AnswerRepository;
import com.brettstine.social_game_backend.repository.QuestionAssignmentRepository;
import com.brettstine.social_game_backend.repository.QuestionRepository;
import com.brettstine.social_game_backend.dto.QuestionDTO;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionAssignmentRepository questionAssignmentRepository;

    public QuestionService(QuestionRepository questionRepository,
            QuestionAssignmentRepository questionAssignmentRepository) {
        this.questionRepository = questionRepository;
        this.questionAssignmentRepository = questionAssignmentRepository;
    }

    public QuestionModel submitQuestion(GameModel game, PlayerModel player, String content) {
        QuestionModel question = new QuestionModel(game, player, content);
        return questionRepository.save(question);
    }

    public boolean hasSubmittedQuestion(PlayerModel player) {
        return questionRepository.existsByPlayer(player);
    }

    public void deleteQuestion(String questionId) {
        questionRepository.deleteById(questionId);
    }

    public QuestionModel getQuestionById(String questionId) {
        return questionRepository.findById(questionId).orElseThrow(() -> new IllegalArgumentException("Question not found with ID: " + questionId));
    }

    public QuestionModel getQuestionByPlayer(PlayerModel player) {
        return questionRepository.findByPlayer(player).orElseThrow(() -> new IllegalArgumentException("Question not found with player ID: " + player.getPlayerId()));
    }

    public List<QuestionDTO> getUnansweredQuestionsForPlayer(PlayerModel player, List<String> answeredQuestionIds) {
        List<QuestionModel> questions = questionAssignmentRepository.findQuestionsAssignedToPlayer(player);
        List<QuestionDTO> questionDTOs = questions.stream()
                .filter(question -> !answeredQuestionIds.contains(question.getQuestionId()))
                .map((question) -> new QuestionDTO(question.getContent(), question.getQuestionId(), question.getPlayer().getName()))
                .collect(Collectors.toList());
        return questionDTOs;
    }

    public void addQuestionForPlayer(GameModel game, PlayerModel player, QuestionModel question) {
        List<QuestionAssignmentModel> existingAssignments = questionAssignmentRepository.findAllByPlayer(player);
        if (existingAssignments.size() >= 2) {
            throw new IllegalStateException("Player with ID " + player.getPlayerId() + " has already been assigned 2 questions.");
        }
        QuestionAssignmentModel questionAssignment = new QuestionAssignmentModel(question, player, game);
        questionAssignmentRepository.save(questionAssignment);
    }

    public List<QuestionModel> getAllQuestions() {
        return questionRepository.findAll();
    }

    public List<QuestionModel> getAllQuestionsByGame(GameModel game) {
        return questionRepository.findAllByGame(game);
    }

    public boolean isQuestionAssignedToPlayer(PlayerModel player, QuestionModel question) {
        return questionAssignmentRepository.isQuestionAssignedToPlayer(player, question);
    }

}
