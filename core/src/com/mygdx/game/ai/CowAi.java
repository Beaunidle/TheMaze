package com.mygdx.game.ai;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.environment.blocks.Block;
import com.mygdx.game.model.moveable.AIPlayer;
import com.mygdx.game.model.moveable.Animal;
import com.mygdx.game.model.moveable.Player;
import com.mygdx.game.model.moveable.Projectile;
import com.mygdx.game.model.moveable.Sprite;
import com.mygdx.game.utils.Locator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class CowAi extends AnimalAi {

    public List<Projectile> decide(float delta, Sprite s, boolean nightTime, Player player, List<AIPlayer> aiPlayers, List<Animal> animals) {

        checkState(s, nightTime);

        //chase mate
        if (s.getTargetSprite() != null) {
            if (moveToTarget(s, 1F, s.getTargetSprite().getPosition(), true)) {
                s.setIntent(Sprite.Intent.MATING);
                return null;
            }
        }

        if (s.getTarget() != null) {
            if (s.getState().equals(Sprite.State.THIRSTY) || s.getState().equals(Sprite.State.HUNGRY)) {
                if (s.getViewCircle().contains(s.getTarget())) {
                    if (getTargetCoordinates() == null) {
                        setTargetCoordinates(new ArrayList<>());
                        setDontGoThere(new HashSet<>());
                        Vector2 target = s.getTarget();
                        float xPos = (float) (Math.floor(s.getCentrePosition().x) + 0.5F);
                        float yPos = (float) (Math.floor(s.getCentrePosition().y) + 0.5F);
                        Vector2 position = new Vector2(xPos, yPos);
                        Block[][] view = s.getView().getBlocks();
                        int xCentre = view.length/2;
                        int yCentre = view[0].length/2;
                        planRoute(s,0, target, position, position, view, xCentre, yCentre, locator);
                    }
                }

                if (getTargetCoordinates() != null) {
                    Iterator<Vector2> iterator = getTargetCoordinates().iterator();
                    if (iterator.hasNext() && moveToTarget(s, 0.2F, iterator.next(), true)) iterator.remove();

                    if (getTargetCoordinates().isEmpty()) {
                        s.setIntent(s.getState().equals(Sprite.State.THIRSTY) ? Sprite.Intent.DRINKING : Sprite.Intent.EATING);
                        setTargetCoordinates(null);
                    }
                    return null;
                } else if (moveToTarget(s, 1.5F, s.getTarget(), true)) {
                    s.setIntent(s.getState().equals(Sprite.State.THIRSTY) ? Sprite.Intent.DRINKING : Sprite.Intent.EATING);
                    return null;
                }
            } else if (s.getIntent().equals(Sprite.Intent.HOMEWARD)) {
                s.setState(Sprite.State.MOVING);
                if (moveToTarget(s, 4F, s.getTarget(), false)) {
                    s.setIntent(Sprite.Intent.SEARCHING);
                    s.setTarget(null);
                    setTargetCoordinates(null);
                }
                return null;
            }
        } else {
            if (s.getState().equals(Sprite.State.THIRSTY) && s.getLastWaterPos() != null) {
                if (moveToTarget(s, 5F, s.getLastWaterPos(), false)) s.setIntent(Sprite.Intent.SEARCHING);
                return null;
            }
            if (s.getState().equals(Sprite.State.HUNGRY) && s.getLastFoodPos() != null) {
                if (moveToTarget(s, 5F, s.getLastFoodPos(), false)) s.setIntent(Sprite.Intent.SEARCHING);
                return null;
            }
        }

        return super.decide(delta, s, nightTime, player, aiPlayers, animals);
    }

    public void checkState(Sprite s, boolean nightTime) {
        if (!s.getState().equals(Sprite.State.THIRSTY)) {
            if (s.getWater() == 0 || s.getWater()/s.getMaxWater() < 0.75) {
                s.setState(Sprite.State.THIRSTY);
                s.setIntent(Sprite.Intent.SEARCHING);
                setTargetCoordinates(null);
                s.setTarget(null);
                s.setTargetSprite(null);
            } else if (!s.getState().equals(Sprite.State.HUNGRY)) {
                if (s.getFood() == 0 || s.getFood()/s.getMaxFood() < 0.6) {
                    s.setState(Sprite.State.HUNGRY);
                    s.setIntent(Sprite.Intent.SEARCHING);
                    setTargetCoordinates(null);
                    s.setTarget(null);
                    s.setTargetSprite(null);
                } else
                if ((System.currentTimeMillis() - s.getBirthTime())/1000 > 100) {
                    if (s.isChild()) {
                        s.growUp();
                    } else if (!s.getState().equals(Sprite.State.HORNY)) {
                        System.out.println(((Animal) s).getDamageName() + " is horny");
                        s.setState(Sprite.State.HORNY);
                        s.setIntent(Sprite.Intent.SEARCHING);
                        setTargetCoordinates(null);
                        s.setTarget(null);
                    }
//                    else if (s.getTarget() == null || !s.getTarget().equals(((Animal)s).getSpawn())) {
//                        Vector2 spawnPos = ((Animal)s).getSpawn();
//                        if (checkSpawnDistance(s.getCentrePosition(), spawnPos, ((Animal) s).getDamageName()) > maximumSpawnDistance(nightTime)) {
//                            s.setTarget(new Vector2(spawnPos.x + 0.5F, spawnPos.y + 0.5F));
//                            s.setTargetSprite(null);
//                            setTargetCoordinates(null);
//                            s.setIntent(Sprite.Intent.HOMEWARD);
//                        }
//                    }
                }
            }
        }
    }

    //this is gonna be recursive bitch!!!
    public void planRoute(Sprite s, int iteration, Vector2 target, Vector2 position, Vector2 originalPosition, Block[][] view, int xCentre, int yCentre, Locator locator) {

//        System.out.println("Starting route planning with iteration " + iteration);
        //if iteration has met a preset max then return;
        if (iteration >= 8) {
            return;
        }
        float dist = position.dst(target);
//        System.out.println("Distance from target: " + dist);
        if (dist <= 1) {
            return;
        }

        Vector2 distance = new Vector2(target).sub(position);
        float angle = locator.getAngle(distance);

        //find nearest tile to move to
        Vector2 nextTarget = null;
        if (angle < 22.5 || angle >= 337.5) {
            //to the right
            Block block = view[xCentre + iteration][yCentre];
            if (block == null || !block.isColibible()) {
                nextTarget = (new Vector2(position.x + 1, position.y));
            } else {
//                System.out.println(block.getName() + " at " + block.getPosition() + " with cow at " + s.getPosition() + " to the right");
            }
        } else if (angle >= 22.5 && angle < 67.5) {
            //top right
            Block block = view[xCentre + iteration][yCentre + iteration];
            if (block == null || !block.isColibible()) {
                nextTarget = (new Vector2(position.x + 1, position.y + 1));
            } else {
//                System.out.println(block.getName() + " at " + block.getPosition() + " with cow at " + s.getPosition() + " to the top right");
            }
        } else if (angle >= 67.5 && angle < 112.5) {
            //up
            Block block = view[xCentre][yCentre + iteration];
            if (block == null || !block.isColibible()) {
                nextTarget = (new Vector2(position.x, position.y + 1));
            } else {
//                System.out.println(block.getName() + " at " + block.getPosition() + " with cow at " + s.getPosition() + " to the top");
            }
        } else if (angle >= 112.5 && angle < 157.5) {
            //top left
            Block block = view[xCentre - iteration][yCentre + iteration];
            if (block == null || !block.isColibible()) {
                nextTarget = (new Vector2(position.x - 1, position.y + 1));
            } else {
//                System.out.println(block.getName() + " at " + block.getPosition() + " with cow at " + s.getPosition()  + " to the top left");
            }
        } else if (angle >= 157.5 && angle < 202.5) {
            //to the left
            Block block = view[xCentre - iteration][yCentre];
            if (block == null || !block.isColibible()) {
                nextTarget = (new Vector2(position.x - 1, position.y));
            } else {
//                System.out.println(block.getName() + " at " + block.getPosition() + " with cow at " + s.getPosition()  + " to the left");
            }
        } else if (angle >= 202.5 && angle < 247.5) {
            //bottom left
            Block block = view[xCentre - iteration][yCentre - iteration];
            if (block == null || !block.isColibible()) {
                nextTarget = (new Vector2(position.x - 1, position.y - 1));
            } else {
//                System.out.println(block.getName() + " at " + block.getPosition() + " with cow at " + s.getPosition()  + " to the bottom left");
            }
        } else if (angle >= 247.5 && angle < 292.5) {
            //down
            Block block = view[xCentre][yCentre - iteration];
//            System.out.println("Looking for block below me using view coords: " + xCentre + ", " + (yCentre - iteration));
//            System.out.println((block == null || block.isColibible()) ? "block is null or passable" : (block.getName() + " at " + block.getPosition()));
            if (block == null || !block.isColibible()) {
                nextTarget = (new Vector2(position.x, position.y - 1));
            } else {
//                System.out.println(block.getName() + " at " + block.getPosition() + " with cow at " + s.getPosition() + " to the bottom");
            }
        } else if (angle >= 292.5 && angle < 337.5) {
            //bottom right
            Block block = view[xCentre + iteration][yCentre - iteration];
            if (block == null || !block.isColibible()) {
                nextTarget = (new Vector2(position.x + 1, position.y - 1));
            } else {
//                System.out.println(block.getName() + " at " + block.getPosition() + " with cow at " + s.getPosition() + " to the bottom right");
            }
        }
        if (nextTarget != null && !dontGoThere.contains(target)) {
//            System.out.println("Adding coord at: " + nextTarget.x + ", " + nextTarget.y + ". Position is: " + originalPosition + ". Target is " + target);
            targetCoordinates.add(nextTarget);
            planRoute(s, iteration+1, target, nextTarget, originalPosition, view, xCentre, yCentre, locator);
        } else {
//            System.out.println("Target is null, blanking all the shit." + (dontGoThere.contains(target) ? " DontGoThere said so" : ""));
            getDontGoThere().add(target);
            setTargetCoordinates(null);
            s.setTarget(null);
        }
    }
}
