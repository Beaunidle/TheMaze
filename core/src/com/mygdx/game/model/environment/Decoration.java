package com.mygdx.game.model.environment;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.GameObject;

public class Decoration extends GameObject {

    private float height;
    private float width;

    public Decoration(Vector2 position, String name, float width, float height) {
        super(name, position, new Polygon(new float[]{0, 0, width, 0, width, height, 0, height}));
        this.width = width;
        this.height = height;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }
}
