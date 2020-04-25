package com.mygdx.game.controller;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.AIPlayer;
import com.mygdx.game.model.BloodStain;
import com.mygdx.game.model.Bullet;
import com.mygdx.game.model.ExplodableBlock;
import com.mygdx.game.model.Explosion;
import com.mygdx.game.model.GunPad;
import com.mygdx.game.model.Player;
import com.mygdx.game.model.World;
import com.mygdx.game.screens.LoadingScreen;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    }

    public boolean isLevelFinished() {
        return levelFinished;
    }

    public int getLevel() {
        return level;
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


    public void loadLevel(int level) {
        world.loadWorld(level);
        this.bob = world.getBob();
        this.aiPlayers = world.getAIPlayers();
        this.collisionDetector = new CollisionDetector(world, bob, aiPlayers);
        levelFinished = false;
    }
    /**
     * The main update method
     **/
    public void update(float delta) {
        // Processing the input - setting the states of Bob
        if (aiPlayers.isEmpty()) {
             levelFinished = true;
        }

        if (!bob.getState().equals(Player.State.DEAD)) {
            processInput();
        }
        for (AIPlayer aiPlayer : aiPlayers) {
            if (!aiPlayer.getState().equals(Player.State.DEAD)) {
                processAIInput(aiPlayer);
            }
        }

        if (!bob.getState().equals(Player.State.DEAD)) {
            setAction(delta, bob);
            collisionDetector.checkPlayerCollisionWithExplosions(bob);
        }
        Iterator aiPlayerIterator = aiPlayers.iterator();
        while(aiPlayerIterator.hasNext()) {
            AIPlayer aiPlayer = (AIPlayer)aiPlayerIterator.next();
            if (!aiPlayer.getState().equals(Player.State.DEAD)) {
                setAction(delta, aiPlayer);
                collisionDetector.checkPlayerCollisionWithExplosions(aiPlayer);
            } else {
                world.getBloodStains().add(new BloodStain(aiPlayer.getPosition(), aiPlayer.getName()));
                aiPlayerIterator.remove();
            }
        }

        if (world.getBullets() != null && !world.getBullets().isEmpty()) {
            Iterator bulletIterator = world.getBullets().iterator();
            while (bulletIterator.hasNext()) {
                Bullet bullet = (Bullet)bulletIterator.next();
                if (bullet.getSpeed() == 0 && !bullet.isExploding()) {
                    bulletIterator.remove();
                    continue;
                }
                if (bullet.getSpeed() > 0) {
                    bullet.setVelocity(calculateVelocity(bullet.getSpeed() * delta, bullet.getRotation()));
                    collisionDetector.checkBulletCollisionWithBlocks(bullet, delta);
                    collisionDetector.checkBulletCollisionWithPlayers(bullet, delta);

                    bullet.getPosition().add(bullet.getVelocity().x, bullet.getVelocity().y);
                }
                bullet.update(delta);
            }
        }
        if (world.getExplosions() != null && !world.getExplosions().isEmpty()) {
            Iterator explosionIterator = world.getExplosions().iterator();
            while (explosionIterator.hasNext()) {
                Explosion explosion = (Explosion)explosionIterator.next();
                if (explosion.isFinished()) {
                    explosionIterator.remove();
                }
            }
        }
        for (ExplodableBlock eb : world.getLevel().getExplodableBlocks()) {
            if (eb.getState().equals(ExplodableBlock.State.BANG)) {
                world.getExplosions().add(new Explosion(new Vector2(eb.getPosition().x - ExplodableBlock.getSIZE(), eb.getPosition().y - ExplodableBlock.getSIZE())));
                eb.setState(ExplodableBlock.State.RUBBLE);
            } else if (!eb.getState().equals(ExplodableBlock.State.RUBBLE)) {
                collisionDetector.checkExplodableCollisionWithExplosion(eb);
            }
        }
    }

    private void setAction (float delta, Player player) {
        // Convert acceleration to frame time
        //player.setAcceleration(player.getAcceleration() * delta);

        // apply acceleration to change velocity
        player.setVelocity(calculateVelocity(player.getAcceleration(), player.getRotation()));

        // checking collisions with the surrounding blocks depending on Bob's velocity
        collisionDetector.checkPlayerCollisionWithBlocks(delta, player);

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
        //set direction
//            if (player.getAcceleration().x != 0) {
//                player.getDirection().x = bob.getAcceleration().x;
//                if (player.getAcceleration().y == 0 && (player instanceof AIPlayer || !(upTimer.isScheduled() || downTimer.isScheduled()))) {
//                    player.getDirection().y = 0;
//                }
//            }
//            if (player.getAcceleration().y != 0) {
//                player.getDirection().y = player.getAcceleration().y;
//                if (player.getAcceleration().x == 0 && (player instanceof AIPlayer || !(leftTimer.isScheduled() || rightTimer.isScheduled()))) {
//                    player.getDirection().x = 0;
//                }
//            }
        player.getBounds().setRotation(player.getRotation());
        // simply updates the state time
        player.update(delta);
    }


    /**
     * Change Bob's state and parameters based on input controls
     **/
    private void processInput() {
        if (!bob.getState().equals(Player.State.DEAD)) {
            if (keys.get(Keys.FIRE)) {
                world.getBullets().addAll(bob.fireBullet());
            }

            if (keys.get(Keys.USE)) {
                for (GunPad gunPad : world.getLevel().getGunPads()) {
                    if (Intersector.overlapConvexPolygons(bob.getBounds(), gunPad.getBounds())) {
                        bob.getGun().setType(gunPad.getType());
                    }
                }
            }

            if (keys.get(Keys.LEFT)) {
                // left is pressed
                bob.setState(Player.State.MOVING);
                bob.setRotation(bob.getRotation() + bob.getRotationSpeed());
                if (bob.getRotation() > 360) {
                    bob.setRotation(bob.getRotation() - 360);
                }
            } else if (keys.get(Keys.RIGHT)) {
                // right is pressed
                bob.setState(Player.State.MOVING);
                bob.setRotation(bob.getRotation() - bob.getRotationSpeed());
                if (bob.getRotation() < 0) {
                    bob.setRotation(bob.getRotation() + 360);
                }
            }

            if (keys.get(Keys.UP)) {
                // up is pressed
                bob.setState(Player.State.MOVING);
                bob.setAcceleration(4F);
            } else if (keys.get(Keys.DOWN)) {
                // down is pressed
                bob.setState(Player.State.MOVING);
                bob.setAcceleration(-4F);
            } else {
                bob.setAcceleration(0F);
                bob.setState(Player.State.IDLE);
            }
        }
    }

    private void processAIInput(AIPlayer aiPlayer) {
        if (aiPlayer.getState() != Player.State.DEAD) {
            //do the thing
            if (aiPlayer.getIntent().equals(AIPlayer.Intent.HOMING)) {
                aiPlayer.setState(Player.State.MOVING);
                aiPlayer.setAcceleration(2F);
            } else if (aiPlayer.getIntent().equals(AIPlayer.Intent.SEARCHING)) {
                aiPlayer.setState(Player.State.MOVING);
                aiPlayer.setAcceleration(0F);
                aiPlayer.setRotation(aiPlayer.getRotation() + aiPlayer.getRotationSpeed());
                if (aiPlayer.getRotation() > 360) {
                    aiPlayer.setRotation(aiPlayer.getRotation() - 360);
                }
            }
            if (Math.random() > 0.995) {
                world.getBullets().addAll(aiPlayer.fireBullet());
            }
        }
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
}
