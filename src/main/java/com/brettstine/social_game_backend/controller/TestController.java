package com.brettstine.social_game_backend.controller;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@CrossOrigin(origins = "http://192.168.4.98:3000", allowCredentials = "true")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);
    private static final Map<String, String> sessions = new ConcurrentHashMap<>();

    @GetMapping("/set-session")
    public ResponseEntity<String> setSession(HttpServletResponse response) {
        String sessionId = UUID.randomUUID().toString();
        Cookie cookie = new Cookie("sessionId", sessionId);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        cookie.setMaxAge(5); // 5 seconds. Use only for debugging
        // cookie.setMaxAge(60 * 60); // 1 hour

        cookie.setSecure(false); // Use this line for HTTP requests only

        /* USE THESE LINES FOR HTTPS */
        //cookie.setSecure(true);// Ensure cookie is only sent over HTTPS
        //cookie.setDomain(""); // Set to your backend IP/domain
        //cookie.setAttribute("SameSite", "None"); // Allow cross-site requests

        response.addCookie(cookie);
        sessions.put(sessionId, "Some session data");

        logger.info("Session cookie set with ID: {}", sessionId);
        return ResponseEntity.ok("Session cookie set with ID: " + sessionId);
    }

    @GetMapping("/get-session")
    public ResponseEntity<String> getSession(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                logger.info("Cookie found: Name = {}, Value = {}", cookie.getName(), cookie.getValue());
                if ("sessionId".equals(cookie.getName())) {
                    String sessionId = cookie.getValue();
                    String sessionData = sessions.get(sessionId);
                    logger.info("Session ID found: {}", sessionId);
                    if (sessionData != null) {
                        return ResponseEntity.ok("Session ID: " + sessionId + ", Data: " + sessionData);
                    }
                }
            }
        }

        // If no valid session ID was found, create a new session
        logger.info("No valid session ID found, creating a new session");
        return setSession(response);
    }
}
