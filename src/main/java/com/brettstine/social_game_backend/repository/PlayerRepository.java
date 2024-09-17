package com.brettstine.social_game_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brettstine.social_game_backend.model.PlayerModel;
import java.util.List;


public interface PlayerRepository extends JpaRepository<PlayerModel, String> {
    
    List<PlayerModel> findByGameId(String gameId);
}
