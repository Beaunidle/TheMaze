package com.mygdx.game.model.environment;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class SpawnPoint extends Decoration {

    private final float size = 1f;

    private Polygon bounds;

    public float getSize() {
        return size;
    }

    public SpawnPoint(Vector2 pos) {
        super(pos, "spawn", 0, 0);
        this.bounds = new Polygon(new float[]{0, 0, size, 0, size, size, 0, size});
        this.bounds.setPosition(pos.x, pos.y);

    }

    public Polygon getBounds() {
        return bounds;
    }
}
