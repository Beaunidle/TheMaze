package com.mygdx.game.model;

import com.badlogic.gdx.math.Vector2;

public class FloorPad extends Pad {

    public enum Type {
        SPIKE, SLIME, MOVE
    }

    private Type type;
    private int rot;

    public FloorPad(Vector2 pos, Type type) {
        super(pos);
        this.type = type;
    }

    public FloorPad(Vector2 pos, int rot) {
        super(pos);
        type = Type.MOVE;
        this.rot = rot;
    }

    public Type getType() {
        return type;
    }

    public int getRot() {
        return rot;
    }
}
