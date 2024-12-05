package com.brettstine.social_game_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class SanitizationService {

    // Basic HTML escape
    public String sanitizeForHtml(String input) {
        return HtmlUtils.htmlEscape(input);
    }
    
}
