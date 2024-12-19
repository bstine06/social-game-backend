package com.brettstine.social_game_backend.utils;

import java.security.SecureRandom;

public class GameCodeGenerator {

    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
    private static final int CODE_LENGTH = 4;
    private static final SecureRandom random = new SecureRandom();

    public static String generateGameCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (BadWordFilter.containsBadWord(code));
        return code;
    }

    private static String generateRandomCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHAR_POOL.length());
            code.append(CHAR_POOL.charAt(index));
        }
        return code.toString();
    }
}
