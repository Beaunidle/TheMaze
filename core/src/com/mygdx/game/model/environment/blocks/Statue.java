package com.mygdx.game.model.environment.blocks;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.moveable.Projectile;
import com.mygdx.game.model.moveable.Sprite;

public class Statue extends Block {

    private Sprite.Effect effect;
    private final float timerTrigger;
    private float count;
    boolean ready;

    public Statue(Vector2 pos, double maxDurability, float width, float height, int rotation, String name, Sprite.Effect effect) {
        super(pos, maxDurability, width, height, rotation, name);
        setBlockType(BlockType.STATUE);
        ready = false;
        this.effect = effect;
        this.timerTrigger = 500;
        setColibible(false);
    }

    public void count(float count) {
        if (!ready) {
            this.count += count;
            if (this.count > this.timerTrigger) ready = true;
        }
    }

    public Sprite.Effect getEffect() {
        return effect;
    }

    public boolean isReady() {
        return ready;
    }

    public Projectile shoot() {
        if (ready) {
            ready = false;
            count = 0;
            return new Projectile(getFiringPosition(), getBounds().getRotation(), "statue", 3, Projectile.ProjectileType.FIREBALL, 0, false);
        }
        return null;
    }

    public Vector2 getFiringPosition() {
        Vector2 pos = getPosition();
        Float rotation = getBounds().getRotation();
        switch (rotation.intValue()) {
            case 0:
                return new Vector2(pos.x+1F, pos.y+0.25F);
            case 90:
                return new Vector2(pos.x+0.75F, pos.y+1F);
            case 180:
                return new Vector2(pos.x, pos.y+0.75F);
            case 270:
                return new Vector2(pos.x+0.25F, pos.y);
            default:
                return new Vector2(pos.x+0.25F, pos.y+0.25F);
        }
    }
}
