package com.mygdx.game.model;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Block {

    static final float SIZE = 1f;

    private Vector2 position = new Vector2();
    private Polygon bounds = new Polygon();

    public static float getSIZE() {
        return SIZE;
    }

    public Block(Vector2 pos) {
        this.position = pos;
        this.bounds = new Polygon(new float[]{0, 0, SIZE, SIZE, 0, SIZE, SIZE, 0});
        this.bounds.setPosition(pos.x, pos.y);

    }

    public Vector2 getPosition() {
        return position;
    }

    public Polygon getBounds() {
        return bounds;
    }
}
