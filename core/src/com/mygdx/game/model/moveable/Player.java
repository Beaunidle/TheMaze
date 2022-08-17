package com.mygdx.game.model.moveable;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.model.Inventory;
import com.mygdx.game.model.Recipe;
import com.mygdx.game.model.environment.Tilled;
import com.mygdx.game.model.environment.blocks.Block;
import com.mygdx.game.model.environment.blocks.EnvironmentBlock;
import com.mygdx.game.model.environment.blocks.FillableBlock;
import com.mygdx.game.model.environment.blocks.Grower;
import com.mygdx.game.model.environment.blocks.Wall;
import com.mygdx.game.model.items.Fillable;
import com.mygdx.game.model.items.Food;
import com.mygdx.game.model.items.Magic;
import com.mygdx.game.model.items.Ranged;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.items.Material;
import com.mygdx.game.model.pads.FloorPad;
import com.mygdx.game.utils.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class Player extends Sprite {

    public enum Action {
        IDLE, SWINGTOOL, SWINGSWORD
    }
    public enum Boost {
        HOMING, SPEED, DAMAGE, SHIELD, HEALING, NOTHING;
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
    private final Circle hitCircle;
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

    private final Timer.Task useTimer = new Timer.Task() {
        @Override
        public void run() {
            stopUseTimer();
            action = Action.IDLE;
        }
    };

    private final Timer.Task slotMoveTimer = new Timer.Task() {
        @Override
        public void run() {
            stopSlotMoveTimer();
        }
    };

    private final Timer.Task useDelayTimer = new Timer.Task() {
        @Override
        public void run() {
            stopUseDelayTimer();
            action = Action.IDLE;
        }
    };
    private Timer.Task boostTimer;

    private boolean bulletTimerOn;
    private boolean useTimerOn;
    private boolean useDelayOn;
    private boolean blocking;
    private boolean slotMoving;

    public Player(Vector2 position, String name, float lives, RecipeHolder recipeHolder) {
        super(position, 0.70F, 0.70F, lives);
        shieldCircle = new Circle(getCentrePosition().x, getCentrePosition().y, 2F);
        hitCircle = new Circle(getLeftHandPosition(45, getWidth()), 0.5F);
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
        setName(name);
        ranged = null;
        inventory = new Inventory(20);
        toolBelt = new Inventory(9);
        inventory.addInventory(new Item(Item.ItemType.SHOVEL, 100));
        inventory.addInventory(new Item(Item.ItemType.HOE, 100));
        inventory.addInventory(new Item(Item.ItemType.BED, 100));
        inventory.addInventory(new Item(Item.ItemType.BED, 100));
        inventory.addInventory(new Item(Item.ItemType.SPEAR, 100));
        inventory.addInventory(new Item(Item.ItemType.SPEAR, 100));
        inventory.addInventory(new Item(Item.ItemType.SPEAR, 100));
        toolBelt.addInventory(new Material(Material.Type.PEBBLE, 10));
        toolBelt.addInventory(new Item(Item.ItemType.BENCHHEALER, 10));
        inventory.addInventory(new Item(Item.ItemType.STONEANVIL, 10));
        inventory.addInventory(new Item(Item.ItemType.DOOR, 20));
        inventory.addInventory(new Item(Item.ItemType.SHIELD, 100));
        inventory.addInventory(new Item(Item.ItemType.ARMOUR, 100));
        inventory.addInventory(new Item(Item.ItemType.WALL, 20));
        inventory.addInventory(new Item(Item.ItemType.WALL, 20));
        inventory.addInventory(new Item(Item.ItemType.WALL, 20));
        inventory.addInventory(new Item(Item.ItemType.WALL, 20));
        inventory.addInventory(new Item(Item.ItemType.WALL, 20));
        inventory.addInventory(new Fillable(Item.ItemType.JAR, 20));

        //todo work out how spells can be unlocked
        spells = new ArrayList<>();
        spells.add(new Magic(Projectile.ProjectileType.FIREBALL));
        spells.add(new Magic(3, Magic.Element.ELECTRIC));
        spells.add(new Magic(5, "healing"));
        strongHand = (Material)inventory.getSlots().get(0);
//        weakHand = (Item)inventory.getSlots().get(1);
//        torso = (Item)inventory.getSlots().get(2);
        recipes = recipeHolder.getHandRecipes();
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

    public Circle getHitCircle() {
        return hitCircle;
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
        System.out.println("respawn started");
        setLives(getMaxLives());
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
        System.out.println("respawn finished");
    }

    private void startBulletTimer(float delay) {
        bulletTimerOn = true;
        Timer.schedule(bulletTimer, delay, delay);
    }

    private void stopBulletTimer() {
        bulletTimerOn = false;
        bulletTimer.cancel();
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

    public boolean isUseDelayOn() {
        return  useDelayOn;
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
//        float handRotation = getRotation() + 45;
        if (handRotation > 360) handRotation = handRotation - 360;
        float x = getCentrePosition().x + (float)(width * Math.cos((handRotation) * Math.PI/180));
        float y = getCentrePosition().y + (float)(width * Math.sin((handRotation) * Math.PI/180));
        return new Vector2(x,y);
    }

    public  Vector2 getRightHandPosition() {
        float handRotation = getRotation() - 45;
        if (handRotation < 0) handRotation = handRotation + 360;
        float x = getCentrePosition().x + (float)(getWidth() * Math.cos((handRotation) * Math.PI/180));
        float y = getCentrePosition().y + (float)(getHeight() * Math.sin((handRotation) * Math.PI/180));
        return new Vector2(x,y);
    }

    public void useItem(Player player, Item item, List<AIPlayer> aiPlayers, List<Animal> animals, List<FloorPad> floorPads) {
//        useHand(aiPlayers, animals, floorPads);
        if (item == null) return;

        Block[][] blocks = getView().getBlocks();

        hitCircle.setPosition(leftHanded ? getLeftHandPosition(45, getWidth()) : getRightHandPosition());
        hitCircle.setRadius(0.75f);

        switch (item.getItemType()) {
            case PICK:
            case AXE:
//                action = Action.SWINGTOOL;
                for (Block[] value : blocks) {
                    for (Object o : value) {
                        if (o instanceof EnvironmentBlock) {
                            EnvironmentBlock eb = (EnvironmentBlock) o;
                            if (!(eb.getMaterial().getType().equals(Material.Type.GRASS) || eb.getMaterial().getType().equals(Material.Type.FOOD))) {
                                EnvironmentBlock block = (EnvironmentBlock) o;
                                if (Intersector.overlaps(hitCircle, block.getBounds().getBoundingRectangle()) && block.getDurability() > 0) {
                                    Material material = new Material (block.getMaterial().getType(), block.hit());
                                    inventory.addInventory(material);
                                    return;
                                }
                            }
                        }
                    }
                }
//                    Object[] itemsInFront = whatsInFront();
//                    if (inFront == null) return;
//                    for (Object inFront : itemsInFront) {
//                        if (inFront instanceof EnvironmentBlock) {
//                            if (((EnvironmentBlock) inFront).getDurability() > 0) {
//                                EnvironmentBlock block = (EnvironmentBlock) inFront;
//                                Material material = new Material (block.getType(), block.hit());
//                                inventory.addInventory(material);
//                                return;
//                            }
//                        }
//                    }
                break;
            case SWORD:
                //todo
//                System.out.println("Swinging sword");
                if (!player.getName().equals(this.getName()) && Intersector.overlaps(hitCircle, player.getBounds().getBoundingRectangle())) {
                    player.hit(getName(), 2, getRotation(), getCentrePosition());
                }
                for (AIPlayer aiPlayer : aiPlayers) {
                    if (!aiPlayer.getName().equals(this.getName()) && Intersector.overlaps(hitCircle, aiPlayer.getBounds().getBoundingRectangle())) {
                        aiPlayer.hit(getName(), 2, getRotation(), getCentrePosition());
                    }
                }
                for (Animal animal : animals) {
                    if (Intersector.overlaps(hitCircle, animal.getBounds().getBoundingRectangle())) {
                        animal.hit(getName(), 5, getRotation(), getCentrePosition());
                    }
                }
                break;
            case HAMMER:
                break;
            case JAR:
                Fillable jar = (Fillable) item;
                if (jar.isFilled()) {
                    System.out.println("Slurp slurp");
                    drink(5);
                    jar.setFilled(false);
                }
                for (FloorPad floorPad : floorPads) {
                    if (floorPad.getType().equals(FloorPad.Type.WATER) || floorPad.getType().equals(FloorPad.Type.WATERFLOW)) {
                        if (Intersector.overlapConvexPolygons(floorPad.getBounds(), getBounds())) {
                            System.out.println("Filling jar");
                            jar.setFilled(true);
                        }
                    }
                }
                break;
            case WALL:
//                    if (!isUseTimerOn()) {
//                        int rotation = getStrongHand().getRotation();
//                        Point gridRef = getGridRef(getRotation(), getCentrePosition().x, getCentrePosition().y);
//                        blocks = getView().getBlocks();
//                        Object o = blocks[gridRef.x][gridRef.y];
//
//                        if (o instanceof Wall) {
//                            if (!((Wall) o).isWallFull(rotation)) {
//                                //todo add another wall
//                                if (rotation >= 45 && rotation < 135) {
//                                    ((Wall) o).addWall(Block.getSIZE(), Block.getSIZE()/4, rotation);
//                                } else if (rotation >= 135 && rotation < 225) {
//                                    ((Wall) o).addWall(Block.getSIZE(), Block.getSIZE()/4, rotation);
//                                } else if (rotation >= 225 && rotation < 315) {
//                                    ((Wall) o).addWall(Block.getSIZE(), Block.getSIZE()/4, rotation);
//                                } else { //if (rotation < 45 || rotation >= 315) {
//                                    ((Wall) o).addWall(Block.getSIZE(), Block.getSIZE()/4, rotation);
//                                }
//
//                            }
//                        } else if (o == null) {
//                            blocks[gridRef.x][gridRef.y] = new Wall(new Vector2(gridRef.x, gridRef.y), rotation, Block.getSIZE(), Block.getSIZE()/4);
//                        }
//                        getInventory().removeItem(new Item(Item.ItemType.WALL, 10));
//                        if (!getInventory().checkItem(new Item(Item.ItemType.WALL, 10))) {
//                            setStrongHand(null);
//                        }
//                        startUseTimer(0.5F);
//                    }
            }
    }

    public Block useHand(List<AIPlayer> aiPlayers, List<Animal> animals, List<FloorPad> floorPads) {
        for (FloorPad floorPad : floorPads) {
            if (floorPad.getType().equals(FloorPad.Type.WATER) || floorPad.getType().equals(FloorPad.Type.WATERFLOW)) {
                if (Intersector.overlapConvexPolygons(floorPad.getBounds(), getBounds())) {
                    drink(5);
                }
            }
        }
        for (Block[] value : getView().getBlocks()) {
            for (Object o : value) {
                if (o != null && Intersector.overlaps(hitCircle, ((Block) o).getBounds().getBoundingRectangle())) {
                    if (o instanceof EnvironmentBlock && ((EnvironmentBlock) o).getDurability() > 0) {
                        EnvironmentBlock block = (EnvironmentBlock) o;
                        Material material = block.getMaterial();
                        if (material.getType().equals(Material.Type.FOOD)) {
                            Material newMaterial = new Food (((Food) material).getFoodType(), block.hit());
                            inventory.addInventory(newMaterial);
                            return block;
                        } else                     if (material.getType().equals(Material.Type.STICK)) {
                            Material newMaterial = new Material (Material.Type.WOOD, block.hit());
                            inventory.addInventory(newMaterial);
                            return block;
                        } else if (material.getType().equals(Material.Type.GRASS) || material.getType().equals(Material.Type.PEBBLE)) {
                            Material newMaterial = new Material (material.getType(), block.hit());
                            inventory.addInventory(newMaterial);
                            return block;
                        }
                    }
                    if (o instanceof Wall) {
                        Wall wallBlock = (Wall)o;
                        for (Wall.WallType wall : wallBlock.getWalls().values()) {
                            if (wall != null && wall.isDoor()) {
                                if (Intersector.overlaps(hitCircle, wall.getBounds().getBoundingRectangle())) {
                                    wall.toggleOpen();
                                }
                            }
                        }
                    }
                    if ((o instanceof FillableBlock)) {
//                    if (fillable.getType().equals(FillableBlock.BlockType.CAMPFIRE) || fillable.getType().equals(FillableBlock.BlockType.BENCHHEALER)) {
//                        fillable.toggleActive();
                        return (FillableBlock) o;
//                    }
                    }
                    if (o instanceof Grower) {
                        return (Grower) o;
                    }
                    if (o instanceof Tilled) {
                        return (Tilled) o;
                    }
                }
            }
        }
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
            hitCircle.setPosition(getLeftHandPosition(45, getWidth()));
            blockRectangle.setPosition(getRightHandPosition().x, getRightHandPosition().y);
            blockRectangle.setRotation(blocking ? getRotation() : getRotation() + 90);
        } else {
            hitCircle.setPosition(getRightHandPosition());
            blockRectangle.setPosition(getLeftHandPosition(45, getWidth()).x, getLeftHandPosition(45, getWidth()).y);
            blockRectangle.setRotation(blocking ? getRotation() : getRotation() - 90);
        }
        if (blockRectangle.getRotation() > 360) blockRectangle.setRotation(blockRectangle.getRotation() - 360);
        if (blockRectangle.getRotation() > 0) blockRectangle.setRotation(blockRectangle.getRotation() + 360);
        shieldCircle.setPosition(getCentrePosition().x, getCentrePosition().y);
    }
}
