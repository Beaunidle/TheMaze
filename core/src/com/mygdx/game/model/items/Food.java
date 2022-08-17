package com.mygdx.game.model.items;

public class Food extends Material {

    public enum FoodType {
        BERRY, COOKEDMEAT, POTATO
    }

    FoodType foodType;

    public Food(FoodType foodType, int quantity) {
        super(Type.FOOD, quantity);
        this.foodType = foodType;
        setHoldable(true);
        switch (foodType) {
            case COOKEDMEAT:
                setName("cookedmeat");
                break;
            case BERRY:
                setName("berry");
                break;
            case POTATO:
                setName("potato");
                setPlantable(true);
                break;
        }
    }

    public FoodType getFoodType() {
        return foodType;
    }
}
