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

    private final StateModel appStateModel;

    Set<String> validAppStates = new HashSet<>(Arrays.asList("pregame", "game", "postgame"));
    Set<String> validGameStates = new HashSet<>(Arrays.asList("inactive", "asking", "assigning", "answering", "voting", "scoring"));

    @Autowired
    public StateService(StateModel appStateModel) {
        this.appStateModel = appStateModel;
    }

    public Map<String, String> getState() {
        Map<String, String> response = new HashMap<>();
        response.put("appState", appStateModel.getAppState());
        response.put("gameState", appStateModel.getGameState());
        return response;
    }

    public Map<String, String> updateAppState(String newState) throws IllegalStateException {
        if (!validAppStates.contains(newState)) {
            throw new IllegalStateException(newState + " is not a valid appState");
        }
        appStateModel.setAppState(newState);
        return getState();
    }

    public Map<String, String> updateGameState(String newState) throws IllegalStateException{
        if (!validGameStates.contains(newState)) {
            throw new IllegalStateException(newState + " is not a valid gameState");
        }
        appStateModel.setGameState(newState);
        return getState();
    }

    public Map<String, String> updateState(Map<String, String> newState) throws IllegalStateException{
        String newAppState = newState.get("appState");
        String newGameState = newState.get("gameState");
        if (!validAppStates.contains(newAppState)) {
            throw new IllegalStateException(newAppState + " is not a valid appState");
        }
        if (!validGameStates.contains(newGameState)) {
            throw new IllegalStateException(newGameState + " is not a valid gameState");
        }
        appStateModel.setAppState(newAppState);
        appStateModel.setGameState(newGameState);
        return getState();
    }
}
