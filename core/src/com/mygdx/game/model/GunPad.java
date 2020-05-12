package com.mygdx.game.model;

import com.badlogic.gdx.math.Vector2;

public class GunPad extends Pad{

    private Gun.Type type;

    public GunPad(Vector2 pos, Gun.Type type) {
        super(pos);
        this.type = type;
    }

    public Gun.Type getType() {
        return type;
    }
}
