package com.brettstine.social_game_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.GameModel;

import java.util.List;

public interface PlayerRepository extends JpaRepository<PlayerModel, String> {
    
    // Query to find players by game
    List<PlayerModel> findByGame(GameModel game);

    // Delete players by games
    @Modifying
    @Transactional
    @Query("DELETE FROM PlayerModel p WHERE p.game IN :games")
    void deleteByGames(@Param("games") List<GameModel> games);
}


