package com.mygdx.game.utils;

import static com.mygdx.game.model.items.Consumable.ConsumableType.*;
import static com.mygdx.game.model.items.Item.ItemType.*;
import static com.mygdx.game.model.items.Material.Type.*;
import static com.mygdx.game.model.items.Placeable.PlaceableType.BED;
import static com.mygdx.game.model.items.Placeable.PlaceableType.BENCHHEALER;
import static com.mygdx.game.model.items.Placeable.PlaceableType.CAMPFIRE;
import static com.mygdx.game.model.items.Placeable.PlaceableType.DOOR;
import static com.mygdx.game.model.items.Placeable.PlaceableType.HOUSE;
import static com.mygdx.game.model.items.Placeable.PlaceableType.STONEANVIL;
import static com.mygdx.game.model.items.Placeable.PlaceableType.TORCH;
import static com.mygdx.game.model.items.Placeable.PlaceableType.WALL;
import static com.mygdx.game.model.items.Swingable.SwingableType.AXE;
import static com.mygdx.game.model.items.Swingable.SwingableType.HAMMER;
import static com.mygdx.game.model.items.Swingable.SwingableType.HOE;
import static com.mygdx.game.model.items.Swingable.SwingableType.PICK;
import static com.mygdx.game.model.items.Swingable.SwingableType.SHOVEL;
import static com.mygdx.game.model.items.Swingable.SwingableType.SWORD;
import static com.mygdx.game.model.items.Throwable.ThrowableType.PEBBLE;
import static com.mygdx.game.model.items.Throwable.ThrowableType.SPEAR;

