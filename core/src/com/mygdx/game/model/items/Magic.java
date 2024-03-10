package com.mygdx.game.model.items;

import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.model.moveable.Projectile;

public class Magic extends Item{

    public enum MagicType {
        PROJECTILE, AREA, SELF
    }

    public enum Element {
        ELECTRIC
    }

    private float firingRate;
    private float damage;
    private final MagicType magicType;
    private float buildupTime;
    private float manaRequired;
    private boolean reloading;
    private Timer.Task reloadTimer;

    //for area magic
    private float radius;
    private Element element;
    //for self magic
    private float effect;
    private String attribute;
    //for projectile magic
    private Projectile.ProjectileType projectileType;

    public Magic(MagicType type) {
        super(ItemType.MAGIC, 10);
        this.magicType = type;
        setMagicType();
    }

    public Magic(float radius, Element element) {
        super(ItemType.MAGIC, 10);
        this.magicType = MagicType.AREA;
        this.element = element;
        this.radius = radius;
        setHoldable(true);
        switch (element) {
            case ELECTRIC:
                setName("lightning");
                setUseTime(0.5F);
                setUseDelay(0.5F);
                manaRequired = 10;
                break;
            default:
                break;
        }
    }

    public Magic(float effect, String attribute) {
        super(ItemType.MAGIC, 10);
        this.magicType = MagicType.SELF;
        setName(attribute);
        setUseTime(0.5F);
        setUseDelay(0.5F);
        manaRequired = 5;
        this.attribute = attribute;
        this.effect = effect;
    }

    public Magic(Projectile.ProjectileType projectileType) {
        super(ItemType.MAGIC, 10);
        this.magicType = MagicType.PROJECTILE;
        this.projectileType = projectileType;
        switch (projectileType) {
            case FIREBALL:
                setName("fireball");
                setUseTime(0.5F);
                setUseDelay(0.5F);
                manaRequired = 1;
                break;
            case LIGHNINGBOLT:
                break;
            default:
                setName("");
                break;
        }
        setMagicType();
    }

    public void setMagicType() {
        switch (magicType) {
            case PROJECTILE:
                firingRate = 1.5F;
                damage = 5;
                buildupTime = 1;
                break;
            case AREA:
                firingRate = 0.5F;
                damage = 4;
                break;
            case SELF:
                firingRate = 3F;
                damage = 6;
                break;
            default:
                firingRate = 4F;
                damage = 2.5F;
                break;
        }
    }

    public float getFiringRate() {
        return firingRate;
    }

    public float getDamage() {
        return damage;
    }

    public MagicType getMagicType() {
        return magicType;
    }

    public float getBuildupTime() {
        return buildupTime;
    }

    public boolean isReloading() {
        return reloading;
    }

    public Timer.Task getReloadTimer() {
        return reloadTimer;
    }

    public float getManaRequired() {
        return manaRequired;
    }

    public Projectile.ProjectileType getProjectileType() {
        return projectileType;
    }

    public float getEffect() {
        return effect;
    }

    public String getAttribute() {
        return attribute;
    }

    public float getRadius() {
        return radius;
    }

    public Element getElement() {
        return element;
    }

    public boolean equals(Object o) {
        return o instanceof Magic && ((Magic) o).getMagicType().equals(this.magicType);
    }
}
