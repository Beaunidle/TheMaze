package com.mygdx.game.model;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class SpawnPoint {

    private final float size = 1f;

    private Vector2 position;
    private Polygon bounds;

    public float getSise() {
        return size;
    }

    public SpawnPoint(Vector2 pos) {
        this.position = pos;
        this.bounds = new Polygon(new float[]{0, 0, size, 0, size, size, 0, size});
        this.bounds.setPosition(pos.x, pos.y);

    }

    public Vector2 getPosition() {
        return position;
    }

    public Polygon getBounds() {
        return bounds;
    }
}
