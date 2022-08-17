package com.mygdx.game.model.items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.model.moveable.Projectile;
import com.mygdx.game.utils.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class Ranged extends Item{

    public enum RangedType {
        PISTOL, SMG, SHOTGUN, ROCKET
    }

    private float firingRate;
    private float damage;
    private RangedType rangedType;
    private int CLIP_SIZE;
    private int ammo;
    private int clips;
    private float reloadTime;
    private boolean reloading;
    private Timer.Task reloadTimer;

    public Ranged(RangedType type) {
        super(ItemType.RANGED, 10);
        this.rangedType = type;
        this.ammo = CLIP_SIZE;
        setRangedType();
    }

    public float getDamage() {
        return damage;
   }

    public float getFiringRate() {
        return firingRate;
    }

    public int getAmmo() {
        return ammo;
    }

    public int getClips() {
        return clips;
    }

    public boolean isReloading() {
        return reloading;
    }

    public RangedType getRangedType() {
        return rangedType;
    }

    public void setRangedType() {
        switch (rangedType) {
            case PISTOL:
                firingRate = 1.5F;
                damage = 5;
                CLIP_SIZE = 8;
                clips = 3;
                reloadTime = 1;
                break;
            case SMG:
                firingRate = 0.5F;
                damage = 4;
                CLIP_SIZE = 24;
                clips = 3;
                reloadTime = 1;
                break;
            case SHOTGUN:
                firingRate = 3F;
                damage = 6;
                CLIP_SIZE = 6;
                clips = 3;
                reloadTime = 2;
                break;
            case ROCKET:
                firingRate = 4F;
                damage = 2.5F;
                CLIP_SIZE = 3;
                clips = 3;
                reloadTime = 2.5F;
                break;
        }
        this.ammo = CLIP_SIZE;
    }

    public List<Projectile> fire(Vector2 position, float rotation, String playerName, boolean homing, boolean damageBoost, Projectile.ProjectileType projectileType) {
        List<Projectile> projectiles = new ArrayList<>();
        Vector2 bulletPosition = new Vector2(position);

        float bulletDamage = damageBoost ? damage * 2 : damage;
        if (ammo > 0) {
            if (rangedType.equals(RangedType.ROCKET)) {
                Projectile projectile = new Projectile(bulletPosition, rotation, playerName, bulletDamage, projectileType, 0, false);
                projectile.setExplosive();
                projectiles.add(projectile);
            } else {
                projectiles.add( new Projectile(bulletPosition, rotation, playerName, bulletDamage, projectileType, 0, false));
            }
            if (rangedType.equals(RangedType.SHOTGUN)) {
                float bulletRot1 = rotation - 15;
                if (bulletRot1 < 0) bulletRot1 = 360 + bulletRot1;
                float bulletRot2 = rotation + 15;
                if (bulletRot2 > 360) bulletRot2 = bulletRot2 - 360;

                projectiles.add(new Projectile(bulletPosition, bulletRot1, playerName, bulletDamage, projectileType, 0, false));
                projectiles.add(new Projectile(bulletPosition, bulletRot2, playerName, bulletDamage, projectileType, 0, false));
            }
            ammo--;
        } else {
            reload();
        }
        return projectiles;
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
