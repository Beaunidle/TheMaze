package com.mygdx.game.model.environment.blocks;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.items.Food;
import com.mygdx.game.model.items.Material;

public class Grower extends Block {

    public enum CropType{
        POTATO
    }

    public enum GrowthState{
        SEEDLING,MIDDLING,MATURE
    }

    private final CropType cropType;
    private GrowthState growthState;
    private final float middlingTrigger;
    private final float matureTrigger;
    private float growth;

    public Grower(Vector2 pos, CropType type) {
        super(pos);
        this.cropType = type;
        this.growthState = GrowthState.SEEDLING;
        this.middlingTrigger = 2000;
        this.matureTrigger = 6000;
        this.growth = 0;
        setColibible(false);
    }

    public CropType getCropType() {
        return cropType;
    }

    public GrowthState getGrowthState() {
        return growthState;
    }

    public void grow(float grow) {
        if (growthState != GrowthState.MATURE) {
            growth += grow;
            if (growth > middlingTrigger && growthState.equals(GrowthState.SEEDLING)) growthState = GrowthState.MIDDLING;
            if (growth > matureTrigger) growthState = GrowthState.MATURE;
        }
    }

    public Material harvest() {
        if (growthState.equals(GrowthState.MATURE)) {
            growthState = GrowthState.SEEDLING;
            growth = 0;
            switch (cropType) {
                case POTATO:
                    return new Food(Food.FoodType.POTATO, 1);
                default:
                    return null;
            }
        }
        return null;
    }
}
