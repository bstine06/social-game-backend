package com.brettstine.social_game_backend.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

@Service
public class QuestionHelpService {

    private static final String FILE_PATH = "src/main/resources/question_store.txt";
    
    public QuestionHelpService() {
    }

    public List<String> getRandomPrompts() throws IOException {
        Random random = new Random();
        List<String> selectedPrompts = new ArrayList<>();
        int count = 0;

        try (BufferedReader reader = Files.newBufferedReader(Path.of(FILE_PATH))) {
            String line;

            while ((line = reader.readLine()) != null) {
                count++;

                // Reservoir sampling: Randomly decide to keep the current line
                if (count <= 3) {
                    selectedPrompts.add(line);
                } else {
                    int r = random.nextInt(count);
                    if (r < 3) {
                        selectedPrompts.set(r, line);
                    }
                }
            }
        }

        return selectedPrompts;
    }

}
