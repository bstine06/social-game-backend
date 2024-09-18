package com.brettstine.social_game_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import com.brettstine.social_game_backend.model.PlayerModel;

import java.util.List;


public interface PlayerRepository extends JpaRepository<PlayerModel, String> {
    
    List<PlayerModel> findByGameId(String gameId);

    @Modifying
    @Transactional
    @Query("DELETE FROM PlayerModel p WHERE p.gameId IN :gameIds")
    void deleteByGameIds(List<String> gameIds);

}
