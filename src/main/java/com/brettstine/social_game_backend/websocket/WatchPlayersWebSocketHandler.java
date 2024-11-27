package com.brettstine.social_game_backend.websocket;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import com.brettstine.social_game_backend.service.GameService;
import com.brettstine.social_game_backend.service.PlayerService;
import com.brettstine.social_game_backend.utils.MessageQueue;
import com.brettstine.social_game_backend.dto.PlayerDTO;
import com.brettstine.social_game_backend.dto.WatchPlayersDTO;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class WatchPlayersWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(WatchPlayersWebSocketHandler.class);

    // This object allows serializing the list of players to JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Store active WebSocket queues by gameId
    private final Map<String, List<MessageQueue>> gameQueuesMap = new ConcurrentHashMap<>();

    private final GameService gameService;
    private final PlayerService playerService;

    public WatchPlayersWebSocketHandler(GameService gameService, PlayerService playerService) {
        this.gameService = gameService;
        this.playerService = playerService;
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
        String gameId = getGameIdFromSession(session);
        try {
            GameModel game = gameService.getGameById(gameId);

            // Add the session to the message queue map
            MessageQueue queue = new MessageQueue(session);
            gameQueuesMap.computeIfAbsent(gameId, k -> new ArrayList<>()).add(queue);
            logger.info("WatchPlayers WebSocket connection established for gameId: {}", gameId);

            // Send the initial players' list
            broadcastPlayersList(game);
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
        logger.info("WatchPlayers WebSocket connection closed for gameId: {}. Close status: {}", gameId, status);
    }

    // Method to broadcast the players' list for a specific game
    public void broadcastPlayersList(GameModel game) throws IOException {
        List<PlayerModel> playersInGame = playerService.getAllPlayersByGame(game);
        WatchPlayersDTO watchPlayersDTO = new WatchPlayersDTO();
        playersInGame.stream().forEach(player -> {
            boolean readyStatus = player.isReady();
            watchPlayersDTO.addPlayer(new PlayerDTO(player), readyStatus);
        });

        String message = objectMapper.writeValueAsString(watchPlayersDTO);
        broadcastToAllInGame(game.getGameId(), message);
    }

    private void broadcastToAllInGame(String gameId, String message) {
        // Create a new ArrayList to avoid concurrent modification
        List<MessageQueue> queues = new ArrayList<>(gameQueuesMap.getOrDefault(gameId, Collections.emptyList()));
        for (MessageQueue queue : queues) {
            queue.enqueue(new TextMessage(message));
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
        List<MessageQueue> queues = gameQueuesMap.remove(gameId);
        if (queues != null) {
            for (MessageQueue queue : queues) {
                try {
                    WebSocketSession session = queue.getSession();
                    if (session.isOpen()) {
                        session.close(new CloseStatus(4000, "Game " + gameId + " was deleted."));
                        logger.info("Closed WatchPlayers WebSocket connection for gameId: {}", gameId);
                    }
                } catch (IOException e) {
                    logger.error("Error while closing WebSocket connection for game id: {}", gameId, e);
                }
            }
        }
    }
}
