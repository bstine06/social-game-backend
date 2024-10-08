package com.brettstine.social_game_backend.websocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import com.brettstine.social_game_backend.repository.GameRepository;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class GameStateWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(GameStateWebSocketHandler.class);

    // Store active WebSocket sessions by gameId
    private final Map<String, List<WebSocketSession>> gameSessionsMap = new ConcurrentHashMap<>();

    private final GameRepository gameRepository;

    public GameStateWebSocketHandler(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        String gameId = getGameIdFromSession(session);

        // Check if the game exists directly using GameRepository
        if (!gameRepository.existsById(gameId)) {
            logger.warn("Invalid gameId: {}. Closing WebSocket connection.", gameId);
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        // Store the session under the gameId
        gameSessionsMap.computeIfAbsent(gameId, k -> new ArrayList<>()).add(session);
        logger.info("WebSocket connection established for gameId: {}", gameId); // Log when a connection is established
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        String gameId = getGameIdFromSession(session);
        // Remove the session from the gameId’s session list
        List<WebSocketSession> sessions = gameSessionsMap.getOrDefault(gameId, new ArrayList<>());
        sessions.remove(session);
        if (sessions.isEmpty()) {
            gameSessionsMap.remove(gameId);
        }
        logger.info("WebSocket connection closed for gameId: {}. Close status: {}", gameId, status); // Log when a connection is closed
    }

    // Method to broadcast updates to a specific game
    public void broadcastGameState(String gameId, String gameState) throws IOException {
        List<WebSocketSession> sessions = gameSessionsMap.getOrDefault(gameId, new ArrayList<>());
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(gameState));
            }
        }
    }

    // Helper method to extract the gameId from the WebSocket session’s URL
    private String getGameIdFromSession(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) {
            throw new IllegalArgumentException("Session URI is null. Cannot extract gameId.");
        }

        String query = UriComponentsBuilder.fromUri(uri).build().getQuery();
        if (query == null || !query.contains("=")) {
            throw new IllegalArgumentException("Invalid query string in session URI. Cannot extract gameId.");
        }

        return query.split("=")[1]; // Extracting the gameId assuming the query is like ?gameId=1234
    }

    public void closeConnectionsByGameId(String gameId) {
        List<WebSocketSession> sessions = gameSessionsMap.remove(gameId); // Get and remove the sessions for the gameId
        if (sessions != null) {
            for (WebSocketSession session : sessions) {
                try {
                    session.close(CloseStatus.NORMAL); // Close the session gracefully
                    logger.info("Closed WebSocket connection for gameId: {}", gameId); // Log when a connection is closed
                } catch (IOException e) {
                    logger.error("Error while closing websocket connection for game id: {}", gameId, e);
                }
            }
        }
    }    
}
