package com.mygdx.game.controller;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.AIPlayer;
import com.mygdx.game.model.Block;
import com.mygdx.game.model.BoostPad;
import com.mygdx.game.model.Bullet;
import com.mygdx.game.model.ExplodableBlock;
import com.mygdx.game.model.Explosion;
import com.mygdx.game.model.FloorPad;
import com.mygdx.game.model.Player;
import com.mygdx.game.model.SpawnPoint;
import com.mygdx.game.model.World;
import com.mygdx.game.utils.Locator;

import java.util.List;

class CollisionDetector {

    private World world;
    private Player bob;
    private List<AIPlayer> aiPlayers;

    private Array<Block> collidable = new Array<>();
    private Locator locator = new Locator();

    CollisionDetector(World world, Player bob, List<AIPlayer> aiPlayers) {
        this.world = world;
        this.bob = bob;
        this.aiPlayers = aiPlayers;
    }

    void checkPlayerCollisionWithBlocks(float delta, Player player) {
        // scale velocity to frame units
        player.getVelocity().x = player.getVelocity().x * delta;
        player.getVelocity().y = player.getVelocity().y * delta;

        // Obtain the rectangle from the pool instead of instantiating it
        Polygon playerRect;
        Polygon boundingRect = player.getBounds();
        // set the rectangle to bob's bounding box
        playerRect = new Polygon(new float[]{0, 0, Player.WIDTH, Player.HEIGHT, Player.WIDTH, 0, 0, Player.HEIGHT});

        // we first check the movement on the horizontal X axis
        int startX, endX;
        int startY = (int) boundingRect.getY();
        int endY = (int) (boundingRect.getY() + Player.HEIGHT);
        // if Player is heading left then we check if he collides with the block on his left
        // we check the block on his right otherwise
        if (player.getVelocity().x < 0) {
            startX = endX = (int) Math.floor(boundingRect.getX() + player.getVelocity().x);
        } else {
            startX = endX = (int) Math.floor(boundingRect.getX() + Player.WIDTH + player.getVelocity().x);
        }

        // get the block(s) bob can collide with
        populateCollidableBlocks(startX, startY, endX, endY);

        // simulate player's movement on the X
        playerRect.setPosition(player.getPosition().x + player.getVelocity().x, player.getPosition().y);

        // clear collision boxes in world
        world.getCollisionRects().clear();

        // if player collides, make his horizontal velocity 0
        for (Block block : collidable) {
            if (block == null) continue;
            if (Intersector.overlapConvexPolygons(playerRect, block.getBounds()) &&
                    !(block instanceof ExplodableBlock && ((ExplodableBlock) block).getState().equals(ExplodableBlock.State.RUBBLE))) {
                player.getVelocity().x = 0;
                if (player instanceof AIPlayer) {
                    ((AIPlayer) player).turnAround(90);
                }
                world.getCollisionRects().add(block.getBounds());
                break;
            }
        }

        // reset the x position of the collision box
        playerRect.setPosition(player.getPosition().x, player.getPosition().y);

        // the same thing but on the vertical Y axis
        boundingRect = player.getBounds();
        startX = (int) boundingRect.getX();
        endX = (int) (boundingRect.getX() + Player.WIDTH);
        if (player.getVelocity().y < 0) {
            startY = endY = (int) Math.floor(boundingRect.getY() + player.getVelocity().y);
        } else {
            startY = endY = (int) Math.floor(boundingRect.getY() + Player.HEIGHT + player.getVelocity().y);
        }

        populateCollidableBlocks(startX, startY, endX, endY);

        playerRect.setPosition(player.getPosition().x, player.getPosition().y + player.getVelocity().y);

        for (Block block : collidable) {
            if (block == null) continue;
            if (Intersector.overlapConvexPolygons(playerRect, block.getBounds()) &&
                    !(block instanceof ExplodableBlock && ((ExplodableBlock) block).getState().equals(ExplodableBlock.State.RUBBLE))) {
                player.getVelocity().y = 0;
                if (player instanceof AIPlayer) {
                    ((AIPlayer) player).turnAround(-90);
                }
                world.getCollisionRects().add(block.getBounds());
                break;
            }
        }
        // reset the collision box's position on Y
        playerRect.setPosition(player.getPosition().x, player.getPosition().y);

        // update Player's position
        player.getPosition().add(player.getVelocity());
        player.getBounds().setPosition(player.getPosition().x, player.getPosition().y);
        player.getBounds().setRotation(player.getRotation());
        player.getViewCircle().setPosition(player.getPosition().x - 7.5F, player.getPosition().y - 4F);
        //        player.getViewCircle().setPosition(player.getCentrePosition().x, player.getCentrePosition().y);
        player.getShieldCircle().setPosition(player.getCentrePosition().x, player.getCentrePosition().y);

        // un-scale velocity (not in frame time)
        player.getVelocity().x = player.getVelocity().x * (1 / delta);
        player.getVelocity().y = player.getVelocity().y * (1 / delta);
    }

