package com.mygdx.game.ai;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.moveable.Animal;
import com.mygdx.game.model.moveable.Projectile;
import com.mygdx.game.model.moveable.Player;
import com.mygdx.game.model.moveable.Sprite;

import java.util.List;

public class AnimalAi extends BaseAi{

    public List<Projectile> decide(float delta, Sprite s) {
        Animal animal = (Animal) s;
        Vector2 spawnPos = animal.getSpawn();
        s.setState(Player.State.MOVING);

//        boolean facingTarget = facingTarget(animal.getRotation(), animal.getCentrePosition(), spawnPos);
        if (checkSpawnDistance(animal.getCentrePosition(), spawnPos) > 10 && !facingTarget(animal.getRotation(), animal.getCentrePosition(), spawnPos)) {
            animal.turnAround(rand.nextInt(2) >= 1 ? 90 : - 90);
        }

        checkRotateBy(animal, delta);

        int random = rand.nextInt(10);

        if (random > 5) {
            s.moveForward();
        } else {
            s.stop();
        }

        random = rand.nextInt(1000);
        if (random >= 900 && random < 950) s.rotateClockwise(delta);
        if (random >= 950) s.rotateAntiClockwise(delta);

        return null;
    }

    public boolean facingTarget(float rotation, Vector2 animalPos, Vector2 spawnPos) {
//        float dst = spawnPos.dst(animalPos);
        Vector2 distance = new Vector2(spawnPos).sub(animalPos);
        float deg = locator.getAngle(distance);
        return (deg - rotation) > -45 && (deg - rotation) < 45;
//        return false;
    }

    private double checkSpawnDistance(Vector2 animalPos, Vector2 spawnPos) {
        float v0 = spawnPos.x - animalPos.x;
        float v1 = spawnPos.y - animalPos.y;
        double dif = Math.sqrt(v0*v0 + v1*v1);

        if (dif < 0) dif = dif * -1;
        return dif;
    }
}
