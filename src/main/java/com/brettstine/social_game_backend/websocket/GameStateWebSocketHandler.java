package com.brettstine.social_game_backend.websocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import com.brettstine.social_game_backend.dto.GameStateDTO;
import com.brettstine.social_game_backend.model.GameDeletionReason;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.service.GameService;
import com.brettstine.social_game_backend.utils.MessageQueue;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class GameStateWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(GameStateWebSocketHandler.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, List<MessageQueue>> gameQueuesMap = new ConcurrentHashMap<>();
    private final GameService gameService;

    public GameStateWebSocketHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        String gameId = getGameIdFromSession(session);
        try {
            GameModel game = gameService.getGameById(gameId);

            // Add the session to the message queue map
            MessageQueue queue = new MessageQueue(session);
            gameQueuesMap.computeIfAbsent(gameId, k -> new ArrayList<>()).add(queue);
            logger.info("GameState WebSocket connection established for gameId: {}", gameId);

            // Immediately send the game state
            broadcastGameState(game);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid gameId: {}. Closing WebSocket connection.", gameId);
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) throws Exception {
        String gameId = getGameIdFromSession(session);
        List<MessageQueue> queues = gameQueuesMap.getOrDefault(gameId, new ArrayList<>());

        // Remove the corresponding MessageQueue for this session
        synchronized (queues) {
            queues.removeIf(queue -> queue.getSession().equals(session));
            if (queues.isEmpty()) {
                gameQueuesMap.remove(gameId);
            }
        }
        logger.info("GameState WebSocket connection closed for gameId: {}. Close status: {}", gameId, status);
    }

    public void broadcastGameState(GameModel game) throws IOException {
        String message = objectMapper.writeValueAsString(new GameStateDTO(
            game.getGameId(),
            game.getGameState().toString(),
            game.getTimerEnd() != null ? game.getTimerEnd().toString() : null
        ));
        broadcastToAllInGame(game.getGameId(), message);
    }

    public void closeConnectionsByGameId(String gameId, GameDeletionReason reason) {
        try {
            String message = objectMapper.writeValueAsString(new GameStateDTO(
                gameId,
                reason.toString(),
                null
            ));
            // Notify all clients in the game about the deletion reason
            broadcastToAllInGame(gameId, message);
        } catch (Exception e) {
            logger.error("Error while broadcasting game deletion reason for game id: {}", gameId, e);
        }
    
        // Close all WebSocket sessions associated with this game
        List<MessageQueue> queues = gameQueuesMap.remove(gameId);
        if (queues != null) {
            for (MessageQueue queue : queues) {
                try {
                    WebSocketSession session = queue.getSession();
                    if (session.isOpen()) {
                        session.close(new CloseStatus(4000, "Game " + gameId + " was deleted."));
                        logger.info("Closed GameState WebSocket connection for gameId: {}", gameId);
                    }
                } catch (IOException e) {
                    logger.error("Error while closing WebSocket connection for game id: {}", gameId, e);
                }
            }
        }
    }
    

    private void broadcastToAllInGame(String gameId, String message) {
        // Create a new ArrayList to avoid concurrent modification
        List<MessageQueue> queues = new ArrayList<>(gameQueuesMap.getOrDefault(gameId, Collections.emptyList()));
        for (MessageQueue queue : queues) {
            queue.enqueue(new TextMessage(message));
        }
    }

    private String getGameIdFromSession(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null) {
            throw new IllegalArgumentException("Session URI is null. Cannot extract gameId.");
        }

        String query = UriComponentsBuilder.fromUri(uri).build().getQuery();
        if (query == null || !query.contains("=")) {
            throw new IllegalArgumentException("Invalid query string in session URI. Cannot extract gameId.");
        }

        return query.split("=")[1];
    }
}
