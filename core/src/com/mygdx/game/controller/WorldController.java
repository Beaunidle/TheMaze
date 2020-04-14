package com.mygdx.game.controller;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.model.AIPlayer;
import com.mygdx.game.model.Block;
import com.mygdx.game.model.BloodStain;
import com.mygdx.game.model.Bullet;
import com.mygdx.game.model.ExplodableBlock;
import com.mygdx.game.model.Explosion;
import com.mygdx.game.model.GunPad;
import com.mygdx.game.model.Player;
import com.mygdx.game.model.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class WorldController {

    enum Keys {
        LEFT, RIGHT, UP, DOWN, USE, FIRE
    }

    private static final long LONG_JUMP_PRESS = 150l;
    private static final float ACCELERATION = 20f;
    private static final float GRAVITY = -20f;
    private static final float MAX_JUMP_SPEED = 7f;
    private static final float DAMP = 0.90f;
    private static final float MAX_VEL = 4f;

    private World world;
    private Player bob;
    private List<AIPlayer> aiPlayers = new ArrayList<>();
    private CollisionDetector collisionDetector;

    // This is the rectangle pool used in collision detection
    // Good to avoid instantiation each frame
    private Pool<Polygon> rectPool = new Pool<Polygon>() {
        @Override
        protected Polygon newObject() {
            return new Polygon();
        }
    };

    static Map<Keys, Boolean> keys = new HashMap<WorldController.Keys, Boolean>();

    static {
        keys.put(Keys.LEFT, false);
        keys.put(Keys.RIGHT, false);
        keys.put(Keys.UP, false);
        keys.put(Keys.DOWN, false);
        keys.put(Keys.USE, false);
        keys.put(Keys.FIRE, false);
    }


    //key release timers
    private Timer.Task leftTimer = new Timer.Task() {
        @Override
        public void run() {
            this.cancel();
        }
    };
    private Timer.Task rightTimer = new Timer.Task() {
        @Override
        public void run() {
            this.cancel();
        }
    };
    private Timer.Task upTimer = new Timer.Task() {
        @Override
        public void run() {
            this.cancel();
        }
    };
    private Timer.Task downTimer = new Timer.Task() {
        @Override
        public void run() {
            this.cancel();
        }
    };


    public WorldController(World world) {
        this.world = world;
        this.bob = world.getBob();
        this.aiPlayers = world.getAIPlayers();
        collisionDetector = new CollisionDetector(world, bob, aiPlayers);
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
        Timer.schedule(leftTimer, 0.05F);
    }

    public void rightReleased() {
        keys.get(keys.put(Keys.RIGHT, false));
        Timer.schedule(rightTimer, 0.05F);
    }

    public void upReleased() {
        keys.get(keys.put(Keys.UP, false));
        Timer.schedule(upTimer, 0.05F);
    }

    public void downReleased() {
        keys.get(keys.put(Keys.DOWN, false));
        Timer.schedule(downTimer, 0.05F);
    }

    public void useReleased() {
        keys.get(keys.put(Keys.USE, false));
    }

    public void fireReleased() {
        keys.get(keys.put(Keys.FIRE, false));
    }

    /**
     * The main update method
     **/
    public void update(float delta) {
        // Processing the input - setting the states of Bob
        if (!bob.getState().equals(Player.State.DEAD)) {
            processInput(delta);
        }
        for (AIPlayer aiPlayer : aiPlayers) {
            if (!aiPlayer.getState().equals(Player.State.DEAD)) {
                processAIInput(aiPlayer, delta);
            }
        }

        if (!bob.getState().equals(Player.State.DEAD)) {
            setAction(delta, bob);
            collisionDetector.checkPlayerCollisionWithExplosions(bob, delta);
        }
        Iterator aiPlayerIterator = aiPlayers.iterator();
        while(aiPlayerIterator.hasNext()) {
            AIPlayer aiPlayer = (AIPlayer)aiPlayerIterator.next();
            if (!aiPlayer.getState().equals(Player.State.DEAD)) {
                setAction(delta, aiPlayer);
                collisionDetector.checkPlayerCollisionWithExplosions(aiPlayer, delta);
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
                    collisionDetector.checkBulletCollisionWithBlocks(bullet, delta);
                    collisionDetector.checkBulletCollisionWithPlayers(bullet, delta);

                    Vector2 velocity = bullet.getVelocity();
                    if (velocity.x < 0) bullet.getPosition().add(-bullet.getSpeed() * delta, 0);
                    else if (velocity.x > 0) bullet.getPosition().add(bullet.getSpeed() * delta, 0);

                    if (velocity.y < 0) bullet.getPosition().add(0, -bullet.getSpeed() * delta);
                    else if (velocity.y > 0) bullet.getPosition().add(0, bullet.getSpeed() * delta);
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
            }
        }
    }

    private void setAction (float delta, Player player) {
        // Convert acceleration to frame time
        player.getAcceleration().x = player.getAcceleration().x * delta;
        player.getAcceleration().y = player.getAcceleration().y * delta;

        // apply acceleration to change velocity
        player.getVelocity().add(player.getAcceleration().x, player.getAcceleration().y);

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
            if (player.getAcceleration().x != 0) {
                player.getDirection().x = bob.getAcceleration().x;
                if (player.getAcceleration().y == 0 && (player instanceof AIPlayer || !(upTimer.isScheduled() || downTimer.isScheduled()))) {
                    player.getDirection().y = 0;
                }
            }
            if (player.getAcceleration().y != 0) {
                player.getDirection().y = player.getAcceleration().y;
                if (player.getAcceleration().x == 0 && (player instanceof AIPlayer || !(leftTimer.isScheduled() || rightTimer.isScheduled()))) {
                    player.getDirection().x = 0;
                }
            }
        player.getBounds().setRotation(player.calcRotate(player.getDirection()));
        // simply updates the state time
        player.update(delta);
    }


    /**
     * Change Bob's state and parameters based on input controls
     **/
    private boolean processInput(float delta) {
        //todo can fire up and down as well (diagonal?)
        if (!bob.getState().equals(Player.State.DEAD)) {
            if (keys.get(Keys.FIRE)) {
                world.getBullets().addAll(bob.fireBullet(delta));
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
                bob.getAcceleration().x = -ACCELERATION;
            } else if (keys.get(Keys.RIGHT)) {
                // right is pressed
                bob.setState(Player.State.MOVING);
                bob.getAcceleration().x = ACCELERATION;
            } else {
                bob.getAcceleration().x = 0;
            }

            if (keys.get(Keys.UP)) {
                // up is pressed
                bob.setState(Player.State.MOVING);
                bob.getAcceleration().y = ACCELERATION;
            } else if (keys.get(Keys.DOWN)) {
                // down is pressed
                bob.setState(Player.State.MOVING);
                bob.getAcceleration().y = -ACCELERATION;
            } else {
                bob.getAcceleration().y = 0;
            }
            if (bob.getAcceleration().equals(new Vector2(0, 0))) {
                bob.setState(Player.State.IDLE);
            }
        }
        return false;
    }

    private void processAIInput(AIPlayer aiPlayer, float delta) {
        if (aiPlayer.getState() != Player.State.DEAD) {
            //do the thing
            if (aiPlayer.getIntent().equals(AIPlayer.Intent.HOMING)) {
                aiPlayer.setState(Player.State.MOVING);
                aiPlayer.getAcceleration().y = ACCELERATION;
            } else if (aiPlayer.getIntent().equals(AIPlayer.Intent.SEARCHING)) {
                aiPlayer.setState(Player.State.MOVING);
                aiPlayer.getAcceleration().y = -ACCELERATION;
            }

            if (Math.random() > 0.99) {
                world.getBullets().addAll(aiPlayer.fireBullet(delta));
            }
        }
    }
}
