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
import com.brettstine.social_game_backend.dto.PlayerDTO;
import com.brettstine.social_game_backend.dto.WatchPlayersDTO;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.util.Map; 
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class WatchPlayersWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(WatchPlayersWebSocketHandler.class);

    // This object allows serializing the list of players to JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Store active WebSocket sessions by gameId
    private final Map<String, List<WebSocketSession>> gameSessionsMap = new ConcurrentHashMap<>();

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
            // Attempt to retrieve the game with the provided ID
            GameModel game = gameService.getGameById(gameId);
            
            // Store the websocket session under the gameId
            gameSessionsMap.computeIfAbsent(gameId, k -> new ArrayList<>()).add(session);
            logger.info("WatchPlayers WebSocket connection established for gameId: {}", gameId);
            
            // Immediately send the players' names list as soon as websocket is created
            broadcastPlayersList(game);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid gameId: {}. Closing WebSocket connection.", gameId);
            session.close(CloseStatus.BAD_DATA);
        }
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
        logger.info("WatchPlayers WebSocket connection closed for gameId: {}. Close status: {}", gameId, status); // Log when a connection is closed
    }

    // Method to broadcast player list to a specific game
    public void broadcastPlayersList(GameModel game) throws IOException {
        List<PlayerModel> playersInGame = playerService.getAllPlayersByGame(game);
        WatchPlayersDTO watchPlayersDTO = new WatchPlayersDTO();
        playersInGame.stream()
                .forEach(p -> {
                    boolean readyStatus = p.isReady();
                    watchPlayersDTO.addPlayer(new PlayerDTO(p), readyStatus);
                });
        List<WebSocketSession> sessions = gameSessionsMap.getOrDefault(game.getGameId(), new ArrayList<>());
        String message = objectMapper.writeValueAsString(watchPlayersDTO);  // Convert list to JSON
        logger.info("WatchPlayers WebSocket: broadcasting players for gameId: {}", game.getGameId());
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
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
                    CloseStatus closeStatus = new CloseStatus(4000, "Game " + gameId + " was deleted.");  // Custom close code and reason
                    session.close(closeStatus);
                    logger.info("Closed WatchPlayers WebSocket connection for gameId: {}", gameId); // Log when a connection is closed
                } catch (IOException e) {
                    logger.error("Error while closing websocket connection for game id: {}", gameId, e);
                }
            }
        }
    }    
}