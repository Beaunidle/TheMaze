package com.mygdx.game.utils;

import static com.mygdx.game.model.items.Food.FoodType.*;
import static com.mygdx.game.model.items.Item.ItemType.*;
import static com.mygdx.game.model.items.Material.Type.*;

import com.mygdx.game.model.Recipe;
import com.mygdx.game.model.environment.blocks.FillableBlock;
import com.mygdx.game.model.items.Food;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.items.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeHolder {

    private Map<String, Recipe> recipes = new HashMap<>();

    public RecipeHolder() {
        addInitialRecipes();
    }

    private void addInitialRecipes() {
        //todo add specific tools needed to build benches
        List<Material> requirements3 = Arrays.asList(new Material(GRASS, 3), new Material(WOOD, 2));
        recipes.put("wall", new Recipe("wall", requirements3, WALL, null, null, 30, null));
        List<Material> requirements5 = Arrays.asList(new Material(GRASS, 2), new Material(WOOD, 3));
        recipes.put("door", new Recipe("door", requirements5, DOOR, null, null,20, null));
        List<Material> requirements8 = Arrays.asList(new Material(WOOD, 1), new Material(STONE, 4));
        recipes.put("fire", new Recipe("fire", requirements8, CAMPFIRE, null, null,20, null));
        List<Material> requirements10 = Arrays.asList(new Material(STONE, 5), new Material(WOOD, 1));
        recipes.put("bench-healer", new Recipe("bench-healer", requirements10, BENCHHEALER, null, null, 20, null));
        List<Material> requirements11 = Collections.singletonList(new Material(STONE, 10));
        recipes.put("bench-stone", new Recipe("bench-stone", requirements11, STONEANVIL, null, null, 20, null));
        List<Material> requirements12 = Collections.singletonList(new Material(PEBBLE, 3));
        recipes.put("stone", new Recipe("stone", requirements12, null, STONE, null, 20, null));

        List<Material> requirements = Arrays.asList(new Material(STONE, 2), new Material(WOOD, 2));
        recipes.put("inv_sword", new Recipe("inv_sword", requirements, SWORD, null, null, 20, Collections.singletonList(FillableBlock.FillableType.STONEANVIL)));
        List<Material> requirements2 = Arrays.asList(new Material(STONE, 3), new Material(WOOD, 2));
        recipes.put("inv_hammer", new Recipe("inv_hammer", requirements2, HAMMER, null, null, 20, Collections.singletonList(FillableBlock.FillableType.STONEANVIL)));
        List<Material> requirements7 = Arrays.asList(new Material(WOOD, 2), new Material(STONE, 3));
        recipes.put("inv_pick", new Recipe("inv_pick", requirements7, PICK, null, null,20, Collections.singletonList(FillableBlock.FillableType.STONEANVIL)));
        List<Material> requirements4 = Arrays.asList(new Material(STONE, 2), new Material(WOOD, 4));
        recipes.put("inv_shield", new Recipe("inv_shield", requirements4, SHIELD, null, null,20, Collections.singletonList(FillableBlock.FillableType.STONEANVIL)));
        List<Material> requirements6 = Arrays.asList(new Material(GRASS, 2), new Material(STONE, 3));
        recipes.put("armour-01", new Recipe("armour-01", requirements6, ARMOUR, null, null,20, Collections.singletonList(FillableBlock.FillableType.STONEANVIL)));
        List<Material> requirements13 = Arrays.asList(new Material(GRASS, 1), new Material(STONE, 1), new Material(Material.Type.WOOD, 2));
        recipes.put("spear", new Recipe("spear", requirements13, SPEAR, null, null,20, Collections.singletonList(FillableBlock.FillableType.STONEANVIL)));

        List<Material> requirements9 = Collections.singletonList(new Material(Material.Type.MEAT, 1));
        recipes.put("cookedmeat", new Recipe("cookedmeat", requirements9, null, null, COOKEDMEAT, 1, Collections.singletonList(FillableBlock.FillableType.CAMPFIRE)));
        List<Food> requirements14 = Collections.singletonList(new Food(BERRY, 3));
        recipes.put("berrypaste", new Recipe("berrypaste", requirements14, null, Material.Type.BERRYPASTE, null, 1, Collections.singletonList(FillableBlock.FillableType.BENCHHEALER)));
//        recipes.put("berrypaste", new Recipe("berrypaste", requirements11, null, Material.Type.BERRYPASTE, 20, new ArrayList<>(Collections.singletonList(FillableBlock.BlockType.BENCHHEALER))));
    }

    public Map<String, Recipe> getRecipes() {
        return recipes;
    }


    public List<Recipe> getHandRecipes() {
        List<Recipe> recipesToReturn = new ArrayList<>();
        for (Recipe recipe : recipes.values()) {
            if (recipe.getWorkbenchesRequired() == null) recipesToReturn.add(recipe);
        }
        return recipesToReturn;
//        return recipes.values().stream().filter(recipe -> recipe.getWorkbenchesRequired() == null).collect(Collectors.<Recipe>toList());
    }

    public List<Recipe> getRecipes(FillableBlock.FillableType fillableType) {
        List<Recipe> recipesToReturn = new ArrayList<>();
        for (Recipe recipe : recipes.values()) {
            if (recipe.getWorkbenchesRequired() != null && recipe.getWorkbenchesRequired().contains(fillableType)) recipesToReturn.add(recipe);
        }
        return recipesToReturn;
//        return recipes.values().stream().filter(recipe -> recipe.getWorkbenchesRequired() == null).collect(Collectors.<Recipe>toList());
    }
}
