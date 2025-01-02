package com.brettstine.social_game_backend.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brettstine.social_game_backend.service.QuestionHelpService;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
@RequestMapping("/question-help")
public class QuestionHelpController {
    
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    private final QuestionHelpService questionHelpService;

    public QuestionHelpController(QuestionHelpService questionHelpService) {
        this.questionHelpService = questionHelpService;
    }

    @GetMapping()
    public ResponseEntity<?> getQuestionPrompts() {
        try {
            List<String> randomPrompts = questionHelpService.getRandomPrompts();
            return ResponseEntity.ok(randomPrompts);
        } catch (IOException e) {
            logger.error("Error reading the question prompts file");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not get question prompts", "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error retrieving random question prompts");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not get question prompts", "message", e.getMessage()));
        }
    }
    

}
