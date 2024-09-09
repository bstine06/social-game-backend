package com.brettstine.social_game_backend.service;

import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.SessionModel;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

@Service
public class SessionService {

    private final Map<String, SessionModel> sessions = new ConcurrentHashMap<>();

    public SessionModel createSession() {
        String sessionId = UUID.randomUUID().toString();
        SessionModel session = new SessionModel(sessionId);
        sessions.put(sessionId, session);
        return session;
    }

    public SessionModel getSession(String sessionId) {
        return sessions.get(sessionId);
    }

    public void deleteSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public SessionModel setPlayer(String sessionId, String playerName) {

        PlayerModel player;

        // Check if a player already exists for this session
        SessionModel session = sessions.get(sessionId);
        if (session.getPlayer() != null) {
            player = session.getPlayer();
            player.setPlayerName(playerName);
            return session;
        }

        // Create a new player & add to session
        player = new PlayerModel(playerName);
        session.setPlayer(player);
        return session;
    }

    public Collection<SessionModel> getAllSessions() {
        return sessions.values();
    }

    public void clearAllSessions() {
        // remove every player from players Map
        sessions.clear();
        // reset playerCounter to 0
        PlayerModel.resetHostPlayerFlag();
    }
}
