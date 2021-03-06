package com.mygdx.game.model;

import com.badlogic.gdx.math.Vector2;

public class BloodStain {

    private static final float HEIGHT = 0.9F;
    private static final float WIDTH = HEIGHT/2;

    private Vector2 position;
    private String name;

    public BloodStain(Vector2 position, String name) {
        this.position = position;
        this.name = name;
    }

    public static float getHEIGHT() {
        return HEIGHT;
    }

    public static float getWIDTH() {
        return WIDTH;
    }

    public Vector2 getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }
}
