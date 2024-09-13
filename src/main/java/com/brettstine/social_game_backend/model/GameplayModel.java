package com.brettstine.social_game_backend.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameplayModel {

    private List<PlayerModel> players = new ArrayList<>();

    public List<PlayerModel> getPlayers() {
        return players;
    }

    public void addPlayer(PlayerModel player) {
        players.add(player);
    }

    public Optional<PlayerModel> findPlayerBySessionId(String sessionId) {
        return players.stream()
                .filter(player -> player.getSessionId().equals(sessionId))
                .findFirst();
    }

    public boolean allPlayersHaveSubmittedQuestions() {
        return players.stream().allMatch(p -> p.getSubmittedQuestionId() != null);
    }

    public void assignQuestionsToPlayers() {
        int numPlayers = players.size();
        for (int i = 0; i < numPlayers; i++) {
            PlayerModel currentPlayer = players.get(i);
            PlayerModel nextPlayer = players.get((i + 1) % numPlayers);
            PlayerModel prevPlayer = players.get((i + numPlayers - 1) % numPlayers);

            // Each player's question will be added to two other players' list of questions to answer
            nextPlayer.addQuestionIdToAnswer(currentPlayer.getSubmittedQuestionId());
            prevPlayer.addQuestionIdToAnswer(currentPlayer.getSubmittedQuestionId());
        }
    }
}
