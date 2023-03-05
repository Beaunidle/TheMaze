package com.mygdx.game.model.items;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

public class Item extends Material{

    public enum ItemType {
        PLACEABLE,SWINGABLE,THROWABLE,JAR,SHIELD,ARMOUR,RANGED,MAGIC
    }

    private double durability;
    private ItemType type;
    private int rotation = 0;
    private boolean rotateTimerOn;

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
            rotation = rotation + (this instanceof Swingable && ((Swingable) this).getSwingableType().equals(Swingable.SwingableType.SHOVEL) ? 45 : 90);
            if (rotation >= 360) rotation = rotation - 360;
            startRotateTimer(0.5F);
        }
    }

    private void startRotateTimer(float delay) {
        rotateTimerOn = true;
        Timer.schedule(rotateTimer, delay, delay);
    }

    private void stopRotateTimer() {
        rotateTimerOn = false;
        rotateTimer.cancel();
    }

}
