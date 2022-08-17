package com.mygdx.game.model.items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

public class Item extends Material{

    public enum ItemType {
        PICK,SWORD,SPEAR,HAMMER,AXE,HOE,SHOVEL,JAR,WALL,DOOR,CAMPFIRE,BENCHHEALER,STONEANVIL,BED,SHIELD,ARMOUR,RANGED,MAGIC
    }

    private double durability;
    private ItemType type;
    private int rotation = 0;
    private boolean rotateTimerOn;
    private float size = 1F;

    private final Timer.Task rotateTimer = new Timer.Task() {
        @Override
        public void run() {
            stopRotateTimer();
        }
    };

    public Item(Item item){
        setName(item.getName());
        setType(item.getType());
        setItemType(item.getItemType());
        setQuantity(item.getQuantity());
        setMaxPerStack(item.getMaxPerStack());
        setHoldable(item.isHoldable());
        setUseDelay(item.getUseDelay());
        setUseTime(item.getUseTime());
    }

    public Item(ItemType type, double durability) {
        setType(Type.ITEM);
        setQuantity(1);
        setMaxPerStack(1);
        setHoldable(true);
        this.type = type;
        this.durability = durability;
        switch (type) {
            case PICK:
                setName("inv_pick");
                setUseTime(0.5F);
                setUseDelay(0.5F);
                break;
            case SWORD:
                setName("inv_sword");
                setUseTime(0.5F);
                setUseDelay(0.5F);
                break;
            case HOE:
                setName("inv_hoe");
                setUseTime(0.5F);
                setUseDelay(0.5F);
                break;
            case SHOVEL:
                setName("shovel");
                setUseTime(0.5F);
                setUseDelay(0.5F);
                break;
            case MAGIC:
                break;
            case SPEAR:
                setName("spear");
                setUseTime(0.5F);
                setUseDelay(0.5F);
                setMaxPerStack(1);
                break;
            case HAMMER:
                setName("inv_hammer");
                break;
            case WALL:
                setName("wall");
                setMaxPerStack(10);
                break;
            case DOOR:
                setName("door");
                setMaxPerStack(10);
                break;
            case CAMPFIRE:
                setName("fire");
                setMaxPerStack(5);
                size = 1;
                break;
            case BENCHHEALER:
                setName("bench-healer");
                setMaxPerStack(1);
                size = 2;
                break;
            case STONEANVIL:
                setName("bench-stone");
                setMaxPerStack(1);
                size = 1;
                break;
            case BED:
                setName("bed");
                setMaxPerStack(1);
                size = 1;
                break;
            case JAR:
                setName("inv_jar");
                break;
            case SHIELD:
                setName("inv_shield");
                break;
            case ARMOUR:
                setName("armour-01");
        }
    }

    public double getDurability() {
        return durability;
    }

    public void setDurability(double durability) {
        this.durability = durability;
    }

    public ItemType getItemType() {
        return type;
    }

    public void setItemType(ItemType type) {
        this.type = type;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public void rotate() {
        if (!rotateTimerOn) {
            rotation = rotation + (this.getItemType().equals(ItemType.SHOVEL) ? 45 : 90);
            if (rotation >= 360) rotation = rotation - 360;
            startRotateTimer(1);
        }
    }

    public ItemType use() {
        switch (type) {
            case PICK:
                return ItemType.PICK;
            case SWORD:
                return ItemType.SWORD;
        }
        return null;
    }

    private void startRotateTimer(float delay) {
        rotateTimerOn = true;
        Timer.schedule(rotateTimer, delay, delay);
    }

    private void stopRotateTimer() {
        rotateTimerOn = false;
        rotateTimer.cancel();
    }

    public float getSize() {
        return size;
    }
}
