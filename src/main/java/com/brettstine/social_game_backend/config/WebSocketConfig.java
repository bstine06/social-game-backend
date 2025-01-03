package com.brettstine.social_game_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.brettstine.social_game_backend.websocket.GameStateWebSocketHandler;
import com.brettstine.social_game_backend.websocket.WatchPlayersWebSocketHandler;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // Enable UTF-8 and escape non-ASCII characters for better compatibility
        objectMapper.writer(new DefaultPrettyPrinter());
        objectMapper.registerModule(new JavaTimeModule());  // Register Java 8 date/time module
        return objectMapper;
    }

    // Inject the Environment to access frontend url 
    // from application.properties for CORS validation
    private final Environment environment;
    private final GameStateWebSocketHandler gameStateWebSocketHandler;
    private final WatchPlayersWebSocketHandler watchPlayersWebSocketHandler;

    public WebSocketConfig(GameStateWebSocketHandler gameStateWebSocketHandler, WatchPlayersWebSocketHandler watchPlayersWebSocketHandler, Environment environment) {
        this.gameStateWebSocketHandler = gameStateWebSocketHandler;
        this.watchPlayersWebSocketHandler = watchPlayersWebSocketHandler;
        this.environment = environment;
    }

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        // Retrieve the frontend URL from application.properties
        String frontendUrl = environment.getProperty("frontend.url");

        registry.addHandler(gameStateWebSocketHandler, "/game-updates").setAllowedOrigins(frontendUrl);
        registry.addHandler(watchPlayersWebSocketHandler, "/watch-players").setAllowedOrigins(frontendUrl);
    }
}

