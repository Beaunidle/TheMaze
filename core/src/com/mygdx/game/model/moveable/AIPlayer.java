package com.mygdx.game.model.moveable;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.ai.PlayerAi;
import com.mygdx.game.model.environment.blocks.Block;
import com.mygdx.game.utils.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AIPlayer extends Player {

    private Random rand = new Random();
    private String ignore;
    private final PlayerAi ai;

    public AIPlayer(Vector2 position, String name, RecipeHolder recipeHolder, int houseNumber) {
        super(position, name, 30, recipeHolder, 0, new ArrayList<>());
        ai = new PlayerAi();
    }

    public String getIgnore() {
        return ignore;
    }

    public void setIgnore(String ignore) {
        this.ignore = ignore;
    }

    public void chooseTarget(com.mygdx.game.model.moveable.Player player, List<AIPlayer> aiPlayers) {

        List<com.mygdx.game.model.moveable.Player> players = new ArrayList<>();
        if ((!player.getName().equals(ignore)) && getViewCircle().contains(player.getCentrePosition())) players.add(player);

        for (AIPlayer aiPlayer : aiPlayers) {
            if (!aiPlayer.getName().equals(getName()) && !aiPlayer.getName().equals(ignore)) {
                if (getViewCircle().contains(aiPlayer.getCentrePosition()))players.add(aiPlayer);
            }
        }
        if (players.isEmpty()) {
            setTargetSprite(null);
            setTarget(null);
            getView().setBlockingWall(new Block[3]);
        } else {
            if (getTargetSprite() == null || rand.nextInt(1000) > 999)  {
                setTargetSprite(players.get(rand.nextInt(players.size())));
            }
            setTarget(getTargetSprite().getCentrePosition());
        }
    }

    public void ignore(String name) {
        ignore = name;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                ignore = null;
            }
        }, 5);
    }

    public List<Projectile> decide(float delta) {
        return ai.decide(delta, this);
    }

    public void respawn(Vector2 newPos) {
        this.setTarget(null);
        super.respawn(newPos);
    }
}
