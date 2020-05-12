package com.mygdx.game.model;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class Pad {

    private static final float SIZE = 1F;
    private Vector2 pos;
    private Polygon bounds;

    Pad(Vector2 pos) {
        this.pos = pos;
        this.bounds = new Polygon(new float[]{0, 0, SIZE, SIZE, 0, SIZE, SIZE, 0});
        this.bounds.setPosition(pos.x, pos.y);
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
}
