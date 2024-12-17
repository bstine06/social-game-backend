package com.brettstine.social_game_backend.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class BadWordFilter {
    private static final Set<String> BANNED_WORDS = new HashSet<>();

    static {
        try (InputStream inputStream = BadWordFilter.class.getClassLoader().getResourceAsStream("banned-words.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            while ((line = reader.readLine()) != null) {
                BANNED_WORDS.add(normalizeString(line.trim().toLowerCase())); // Normalize
            }
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Failed to load banned words list", e);
        }
    }

    // Normalize similar-looking characters
    private static String normalizeString(String input) {
        return input.replace("1", "i")
                    .replace("3", "e")
                    .replace("5", "s")
                    .replace("0", "o");
    }

    public static boolean containsBadWord(String code) {
        // Normalize the code before checking against banned words
        return BANNED_WORDS.contains(normalizeString(code));
    }
}
