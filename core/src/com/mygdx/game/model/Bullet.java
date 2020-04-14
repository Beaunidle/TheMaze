package com.mygdx.game.model;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

public class Bullet {

    private Vector2 position = new Vector2();
    private Polygon bounds;
    private float speed = 5.5f;
    private float stateTime = 0;
    private float height = 0.04F;
    private float width = 0.15f;
    private Vector2 velocity;
    private Timer.Task explodeTimer = new Timer.Task() {
        @Override
        public void run() {
            stopExplodeTimer();
        }
    };
    private boolean exploding = false;
    private float explodeTime = 0.5f;
    private final String playerName;
    private boolean explosive;

    public Bullet(Vector2 position, Vector2 velocity, String playerName) {
        this.position.x = position.x;
        this.position.y = position.y + 0.25F;
        bounds = new Polygon(new float[]{0, 0, width, height, width, 0, 0, height});
        bounds.setPosition(position.x, position.y);
        this.velocity = new Vector2(0,1);
        if (!velocity.equals(new Vector2(0,0))) {
            this.velocity.x = velocity.x;
            this.velocity.y = velocity.y;
        }
        this.playerName = playerName;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
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

    public void setPosition(Vector2 position) {
        this.position = position;
        this.bounds.setPosition(position.x, position.y);
    }

    public boolean isExploding() {
        return exploding;
    }

    public boolean isExplosive() {
        return explosive;
    }

    public void setExplosive(boolean explosive) {
        this.explosive = explosive;
    }

    public void update(float delta) {
//		position.add(velocity.x * delta, velocity.y * delta);
        this.bounds.setPosition(position.x, position.y);
        stateTime += delta;
    }

    public int calcRotate(Vector2 direction) {

        if (direction.x > 0 && direction.y > 0) {
            return 45;
        }
        if (direction.x == 0 && direction.y > 0) {
            return 90;
        }
        if (direction.x < 0 && direction.y > 0) {
            return 135;
        }
        if (direction.x < 0 && direction.y == 0) {
            return 180;
        }
        if (direction.x < 0 && direction.y < 0) {
            return 225;
        }
        if (direction.x == 0 && direction.y < 0) {
            return 270;
        }
        if (direction.x > 0 && direction.y < 0) {
            return 315;
        }
        if (direction.x > 0 && direction.y == 0) {
            return 0;
        }
        return 90;
    }

    public void startExplodeTimer() {
        exploding = true;
        Timer.schedule(explodeTimer, explodeTime, 0);
    }

    private void stopExplodeTimer() {
        exploding = false;
        explodeTimer.cancel();
    }
}
