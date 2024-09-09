package com.brettstine.social_game_backend.controller;

import com.brettstine.social_game_backend.model.ErrorResponse;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brettstine.social_game_backend.service.AppStateService;;

@RestController
@CrossOrigin(origins = "${frontend.url}")
@RequestMapping("/appstate")
public class AppStateController {

    @Autowired
    private AppStateService appStateService;

    @GetMapping("/get-appstate")
    public ResponseEntity<Map<String, String>> getAppState() {
        return appStateService.getAppState();
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateAppState(@RequestBody Map<String, String> payload) {
        String appState = payload.get("appState");

        if (appState == null) {
          return ResponseEntity.status(400).body(new ErrorResponse("'appState' value is not defined"));
        }

        return appStateService.update(appState);
    }
}