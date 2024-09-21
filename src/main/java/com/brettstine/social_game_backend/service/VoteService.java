package com.brettstine.social_game_backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.AnswerModel;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.PlayerAnswerVoteModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.repository.PlayerAnswerVoteRepository;

@Service
public class VoteService {
    
    private final PlayerAnswerVoteRepository playerAnswerVoteRepository;

    public VoteService(PlayerAnswerVoteRepository playerAnswerVoteRepository) {
        this.playerAnswerVoteRepository = playerAnswerVoteRepository;
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

}
