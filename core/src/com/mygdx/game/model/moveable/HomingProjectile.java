package com.mygdx.game.model.moveable;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class HomingProjectile extends Projectile{

//    private Timer.Task homingTimer;
//    private Sprite target;
//    private boolean activated;
//    private final Circle viewCircle;

    public HomingProjectile(Vector2 position, float rotation, String playerName, float damage, ProjectileType type, float addedMommentum) {
        super(position, rotation, playerName, damage, type, addedMommentum, false);
//        this.homing = true;

        setSpeed(5.5F);
//        if (isHoming()) {
//            homingTimer =  new Timer.Task() {
//                @Override
//                public void run() {
//                    activated = true;
//                    homingTimer.cancel();
//                }
//            };
//            Timer.schedule(homingTimer, 1);
//        }
//        viewCircle = new Circle(position.x, position.y, 4.5F);
    }

//    public boolean isActivated() {
//        return activated;
//    }
//
//    public Sprite getTarget() {
//        return target;
//    }
//
//    public Circle getViewCircle() {
//        return viewCircle;
//    }
//
//    public void chooseTarget(Player player, Collection<? extends Sprite> sprites) {
//
//        List<Sprite> targetSprites = new ArrayList<>();
//        if (!player.getName().equals(getPlayerName()) && getViewCircle().contains(player.getCentrePosition())) targetSprites.add(player);
//
//        for (Sprite sprite : sprites) {
//            if (!sprite.getName().equals(getPlayerName())) {
//                if (getViewCircle().contains(sprite.getCentrePosition()))targetSprites.add(sprite);
//            }
//        }
//        if (targetSprites.isEmpty()) {
//            target = null;
//        } else {
//            Random rand = new Random();
//            if (target != null && rand.nextInt(1000) < 999) return;
//
//            target =  targetSprites.get(rand.nextInt(targetSprites.size()));
//        }
//    }
}
