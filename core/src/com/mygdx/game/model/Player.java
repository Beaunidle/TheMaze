package com.mygdx.game.model;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.List;

public class Player {

    public enum State {
        IDLE, MOVING, DEAD
    }

    public static final float HEIGHT = 0.9F;
    public static final float WIDTH = HEIGHT/2;

    private Vector2 position = new Vector2();
    Vector2 acceleration = new Vector2();
    Vector2 velocity = new Vector2();
    Polygon bounds = new Polygon();
    State	state = State.IDLE;
    Vector2  direction;
    float	stateTime = 0;
    private final String name;
    private int lives = 5;
    private Gun gun;

    private Timer.Task bulletTimer = new Timer.Task() {
        @Override
        public void run() {
            stopBulletTimer();
        }
    };
    private Timer.Task injuredTimer;

    private boolean bulletTimerOn;
    private boolean injured;

    public Player(Vector2 position, String name) {
        this.position = position;
        bounds = new Polygon(new float[]{0, 0 ,WIDTH, HEIGHT ,WIDTH ,0, 0,HEIGHT});
        bounds.setPosition(position.x, position.y);
        bounds.setOrigin(WIDTH/2, HEIGHT/2);
        direction = new Vector2(1,0);
        this.name = name;
        gun = new Gun(Gun.Type.PISTOL);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getAcceleration() {
        return acceleration;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Polygon getBounds() {
        return bounds;
    }

    public State getState() {
        return state;
    }

    public void setState(State newState) {
        this.state = newState;
    }

    public float getStateTime() {
        return stateTime;
    }

    public Vector2 getDirection() {
        return direction;
    }

    public String getName() {
        return name;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public void setDirection(Vector2 direction) {
        this.direction = direction;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
        this.bounds.setPosition(position.x, position.y);
    }

    public void setAcceleration(Vector2 acceleration) {
        this.acceleration = acceleration;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public void setBounds(Polygon bounds) {
        this.bounds = bounds;
    }

    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
    }

    public Gun getGun() {
        return gun;
    }

    public boolean isInjured() {
        return injured;
    }

    public List<Bullet> fireBullet(float delta) {
        List<Bullet> bullets = new ArrayList<>();
        if (!bulletTimerOn) {
            startBulletTimer(gun.getFiringRate() * 0.25F);
            bullets.addAll(gun.fire(position, direction, WIDTH, HEIGHT, name));
        }
        return bullets;
    }

    private void startBulletTimer(float delay) {
        bulletTimerOn = true;
        Timer.schedule(bulletTimer, delay, delay);
    }

    private void stopBulletTimer() {
        bulletTimerOn = false;
        bulletTimer.cancel();
    }

    private void stopInjureTimer() {
        injuredTimer.cancel();
        injured = false;
    }

    public void isShot(float delta, String name) {
        if (!injured) {
            injuredTimer = new Timer.Task() {
                @Override
                public void run() {
                    stopInjureTimer();
                }
            };
            injured = true;
            lives --;
            if (lives == 0) {
                dead();
                System.out.println(this.getName() + " killed by " + name);
                return;
            }
            Timer.schedule(injuredTimer, 1.5F, 1.5f);
        }
    }

    public void dead() {
        setState(State.DEAD);
    }

    public void update(float delta) {
//		position.add(velocity.x * delta, velocity.y * delta);
//		bounds.x = position.x;
//		bounds.y = position.y;
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
}
