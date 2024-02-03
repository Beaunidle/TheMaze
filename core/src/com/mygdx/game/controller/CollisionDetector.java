package com.mygdx.game.controller;

import static com.mygdx.game.model.environment.AreaAffect.AffectType.DAMAGE;
import static com.mygdx.game.model.environment.AreaAffect.AffectType.EXPLOSION;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.environment.blocks.Building;
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
import java.util.stream.Collectors;

class CollisionDetector {

    private final World world;
    private final Player bob;
    private final List<AIPlayer> aiPlayers;

    private final Array<Block> collidable = new Array<>();
    private final Array<Sprite> collidableSprites = new Array<>();
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
//        playerRect.setRotation(player.getRotation());

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
        populateCollidableBlocks(startX, startY, endX, endY, player);
        populateCollidableSprites(player);

        //todo lets check a simulation of both
        boolean wayClear = true;
        playerRect.setPosition(player.getPosition().x + player.getVelocity().x, player.getPosition().y + player.getVelocity().y);

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
                            //todo move ai classes
//                            if (player instanceof AIPlayer || player instanceof Animal) {
//                                player.turnAround(90);
//                            }
                        }
                    }
                }
            } else if (Intersector.overlapConvexPolygons(playerRect, block.getBounds()) &&
                    !(block instanceof ExplodableBlock && ((ExplodableBlock) block).getState().equals(ExplodableBlock.State.RUBBLE))) {
                player.getVelocity().x = 0;
//                if (player instanceof AIPlayer || player instanceof Animal) {
//                    player.turnAround(90);
//                }
                world.getCollisionRects().add(block.getBounds());
                break;
            }
        }
//        if (player.getVelocity().x != 0) {
//            for (Sprite collidableSprite : collidableSprites) {
//                if (Intersector.overlapConvexPolygons(playerRect, collidableSprite.getBounds())) {
//                    player.getVelocity().x = 0;
//                    world.getCollisionRects().add(collidableSprite.getBounds());
//                    if (player instanceof AIPlayer) {
//                        ((AIPlayer) player).turnAround(-90);
//                    }
//                    break;
//                }
//            }
//        }
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

        populateCollidableBlocks(startX, startY, endX, endY, player);

        playerRect.setPosition(player.getPosition().x, player.getPosition().y + player.getVelocity().y);

        for (Block block : collidable) {
            if (block == null || block.getDurability() <= 0) continue;
            if (block instanceof Wall) {
                for (Wall.WallType wall : ((Wall) block).getWalls().values()) {
                    if (wall != null && !wall.isOpen()) {
                        if (Intersector.overlapConvexPolygons(playerRect, wall.getBounds())) {
                            player.getVelocity().y = 0;
                            world.getCollisionRects().add(wall.getBounds());
//                            if (player instanceof AIPlayer) {
//                                ((AIPlayer) player).turnAround(90);
//                            }
                        }
                    }
                }
//                break;
            } else if (Intersector.overlapConvexPolygons(playerRect, block.getBounds()) &&
                    !(block instanceof ExplodableBlock && ((ExplodableBlock) block).getState().equals(ExplodableBlock.State.RUBBLE))) {
                player.getVelocity().y = 0;
//                if (player instanceof AIPlayer) {
//                    ((AIPlayer) player).turnAround(-90);
//                }
                world.getCollisionRects().add(block.getBounds());
                break;
            }
        }
