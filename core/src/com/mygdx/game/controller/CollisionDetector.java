package com.mygdx.game.controller;

import static com.mygdx.game.model.environment.AreaAffect.AffectType.EXPLOSION;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.environment.blocks.EnvironmentBlock;
import com.mygdx.game.model.environment.blocks.Wall;
import com.mygdx.game.model.moveable.AIPlayer;
import com.mygdx.game.model.environment.blocks.Block;
import com.mygdx.game.model.moveable.Animal;
import com.mygdx.game.model.moveable.Projectile;
import com.mygdx.game.model.moveable.Sprite;
import com.mygdx.game.model.pads.BoostPad;
import com.mygdx.game.model.environment.blocks.ExplodableBlock;
import com.mygdx.game.model.environment.AreaAffect;
import com.mygdx.game.model.pads.FloorPad;
import com.mygdx.game.model.moveable.Player;
import com.mygdx.game.model.environment.SpawnPoint;
import com.mygdx.game.model.World;
import com.mygdx.game.utils.Locator;

import java.util.List;

class CollisionDetector {

    private final World world;
    private final Player bob;
    private final List<AIPlayer> aiPlayers;

    private final Array<Block> collidable = new Array<>();
    private final Locator locator = new Locator();

    CollisionDetector(World world, Player bob, List<AIPlayer> aiPlayers) {
        this.world = world;
        this.bob = bob;
        this.aiPlayers = aiPlayers;
    }

