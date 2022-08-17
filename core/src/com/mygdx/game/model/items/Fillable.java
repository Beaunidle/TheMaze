package com.mygdx.game.model.items;

public class Fillable extends Item {

    boolean filled;

    public Fillable(ItemType type, double durability) {
        super(type, durability);
        setHoldable(true);
        filled = false;
    }

    public boolean isFilled() {
        return filled;
    }

    public void setFilled(boolean filled) {
        this.filled = filled;
    }
}
