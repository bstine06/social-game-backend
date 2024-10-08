package com.brettstine.social_game_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.brettstine.social_game_backend.controller.GameStateWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final GameStateWebSocketHandler gameStateWebSocketHandler;

    public WebSocketConfig(GameStateWebSocketHandler gameStateWebSocketHandler) {
        this.gameStateWebSocketHandler = gameStateWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(gameStateWebSocketHandler, "/game-updates").setAllowedOrigins("${frontend.url}");
    }
}