    void checkPlayerCollisionWithBlocks(float delta, Sprite player) {
        // scale velocity to frame units
        player.getVelocity().x = player.getVelocity().x * delta;
        player.getVelocity().y = player.getVelocity().y * delta;

        // Obtain the rectangle from the pool instead of instantiating it
        Polygon playerRect;
        Polygon boundingRect = player.getBounds();
        // set the rectangle to bob's bounding box
        playerRect = new Polygon(new float[]{0, 0, player.getWidth(), player.getHeight(), player.getWidth(), 0, 0, player.getHeight()});

        // we first check the movement on the horizontal X axis
        int startX, endX;
        int startY = (int) boundingRect.getY();
        int endY = (int) (boundingRect.getY() + player.getHeight());
        // if Player is heading left then we check if he collides with the block on his left
        // we check the block on his right otherwise
        if (player.getVelocity().x < 0) {
            startX = endX = (int) Math.floor(boundingRect.getX() + player.getVelocity().x);
        } else {
            startX = endX = (int) Math.floor(boundingRect.getX() + player.getWidth() + player.getVelocity().x);
        }

        // get the block(s) bob can collide with
        populateCollidableBlocks(startX, startY, endX, endY);

        // simulate player's movement on the X
        playerRect.setPosition(player.getPosition().x + player.getVelocity().x, player.getPosition().y);

        // clear collision boxes in world
        world.getCollisionRects().clear();

        // if player collides, make his horizontal velocity 0
        for (Block block : collidable) {
            if (block == null || block.getDurability() <= 0) continue;
            if (block instanceof Wall) {
                for (Wall.WallType wall : ((Wall) block).getWalls().values()) {
                    if (wall != null && !wall.isOpen()) {
                        if (Intersector.overlapConvexPolygons(playerRect, wall.getBounds())) {
                            player.getVelocity().x = 0;
                            world.getCollisionRects().add(wall.getBounds());
                            if (player instanceof AIPlayer || player instanceof Animal) {
                                player.turnAround(90);
                            }
                        }
                    }
                }
//                System.out.println("Wall here");
//                System.out.println(block.getBounds().getX() + ", " + block.getBounds().getY());
//                System.out.println("Person: " + playerRect.getX() + ", " + playerRect.getY());
//                break;
            } else if (Intersector.overlapConvexPolygons(playerRect, block.getBounds()) &&
                    !(block instanceof ExplodableBlock && ((ExplodableBlock) block).getState().equals(ExplodableBlock.State.RUBBLE))) {
                player.getVelocity().x = 0;
                if (player instanceof AIPlayer || player instanceof Animal) {
                    player.turnAround(90);
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
        endX = (int) (boundingRect.getX() + player.getWidth());
        if (player.getVelocity().y < 0) {
            startY = endY = (int) Math.floor(boundingRect.getY() + player.getVelocity().y);
        } else {
            startY = endY = (int) Math.floor(boundingRect.getY() + player.getHeight() + player.getVelocity().y);
        }

        populateCollidableBlocks(startX, startY, endX, endY);

        playerRect.setPosition(player.getPosition().x, player.getPosition().y + player.getVelocity().y);

        for (Block block : collidable) {
            if (block == null || block.getDurability() <= 0) continue;
            if (block instanceof Wall) {
                for (Wall.WallType wall : ((Wall) block).getWalls().values()) {
                    if (wall != null && !wall.isOpen()) {
                        if (Intersector.overlapConvexPolygons(playerRect, wall.getBounds())) {
                            player.getVelocity().y = 0;
                            world.getCollisionRects().add(wall.getBounds());
                            if (player instanceof AIPlayer) {
                                ((AIPlayer) player).turnAround(90);
                            }
                        }
                    }
                }
//                break;
            } else if (Intersector.overlapConvexPolygons(playerRect, block.getBounds()) &&
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

//        if (player instanceof Player && !(player instanceof AIPlayer)) {
        player.updateShapes();
//        }

        // un-scale velocity (not in frame time)
        player.getVelocity().x = player.getVelocity().x * (1 / delta);
        player.getVelocity().y = player.getVelocity().y * (1 / delta);
    }

    /**
     * Collision checking
     **/
    void checkProjectileCollisionWithBlocks(Projectile projectile, float delta) {

        projectile.setSpeed(projectile.getSpeed() * delta);
        // Obtain the rectangle from the pool instead of instantiating it
        Polygon bulletRect;
        Polygon boundingRect = projectile.getBounds();

        // set the rectangle to bullet's bounding box
        bulletRect = new Polygon(new float[]{0, 0, projectile.getWidth(), projectile.getHeight(), projectile.getWidth(), 0, 0, projectile.getHeight()});

        int startX, endX, startY, endY;

        if (projectile.getVelocity().x != 0) {
            // we first check the movement on the horizontal X axis
            startY = (int) boundingRect.getY();
            endY = (int) (boundingRect.getY() + projectile.getHeight());
            // if Player is heading left then we check if he collides with the block on his left
            // we check the block on his right otherwise
            if (projectile.getVelocity().x < 0) {
                startX = endX = (int) Math.floor(boundingRect.getX() + projectile.getSpeed());
            } else {
                startX = endX = (int) Math.floor(boundingRect.getX() + projectile.getWidth() + projectile.getSpeed());
            }

            // get the block(s) bob can collide with
            populateCollidableBlocks(startX, startY, endX, endY);

            // simulate player's movement on the X
            bulletRect.setPosition(projectile.getPosition().x + projectile.getSpeed(), projectile.getPosition().y);

            // clear collision boxes in world
            world.getCollisionRects().clear();

            // if player collides, make his horizontal velocity 0
            for (Block block : collidable) {
                if (block == null) continue;
                if (Intersector.overlapConvexPolygons(bulletRect, block.getBounds()) &&
                        !(block instanceof ExplodableBlock && ((ExplodableBlock) block).getState().equals(ExplodableBlock.State.RUBBLE))) {
                    if (block instanceof ExplodableBlock) {
                        ((ExplodableBlock) block).explode(projectile.getExplodeTime());
                    }

                    if (projectile.isExplosive()) {
                        projectile.setSpeed(0);
                        projectile.startExplodeTimer();
                        world.getAreaAffects().add(new AreaAffect(new Vector2(projectile.getPosition().x, projectile.getPosition().y), projectile.getPlayerName(), 2, 2, EXPLOSION));
                    }
                    return;
                }
            }
            // reset the x position of the collision box
            bulletRect.setPosition(projectile.getPosition().x, projectile.getPosition().y);
        }

        if (projectile.getVelocity().y != 0) {
            // the same thing but on the vertical Y axis
            boundingRect = projectile.getBounds();
            startX = (int) boundingRect.getX();
            endX = (int) (boundingRect.getX() + projectile.getWidth());
            if (projectile.getVelocity().y < 0) {
                startY = endY = (int) Math.floor(boundingRect.getY() + projectile.getSpeed());
            } else {
                startY = endY = (int) Math.floor(boundingRect.getY() + projectile.getHeight() + projectile.getSpeed());
            }

            populateCollidableBlocks(startX, startY, endX, endY);

            bulletRect.setPosition(projectile.getPosition().x, projectile.getPosition().y + projectile.getSpeed());

            for (Block block : collidable) {
                if (block == null) continue;
                if (Intersector.overlapConvexPolygons(bulletRect, block.getBounds()) &&
                        !(block instanceof ExplodableBlock && ((ExplodableBlock) block).getState().equals(ExplodableBlock.State.RUBBLE))) {
                    if (block instanceof ExplodableBlock) {
                        ((ExplodableBlock) block).explode(projectile.getExplodeTime());
                    }
                    projectile.setSpeed(0);
                    projectile.startExplodeTimer();
                    world.getCollisionRects().add(block.getBounds());
                    if (projectile.isExplosive()) {
                        world.getAreaAffects().add(new AreaAffect(new Vector2(projectile.getPosition().x, projectile.getPosition().y), projectile.getPlayerName(), 2, 2, EXPLOSION));
                    }
                    return;
                }
            }
            // reset the collision box's position on Y
            bulletRect.setPosition(projectile.getPosition().x, projectile.getPosition().y);
        }
        projectile.setSpeed(projectile.getSpeed() * (1/delta));
    }

     void checkProjectileCollisionWithPlayers(Projectile projectile, float delta) {
        projectile.setSpeed(projectile.getSpeed() * delta);
        // set the rectangle to bullet's bounding box
        Polygon bulletRect = new Polygon(new float[]{0, 0, projectile.getWidth(), projectile.getHeight(), projectile.getWidth(), 0, 0, projectile.getHeight()});

        if (projectile.getVelocity().x != 0) {
            // we first check the movement on the horizontal X axis
            // simulate player's movement on the X
            bulletRect.setPosition(projectile.getPosition().x + projectile.getSpeed(), projectile.getPosition().y);
            // if player collides, make his horizontal velocity 0
            for (Player player : aiPlayers) {
                if (checkSpriteWithBullet(player, projectile, bulletRect)) return;
            }
            for (Animal animal : world.getAnimals()) {
                if (checkSpriteWithBullet(animal, projectile, bulletRect)) return;
            }
            if (checkSpriteWithBullet(bob, projectile, bulletRect)) return;

                       // reset the x position of the collision box
            bulletRect.setPosition(projectile.getPosition().x, projectile.getPosition().y);
        }

        if (projectile.getVelocity().y != 0) {
            // the same thing but on the vertical Y axis
            bulletRect.setPosition(projectile.getPosition().x, projectile.getPosition().y + projectile.getSpeed());

            for (Player player : aiPlayers) {
                if (checkSpriteWithBullet(player, projectile, bulletRect)) return;
            }
            for (Animal animal : world.getAnimals()) {
                if (checkSpriteWithBullet(animal, projectile, bulletRect)) return;
            }
            if (checkSpriteWithBullet(bob, projectile, bulletRect)) return;

            // reset the collision box's position on Y
            bulletRect.setPosition(projectile.getPosition().x, projectile.getPosition().y);
        }
        projectile.setSpeed(projectile.getSpeed() * (1/delta));
    }

    public boolean checkSpriteWithBullet(Sprite sprite, Projectile projectile, Polygon bulletRect) {
        if (!sprite.getState().equals(Player.State.DEAD)) {
            if (!projectile.getPlayerName().equals(sprite.getName())) {
                if (sprite instanceof Player) {
                    Player player = (Player) sprite;
                    if (player.getBoost().equals(Player.Boost.SHIELD) && Intersector.overlaps(player.getShieldCircle(), bulletRect.getBoundingRectangle())) {
                        if (projectile.isExplosive()) {
                            world.getAreaAffects().add(new AreaAffect(new Vector2(projectile.getPosition().x, projectile.getPosition().y), projectile.getPlayerName(), 2, 2, EXPLOSION));
                            projectile.setSpeed(0);
                            projectile.startExplodeTimer();
                        }
                    }
                }
                if (Intersector.overlapConvexPolygons(bulletRect, sprite.getBounds())) {
                    projectile.setSpeed(0);
                    //todo ai player is shot
                    if (projectile.isExplosive()) {
                        world.getAreaAffects().add(new AreaAffect(new Vector2(projectile.getPosition().x, projectile.getPosition().y), projectile.getPlayerName(), 2, 2, EXPLOSION));
                        projectile.startExplodeTimer();
                    }
                    sprite.isShot(projectile.getPlayerName(), projectile.getDamage());
                    if (projectile.getProjectileType().equals(Projectile.ProjectileType.FIREBALL)) {
                        sprite.setAlight(3F);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    void checkPlayerCollisionWithExplosions(Sprite player) {

        for (AreaAffect areaAffect : world.getAreaAffects()) {
            boolean immune = false;
            if (player instanceof Player && ((Player) player).getBoost().equals(Player.Boost.SHIELD) && !player.isInjured()) immune = true;
            if (!immune && Intersector.overlaps(areaAffect.getBounds(), player.getBounds().getBoundingRectangle())) {
                if (!locator.wallInbetweenExplosion(player, areaAffect.getPosition())) player.isShot(areaAffect.getName(), 2.5F);
            }
        }
    }

    void checkPlayerCollisionWithBoosts(Player player) {
        for (BoostPad boostPad : world.getLevel().getBoostPads()) {
            if (boostPad.getBoost() != null && player.getBoost().equals(Player.Boost.NOTHING) &&
                    Intersector.overlapConvexPolygons(player.getBounds(), boostPad.getBounds())) {
                player.setBoost(boostPad.collectBoost(), 10);
            }
        }
    }

    void checkPlayerCollisionWithFloorPads(Sprite player) {

        for (FloorPad floorPad : world.getLevel().getFloorPads()) {
            if (Intersector.overlapConvexPolygons(floorPad.getBounds(), player.getBounds())) {
                float movespeed = 1.5f;
                switch (floorPad.getType()) {
                    case MOVE:
                        movespeed = 3F;
                    case WATERFLOW:
                        switch (floorPad.getRotation()) {
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
                    case WATER:
                        player.getVelocity().x = player.getVelocity().x * 0.85F;
                        player.getVelocity().y = player.getVelocity().y * 0.85F;
                }
            }
        }
    }

    void checkExplodableCollisionWithExplosion(ExplodableBlock eb) {
        for (AreaAffect areaAffect : world.getAreaAffects()) {
            if (Intersector.overlaps(areaAffect.getBounds(), eb.getBounds().getBoundingRectangle())) {
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
                    Block block = world.getLevel().getBlocks()[x][y];
                    boolean colidible = true;
                    if (block != null && !block.isColibible()) colidible = false;
                    if (colidible) collidable.add(world.getLevel().getBlocks()[x][y]);
                }
            }
        }
    }


}
