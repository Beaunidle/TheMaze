package com.mygdx.game.model.items;

public class Material {

    public enum Type {
        COAL, STONE, WOOD, ITEM, MEAT, FOOD, BERRYPASTE, GRASS, STICK, PEBBLE
    }

    private String name;
    private Type type;
    private int maxPerStack = 100;
    private int quantity;
    private float useTime;
    private float useDelay;
    private boolean holdable;
    private boolean plantable;

    public Material() {

    }

    public Material(Material material) {
        this.type = material.type;
        this.name = material.getName();
        this.maxPerStack = material.maxPerStack;
        this.quantity = material.getQuantity();
        this.useTime = 0.25F;
        this.useDelay = 0.25F;
    }

    public Material(Type type, int quantity) {
        this.type = type;
        this.quantity = quantity;
        useTime = 0.25F;
        useDelay = 0.25F;
        switch (type) {
            case COAL:
                name = "coal";
                break;
            case STONE:
                name = "stone";
                break;
            case WOOD:
                name = "wood";
                break;
            case MEAT:
                name = "meat";
                break;
            case BERRYPASTE:
                name = "berrypaste";
                holdable = true;
                break;
            case GRASS:
                name = "grass";
                break;
            case STICK:
                name = "stick";
                break;
            case PEBBLE:
                name = "pebble";
                holdable = true;
                break;
        }
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getMaxPerStack() {
        return maxPerStack;
    }

    public void setMaxPerStack(int maxPerStack) {
        this.maxPerStack = maxPerStack;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getUseTime() {
        return useTime;
    }

    public float getUseDelay() {
        return useDelay;
    }

    public void setUseDelay(float useDelay) {
        this.useDelay = useDelay;
    }

    public void setUseTime(float useTime) {
        this.useTime = useTime;
    }

    public boolean isPlantable() {
        return plantable;
    }

    public void setPlantable(boolean plantable) {
        this.plantable = plantable;
    }

    public boolean isHoldable() {
        return holdable;
    }

    public void setHoldable(boolean holdable) {
        this.holdable = holdable;
    }
}
