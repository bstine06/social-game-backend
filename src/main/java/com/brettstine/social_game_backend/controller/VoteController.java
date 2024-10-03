package com.brettstine.social_game_backend.controller;

import java.util.Map;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brettstine.social_game_backend.dto.BallotDTO;
import com.brettstine.social_game_backend.model.AnswerModel;
import com.brettstine.social_game_backend.model.QuestionModel;
import com.brettstine.social_game_backend.model.VotingStatus;
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
import jakarta.ws.rs.core.Response;

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
    public ResponseEntity<?> submitVote(HttpServletRequest request, @RequestBody Map<String, String> payload) {
        String answerId = payload.get("answerId");
        String playerId = CookieUtil.getDataFromCookie(request, "playerId");
        if (playerId == null || playerId.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "error submitting question", "message", "Player Id was not given"));
        }
        try {
            PlayerModel player = fetchService.getPlayerById(playerId);
            AnswerModel answer = fetchService.getAnswerById(answerId);
            GameModel game = player.getGame();
            validationService.validatePlayer(player, game);
            validationService.ensureGameState(game, GameState.VOTE);
            validationService.ensureVotingIsInProgress(answer.getQuestion());
            validationService.ensurePlayerIsntActivelyCompeting(player, answer.getQuestion());
            voteService.submitVote(game, player, answer);
            gameFlowService.grantPointForVote(answer);
            logger.info("Game: {} : Successfully submitted vote from player with id: {}", game.getGameId(), player.getPlayerId());
            gameFlowService.tryAdvanceGameState(game);
            return ResponseEntity.ok(Map.of("success", "vote submitted"));
        } catch (IllegalArgumentException e) {
            logger.error("Error submitting vote", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "error submitting question", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error submitting vote", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "error submitting question", "message", e.getMessage()));
        }
    }

    @GetMapping("/{gameId}/get-current-question")
    public ResponseEntity<?> getCurrentQuestion(@PathVariable String gameId) {
        try {
            GameModel game = fetchService.getGameById(gameId);
            QuestionModel currentQuestion = voteService.getCurrentQuestion(game); 
            return ResponseEntity.ok(currentQuestion);
        } catch (IllegalStateException e) {
            logger.error("Game: {} : Error retreiving current question", gameId, e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "error retreiving current question", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Game: {} : Error retreiving current question", gameId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "error retreiving current question", "message", e.getMessage()));
        }
    }

    @GetMapping("/{gameId}/get-current-ballot")
    public ResponseEntity<?> getCurrentBallot(@PathVariable String gameId) {
        try {
            GameModel game = fetchService.getGameById(gameId);
            BallotDTO currentBallot = voteService.getCurrentBallot(game); 
            return ResponseEntity.ok(currentBallot);
        } catch (IllegalStateException e) {
            logger.error("Game: {} : Error retreiving current ballot", gameId, e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "error retreiving current ballot", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Game: {} : Error retreiving current ballot", gameId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "error retreiving current ballot", "message", e.getMessage()));
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllQuestions() {
        try {
            List<QuestionModel> questions = voteService.getAllQuestions();
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            logger.error("Error retreiving all questions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "error retreiving all questions", "message", e.getMessage()));
        }
    }


}
