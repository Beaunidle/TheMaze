package com.mygdx.game.controller;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.model.AIPlayer;
import com.mygdx.game.model.BloodStain;
import com.mygdx.game.model.Bullet;
import com.mygdx.game.model.ExplodableBlock;
import com.mygdx.game.model.Explosion;
import com.mygdx.game.model.FloorPad;
import com.mygdx.game.model.GunPad;
import com.mygdx.game.model.Player;
import com.mygdx.game.model.ScoreBoard;
import com.mygdx.game.model.SpawnPoint;
import com.mygdx.game.model.World;
import com.mygdx.game.screens.LoadingScreen;
import com.mygdx.game.utils.JoyStick;
import com.mygdx.game.utils.Locator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class WorldController {

    enum Keys {
        LEFT, RIGHT, UP, DOWN, USE, FIRE
    }

    private static final float DAMP = 0.90f;
    private static final float MAX_VEL = 4f;

    private Game game;
    private World world;
    private Player bob;
    private List<AIPlayer> aiPlayers;
    private CollisionDetector collisionDetector;
    private int level = 1;
    private boolean levelFinished;
    private Locator locator;
    private ScoreBoard scoreBoard;

    private static Map<Keys, Boolean> keys = new HashMap<>();

    static {
        keys.put(Keys.LEFT, false);
        keys.put(Keys.RIGHT, false);
        keys.put(Keys.UP, false);
        keys.put(Keys.DOWN, false);
        keys.put(Keys.USE, false);
        keys.put(Keys.FIRE, false);
    }

    public WorldController(World world, Game game) {
        this.game = game;
        this.world = world;
        this.bob = world.getBob();
        this.aiPlayers = world.getAIPlayers();
        collisionDetector = new CollisionDetector(world, bob, aiPlayers);
        levelFinished = false;
        locator = new Locator();
        scoreBoard = new ScoreBoard(bob, aiPlayers);

        Timer.Task gameTimer = new Timer.Task() {
            @Override
            public void run() {
                levelFinished = true;
//                System.out.print(scoreBoard.toString());
            }
        };
        Timer.schedule(gameTimer, 120);
    }

    public boolean isLevelFinished() {
        return levelFinished;
    }

    public int getLevel() {
        return level;
    }

    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    // ** Key presses and touches **************** //

    public void leftPressed() {
        keys.get(keys.put(Keys.LEFT, true));
    }

    public void rightPressed() {
        keys.get(keys.put(Keys.RIGHT, true));
    }

    public void upPressed() {
        keys.get(keys.put(Keys.UP, true));
    }

    public void downPressed() {
        keys.get(keys.put(Keys.DOWN, true));
    }

    public void usePressed() {
        keys.get(keys.put(Keys.USE, true));
    }

    public void firePressed() {
        keys.get(keys.put(Keys.FIRE, true));
    }

    public void leftReleased() {
        keys.get(keys.put(Keys.LEFT, false));
    }

    public void rightReleased() {
        keys.get(keys.put(Keys.RIGHT, false));
    }

    public void upReleased() {
        keys.get(keys.put(Keys.UP, false));
    }

    public void downReleased() {
        keys.get(keys.put(Keys.DOWN, false));
    }

    public void useReleased() {
        keys.get(keys.put(Keys.USE, false));
    }

    public void fireReleased() {
        keys.get(keys.put(Keys.FIRE, false));
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

        //check health of players and set control instructions
        if (bob.getState().equals(Player.State.DEAD)) {
            scoreBoard.addDeath(bob.getName());
            if (bob.getKilledBy() != null) {
                scoreBoard.addKill(bob.getKilledBy());
                bob.setKilledBy(null);
            }
            world.getBloodStains().add(new BloodStain(bob.getPosition(), bob.getName()));
            SpawnPoint sp = findSpawnPoint(bob.getName());
            bob.respawn(sp.getPosition());
        } else {
            bob.heal();
            fillView(bob);
//            bob.getView().printView();
            processInput();
        }
        for (AIPlayer aiPlayer : aiPlayers) {
            if (aiPlayer.getState().equals(Player.State.DEAD)) {
                System.out.println("player dead");
                scoreBoard.addDeath(aiPlayer.getName());
                if (aiPlayer.getKilledBy() != null) {
                    scoreBoard.addKill(aiPlayer.getKilledBy());
                    aiPlayer.setKilledBy(null);
                }
                world.getBloodStains().add(new BloodStain(aiPlayer.getPosition(), aiPlayer.getName()));
                System.out.println("finding spawn");

                SpawnPoint sp = findSpawnPoint(aiPlayer.getName());
                aiPlayer.respawn(sp.getPosition());
                System.out.println("Respawn complete");
            }
            else {
                aiPlayer.heal();
                fillView(aiPlayer);
                processAIInput(aiPlayer, delta);
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
            else {
            }
        }

        //check collisions and move bullets
        if (world.getBullets() != null && !world.getBullets().isEmpty()) {
            Iterator bulletIterator = world.getBullets().iterator();
            while (bulletIterator.hasNext()) {
                Bullet bullet = (Bullet)bulletIterator.next();
                if (bullet.getSpeed() == 0 && !bullet.isExploding()) {
                    bulletIterator.remove();
                    continue;
                }
                if (bullet.getSpeed() > 0) {

                    if (bullet.isHoming() && bullet.isActivated()) {
                        bullet.chooseTarget(bob, aiPlayers);

                        if (bullet.getTarget() != null) {
                            Vector2 distance = new Vector2(bullet.getTarget().getCentrePosition()).sub(bullet.getPosition());
                            double rot = Math.atan2(distance.y, distance.x);
                            float deg = locator.getAngle(distance);

                            if (locator.locate(deg, bullet.getRotation()) < 0) {
                                bullet.rotateAntiClockwise(delta);
                            } else if (locator.locate(deg, bullet.getRotation()) > 0) {
                                bullet.rotateClockwise(delta);
                            }
                        }
                    }

                    bullet.setVelocity(calculateVelocity(bullet.getSpeed() * delta, bullet.getRotation()));
                    collisionDetector.checkBulletCollisionWithBlocks(bullet, delta);
                    collisionDetector.checkBulletCollisionWithPlayers(bullet, delta);

                    bullet.getPosition().add(bullet.getVelocity().x, bullet.getVelocity().y);
                    bullet.getViewCircle().setPosition(bullet.getPosition().x, bullet.getPosition().y);
                }
                bullet.update(delta);
            }
        }

        //remove finished explosions
        if (world.getExplosions() != null && !world.getExplosions().isEmpty()) {
            Iterator explosionIterator = world.getExplosions().iterator();
            while (explosionIterator.hasNext()) {
                Explosion explosion = (Explosion)explosionIterator.next();
                if (explosion.isFinished()) {
                    explosionIterator.remove();
                }
            }
        }

        //handle explodable blocks
        for (ExplodableBlock eb : world.getLevel().getExplodableBlocks()) {
            if (!eb.getState().equals(ExplodableBlock.State.RUBBLE)) {
                if (eb.getState().equals(ExplodableBlock.State.BANG)) {
                    world.getExplosions().add(new Explosion(new Vector2(eb.getPosition().x + ExplodableBlock.getSIZE()/2, eb.getPosition().y + ExplodableBlock.getSIZE()/2), "explosion"));
                    eb.setState(ExplodableBlock.State.RUBBLE);
                } else {
                    collisionDetector.checkExplodableCollisionWithExplosion(eb);
                }
            }
        }
    }

    private void setAction (float delta, Player player) {
        // Convert acceleration to frame time
        //player.setAcceleration(player.getAcceleration() * delta);

        if (player.isTurningAntiClockwise()) {
            player.rotateAntiClockwise(delta);
        }
        if (player.isTurningClcokwise()) {
            player.rotateClockwise(delta);
        }

        // apply acceleration to change velocity
        player.setVelocity(calculateVelocity(player.getAcceleration(), player.getRotation()));

        //apply world effects
        collisionDetector.checkPlayerCollisionWithFloorPads(player);

        // checking collisions with the surrounding blocks depending on Bob's velocity
        collisionDetector.checkPlayerCollisionWithBlocks(delta, player);

        //effects after moving
        collisionDetector.checkPlayerCollisionWithExplosions(player);
        collisionDetector.checkPlayerCollisionWithBoosts(player);

        // apply damping to halt Player nicely
        player.getVelocity().x *= DAMP;
        player.getVelocity().y *= DAMP;
        // ensure terminal velocity is not exceeded
        if (player.getVelocity().x > MAX_VEL) {
            player.getVelocity().x = MAX_VEL;
        }
        if (player.getVelocity().x < -MAX_VEL) {
            player.getVelocity().x = -MAX_VEL;
        }

        // simply updates the state time
        player.update(delta);
    }


    /**
     * Change Bob's state and parameters based on input controls
     **/
    private void processInput() {
        if (!bob.getState().equals(Player.State.DEAD)) {
            if (keys.get(Keys.FIRE)) {
                world.getBullets().addAll(bob.fireBullet(bob.getRotation()));
            }

            if (keys.get(Keys.USE)) {
                for (GunPad gunPad : world.getLevel().getGunPads()) {
                    if (Intersector.overlapConvexPolygons(bob.getBounds(), gunPad.getBounds())) {
                        bob.getGun().setType(gunPad.getType());
                    }
                }
                if (!bob.getGun().fullAmmo()) {
                    bob.getGun().reload();
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
        }

        if (world.getFireJoystick() != null && world.getFireJoystick().getDrag() != null) {
            JoyStick fireJoystick = world.getFireJoystick();

            float fireDeg = fireJoystick.getAngle();
            world.getBullets().addAll(bob.fireBullet(fireDeg));
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
            //do the thing
            aiPlayer.chooseTarget(bob, aiPlayers);
            aiPlayer.setTarget(locator.wallInbetween(aiPlayer, aiPlayer.getTarget()));
            if (aiPlayer.getTarget() == null && aiPlayer.getTargetPlayer() != null) {
                aiPlayer.ignore(aiPlayer.getTargetPlayer().getName());
            }
            world.getBullets().addAll(aiPlayer.decide(delta));
        }
    }

    private void fillView(Player player) {
        player.clearView();
        int xPos = (int)Math.floor(player.getCentrePosition().x - player.getViewCircleWidth()/2);
        int yPos = (int)Math.floor(player.getCentrePosition().y - player.getViewCircleHeight()/2);
//        System.out.println("Player centre pos: " + player.getCentrePosition());
//        System.out.println("X and Y: " + xPos + ", " + yPos);

        for (int i = 0; i < player.getViewCircleWidth(); i++) {
            for (int j = 0; j < player.getViewCircleHeight(); j++) {
                int col = xPos + i;
                int row = yPos + j;
                if (col >= 0 && row >= 0 && col < world.getLevel().getWidth() && row < world.getLevel().getHeight()) {
                    player.getView().getBlocks()[i][j] = world.getLevel().get(col, row);
                    if (world.getLevel().get(col, row) != null) {
//                        System.out.println("Block at: " + new Vector2(i, j));
                    }
                }
            }
        }
        for (FloorPad floorPad : world.getLevel().getFloorPads()) {
            if (Intersector.overlapConvexPolygons(floorPad.getBounds(), player.getViewCircle())) {
                player.getView().getFloorPads().add(floorPad);
            }
        }

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
        boolean occupied = true;
        while (occupied) {
            if (collisionDetector.checkSpawnPointForPlayers(sp, name)) {
                occupied = false;
            } else {
                sp = sps.get(rand.nextInt(sps.size()));
            }
        }
        return sp;
    }
}
