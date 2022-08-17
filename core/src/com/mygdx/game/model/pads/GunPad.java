package com.mygdx.game.model.pads;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.items.Ranged;

public class GunPad extends Pad {

    private final Ranged.RangedType type;

    public GunPad(Vector2 pos, Ranged.RangedType type) {
        super(pos);
        this.type = type;
    }

    public Ranged.RangedType getType() {
        return type;
    }
}