    /**
     * Collision checking
     **/
    void checkBulletCollisionWithBlocks(Bullet bullet, float delta) {

        bullet.setSpeed(bullet.getSpeed() * delta);
        // Obtain the rectangle from the pool instead of instantiating it
        Polygon bulletRect;
        Polygon boundingRect = bullet.getBounds();

        // set the rectangle to bullet's bounding box
        bulletRect = new Polygon(new float[]{0, 0, bullet.getWidth(), bullet.getHeight(), bullet.getWidth(), 0, 0, bullet.getHeight()});

        int startX, endX, startY, endY;

        if (bullet.getVelocity().x != 0) {
            // we first check the movement on the horizontal X axis
            startY = (int) boundingRect.getY();
            endY = (int) (boundingRect.getY() + bullet.getHeight());
            // if Player is heading left then we check if he collides with the block on his left
            // we check the block on his right otherwise
            if (bullet.getVelocity().x < 0) {
                startX = endX = (int) Math.floor(boundingRect.getX() + bullet.getSpeed());
            } else {
                startX = endX = (int) Math.floor(boundingRect.getX() + bullet.getWidth() + bullet.getSpeed());
            }

            // get the block(s) bob can collide with
            populateCollidableBlocks(startX, startY, endX, endY);

            // simulate player's movement on the X
            bulletRect.setPosition(bullet.getPosition().x + bullet.getSpeed(), bullet.getPosition().y);

            // clear collision boxes in world
            world.getCollisionRects().clear();

            // if player collides, make his horizontal velocity 0
            for (Block block : collidable) {
                if (block == null) continue;
                if (Intersector.overlapConvexPolygons(bulletRect, block.getBounds()) &&
                        !(block instanceof ExplodableBlock && ((ExplodableBlock) block).getState().equals(ExplodableBlock.State.RUBBLE))) {

                    if (block instanceof ExplodableBlock) {
                        ((ExplodableBlock) block).explode(bullet.getExplodeTime());
                    }

                    bullet.setSpeed(0);
                    bullet.startExplodeTimer();
                    if (bullet.isExplosive()) {
                        world.getExplosions().add(new Explosion(new Vector2(bullet.getPosition().x, bullet.getPosition().y), bullet.getPlayerName()));
                    }
                    return;
                }
            }
            // reset the x position of the collision box
            bulletRect.setPosition(bullet.getPosition().x, bullet.getPosition().y);
        }

        if (bullet.getVelocity().y != 0) {
            // the same thing but on the vertical Y axis
            boundingRect = bullet.getBounds();
            startX = (int) boundingRect.getX();
            endX = (int) (boundingRect.getX() + bullet.getWidth());
            if (bullet.getVelocity().y < 0) {
                startY = endY = (int) Math.floor(boundingRect.getY() + bullet.getSpeed());
            } else {
                startY = endY = (int) Math.floor(boundingRect.getY() + bullet.getHeight() + bullet.getSpeed());
            }

            populateCollidableBlocks(startX, startY, endX, endY);

            bulletRect.setPosition(bullet.getPosition().x, bullet.getPosition().y + bullet.getSpeed());

            for (Block block : collidable) {
                if (block == null) continue;
                if (Intersector.overlapConvexPolygons(bulletRect, block.getBounds()) &&
                        !(block instanceof ExplodableBlock && ((ExplodableBlock) block).getState().equals(ExplodableBlock.State.RUBBLE))) {
                    if (block instanceof ExplodableBlock) {
                        ((ExplodableBlock) block).explode(bullet.getExplodeTime());
                    }
                    bullet.setSpeed(0);
                    bullet.startExplodeTimer();
                    world.getCollisionRects().add(block.getBounds());
                    if (bullet.isExplosive()) {
                        world.getExplosions().add(new Explosion(new Vector2(bullet.getPosition().x, bullet.getPosition().y), bullet.getPlayerName()));
                    }
                    return;
                }
            }
            // reset the collision box's position on Y
            bulletRect.setPosition(bullet.getPosition().x, bullet.getPosition().y);
        }
        bullet.setSpeed(bullet.getSpeed() * (1/delta));
    }

