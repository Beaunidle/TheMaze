package com.mygdx.game.utils;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class JoyStick {

    private static final float WIDTH = 2F;

    private Vector2 position;
    private Vector2 drag;
    private Circle touchCircle;
    private Locator locator;

    public JoyStick(Vector2 position) {
        this.position = position;
        touchCircle = new Circle(position.x, position.y, 500);
        locator = new Locator();
    }

    public static float getWIDTH() {
        return WIDTH;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getDrag() {
        return drag;
    }

    public void setDrag(Vector2 drag) {
        this.drag = drag;
    }

    public Circle getTouchCircle() {
        return touchCircle;
    }

    public float getDistance() {
        return drag.dst(position);
    }

    public float getAngle() {
        Vector2 distance = new Vector2(drag).sub(position);
        return locator.getAngle(distance);
    }
}
