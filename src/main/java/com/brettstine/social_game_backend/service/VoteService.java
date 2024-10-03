package com.brettstine.social_game_backend.service;

import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;

import com.brettstine.social_game_backend.dto.AnswerDTO;
import com.brettstine.social_game_backend.dto.BallotDTO;
import com.brettstine.social_game_backend.dto.QuestionDTO;
import com.brettstine.social_game_backend.model.AnswerModel;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.PlayerAnswerVoteModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.QuestionModel;
import com.brettstine.social_game_backend.model.VotingStatus;
import com.brettstine.social_game_backend.repository.PlayerAnswerVoteRepository;
import com.brettstine.social_game_backend.repository.QuestionRepository;

@Service
@RequestMapping("/vote")
public class VoteService {
    
    private final PlayerAnswerVoteRepository playerAnswerVoteRepository;
    private final QuestionRepository questionRepository;

    public VoteService(PlayerAnswerVoteRepository playerAnswerVoteRepository, QuestionRepository questionRepository) {
        this.playerAnswerVoteRepository = playerAnswerVoteRepository;
        this.questionRepository = questionRepository;
    }

    public void submitVote(GameModel game, PlayerModel player, AnswerModel answer) {
        PlayerAnswerVoteModel vote = new PlayerAnswerVoteModel(player, answer, game);
        playerAnswerVoteRepository.save(vote);
    }

    public List<PlayerModel> getPlayersWhoVotedForAnswer(AnswerModel answer) {
        return playerAnswerVoteRepository.findPlayersWhoVotedForAnswer(answer);
    }

    public void clearAllVotesForGame(GameModel game) {
        playerAnswerVoteRepository.deleteByGame(game);
    }

    public QuestionModel getOneUnvotedQuestionInGame(GameModel game) {
        List<QuestionModel> questions = questionRepository.findByGameAndVotingStatus(game, VotingStatus.NOT_VOTED);

        if (questions.isEmpty()) {
            return null;
        }

        return questions.get(0); // Assuming you want the first question.
    }

    public QuestionModel getCurrentQuestion(GameModel game) {
        List<QuestionModel> questions = questionRepository.findByGameAndVotingStatus(game, VotingStatus.IN_PROGRESS);

        if (questions.isEmpty()) {
            throw new IllegalStateException("No question found with VotingStatus.IN_PROGRESS for the given game.");
        }

        return questions.get(0); // Assuming you want the first question.
    }

    public BallotDTO getCurrentBallot(GameModel game) {
        QuestionModel question = getCurrentQuestion(game);
        QuestionDTO questionDTO = new QuestionDTO(question.getContent(), question.getQuestionId(), question.getPlayer().getName());
        List<AnswerModel> answers = question.getAnswers();
        List<AnswerDTO> answerDTOs = answers.stream()
                .map((answer) -> new AnswerDTO(answer.getContent(), answer.getAnswerId(), answer.getPlayer().getName()))
                .collect(Collectors.toList());
        BallotDTO ballotDTO = new BallotDTO(questionDTO, answerDTOs);
        return ballotDTO;
    }

    public List<QuestionModel> getAllQuestions() {
        return questionRepository.findAll();
    }

    public void openVotingForQuestion(QuestionModel question) {
        if (question.getAnswers().size() < 2) {
            throw new IllegalStateException("Cannot open voting for a question without at least 2 answers.");
        }
        GameModel game = question.getGame();
        List<QuestionModel> questionsInProgressInGame = questionRepository.findByGameAndVotingStatus(game,
                VotingStatus.IN_PROGRESS);

        if (!questionsInProgressInGame.isEmpty()) {
            throw new IllegalStateException("Another question is already in progress for this game.");
        }

        question.setVotingStatus(VotingStatus.IN_PROGRESS);
        questionRepository.save(question);
    }

    public void closeVotingForQuestion(QuestionModel question) {
        question.setVotingStatus(VotingStatus.COMPLETE);
        questionRepository.save(question);
    }

    public boolean hasQuestionReceivedAllPossibleVotes(QuestionModel question) {
        int totalPossibleVotes = question.getGame().getPlayers().size() - 2; //exclude the players actively competing
        int totalVotes = question.getAnswers().stream()
                .mapToInt(answer -> playerAnswerVoteRepository.countByAnswer(answer))
                .sum();
        return (totalVotes >= totalPossibleVotes);
    }

}
