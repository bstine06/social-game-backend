package com.brettstine.social_game_backend.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brettstine.social_game_backend.model.AnswerModel;
import com.brettstine.social_game_backend.model.GameModel;
import com.brettstine.social_game_backend.model.GameState;
import com.brettstine.social_game_backend.model.PlayerModel;
import com.brettstine.social_game_backend.model.QuestionModel;
import com.brettstine.social_game_backend.service.FetchService;
import com.brettstine.social_game_backend.service.GameFlowService;
import com.brettstine.social_game_backend.service.ValidationService;
import com.brettstine.social_game_backend.service.VoteService;
import com.brettstine.social_game_backend.utils.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
@RequestMapping("/vote")
public class VoteController {

    private static final Logger logger = LoggerFactory.getLogger(VoteController.class);
    
    private final VoteService voteService;
    private final FetchService fetchService;
    private final ValidationService validationService;
    private final GameFlowService gameFlowService;

    public VoteController(VoteService voteService, FetchService fetchService, ValidationService validationService, GameFlowService gameFlowService) {
        this.voteService = voteService;
        this.fetchService = fetchService;
        this.validationService = validationService;
        this.gameFlowService = gameFlowService;
    }

    @PostMapping("/submit-vote")
    public ResponseEntity<?> submitQuestion(HttpServletRequest request, @RequestBody Map<String, String> payload) {
        String gameId = payload.get("gameId");
        String answerId = payload.get("answerId");
        String playerId = CookieUtil.getDataFromCookie(request, "playerId");
        try {
            GameModel game = fetchService.getGameById(gameId);
            PlayerModel player = fetchService.getPlayerById(playerId);
            AnswerModel answer = fetchService.getAnswerById(answerId);
            validationService.ensureGameState(game, GameState.QUESTION);
            validationService.validatePlayerCanSubmitQuestion(player);
            voteService.submitVote(game, player, answer);
            logger.info("Game: {} : Successfully submitted vote from player with id: {}", gameId, player.getPlayerId());
            gameFlowService.tryAdvanceGameState(game);
            return ResponseEntity.ok(Map.of("success", "vote submitted"));
        } catch (IllegalArgumentException e) {
            logger.error("Game: {} : Error submitting question", gameId, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "error submitting question", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Game: {} : Error submitting question", gameId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "error submitting question", "message", e.getMessage()));
        }
    }


}