     void checkBulletCollisionWithPlayers(Bullet bullet, float delta) {

        bullet.setSpeed(bullet.getSpeed() * delta);
        Polygon bulletRect;

        // set the rectangle to bullet's bounding box
        bulletRect = new Polygon(new float[]{0, 0, bullet.getWidth(), bullet.getHeight(), bullet.getWidth(), 0, 0, bullet.getHeight()});

        if (bullet.getVelocity().x != 0) {
            // we first check the movement on the horizontal X axis

            // simulate player's movement on the X
            bulletRect.setPosition(bullet.getPosition().x + bullet.getSpeed(), bullet.getPosition().y);

            // if player collides, make his horizontal velocity 0
            for (Player player : aiPlayers) {
                if (!player.getState().equals(Player.State.DEAD)) {
                    if (!bullet.getPlayerName().equals(player.getName())) {
                        if (player.getBoost().equals(Player.Boost.SHIELD) && Intersector.overlaps(player.getShieldCircle(), bulletRect.getBoundingRectangle())) {
                            bullet.setSpeed(0);
                            bullet.startExplodeTimer();
                            if (bullet.isExplosive()) {
                                world.getExplosions().add(new Explosion(new Vector2(bullet.getPosition().x, bullet.getPosition().y), bullet.getPlayerName()));
                            }
                        }
                        if (Intersector.overlapConvexPolygons(bulletRect, player.getBounds())) {
                            bullet.setSpeed(0);
                            bullet.startExplodeTimer();
                            //todo ai player is shot
                            if (bullet.isExplosive()) {
                                world.getExplosions().add(new Explosion(new Vector2(bullet.getPosition().x, bullet.getPosition().y), bullet.getPlayerName()));
                            }
                            player.isShot(bullet.getPlayerName(), bullet.getDamage());
                            return;
                        }
                    }
                }
            }
            if  (!bullet.getPlayerName().equals(bob.getName())) {
                if (bob.getBoost().equals(Player.Boost.SHIELD) && Intersector.overlaps(bob.getShieldCircle(), bulletRect.getBoundingRectangle())) {
                    bullet.setSpeed(0);
                    bullet.startExplodeTimer();
                    if (bullet.isExplosive()) {
                        world.getExplosions().add(new Explosion(new Vector2(bullet.getPosition().x, bullet.getPosition().y), bullet.getPlayerName()));
                    }
                }
                if (Intersector.overlapConvexPolygons(bulletRect, bob.getBounds())) {
                    bullet.setSpeed(0);
                    bullet.startExplodeTimer();
                    //player is shot
                    if (bullet.isExplosive()) {
                        world.getExplosions().add(new Explosion(new Vector2(bullet.getPosition().x, bullet.getPosition().y), bullet.getPlayerName()));
                    }
                    bob.isShot(bullet.getPlayerName(), bullet.getDamage());
                    return;
                }

            }
                       // reset the x position of the collision box
            bulletRect.setPosition(bullet.getPosition().x, bullet.getPosition().y);
        }

        if (bullet.getVelocity().y != 0) {
            // the same thing but on the vertical Y axis
            bulletRect.setPosition(bullet.getPosition().x, bullet.getPosition().y + bullet.getSpeed());

            for (Player player : aiPlayers) {
                if (!player.getState().equals(Player.State.DEAD)) {
                    if (!bullet.getPlayerName().equals(player.getName()) && Intersector.overlapConvexPolygons(bulletRect, player.getBounds())) {
                        bullet.setSpeed(0);
                        bullet.startExplodeTimer();
                        //todo ai player is shot
                        if (bullet.isExplosive()) {
                            world.getExplosions().add(new Explosion(new Vector2(bullet.getPosition().x, bullet.getPosition().y), bullet.getPlayerName()));
                        }
                        player.isShot(bullet.getPlayerName(), bullet.getDamage());
                        return;
                    }
                }
            }
            if (!bullet.getPlayerName().equals(bob.getName()) && Intersector.overlapConvexPolygons(bulletRect, bob.getBounds())) {
                bullet.setSpeed(0);
                bullet.startExplodeTimer();
                //todo player is shot
                if (bullet.isExplosive()) {
                    world.getExplosions().add(new Explosion(new Vector2(bullet.getPosition().x, bullet.getPosition().y), bullet.getPlayerName()));
                }
                bob.isShot(bullet.getPlayerName(), bullet.getDamage());
                return;
            }
            // reset the collision box's position on Y
            bulletRect.setPosition(bullet.getPosition().x, bullet.getPosition().y);
        }
        bullet.setSpeed(bullet.getSpeed() * (1/delta));
    }

