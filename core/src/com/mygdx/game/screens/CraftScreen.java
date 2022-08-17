package com.mygdx.game.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.controller.InventoryButton;
import com.mygdx.game.controller.WorldController;
import com.mygdx.game.model.Inventory;
import com.mygdx.game.model.Recipe;
import com.mygdx.game.model.World;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.items.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftScreen extends InventoryScreen  {

    private final int width = Gdx.app.getGraphics().getWidth();
    private final int height = Gdx.app.getGraphics().getHeight();
    private final Screen gameScreen;
    private final List<Recipe> recipes;
    private List<InventoryButton> inputButtons, outputButtons, inventoryButtons;
    private InventoryButton selected;

    public CraftScreen(Game game, SpriteBatch spriteBatch, BitmapFont font, Screen gameScreen, World world, List<Recipe> recipes) {
        super(game, spriteBatch, font, gameScreen, world);
        this.recipes = recipes;

        OrthographicCamera camera = new OrthographicCamera();
        Viewport viewport = new FitViewport(width, height, camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        this.gameScreen = gameScreen;
    }

    @Override
    public void show(){
        Gdx.input.setCursorCatched(false);
        Gdx.input.setInputProcessor(getStage());
        getStage().getViewport().update(Gdx.app.getGraphics().getWidth(), Gdx.app.getGraphics().getHeight(),true);

        Table mainTable = new Table();
        Table topTable = new Table();
        Table bottomTable = new Table();
        Table centreTable = new Table();

        //Set table to fill stage
        mainTable.setFillParent(true);
        inventoryButtons = createInventoryButtons(false);
        inputButtons = createCraftingButtons();
        List<Button> bottomButtons = createOtherButtons();

        addButtonToTable(topTable, inputButtons, 10);
        addButtonToTable(centreTable, inventoryButtons, 10);
        for (Button button : bottomButtons) {
            bottomTable.add(button).width(70).height(70);
        }

        mainTable.center();
        mainTable.add(topTable).top().padBottom(35);
        mainTable.row();
        mainTable.add(centreTable).padBottom(35);
        mainTable.row();
        mainTable.add(bottomTable).bottom();

        getStage().addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, .25f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        getStage().getViewport().update(Gdx.app.getGraphics().getWidth(), Gdx.app.getGraphics().getHeight(),true);
        getStage().act();
        getStage().draw();

        resetButtons(inventoryButtons, getInventory());

        getSpriteBatch().begin();
        getFont().getLineHeight();
        getFont().setColor(Color.BLACK);

        if (Gdx.app.getType().equals(Application.ApplicationType.Android)) getFont().getData().setScale(4);

        drawButtonNumbers(inventoryButtons);
        for (InventoryButton button : inputButtons) {
            int count = 1;
            for (Material m : button.getRecipe().getRequirements()) {
                Vector2 pos = button.localToAscendantCoordinates(button.getParent(), new Vector2(button.getParent().getX(), button.getParent().getY()));
                getSpriteBatch().draw(atlas.findRegion(m.getName()), pos.x + count*15, pos.y + 10, 10, 10);
                getFont().draw(getSpriteBatch(), String.valueOf(m.getQuantity()), pos.x + count*15, pos.y);
                count++;
            }

        }

//        getFont().draw(getSpriteBatch(), "CRAFTING SCREEN!", Gdx.graphics.getWidth() * .35f, Gdx.graphics.getHeight() * .95f);
//        font.draw(spriteBatch, "Run around and don't die!!!", Gdx.graphics.getWidth() * .35f, Gdx.graphics.getHeight() * .65f);
//        font.draw(spriteBatch, "Click start to play", Gdx.graphics.getWidth() * .35f, Gdx.graphics.getHeight() * .55f);
        getSpriteBatch().end();
    }

    private List<InventoryButton> createCraftingButtons() {
        //Create buttons
        final List<InventoryButton> buttons = new ArrayList<>();
        for (Recipe recipe : recipes) {

            final InventoryButton button = new InventoryButton(new TextureRegionDrawable(buttonsAtlas.findRegion("inventoryBox")));
            setButtonBackground(button, new TextureRegionDrawable(buttonsAtlas.findRegion("inventoryBox")));

            if (recipe != null) {
//                button.setItem(material);
                if (inventoryAtlas.findRegion(recipe.getName()) != null) {
                    setButtonImage(button, recipe.getName());
                } else {
                    setButtonImage(button, recipe.getName());
                }
                button.setRecipe(recipe);
            }
            button.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selected(button, buttons);
                }
            });
            buttons.add(button);
        }
        return buttons;
    }

    public void selected(InventoryButton button, List<InventoryButton> buttons) {
        if (button.equals(getSelected())) {
            button.setSelected(false);
            setSelected(null);
            button.setChecked(false);
            setButtonBackground(button, new TextureRegionDrawable(new TextureRegion(buttonsAtlas.findRegion("inventoryBox"))));
        } else {
            if (getSelected() != null) {
                getSelected().setSelected(false);
                setButtonBackground(getSelected(), new TextureRegionDrawable(new TextureRegion(buttonsAtlas.findRegion("inventoryBox"))));
                getSelected().setChecked(false);
            }
            button.setSelected(true);
            setButtonBackground(button, new TextureRegionDrawable(new TextureRegion(atlas.findRegion("explodingBlockYellow"))));
            setSelected(button);
        }
    }

    public InventoryButton getSelected() {
        return selected;
    }

    public void setSelected(InventoryButton selected) {
        this.selected = selected;
    }

    public List<Button> createOtherButtons() {

        List<Button> buttons = new ArrayList<>();
        Button startButton = new Button(new TextureRegionDrawable(buttonsAtlas.findRegion("game")));
        Button exitButton = new Button(new TextureRegionDrawable(buttonsAtlas.findRegion("inventory")));
        Button craftButton = new Button(new TextureRegionDrawable(buttonsAtlas.findRegion("craft")));

        startButton.setWidth(width/10f);
        startButton.setHeight(height/5f);
        startButton.setX(width * 0.25F);
        startButton.setY(height * 0.25F);

        exitButton.setWidth(width/10f);
        exitButton.setHeight(height/5f);
        exitButton.setX(width * 0.65F);
        exitButton.setY(height * 0.25F);

        craftButton.setWidth(width/10f);
        craftButton.setHeight(height/5f);
        craftButton.setX(Gdx.graphics.getWidth() * .35f);
        craftButton.setY(Gdx.graphics.getHeight() * .95f);


        startButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //todo switch back to game screen needs to be smooth
                getGame().setScreen(gameScreen);
                ((GameScreen) gameScreen).getController().startUnpauseTimer(5);
            }
        });
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().setScreen(new InventoryScreen(getGame(), getSpriteBatch(), getFont(), gameScreen, getWorld()));
            }
        });
        craftButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selected != null) {
                    craft(selected.getRecipe());
                }
                else {
                    System.out.println("No recipe selected");
                }
            }
        });

        buttons.add(startButton);
        buttons.add(exitButton);
        buttons.add(craftButton);
        return buttons;
    }

    public void craft(Recipe recipe) {
        System.out.println("Crafting");
        List<Material> removedMaterials = new ArrayList<>();
        for (Material material : recipe.getRequirements()) {
            if (!getInventory().checkInventory(material)) {
                System.out.println("Not enough " + material.getName());
                return;
            }
        }
        for (Material material : recipe.getRequirements()) {
            if (getInventory().removeMaterial(material)) {
                removedMaterials.add(material);
            } else {
                for (Material materialToReAdd : removedMaterials) {
                    getInventory().addInventory(materialToReAdd);
                }
                System.out.println("Something went wrong, " + recipe.getName() + " not added");
                return;
            }
        }
        if (recipe.getType() != null) {
            getInventory().addInventory(new Item(recipe.getType(), recipe.getBaseDurability()));
        }
        if (recipe.getMaterialType() != null) {
            getInventory().addInventory(new Material(recipe.getMaterialType(), recipe.getBaseDurability()));
        }
        System.out.println(recipe.getName() + " added to inventory");
    }

    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }

    public boolean keyDown (int keycode) {
        System.out.println(keycode);
        if (keycode == Input.Keys.C)
            if(getSelected() != null)
            System.out.println(("Crafting" + getSelected().getRecipe().getName()));
        return true;
    }

    public boolean keyUp (int keycode) {
        return true;
    }

    public boolean keyTyped (char character) {
         return true;
    }

    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        return true;
    }

    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        return true;
    }

    public boolean touchDragged (int screenX, int screenY, int pointer) {
        return true;
    }

    public boolean mouseMoved (int screenX, int screenY) {
        return true;
    }

    public boolean scrolled (int amount) {
        return true;
    }
}
