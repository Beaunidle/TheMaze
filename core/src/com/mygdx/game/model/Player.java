package com.mygdx.game.model;

import com.badlogic.gdx.math.Polygon;
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

    private Vector2 position;
    private Float acceleration;
    private Vector2 velocity = new Vector2();
    private Polygon bounds;
    private State state = State.IDLE;
    private float rotation;
    private final float rotationSpeed;
    private float stateTime = 0;
    private final String name;
    private int lives;
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

    public Player(Vector2 position, String name, int lives) {
        this.position = position;
        bounds = new Polygon(new float[]{0, 0 ,WIDTH, HEIGHT ,WIDTH ,0, 0,HEIGHT});
        bounds.setPosition(position.x, position.y);
        bounds.setOrigin(WIDTH/2, HEIGHT/2);
        rotation = 45;
        rotationSpeed = 3.5F;
        this.name = name;
        this.lives = lives;
        gun = new Gun(Gun.Type.PISTOL);
    }

    public Vector2 getPosition() {
        return position;
    }

    public Float getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Float acceleration) {
        this.acceleration = acceleration;
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

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getRotationSpeed() {
        return rotationSpeed;
    }

    public String getName() {
        return name;
    }

    public int getLives() {
        return lives;
    }

//    public void setLives(int lives) {
//        this.lives = lives;
//    }

//    public void setPosition(Vector2 position) {
//        this.position = position;
//        this.bounds.setPosition(position.x, position.y);
//    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

//    public void setBounds(Polygon bounds) {
//        this.bounds = bounds;
//    }

    public Gun getGun() {
        return gun;
    }

    public boolean isInjured() {
        return injured;
    }

    public List<Bullet> fireBullet() {
        List<Bullet> bullets = new ArrayList<>();
        if (!bulletTimerOn) {
            startBulletTimer(gun.getFiringRate() * 0.25F);
            bullets.addAll(gun.fire(position, rotation, WIDTH, HEIGHT, name));
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

    public void isShot(String name, float damage) {
        if (!injured) {
            injuredTimer = new Timer.Task() {
                @Override
                public void run() {
                    stopInjureTimer();
                }
            };
            injured = true;
            lives = lives - (int)Math.floor(damage);
            if (lives <= 0) {
                dead();
                System.out.println(this.getName() + " killed by " + name);
                return;
            }
            Timer.schedule(injuredTimer, 0.5F, 1.5f);
        }
    }

    private void dead() {
        setState(State.DEAD);
    }

    public void update(float delta) {
//		position.add(velocity.x * delta, velocity.y * delta);
//		bounds.x = position.x;
//		bounds.y = position.y;
        stateTime += delta;
    }
}
