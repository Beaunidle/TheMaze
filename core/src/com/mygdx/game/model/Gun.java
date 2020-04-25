package com.mygdx.game.model;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Gun {

    public enum Type {
        PISTOL, SMG, SHOTGUN, ROCKET
    }

    private float firingRate;
    private float damage;
    private Type type;

    Gun(Gun.Type type) {
        this.type = type;
        setType(type);
    }

    public float getDamage() {
        return damage;
   }

    float getFiringRate() {
        return firingRate;
    }

//    public Type getType() {
//        return type;
//    }

    public void setType(Type type) {
        this.type = type;
        switch (type) {
            case PISTOL:
                firingRate = 1.5F;
                damage = 5;
                break;
            case SMG:
                firingRate = 0.5F;
                damage = 4;
                break;
            case SHOTGUN:
                firingRate = 3F;
                damage = 4;
                break;
            case ROCKET:
                firingRate = 4F;
                damage = 5;
                break;
        }
    }

    List<Bullet> fire(Vector2 position, float rotation, float WIDTH, float HEIGHT, String playerName) {
        List<Bullet> bullets = new ArrayList<>();
        Vector2 bulletPosition = new Vector2(position);

        if (type.equals(Type.ROCKET)) {
            Bullet bullet = new Bullet(bulletPosition, rotation, playerName, damage);
            bullet.setExplosive();
            bullets.add(bullet);
        } else {
            bullets.add( new Bullet(bulletPosition, rotation, playerName, damage));
        }
        if (type.equals(Type.SHOTGUN)) {
           float bulletRot1 = rotation - 15;
           if (bulletRot1 < 0) bulletRot1 = 360 + bulletRot1;
           float bulletRot2 = rotation + 15;
           if (bulletRot2 > 360) bulletRot2 = bulletRot2 - 360;

            bullets.add(new Bullet(bulletPosition, bulletRot1, playerName, damage));
            bullets.add(new Bullet(bulletPosition, bulletRot2, playerName, damage));
        }
        return bullets;
    }
}
