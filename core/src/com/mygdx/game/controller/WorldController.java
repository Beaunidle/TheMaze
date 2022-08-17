package com.mygdx.game.controller;

import static com.mygdx.game.model.environment.AreaAffect.AffectType.EXPLOSION;
import static com.mygdx.game.model.environment.AreaAffect.AffectType.LIGHTNING;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.model.environment.AnimalSpawn;
import com.mygdx.game.model.environment.Tilled;
import com.mygdx.game.model.environment.blocks.Block;
import com.mygdx.game.model.environment.blocks.EnvironmentBlock;
import com.mygdx.game.model.environment.blocks.FillableBlock;
import com.mygdx.game.model.environment.blocks.Grower;
import com.mygdx.game.model.environment.blocks.Irrigation;
import com.mygdx.game.model.environment.blocks.Wall;
import com.mygdx.game.model.items.Food;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.items.Magic;
import com.mygdx.game.model.items.Material;
import com.mygdx.game.model.moveable.AIPlayer;
import com.mygdx.game.model.environment.BloodStain;
import com.mygdx.game.model.moveable.Animal;
import com.mygdx.game.model.moveable.Projectile;
import com.mygdx.game.model.environment.blocks.ExplodableBlock;
import com.mygdx.game.model.environment.AreaAffect;
import com.mygdx.game.model.moveable.Sprite;
import com.mygdx.game.model.moveable.Player;
import com.mygdx.game.model.ScoreBoard;
import com.mygdx.game.model.environment.SpawnPoint;
import com.mygdx.game.model.World;
import com.mygdx.game.model.pads.FloorPad;
import com.mygdx.game.utils.JoyStick;
import com.mygdx.game.utils.Locator;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WorldController {

    enum Keys {
        LEFT, RIGHT, STRAFELEFT, STRAFERIGHT, UP, DOWN, USE, FIRE, INV, MAP, DODGE, PAUSE, SLOTLEFT, SLOTRIGHT, SLOTUSE
    }

    private static final float DAMP = 0.90f;
    private static final float MAX_VEL = 4f;

    private final World world;
    private Player bob;
    private List<AIPlayer> aiPlayers;
//    private List<Animal> animals;
    private CollisionDetector collisionDetector;
    private FillableBlock fillableToShow;
    private int level = 1;
    private int frameSkipCount = 0;
    private boolean levelFinished;
    private boolean paused;
    private final Locator locator;
    private final ScoreBoard scoreBoard;
    private boolean pauseButtonRestricted;
    private final Timer.Task unpauseTimer = new Timer.Task() {
        @Override
        public void run() {
            stopUnpauseTimer();
        }
    };
    private final Timer.Task pauseButtonTimer = new Timer.Task() {
        @Override
        public void run() {
            stopPauseButtonTimer();
        }
    };
    private int baseTime = 0;

    private static final Map<Keys, Boolean> keys = new HashMap<>();
    private long fireButtonTime = 0;

    static {
        keys.put(Keys.LEFT, false);
        keys.put(Keys.RIGHT, false);
        keys.put(Keys.STRAFELEFT, false);
        keys.put(Keys.STRAFERIGHT, false);
        keys.put(Keys.UP, false);
        keys.put(Keys.DOWN, false);
        keys.put(Keys.USE, false);
        keys.put(Keys.FIRE, false);
        keys.put(Keys.INV, false);
        keys.put(Keys.MAP, false);
        keys.put(Keys.PAUSE, false);
        keys.put(Keys.SLOTLEFT, false);
        keys.put(Keys.SLOTRIGHT, false);
        keys.put(Keys.SLOTUSE, false);
    }

    public WorldController(World world, Game game) {
        this.world = world;
        this.bob = world.getBob();
        this.aiPlayers = world.getAIPlayers();
//        this.animals = world.getAnimals();
        collisionDetector = new CollisionDetector(world, bob, aiPlayers);
        levelFinished = false;
//        paused = false;
        locator = new Locator();
        scoreBoard = new ScoreBoard(bob, aiPlayers);

        Timer.Task gameTimer = new Timer.Task() {
            @Override
            public void run() {
//                levelFinished = true;
//                System.out.print(scoreBoard.toString());
            }
        };
        Timer.schedule(gameTimer, 1000);
    }

    public FillableBlock getFillableToShow() {
        return fillableToShow;
    }

    public void setFillableToShow(FillableBlock fillableToShow) {
        this.fillableToShow = fillableToShow;
    }

    public boolean isLevelFinished() {
        return levelFinished;
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public int getLevel() {
        return level;
    }

    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    // ** Key presses and touches **************** //

    public void leftPressed() {
        keys.put(Keys.LEFT, true);
    }

    public void rightPressed() {
        keys.put(Keys.RIGHT, true);
    }

    public void strafeLeftPressed() {
        keys.put(Keys.STRAFELEFT, true);
    }

    public void strafeRightPressed() {
        keys.put(Keys.STRAFERIGHT, true);
    }

    public void upPressed() {
        keys.put(Keys.UP, true);
    }

    public void downPressed() {
        keys.put(Keys.DOWN, true);
    }

    public void usePressed() { keys.put(Keys.USE, true); }

    public void firePressed() {
        keys.put(Keys.FIRE, true);
        fireButtonTime = System.currentTimeMillis();
    }

    public void shiftPressed() {
        keys.put(Keys.DODGE, true);
    }

    public void invPressed() {
        keys.put(Keys.INV, true);
    }

    public void mapPressed() { keys.put(Keys.MAP, true); }

    public void pausePressed()  {
        keys.put(Keys.PAUSE, true);
    }

    public void slotLeftPressed() {
        keys.put(Keys.SLOTLEFT, true);
    }

    public void slotRightPressed() {
        keys.put(Keys.SLOTRIGHT, true);
    }

    public void slotUsePressed() {
        keys.put(Keys.SLOTUSE, true);
    }

    public void leftReleased() {
        keys.put(Keys.LEFT, false);
    }

    public void rightReleased() {
        keys.put(Keys.RIGHT, false);
    }

    public void strafeLeftReleased() {
        keys.put(Keys.STRAFELEFT, false);
    }

    public void strafeRightReleased() {
        keys.put(Keys.STRAFERIGHT, false);
    }

    public void upReleased() {
        keys.put(Keys.UP, false);
    }

    public void downReleased() {
        keys.put(Keys.DOWN, false);
    }

    public void useReleased() {
        keys.put(Keys.USE, false);
    }

    public void fireReleased() {
        keys.put(Keys.FIRE, false);
    }

    public void shiftReleased() {
        keys.put(Keys.DODGE, false);
    }

    public void invReleased() { keys.put(Keys.INV, false); }

    public void mapReleased() { keys.put(Keys.MAP, false); }

    public void slotLeftReleased() { keys.put(Keys.SLOTLEFT, false); }

    public void slotRightReleased() { keys.put(Keys.SLOTRIGHT, false); }

    public void slotUseReleased() {
        keys.put(Keys.SLOTUSE, false);
    }

    public void pauseReleased() {
        keys.put(Keys.PAUSE, false);
    }

    public void loadLevel(int number) {
        System.out.println("controller Loading level " + number);
        world.loadWorld(number);
        this.bob = world.getBob();
        this.aiPlayers = world.getAIPlayers();
        this.collisionDetector = new CollisionDetector(world, bob, aiPlayers);
        level = number;
        levelFinished = false;
    }
    /**
     * The main update method
     **/
    public void update(float delta) {
        // Processing the input - setting the states of Bob
//        if (aiPlayers.isEmpty()) {
//             levelFinished = true;
//        }

        baseTime++;
        if (baseTime == 20) {
            world.increaseMinute();
            baseTime = 0;
        }

        if (keys.get(Keys.PAUSE)) {
            if (!pauseButtonRestricted) {
                startPauseButtonTimer(0.5F);
                setPaused(!isPaused());
            }
        }
        if (paused) {
            return;
        }
        if (bob.getLives() <= 0) bob.setState(Sprite.State.DEAD);
        //check health of players and set control instructions
        if (bob.getState().equals(Sprite.State.DEAD)) {
            scoreBoard.addDeath(bob.getName());
            if (bob.getKilledBy() != null) {
//                scoreBoard.addKill(bob.getKilledBy());
                bob.setKilledBy(null);
            }
            world.getBloodStains().add(new BloodStain(bob.getPosition(), bob.getName()));

            SpawnPoint sp = findSpawnPoint(bob.getName());
            bob.respawn(bob.getPersonalSpawn() != null ? bob.getPersonalSpawn() : sp.getPosition());
        } else {
            bob.heal();
            bob.age();
            fillView(bob);
//            bob.getView().printView();
            if (!bob.isStaggered()) {
                processInput();
            }
        }
        for (AIPlayer aiPlayer : aiPlayers) {
            if (aiPlayer.getLives() <= 0) aiPlayer.setState(Sprite.State.DEAD);
            if (aiPlayer.getState().equals(Player.State.DEAD)) {
                System.out.println("player dead");
                scoreBoard.addDeath(aiPlayer.getName());
                if (aiPlayer.getKilledBy() != null) {
//                    scoreBoard.addKill(aiPlayer.getKilledBy());
                    aiPlayer.setKilledBy(null);
                }
                world.getBloodStains().add(new BloodStain(aiPlayer.getPosition(), aiPlayer.getName()));
                System.out.println("finding spawn");


                SpawnPoint sp = findSpawnPoint(aiPlayer.getName());
                aiPlayer.respawn(sp.getPosition());
                System.out.println("Respawn complete");
            }
            else {
                if (!aiPlayer.isStaggered()) {
                    aiPlayer.heal();
                    fillView(aiPlayer);
                    processAIInput(aiPlayer, delta);
                }
                frameSkipCount++;
                if (frameSkipCount == 5) frameSkipCount = 0;

            }
        }
        Iterator<Animal> animalIterator = world.getAnimals().listIterator();
        while(animalIterator.hasNext()) {
            Animal animal = animalIterator.next();
            if (animal.getLives() <= 0) animal.setState(Sprite.State.DEAD);
            if (animal.getState().equals(Player.State.DEAD)) {
                System.out.println("animal dead");
//                scoreBoard.addDeath(aiPlayer.getName());
//                if (aiPlayer.getKilledBy() != null) {
//                    scoreBoard.addKill(aiPlayer.getKilledBy());
//                    aiPlayer.setKilledBy(null);
//                }
//                world.getBloodStains().add(new BloodStain(animal.getPosition(), animal.getName()));
                Vector2 pos = animal.getCentrePosition();
                EnvironmentBlock meat = new EnvironmentBlock(new Vector2((float)Math.floor(pos.x), (float)Math.floor(pos.y)), new Material(Material.Type.MEAT, 1));
                world.getBodies().add(meat);
                world.getLevel().getBlocks()[(int)pos.x][(int)pos.y] = meat;
                animal.die();
                animalIterator.remove();
            }
            else {
                animal.heal();
//                fillView(aiPlayer);
//                processAIInput(animal, delta);
                if (!animal.isStaggered()) {
                    animal.decide(delta);
                }
            }
        }

        for (Grower grower : world.getLevel().getGrowers()) {
            boolean nearWater = false;
            for (FloorPad floorPad : world.getLevel().getFloorPads()) {
                if (floorPad.getType().equals(FloorPad.Type.WATER) || floorPad.getType().equals(FloorPad.Type.WATERFLOW) || (floorPad.getType().equals(FloorPad.Type.IRRIGATION) && floorPad.isConnected())) {
                    double dist = Math.sqrt(Math.pow((floorPad.getPos().x - grower.getPosition().x), 2) + Math.pow((floorPad.getPos().y - grower.getPosition().y), 2));
                    if (dist < 3) {
                        nearWater = true;
                        break;
                    }
                }
            }
            grower.grow((nearWater ? 10 : 5));
        }


        //check for collisions and carry out movements
        if (!bob.getState().equals(Player.State.DEAD)) {
            setAction(delta, bob);
        }
        for (AIPlayer aiPlayer : aiPlayers) {
            if (!aiPlayer.getState().equals(Player.State.DEAD)) {
                setAction(delta, aiPlayer);
            }
        }
        for (Animal animal : world.getAnimals()) {
            if (!animal.getState().equals(Sprite.State.DEAD)) {
                setAction(delta, animal);
            }
        }

        //check collisions and move bullets
        if (world.getProjectiles() != null && !world.getProjectiles().isEmpty()) {
            Iterator<Projectile> bulletIterator = world.getProjectiles().iterator();
            while (bulletIterator.hasNext()) {
                Projectile projectile = (Projectile)bulletIterator.next();
                if (projectile.getSpeed() == 0 && !projectile.isExploding()) {
                    bulletIterator.remove();
                    continue;
                }
                if (projectile.getSpeed() > 0) {
                    if (projectile.isHoming() && projectile.isActivated()) {
//                        HomingProjectile hp = (HomingProjectile) projectile;
                        projectile.chooseTarget(bob, aiPlayers);
                        if (projectile.getTarget() == null) {
                            projectile.chooseTarget(bob, world.getAnimals());
                        }
                        if (projectile.getTarget() != null) {
                            Vector2 distance = new Vector2(projectile.getTarget().getCentrePosition()).sub(projectile.getPosition());
//                            double rot = Math.atan2(distance.y, distance.x);
                            float deg = locator.getAngle(distance);
                            if (locator.locate(deg, projectile.getRotation()) < 0) {
                                projectile.rotateAntiClockwise(delta);
                            } else if (locator.locate(deg, projectile.getRotation()) > 0) {
                                projectile.rotateClockwise(delta);
                            }
                        }
                    }

                    projectile.setVelocity(calculateVelocity(projectile.getSpeed() * delta, projectile.getRotation()));
                    collisionDetector.checkProjectileCollisionWithBlocks(projectile, delta);
                    collisionDetector.checkProjectileCollisionWithPlayers(projectile, delta);

                    projectile.getPosition().add(projectile.getVelocity().x, projectile.getVelocity().y);
                    projectile.setMomentum(projectile.getMomentum() - 5);
                    if (projectile.getMomentum() <= 0) projectile.setSpeed(0);
                    if (projectile.isHoming()) {
                        projectile.getViewCircle().setPosition(projectile.getPosition().x, projectile.getPosition().y);
                        projectile.getBounds().setPosition(projectile.getPosition().x, projectile.getPosition().y);
                        projectile.getBounds().setRotation(projectile.getRotation());
                    }
                }
                projectile.update(delta);
            }
        }

        //remove finished explosions
        if (world.getAreaAffects() != null && !world.getAreaAffects().isEmpty()) {
            Iterator<AreaAffect> explosionIterator = world.getAreaAffects().iterator();
            while (explosionIterator.hasNext()) {
                AreaAffect areaAffect = (AreaAffect)explosionIterator.next();
                if (areaAffect.isFinished()) {
                    explosionIterator.remove();
                }
            }
        }

        //handle explodable blocks
        for (ExplodableBlock eb : world.getLevel().getExplodableBlocks()) {
            if (!eb.getState().equals(ExplodableBlock.State.RUBBLE)) {
                if (eb.getState().equals(ExplodableBlock.State.BANG)) {
                    world.getAreaAffects().add(new AreaAffect(new Vector2(eb.getPosition().x + ExplodableBlock.getSIZE()/2, eb.getPosition().y + ExplodableBlock.getSIZE()/2), "explotion", 2, 2, EXPLOSION));
                    eb.setState(ExplodableBlock.State.RUBBLE);
                } else {
                    collisionDetector.checkExplodableCollisionWithExplosion(eb);
                }
            }
        }

        for (AnimalSpawn animalSpawn : world.getLevel().getAnimalSpawnPoints()) {
            if (animalSpawn.isReadyToAdd() && animalSpawn.getPopulation() < animalSpawn.getMaxPopulation()) {
                world.getAnimals().add(animalSpawn.addAnimal(""));
            }
        }
    }

    private void setAction (float delta, Sprite sprite) {
        // Convert acceleration to frame time
        //sprite.setAcceleration(sprite.getAcceleration() * delta);

        if (sprite.isTurningAntiClockwise()) {
            sprite.rotateAntiClockwise(delta);
        }
        if (sprite.isTurningClcokwise()) {
            sprite.rotateClockwise(delta);
        }

        //check for stagger
        if (sprite.isStaggered()) {
//            sprite.getCentrePosition().
            float hitAngle = locator.getAngle(new Vector2(sprite.getHitPosition()).sub(sprite.getCentrePosition()));
            sprite.setVelocity(calculateVelocity(sprite.getAcceleration(), hitAngle));
        } else {
            // apply acceleration to change velocity
            if (sprite.getAcceleration() != 0 ) {
                sprite.setVelocity(calculateVelocity(sprite.getAcceleration() + (sprite.getAcceleration() > 0 ? sprite.getDodge() : -sprite.getDodge()), sprite.getRotation()));
            } else {
                sprite.setVelocity(calculateVelocity(sprite.getAcceleration() , sprite.getRotation()));
            }
            if (sprite.getLeftAcceleration() > 0) {
                Vector2 leftVelocity = (calculateVelocity(sprite.getLeftAcceleration() + sprite.getDodge(), sprite.getRotation() + 90));
                sprite.getVelocity().add(leftVelocity);
            } else if (sprite.getRightAcceleration() > 0) {
                Vector2 rightVelocity = (calculateVelocity(sprite.getRightAcceleration() + sprite.getDodge(), sprite.getRotation() + 270));
                sprite.getVelocity().add(rightVelocity);
            }
            if (sprite.getDodge() != 0) {
                sprite.setDodge(sprite.getDodge()*0.85F);
                if (sprite.getDodge() < 1 && sprite.getDodge() > -1) sprite.setDodge(0);
            }
        }

        boolean instanceOfPlayer = sprite instanceof Player;
        //apply world effects
        collisionDetector.checkPlayerCollisionWithFloorPads(sprite);

        // checking collisions with the surrounding blocks depending on Bob's velocity
        collisionDetector.checkPlayerCollisionWithBlocks(delta, sprite);

        //effects after moving
        collisionDetector.checkPlayerCollisionWithExplosions(sprite);
        if (instanceOfPlayer) collisionDetector.checkPlayerCollisionWithBoosts((Player)sprite);

        // apply damping to halt Player nicely
        sprite.getVelocity().x *= DAMP;
        sprite.getVelocity().y *= DAMP;
        // ensure terminal velocity is not exceeded
        if (sprite.getVelocity().x > MAX_VEL) {
            sprite.getVelocity().x = MAX_VEL;
        }
        if (sprite.getVelocity().x < -MAX_VEL) {
            sprite.getVelocity().x = -MAX_VEL;
        }

        // simply updates the state time
        sprite.update(delta);
    }


    /**
     * Change Bob's state and parameters based on input controls
     **/
    private void processInput() {
        if (!bob.getState().equals(Player.State.DEAD)) {
//            bob.whatsInFront();
            if (keys.get(Keys.USE)) {
//                world.getBullets().addAll(bob.fireBullet(bob.getRotation()));
                //rotate a wall block ready to be placed
                if (bob.getStrongHand() != null) {
                    if (bob.getStrongHand() instanceof Item) {
                        Item item = (Item) bob.getStrongHand();
                         if (new ArrayList<>(Arrays.asList(Item.ItemType.SHOVEL, Item.ItemType.WALL, Item.ItemType.DOOR, Item.ItemType.CAMPFIRE, Item.ItemType.BENCHHEALER, Item.ItemType.STONEANVIL, Item.ItemType.BED)).contains(item.getItemType())) {
                             item.rotate();
                         }
                    }
                }
                //raise shield
                if (bob.getWeakHand() != null && bob.getWeakHand().getItemType().equals(Item.ItemType.SHIELD)) {
                    bob.setBlocking(true);
                }
                //use your hand on the object in front
                if (!bob.isUseDelayOn()) {
//                    bob.startUseTimer(0.25F);
                    bob.startUseDelayTimer(0.25F);
                    Block block = bob.useHand(world.getAIPlayers(), world.getAnimals(), world.getLevel().getFloorPads());
                    if (block instanceof FillableBlock) {
                        setFillableToShow((FillableBlock) block);
                        return;
                    }
                    if (block instanceof Grower) {
                        Grower grower = (Grower) block;
                        if (grower.getGrowthState().equals(Grower.GrowthState.MATURE)) bob.getInventory().addInventory(grower.harvest());
                    }
                    if (block instanceof Tilled) {
                        if (bob.getStrongHand() != null && bob.getStrongHand().isPlantable()) {
                            Point gridRef = bob.getGridRef(bob.getRotation(), bob.getCentrePosition().x, bob.getCentrePosition().y);
                            Block[][] blocks = world.getLevel().getBlocks();
                            switch (bob.getStrongHand().getType()) {
                                case FOOD:
                                    switch (((Food) bob.getStrongHand()).getFoodType()) {
                                        case POTATO:
                                            Grower grower = new Grower(new Vector2(block.getPosition().x, block.getPosition().y), Grower.CropType.POTATO);

                                            blocks[Math.round(block.getPosition().x)][Math.round(block.getPosition().y)] = grower;
                                            world.getLevel().getGrowers().add(grower);
                                            bob.getInventory().removeMaterial(new Food(Food.FoodType.POTATO, 1));
                                            if (!bob.getInventory().checkInventory(new Food(Food.FoodType.POTATO, 1))) bob.setStrongHand(null);
                                            break;
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
                //Activate floor pad
//                for (GunPad gunPad : world.getLevel().getGunPads()) {
//                    if (Intersector.overlapConvexPolygons(bob.getBounds(), gunPad.getBounds())) {
//                        bob.getGun().setType(gunPad.getType());
//                    }
//                }
                //reload gun
                if (bob.getGun() != null && !bob.getGun().fullAmmo()) {
                    bob.getGun().reload();
                }
            } else {
                bob.setBlocking(false);
            }

            if (keys.get(Keys.INV)) {
                setPaused(true);
                setFillableToShow(new FillableBlock(new Vector2(bob.getCentrePosition()), 10, FillableBlock.FillableType.INVSCREEN, 0, 0, world.getRecipeHolder()));
            }
            if (keys.get(Keys.MAP)) {
                setPaused(true);
                setFillableToShow(new FillableBlock(new Vector2(bob.getCentrePosition()), 10, FillableBlock.FillableType.MAPSCREEN, 0, 0, world.getRecipeHolder()));
            }

            if (fireButtonTime != 0 && !keys.get(Keys.FIRE)) {
                long buttonPressTime = System.currentTimeMillis() - fireButtonTime;
                boolean poweredUp = buttonPressTime > 800;
                if (bob.getStrongHand() != null && !bob.isUseDelayOn()) {
                    bob.startUseTimer(bob.getStrongHand().getUseTime());
                    bob.startUseDelayTimer(bob.getStrongHand().getUseDelay());
                    if (bob.getStrongHand() != null) {
                        if (bob.getStrongHand() instanceof Item) {
                            if (bob.getStrongHand() instanceof Magic) {
                                Magic magic = (Magic) bob.getStrongHand();
                                if (bob.getMana() >= magic.getManaRequired()) {
                                    switch (magic.getMagicType()) {
                                        case PROJECTILE:
                                            switch (magic.getProjectileType()) {
                                                case FIREBALL:
                                                    //todo HERERERERERE
                                                    Projectile projectile = new Projectile(new Vector2(bob.getLeftHandPosition(140, bob.getWidth()/2)), bob.getRotation(), bob.getName(), 3, Projectile.ProjectileType.FIREBALL, 0, poweredUp);
                                                    world.getProjectiles().add(projectile);
                                                    bob.useMana(magic.getManaRequired());
                                                    break;
                                                case LIGHNINGBOLT:
                                                    break;
                                                default:
                                                    break;
                                            }
                                            break;
                                        case AREA:
                                            switch (magic.getElement()) {
                                                case ELECTRIC:
                                                    //todo targeting :D
                                                    if (poweredUp) {
                                                        AreaAffect areaAffect = new AreaAffect(bob.getLeftHandPosition(0, 6),"lightning", 2, 1, LIGHTNING);
                                                        world.getAreaAffects().add(areaAffect);
                                                        bob.useMana(magic.getManaRequired());
//                                                    bob.ta
                                                    }
                                            }
                                            break;
                                        case SELF:
                                            switch (magic.getAttribute()) {
                                                case "healing":
                                                    if (bob.getLives() < bob.getMaxLives()) {
                                                        bob.increaseLife(magic.getEffect());
                                                        bob.useMana(magic.getManaRequired());
                                                        bob.setBoost(Player.Boost.HEALING, 2);
                                                    }
                                                    break;
                                                default:
                                                    break;
                                            }
                                            break;
                                    }
                                }
                            }
                            Item item = (Item)bob.getStrongHand();
                            Point gridRef = bob.getGridRef(bob.getRotation(), bob.getCentrePosition().x, bob.getCentrePosition().y);
                            Block[][] blocks = world.getLevel().getBlocks();
                            Object o = blocks[gridRef.x][gridRef.y];
                            if (item.getItemType().equals(Item.ItemType.HOE)) {
                                if (o == null) {
                                    Tilled tilled = new Tilled(new Vector2(gridRef.x, gridRef.y), "tilled");
                                    blocks[gridRef.x][gridRef.y] = tilled;
                                }
                            }
                            if (item.getItemType().equals(Item.ItemType.SHOVEL)) {
                                if (o == null) {
                                    FloorPad irrigation = new FloorPad(new Vector2(gridRef.x, gridRef.y), FloorPad.Type.IRRIGATION, item.getRotation());
                                    for (FloorPad floorPad : world.getLevel().getFloorPads()) {
                                        switch (floorPad.getType()) {
                                            case WATER:
                                            case WATERFLOW:
                                                for (FloorPad.Connection connection : irrigation.getConnections()) {
                                                    if (connection.equals(FloorPad.Connection.N) && floorPad.getPos().y == irrigation.getPos().y + 1 && floorPad.getPos().x == irrigation.getPos().x) {
                                                        irrigation.setConnected(true);
                                                        break;
                                                    }
                                                    if (connection.equals(FloorPad.Connection.S) && floorPad.getPos().y == irrigation.getPos().y - 1 && floorPad.getPos().x == irrigation.getPos().x) {
                                                        irrigation.setConnected(true);
                                                        break;
                                                    }
                                                    if (connection.equals(FloorPad.Connection.E) && floorPad.getPos().x == irrigation.getPos().x + 1 && floorPad.getPos().y == irrigation.getPos().y) {
                                                        irrigation.setConnected(true);
                                                        break;
                                                    }
                                                    if (connection.equals(FloorPad.Connection.W) && floorPad.getPos().x == irrigation.getPos().x - 1 && floorPad.getPos().y == irrigation.getPos().y) {
                                                        irrigation.setConnected(true);
                                                        break;
                                                    }
                                                }
                                                break;
                                            case IRRIGATION:
                                                if (floorPad.isConnected()) {
                                                    for (FloorPad.Connection connection : irrigation.getConnections()) {
                                                        if (connection.equals(FloorPad.Connection.N) && floorPad.getConnections().contains(FloorPad.Connection.S) && floorPad.getPos().y == irrigation.getPos().y + 1 && floorPad.getPos().x == irrigation.getPos().x) {
                                                            irrigation.setConnected(true);
                                                            break;
                                                        }
                                                        if (connection.equals(FloorPad.Connection.S) && floorPad.getConnections().contains(FloorPad.Connection.N) && floorPad.getPos().y == irrigation.getPos().y - 1 && floorPad.getPos().x == irrigation.getPos().x) {
                                                            irrigation.setConnected(true);
                                                            break;
                                                        }
                                                        if (connection.equals(FloorPad.Connection.E) && floorPad.getConnections().contains(FloorPad.Connection.W) && floorPad.getPos().x == irrigation.getPos().x + 1 && floorPad.getPos().y == irrigation.getPos().y) {
                                                            irrigation.setConnected(true);
                                                            break;
                                                        }
                                                        if (connection.equals(FloorPad.Connection.W) && floorPad.getConnections().contains(FloorPad.Connection.E) && floorPad.getPos().x == irrigation.getPos().x - 1 && floorPad.getPos().y == irrigation.getPos().y) {
                                                            irrigation.setConnected(true);
                                                            break;
                                                        }
                                                    }
                                                }
                                                break;
                                        }
                                    }
                                    world.getLevel().getFloorPads().add(irrigation);
                                }
                            }
                            if (item.getItemType().equals(Item.ItemType.WALL) || item.getItemType().equals(Item.ItemType.DOOR)) {
                                boolean isDoor = item.getItemType().equals(Item.ItemType.DOOR);
                                int rotation = item.getRotation();

                                if (o instanceof Wall) {
                                    if (!((Wall) o).isWallFull(rotation)) {
                                        ((Wall) o).addWall(Block.getSIZE(), Block.getSIZE()/4, rotation, isDoor);
                                        bob.getStrongHand().setQuantity(bob.getStrongHand().getQuantity() - 1);
                                    }
                                } else if (o == null) {
                                    blocks[gridRef.x][gridRef.y] = new Wall(new Vector2(gridRef.x, gridRef.y), rotation, Block.getSIZE(), Block.getSIZE()/4, isDoor);
                                    bob.getStrongHand().setQuantity(bob.getStrongHand().getQuantity() - 1);
                                }
                                if (bob.getStrongHand().getQuantity() <= 0 && !bob.getInventory().checkItem(new Item(Item.ItemType.WALL, 10))) {
                                    bob.setStrongHand(null);
                                }
                            } else if (item.getItemType().equals(Item.ItemType.CAMPFIRE) || item.getItemType().equals(Item.ItemType.BENCHHEALER)
                                    || item.getItemType().equals(Item.ItemType.STONEANVIL) || item.getItemType().equals(Item.ItemType.BED)) {
                                int rotation = item.getRotation();

                                if (o == null) {
                                    //todo make this a separate method
                                    if (item.getItemType().equals(Item.ItemType.BED)) {
                                        blocks[gridRef.x][gridRef.y] = new Block(new Vector2(gridRef.x, gridRef.y), 10, item.getSize(), rotation, Block.BlockType.BED);
                                        bob.setPersonalSpawn(new Vector2(gridRef.x, gridRef.y));
                                        System.out.println("Spawn point is being reset to " + bob.getPersonalSpawn());
                                    } else {
                                        FillableBlock.FillableType fillableType = item.getItemType().equals(Item.ItemType.CAMPFIRE) ? FillableBlock.FillableType.CAMPFIRE : item.getItemType().equals(Item.ItemType.BENCHHEALER) ? FillableBlock.FillableType.BENCHHEALER : FillableBlock.FillableType.STONEANVIL;
                                        blocks[gridRef.x][gridRef.y] = new FillableBlock(new Vector2(gridRef.x, gridRef.y), 10, fillableType, item.getSize(), rotation, world.getRecipeHolder());
                                        if (item.getSize() == 2) {
                                            if (rotation == 0) blocks[gridRef.x+1][gridRef.y] = new FillableBlock(new Vector2(gridRef.x, gridRef.y), 10, fillableType, item.getSize(), item.getRotation(), world.getRecipeHolder());
                                            if (rotation == 90) blocks[gridRef.x][gridRef.y+1] = new FillableBlock(new Vector2(gridRef.x, gridRef.y), 10, fillableType, item.getSize(), item.getRotation(), world.getRecipeHolder());
                                            if (rotation == 180) blocks[gridRef.x-1][gridRef.y] = new FillableBlock(new Vector2(gridRef.x, gridRef.y), 10, fillableType, item.getSize(), item.getRotation(), world.getRecipeHolder());
                                            if (rotation == 270) blocks[gridRef.x][gridRef.y-1] = new FillableBlock(new Vector2(gridRef.x, gridRef.y), 10, fillableType, item.getSize(), item.getRotation(), world.getRecipeHolder());
                                        }
                                    }
//                                    bob.getInventory().removeItem(new Item(item.getItemType(), 10));
                                    bob.getStrongHand().setQuantity(bob.getStrongHand().getQuantity() - 1);
                                }
                                if (bob.getStrongHand().getQuantity() <= 0 && !bob.getInventory().checkItem(new Item(item.getItemType(), 10))) {
                                    bob.setStrongHand(null);
                                }
                            } else if (item.getItemType().equals(Item.ItemType.SPEAR)) {
                                //todo think of all the variables that will go into calculating these

                                Projectile projectile = new Projectile(new Vector2(bob.getLeftHandPosition(140, bob.getWidth()/2)), bob.getRotation(), bob.getName(), 2, Projectile.ProjectileType.SPEAR, 0, poweredUp);

                                world.getProjectiles().add(projectile);
                                bob.getStrongHand().setQuantity(bob.getStrongHand().getQuantity() - 1);

                                if (bob.getStrongHand().getQuantity() <= 0 && !bob.getInventory().checkInventory(new Item(Item.ItemType.SPEAR, 1))) bob.setStrongHand(null);
                            } else {
                                bob.useItem(bob, item, aiPlayers, world.getAnimals(), world.getLevel().getFloorPads());
                            }
                        }
                        if (bob.getStrongHand() instanceof Material) {
                            Material material = (Material) bob.getStrongHand();
                            if (material.getType().equals(Material.Type.PEBBLE)) {
                                world.getProjectiles().add(new Projectile(bob.getLeftHandPosition(45, bob.getWidth()/2), bob.getRotation(), bob.getName(), 1, Projectile.ProjectileType.PEBBLE, 0, poweredUp));
                                bob.getStrongHand().setQuantity(bob.getStrongHand().getQuantity() - 1);
                                if (bob.getStrongHand().getQuantity() <= 0 && !bob.getInventory().checkInventory(new Material(Material.Type.PEBBLE, 1))) bob.setStrongHand(null);
                            }
                            if (material instanceof Food) {
                                //eat
                                world.getBob().eat(5);
                                bob.getInventory().removeMaterial(new Material(material.getType(), 1));
                                if (!bob.getInventory().checkInventory(new Material(material.getType(), 1))) bob.setStrongHand(null);
                            }
                        }
                    } else {
                        //todo punch
//                        bob.startUseTimer(0.25F);
//                        bob.startUseDelayTimer(0.25F);
//                        bob.useHand(world.getAIPlayers(), world.getAnimals(), world.getLevel().getFloorPads());
                    }
                }
                fireButtonTime = 0;
            }

            if (keys.get(Keys.SLOTLEFT)) {
                if (!bob.isSlotMoving()) {
                    bob.startSlotMoveTimer(0.2F);
                    bob.decreaseSlot();
                }
            }

            if (keys.get(Keys.SLOTRIGHT)) {
                if (!bob.isSlotMoving()) {
                    bob.startSlotMoveTimer(0.2F);
                    bob.increaseSlot();
                }
            }

            if (keys.get(Keys.SLOTUSE)) {
                if (!bob.isSlotMoving()) {
                    bob.startSlotMoveTimer(0.2F);
                    Material strongHandMaterial = bob.getStrongHand();
                    Material slotMaterial = (Material)bob.getToolBelt().getSlots().get(bob.getSlotNo());
                    bob.setStrongHand(slotMaterial);
                    bob.getToolBelt().getSlots().put(bob.getSlotNo(), strongHandMaterial);

                }
            }

            if (keys.get(Keys.LEFT)) {
                // left is pressed
                bob.setState(Player.State.MOVING);
                bob.setTurningAntiClockwise(true);
                bob.setTurningClcokwise(false);
            } else if (keys.get(Keys.RIGHT)) {
                // right is pressed
                bob.setState(Player.State.MOVING);
                bob.setTurningClcokwise(true);
                bob.setTurningAntiClockwise(false);
            } else {
                bob.setTurningClcokwise(false);
                bob.setTurningAntiClockwise(false);
            }

            if (keys.get(Keys.STRAFELEFT) && keys.get(Keys.STRAFERIGHT)) {
                bob.setLeftAcceleration(0F);
                bob.setRightAcceleration(0F);
            } else if (keys.get(Keys.STRAFELEFT)) {
                bob.setLeftAcceleration(bob.getBoost().equals(Player.Boost.SPEED) ? 8F : 4F);
            } else if (keys.get(Keys.STRAFERIGHT)) {
                bob.setRightAcceleration(bob.getBoost().equals(Player.Boost.SPEED) ? 8F : 4F);
            } else {
                bob.setLeftAcceleration(0F);
                bob.setRightAcceleration(0F);
            }
            if (keys.get(Keys.UP)) {
                // up is pressed
                bob.setState(Player.State.MOVING);
                bob.setAcceleration(bob.getBoost().equals(Player.Boost.SPEED) ? 8F : 4F);
            } else if (keys.get(Keys.DOWN)) {
                // down is pressed
                bob.setState(Player.State.MOVING);
                bob.setAcceleration(bob.getBoost().equals(Player.Boost.SPEED) ? -8F : -4F);
            } else {
                bob.setAcceleration(0F);
                bob.setState(Player.State.IDLE);
            }
            if (keys.get(Keys.DODGE)) {
                bob.dodge(15);
            }
        }

        if (world.getFireJoystick() != null && world.getFireJoystick().getDrag() != null) {
            JoyStick fireJoystick = world.getFireJoystick();

//            float fireDeg = fireJoystick.getAngle();
//            world.getBullets().addAll(bob.fireBullet(fireDeg));
        }
        if (world.getMoveJoystick() != null && world.getMoveJoystick().getDrag() != null) {
            JoyStick moveJoystick = world.getMoveJoystick();
            float dst = moveJoystick.getDistance();
            float deg = moveJoystick.getAngle();

            if (dst > 2) {
                if (locator.locate(deg, bob.getRotation()) < 0) {
                    bob.setState(Player.State.MOVING);
                    bob.setTurningAntiClockwise(true);
                    bob.setTurningClcokwise(false);
                } else if (locator.locate(deg, bob.getRotation()) > 0) {
                    bob.setState(Player.State.MOVING);
                    bob.setTurningClcokwise(true);
                    bob.setTurningAntiClockwise(false);
                }
            } else {
                bob.setTurningClcokwise(false);
                bob.setTurningAntiClockwise(false);
            }
            if (dst > 25) {
                bob.setState(Player.State.MOVING);
                bob.setAcceleration(bob.getBoost().equals(Player.Boost.SPEED) ? 8F : 4F);
            }

        } else if (!Gdx.app.getType().equals(Application.ApplicationType.Desktop)) {
            bob.setTurningClcokwise(false);
            bob.setTurningAntiClockwise(false);
            bob.setAcceleration(0F);
        }
    }

    private void processAIInput(AIPlayer aiPlayer, float delta) {
        if (aiPlayer.getState() != Player.State.DEAD) {
            //todo targeting switched off for now
//            aiPlayer.chooseTarget(bob, aiPlayers);
//            aiPlayer.setTarget(locator.wallInbetween(aiPlayer, aiPlayer.getTarget()));
            if (aiPlayer.getTarget() == null && aiPlayer.getTargetPlayer() != null) {
                aiPlayer.ignore(aiPlayer.getTargetPlayer().getName());
            }
            world.getProjectiles().addAll(aiPlayer.decide(delta));
            if (aiPlayer.getIntent().equals(AIPlayer.Intent.KILLING)) {
                if (!aiPlayer.isUseDelayOn()) {
                    aiPlayer.startUseTimer(aiPlayer.getStrongHand().getUseTime());
                    aiPlayer.startUseDelayTimer(aiPlayer.getStrongHand().getUseDelay());
                    aiPlayer.useItem(bob, (Item)aiPlayer.getStrongHand(), aiPlayers, world.getAnimals(), world.getLevel().getFloorPads());
                }
            }
        }
    }

    private void fillView(Player player) {
        player.clearView();
        int xPos = (int)Math.floor(player.getCentrePosition().x - player.getViewCircleWidth()/2);
        int yPos = (int)Math.floor(player.getCentrePosition().y - player.getViewCircleHeight()/2);

        for (int i = 0; i < player.getViewCircleWidth(); i++) {
            for (int j = 0; j < player.getViewCircleHeight(); j++) {
                int col = xPos + i;
                int row = yPos + j;
                if (col >= 0 && row >= 0 && col < world.getLevel().getWidth() && row < world.getLevel().getHeight()) {
                    player.getView().getBlocks()[i][j] = world.getLevel().getBlock(col, row);
                }
            }
        }
//        for (FloorPad floorPad : world.getLevel().getFloorPads()) {
//            if (Intersector.overlapConvexPolygons(floorPad.getBounds(), player.getViewCircle())) {
//                player.getView().getFloorPads().add(floorPad);
//            }
//        }

        //todo think about a new Class, viewObject, which simply contains names and locations.

    }

    private Vector2 calculateVelocity(float acceleration, float rotation) {
        Vector2 velocity = new Vector2();
        rotation = (float)(rotation * (Math.PI/180));

        if (rotation < 90) {
            velocity.x = (float)Math.cos(rotation)*acceleration;
            velocity.y = (float)Math.sin(rotation)*acceleration;
        } else if (rotation < 180) {
            velocity.x = -(float)Math.cos(rotation)*acceleration;
            velocity.y = (float)Math.sin(rotation)*acceleration;
        } else if (rotation < 270) {
            velocity.x = -(float)Math.cos(rotation)*acceleration;
            velocity.y = -(float)Math.sin(rotation)*acceleration;
        } else {
            velocity.x = (float)Math.cos(rotation)*acceleration;
            velocity.y = -(float)Math.sin(rotation)*acceleration;
        }
        return velocity;
    }

    private SpawnPoint findSpawnPoint(String name) {
        Random rand = new Random();
        Map<Integer, SpawnPoint> sps = world.getLevel().getSpawnPoints();
        SpawnPoint sp = sps.get(rand.nextInt(sps.size()));
//        boolean occupied = true;
//        while (occupied) {
//            boolean huh = collisionDetector.checkSpawnPointForPlayers(sp, name);
//            if (!huh) {
//                occupied = false;
//            } else {
//                sp = sps.get(rand.nextInt(sps.size()));
//            }
//        }
        return sp;
    }

    public void startUnpauseTimer(float delay) {
        Timer.schedule(unpauseTimer, delay);
    }

    private void stopUnpauseTimer() {
        unpauseTimer.cancel();
        paused = false;
    }

    public void startPauseButtonTimer(float delay) {
        pauseButtonRestricted = true;
        Timer.schedule(pauseButtonTimer, delay);
    }

    private void stopPauseButtonTimer() {
        pauseButtonTimer.cancel();
        pauseButtonRestricted = false;
    }
}
