package com.brettstine.social_game_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.AnswerModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.QuestionModel;
import com.brettstine.social_game_backend.repository.AnswerDatabase;
import com.brettstine.social_game_backend.repository.PlayerQuestionDatabase;
import com.brettstine.social_game_backend.repository.QuestionAnswerDatabase;
import com.brettstine.social_game_backend.repository.QuestionDatabase;

@Service
public class ConversationService {

    private final QuestionDatabase questionDatabase;
    private final AnswerDatabase answerDatabase;
    private final QuestionAnswerDatabase questionAnswerDatabase;
    private final PlayerQuestionDatabase playerQuestionDatabase;

    public ConversationService(AnswerDatabase answerDatabase,
            QuestionDatabase questionDatabase,
            QuestionAnswerDatabase questionAnswerDatabase,
            PlayerQuestionDatabase playerQuestionDatabase) {
        this.questionDatabase = questionDatabase;
        this.answerDatabase = answerDatabase;
        this.questionAnswerDatabase = questionAnswerDatabase;
        this.playerQuestionDatabase = playerQuestionDatabase;
    }

    public QuestionModel submitQuestion(String gameId, String playerId, String content) {
        QuestionModel question = new QuestionModel(gameId, playerId, content);
        questionDatabase.addQuestion(question);
        return question;
    }

    public boolean hasSubmittedQuestion(String playerId) {
        return questionDatabase.hasQuestionByPlayerId(playerId);
    }

    public AnswerModel submitAnswer(String gameId, String playerId, String questionId, String content) {
        AnswerModel answer = new AnswerModel(gameId, playerId, questionId, content);
        answerDatabase.addAnswer(answer);
        questionAnswerDatabase.addQuestionAnswer(questionId, answer.getAnswerId());
        return answer;
    }

    public boolean hasTwoAnswers(String questionId) {
        return questionAnswerDatabase.hasExactlyTwoAnswers(questionId);
    }

    public void deleteQuestion(String questionId) {
        questionDatabase.deleteQuestion(questionId);
    }

    public void deleteAnswer(String answerId) {
        answerDatabase.deleteAnswer(answerId);
    }

    public QuestionModel getQuestionById(String questionId) {
        return questionDatabase.getQuestionById(questionId);
    }

    public AnswerModel getAnswerById(String answerId) {
        return answerDatabase.getAnswerById(answerId);
    }

    public QuestionModel getQuestionByPlayerId(String playerId) {
        return questionDatabase.getQuestionByPlayerId(playerId);
    }

    public List<QuestionModel> getQuestionsForPlayer(String playerId) {
        List<String> questionIds = playerQuestionDatabase.getQuestionsForPlayer(playerId);
        List<QuestionModel> questions = questionIds.stream()
                .map(questionId -> getQuestionById(questionId))
                .collect(Collectors.toList());
        return questions;
    }

    public void addQuestionForPlayer(String playerId, String questionId) {
        playerQuestionDatabase.addPlayerQuestion(playerId, questionId);
    }

    public List<AnswerModel> getAnswersForQuestion(String questionId) {
        List<String> answerIds = questionAnswerDatabase.getAnswersForQuestion(questionId);
        List<AnswerModel> answers = answerIds.stream()
                .map(answerId -> getAnswerById(answerId))
                .collect(Collectors.toList());
        return answers;
    }

    public List<QuestionModel> getAllQuestions() {
        return questionDatabase.getAllQuestions();
    }

    public List<QuestionModel> getAllQuestionsByGameId(String gameId) {
        return questionDatabase.getAllQuestionsByGameId(gameId);
    }

    public List<AnswerModel> getAllAnswers() {
        return answerDatabase.getAllAnswers();
    }

}
