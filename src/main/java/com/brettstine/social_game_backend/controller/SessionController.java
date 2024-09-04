package com.brettstine.social_game_backend.controller;

import com.brettstine.social_game_backend.model.SessionModel;
import com.brettstine.social_game_backend.service.SessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
public class SessionController {

    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/set-session")
    public ResponseEntity<Map<String, String>> setSession(HttpServletResponse response) {
        SessionModel session = sessionService.createSession();
        String sessionId = session.getSessionId();
        
        Cookie cookie = new Cookie("sessionId", sessionId);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        // cookie.setMaxAge(5); // 5 seconds. Use only for debugging
        cookie.setMaxAge(60 * 60); // 1 hour

        cookie.setSecure(false); // Use this line for HTTP requests only

        /* USE THESE LINES FOR HTTPS */
        //cookie.setSecure(true);// Ensure cookie is only sent over HTTPS
        //cookie.setDomain(""); // Set to your backend IP/domain
        //cookie.setAttribute("SameSite", "None"); // Allow cross-site requests

        response.addCookie(cookie);
        logger.info("Session cookie set with ID: {}", sessionId);

        // Create a JSON response with session information
        return ResponseEntity.ok(Map.of("message", "Session cookie set with ID: " + sessionId));
    }

    @GetMapping("/get-session")
    public ResponseEntity<Map<String, String>> getSession(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("sessionId".equals(cookie.getName())) {
                    String sessionId = cookie.getValue();
                    SessionModel session = sessionService.getSession(sessionId);
                    if (session != null) {
                        // Return session information as JSON
                        return ResponseEntity.ok(Map.of(
                            "sessionId", session.getSessionId(),
                            "sessionData", session.getSessionData()
                        ));
                    }
                }
            }
        }

        // If no valid session ID was found, create a new session and return the response
        return setSession(response);
    }
}
