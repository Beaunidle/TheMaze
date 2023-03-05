package com.mygdx.game.model;

import com.mygdx.game.model.environment.blocks.FillableBlock;
import com.mygdx.game.model.items.Consumable;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.items.Material;
import com.mygdx.game.model.items.Placeable;
import com.mygdx.game.model.items.Swingable;
import com.mygdx.game.model.items.Throwable;

import java.util.List;

public class Recipe {

    private final String name;
    private final List<? extends Material> requirements;
    private final Item.ItemType type;
    private final Material.Type materialType;
    private final Swingable.SwingableType swingableType;
    private final Placeable.PlaceableType placeableType;
    private final Throwable.ThrowableType throwableType;
    private final Consumable.ConsumableType foodType;
    private final int baseDurability;
    private List<FillableBlock.FillableType> workbenchesRequired;
    //todo attribute requirements

    public Recipe(String name, List<? extends Material> requirements, Item.ItemType type, Material.Type materialType, Swingable.SwingableType swingableType, Consumable.ConsumableType foodType, Placeable.PlaceableType placeableType, Throwable.ThrowableType throwableType, int baseDurability, List<FillableBlock.FillableType> workbenchesRequired) {
        this.name = name;
        this.requirements = requirements;
        this.type = type;
        this.materialType = materialType;
        this.foodType = foodType;
        this.swingableType = swingableType;
        this.placeableType = placeableType;
        this.baseDurability = baseDurability;
        this.workbenchesRequired = workbenchesRequired;
        this.throwableType = throwableType;
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

    public Swingable.SwingableType getSwingableType() {
        return swingableType;
    }

    public Placeable.PlaceableType getPlaceableType() {
        return placeableType;
    }

    public Consumable.ConsumableType getConsumableType() {
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
