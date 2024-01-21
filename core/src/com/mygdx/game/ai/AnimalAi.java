package com.mygdx.game.ai;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.moveable.AIPlayer;
import com.mygdx.game.model.moveable.Animal;
import com.mygdx.game.model.moveable.Projectile;
import com.mygdx.game.model.moveable.Player;
import com.mygdx.game.model.moveable.Sprite;

import java.util.List;

public class AnimalAi extends BaseAi{

    public List<Projectile> decide(float delta, Sprite s, boolean nightTime, Player player, List<AIPlayer> aiPlayers, List<Animal> animals) {
        Animal animal = (Animal) s;
        int random = rand.nextInt(20);

        if (random > 3) {
            animal.moveForward();
        } else {
            animal.stop();
        }
//        if (animal.getAcceleration() == 0) {
//            System.out.println("No acceleration: " + animal.getDamageName());
//        }

        random = rand.nextInt(1000);
        if (animal.getRotateBy() == 0 && random >= 950 && random < 975) {
            animal.setRotateBy(15);
        } else if (random >= 950) {
            animal.setRotateBy(-15);
        } else if (animal.getRotateBy() != 0) {
            if (animal.getRotateBy() > 0) animal.setRotateBy(animal.getRotateBy()-1);
            if (animal.getRotateBy() < 0) animal.setRotateBy(animal.getRotateBy()+1);
        }
        checkRotateBy(animal, delta);
        return null;
    }

    public boolean facingTarget(float rotation, Vector2 animalPos, Vector2 spawnPos) {
//        float dst = spawnPos.dst(animalPos);
        Vector2 distance = new Vector2(spawnPos).sub(animalPos);
        float deg = locator.getAngle(distance);
        return (deg - rotation) > -45 && (deg - rotation) < 45;
//        return false;
    }

    protected double checkSpawnDistance(Vector2 animalPos, Vector2 spawnPos, String name) {
        float v0 = spawnPos.x - animalPos.x;
        float v1 = spawnPos.y - animalPos.y;
        double dif = Math.sqrt(v0*v0 + v1*v1);

        if (dif < 0) dif = dif * -1;
        return dif;
    }

    public int maximumSpawnDistance(Boolean nightTime) {
        return nightTime ? 10 : 30;
    }

    public boolean moveToTarget(Sprite s, float stop, Vector2 target, boolean shouldBeVisible) {
        if (target != null && (!shouldBeVisible || s.getViewCircle().contains(target))) {
            //todo plan a fucking route
            float dst = target.dst(s.getCentrePosition());
            float deg = locator.getAngle(new Vector2(target).sub(s.getCentrePosition()));
            //if angle is within a cone in front then move forward
            if (-20 < (deg - s.getRotation())  && (deg - s.getRotation()) < 20) {
                if (dst >= stop) {
                    s.moveForward();
                } else {
                    return true;
                }
            } else {
                s.stop();
                int random = rand.nextInt(100);
                if (random > 95) s.moveForward();
            }

            if (locator.locate(deg, s.getRotation()) < 0) {
                s.setTurningAntiClockwise(true);
                s.setTurningClcokwise(false);
            } else if (locator.locate(deg, s.getRotation()) > -0) {
                s.setTurningAntiClockwise(false);
                s.setTurningClcokwise(true);
            } else {
                s.setTurningAntiClockwise(false);
                s.setTurningClcokwise(false);
                s.stop();
            }
        } else {
            s.setTarget(null);
            s.setTurningAntiClockwise(false);
            s.setTurningClcokwise(false);
        }
        return false;
    }
}
