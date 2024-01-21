package com.mygdx.game.model.items;

public class Placeable extends Item {

    public enum PlaceableType {
        WALL,DOOR,CAMPFIRE,BENCHHEALER,STONEANVIL,BED,TORCH,CHEST,HOUSE
    }

    private PlaceableType placeableType;
    private float width = 1F, height = 1F;

    public Placeable(PlaceableType type, double durability) {
        super(ItemType.PLACEABLE, durability);
        this.placeableType = type;

        switch (placeableType) {
            case WALL:
                setName("wall");
                setMaxPerStack(20);
                break;
            case DOOR:
                setName("door");
                setMaxPerStack(10);
                break;
            case CAMPFIRE:
                setName("fire");
                setMaxPerStack(5);
                break;
            case BENCHHEALER:
                setName("bench-healer");
                setMaxPerStack(1);
                width = 2;
                height = 1;
                break;
            case STONEANVIL:
                setName("bench-stone");
                setMaxPerStack(1);
                break;
            case BED:
                setName("bed");
                setMaxPerStack(1);
                break;
            case TORCH:
                setName("torch");
                setMaxPerStack(10);
                break;
            case CHEST:
                setName("chest");
                setMaxPerStack(5);
                break;
            case HOUSE:
                setName("tipi");
                setMaxPerStack(1);
                width = 3;
                height = 3;
                break;
        }
    }

    public PlaceableType getPlaceableType() {
        return placeableType;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
