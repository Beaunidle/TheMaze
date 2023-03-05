package com.mygdx.game.model;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class GameObject {

    private final String name;
    private Vector2 position;
    private final Polygon bounds;

    public GameObject(String name, Vector2 position, Polygon bounds) {
        this.name = name;
        this.position = position;
        this.bounds = bounds;
        this.bounds.setPosition(position.x, position.y);
    }

    public String getName() {
        return name;
    }



    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public Polygon getBounds() {
        return bounds;
    }
}
