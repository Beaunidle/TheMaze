package com.mygdx.game.controller;

import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.mygdx.game.model.Recipe;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.items.Material;

public class InventoryButton extends ImageButton {
    private boolean selected = false;
    private Recipe recipe;
    private Object item;

    public InventoryButton(Drawable up) {
        super(up);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Object getItem() {
        return item;
    }

    public void setItem(Object item) {
        this.item = item;
    }
}
