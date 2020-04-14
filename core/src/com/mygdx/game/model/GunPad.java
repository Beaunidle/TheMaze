package com.mygdx.game.model;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class GunPad {


    private static final float SIZE = 1F;
    private Vector2 pos;
    private Polygon bounds;
    private Gun.Type type;

    public GunPad(Vector2 pos, Gun.Type type) {
        this.pos = pos;
        this.bounds = new Polygon(new float[]{0, 0, SIZE, SIZE, 0, SIZE, SIZE, 0});
        this.bounds.setPosition(pos.x, pos.y);
        this.type = type;
    }

    public static float getSIZE() {
        return SIZE;
    }

    public Vector2 getPos() {
        return pos;
    }

    public Polygon getBounds() {
        return bounds;
    }

    public Gun.Type getType() {
        return type;
    }
}
