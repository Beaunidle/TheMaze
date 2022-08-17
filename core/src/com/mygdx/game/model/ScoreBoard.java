package com.mygdx.game.model;

import com.mygdx.game.model.moveable.AIPlayer;
import com.mygdx.game.model.moveable.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreBoard {

    private Map<String, PlayerScore> scores;

    public ScoreBoard(Player player, List<com.mygdx.game.model.moveable.AIPlayer> aiPlayers) {
        scores = new HashMap<>();
        scores.put(player.getName(), new PlayerScore(player.getName()));
        for (AIPlayer aiPlayer : aiPlayers) {
            scores.put(aiPlayer.getName(), new PlayerScore(aiPlayer.getName()));
        }
        scores.put("explosion", new PlayerScore("explosion"));
        scores.put("Spike Floor", new PlayerScore("Spike Floor"));

    }

    public void addDeath(String name) {
        scores.get(name).addDeath();
    }

    public void addKill(String name) {
        scores.get(name).addKill();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (PlayerScore ps : scores.values()) {
            sb.append(ps.name).append(", Kills: ").append(ps.kills).append(". Deaths: ").append(ps.deaths).append("\n");
        }
       return sb.toString();
    }

    private class PlayerScore {
        String name;
        int kills;
        int deaths;

        private PlayerScore(String name) {
            this.name = name;
            this.kills = 0;
            this.deaths = 0;
        }

        void addDeath() {
            deaths++;
        }

        void addKill() {
            kills++;
        }
    }
}
