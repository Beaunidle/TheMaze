package com.mygdx.game.controller;

import static com.mygdx.game.model.environment.AreaAffect.AffectType.DAMAGE;
import static com.mygdx.game.model.environment.AreaAffect.AffectType.EXPLOSION;
import static com.mygdx.game.model.environment.AreaAffect.AffectType.LIGHTNING;
import static com.mygdx.game.model.items.Material.Type.CONSUMABLE;
import static com.mygdx.game.model.items.Material.Type.GRASS;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.ai.CowAi;
import com.mygdx.game.model.GameObject;
import com.mygdx.game.model.environment.AnimalSpawn;
import com.mygdx.game.model.environment.Tilled;
import com.mygdx.game.model.environment.blocks.Block;
import com.mygdx.game.model.environment.blocks.EnvironmentBlock;
import com.mygdx.game.model.environment.blocks.FillableBlock;
import com.mygdx.game.model.environment.blocks.Grower;
import com.mygdx.game.model.environment.blocks.Wall;
import com.mygdx.game.model.items.Consumable;
import com.mygdx.game.model.items.Fillable;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.items.Magic;
import com.mygdx.game.model.items.Material;
import com.mygdx.game.model.items.Placeable;
import com.mygdx.game.model.items.Swingable;
import com.mygdx.game.model.items.Throwable;
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
import com.mygdx.game.model.pads.WaterPad;
import com.mygdx.game.utils.JoyStick;
import com.mygdx.game.utils.Locator;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;

public class WorldController {

    enum Keys {
        LEFT, RIGHT, STRAFELEFT, STRAFERIGHT, UP, DOWN, USE, FIRE, INV, MAP, DODGE, PAUSE, SLOTLEFT, SLOTRIGHT, SLOTUSE
    }

