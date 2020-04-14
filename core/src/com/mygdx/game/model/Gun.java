package com.mygdx.game.model;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class Gun {

    public enum Type {
        PISTOL, SMG, SHOTGUN, ROCKET
    }

    private float damage;
    private float firingRate;
    private Type type;

    public Gun(Gun.Type type) {
        this.type = type;
        damage = 20;
        setType(type);
    }

    public float getDamage() {
        return damage;
    }

    public float getFiringRate() {
        return firingRate;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
        switch (type) {
            case PISTOL:
                firingRate = 1.5F;
                break;
            case SMG:
                firingRate = 0.5F;
                break;
            case SHOTGUN:
                firingRate = 3F;
                break;
            case ROCKET:
                firingRate = 4F;
                break;
        }
    }

    public List<Bullet> fire(Vector2 position, Vector2 direction, float WIDTH, float HEIGHT, String playerName) {
        List<Bullet> bullets = new ArrayList<>();
        Vector2 bulletPosition = new Vector2(position);
        if (direction.y < 0) {
            bulletPosition.x += WIDTH/2 + 0.2;
        } else if (direction.y > 0) {
            bulletPosition.y += HEIGHT/2;
            bulletPosition.x -= 0.2;
        }
        if (direction.x > 0) {
            bulletPosition.x += WIDTH/2;
            bulletPosition.y += HEIGHT/2;
        }
        if (type.equals(Type.ROCKET)) {
            Bullet bullet = new Bullet(bulletPosition, direction, playerName);
            bullet.setExplosive(true);
            bullets.add(bullet);
        } else {
            bullets.add( new Bullet(bulletPosition, direction, playerName));
        }
        if (type.equals(Type.SHOTGUN)) {
           if (direction.x != 0 && direction.y == 0) {
               bullets.add(new Bullet(bulletPosition, new Vector2(direction.x, 1), playerName ));
               bullets.add(new Bullet(bulletPosition, new Vector2(direction.x, -1), playerName ));
           }
           if (direction.x == 0 && direction.y != 0) {
               bullets.add(new Bullet(bulletPosition, new Vector2(1, direction.y), playerName ));
               bullets.add(new Bullet(bulletPosition, new Vector2(-1, direction.y), playerName ));
           }
           if (direction.x != 0 && direction.y != 0) {
               bullets.add(new Bullet(bulletPosition, new Vector2(0, direction.y), playerName ));
               bullets.add(new Bullet(bulletPosition, new Vector2(direction.x, 0), playerName ));
           }
        }

        return bullets;
    }
}