//        if (player.getVelocity().y != 0) {
//            for (Sprite collidableSprite : collidableSprites) {
//                if (Intersector.overlapConvexPolygons(playerRect, collidableSprite.getBounds())) {
//                    player.getVelocity().y = 0;
//                    world.getCollisionRects().add(collidableSprite.getBounds());
//                    if (player instanceof AIPlayer) {
//                        ((AIPlayer) player).turnAround(-90);
//                    }
//                    break;
//                }
//            }
//        }
//        }
        // reset the x position of the collision box
        playerRect.setPosition(player.getPosition().x, player.getPosition().y);

        //todo calculate the full move box add it to the map
        playerRect.setPosition(player.getPosition().x + player.getVelocity().x, player.getPosition().y + player.getVelocity().y);
        if (player.isTurningClcokwise()) {
            playerRect.setRotation(player.getRotation() - (player.getRotationSpeed() * delta));
            if (playerRect.getRotation() < 0) {
                playerRect.setRotation(playerRect.getRotation() + 360);
            }
        }
        if (player.isTurningAntiClockwise()) {
            playerRect.setRotation(player.getRotation() + (player.getRotationSpeed() * delta));
            if (playerRect.getRotation() > 360) {
                playerRect.setRotation(playerRect.getRotation() - 360);
            }
        }
        world.getMovementRects().put(player, playerRect);

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
            populateCollidableBlocks(startX, startY, endX, endY, null);

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
                        world.getAreaAffects().add(new AreaAffect(new Vector2(projectile.getPosition().x, projectile.getPosition().y), projectile.getPlayerName(), 2, 2, EXPLOSION, projectile.getPlayerName(), null, 2));
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

            //todo make sure projectiles don't go funny when player is in house
            // also sort out the damn naming in this class, it should be sprite, not player
            populateCollidableBlocks(startX, startY, endX, endY, null);

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
                        world.getAreaAffects().add(new AreaAffect(new Vector2(projectile.getPosition().x, projectile.getPosition().y), projectile.getName(), 2, 2, EXPLOSION, projectile.getPlayerName(), null, 2));
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
                            world.getAreaAffects().add(new AreaAffect(new Vector2(projectile.getPosition().x, projectile.getPosition().y), projectile.getName(), 2, 2, EXPLOSION, projectile.getPlayerName(), null, 2));
                            projectile.setSpeed(0);
                            projectile.startExplodeTimer();
                        }
                    }
                }
                if (Intersector.overlapConvexPolygons(bulletRect, sprite.getBounds())) {
                    projectile.setSpeed(0);
                    //todo ai player is shot
                    if (projectile.isExplosive()) {
                        world.getAreaAffects().add(new AreaAffect(new Vector2(projectile.getPosition().x, projectile.getPosition().y), projectile.getPlayerName(), 2, 2, EXPLOSION, projectile.getPlayerName(), null, 2));
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

    void checkPlayerCollisionWithAreaAffects(Sprite sprite) {

        @SuppressWarnings("NewApi")
        List<AreaAffect> damageAffects = world.getAreaAffects().stream().filter(ae -> ae.getAffectType().equals(DAMAGE)).collect(Collectors.toList());
//        if (!damageAffects.isEmpty()) System.out.println(damageAffects.size());
        for (AreaAffect areaAffect : world.getAreaAffects()) {
            boolean immune = sprite instanceof Player && ((Player) sprite).getBoost().equals(Player.Boost.SHIELD) && !sprite.isInjured();
            if (!immune && Intersector.overlaps(areaAffect.getBoundingCircle(), sprite.getBounds().getBoundingRectangle())) {
                if (!locator.wallInbetweenExplosion(sprite, areaAffect.getPosition())) {
                    switch (areaAffect.getAffectType()) {
                        case LIGHTNING:
                            sprite.isShot(areaAffect.getName(), 2.5F);
                            break;
                        case EXPLOSION:
                            sprite.isShot(areaAffect.getName(), 2.5F);
                            break;
                        case DAMAGE:
                            Vector2 distance = new Vector2(sprite.getCentrePosition()).sub(areaAffect.getPosition());
                            float hitRotation = locator.getAngle(distance);
                            immune = sprite.getName().equals(areaAffect.getSpriteName()) || (sprite instanceof Animal && ((Animal) sprite).getAnimalType().equals(areaAffect.getAnimalType()));
                            if (!immune) sprite.hit(areaAffect.getSpriteName(), areaAffect.getDamage(), hitRotation, areaAffect.getPosition());
                            break;
                    }
                }
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
                float movespeed = 0.5f;
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
                        player.isShot("Spike Floor", 1F);
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
            if (Intersector.overlaps(areaAffect.getBoundingCircle(), eb.getBounds().getBoundingRectangle())) {
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
        return name.equals(bob.getName()) || Intersector.overlapConvexPolygons(bob.getBounds(), sp.getBounds());
        //return true if it is clear
    }

    /**
     * populate the collidable array with the blocks found in the enclosing coordinates
     **/
    private void populateCollidableBlocks(int startX, int startY, int endX, int endY, Sprite sprite) {
        collidable.clear();
        if (sprite != null && sprite.isInHouse()) {
            Building house = world.getLevel().getBuildings().get(sprite.getHouseNumber());
            for (int x = startX-3; x <= endX+3; x++) {
                for (int y = startY-3; y <= endY+3; y++) {
                    if (x >= house.getNumber()*1000
                            && x < house.getNumber() * 1000 + house.getInternalWidth()
                            && y >= house.getNumber()*1000
                            && y < house.getNumber() *1000 + house.getInternalHeight()) {
                        Block block = house.getBlock(x,y);
                        boolean colidible = block != null && block.isColibible();
                        if (colidible) collidable.add(block);
                    }
                }
            }
        } else {
            for (int x = startX-3; x <= endX+3; x++) {
                for (int y = startY-3; y <= endY+3; y++) {
                    if (x >= 0 && x < world.getLevel().getWidth() && y >= 0 && y < world.getLevel().getHeight()) {
                        Block block = world.getLevel().getBlock(x, y);
                        boolean colidible = block != null && block.isColibible();
                        if (colidible) collidable.add(block);
                    }
                }
            }
        }
    }

    private void populateCollidableSprites(Sprite sprite) {
        collidableSprites.clear();
        if (!(sprite instanceof Player) && (Intersector.overlaps(sprite.getCollideCircle(), bob.getCollideCircle()))) collidableSprites.add(bob);
        for (AIPlayer aiPlayer : world.getAIPlayers()) {
            if (!sprite.getName().equals(aiPlayer.getName()) && Intersector.overlaps(sprite.getCollideCircle(), aiPlayer.getCollideCircle())) {
                collidableSprites.add(aiPlayer);
            }
        }
        for (Animal animal : world.getAnimals()) {
            if (animal.getAnimalType().equals(Animal.AnimalType.SPIDER) && !(sprite instanceof Animal && ((Animal) sprite).getAnimalType().equals(Animal.AnimalType.SPIDER))) continue;
            if (sprite != animal && Intersector.overlaps(sprite.getCollideCircle(), animal.getCollideCircle())) {
                collidableSprites.add(animal);
            }
        }
    }
}
