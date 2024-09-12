package com.brettstine.social_game_backend.controller;

import com.brettstine.social_game_backend.model.ErrorResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brettstine.social_game_backend.service.StateService;

import java.lang.IllegalStateException;

@RestController
@CrossOrigin(origins = "${frontend.url}")
@RequestMapping("/state")
public class StateController {

    private static final Logger logger = LoggerFactory.getLogger(StateController.class);

    @Autowired
    private StateService stateService;

    @GetMapping("/get-state")
    public ResponseEntity<Map<String, String>> getState() {
        try {
            Map<String, String> state = stateService.getState();
            return ResponseEntity.ok(state);
        } catch (Exception e) {
            logger.error("Error while executing getState", e);
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateState(@RequestBody Map<String, String> payload) {
        String appState = payload.get("appState");
        String gameState = payload.get("gameState");

        logger.info("Received request to update state: appState={}, gameState={}", appState, gameState);

        // Check against the set of expected keys
        Set<String> expectedKeys = Set.of("appState", "gameState");
        if (!payload.keySet().equals(expectedKeys)) {
            return ResponseEntity.status(400).body(new ErrorResponse("Payload contains invalid keys"));
        }

        // Check that expected keys have values
        if (appState == null) {
            return ResponseEntity.status(400).body(new ErrorResponse("'appState' value is not defined"));
        }
        if (gameState == null) {
            return ResponseEntity.status(400).body(new ErrorResponse("'gameState' value is not defined"));
        }
        
        try {
            Map<String, String> response = stateService.updateState(payload);
            logger.info("Successfully updated state to appState={}, gameState={}", appState, gameState);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            logger.error("Error while updating state", e);
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }
}