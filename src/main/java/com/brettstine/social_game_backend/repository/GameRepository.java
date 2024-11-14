package com.brettstine.social_game_backend.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import com.brettstine.social_game_backend.model.GameModel;

public interface GameRepository extends JpaRepository<GameModel, String> {

    @Modifying
    @Transactional
    @Query("DELETE FROM GameModel g WHERE g.creationTime < :cutoffTime")
    int deleteOldGames(@Param("cutoffTime") Instant cutoffTime);

    @Query("SELECT g FROM GameModel g WHERE g.creationTime < :cutoffTime")
    List<GameModel> findOldGames(@Param("cutoffTime") Instant cutoffTime);

    @Query("SELECT g FROM GameModel g WHERE g.hostId = :hostId")
    Optional<GameModel> findByHostId(@Param("hostId") String hostId);
}

