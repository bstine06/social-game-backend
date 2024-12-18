package com.brettstine.social_game_backend.service;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;

import com.brettstine.social_game_backend.dto.AnswerDTO;
import com.brettstine.social_game_backend.dto.BallotDTO;
import com.brettstine.social_game_backend.dto.QuestionDTO;
import com.brettstine.social_game_backend.dto.VoteDTO;
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
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final QuestionRepository questionRepository;

    public VoteService(PlayerAnswerVoteRepository playerAnswerVoteRepository, QuestionService questionService, AnswerService answerService, QuestionRepository questionRepository) {
        this.playerAnswerVoteRepository = playerAnswerVoteRepository;
        this.questionService = questionService;
        this.answerService = answerService;
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
        QuestionDTO questionDTO = new QuestionDTO(question.getContent(), question.getQuestionId(), question.getPlayer());

        List<AnswerModel> answers = question.getAnswers();
        List<AnswerDTO> answerDTOs = answers.stream()
                .map((answer) -> new AnswerDTO(answer.getContent(), answer.getAnswerId(), answer.getPlayer()))
                .collect(Collectors.toList());

        if (answerService.getAnswersForQuestion(question).size() < 2) {
            getPlayersWhoFailedToAnswerQuestion(question).stream()
                .forEach((player) -> {
                    AnswerDTO failedAnswerDTO = new AnswerDTO(player, false);
                    answerDTOs.add(failedAnswerDTO);
                });
        }

        BallotDTO ballotDTO = new BallotDTO(questionDTO, answerDTOs);
        return ballotDTO;
    }

    public List<VoteDTO> getCurrentBallotVotes(GameModel game) {
        QuestionModel question = getCurrentQuestion(game);
        List<AnswerModel> answers = question.getAnswers();
        List<PlayerAnswerVoteModel> allPlayerAnswerVotes = new ArrayList<>();
        answers.stream()
                .forEach((answer) -> {
                    allPlayerAnswerVotes.addAll(playerAnswerVoteRepository.findAllByAnswer(answer));
                });
        List<VoteDTO> votes = allPlayerAnswerVotes.stream()
                .map((playerAnswerVote) -> {
                    return new VoteDTO(playerAnswerVote.getPlayer(), playerAnswerVote.getAnswerId());
                })
                .collect(Collectors.toList());
        return votes;
    }

    public List<QuestionModel> getAllQuestions() {
        return questionRepository.findAll();
    }

    public boolean canQuestionReceiveVotes(QuestionModel question) {
        GameModel game = question.getGame();
        List<QuestionModel> questionsInProgressInGame = questionRepository.findByGameAndVotingStatus(game,
                VotingStatus.IN_PROGRESS);

        if (!questionsInProgressInGame.isEmpty()) {
            return false;
            // throw new IllegalStateException("Another question is already in progress for this game.");
        }

        if (answerService.getAnswersForQuestion(question).size() < 2) {
            return false;
        }

        return true;
    }

    public void openVotingForQuestion(QuestionModel question) {
        question.setVotingStatus(VotingStatus.IN_PROGRESS);
        questionRepository.save(question);
    }

    public void closeVotingForQuestion(QuestionModel question) {
        question.setVotingStatus(VotingStatus.COMPLETE);
        questionRepository.save(question);
    }

    private List<PlayerModel> getPlayersWhoFailedToAnswerQuestion(QuestionModel question) {
        // Get all players assigned to the question
        List<PlayerModel> assignedPlayers = questionService.getPlayersAssignedToQuestion(question);
        
        // Get all players who submitted answers
        List<PlayerModel> playersWithAnswers = questionRepository.findAnswersByQuestionId(question.getQuestionId())
                .stream()
                .map(AnswerModel::getPlayer)
                .distinct()
                .toList();
    
        // Identify players who failed to answer
        List<PlayerModel> playersWhoFailed = assignedPlayers.stream()
                .filter(player -> !playersWithAnswers.contains(player))
                .toList();

        return playersWhoFailed;
    }

    public boolean hasQuestionReceivedAllPossibleVotes(QuestionModel question) {
        int totalPossibleVotes = questionRepository.countPlayersForGameOfQuestion(question.getQuestionId()) - 2; // exclude players actively competing

        List<AnswerModel> answers = questionRepository.findAnswersByQuestionId(question.getQuestionId());
        int totalVotes = answers.stream()
                .mapToInt(answer -> {
                    Integer count = playerAnswerVoteRepository.countByAnswer(answer);
                    return (count != null) ? count : 0;
                })
                .sum();
        return totalVotes >= totalPossibleVotes;
    }

}
