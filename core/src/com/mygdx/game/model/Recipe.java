package com.mygdx.game.model;

import com.mygdx.game.model.environment.blocks.FillableBlock;
import com.mygdx.game.model.items.Food;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.items.Material;

import java.util.List;

public class Recipe {

    private final String name;
    private final List<? extends Material> requirements;
    private final Item.ItemType type;
    private final Material.Type materialType;
    private final Food.FoodType foodType;
    private final int baseDurability;
    private List<FillableBlock.FillableType> workbenchesRequired;
    //todo attribute requirements

    public Recipe(String name, List<? extends Material> requirements, Item.ItemType type, Material.Type materialType, Food.FoodType foodType, int baseDurability, List<FillableBlock.FillableType> workbenchesRequired) {
        this.name = name;
        this.requirements = requirements;
        this.type = type;
        this.materialType = materialType;
        this.foodType = foodType;
        this.baseDurability = baseDurability;
        this.workbenchesRequired = workbenchesRequired;
    }

    public String getName() {
        return name;
    }

    public List<? extends Material> getRequirements() {
        return requirements;
    }

    public Item.ItemType getType() {
        return type;
    }

    public Material.Type getMaterialType() {
        return materialType;
    }

    public Food.FoodType getFoodType() {
        return foodType;
    }

    public int getBaseDurability() {
        return baseDurability;
    }

    public List<FillableBlock.FillableType> getWorkbenchesRequired() {
        return workbenchesRequired;
    }

    public void setWorkbenchesRequired(List<FillableBlock.FillableType> workbenchesRequired) {
        this.workbenchesRequired = workbenchesRequired;
    }
}
