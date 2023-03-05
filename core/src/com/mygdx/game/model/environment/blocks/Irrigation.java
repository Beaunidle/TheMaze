package com.mygdx.game.model.environment.blocks;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.pads.FloorPad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Irrigation extends Block {

    public Irrigation(Vector2 position, String name, float rotation) {
        super(position, name);
        setColibible(false);
        getBounds().setRotation(rotation);
    }


}
