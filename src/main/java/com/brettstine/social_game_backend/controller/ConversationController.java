package com.brettstine.social_game_backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.brettstine.social_game_backend.model.ConversationModel;
import com.brettstine.social_game_backend.service.ConversationService;

@RestController
@CrossOrigin(origins = "${frontend.url}", allowCredentials = "true")
@RequestMapping("/conversation")
public class ConversationController {

  private final ConversationService conversationService;

  public ConversationController(ConversationService conversationService) {
    this.conversationService = conversationService;
  }
  
  @GetMapping("/get")
  public ResponseEntity<?> getConversationModelFromId(@RequestBody Map<String, String> payload) {
    String conversationId = payload.get("conversationId");
    ConversationModel conversationModel = conversationService.getConversationModelFromId(conversationId);
    return ResponseEntity.ok(conversationModel);
  }

  @GetMapping("/get-answers-for-question")
  public ResponseEntity<?> getConversationModel(@RequestBody Map<String, String> payload) {
    String questionId = payload.get("questionId");
    List<ConversationModel> answerModels = conversationService.getAnswersForQuestion(questionId);
    return ResponseEntity.ok(answerModels);
  }

}
