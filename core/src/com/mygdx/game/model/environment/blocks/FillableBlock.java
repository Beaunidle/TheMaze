package com.mygdx.game.model.environment.blocks;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.model.Inventory;
import com.mygdx.game.model.Recipe;
import com.mygdx.game.model.items.Consumable;
import com.mygdx.game.model.items.Material;
import com.mygdx.game.utils.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FillableBlock extends Block {

    public enum FillableType {
        INVSCREEN,MAPSCREEN,CAMPFIRE,BENCHHEALER,STONEANVIL,TORCH,CHEST,HOUSE,BODYANIMAL
    }

    private Inventory input = new Inventory(0);
    private Inventory output = new Inventory(0);
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

    private void startActiveTimer(float interval) {
        activeTimerOn = true;
        Timer.schedule(activeTimer, 0, interval);
    }

    private void stopActiveTimer() {
        activeTimerOn = false;
        activeTimer.cancel();
    }

    public FillableBlock(Vector2 pos, double maxDurability, FillableType type, float width, float height, int rotation, RecipeHolder recipeHolder, String name) {
        super(pos, maxDurability, width, height, rotation, name);
        setBlockType(BlockType.FILLABLE);
        this.fillableType = type;
        this.recipeHolder = recipeHolder;
        setBlockType(Block.BlockType.FILLABLE);

        if (recipeHolder != null) recipes.addAll(recipeHolder.getRecipes(type));
        switch (type) {
            case CAMPFIRE:
                this.input = new Inventory(6);
                this.output = new Inventory(6);
                recipeSelect = true;
                break;
            case BENCHHEALER:
            case STONEANVIL:
                recipeSelect = false;
                break;
            case TORCH:
                toggleActive();
                break;
            case CHEST:
                this.input = new Inventory(20);
                recipeSelect = true;
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
        if (input.checkMaterial(new Material(Material.Type.WOOD, 1)) || fillableType.equals(FillableType.TORCH)) {
            active = !active;
        }
        else {
            active = false;
        }
        if (!fillableType.equals(FillableType.TORCH)) {
            if (active && !activeTimerOn) {
                startActiveTimer(5);
            } else {
                stopActiveTimer();
            }
        }
    }

    public void startNewCycle() {
            //todo write method to select recipe for automatic bench. Maybe iterate through them all, in which case find an order
            Recipe recipe = recipes.get(0);
            if (materialToCook != null) {
                output.addInventory(materialToCook instanceof Consumable ? new Consumable(((Consumable) materialToCook).getConsumableType(), materialToCook.getQuantity()) : new Material(materialToCook));
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
                    if (recipe.getConsumableType() != null) {
                        materialToCook = new Consumable(recipe.getConsumableType(), 1);
                    } else {
                        materialToCook = new Material(recipe.getMaterialType(), 1);
                    }
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

    public boolean isRecipeSelect() {
        return recipeSelect;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public String getName() {
        StringBuilder fillableName = new StringBuilder();
        fillableName.append(super.getName());
        if (getFillableType().equals(FillableBlock.FillableType.CAMPFIRE)) {
            if (isActive()) {
                Random rand = new Random();
                int num = rand.nextInt(3) + 1;
                fillableName.append("-burning").append(num);
            }
        } else if (getFillableType().equals(FillableBlock.FillableType.TORCH)) {
            Random rand = new Random();
            int myNum = rand.nextInt(4) + 1;
            if (isActive()) fillableName.append("-burning-0").append(myNum);
        }
        return fillableName.toString();
    }
}
