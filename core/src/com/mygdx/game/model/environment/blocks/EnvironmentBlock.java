package com.mygdx.game.model.environment.blocks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.model.items.Food;
import com.mygdx.game.model.items.Material;

import java.util.Vector;

public class EnvironmentBlock extends Block{

    private int returnPerHit;
    private final Material material;
    private int replenishTime;
    private boolean replenishing = false;
    private final Timer.Task replenishTimer = new Timer.Task() {
        @Override
        public void run() {
            increaseDurability(1);
            if (getDurability() >= getMaxDurability()) {
                stopReplenishTimer();
            }
        }
    };


    public EnvironmentBlock(Vector2 pos, Material material) {
        super(pos, 10);
        this.material = material;
        if (material instanceof Food) {
            switch (((Food) material).getFoodType()) {
                case BERRY:
                    returnPerHit = 2;
                    replenishTime = 5;
                    setColibible(false);
                    break;
                case POTATO:
                    break;
            }
        } else {
            switch (material.getType()) {
                case COAL:
                    returnPerHit = 2;
                    replenishTime = 5;
                    break;
                case STONE:
                    returnPerHit = 3;
                    replenishTime = 5;
                    break;
                case WOOD:
                    returnPerHit = 4;
                    replenishTime = 5;
                    break;
                case MEAT:
                    returnPerHit = 2;
                    setColibible(false);
                    break;
                case GRASS:
                    returnPerHit = 2;
                    replenishTime = 5;
                    setColibible(false);
                    break;
                case PEBBLE:
                case STICK:
                    returnPerHit = 1;
                    replenishTime = 5;
                    setDurability(1);
                    setMaxDurability(1);
                    setColibible(false);
                    break;
            }
        }
    }

    public int hit() {
        if (getDurability() <=0) return 0;
        decreaseDurability(1);
        if (getDurability() <= 0 && !replenishing) {
            startReplenishTimer(replenishTime);
        }
        System.out.println("Returning " + returnPerHit + getMaterial().getName());
        return returnPerHit;
    }

    public Material getMaterial() {
        return material;
    }

    private void startReplenishTimer(float delay) {
        Timer.schedule(replenishTimer, delay, delay);
        replenishing = true;
    }

    private void stopReplenishTimer() {
        replenishTimer.cancel();
        replenishing = false;
    }
}
