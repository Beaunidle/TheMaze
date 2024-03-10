package com.mygdx.game.model.moveable;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.model.Attributes;
import com.mygdx.game.model.GameObject;
import com.mygdx.game.model.items.Consumable;
import com.mygdx.game.model.items.Material;
import com.mygdx.game.utils.Locator;
import com.mygdx.game.utils.View;

import java.awt.Point;
import java.util.List;

public class Sprite extends GameObject {

    public enum State {
        IDLE,MOVING,DEAD,HIDING,HUNGRY,THIRSTY,HORNY
    }

    public enum Intent {
        SEARCHING, HOMEWARD, HOMING, KILLING, FLEEING, EATING, DRINKING, MATING
    }

    public enum Effect {
        FIRE, ELECTRIC, POISON, SLOW, HOMING, SPEED, DAMAGE, SHIELD, HEALING, NOTHING
    }

    private float height;
    private float width;

    private Float acceleration;
    private Float leftAcceleration, rightAcceleration;
    private float dodge;
    private Vector2 velocity = new Vector2();
    private State state = State.IDLE;
    private float rotation;
    private float rotationSpeed;
    private Vector2 hitPosition;
    private float stateTime = 0;
    private int hitPhase = 0;
    private int comboPhase = 0;
    private float hitTime = 0;
    private int maxHits;
    private final Polygon viewCircle;
    private final float viewCircleWidth;
    private final float viewCircleHeight;
    private Circle hitCircle;
    private Circle collideCircle;
    private View view;
    Locator locator;
    private Sprite targetSprite;
    private Vector2 target;
    private Vector2 lastFoodPos;
    private Vector2 lastWaterPos;
    private Intent intent = Intent.SEARCHING;
    private long birthTime;
    private boolean child;
    private List<Effect> immunities;

    private float maxWater, maxFood;
    private float lives, food, water, mana;
    private boolean turningAntiClockwise = false, turningClcokwise = false, moveForward = false, moveBackward = false;
    private Timer.Task injuredTimer;
    private final Timer.Task healTimer;
    private final Timer.Task ageTimer;
    private final Timer.Task dodgeTimer;
    private final Timer.Task staggaredTimer;
    private final Timer.Task onFireTimer;

    private boolean comboTimerOn;
    private boolean useTimerOn;
    private boolean useDelayOn;

    private int houseNumber;
    private boolean injured, healing, aging, dodging, staggered, onfire;
    private String killedBy;
    private int rotateBy = 0;
    private Attributes attributes;


    private final Timer.Task hitTimer = new Timer.Task() {
        @Override
        public void run() {
            if (maxHits > 0) hitPhaseIncrease(maxHits);
        }
    };

    private final Timer.Task useTimer = new Timer.Task() {
        @Override
        public void run() {
            stopUseTimer();
        }
    };

    private final Timer.Task useDelayTimer = new Timer.Task() {
        @Override
        public void run() {
            stopUseDelayTimer();
        }
    };

    private final Timer.Task comboTimer = new Timer.Task() {
        @Override
        public void run() {
            stopComboTimer();
        }
    };

