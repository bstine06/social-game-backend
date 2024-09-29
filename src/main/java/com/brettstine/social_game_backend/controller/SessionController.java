package com.brettstine.social_game_backend.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.GameState;
import com.brettstine.social_game_backend.model.Role;
import com.brettstine.social_game_backend.model.SessionModel;
import com.brettstine.social_game_backend.service.FetchService;
import com.brettstine.social_game_backend.service.GameFlowService;
import com.brettstine.social_game_backend.service.SessionService;
import com.brettstine.social_game_backend.service.ValidationService;
import com.brettstine.social_game_backend.utils.CookieUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
@RequestMapping("/session")
public class SessionController {

    private static final Logger logger = LoggerFactory.getLogger(SessionController.class);

    private final SessionService sessionService;
    private final ValidationService validationService;
    private final FetchService fetchService;
    private final GameFlowService gameFlowService;

    public SessionController(SessionService sessionService, ValidationService validationService, FetchService fetchService, GameFlowService gameFlowService) {
        this.sessionService = sessionService;
        this.validationService = validationService;
        this.fetchService = fetchService;
        this.gameFlowService = gameFlowService;
    }
    
    @GetMapping
    public ResponseEntity<?> getSession(HttpServletRequest request, HttpServletResponse response) {

        String sessionId = CookieUtil.getDataFromCookie(request, "sessionId");
        try {
            if (sessionId == null) {
                return setSession(response);
            } else {
                SessionModel session = sessionService.getSessionById(sessionId);
                // delete session cookie & set a new one if session id doesn't exist in DB
                if (session == null) {
                    CookieUtil.deleteCookie(response, "sessionId");
                    return setSession(response);
                }

                return ResponseEntity.ok(session);
            }
        } catch (Exception e) {
            logger.error("Error while getting session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "could not get session", "message", e.getMessage()));
        }

    }

    private ResponseEntity<?> setSession(HttpServletResponse response) {

        try {
            SessionModel session = sessionService.createSession();
            String sessionId = session.getSessionId();
            logger.info("Session created with ID: {}, name: {}", sessionId);

            Cookie cookie = new Cookie("sessionId", sessionId);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60); // 1 hour
            cookie.setSecure(false); // Use this line for HTTP requests only
            response.addCookie(cookie);

            logger.info("Session cookie set with ID: {}", sessionId);

            return ResponseEntity.ok(session);
        } catch (Exception e) {
            logger.error("Error while setting session", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "could not set session", "message", e.getMessage()));
        }

    }

    @PatchMapping("/{gameId}/make-player")
    public ResponseEntity<?> makePlayerAndJoinGame(HttpServletRequest request, @PathVariable String gameId) {
        String sessionId = CookieUtil.getDataFromCookie(request, "sessionId");

        try {
            GameModel game = fetchService.getGameById(gameId);
            validationService.ensureGameState(game, GameState.LOBBY);
            sessionService.updateRoleOfSession(sessionId, Role.PLAYER);
            sessionService.assignGameToSession(sessionId, game);
            SessionModel session = sessionService.getSessionById(sessionId);
            return ResponseEntity.ok(session);
        } catch (IllegalArgumentException e) {
            logger.error("Error adding session to game", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "could not update session", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error adding session to game", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "could not update session", "message", e.getMessage()));
        }
    }

    @PatchMapping("/host")
    public ResponseEntity<?> makeHostAndStartGame(HttpServletRequest request) {
        String sessionId = CookieUtil.getDataFromCookie(request, "sessionId");

        try {
            sessionService.updateRoleOfSession(sessionId, Role.HOST);
            SessionModel session = sessionService.getSessionById(sessionId);
            GameModel game = gameFlowService.createGame(session);
            sessionService.assignGameToSession(sessionId, game);
            session = sessionService.getSessionById(sessionId);
            return ResponseEntity.ok(session);
        } catch (IllegalArgumentException e) {
            logger.error("Error adding session to game", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "could not update session", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error adding session to game", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "could not update session", "message", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllSessions() {
        try {
            List<SessionModel> sessions = sessionService.getAllSessions();
            return ResponseEntity.ok(sessions);
        } catch (Exception e) {
            logger.error("Error retrieving all sessions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "could not retrieve all sessions", "message", e.getMessage()));
        }
    }
    
    

}
