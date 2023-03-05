package com.mygdx.game.model.items;

public class Throwable extends Item {

    public enum ThrowableType {
        SPEAR, PEBBLE
    }

    private final ThrowableType throwableType;

    public Throwable(ThrowableType type, int quant) {
        super(ItemType.THROWABLE, 1);
        this.throwableType = type;
        setQuantity(quant);

        switch (throwableType) {
            case PEBBLE:
                setName("pebble");
                setHoldable(true);
                setUseDelay(0.5F);
                setMaxPerStack(10);
                break;
            case SPEAR:
                setName("spear");
                setUseDelay(0.5F);
                setMaxPerStack(1);
                break;
        }
    }

    public ThrowableType getThrowableType() {
        return throwableType;
    }
}
