package com.mygdx.game.model;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

public class GameButton {

    public enum Type {
        FIRE, USE, EXIT
    }
    private Vector2 position;
    private int radius;
    private Circle area;
    private Type type;

    public GameButton(Vector2 position, float radius, Type type) {
        area = new Circle(position.x, position.y, radius);
        this.type = type;
    }

    public Circle getArea() {
        return area;
    }

    public Type getType() {
        return type;
    }
}
