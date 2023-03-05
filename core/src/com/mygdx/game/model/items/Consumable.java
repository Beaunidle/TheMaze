package com.mygdx.game.model.items;

public class Consumable extends Material {

    public enum ConsumableType {
        BERRY, COOKEDMEAT, POTATO, MELON, CARROT, BERRYPASTE
    }

    ConsumableType consumableType;
    private int food, protein, fat, sugar;

    public Consumable(ConsumableType consumableType, int quantity) {
        super(Type.CONSUMABLE, quantity);
        this.consumableType = consumableType;
        setHoldable(true);
        switch (consumableType) {
            case COOKEDMEAT:
                setName("cookedmeat");
                //todo use proteins, decide on attributes affected in different ways
                food = 4;
                protein = 2;
                fat = 2;
                sugar = 0;
                break;
            case BERRY:
                setName("berry");
                food = 2;
                protein = 0;
                fat = 0;
                sugar = 2;
                break;
            case POTATO:
                setName("potato");
                setPlantable(true);
                food = 3;
                protein = 1;
                fat = 0;
                sugar = 2;
                break;
            case MELON:
                setName("melon");
                setPlantable(true);
                setMaxPerStack(1);
                food = 4;
                protein = 1;
                fat = 0;
                sugar = 3;
                break;
            case CARROT:
                setName("carrot");
                setPlantable(true);
                setMaxPerStack(10);
                food = 3;
                protein = 1;
                fat = 0;
                sugar = 2;
                break;
            case BERRYPASTE:
                setName("berrypaste");
                food = 3;
                protein = 0;
                fat = 0;
                sugar = 3;
                break;
        }
    }

    public ConsumableType getConsumableType() {
        return consumableType;
    }

    public int getFood() {
        return food;
    }

    public int getProtein() {
        return protein;
    }

    public int getFat() {
        return fat;
    }

    public int getSugar() {
        return sugar;
    }
}
