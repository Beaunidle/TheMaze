package com.mygdx.game.model.moveable;

import static com.mygdx.game.model.items.Material.Type.*;
import static com.mygdx.game.model.moveable.Projectile.ProjectileType.FIREBALL;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.model.Attributes;
import com.mygdx.game.model.Inventory;
import com.mygdx.game.model.Recipe;
import com.mygdx.game.model.environment.blocks.Block;
import com.mygdx.game.model.items.Fillable;
import com.mygdx.game.model.items.Magic;
import com.mygdx.game.model.items.Placeable;
import com.mygdx.game.model.items.Ranged;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.items.Material;
import com.mygdx.game.model.items.Swingable;
import com.mygdx.game.model.pads.FloorPad;
import com.mygdx.game.utils.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class Player extends Sprite {

    public enum Action {
        IDLE, SWINGTOOL, SWINGSWORD
    }
    public enum Boost {
        HOMING, SPEED, DAMAGE, SHIELD, HEALING, NOTHING
    }

    private Material strongHand;
    private Item weakHand;
    private Item torso;
    private Boost boost;
    private Action action = Action.IDLE;
    private Ranged ranged;
    private final Inventory inventory;
    private final Inventory toolBelt;
    private final List<Magic> spells;
    private final List<Recipe> recipes;
    private final Circle shieldCircle;
    private final Polygon blockRectangle;
    private boolean leftHanded;
    private int slotNo = 0;
    private Vector2 personalSpawn;
//    private Circle viewCircle;

    private final Timer.Task bulletTimer = new Timer.Task() {
        @Override
        public void run() {
            stopBulletTimer();
        }
    };

    private final Timer.Task slotMoveTimer = new Timer.Task() {
        @Override
        public void run() {
            stopSlotMoveTimer();
        }
    };

    private Timer.Task boostTimer;

    private boolean bulletTimerOn;
    private boolean blocking;
    private boolean slotMoving;

    public Player(Vector2 position, String name, float lives, RecipeHolder recipeHolder) {
        super(position, 0.70F, 0.70F, lives, 10, 5, 5, name);
        shieldCircle = new Circle(getCentrePosition().x, getCentrePosition().y, 5F);
        setHitCircle(new Circle(getLeftHandPosition(45, getWidth()), 0.5F));
        leftHanded = true;//!(this instanceof AIPlayer);

        if (leftHanded) {
            blockRectangle = new Polygon(new float[]{0, 0, 0.25F, 0, 0.25F, 1F, 0, 1F});
            blockRectangle.setOrigin(0, 0);
        } else {
            blockRectangle = new Polygon(new float[]{0, 0, 0.25F, 0, 0.25F, -1F, 0, -1F});
            blockRectangle.setOrigin(0F, 0F);
        }
        blockRectangle.setPosition(getRightHandPosition().x,getRightHandPosition().y);
        blockRectangle.setRotation(getRotation());
        ranged = null;
        inventory = new Inventory(20);
        toolBelt = new Inventory(9);
        toolBelt.addInventory(new Swingable(Swingable.SwingableType.AXE, 50, new Material(FLINT, 1)));
//        toolBelt.addInventory(new Swingable(Swingable.SwingableType.AXE, 50, new Material(COPPER, 1)));
        toolBelt.addInventory(new Swingable(Swingable.SwingableType.PICK, 50, new Material(FLINT, 1)));
        toolBelt.addInventory(new Swingable(Swingable.SwingableType.SWORD, 50, new Material(FLINT, 1)));
//        toolBelt.addInventory(new Swingable(Swingable.SwingableType.SWORD, 50, new Material(COPPER, 1)));
//        toolBelt.addInventory(new Swingable(Swingable.SwingableType.SHOVEL, 10, new Material(BONE, 1)));
//        toolBelt.addInventory(new Swingable(Swingable.SwingableType.HOE, 10, new Material(BONE, 1)));
        toolBelt.addInventory(new Swingable(Swingable.SwingableType.HAMMER, 10, new Material(STONE, 1)));
//        toolBelt.addInventory(new Swingable(Swingable.SwingableType.CLUB, 10, new Material(BONE, 1)));
//        toolBelt.addInventory(new Placeable(Placeable.PlaceableType.CAMPFIRE, 10));
        toolBelt.addInventory(new Placeable(Placeable.PlaceableType.STONEANVIL, 10));
        toolBelt.addInventory(new Placeable(Placeable.PlaceableType.TORCH, 10));
        toolBelt.addInventory(new Placeable(Placeable.PlaceableType.HOUSE, 10));
//        toolBelt.addInventory(new Placeable(Placeable.PlaceableType.HOUSE, 10));
//        toolBelt.addInventory(new Placeable(Placeable.PlaceableType.HOUSE, 10));
//        for (int i = 0; i < 100; i++) {
//            inventory.addInventory(new Placeable(Placeable.PlaceableType.WALL, 10));
//        }
        toolBelt.addInventory(new Placeable(Placeable.PlaceableType.BED, 10));
        toolBelt.addInventory(new Placeable(Placeable.PlaceableType.BED, 10));
//        inventory.addInventory(new Material(COPPER, 20));
//        inventory.addInventory(new Material(FLINT, 20));
//        inventory.addInventory(new Material(Material.Type.WOOD, 10));
//        inventory.addInventory(new Fillable(Item.ItemType.JAR, 20));

        //todo work out how spells can be unlocked
        spells = new ArrayList<>();
//        spells.add(new Magic(Projectile.ProjectileType.FIREBALL));
//        spells.add(new Magic(3, Magic.Element.ELECTRIC));
//        spells.add(new Magic(5, "healing"));
//        strongHand = spells.get(0);
//        weakHand = (Item)inventory.getSlots().get(1);
//        torso = (Item)inventory.getSlots().get(2);
        recipes = recipeHolder.getHandRecipes();
        setHitTime(0.0625F);
    }

    public Material getStrongHand() {
        return strongHand;
    }

    public void setStrongHand(Material strongHand) {
        this.strongHand = strongHand;
    }

    public Item getWeakHand() {
        return weakHand;
    }

    public void setWeakHand(Item weakHand) {
        this.weakHand = weakHand;
    }

    public Item getTorso() {
        return torso;
    }

    public void setTorso(Item torso) {
        this.torso = torso;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setBoost(Boost boost, float delay) {
        this.boost = boost;
        boostTimer = new Timer.Task() {
            @Override
            public void run() {
                setBoost(null, 0);
                boostTimer.cancel();
            }
        };
        Timer.schedule(boostTimer, delay);
    }

    public Polygon getBlockRectangle() { return  blockRectangle;}

    public Circle getShieldCircle() {
        return shieldCircle;
    }

    public Player.Boost getBoost() {
        return boost == null ? Player.Boost.NOTHING : boost;
    }

    public Ranged getGun() {
        return ranged;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Inventory getToolBelt() {
        return toolBelt;
    }

    public List<Magic> getSpells() {
        return spells;
    }

    public List<Projectile> fireBullet(float rot) {
        if (ranged == null) new ArrayList<>();
        List<Projectile> projectiles = new ArrayList<>();
        if (!bulletTimerOn) {
            startBulletTimer(ranged.getFiringRate() * 0.25F);
            float x = getCentrePosition().x + (float)(getWidth()/2 * Math.cos(rot * Math.PI/180));
            float y = getCentrePosition().y + (float)(getHeight()/2 * Math.sin(rot * Math.PI/180));
            projectiles.addAll(ranged.fire(new Vector2(x, y), rot, getName(), getBoost().equals(Boost.HOMING), getBoost().equals(Boost.DAMAGE), Projectile.ProjectileType.BULLET));
        }
        return projectiles;
    }

    public void respawn(Vector2 newPos) {
//        System.out.println("respawn started");
        setLives(getMaxHealth());
        setMana(getMaxMana());
        setWater(getMaxWater());
        setFood(getMaxFood());
//        this.gun = new Gun(Gun.Type.ROCKET);
        setPosition(new Vector2(newPos));
        getBounds().setPosition(getPosition().x, getPosition().y);
        getBounds().setRotation(getRotation());
//        this.viewCircle.setPosition(getCentrePosition().x, getCentrePosition().y);
        this.getViewCircle().setPosition(this.getPosition().x, this.getPosition().y);
        this.shieldCircle.setPosition(getCentrePosition().x, getCentrePosition().y);
        setInjured(false);
        setStaggered(false);
//        this.setBoost(Boost.SHIELD);
        setState(State.IDLE);
//        System.out.println("respawn finished");
    }

    private void startBulletTimer(float delay) {
        bulletTimerOn = true;
        Timer.schedule(bulletTimer, delay, delay);
    }

    private void stopBulletTimer() {
        bulletTimerOn = false;
        bulletTimer.cancel();
    }

    public void startSlotMoveTimer(float delay) {
        slotMoving = true;
        Timer.schedule(slotMoveTimer, delay);
    }
    private void stopSlotMoveTimer() {
        slotMoveTimer.cancel();
        slotMoving = false;
    }

    public boolean isBlocking() {
        return blocking;
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public boolean isSlotMoving() {
        return slotMoving;
    }

    public Vector2 getPersonalSpawn() {
        return personalSpawn;
    }

    public void setPersonalSpawn(Vector2 personalSpawn) {
        this.personalSpawn = personalSpawn;
    }

    //    public void update(float delta) {
//		position.add(velocity.x * delta, velocity.y * delta);
//		bounds.x = position.x;
//		bounds.y = position.y;
//        viewCircle.setPosition(viewCircle.getX() + (getVelocity().x * delta), viewCircle.getY() + (getVelocity().y * delta));
//        setStateTime(getStateTime() + delta);
//    }

    public void swingPick() {
        //todo need to figure out how to do it
    }

    public boolean isLeftHanded() {
        return leftHanded;
    }

    public void setLeftHanded(boolean leftHanded) {
        this.leftHanded = leftHanded;
    }

    public int getSlotNo() {
        return slotNo;
    }

    public void increaseSlot() {
        slotNo++;
        if (slotNo > toolBelt.getSize() - 1) {
            slotNo = 0;
        }
    }

    public void decreaseSlot() {
        slotNo--;
        if (slotNo < 0) {
            slotNo = toolBelt.getSize() - 1;
        }
    }

    public Vector2 getLeftHandPosition(float rotation, float width) {
        float handRotation = getRotation() + rotation;
        if (handRotation > 360) handRotation = handRotation - 360;
        float x = getCentrePosition().x + (float)(width * Math.cos((handRotation) * Math.PI/180));
        float y = getCentrePosition().y + (float)(width * Math.sin((handRotation) * Math.PI/180));
        return new Vector2(x,y);
    }

    public Vector2 getRightHandPosition() {
        float handRotation = getRotation() - 45;
        if (handRotation < 0) handRotation = handRotation + 360;
        float x = getCentrePosition().x + (float)(getWidth()/2 * Math.cos((handRotation) * Math.PI/180));
        float y = getCentrePosition().y + (float)(getHeight()/2 * Math.sin((handRotation) * Math.PI/180));
        return new Vector2(x,y);
    }

    public void updateHitCircle() {
        //todo here is where to think about stab/swing/lunge ect
        getHitCircle().setRadius(0.75f);

        Vector2 gridRef = getCentrePosition();
        float rotation = getRotation();
        float x = 0;
        float y = 0;
        if (rotation < 0) rotation = rotation + 360;
        if (rotation > 360) rotation = rotation - 360;
        switch (getHitPhase()) {
            case 0:
                rotation = 0;
                getHitCircle().setRadius(0.001F);
                x = gridRef.x + (float)(getWidth() * Math.cos((rotation)) * Math.PI/180);
                y = gridRef.y + (float)(getHeight() * Math.sin((rotation)) * Math.PI/180);
                break;
            case 1:
            case 2:
            case 3:
            case 4:
                x = getLeftHandPosition(45, getWidth()).x + (float)(getWidth() * 1.5 * Math.cos(rotation * Math.PI/180));
                y = getLeftHandPosition(45, getWidth()).y + (float)(getHeight() * 1.5 * Math.sin((rotation * Math.PI/180)));
                System.out.println("Case 1: Player position: " + getCentrePosition() + ", x:" + x + ", y: " + y + ", Rotation: " + rotation);
                break;
//            case 2:
//                x = gridRef.x + (float)(getWidth()*2 * Math.cos((rotation) * Math.PI/180));
//                y = gridRef.y + (float)(getHeight()*2 * Math.sin((rotation) * Math.PI/180));
//                System.out.println("Case 2: x:" + x + ", y: " + y + ". Rotation: " + rotation);
//                break;
//            case 3:
//                x = gridRef.x + (float)(getWidth()*1.75 * Math.cos((rotation - 10) * Math.PI/180));
//                y = gridRef.y + (float)(getHeight()*1.75 * Math.sin((rotation - 10) * Math.PI/180));
//                System.out.println("Case 3: x:" + x + ", y: " + y + ". Rotation: " + rotation);
//                break;
//            case 4:
//                x = gridRef.x + (float)(getWidth()*5 * Math.cos((rotation - 15) * Math.PI/180));
//                y = gridRef.y + (float)(getHeight()*5 * Math.sin((rotation - 15) * Math.PI/180));
//                System.out.println("Case 4: x:" + x + ", y: " + y + ". Rotation: " + rotation);
//                break;
        }

//        x = gridRef.x + (float)(getWidth()*1.4F * Math.cos(rotation * Math.PI/180));
//        y = gridRef.y + (float)(getHeight()*4.4F * Math.sin(rotation * Math.PI/180));
        getHitCircle().setPosition(new Vector2(x, y));
    }

    public Block useHand(List<AIPlayer> aiPlayers, List<Animal> animals, List<FloorPad> floorPads) {
        return null;
    }

    public Object[] whatsInFront() {
        Block[][] blocks = getView().getBlocks();
//        for (int i = 0; i< blocks.length; i++ ) {
//            for (int j = 0; j < blocks[0].length; j++) {
//                if (blocks[i][j] != null && blocks[i][j] instanceof Coal) {
//                    System.out.println("Coal at" + i + "," + j);
//                }
//            }
//        }
        Object[] objectsInFront = new Object[3];
        if (getRotation() >= 45 && getRotation() < 135) {
            objectsInFront[0] = blocks[6][5];
            objectsInFront[1] = blocks[7][5];
//            objectsInFront[2] = blocks[8][5];
            return objectsInFront;
        }
        if (getRotation() >= 135 && getRotation() < 225) {
            objectsInFront[0] = blocks[6][3];
            objectsInFront[1] = blocks[6][4];
//            objectsInFront[2] = blocks[6][5];
            return objectsInFront;
        }
        if (getRotation() >= 225 && getRotation() < 315) {
//            objectsInFront[0] = blocks[7][3];
            objectsInFront[1] = blocks[8][3];
            objectsInFront[2] = blocks[9][3];
            return objectsInFront;
        }
        if (getRotation() < 45 || getRotation() >= 315) {
//            objectsInFront[0] = blocks[9][3];
            objectsInFront[1] = blocks[9][4];
            objectsInFront[2] = blocks[9][5];
            return objectsInFront;
        }
        return null;
    }

    public float checkBlock(Vector2 hitPosition) {

        if (getWeakHand() != null && getWeakHand().getItemType().equals(Item.ItemType.SHIELD) && blockSuccess(hitPosition)) {
            return 5;
        }
        return(super.checkBlock(hitPosition));
    }

    public float checkArmour() {
        if (getTorso() != null && getTorso().getItemType().equals(Item.ItemType.ARMOUR)) {
            return 1;
        }
        return super.checkArmour();
    }

    public boolean blockSuccess(Vector2 hitPosition) {
        Vector2 dst = new Vector2(new Vector2(hitPosition).sub(getCentrePosition()));
        float hitRotation = locator.getAngle(dst);
        float relativeAngle = getRotation() - hitRotation;
        if (relativeAngle < 0) relativeAngle = relativeAngle + 360;
        if (leftHanded) {
            return blocking && relativeAngle <= 45 || relativeAngle >= 325 || !blocking && relativeAngle >= 45 && relativeAngle < 135;
        } else {
            return blocking && relativeAngle >= 45 || relativeAngle >= 325 || !blocking && relativeAngle >= 225 && relativeAngle < 305;
        }
    }

    public void updateShapes() {
        if (leftHanded) {
//            hitCircle.setPosition(getLeftHandPosition(45, getWidth()));
            blockRectangle.setPosition(getRightHandPosition().x, getRightHandPosition().y);
            blockRectangle.setRotation(blocking ? getRotation() : getRotation() + 90);
        } else {
//            hitCircle.setPosition(getRightHandPosition());
            blockRectangle.setPosition(getLeftHandPosition(45, getWidth()).x, getLeftHandPosition(45, getWidth()).y);
            blockRectangle.setRotation(blocking ? getRotation() : getRotation() - 90);
        }
        if (blockRectangle.getRotation() > 360) blockRectangle.setRotation(blockRectangle.getRotation() - 360);
        if (blockRectangle.getRotation() > 0) blockRectangle.setRotation(blockRectangle.getRotation() + 360);
        shieldCircle.setPosition(getCentrePosition().x, getCentrePosition().y);
        getCollideCircle().setPosition(getCentrePosition().x, getCentrePosition().y);
    }

    public boolean isHoldingFire() {
        return getStrongHand() != null && getStrongHand() instanceof Magic && ((Magic) getStrongHand()).getMagicType().equals(Magic.MagicType.PROJECTILE)
                && ((Magic) getStrongHand()).getProjectileType().equals(FIREBALL)
                || (getStrongHand() instanceof Placeable && ((Placeable) getStrongHand()).getPlaceableType().equals(Placeable.PlaceableType.TORCH));
    }

    public void addToInventory(Material material) {
        int added = 0;
        if(material.isHoldable()) {
            added = toolBelt.addInventory(material);
        }
        if (added == 0) {
            inventory.addInventory(material);
        }
    }

    public void addAllToInventory(List<Material> materials) {
        for (Material material : materials) {
            addToInventory(material);
        }
    }
}
