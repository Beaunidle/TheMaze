package com.mygdx.game.model.environment;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class SpawnPoint extends Decoration {

    private final float size = 1f;

    public float getSize() {
        return size;
    }

    public SpawnPoint(Vector2 pos) {
        super(pos, "spawn", 1, 1);
    }
}
