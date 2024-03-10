package com.mygdx.game.model.environment;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.model.moveable.Animal;

import java.util.Random;

public class AreaAffect extends Decoration {

    public enum AffectType {
        EXPLOSION,LIGHTNING,DAMAGE,FIREBITE
    }

    private Circle boundingCircle = new Circle();
    private boolean finished = false;
    private final float damage;
    private final float knockback;
    private final Timer.Task expireTimer = new Timer.Task() {
        @Override
        public void run() {
            finish();
        }
    };
    private final AffectType affectType;
    private final String spriteName;
    private final Animal.AnimalType animalType;

    public AreaAffect(Vector2 pos, String name, float radius, float expires, AffectType type, String spriteName, Animal.AnimalType animalType, float damage) {
        super(pos, name, radius, radius);
        this.boundingCircle = new Circle(getPosition().x, getPosition().y, radius);
        this.boundingCircle.setPosition(pos.x, pos.y);
        this.affectType = type;
        this.spriteName = spriteName;
        this.animalType = animalType;
        this.damage = damage;
        if (type.equals(AffectType.DAMAGE) || type.equals(AffectType.FIREBITE)) {
            knockback = 5;
        } else {
            knockback = 0;
        }
//        if (animalType != null && animalType.equals(Animal.AnimalType.SPIDER)) System.out.println("Damage area from: " + name);
        Timer.schedule(expireTimer, expires);
    }

    public Circle getBoundingCircle() {
        return boundingCircle;
    }

    public boolean isFinished() {
        return finished;
    }

    private void finish() {
        finished = true;
        expireTimer.cancel();
    }

    public float getDamage() {
        return damage;
    }

    public float getKnockback() {
        return knockback;
    }

    public AffectType getAffectType() {
        return affectType;
    }

    public String getSpriteName() {
        return spriteName;
    }

    public Animal.AnimalType getAnimalType() {
        return animalType;
    }

    public String getName() {
        StringBuilder affectString = new StringBuilder();
        affectString.append(super.getName());
        switch (affectType) {
            case EXPLOSION:
                break;
            case LIGHTNING:
                Random rand = new Random();
                affectString.append("-0").append(rand.nextInt(4) + 1);
        }
        return affectString.toString();
    }
}
