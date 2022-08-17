package com.mygdx.game.model.environment.blocks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.model.Inventory;
import com.mygdx.game.model.Recipe;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.items.Material;
import com.mygdx.game.utils.RecipeHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FillableBlock extends Block{

    public enum FillableType {
        INVSCREEN,MAPSCREEN,CAMPFIRE,BENCHHEALER,STONEANVIL
    }

    private final Inventory input;
    private final Inventory output;
    private final FillableType fillableType;
    private Material materialToCook;
    private boolean active = false;
    private final Timer.Task activeTimer = new Timer.Task() {
        @Override
        public void run() {
            startNewCycle();
        }
    };
    private boolean activeTimerOn;
    private boolean recipeSelect;
    private final List<Recipe> recipes = new ArrayList<>();
    private final List<Material> fuels = new ArrayList<>();
    private final RecipeHolder recipeHolder;
    private String name;

    private void startActiveTimer(float interval) {
        activeTimerOn = true;
        Timer.schedule(activeTimer, 0, interval);
    }

    private void stopActiveTimer() {
        activeTimerOn = false;
        activeTimer.cancel();
    }

    public FillableBlock(Vector2 pos, double maxDurability, FillableType type, float blockSize, int rotation, RecipeHolder recipeHolder) {
        super(pos, maxDurability, blockSize, rotation);
        this.fillableType = type;
        this.input = new Inventory(6);
        this.output = new Inventory(6);
        this.recipeHolder = recipeHolder;
        setBlockType(Block.BlockType.FILLABLE);

        recipes.addAll(recipeHolder.getRecipes(type));
        switch (type) {
            case CAMPFIRE:
                recipeSelect = false;
                name = "fire";
                break;
            case BENCHHEALER:
                recipeSelect = true;
                name = "bench-healer";
                break;
            case STONEANVIL:
                recipeSelect = true;
                name = "bench-stone";
                break;
        }
    }

    public FillableBlock(Vector2 pos, double maxDurability, FillableType type, Inventory input, Inventory output, int blockSize, int rotation, RecipeHolder recipeHolder) {
        super(pos, maxDurability, blockSize, rotation);
        this.fillableType = type;
        this.input = input;
        this.output = output;
        this.recipeHolder = recipeHolder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Inventory getInput() {
        return input;
    }

    public Inventory getOutput() {
        return output;
    }

    public FillableType getFillableType() {
        return fillableType;
    }

    public void toggleActive(){
        //todo hard code possible fuels, check for them all
        if (input.checkMaterial(new Material(Material.Type.WOOD, 1))) {
            active = !active;
        }
        else {
            active = false;
        }
        if (active && !activeTimerOn) {
            startActiveTimer(5);
        } else {
            stopActiveTimer();
        }
    }

    public void startNewCycle() {
        if (!recipeSelect) {
            //todo write method to select recipe for automatic bench. Maybe iterate through them all, in which case find an order
            Recipe recipe = recipes.get(0);
            if (materialToCook != null) {
                output.addInventory(new Material(materialToCook));
                materialToCook = null;
            }
            if (input.checkMaterial(new Material(Material.Type.WOOD, 1))) {
                input.removeMaterial(new Material(Material.Type.WOOD, 1));
                boolean enoughIngredients = true;
                for (Material required : recipe.getRequirements()) {
                    if (!input.checkMaterial(required)) {
                        enoughIngredients = false;
                        break;
                    }
                }
                if (enoughIngredients) {
                    materialToCook = new Material(recipe.getMaterialType(), 1);
                    for (Material required : recipe.getRequirements()) {
                        input.removeMaterial(required);
                    }
                }
            } else {
                materialToCook = null;
                toggleActive();
                stopActiveTimer();
            }
        }
    }

    public boolean isRecipeSelect() {
        return recipeSelect;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public String getName() {
        return name;
    }
}
