package com.brettstine.social_game_backend.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import com.brettstine.social_game_backend.service.FetchService;
import com.brettstine.social_game_backend.utils.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Controller
@CrossOrigin(origins = "${frontend.url}")
@RequestMapping("/session")
public class SessionController {
    
    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);

    private final FetchService fetchService;

    public SessionController(FetchService fetchService) {
        this.fetchService = fetchService;
    }

    @GetMapping
    public ResponseEntity<Map<String,String>> getSessionRole(HttpServletRequest request, HttpServletResponse response) {
        
        String optionalHostId = CookieUtil.getDataFromCookie(request, "hostId");
        String optionalPlayerId=CookieUtil.getDataFromCookie(request, "playerId");

        if (optionalPlayerId != null) {
            try {
                fetchService.getPlayerById(optionalPlayerId);
                return ResponseEntity.ok(Map.of("role", "PLAYER"));
            } catch (IllegalArgumentException e) {
                logger.info("Caught non-problematic exception: no player found with id: {}", optionalPlayerId);
                CookieUtil.deleteCookie(response, "playerId");
                logger.info("Deleted stale player cookie with id: {}", optionalPlayerId);
            }
        }

        if (optionalHostId != null) {
            try {
                fetchService.getGameByHostId(optionalHostId);
                return ResponseEntity.ok(Map.of("role", "HOST"));
            } catch (IllegalArgumentException e) {
                logger.info("Caught non-problematic exception: no game found with host id: {}", optionalHostId);
                CookieUtil.deleteCookie(response, "hostId");
                logger.info("Deleted stale host cookie with id: {}", optionalHostId);
            }
        }

        return ResponseEntity.ok(Map.of("role", "UNASSIGNED"));

    }

}