import com.mygdx.game.model.Recipe;
import com.mygdx.game.model.environment.blocks.FillableBlock;
import com.mygdx.game.model.items.Consumable;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.items.Material;
import com.mygdx.game.model.items.Placeable;
import com.mygdx.game.model.items.Swingable;
import com.mygdx.game.model.items.Throwable;

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
        //hand builds
        List<Material> requirements3 = Arrays.asList(new Material(GRASS, 3), new Material(WOOD, 2));
        recipes.put("wall", new Recipe("wall", requirements3, PLACEABLE, ITEM, null , null, WALL, null, 30, null));
        List<Material> requirementsHouse = Arrays.asList(new Material(GRASS, 3), new Material(WOOD, 2));
        recipes.put("house", new Recipe("house", requirementsHouse, PLACEABLE, ITEM, null , null, HOUSE, null, 30, null));
        List<Material> requirements5 = Arrays.asList(new Material(GRASS, 2), new Material(WOOD, 3));
        recipes.put("door", new Recipe("door", requirements5, PLACEABLE, ITEM, null, null, DOOR, null,20, null));
        List<Material> requirements8 = Arrays.asList(new Material(WOOD, 1), new Material(STONE, 4));
        recipes.put("fire", new Recipe("fire", requirements8, PLACEABLE, ITEM, null, null, CAMPFIRE, null, 20, null));
        List<Material> requirements10 = Arrays.asList(new Material(STONE, 2), new Material(WOOD, 4), new Material(BONEFRAGMENT, 2));
        recipes.put("bench-healer", new Recipe("bench-healer", requirements10, PLACEABLE, ITEM, null, null, BENCHHEALER, null, 20, null));
        List<Material> requirements11 = Collections.singletonList(new Material(STONE, 10));
        recipes.put("bench-stone", new Recipe("bench-stone", requirements11, PLACEABLE, ITEM, null, null,  STONEANVIL, null, 20, null));
        List<Throwable> requirements12 = Collections.singletonList(new Throwable(PEBBLE, 3));
        recipes.put("stone", new Recipe("stone", requirements12, null, STONE, null, null, null, null, 1, null));
        List<Material> requirementsHammer = Arrays.asList(new Material(STONE, 2), new Material(STICK, 1), new Material(GRASS, 3));
        recipes.put("inv_hammer_stone", new Recipe("inv_hammer_stone", requirementsHammer, SWINGABLE, STONE, HAMMER, null, null, null, 20, null));
        List<Material> requirementsBed = Arrays.asList(new Material(WOOD, 2), new Material(GRASS, 4));
        recipes.put("bed", new Recipe("bed", requirementsBed, PLACEABLE, ITEM, null, null, BED, null, 10, null));
        List<Material> requirementsTorch = Arrays.asList(new Material(WOOD, 2), new Material(GRASS, 1), new Material(FLINT, 2));
        recipes.put("torch", new Recipe("torch", requirementsTorch, PLACEABLE, ITEM, null, null, TORCH, null, 10, null));

        List<Material> requirements = Arrays.asList(new Material(FLINT, 4), new Material(WOOD, 1));
        recipes.put("inv_sword_flint", new Recipe("inv_sword_flint", requirements, SWINGABLE, FLINT, SWORD, null, null, null, 20, Collections.singletonList(FillableBlock.FillableType.STONEANVIL)));
        List<Material> requirementsAxe = Arrays.asList(new Material(FLINT, 2), new Material(STICK, 2), new Material(GRASS, 2));
        recipes.put("inv_axe_flint", new Recipe("inv_axe_flint", requirementsAxe, SWINGABLE, FLINT, AXE, null, null, null, 20, Collections.singletonList(FillableBlock.FillableType.STONEANVIL)));
        List<Material> requirements2 = Collections.singletonList(new Material(COPPER, 5));
        recipes.put("inv_sword_copper", new Recipe("inv_sword_copper", requirements2, SWINGABLE, COPPER, SWORD, null, null, null, 20, Collections.singletonList(FillableBlock.FillableType.STONEANVIL)));
        List<Material> requirementsShovel = Arrays.asList(new Material(SCAPULA, 1), new Material(WOOD, 2));
        recipes.put("inv_shovel_bone", new Recipe("inv_shovel_bone", requirementsShovel, SWINGABLE, BONE, SHOVEL, null, null, null, 20, Collections.singletonList(FillableBlock.FillableType.STONEANVIL)));
        List<Material> requirementsHoe = Arrays.asList(new Material(SCAPULA, 1), new Material(WOOD, 1));
        recipes.put("inv_hoe_bone", new Recipe("inv_hoe_bone", requirementsHoe, SWINGABLE, BONE, HOE, null, null, null, 20, Collections.singletonList(FillableBlock.FillableType.STONEANVIL)));
        List<Material> requirements7 = Arrays.asList(new Material(STICK, 2), new Material(FLINT, 3));
        recipes.put("inv_pick_flint", new Recipe("inv_pick_flint", requirements7, SWINGABLE, FLINT, PICK, null, null, null,20, Collections.singletonList(FillableBlock.FillableType.STONEANVIL)));
        List<Material> requirements4 = Arrays.asList(new Material(STONE, 2), new Material(WOOD, 4));
        recipes.put("inv_shield", new Recipe("inv_shield", requirements4, SHIELD, ITEM, null,null, null, null,20, Collections.singletonList(FillableBlock.FillableType.STONEANVIL)));
        List<Material> requirements6 = Arrays.asList(new Material(GRASS, 2), new Material(STONE, 3));
        recipes.put("armour-01", new Recipe("armour-01", requirements6, ARMOUR, ITEM, null, null, null, null,20, Collections.singletonList(FillableBlock.FillableType.STONEANVIL)));
        List<Material> requirements13 = Arrays.asList(new Material(GRASS, 1), new Material(STONE, 1), new Material(Material.Type.WOOD, 2));
        recipes.put("spear", new Recipe("spear", requirements13, THROWABLE, ITEM, null, null, null, SPEAR, 20, Collections.singletonList(FillableBlock.FillableType.STONEANVIL)));

        List<Material> requirements9 = Collections.singletonList(new Material(Material.Type.MEAT, 1));
        recipes.put("cookedmeat", new Recipe("cookedmeat", requirements9, null, CONSUMABLE, null, COOKEDMEAT, null, null, 1, Collections.singletonList(FillableBlock.FillableType.CAMPFIRE)));

        List<Consumable> requirements14 = Collections.singletonList(new Consumable(BERRY, 3));
        recipes.put("berrypaste", new Recipe("berrypaste", requirements14, null, CONSUMABLE, null, BERRYPASTE, null, null, 1, Collections.singletonList(FillableBlock.FillableType.BENCHHEALER)));
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
