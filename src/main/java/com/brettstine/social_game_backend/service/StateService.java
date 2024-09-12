package com.brettstine.social_game_backend.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.StateModel;

import java.lang.IllegalStateException;

@Service
public class StateService {

    private final StateModel stateModel;

    @Autowired
    public StateService(StateModel stateModel) {
        this.stateModel = stateModel;
    }

    @Autowired
    SessionService sessionService;

    Set<String> validAppStates = new HashSet<>(Arrays.asList("pregame", "game", "postgame"));
    Set<String> validGameStates = new HashSet<>(Arrays.asList("inactive", "asking", "assigning", "answering", "voting", "scoring"));

    public Map<String, String> getState() {
        Map<String, String> response = new HashMap<>();
        response.put("appState", stateModel.getAppState());
        response.put("gameState", stateModel.getGameState());
        return response;
    }

    public Map<String, String> updateAppState(String newState) throws IllegalStateException {
        if (!validAppStates.contains(newState)) {
            throw new IllegalArgumentException(newState + " is not a valid appState");
        }
        stateModel.setAppState(newState);
        return getState();
    }

    public Map<String, String> updateGameState(String newState) throws IllegalStateException{
        if (!validGameStates.contains(newState)) {
            throw new IllegalArgumentException(newState + " is not a valid gameState");
        }
        stateModel.setGameState(newState);
        return getState();
    }

    public Map<String, String> updateState(Map<String, String> newState){
        String newAppState = newState.get("appState");
        String newGameState = newState.get("gameState");
        if (newAppState == null || newGameState == null) {
            throw new IllegalArgumentException("appState or gameState value is missing");
        }
        if (!validAppStates.contains(newAppState)) {
            throw new IllegalArgumentException(newAppState + " is not a valid appState");
        }
        if (!validGameStates.contains(newGameState)) {
            throw new IllegalArgumentException(newGameState + " is not a valid gameState");
        }
        if (newAppState.equals("game") && sessionService.getAllNamedSessions().size() < 3) {
            throw new IllegalStateException("There must be at least 3 names submitted to initialize the game");
        }
        stateModel.setAppState(newAppState);
        stateModel.setGameState(newGameState);
        return getState();
    }
}
