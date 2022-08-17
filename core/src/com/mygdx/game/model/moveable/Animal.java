package com.mygdx.game.model.moveable;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.ai.AnimalAi;
import com.mygdx.game.model.environment.AnimalSpawn;

import java.util.List;

public class Animal extends Sprite {

    private final AnimalAi ai;
    private AnimalSpawn spawn;

    public Animal(Vector2 position, String name, AnimalSpawn spawn) {
        super(position, 2.80F, 0.7F, 20);
        setName(name);
        ai = new AnimalAi();
        this.spawn = spawn;
        setRotationSpeed(100);
    }

    public List<Projectile> decide(float delta) {
        return ai.decide(delta, this);
    }

    public void die() {
        spawn.setPopulation(spawn.getPopulation() - 1);
    }

    public Vector2 getSpawn() {
        return spawn.getPosition();
    }

}
