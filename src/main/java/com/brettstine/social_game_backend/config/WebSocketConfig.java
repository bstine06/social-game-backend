package com.brettstine.social_game_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.brettstine.social_game_backend.websocket.GameStateWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    // Inject the Environment to access frontend url 
    // from application.properties for CORS validation
    private final Environment environment;
    private final GameStateWebSocketHandler gameStateWebSocketHandler;

    public WebSocketConfig(GameStateWebSocketHandler gameStateWebSocketHandler, Environment environment) {
        this.gameStateWebSocketHandler = gameStateWebSocketHandler;
        this.environment = environment;
    }

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        // Retrieve the frontend URL from application.properties
        String frontendUrl = environment.getProperty("frontend.url");

        registry.addHandler(gameStateWebSocketHandler, "/game-updates").setAllowedOrigins(frontendUrl);
    }
}

