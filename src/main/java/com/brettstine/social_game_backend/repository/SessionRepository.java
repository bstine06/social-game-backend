package com.brettstine.social_game_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.brettstine.social_game_backend.model.SessionModel;

public interface SessionRepository extends JpaRepository<SessionModel, String> {
    
    

}
