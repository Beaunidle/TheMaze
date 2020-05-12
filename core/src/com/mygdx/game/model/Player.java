package com.mygdx.game.model;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.List;

public class Player {

    public enum State {
        IDLE, MOVING, DEAD
    }

    public enum Boost {
        HOMING, SPEED, DAMAGE, SHIELD, NOTHING;
    }
    public static final float HEIGHT = 0.9F;
    public static final float WIDTH = HEIGHT;

    private Vector2 position;
    private Float acceleration;
    private Vector2 velocity = new Vector2();
    private Polygon bounds;
    private State state = State.IDLE;
    private Boost boost;
    private float rotation;
    private final float rotationSpeed;
    private float stateTime = 0;
    private final String name;
    private int lives;
    private int maxLives;
    private Gun gun;
    private Circle viewCircle, shieldCircle;

    private Timer.Task bulletTimer = new Timer.Task() {
        @Override
        public void run() {
            stopBulletTimer();
        }
    };
    private Timer.Task injuredTimer;
    private Timer.Task healTimer;
    private Timer.Task boostTimer;

    private boolean bulletTimerOn;
    private boolean injured, healing;
    private boolean turningAntiClockwise = false, turningClcokwise = false, moveForward = false, moveBackward = false;

    Player(Vector2 position, String name, int lives) {
        this.position = position;
        bounds = new Polygon(new float[]{0, 0, WIDTH, 0, WIDTH, HEIGHT, 0, HEIGHT});

        bounds.setPosition(position.x, position.y);
        bounds.setOrigin(WIDTH/2, HEIGHT/2);
        viewCircle = new Circle(getCentrePosition().x, getCentrePosition().y, 7.5F);
        shieldCircle = new Circle(getCentrePosition().x, getCentrePosition().y, 2F);
        rotation = 0;
        rotationSpeed = 150;
        this.name = name;
        this.lives = lives;
        this.maxLives = lives;
        gun = new Gun(Gun.Type.PISTOL);
        acceleration = 0F;
    }

    public Vector2 getPosition() {
        return position;
    }


    public Vector2 getCentrePosition() {
        return new Vector2(getPosition().x + WIDTH/2, getPosition().y + HEIGHT/2);
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

    public Boost getBoost() {
        return boost == null ? Boost.NOTHING : boost;
    }

    public void setBoost(Boost boost) {
        this.boost = boost;
        boostTimer = new Timer.Task() {
            @Override
            public void run() {
                setBoost(null);
                boostTimer.cancel();
            }
        };
        Timer.schedule(boostTimer, 10);
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

    public boolean isTurningAntiClockwise() {
        return turningAntiClockwise;
    }

    public void setTurningAntiClockwise(boolean turningAntiClockwise) {
        this.turningAntiClockwise = turningAntiClockwise;
    }

    public boolean isTurningClcokwise() {
        return turningClcokwise;
    }

    public void setTurningClcokwise(boolean turningClcokwise) {
        this.turningClcokwise = turningClcokwise;
    }

    public void rotateAntiClockwise(float delta) {
        //left is presses
        setRotation(getRotation() + (getRotationSpeed() * delta));
        if (getRotation() > 360) {
            setRotation(getRotation() - 360);
        }
    }

     public void rotateClockwise(float delta){
        // right is pressed
        setState(Player.State.MOVING);
        setRotation(getRotation() - (getRotationSpeed() * delta));
        if (getRotation() < 0) {
            setRotation(getRotation() + 360);
        }
    }

    public void moveForward() {
        setState(State.MOVING);
        setAcceleration(3.5F);
    }

    public void stop() {
        setState(State.IDLE);
        setAcceleration(0F);
    }

    public String getName() {
        return name;
    }

    public int getLives() {
        return lives;
    }

    public Circle getViewCircle() {
        return viewCircle;
    }

    public Circle getShieldCircle() {
        return shieldCircle;
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
            bullets.addAll(gun.fire((new Vector2(viewCircle.x, viewCircle.y)), rotation, name, getBoost().equals(Boost.HOMING), getBoost().equals(Boost.DAMAGE)));
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

    private void stopHealTimer() {
        healTimer.cancel();
        healing = false;
        lives = lives + 4;
        if (lives > maxLives) lives = maxLives;
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
            Timer.schedule(injuredTimer, 0.02F, 1.5f);
        }
    }

    private void dead() {
        setState(State.DEAD);
    }

    public void heal() {
        if (!healing && lives < maxLives) {
            healTimer = new Timer.Task() {
                @Override
                public void run() {
                    stopHealTimer();
                }
            };
            healing = true;
            Timer.schedule(healTimer, 2F);
        }
    }

    public void update(float delta) {
//		position.add(velocity.x * delta, velocity.y * delta);
//		bounds.x = position.x;
//		bounds.y = position.y;
//        viewCircle.setPosition(viewCircle.getX() + (getVelocity().x * delta), viewCircle.getY() + (getVelocity().y * delta));
        stateTime += delta;
    }
}
