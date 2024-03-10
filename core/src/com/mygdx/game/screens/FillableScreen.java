package com.mygdx.game.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.controller.InventoryButton;
import com.mygdx.game.model.Inventory;
import com.mygdx.game.model.World;
import com.mygdx.game.model.environment.blocks.EnvironmentBlock;
import com.mygdx.game.model.environment.blocks.FillableBlock;
import com.mygdx.game.model.items.Consumable;
import com.mygdx.game.model.items.Fillable;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.items.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FillableScreen extends InventoryScreen {

    private List<InventoryButton> inputButtons, outputButtons, inventoryButtons;
    private final FillableBlock fillableToShow;

    public FillableScreen(Game game, SpriteBatch spriteBatch, BitmapFont font, Screen gameScreen, World world, FillableBlock fillableToShow) {
        super(game, spriteBatch, font, gameScreen, world);
        this.fillableToShow = fillableToShow;
    }

    @Override
    public void show(){
        Gdx.input.setCursorCatched(false);
        Gdx.input.setInputProcessor(getStage());
        getStage().getViewport().update(Gdx.app.getGraphics().getWidth(), Gdx.app.getGraphics().getHeight(),true);

        Table mainTable = new Table();
        Table topTable = new Table();
        Table topTableLeft = new Table();
        Table topTableRight = new Table();
        Table bottomTable = new Table();
        Table centreTable = new Table();

        //Set table to fill stage
        mainTable.setFillParent(true);
        mainTable.center();

        inventoryButtons = createInventoryButtons(true);
        inputButtons = createFillableButtons(fillableToShow.getInput());
        outputButtons = createFillableButtons(fillableToShow.getOutput());
        List<Button> bottomButtons = createOtherButtons();

        addButtonToTable(centreTable, inventoryButtons, 8);
        addButtonToTable(topTableLeft, inputButtons, 3);
        addButtonToTable(topTableRight, outputButtons, 3);
        for (Button button : bottomButtons) {
            bottomTable.add(button).width(70).height(70);
        }

        topTable.add(topTableLeft).center().padBottom(40).padRight(30);
        topTable.add(topTableRight).center().padBottom(40).padLeft(30);
        mainTable.add(topTable);
        mainTable.row().center();
        mainTable.add(centreTable).padBottom(20);
        mainTable.row().center();
        mainTable.add(bottomTable);
        mainTable.setColor(Color.RED);

        getStage().addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, .25f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        getStage().getViewport().update(Gdx.app.getGraphics().getWidth(), Gdx.app.getGraphics().getHeight(),true);
        getStage().act();
        getStage().draw();
        resetButtons(inputButtons, fillableToShow.getInput());
        resetButtons(outputButtons, fillableToShow.getOutput());
        resetButtons(inventoryButtons, getInventory());

        getSpriteBatch().begin();
        getFont().getLineHeight();
        getFont().setColor(Color.BLACK);

        if (Gdx.app.getType().equals(Application.ApplicationType.Android)) getFont().getData().setScale(4);
        drawButtonNumbers(inputButtons);
        drawButtonNumbers(outputButtons);
        drawButtonNumbers(inventoryButtons);

        if (fillableToShow.isActive()) getFont().draw(getSpriteBatch(), "Burning", Gdx.graphics.getWidth() * .05f, Gdx.graphics.getHeight() * .75f);
//        getFont().draw(getSpriteBatch(), "Run around and don't die!!!", Gdx.graphics.getWidth() * .35f, Gdx.graphics.getHeight() * .65f);
//        getFont().draw(getSpriteBatch(), "Click start to play", Gdx.graphics.getWidth() * .35f, Gdx.graphics.getHeight() * .55f);
        getSpriteBatch().end();
    }

    public void useItem(InventoryButton selectedButton) {
        if (selectedButton == null) {
            fillableToShow.toggleActive();
            return;
        }

        if (inventoryButtons.contains(selectedButton)) {
//            System.out.println("here inv");
            if (selectedButton.getItem() instanceof Material) {

                Material material = (Material) selectedButton.getItem();
                boolean campfire = fillableToShow.getFillableType().equals(FillableBlock.FillableType.CAMPFIRE);
                if (!campfire || (material.getType().equals(Material.Type.WOOD) || material.getType().equals(Material.Type.MEAT))) {
                    int filled = fillableToShow.getInput().addInventory(material);
                    if (filled > 0) {
                        getInventory().removeMaterial(material);
                        fillButton(selectedButton, null);
                    }
                }
            }
        }
        if (inputButtons.contains(selectedButton)) {
//            System.out.println("here input");
            if (selectedButton.getItem() instanceof Material) {
                Material material = (Material) selectedButton.getItem();
//                if (material.getType().equals(Material.Type.WOOD) || material.getType().equals(Material.Type.MEAT)) {
                    int filled = getInventory().addInventory(material);
                    if (filled > 0) {
                        fillableToShow.getInput().removeMaterial(material);
                        fillButton(selectedButton, null);
                    }
//                }
            }
        }
        if (outputButtons.contains(selectedButton)) {
//            System.out.println("here here output");
            if (selectedButton.getItem() instanceof Material) {
                Material material = (Material) selectedButton.getItem();
                int filled = getInventory().addInventory(material instanceof Consumable ? new Consumable(((Consumable) material).getConsumableType(), material.getQuantity()): new Material(material));
                if (filled > 0) {
                    fillableToShow.getOutput().removeMaterial(material);
                    fillButton(selectedButton, null);
                }
            }
        }
    }
}
