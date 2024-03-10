package com.mygdx.game.model.items;

public class Material {

    public enum Type {
        COAL,STONE,WOOD,ITEM,MEAT,CONSUMABLE,GRASS,STICK,COPPER,FLINT,BONE,SCAPULA,BONEFRAGMENT,FIRESTONE
    }

    private String name;
    private Type type;
    private int maxPerStack;
    private int quantity;
    private float baseDamage;
    private float baseDurability;
    private float useTime;
    private float useDelay;
    private boolean mineable;
    private boolean mineableByHand;
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
        this.holdable = material.isHoldable();
        this.plantable = material.isPlantable();
        this.baseDamage = material.getBaseDamage();
        this.baseDurability = material.getBaseDurability();
    }

    public Material(Type type, int quantity) {
        this.type = type;
        this.quantity = quantity;
        useTime = 0.25F;
        useDelay = 0.25F;
        maxPerStack = 100;
        baseDamage = 0;
        baseDurability = 0;
        switch (type) {
            case FIRESTONE:
                name = "firestone-03";
                mineable = true;
                holdable = true;
                maxPerStack = 1;
                break;
            case COAL:
                name = "coal";
                mineable = true;
                break;
            case STONE:
                name = "stone";
                mineable = true;
                break;
            case WOOD:
                name = "wood";
                mineable = true;
                mineableByHand = true;
                break;
            case MEAT:
                name = "meat";
                mineable = true;
                mineableByHand = true;
                break;
            case GRASS:
                name = "grass";
                break;
            case STICK:
                name = "stick";
                break;
            case SCAPULA:
                name = "scapula";
                break;
            case COPPER:
                name = "copper";
                baseDamage = 3;
                baseDurability = 1;
                break;
            case FLINT:
                name = "flint";
                baseDamage = 2;
                baseDurability = 2;
                break;
            case BONE:
                name = "bone";
                baseDamage =  3;
                baseDurability = 5;
                break;
            case BONEFRAGMENT:
                setName("fragment_bone");
                setHoldable(false);
                setMaxPerStack(10);
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

    public boolean isMineable() {
        return mineable;
    }

    public boolean isMineableByHand() {
        return mineableByHand;
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

    public float getBaseDamage() {
        return baseDamage;
    }

    public void setBaseDamage(float baseDamage) {
        this.baseDamage = baseDamage;
    }

    public float getBaseDurability() {
        return baseDurability;
    }

    public void setBaseDurability(float baseDurability) {
        this.baseDurability = baseDurability;
    }
}