    public Sprite(Vector2 position, float width, float height, float lives, float aptitude, float food, float water, String name, int houseNumber, List<Effect> immunities) {
        super(name, position, new Polygon(new float[]{0, 0, width, 0, width, height, 0, height}));
        birthTime = System.currentTimeMillis();
        getBounds().setOrigin(width/2, height/2);
        viewCircleWidth = 18;
        viewCircleHeight = 8;
        viewCircle = new Polygon(new float[]{-5, -2, viewCircleWidth + 2, -2, viewCircleWidth + 2, viewCircleHeight + 2, -5, viewCircleHeight + 2});
        hitCircle = new Circle(getCentrePosition(), 5F);
        collideCircle = new Circle(getCentrePosition(), 2.5F);
        collideCircle.setPosition(getCentrePosition());
        attributes = new Attributes(lives, aptitude);
        this.houseNumber = houseNumber;

        this.width = width;
        this.height = height;
        this.lives = getMaxHealth();
        this.mana = attributes.getMaxMana();
        this.water = water;
        this.food = food;
        this.maxWater = water;
        this.maxFood = food;
        locator = new Locator();

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

        this.immunities = immunities;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
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

    public void setMaxWater(float maxWater) {
        this.maxWater = maxWater;
    }

    public void setMaxFood(float maxFood) {
        this.maxFood = maxFood;
    }

    public float getMaxWater() {
        return maxWater;
    }

    public float getMaxFood() { return attributes.getMaxFood(); }


    public float getMaxHealth() {return attributes.getMaxHealth();}

    public float getMaxMana() {
        return attributes.getMaxMana();
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
        if (lives > getMaxHealth()) lives = getMaxHealth();
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

    public int getHitPhase() {
        return hitPhase;
    }

    public void setHitPhase(int hitPhase) {
        this.hitPhase = hitPhase;
    }

    public int getComboPhase() {
        return comboPhase;
    }

    public void setComboPhase(int comboPhase) {
        this.comboPhase = comboPhase;
    }

    public float getHitTime() {
        return hitTime;
    }

    public void setHitTime(float hitTime) {
        this.hitTime = hitTime;
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

    public Circle getHitCircle() {
        return hitCircle;
    }

    public void setHitCircle(Circle hitCircle) {
        this.hitCircle = hitCircle;
    }

    public Circle getCollideCircle() {
        return collideCircle;
    }

    public void setCollideCircle(Circle collideCircle) {
        this.collideCircle = collideCircle;
    }

    public List<Effect> getImmunities() {
        return immunities;
    }

    public void  hitPhaseIncrease(int numberOfHits) {
        if (hitTimer.isScheduled()) hitTimer.cancel();
        hitPhase ++;
        maxHits = numberOfHits;
        if (hitPhase > maxHits) {
            hitPhase = 0;
            maxHits = 0;
        } else {
            Timer.schedule(hitTimer, hitTime);
        }
//        Timer.schedule(hitTimer, 0.0625F);
    }

    public void increaseCombo() {
        startComboTimer(0.75F);
        comboPhase = comboPhase + 1;
        if (comboPhase >= 4) {
            setAcceleration(100F);
            comboPhase = 0;
        }
    }

    private void startComboTimer(float delay) {
        if (comboTimerOn) comboTimer.cancel();
        comboTimerOn = true;
        Timer.schedule(comboTimer, delay);
    }

    private void stopComboTimer() {
        comboTimerOn = false;
        setComboPhase(0);
        comboTimer.cancel();
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

    public long getBirthTime() {
        return birthTime;
    }

    public void setBirthTime(long birthTime) {
        this.birthTime = birthTime;
    }

    public boolean isChild() {
        return child;
    }

    public void setChild(boolean child) {
        this.child = child;
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

    public boolean isInHouse() {
        return getPosition().x > 500 && getPosition().y > 500 && houseNumber > 0;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public void moveForward() {
//        setState(State.MOVING);
        setAcceleration(3.5F);
    }

    public void moveBackward() {
        setAcceleration(-3.5F);
    }

    public void moveLeft() {
//        setState(State.MOVING);
        setLeftAcceleration(3.5F);
    }

    public void moveRight() {
//        setState(State.MOVING);
        setRightAcceleration(3.5F);
    }

    public void stop() {
//        setState(State.IDLE);
        setAcceleration(0F);
    }

    public void rotateAntiClockwise(float delta) {
        //left is presses
    }

    public void rotateClockwise(float delta){
        // right is pressed
        setState(Player.State.MOVING);
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

    public void move(float delta) {
        // update Player's position
        getVelocity().x = getVelocity().x * delta;
        getVelocity().y = getVelocity().y * delta;

        getPosition().add(getVelocity());
        getBounds().setPosition(getPosition().x, getPosition().y);
        getViewCircle().setPosition(getPosition().x - 7.5F, getPosition().y - 4F);

        //todo rotation needs to be handled nicely
        if (isTurningClcokwise()) {
            setRotation(getRotation() - (getRotationSpeed() * delta));
            if (getRotation() < 0) {
                setRotation(getRotation() + 360);
            }
        }
        if (isTurningAntiClockwise()) {
            setRotation(getRotation() + (getRotationSpeed() * delta));
            if (getRotation() > 360) {
                setRotation(getRotation() - 360);
            }
        }
        getBounds().setRotation(getRotation());
        //        getViewCircle().setPosition(getCentrePosition().x, getCentrePosition().y);

//        if (sprite instanceof sprite && !(sprite instanceof AIsprite)) {
        updateShapes();
        getVelocity().x = getVelocity().x * (1 / delta);
        getVelocity().y = getVelocity().y * (1 / delta);
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
        if (food > getMaxFood()) food = getMaxFood();
    }

    public void eat(Consumable consumable) {
        int amount = consumable.getFood();
        if (amount > 0) {
            food += amount;
            if (food > getMaxFood()) food = getMaxFood();

            //todo sort out nutrients. Use nutrients to decide attribute buffs :)
            if (this instanceof Player) {
                attributes.increaseVitality(amount);
            }
        }
    }

    public void increaseAptitude(float buff) {
        attributes.increaseAptitude(buff);
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
            Timer.schedule(injuredTimer, 0.5F);
        }
    }

    public float checkBlock(Vector2 hiyRotation) {
        return(0);
    }

    public float checkArmour() {
        return(0);
    }

    public void hit(String name, float damage, float hitRotation, Vector2 hitPosition, float knockback) {
        if (!injured && !staggered) {
            float block = checkBlock(hitPosition);
            block = block + checkArmour();
            damage = damage - block;
//        System.out.println("damage is " + damage);
            if (damage <= 0) return;
            lives = lives - damage;
            if (lives <= 0) {
                dead(name);
                return;
            }
            this.hitPosition = hitPosition;
            acceleration = -knockback;
            injured = true;
            staggered = true;
            Timer.schedule(injuredTimer, 0.5F);
            Timer.schedule(staggaredTimer, 0.25F);
        }
    }

    public void setAlight(float burningTime) {
        if (!onfire && !immunities.contains(Effect.FIRE)) {
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
            if (onfire) lives = lives - 2;
            if (food > 0 && water > 0) {
                float maxLives = getMaxHealth();
                if (lives < maxLives) {
                    healing = true;
                    //todo add healing factor
                    lives = lives + attributes.getHealing();;
                    if (lives > maxLives) lives = maxLives;
                }
                float maxMana = attributes.getMaxMana();
                if (mana < maxMana) {
                    healing = true;
                    mana = mana + 0.3F;
                    if (mana > maxMana) mana = maxMana;
                }
                if (healing) Timer.schedule(healTimer, 1F);
            }
        }
    }

    public void magicHeal() {

    }

    public void age() {
        if (!aging && !getState().equals(State.HIDING)) {
            aging = true;
            Timer.schedule(ageTimer, 5F);
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
        collideCircle.setPosition(getCentrePosition().x, getCentrePosition().y);
    }

    public void useMana(float mana) {
        this.mana -= mana;
        if (this.mana < 0) this.mana = 0;
    }

    public Locator getLocator() {
        return locator;
    }

    public Sprite getTargetSprite() {
        return targetSprite;
    }

    public void setTargetSprite(Sprite targetSprite) {
        this.targetSprite = targetSprite;
    }

    public Vector2 getTarget() {
        return target;
    }

    public void setTarget(Vector2 target) {
        this.target = target;
    }

    public Vector2 getLastFoodPos() {
        return lastFoodPos;
    }

    public void setLastFoodPos(Vector2 lastFoodPos) {
        this.lastFoodPos = lastFoodPos;
    }

    public Vector2 getLastWaterPos() {
        return lastWaterPos;
    }

    public void setLastWaterPos(Vector2 lastWaterPos) {
        this.lastWaterPos = lastWaterPos;
    }

    public void startUseTimer(float delay) {
        useTimerOn = true;
        Timer.schedule(useTimer, delay, delay);
    }
    private void stopUseTimer() {
        useTimerOn = false;
        useTimer.cancel();
    }

    public boolean isUseTimerOn() {
        return useTimerOn;
    }

    public void startUseDelayTimer(float delay) {
        useDelayOn = true;
        Timer.schedule(useDelayTimer, delay, delay);
    }
    private void stopUseDelayTimer() {
        useDelayTimer.cancel();
        useDelayOn = false;
    }

    public boolean isUseDelayOn() {
        return  useDelayOn;
    }

    public void updateHitCircle() {
    }

    //todo store this shit somewhere
    public void growUp() {
        if (this instanceof Animal &&  ((Animal) this).getAnimalType().equals(Animal.AnimalType.COW)) {
            width = 2.50F;
            height = 0.9F;
            this.setBounds(new Polygon(new float[]{0, 0, width, 0, width, height, 0, height}));
            getBounds().setOrigin(width/2, height/2);
            //todo don't forget max lives increase for adults
//            maxLives = 20;
            maxFood = 10;
            maxWater = 10;
            child = false;
//            System.out.println("I am an adult!!!");
        }
    }

    public boolean isMaxHealth() {
        return  (lives >= getMaxHealth());
    }
}
