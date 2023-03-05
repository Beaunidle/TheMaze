package com.mygdx.game.model.environment;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.environment.blocks.Block;

public class Tilled extends Block {


    public Tilled(Vector2 position, String name) {
        super(position, name);
        setBlockType(BlockType.TILLED);
        setColibible(false);
    }

    public String getName() {
        return super.getName();
    }
}