    private static final float DAMP = 0.90f;
    private static final float MAX_VEL = 10f;

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
    private long gameTime;
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

//        Timer.Task gameTimer = new Timer.Task() {
//            @Override
//            public void run() {
//                levelFinished = true;
//                System.out.print(scoreBoard.toString());
//            }
//        };
//        Timer.schedule(gameTimer, 1000);
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
        gameTime = System.currentTimeMillis();
        // Processing the input - setting the states of Bob
//        if (aiPlayers.isEmpty()) {
//             levelFinished = true;
//        }
        for (int i = 0; i < world.getLevel().getWidth(); i++) {
            for (int j = 0; j < world.getLevel().getHeight(); j++) {
                Block block = world.getLevel().getBlock(i,j);
                if (block != null && !(i == block.getPosition().x && j == block.getPosition().y)) {
//                    System.out.println("Blocks gone wrong. Block: " + block.getPosition() + ", i and j: " + i + ", " +j + ". " + block.getName());
                }
            }
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

        baseTime++;
        if (baseTime == 40) {
            world.increaseMinute();
            baseTime = 0;
        }

        if (bob.getLives() <= 0) bob.setState(Sprite.State.DEAD);
        if (bob.getState().equals(Sprite.State.DEAD)) {
            processDeath(bob);
        } else {
            bob.heal();
            bob.age();
//            bob.getView().printView();
            if (!bob.isStaggered()) {
                fillView(bob);
                bob.updateHitCircle();
                if (bob.getHitPhase() != 0) {
                    Vector2 damageVector = new Vector2(bob.getHitCircle().x, bob.getHitCircle().y);
                    float damage = bob.getStrongHand() == null ? 1F : ((Swingable)bob.getStrongHand()).getDamage();
                    world.getAreaAffects().add(new AreaAffect(damageVector, "", bob.getHitCircle().radius, 0.1F, DAMAGE, bob.getName(), null, damage));
                }
                processInput();
            }
        }
        for (AIPlayer aiPlayer : aiPlayers) {
            if (aiPlayer.getLives() <= 0) aiPlayer.setState(Sprite.State.DEAD);
            aiPlayer.heal();
            aiPlayer.age();
            //todo function to check if AIs hurt AI/animals
//            if (bob.getHitCircle().radius > 0.1 && !aiPlayer.getName().equals(bob.getName()) && Intersector.overlaps(bob.getHitCircle(), aiPlayer.getBounds().getBoundingRectangle())) {
//                aiPlayer.hit(bob.getName(), bob.getStrongHand() != null ? ((Swingable)bob.getStrongHand()).getDamage() : 1F, bob.getRotation(), bob.getCentrePosition());
//            }
            if (aiPlayer.getState().equals(Player.State.DEAD)) {
                processDeath(aiPlayer);
            }
            else {
                aiPlayer.heal();
                if (!aiPlayer.isStaggered()) {
                    fillView(aiPlayer);
                    processAIInput(aiPlayer, delta);
                }
//                frameSkipCount++;
//                if (frameSkipCount == 5) frameSkipCount = 0;

            }
        }
        ListIterator<Animal> animalIterator = world.getAnimals().listIterator();
        while(animalIterator.hasNext()) {
            Animal animal = animalIterator.next();
            Vector2 pos = animal.getPosition();
            if (pos.x < 5 || pos.x > 295 || pos.y < 5 || pos.y > 295) {
                System.out.println("I am breaking the bounds of reality says " + animal.getDamageName() + ", " + animal.getPosition());
                animal.die();
                animalIterator.remove();
            }
            if (animal.getLives() <= 0) animal.setState(Sprite.State.DEAD);
            if (animal.getState().equals(Player.State.DEAD)) {
//                System.out.println("animal dead");

                EnvironmentBlock meat = animal.getBody();
                Vector2 meatPos = meat.getPosition();
                if (meatPos.x > 2 && meatPos.x < 298 && meatPos.y > 2 && meatPos.y < 298 && world.getLevel().getBlock((int)meatPos.x, (int)meatPos.y) == null) {
                    world.getBodies().add(meat);
                    world.getLevel().getBlocks()[(int)meatPos.x][(int)meatPos.y] = meat;
                }
                animal.die();
                System.out.println(animal.getDamageName() + " Dead. Population now " + animal.getSpawnPoint().getPopulation());
                animalIterator.remove();
            } else if (animal.getPosition().x < 0 || animal.getPosition().x > 300 || animal.getPosition().y < 0 || animal.getPosition().y > 300) {
                animal.die();
                animalIterator.remove();
            } else {
                animal.age();
                animal.heal();
                if (animal.getLastFoodPos() != null && animal.getViewCircle().contains(animal.getLastFoodPos())) {
                    Block block = world.getLevel().getBlock((int) animal.getLastFoodPos().x,(int)animal.getLastFoodPos().y);
                    if (!(block instanceof EnvironmentBlock && ((EnvironmentBlock) block).getMaterial().getType().equals(GRASS))) {
                        animal.setLastFoodPos(null);
                    }
                }
                if (!animal.isStaggered()) {
                    if (animal.getTarget() == null && animal.getTargetSprite() == null) {
                        if (animal.getState().equals(Sprite.State.THIRSTY)) {
                            findNearbyMaterial(animal, null, FloorPad.Type.WATER, null);
                        } else if ((animal.getState().equals(Sprite.State.HUNGRY))) {
                            findNearbyMaterial(animal, new Material(GRASS, 1), null, null);
                        } else if (animal.getState().equals(Sprite.State.HORNY)) {
                            findNearbyMaterial(animal,null, null, Animal.AnimalType.COW);
                        }
                    }
                    if (animal.getTargetSprite() != null) {
                        if (animal.getIntent().equals(Sprite.Intent.MATING)) {
                            System.out.println(animal.getDamageName() + " attempting to mate");
                            Animal otherAnimal = (Animal) animal.getTargetSprite();
                            if (animal.getPosition().dst(otherAnimal.getPosition()) < 2 && otherAnimal.getState().equals(Sprite.State.HORNY)) {
                                System.out.println("We be mating :) " + animal.getDamageName() + " and " + otherAnimal.getDamageName());
                                AnimalSpawn spawnPoint = animal.getSpawnPoint();
                                Vector2 newPos = new Vector2(animal.getCentrePosition().x + 0.5F, animal.getPosition().y + 0.5F);
                                int count = spawnPoint.getAnimalCount();
                                Animal newAnimal = new Animal(newPos, animal.getName(), spawnPoint, animal.getAnimalType(), 1, 0.5F, animal.getRotationSpeed(), new CowAi(), 10, 6, 6, count, true);
                                animalIterator.add(newAnimal);
                                spawnPoint.addAnimalToSpawn(0);
                                System.out.println(newAnimal.getDamageName() + " has been born");
                                System.out.println("Cow population: " + world.getAnimals().size());
                                animal.setBirthTime(System.currentTimeMillis());
                                otherAnimal.setBirthTime(System.currentTimeMillis());
                            }
                            animal.setState(Sprite.State.IDLE);
                            animal.setIntent(Sprite.Intent.SEARCHING);
                            animal.setTargetSprite(null);
                            animal.setTarget(null);
                            animal.getAi().setTargetCoordinates(null);
                            otherAnimal.setState(Sprite.State.IDLE);
                            otherAnimal.setIntent(Sprite.Intent.SEARCHING);
                            otherAnimal.setTargetSprite(null);
                            otherAnimal.setTarget(null);
                            otherAnimal.getAi().setTargetCoordinates(null);
                        }
                    }
                    if (animal.getTarget() != null) {
                        if (animal.getTarget() != null && animal.getIntent().equals(Sprite.Intent.DRINKING)) {
                            FloorPad floorPad = world.getLevel().getPad((int) animal.getTarget().x,(int) animal.getTarget().y);
                            if (floorPad != null && (floorPad.getType().equals(FloorPad.Type.WATERFLOW) || floorPad.getType().equals(FloorPad.Type.WATER))) {
                                animal.drink(1);
                                animal.setLastWaterPos(animal.getTarget());
                                if (animal.getWater() >= animal.getMaxWater()) {
                                    animal.setState(Sprite.State.IDLE);
                                    animal.setTarget(null);
                                    animal.setTurningAntiClockwise(false);
                                    animal.setTurningClcokwise(false);
                                    animal.getAi().setTargetCoordinates(null);
                                }
                            }
                        }
                        if (animal.getTarget() != null && animal.getIntent().equals(Sprite.Intent.EATING)) {
                            Block block = world.getLevel().getBlock((int) animal.getTarget().x,(int) animal.getTarget().y);
                            if (block instanceof EnvironmentBlock && ((EnvironmentBlock) block).getMaterial().getType().equals(GRASS)) {
                                if (block.getDurability() > 0) {
                                    animal.eat(((EnvironmentBlock)block).hit(null).get(0).getQuantity() * 0.5F);
                                    animal.setLastFoodPos(animal.getTarget());
                                    if (animal.getFood() >= animal.getMaxFood()) {
                                        animal.setState(Sprite.State.IDLE);
                                        animal.setTarget(null);
                                        animal.setTurningAntiClockwise(false);
                                        animal.setTurningClcokwise(false);
                                        animal.getAi().setTargetCoordinates(null);
                                    }
                                }
                                if (block.getDurability() <= 0){
                                    world.getLevel().getBlocks()[(int) block.getPosition().x][(int) block.getPosition().y] = null;
                                    animal.setTarget(null);
                                    animal.setTurningAntiClockwise(false);
                                    animal.setTurningClcokwise(false);
                                }
                            } else {
                                animal.setTarget(null);
                                animal.setTurningAntiClockwise(false);
                                animal.setTurningClcokwise(false);
                            }
                            animal.setIntent(Sprite.Intent.SEARCHING);
                        }
                    }
                    fillView(animal);
                    animal.decide(delta, world.isNightTime(), bob, aiPlayers, world.getAnimals());
                    Vector2 target = locator.wallInbetween(animal, animal.getTarget());
                    if (target == null && animal.getLastFoodPos() != null)

//                    System.out.println(animal.getName() + " at " + animal.getPosition());
                    if (animal.getView().getBlockingWall() != null) {
                        Block[] blockingWall = animal.getView().getBlockingWall();
//                        for (int i = 0; i < blockingWall.length; i++) {
//                            if (blockingWall[i] != null) System.out.println("Blocking block: " + blockingWall[i].getName() + ", " + blockingWall[i].getPosition());
//                        }
                    }
//                    animal.getView().printView();
                    if (animal.getTarget() != null && !animal.getTarget().equals(target)) {
//                        System.out.println("old target: " + animal.getTarget());
//                        System.out.println("new target: " + target);
                        animal.setTarget(target);
                        animal.getAi().setTargetCoordinates(null);
                    }
                }
                if (animal.getHitPhase() != 0) {
                    Vector2 damageVector = new Vector2(animal.getHitCircle().x, animal.getHitCircle().y);
                    float damage = 1F;
                    world.getAreaAffects().add(new AreaAffect(damageVector, animal.getDamageName(), animal.getHitCircle().radius, animal.getHitTime(), DAMAGE, animal.getName(), animal.getAnimalType(), damage));
                    animal.hitPhaseIncrease(1);
                }
                processAnimalInput(animal, delta);
            }
        }

        for (Grower grower : world.getLevel().getGrowers()) {
            boolean nearWater = false;
            for (FloorPad floorPad : world.getLevel().getFloorPads()) {
                if (floorPad.getType().equals(FloorPad.Type.WATER) || floorPad.getType().equals(FloorPad.Type.WATERFLOW) || (floorPad.getType().equals(FloorPad.Type.IRRIGATION) && floorPad.isConnected())) {
                    double dist = Math.sqrt(Math.pow((floorPad.getPosition().x - grower.getPosition().x), 2) + Math.pow((floorPad.getPosition().y - grower.getPosition().y), 2));
                    if (dist < 3) {
                        nearWater = true;
                        break;
                    }
                }
            }
            grower.grow((nearWater ? 10 : 5));
        }

        for (FloorPad irrigation : world.getLevel().getFloorPads()) {
            if (irrigation.getType().equals(FloorPad.Type.IRRIGATION) && irrigation.isNotify()) {
                for (FloorPad floorPad : world.getLevel().getFloorPads()) {
                    if (floorPad.getType().equals(FloorPad.Type.IRRIGATION) && !floorPad.isConnected()) {
                        boolean connected = false;
                        for (FloorPad.Connection connection : irrigation.getConnections()) {
                            if (connection.equals(FloorPad.Connection.N) && floorPad.getConnections().contains(FloorPad.Connection.S) && floorPad.getPosition().y == irrigation.getPosition().y + 1 && floorPad.getPosition().x == irrigation.getPosition().x ||
                                    connection.equals(FloorPad.Connection.S) && floorPad.getConnections().contains(FloorPad.Connection.N) && floorPad.getPosition().y == irrigation.getPosition().y - 1 && floorPad.getPosition().x == irrigation.getPosition().x ||
                                    connection.equals(FloorPad.Connection.E) && floorPad.getConnections().contains(FloorPad.Connection.W) && floorPad.getPosition().x == irrigation.getPosition().x + 1 && floorPad.getPosition().y == irrigation.getPosition().y ||
                                    connection.equals(FloorPad.Connection.W) && floorPad.getConnections().contains(FloorPad.Connection.E) && floorPad.getPosition().x == irrigation.getPosition().x - 1 && floorPad.getPosition().y == irrigation.getPosition().y) {
                                connected = true;
                                break;
                            }
                        }
                        if (connected) {
                            floorPad.setConnected(true);
                            floorPad.startNotify(1F);
                            irrigation.setNotify(false);
                        }
                    }
                }
            }
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

        //check all the moving sprites
        for (Sprite sprite : world.getMovementRects().keySet()) {
            sprite.move(delta);
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

        for (ExplodableBlock eb : world.getLevel().getExplodableBlocks()) {
            if (!eb.getState().equals(ExplodableBlock.State.RUBBLE)) {
                if (eb.getState().equals(ExplodableBlock.State.BANG)) {
                    world.getAreaAffects().add(new AreaAffect(new Vector2(eb.getPosition().x + ExplodableBlock.getSIZE()/2, eb.getPosition().y + ExplodableBlock.getSIZE()/2), "explosion", 2, 2, EXPLOSION, "", null, 2));
                    eb.setState(ExplodableBlock.State.RUBBLE);
                } else {
                    collisionDetector.checkExplodableCollisionWithExplosion(eb);
                }
            }
        }

        Iterator<EnvironmentBlock> blockIterator = world.getBodies().iterator();
        while (blockIterator.hasNext()) {
            EnvironmentBlock body = blockIterator.next();
            if (body.getDurability() <= 0) {
                world.getLevel().getBlocks()[(int)body.getPosition().x] [(int)body.getPosition().y] = null;
                blockIterator.remove();

            }
        }

        for (AnimalSpawn animalSpawn : world.getLevel().getAnimalSpawnPoints()) {
            animalSpawn.checkSpawn(gameTime);
            if (animalSpawn.isReadyToAdd()) {
                Animal animal = animalSpawn.addAnimal();
                if (animal != null) {
                    Vector2 pos = animal.getPosition();
                    if (pos.x <= 0 || pos.x >= world.getLevel().getWidth() || pos.y <= 0 || pos.y >= world.getLevel().getHeight()) animal.die();
                    else  {
                        world.getAnimals().add(animal);
                        animalSpawn.addAnimalToSpawn(gameTime);
                    }
                }
            }
        }

        for (int i = 0; i < world.getLevel().getWidth(); i++) {
            for (int j = 0; j < world.getLevel().getHeight(); j++) {
                Block block = world.getLevel().getBlock(i, j);
                if (block instanceof EnvironmentBlock) {
                    EnvironmentBlock eb = (EnvironmentBlock) block;

                    eb.replenish(gameTime);
                    eb.degrade(gameTime);
                    eb.checkSpread(gameTime);
                    //todo handle chosen place somewhere else, call it with a method
                    if (eb.isWantToSpread() && eb.getDurability() == eb.getMaxDurability()) {
                        Vector2 chosenPlace = choosePlaceToGrow(eb, world.getLevel().getBlocks());
                        boolean withinWorld = chosenPlace != null && chosenPlace.x > 5 && chosenPlace.x < 295 && chosenPlace.y > 5 && chosenPlace.y < 295;
                        if (withinWorld) world.getLevel().getBlocks()[(int) chosenPlace.x][(int) chosenPlace.y] = new EnvironmentBlock(chosenPlace, new Material(GRASS, 1), null, 2, 30, 0, 200, false, null, 10, "grass", 1, 1);;
                        eb.setWantToSpread(false);
                        eb.setLastSpread(gameTime);
                    }
                }
            }
        }
    }

    public void findNearbyMaterial(Sprite sprite, Material material, FloorPad.Type water, Animal.AnimalType animal) {
        if (animal != null) {
            for (Animal a : world.getAnimals()) {
                if (!sprite.equals(a) && sprite.getViewCircle().contains(a.getCentrePosition()) && a.getAnimalType().equals(Animal.AnimalType.COW) && a.getState().equals(Sprite.State.HORNY)) {
                    sprite.setTargetSprite(a);
                    return;
                }
            }
        }
        int offSet = 1;
        Vector2 pos = sprite.getPosition();
        while (offSet < 10) {
            for (int i=-offSet; i<=offSet; i++) {
                for (int j=-offSet; j<=offSet; j++) {
                    if (i == offSet || i == -offSet || j == offSet || j == -offSet) {
                        if (material != null) {
                            Block block = world.getLevel().getBlock((int)(pos.x + i), (int)(pos.y+j));
                            if (block instanceof EnvironmentBlock && ((EnvironmentBlock) block).getMaterial().getType().equals(material.getType()) && block.getDurability() > 0
                                    && sprite.getViewCircle().contains(block.getPosition())) {
                                sprite.setTarget(new Vector2(block.getPosition().x + 0.5F, block.getPosition().y + 0.5F));
                                return;
                            }
                        }
                        if (water != null) {
                            FloorPad floorPad = world.getLevel().getPad((int)(pos.x + i), (int)(pos.y+j));
                            if (floorPad != null && (floorPad.getType().equals(FloorPad.Type.WATER) || floorPad.getType().equals(FloorPad.Type.WATERFLOW))) {
                                sprite.setTarget(new Vector2(floorPad.getPosition().x + 0.5F, floorPad.getPosition().y + 0.5F));
                                return;
                            }
                        }
                    }
                }
            }
            offSet++;
        }
    }
    private Vector2 choosePlaceToGrow(Block block, GameObject[][] blocks) {
        Vector2 pos = block.getPosition();
        if (pos.x > 2 && pos.x < 298 && pos.y > 2 && pos.y < 298) {
            Random rand = new Random();
            int myInt = rand.nextInt(10);
            switch (myInt) {
                case 0:
                    return null;
                case 1:
                    if (blocks[(int) (pos.x-1)][(int) (pos.y+1)] == null) return new Vector2(pos.x-1, pos.y+1);
                    break;
                case 2:
                    if (blocks[(int) (pos.x)][(int) (pos.y+1)] == null) return new Vector2(pos.x, pos.y+1);
                    break;
                case 3:
                    if (blocks[(int) (pos.x+1)][(int) (pos.y+1)] == null) return new Vector2(pos.x+1, pos.y+1);
                    break;
                case 4:
                    if (blocks[(int) (pos.x-1)][(int) (pos.y)] == null) return new Vector2(pos.x-1, pos.y);
                    break;
                case 5:
                    if (blocks[(int) (pos.x)][(int) (pos.y)] == null) return new Vector2(pos.x, pos.y);
                    break;
                case 6:
                    if (blocks[(int) (pos.x+1)][(int) (pos.y)] == null) return new Vector2(pos.x+1, pos.y);
                    break;
                case 7:
                    if (blocks[(int) (pos.x-1)][(int) (pos.y-1)] == null) return new Vector2(pos.x-1, pos.y-1);
                    break;
                case 8:
                    if (blocks[(int) (pos.x)][(int) (pos.y-1)] == null) return new Vector2(pos.x, pos.y-1);
                    break;
                case 9:
                    if (blocks[(int) (pos.x+1)][(int) (pos.y-1)] == null) return new Vector2(pos.x+1, pos.y-1);
                    break;
            }
        }
        return null;
    }
    private void processDeath(Player player) {
        scoreBoard.addDeath(player.getName());
        if (player.getKilledBy() != null) {
//                scoreBoard.addKill(bob.getKilledBy());
            player.setKilledBy(null);
        }
        world.getBloodStains().add(new BloodStain(player.getPosition(), player.getName()));

        player.respawn(player.getPersonalSpawn() != null ? player.getPersonalSpawn() : findSpawnPoint(player.getName()).getPosition());
    }

    private void setAction (float delta, Sprite sprite) {
        // Convert acceleration to frame time
        //sprite.setAcceleration(sprite.getAcceleration() * delta);

//        if (sprite.isTurningAntiClockwise()) {
//            sprite.rotateAntiClockwise(delta);
//        }
//        if (sprite.isTurningClcokwise()) {
//            sprite.rotateClockwise(delta);
//        }

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
            }
            if (sprite.getRightAcceleration() > 0) {
                Vector2 rightVelocity = (calculateVelocity(sprite.getRightAcceleration() + sprite.getDodge(), sprite.getRotation() + 270));
                sprite.getVelocity().add(rightVelocity);
            }
            if (sprite.getDodge() != 0) {
                sprite.setDodge(sprite.getDodge()*0.85F);
                if (sprite.getDodge() < 1 && sprite.getDodge() > -1) sprite.setDodge(0);
            }
        }

        if (sprite instanceof Animal) {
            if (sprite.getVelocity().x > 10 || sprite.getVelocity().y > 10 || sprite.getVelocity().x < -10 || sprite.getVelocity().y < -10) {
                System.out.println("Is this big?");
            }
        }
        boolean instanceOfPlayer = sprite instanceof Player;
        //apply world effects
        collisionDetector.checkPlayerCollisionWithFloorPads(sprite);

        // checking collisions with the surrounding blocks depending on Bob's velocity
        collisionDetector.checkPlayerCollisionWithBlocks(delta, sprite);

        //effects after moving todo move this to outside individual player moves
        collisionDetector.checkPlayerCollisionWithAreaAffects(sprite);
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
        if (sprite.getVelocity().y > MAX_VEL) {
            sprite.getVelocity().y = MAX_VEL;
        }
        if (sprite.getVelocity().y < -MAX_VEL) {
            sprite.getVelocity().y = -MAX_VEL;
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
            if (keys.get(Keys.INV)) {
                setPaused(true);
                setFillableToShow(new FillableBlock(new Vector2(bob.getCentrePosition()), 10, FillableBlock.FillableType.INVSCREEN, 0, 0, 0, world.getRecipeHolder(), ""));
                return;
            }
            if (keys.get(Keys.MAP)) {
                setPaused(true);
                setFillableToShow(new FillableBlock(new Vector2(bob.getCentrePosition()), 10, FillableBlock.FillableType.MAPSCREEN, 0, 0, 0, world.getRecipeHolder(), ""));
                return;
            }

            if (keys.get(Keys.LEFT) && keys.get(Keys.RIGHT)) {
                bob.setTurningAntiClockwise(false);
                bob.setTurningClcokwise(false);
            } else if (keys.get(Keys.LEFT)) {
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
                if (bob.getDodge() <= 0) {
                    bob.dodge(15);
                }
            }

            if (keys.get(Keys.USE)) {
                use(bob);
            } else {
                bob.setBlocking(false);
            }

            if (keys.get(Keys.FIRE)) {
                //todo stuff that fires continuously when firing
                //todo this is fun, make a spell to do this
//                if (bob.getStrongHand() != null && !bob.isUseDelayOn()) {
//                    if (bob.getStrongHand() instanceof Magic) {
//                        Magic magic = (Magic) bob.getStrongHand();
//                        if (bob.getMana() >= 0.2F) {
//                            switch (magic.getMagicType()) {
//                                case PROJECTILE:
//                                    switch (magic.getProjectileType()) {
//                                        case FIREBALL:
//                                            bob.startUseTimer(0.01F);
//                                            bob.startUseDelayTimer(0.01F);
//                                            Projectile projectile = new Projectile(new Vector2(bob.getLeftHandPosition(140, bob.getWidth() / 2)), bob.getRotation(), bob.getName(), 3, Projectile.ProjectileType.FIREBALL, 0, false);
//                                            world.getProjectiles().add(projectile);
//                                            bob.useMana((0.2F));
//                                            break;
//                                        default:
//                                            break;
//                                    }
//                                    break;
//                            }
//                        }
//                    }
//                }
            } else {
                if (fireButtonTime != 0) {
                    long buttonPressTime = System.currentTimeMillis() - fireButtonTime;
                    longPressFire(bob, buttonPressTime);
                    fireButtonTime = 0;
                }
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

        if (bob.getRotateBy() < 0) {
            bob.setTurningAntiClockwise(false);
            bob.setTurningClcokwise(true);
            bob.setRotateBy(bob.getRotateBy()+5);
        }
        if (bob.getRotateBy() > 0) {
            bob.setTurningAntiClockwise(true);
            bob.setTurningClcokwise(false);
            bob.setRotateBy(bob.getRotateBy()-5);
        }
    }

    public List<Sprite> getSpritesToHit(Sprite sprite) {
        List<Sprite> spritesToHit = new ArrayList<>();
        Circle hitCircle = sprite.getHitCircle();
        if (!bob.getName().equals(sprite.getName()) && Intersector.overlaps(hitCircle, sprite.getBounds().getBoundingRectangle())) {
            spritesToHit.add(bob);
        }
        for (AIPlayer aiPlayer : aiPlayers) {
            if (!aiPlayer.getName().equals(sprite.getName()) && Intersector.overlaps(hitCircle, aiPlayer.getBounds().getBoundingRectangle())) {
                spritesToHit.add(aiPlayer);
            }
        }

        for (Animal animal : world.getAnimals()) {
            boolean sameSpecies = sprite instanceof Animal && animal.getAnimalType().equals(((Animal) sprite).getAnimalType());
            if (!sameSpecies && Intersector.overlaps(hitCircle, animal.getBounds().getBoundingRectangle())) {
                spritesToHit.add(animal);
            }
        }
        return spritesToHit;
    }

    public void longPressFire(Player player, Long buttonPressTime) {
        boolean poweredUp = buttonPressTime > 800;
        if (!player.isUseDelayOn()) {
            List<Sprite> spritesToHit = getSpritesToHit(player);

            GameObject[][] blocks = world.getLevel().getBlocks();
            Point gridRef = player.getGridRef(player.getRotation(), player.getCentrePosition().x, player.getCentrePosition().y);
            if (player.getStrongHand() != null ) {
                player.startUseTimer(player.getStrongHand().getUseTime());
                player.startUseDelayTimer(player.getStrongHand().getUseDelay());
                if (player.getStrongHand() instanceof Item) {
                    Object o = blocks[gridRef.x][gridRef.y];
                    Item item = (Item) player.getStrongHand();
//                    player.updateHitCircle(item);
                    switch (item.getItemType()) {
                        case MAGIC:
                            handleUseMagic(player, poweredUp);
                            break;
                        case SWINGABLE:
                            handleUseSwingable(player, item, blocks, gridRef, o, spritesToHit);
                            break;
                        case PLACEABLE:
                            handleUsePlaceable(player, item, blocks, gridRef, o);
                            break;
                        case THROWABLE:
                            handleUseThrowable(player, item, poweredUp);
                            break;
                        case JAR:
                            handleUseJar(player, item);
                            break;
                        default:
                            break;
                    }
                } else {
                    Material material = player.getStrongHand();
                    if (material.getType().equals(CONSUMABLE)) {
                        //eat
                        player.eat((Consumable) material);
                        player.getStrongHand().setQuantity(player.getStrongHand().getQuantity() - 1);
                        if (player.getStrongHand().getQuantity() <= 0) player.setStrongHand(null);
                    }
                }
            } else {
                player.startUseTimer(0.25F);
                player.startUseDelayTimer(0.25F);
                player.hitPhaseIncrease(4);
            }
        }
    }

    public void handleUseMagic(Player player, boolean poweredUp) {
        Magic magic = (Magic) player.getStrongHand();
        if (player.getMana() >= magic.getManaRequired()) {
            switch (magic.getMagicType()) {
                case PROJECTILE:
                    switch (magic.getProjectileType()) {
                        case FIREBALL:
                            //todo HERERERERERE Figure out why I wrote this
                            world.getProjectiles().add(new Projectile(new Vector2(player.getLeftHandPosition(140, player.getWidth()/2)), player.getRotation(), player.getName(), 3, Projectile.ProjectileType.FIREBALL, 0, poweredUp));
                            player.useMana(magic.getManaRequired() + (poweredUp ? 2 : 0));
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
                                world.getAreaAffects().add(new AreaAffect(player.getLeftHandPosition(0, 6),"lightning", 2, 1, LIGHTNING, player.getName(), null, 2.7F));
                                player.useMana(magic.getManaRequired());
                            }
                    }
                    break;
                case SELF:
                    switch (magic.getAttribute()) {
                        case "healing":
                            if (player.getLives() < player.getMaxLives()) {
                                player.increaseLife(magic.getEffect());
                                player.useMana(magic.getManaRequired() + (poweredUp ? 2 : 0));
                                player.setBoost(Player.Boost.HEALING, poweredUp ? 4 : 2);
                            }
                            break;
                        default:
                            break;
                    }
                    break;
            }
        }
    }

    public void handleUseSwingable(Player player, Item item, GameObject[][] blocks, Point gridRef, Object o, List<Sprite> spritesToHit) {
        Swingable swingable = (Swingable) item;

        //find block ahead
        List<EnvironmentBlock> ebList = new ArrayList<>();
        for (GameObject[] value : blocks) {
            for (Object ob : value) {
                if (ob instanceof EnvironmentBlock) {
                    EnvironmentBlock eb = (EnvironmentBlock) ob;
                    if (eb.getMaterial().isMineable() && Intersector.overlaps(new Circle(player.getLeftHandPosition(45, 1).x, player.getLeftHandPosition(45, 1).y, 1), eb.getBounds().getBoundingRectangle()) && eb.getDurability() > 0) {
                        ebList.add((EnvironmentBlock) ob);
                    }
                }
            }
        }

        if (swingable.getDurability() > 0) {
            switch (swingable.getSwingableType()) {
                case PICK:
                case AXE:
                case SWORD:
                case CLUB:
                case HAMMER:
                    for (EnvironmentBlock eb : ebList) {
                        if (!(eb.getMaterial().getType().equals(Material.Type.GRASS) || eb.getMaterial().getType().equals(CONSUMABLE))) {
                            player.addAllToInventory(eb.hit(swingable));
                            swingable.use();
                        }
                    }
                    swingable.use();
                    player.hitPhaseIncrease(1);
                    player.increaseCombo();
                    break;
                case HOE:
                    if (o == null) {
                        Tilled tilled = new Tilled(new Vector2(gridRef.x, gridRef.y), "tilled");
                        player.hitPhaseIncrease(1);
                        blocks[gridRef.x][gridRef.y] = tilled;
                    }
                    break;
                case SHOVEL:
                    if (o == null) {
                        FloorPad irrigation = new FloorPad("irrigation", new Vector2(gridRef.x, gridRef.y), FloorPad.Type.IRRIGATION, item.getRotation());
                        irrigation.checkConnections(world.getLevel().getFloorPads());
                        player.hitPhaseIncrease(1);
                        world.getLevel().getFloorPads().add(irrigation);
                    }
            }
        }
    }

    public void handleUsePlaceable(Player player, Item item, GameObject[][] blocks, Point gridRef, Object o) {
        Placeable placeable = (Placeable) item;

        if (placeable.getPlaceableType().equals(Placeable.PlaceableType.WALL) || placeable.getPlaceableType().equals(Placeable.PlaceableType.DOOR)) {
            boolean isDoor = placeable.getPlaceableType().equals(Placeable.PlaceableType.DOOR);
            int rotation = placeable.getRotation();

            if (o instanceof Wall) {
                if (!((Wall) o).isWallFull(rotation)) {
                    ((Wall) o).addWall(Block.getSIZE(), Block.getSIZE()/4, rotation, isDoor);
                    player.getStrongHand().setQuantity(player.getStrongHand().getQuantity() - 1);
                }
            } else if (o == null) {
                blocks[gridRef.x][gridRef.y] = new Wall(new Vector2(gridRef.x, gridRef.y), rotation, Block.getSIZE(), Block.getSIZE()/4, isDoor);
                player.getStrongHand().setQuantity(player.getStrongHand().getQuantity() - 1);
            }
            if (player.getStrongHand().getQuantity() <= 0 && !player.getInventory().checkItem(new Placeable(Placeable.PlaceableType.WALL, 10))) {
                player.setStrongHand(null);
            }
        } else {
            int rotation = placeable.getRotation();
            if (o == null) {
                //todo make this a separate method
                if (placeable.getPlaceableType().equals(Placeable.PlaceableType.BED)) {
                    blocks[gridRef.x][gridRef.y] = new Block(new Vector2(gridRef.x, gridRef.y), 10, placeable.getWidth(), rotation, Block.BlockType.BED);
                    player.setPersonalSpawn(new Vector2(gridRef.x, gridRef.y));
                    System.out.println("Spawn point is being reset to " + player.getPersonalSpawn());
                } else if (placeable.getPlaceableType().equals(Placeable.PlaceableType.HOUSE)) {
                    //todo buildings with an interior
                } else {
                    FillableBlock.FillableType fillableType = null;
                    String name = null;
                    switch (placeable.getPlaceableType()) {
                        case CAMPFIRE:
                            fillableType = FillableBlock.FillableType.CAMPFIRE;
                            name = "fire";
                            break;
                        case BENCHHEALER:
                            fillableType = FillableBlock.FillableType.BENCHHEALER;
                            name = "bench-healer";
                            break;
                        case STONEANVIL:
                            fillableType = FillableBlock.FillableType.STONEANVIL;
                            name = "bench-stone";
                            break;
                        case TORCH:
                            fillableType = FillableBlock.FillableType.TORCH;
                            name = "torch";
                            break;
                        case CHEST:
                            fillableType = FillableBlock.FillableType.CHEST;
                            name = "chest";
                            break;
                    }
                    if (fillableType != null) {
                        FillableBlock fillableBlock = new FillableBlock(new Vector2(gridRef.x, gridRef.y), 10, fillableType, placeable.getWidth(), placeable.getHeight(), rotation, world.getRecipeHolder(), name);
//                        blocks[gridRef.x][gridRef.y] = fillableBlock;
//                        System.out.println("Grid ref: " + gridRef.x + "," + gridRef.y);
                        for (int i = 0; i < placeable.getWidth(); i++) {
                            for (int j = 0; j < placeable.getHeight(); j++) {
                                if (rotation == 0) {
                                    blocks[gridRef.x + i][gridRef.y + j] = fillableBlock;
                                    System.out.println((gridRef.x + i) + "," + (gridRef.y + j));
                                }
                                if (rotation == 90) {
                                    blocks[gridRef.x - j][gridRef.y + i] = fillableBlock;
                                    System.out.println((gridRef.x - j) + "," + (gridRef.y + i));
                                }
                                if (rotation == 180) {
                                    blocks[gridRef.x - i][gridRef.y - j] = fillableBlock;
                                    System.out.println((gridRef.x - i) + "," + (gridRef.y - j));
                                }
                                if (rotation == 270) {
                                    blocks[gridRef.x + j][gridRef.y - i] = fillableBlock;
                                    System.out.println((gridRef.x + j) + "," + (gridRef.y - i));
                                }
                            }
                        }
                    }
                }
                player.getStrongHand().setQuantity(player.getStrongHand().getQuantity() - 1);
            }
            if (player.getStrongHand().getQuantity() <= 0) {
                //todo look in inventory for other stacks of same item
                player.setStrongHand(null);
            }
        }
    }

    public void handleUseThrowable(Player player, Item item, boolean poweredUp) {
        //todo think of all the variables that will go into calculating these
        Throwable throwable = (Throwable) item;
        Projectile projectile = null;
        switch (throwable.getThrowableType()) {
            case PEBBLE:
                projectile = new Projectile(player.getLeftHandPosition(45, player.getWidth()/2), player.getRotation(), player.getName(), 1, Projectile.ProjectileType.PEBBLE, 0, poweredUp);
                break;
            case SPEAR:
                projectile = new Projectile(new Vector2(player.getLeftHandPosition(140, player.getWidth()/2)), player.getRotation(), player.getName(), 2, Projectile.ProjectileType.SPEAR, 0, poweredUp);
                break;
        }
        if (projectile != null) {
            world.getProjectiles().add(projectile);
            player.getStrongHand().setQuantity(player.getStrongHand().getQuantity() - 1);
        }

        if (player.getStrongHand().getQuantity() <= 0 && !player.getInventory().checkInventory(new Throwable(Throwable.ThrowableType.SPEAR, 1))) player.setStrongHand(null);
    }

    public void handleUseJar(Player player, Item item) {
        if (item.getItemType().equals(Item.ItemType.JAR)) {
            Fillable jar = (Fillable) item;
            if (jar.isFilled()) {
                player.drink(5);
                jar.setFilled(false);
            }
            for (FloorPad floorPad : world.getLevel().getFloorPads()) {
                if (floorPad.getType().equals(FloorPad.Type.WATER) || floorPad.getType().equals(FloorPad.Type.WATERFLOW)) {
                    if (Intersector.overlapConvexPolygons(floorPad.getBounds(), player.getBounds())) {
                        jar.setFilled(true);
                    }
                }
            }
        }
    }

    public void use(Player player) {
//        world.getBullets().addAll(bob.fireBullet(bob.getRotation()));
        //rotate a wall block ready to be placed
        if (player.getStrongHand() != null) {
            if (player.getStrongHand() instanceof Item) {
                Item item = (Item) player.getStrongHand();
                if (item instanceof Placeable ||
                        (item instanceof Swingable && ((Swingable) item).getSwingableType().equals(Swingable.SwingableType.SHOVEL))) {
                    item.rotate();
                }
            }
        }
        //raise shield
        if (player.getWeakHand() != null && player.getWeakHand().getItemType().equals(Item.ItemType.SHIELD)) {
            player.setBlocking(true);
        }
        //use your hand on the object in front
        if (!player.isUseDelayOn()) {
//            bob.startUseTimer(0.25F);
            player.startUseDelayTimer(0.25F);
//            Block block = player.useHand(world.getAIPlayers(), world.getAnimals(), world.getLevel().getFloorPads());

            for (FloorPad floorPad : world.getLevel().getFloorPads()) {
                if (floorPad.getType().equals(FloorPad.Type.WATER) || floorPad.getType().equals(FloorPad.Type.WATERFLOW)) {
                    if (Intersector.overlapConvexPolygons(floorPad.getBounds(), player.getBounds())) {
                        player.drink(5);
                    }
                }
            }

            for (Block[] value : player.getView().getBlocks()) {
                for (Object o : value) {
                    if (o != null && Intersector.overlaps(new Circle(player.getLeftHandPosition(45, 0.5F).x, player.getLeftHandPosition(45, 0.5F).y, 0.5F), ((Block) o).getBounds().getBoundingRectangle())) {
                        if (o instanceof EnvironmentBlock) {
                            EnvironmentBlock eb = (EnvironmentBlock) o;
                            if (eb.getDurability() > 0) {
                                player.addAllToInventory(eb.hit(null));
                                if (eb.getMaterial().getType().equals(GRASS) && eb.getDurability() <= 0) world.getLevel().getBlocks()[(int) eb.getPosition().x][(int) eb.getPosition().y] = null;
                            }
                        }
                        if (o instanceof Wall) {
                            Wall wallBlock = (Wall) o;
                            for (Wall.WallType wall : wallBlock.getWalls().values()) {
                                if (wall != null && wall.isDoor()) {
                                    if (Intersector.overlaps(player.getHitCircle(), wall.getBounds().getBoundingRectangle())) {
                                        wall.toggleOpen();
                                    }
                                }
                            }
                        }
                        if ((o instanceof FillableBlock)) {
                            if (((FillableBlock) o).getFillableType().equals(FillableBlock.FillableType.TORCH)) {
                                ((FillableBlock) o).toggleActive();
                                return;
                            }
                            setFillableToShow((FillableBlock) o);
                            return;
                        }
                        if (o instanceof Grower) {
                            Grower grower = (Grower) o;
                            if (grower.getGrowthState().equals(Grower.GrowthState.MATURE)) player.addToInventory(grower.harvest());
                        }
                        if (o instanceof Tilled) {
                            if (player.getStrongHand() != null && player.getStrongHand().isPlantable()) {
                                GameObject[][] blocks = world.getLevel().getBlocks();
                                switch (player.getStrongHand().getType()) {
                                    case CONSUMABLE:
                                        Grower grower = null;
                                        switch (((Consumable) player.getStrongHand()).getConsumableType()) {
                                            case POTATO:
                                                grower = new Grower(new Vector2(((Tilled) o).getPosition().x, ((Tilled) o).getPosition().y), Grower.CropType.POTATO, "potato");
                                                break;
                                            case MELON:
                                                grower = new Grower(new Vector2(((Tilled) o).getPosition().x, ((Tilled) o).getPosition().y), Grower.CropType.MELON, "melon");
                                                break;
                                            case CARROT:
                                                grower = new Grower(new Vector2(((Tilled) o).getPosition().x, ((Tilled) o).getPosition().y), Grower.CropType.CARROT, "carrot");
                                                break;
                                        }
                                        blocks[Math.round(((Tilled) o).getPosition().x)][Math.round(((Tilled) o).getPosition().y)] = grower;
                                        world.getLevel().getGrowers().add(grower);
                                        player.getStrongHand().setQuantity(player.getStrongHand().getQuantity() - 1);
                                        if (player.getStrongHand().getQuantity() <= 0) player.setStrongHand(null);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (player.getGun() != null && !player.getGun().fullAmmo()) {
            player.getGun().reload();
        }
    }

    private void processAIInput(AIPlayer aiPlayer, float delta) {
        if (aiPlayer.getState() != Player.State.DEAD) {
            //todo targeting switched off for now
            aiPlayer.chooseTarget(bob, aiPlayers);
            aiPlayer.setTarget(locator.wallInbetween(aiPlayer, aiPlayer.getTarget()));
            if (aiPlayer.getTarget() == null && aiPlayer.getTargetSprite() != null) {
                aiPlayer.ignore(aiPlayer.getTargetSprite().getName());
            }
            world.getProjectiles().addAll(aiPlayer.decide(delta));
            if (aiPlayer.getIntent().equals(AIPlayer.Intent.KILLING)) {
                if (!aiPlayer.isUseDelayOn()) {
                    aiPlayer.startUseTimer(aiPlayer.getStrongHand().getUseTime());
                    aiPlayer.startUseDelayTimer(aiPlayer.getStrongHand().getUseDelay());
//                    aiPlayer.useItem((Item)aiPlayer.getStrongHand());
                }
            }
        }
    }

    private void processAnimalInput(Sprite animal, float delta) {
        if (animal.getIntent().equals(Sprite.Intent.KILLING)) {
//            if (!animal.isUseDelayOn()) {
//                animal.startUseTimer(0.25F);
//                animal.startUseDelayTimer(0.35F);
//                getSpritesToHit(animal);
////                    animal.useItem((Item)animal.getStrongHand());
//            }
        }
    }

    private void fillView(Sprite sprite) {
        sprite.clearView();
        int xPos = (int)Math.floor(sprite.getCentrePosition().x - (float)sprite.getView().getBlocks().length/2);
        int yPos = (int)Math.floor(sprite.getCentrePosition().y - (float)sprite.getView().getBlocks()[0].length/2);

        for (int i = 0; i < sprite.getView().getBlocks().length; i++) {
            for (int j = 0; j < sprite.getView().getBlocks()[0].length; j++) {
                int col = xPos + i;
                int row = yPos + j;
                if (col >= 0 && row >= 0 && col < world.getLevel().getWidth() && row < world.getLevel().getHeight()) {
                    Block block = world.getLevel().getBlock(col, row);

                    //&& locator.wallInbetween(sprite, block.getPosition()) != null
                    if (block != null) {
                        sprite.getView().getBlocks()[i][j] = block;
                    }
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
