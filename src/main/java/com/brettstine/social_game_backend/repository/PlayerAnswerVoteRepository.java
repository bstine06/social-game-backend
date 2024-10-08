package com.brettstine.social_game_backend.repository;

import com.brettstine.social_game_backend.model.PlayerAnswerVoteModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.AnswerModel;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.PlayerAnswerVoteId;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

public interface PlayerAnswerVoteRepository extends JpaRepository<PlayerAnswerVoteModel, PlayerAnswerVoteId> {

    List<PlayerAnswerVoteModel> findAllByPlayer(PlayerModel player);

    List<PlayerAnswerVoteModel> findAllByAnswer(AnswerModel answer);

    List<PlayerAnswerVoteModel> findAllByGame(GameModel game);

    // Delete by games
    @Modifying
    @Transactional
    @Query("DELETE FROM PlayerAnswerVoteModel pAV WHERE pAV.game IN :games")
    void deleteByGames(@Param("games") List<GameModel> games);

    // Delete by a single game
    @Modifying
    @Transactional
    @Query("DELETE FROM PlayerAnswerVoteModel pAV WHERE pAV.game = :game")
    void deleteByGame(@Param("game") GameModel game);

    @Query("SELECT COUNT(pAV) > 0 FROM PlayerAnswerVoteModel pAV WHERE pAV.player = :player AND pAV.answer = :answer AND pAV.game = :game")
    boolean hasPlayerVotedForAnswerInGame(@Param("player") PlayerModel player, @Param("answer") AnswerModel answer, @Param("game") GameModel game);

    @Query("SELECT pAV.player FROM PlayerAnswerVoteModel pAV WHERE pAV.answer = :answer")
    List<PlayerModel> findPlayersWhoVotedForAnswer(@Param("answer") AnswerModel answer);

    // Method to count votes by AnswerModel
    @Query("SELECT COUNT(v) FROM PlayerAnswerVoteModel v WHERE v.answer = :answer")
    int countByAnswer(@Param("answer") AnswerModel answer);

}

