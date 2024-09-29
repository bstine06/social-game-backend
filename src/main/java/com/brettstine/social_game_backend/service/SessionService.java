package com.brettstine.social_game_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.Role;
import com.brettstine.social_game_backend.model.SessionModel;
import com.brettstine.social_game_backend.repository.SessionRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SessionService {

    @Autowired
    private SessionRepository sessionRepository;

    public SessionModel createSession() {
        SessionModel session = new SessionModel();
        return sessionRepository.save(session);
    }

    public SessionModel getSessionById(String sessionId) {
        Optional<SessionModel> sessionOptional = sessionRepository.findById(sessionId);
        if (sessionOptional.isPresent()) {
            return sessionOptional.get();
        } else {
            return null;
        }
    }

    // Method to assign a game to a session
    public void assignGameToSession(String sessionId, GameModel game) {
        Optional<SessionModel> sessionOptional = sessionRepository.findById(sessionId);
        
        if (sessionOptional.isPresent()) {
            SessionModel session = sessionOptional.get();
            session.setGame(game);
            sessionRepository.save(session);
        } else {
            throw new RuntimeException("Session not found");
        }
    }

    // Method to update the role of a session
    public void updateRoleOfSession(String sessionId, Role newRole) {
        Optional<SessionModel> sessionOptional = sessionRepository.findById(sessionId);
        
        if (sessionOptional.isPresent()) {
            SessionModel session = sessionOptional.get();
            session.setRole(newRole);
            sessionRepository.save(session);
        } else {
            throw new RuntimeException("Session not found");
        }
    }

    public List<SessionModel> getAllSessions() {
        return sessionRepository.findAll();
    }
}

