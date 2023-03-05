package com.mygdx.game.model.pads;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.GameObject;

public class Pad extends GameObject {

    private static final float SIZE = 1F;

    Pad(String name, Vector2 pos) {
        super(name, pos, new Polygon(new float[]{0, 0, SIZE, SIZE, 0, SIZE, SIZE, 0}));
    }

    public static float getSIZE() {
        return SIZE;
    }
}
