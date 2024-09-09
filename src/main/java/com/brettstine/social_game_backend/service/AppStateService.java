package com.brettstine.social_game_backend.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.brettstine.social_game_backend.model.AppStateModel;

@Service
public class AppStateService {

    private final AppStateModel appStateModel;

    @Autowired
    public AppStateService(AppStateModel appStateModel) {
        this.appStateModel = appStateModel;
    }

    public ResponseEntity<Map<String, String>> getAppState() {
        Map<String, String> response = new HashMap<>();
        response.put("appState", appStateModel.getAppState());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, String>> update(String newState) {
        appStateModel.setAppState(newState);
        Map<String, String> response = new HashMap<>();
        response.put("appState", appStateModel.getAppState());
        return ResponseEntity.ok(response);
    }
}
