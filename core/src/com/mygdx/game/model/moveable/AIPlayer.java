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

    public enum Intent {
        SEARCHING, HOMING, KILLING, FLEEING
    }

    private Intent intent = Intent.SEARCHING;
    private Random rand = new Random();
    private Player targetPlayer;
    private String ignore;
    private Vector2 target;
    private final PlayerAi ai;

    public AIPlayer(Vector2 position, String name, RecipeHolder recipeHolder) {
        super(position, name, 30, recipeHolder);
        ai = new PlayerAi();
//        switch ((int)Math.floor(Math.random() * Math.floor(4))) {
//            case 0:
//                this.getGun().setType(Gun.Type.PISTOL);
//                break;
//            case 1:
//                this.getGun().setType(Gun.Type.SMG);
//                break;
//            case 2:
//                this.getGun().setType(Gun.Type.SHOTGUN);
//                break;
//            case 3:
//                this.getGun().setType(Gun.Type.ROCKET);
//                break;
//        }
//        this.getGun().setType(Gun.Type.PISTOL);
    }

    private void switchIntent() {
        if (intent.equals(Intent.SEARCHING)) {
            intent = Intent.KILLING;
        } else if (intent.equals(Intent.KILLING)) {
            intent = Intent.SEARCHING;
        }
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public Vector2 getTarget() {
        return target;
    }

    public com.mygdx.game.model.moveable.Player getTargetPlayer() {
        return targetPlayer;
    }

    public void setTargetPlayer(com.mygdx.game.model.moveable.Player targetPlayer) {
        this.targetPlayer = targetPlayer;
    }

    public void setTarget(Vector2 target) {
        this.target = target;
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
            targetPlayer = null;
            target = null;
            getView().setBlockingWall(new Block[3]);
        } else {
            if (targetPlayer == null || rand.nextInt(1000) > 999)  {
                targetPlayer =  players.get(rand.nextInt(players.size()));
            }
            target = targetPlayer.getCentrePosition();
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
