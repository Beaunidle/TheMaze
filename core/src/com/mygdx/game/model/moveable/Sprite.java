package com.mygdx.game.model.moveable;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.utils.Locator;
import com.mygdx.game.utils.View;

import java.awt.Point;

public class Sprite {

    public enum State {
        IDLE, MOVING, DEAD
    }

    private float height = 0.70F;
    private float width = 0.70F;

    private String name;
    private Vector2 position;
    private Float acceleration;
    private Float leftAcceleration, rightAcceleration;
    private float dodge;
    private Vector2 velocity = new Vector2();
    private final Polygon bounds;
    private State state = State.IDLE;
    private float rotation;
    private float rotationSpeed;
    private Vector2 hitPosition;
    private float stateTime = 0;
    private final Polygon viewCircle;
    private final float viewCircleWidth;
    private final float viewCircleHeight;
    private View view;
    Locator locator;

    private float maxLives, maxWater, maxFood, maxMana;
    private float lives, food, water, mana;
    private boolean turningAntiClockwise = false, turningClcokwise = false, moveForward = false, moveBackward = false;
    private Timer.Task injuredTimer;
    private final Timer.Task healTimer;
    private final Timer.Task ageTimer;
    private final Timer.Task dodgeTimer;
    private final Timer.Task staggaredTimer;
    private Timer.Task onFireTimer;

    private boolean injured, healing, aging, dodging, staggered, onfire;
    private String killedBy;
    private int rotateBy = 0;

    public Sprite(Vector2 position, float width, float height, float lives) {
        this.position = position;
        this.width = width;
        this.height = height;
        this.maxLives = lives;
        this.lives = lives;
        this.mana = 20;
        this.water = 10;
        this.food = 10;
        this.maxWater = water;
        this.maxFood = food;
        this.maxMana = mana;
        locator = new Locator();

        bounds = new Polygon(new float[]{0, 0, width, 0, width, height, 0, height});
        bounds.setPosition(position.x, position.y);
        bounds.setOrigin(width/2, height/2);
        viewCircleWidth = 18;
        viewCircleHeight = 8;
        float viewX = viewCircleWidth/2 ;
        float viewY = viewCircleHeight/2 ;
        viewCircle = new Polygon(new float[]{-5, -2, viewCircleWidth + 2, -2, viewCircleWidth + 2, viewCircleHeight + 2, -5, viewCircleHeight + 2});
//        viewCircle.setPosition(position.x - viewX*4, position.y - viewY*4);
        view = new View();
        rotation = 0;
        hitPosition = null;
        rotationSpeed = 300;
        acceleration = 0F;
        leftAcceleration = 0F;
        rightAcceleration = 0f;
        dodge = 0;

        healTimer = new Timer.Task() {
            @Override
            public void run() {
                stopHealTimer();
            }
        };
        ageTimer = new Timer.Task() {
            @Override
            public void run() {
                stopAgeTimer();
            }
        };
        dodgeTimer = new Timer.Task() {
            @Override
            public void run() {
                stopDodgeTimer();
            }
        };
        injuredTimer = new Timer.Task() {
            @Override
            public void run() {
                stopInjureTimer();
            }
        };
        staggaredTimer = new Timer.Task() {
            @Override
            public void run() {
                stopStaggeredTimer();
            }
        };
        onFireTimer = new Timer.Task() {
            @Override
            public void run() {
                stopOnFireTimer();
            }
        };
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vector2 getCentrePosition() {
        return new Vector2(getPosition().x + width/2, getPosition().y + height/2);
    }

    public Float getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Float acceleration) {
        this.acceleration = acceleration;
    }

    public Float getLeftAcceleration() {
        return leftAcceleration;
    }

    public void setLeftAcceleration(Float leftAcceleration) {
        this.leftAcceleration = leftAcceleration;
    }

    public float getDodge() {
        return dodge;
    }

    public void setDodge(float dodge) {
        this.dodge = dodge;
    }

    public Float getRightAcceleration() {
        return rightAcceleration;
    }

