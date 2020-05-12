package com.mygdx.game.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.ArrayList;
import java.util.List;

public class Gun {

    public enum Type {
        PISTOL, SMG, SHOTGUN, ROCKET
    }

    private float firingRate;
    private float damage;
    private Type type;
    private int CLIP_SIZE;
    private int ammo;
    private int clips;
    private int reloadTime;
    private boolean reloading;
    private Timer.Task reloadTimer;

    Gun(Gun.Type type) {
        this.type = type;
        setType(type);
        this.ammo = CLIP_SIZE;
    }

    public float getDamage() {
        return damage;
   }

    float getFiringRate() {
        return firingRate;
    }

    public int getAmmo() {
        return ammo;
    }

    public int getClips() {
        return clips;
    }

        public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
        switch (type) {
            case PISTOL:
                firingRate = 1.5F;
                damage = 5;
                CLIP_SIZE = 8;
                clips = 3;
                reloadTime = 2;
                break;
            case SMG:
                firingRate = 0.5F;
                damage = 4;
                CLIP_SIZE = 24;
                clips = 3;
                reloadTime = 2;
                break;
            case SHOTGUN:
                firingRate = 3F;
                damage = 6;
                CLIP_SIZE = 6;
                clips = 3;
                reloadTime = 3;
                break;
            case ROCKET:
                firingRate = 4F;
                damage = 2.5F;
                CLIP_SIZE = 3;
                clips = 3;
                reloadTime = 4;
                break;
        }
    }

    List<Bullet> fire(Vector2 position, float rotation, String playerName, boolean homing, boolean damageBoost) {
        List<Bullet> bullets = new ArrayList<>();
        Vector2 bulletPosition = new Vector2(position);


        float bulletDamage = damageBoost ? damage * 2 : damage;
        if (ammo > 0) {
            if (type.equals(Type.ROCKET)) {
                Bullet bullet = new Bullet(bulletPosition, rotation, playerName, bulletDamage, homing);
                bullet.setExplosive();
                bullets.add(bullet);
            } else {
                bullets.add( new Bullet(bulletPosition, rotation, playerName, bulletDamage, homing));
            }
            if (type.equals(Type.SHOTGUN)) {
                float bulletRot1 = rotation - 15;
                if (bulletRot1 < 0) bulletRot1 = 360 + bulletRot1;
                float bulletRot2 = rotation + 15;
                if (bulletRot2 > 360) bulletRot2 = bulletRot2 - 360;

                bullets.add(new Bullet(bulletPosition, bulletRot1, playerName, bulletDamage, homing));
                bullets.add(new Bullet(bulletPosition, bulletRot2, playerName, bulletDamage, homing));
            }
            ammo--;
        } else {
            reload();
        }
        return bullets;
    }

    public boolean fullAmmo() {
        return ammo == CLIP_SIZE;
    }

    public void reload() {
        if (clips > 0 && !reloading) {
            reloadTimer =  new Timer.Task() {
                @Override
                public void run() {
                    reloading = false;
                    ammo = CLIP_SIZE;
                    clips--;
                    reloadTimer.cancel();
                }
            };
            reloading = true;
            Timer.schedule(reloadTimer, reloadTime);
        }
    }
}
