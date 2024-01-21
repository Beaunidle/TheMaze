package com.mygdx.game.model.environment.blocks;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.items.Consumable;
import com.mygdx.game.model.items.Material;

public class Grower extends Block {

    public enum CropType{
        POTATO, MELON, CARROT
    }

    public enum GrowthState{
        SEEDLING,MIDDLING,MATURE
    }

    private final CropType cropType;
    private GrowthState growthState;
    private final float middlingTrigger;
    private final float matureTrigger;
    private float growth;

    public Grower(Vector2 pos, CropType type, String name) {
        super(pos, name);
        setBlockType(BlockType.GROWER);
        this.cropType = type;
        this.growthState = GrowthState.SEEDLING;
        //todo different triggers for different growers
//        this.middlingTrigger = 43000;
//        this.matureTrigger = 90000;
        this.middlingTrigger = 430;
        this.matureTrigger = 900;
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
                    return new Consumable(Consumable.ConsumableType.POTATO, 1);
                case MELON:
                    return new Consumable(Consumable.ConsumableType.MELON, 1);
                case CARROT:
                    return new Consumable(Consumable.ConsumableType.CARROT, 1);
                default:
                    return null;
            }
        }
        return null;
    }

    @Override
    public String getName() {
        StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append(super.getName()).append("-");

        switch (growthState) {
            case SEEDLING:
                nameBuilder.append("seedling");
                break;
            case MIDDLING:
                nameBuilder.append("middling");
                break;
            case MATURE:
                nameBuilder.append("mature");
                break;
        }
        return nameBuilder.toString();
    }
}