    public void setRightAcceleration(Float rightAcceleration) {
        this.rightAcceleration = rightAcceleration;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Polygon getBounds() {
        return bounds;
    }

    public String getKilledBy() {
        return killedBy;
    }

    public void setKilledBy(String killedBy) {
        this.killedBy = killedBy;
    }

    public float getLives() {
        return lives;
    }

    public void setLives(float lives) {
        this.lives = lives;
    }

    public float getMana() {
        return mana;
    }

    public void setMana(float mana) {
        this.mana = mana;
    }

    public float getMaxLives() {
        return maxLives;
    }

    public float getMaxWater() {
        return maxWater;
    }

    public float getMaxFood() {
        return maxFood;
    }

    public float getMaxMana() {
        return maxMana;
    }

    public float getFood() {
        return food;
    }

    public void setFood(float food) {
        this.food = food;
    }

    public float getWater() {
        return water;
    }

    public void setWater(float water) {
        this.water = water;
    }

    public void reduceLife(float damage) {
        lives = lives - damage;
    }

    public void increaseLife(float healing) {
        lives = lives + healing;
        if (lives > maxLives) lives = maxLives;
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

    public void setStateTime(float stateTime) {
        this.stateTime = stateTime;
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

    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }

    public Vector2 getHitPosition() {
        return hitPosition;
    }

    public void setHitPosition(Vector2 hitPosition) {
        this.hitPosition = hitPosition;
    }

    public Polygon getViewCircle() {
        return viewCircle;
    }

    public float getViewCircleWidth() {
        return viewCircleWidth;
    }

    public float getViewCircleHeight() {
        return viewCircleHeight;
    }

    public View getView() {
        return view;
    }

    public void clearView() {
        view = new View();
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public boolean isInjured() {
        return injured;
    }

    public void setInjured(boolean injured) {
        this.injured = injured;
    }

    public boolean isStaggered() {
        return staggered;
    }

    public void setStaggered(boolean staggered) {
        this.staggered = staggered;
    }

    public boolean isOnfire() {
        return onfire;
    }

    public void setOnfire(boolean onfire) {
        this.onfire = onfire;
    }

    public void moveForward() {
        setState(State.MOVING);
        setAcceleration(3.5F);
    }

    public void stop() {
        setState(State.IDLE);
        setAcceleration(0F);
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

    public int getRotateBy() {
        return rotateBy;
    }

    public void setRotateBy(int rotateBy) {
        this.rotateBy = rotateBy;
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

    public void update(float delta) {
        setStateTime(getStateTime() + delta);
    }

    private void stopInjureTimer() {
        injuredTimer.cancel();
        injured = false;
    }

    private void stopStaggeredTimer() {
        staggaredTimer.cancel();
        staggered = false;
    }

    private void stopOnFireTimer() {
        onFireTimer.cancel();
        onfire = false;
    }

    private void stopHealTimer() {
        healTimer.cancel();
        healing = false;
    }

    private void stopAgeTimer() {
        ageTimer.cancel();
        aging = false;
    }

    private void stopDodgeTimer() {
        dodgeTimer.cancel();
        dodging = false;
    }

//    private void lowerWater() {
//        if (water > 0 ) {
//            water--;
//        }
//    }
//
//    private void lowerFood() {
//        if (food > 0) {
//            food--;
//        }
//    }

    public void drink(float quantity) {
        water = water + quantity;
        if (water > maxWater) water = maxWater;
    }

    public void eat(float quantity) {
        food = food + quantity;
        if (food > maxFood) food = maxFood;
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
            lives = lives - damage;
            if (lives <= 0) {
                dead(name);
                return;
            }
            Timer.schedule(injuredTimer, 0.02F, 1.5f);
        }
    }

    public float checkBlock(Vector2 hiyRotation) {
        return(0);
    }

    public float checkArmour() {
        return(0);
    }

    public void hit(String name, float damage, float hitRotation, Vector2 hitPosition) {
        float block = checkBlock(hitPosition);
        block = block + checkArmour();
        damage = damage - block;
//        System.out.println(damage);
        if (damage <= 0) return;
        if (!injured && !staggered) {
            injured = true;
            staggered = true;
            lives = lives - damage;
            if (lives <= 0) {
                dead(name);
                return;
            }
            this.hitPosition = hitPosition;
            acceleration = -damage/2    ;
            Timer.schedule(injuredTimer, 0.02F, 1.5f);
            Timer.schedule(staggaredTimer, 0.25F);
        }
    }

    public void setAlight(float burningTime) {
        if (!onfire) {
            onfire = true;
            Timer.schedule(onFireTimer, burningTime);
        }
    }

    private void dead(String name) {
        setState(State.DEAD);
        killedBy = name;
    }

    public void heal() {
        if (!healing) {
            if (onfire) lives = lives - 4;
            if (food > 0 && water > 0) {
                if (lives < maxLives ) {
                    healing = true;
                    lives = lives + 1;
                    if (lives > maxLives) lives = maxLives;
                }
                if (mana < maxMana) {
                    healing = true;
                    mana = mana + 1;
                    if (mana > maxMana) mana = maxMana;
                }
                if (healing) Timer.schedule(healTimer, 1F);
            }
        }
    }

    public void magicHeal() {

    }

    public void age() {
        if (!aging) {
            aging = true;
            Timer.schedule(ageTimer, 3F);
            if (water > 0 ) {
                water = water- 0.25F;
            } else {
                if (lives > 0) lives--;
            }
            if (food > 0) {
                food = food - 0.25F;
            } else {
                if (lives > 0) lives--;
            }
        }

    }

    public void dodge(float dodge) {
        if (!dodging) {
            dodging = true;
            Timer.schedule(dodgeTimer, 0.5F);
            setDodge(dodge);
        }
    }

    public void turnAround(int angle) {
        if (rotateBy == 0) {
            rotateBy = angle;
        }
    }

    public Point getGridRef(float bobRotation, float xPos, float yPos) {
        int col;
        int row;
        if (bobRotation >= 45 && bobRotation < 135) {
            col = (int)xPos;
            row = (int)yPos + 2;
        } else if (bobRotation >= 135 && bobRotation < 225) {
            col = (int)xPos - 2;
            row = (int)yPos;
        } else if (bobRotation >= 225 && bobRotation < 315) {
            col = (int)xPos;
            row = (int)yPos + -2;
        } else {//(rotation < 45 || rotation >= 315) {
            col = (int)xPos + 2;
            row = (int)yPos;
        }
        return new Point(col, row);
    }

    public void updateShapes() {
        //do nothing for now
    }

    public void useMana(float mana) {
        this.mana -= mana;
        if (this.mana < 0) this.mana = 0;
    }

    public Locator getLocator() {
        return locator;
    }
}
