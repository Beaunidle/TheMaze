package com.mygdx.game.model.environment;

import com.badlogic.gdx.math.Vector2;

public class Decoration {

    private float height;
    private float width;
    private Vector2 position;
    private String name;

    public Decoration(Vector2 position, String name, float width, float height) {
        this.position = position;
        this.name = name;
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

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
