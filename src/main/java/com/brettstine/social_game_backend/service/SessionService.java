package com.brettstine.social_game_backend.service;

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

    public SessionModel setName(String sessionId, String name) {
        SessionModel session = sessions.get(sessionId);
        session.setName(name);
        return session;
    }

    public Collection<SessionModel> getAllSessions() {
        return sessions.values();
    }

    public void clearAllSessions() {
        sessions.clear();
    }
}