    void checkPlayerCollisionWithExplosions(Player player) {

        for (Explosion explosion : world.getExplosions()) {
            if (!player.getBoost().equals(Player.Boost.SHIELD) && !player.isInjured()
                    && Intersector.overlaps(explosion.getBounds(), player.getBounds().getBoundingRectangle())) {
                if (!locator.wallInbetweenExplosion(player, explosion.getPosition())) player.isShot(explosion.getName(), 2.5F);
            }
        }
    }

    void checkPlayerCollisionWithBoosts(Player player) {
        for (BoostPad boostPad : world.getLevel().getBoostPads()) {
            if (boostPad.getBoost() != null && player.getBoost().equals(Player.Boost.NOTHING) &&
                    Intersector.overlapConvexPolygons(player.getBounds(), boostPad.getBounds())) {
                player.setBoost(boostPad.collectBoost());
            }
        }
    }

    void checkPlayerCollisionWithFloorPads(Player player) {

        for (FloorPad floorPad : world.getLevel().getFloorPads()) {
            if (Intersector.overlapConvexPolygons(floorPad.getBounds(), player.getBounds())) {
                float movespeed = 4f;
                switch (floorPad.getType()) {
                    case MOVE:
                        switch (floorPad.getRot()) {
                            case 0:
                                player.getVelocity().x = player.getVelocity().x + movespeed;
                                break;
                            case 90:
                                player.getVelocity().y = player.getVelocity().y + movespeed;
                                break;
                            case 180:
                                player.getVelocity().x = player.getVelocity().x - movespeed;
                                break;
                            case 270:
                                player.getVelocity().y = player.getVelocity().y - movespeed;
                                break;
                        }
                        break;
                    case SPIKE:
                        player.isShot("Spike Floor", 0.2F);
                        break;
                    case SLIME:
                        player.getVelocity().x = player.getVelocity().x * 0.7F;
                        player.getVelocity().y = player.getVelocity().y * 0.7F;
                }
            }
        }
    }

    void checkExplodableCollisionWithExplosion(ExplodableBlock eb) {
        for (Explosion explosion : world.getExplosions()) {
            if (Intersector.overlaps(explosion.getBounds(), eb.getBounds().getBoundingRectangle())) {
                eb.explode(0.5F);
            }
        }
    }

    public boolean checkSpawnPointForPlayers(SpawnPoint sp, String name) {

        for (AIPlayer aiPlayer : aiPlayers) {
            if (!name.equals(aiPlayer.getName()) && Intersector.overlapConvexPolygons(aiPlayer.getBounds(), sp.getBounds())) {
                return false;
            }
        }
        if (!name.equals(bob.getName()) && !Intersector.overlapConvexPolygons(bob.getBounds(), sp.getBounds())) {
            return false;
        }
        return true;
        //return true if it is clear
    }

    /**
     * populate the collidable array with the blocks found in the enclosing coordinates
     **/
    private void populateCollidableBlocks(int startX, int startY, int endX, int endY) {
        collidable.clear();
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                if (x >= 0 && x < world.getLevel().getWidth() && y >= 0 && y < world.getLevel().getHeight()) {
                    collidable.add(world.getLevel().getBlocks()[x][y]);
                }
            }
        }
    }


}
