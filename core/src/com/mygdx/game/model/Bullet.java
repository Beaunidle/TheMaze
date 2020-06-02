package com.mygdx.game.model;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Bullet {

    private Vector2 position = new Vector2();
    private Polygon bounds;
    private float speed;
    private float stateTime = 0;
    private float height = 0.04F;
    private float width = 0.15f;
    private float rotation;
    private float damage;
    private Vector2 velocity;
    private Timer.Task explodeTimer;
    private Timer.Task homingTimer;
    private boolean exploding = false;
    private float explodeTime = 0.5f;
    private final String playerName;
    private boolean explosive = false;
    private final boolean homing;
    private boolean activated;
    private Circle viewCircle;
    private final float rotationSpeed;
    private Player target;

    Bullet(Vector2 position, float rotation, String playerName, float damage, final boolean homing) {
        this.position.x = position.x;
        this.position.y = position.y;
        bounds = new Polygon(new float[]{0, 0, width, height, width, 0, 0, height});
        bounds.setPosition(position.x, position.y);
        this.rotation = rotation;
        this.playerName = playerName;
        this.damage = damage;
        this.homing = homing;
        viewCircle = new Circle(position.x, position.y, 4.5F);
        rotationSpeed = 250;
        speed = isHoming()? 4.5F : 5.5F;
        if (isHoming()) {
            homingTimer =  new Timer.Task() {
                @Override
                public void run() {
                    activated = true;
                    homingTimer.cancel();
                }
            };
            Timer.schedule(homingTimer, 1);
        }
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public float getRotation() {
        return rotation;
    }

    public Polygon getBounds() {
        return bounds;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDamage() {
        return damage;
    }

    public float getStateTime() {
        return stateTime;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public float getExplodeTime() {
        return explodeTime;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isExploding() {
        return exploding;
    }

    public boolean isExplosive() {
        return explosive;
    }

    public boolean isHoming() {
        return homing;
    }

    public boolean isActivated() {
        return activated;
    }

    public Circle getViewCircle() {
        return viewCircle;
    }

    public Player getTarget() {
        return target;
    }

    void setExplosive() {
        this.explosive = true;
    }

    public void update(float delta) {
//		position.add(velocity.x * delta, velocity.y * delta);
        this.bounds.setPosition(position.x, position.y);
        stateTime += delta;
    }

    public void startExplodeTimer() {
        exploding = true;
        explodeTimer =  new Timer.Task() {
            @Override
            public void run() {
                stopExplodeTimer();
            }
        };
        Timer.schedule(explodeTimer, explodeTime, 0);
    }

    private void stopExplodeTimer() {
        exploding = false;
        explodeTimer.cancel();
    }

    public void rotateAntiClockwise(float delta) {
        rotation = rotation + (rotationSpeed * delta);
        if (getRotation() > 360) {
            rotation = rotation - 360;
        }
    }

    public void rotateClockwise(float delta){
        rotation = rotation - (rotationSpeed * delta);
        if (getRotation() < 0) {
            rotation = rotation + 360;
        }
    }

    public void chooseTarget(Player player, List<AIPlayer> aiPlayers) {

        List<Player> players = new ArrayList<>();
        if (!player.getName().equals(getPlayerName()) && viewCircle.contains(player.getCentrePosition())) players.add(player);

        for (AIPlayer aiPlayer : aiPlayers) {
            if (!aiPlayer.getName().equals(getPlayerName())) {
                if (viewCircle.contains(aiPlayer.getCentrePosition()))players.add(aiPlayer);
            }
        }
        if (players.isEmpty()) {
            target = null;
        } else {
            Random rand = new Random();
            if (target != null && rand.nextInt(1000) < 999) return;

            target =  players.get(rand.nextInt(players.size()));
        }
    }
}
