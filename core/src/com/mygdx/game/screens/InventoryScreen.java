package com.mygdx.game.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.controller.InventoryButton;
import com.mygdx.game.controller.WorldController;
import com.mygdx.game.model.Inventory;
import com.mygdx.game.model.Recipe;
import com.mygdx.game.model.World;
import com.mygdx.game.model.items.Fillable;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.items.Magic;
import com.mygdx.game.model.items.Material;
import com.mygdx.game.model.moveable.Projectile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryScreen extends ScreenAdapter implements InputProcessor {

    private final Game game;
    private final SpriteBatch spriteBatch;
    private final BitmapFont font;
    private final Stage stage;
    private final int width = Gdx.app.getGraphics().getWidth();
    private final int height = Gdx.app.getGraphics().getHeight();
    private final Screen gameScreen;
    private World world;
    private WorldController controller;
    private final Inventory inventory;
    private final Inventory toolBelt;
    private InventoryButton selectedButton;
    private List<InventoryButton> topButtons;
    private List<Button> bottomButtons;
    private Map<String,InventoryButton> leftButtons;
    private List<InventoryButton> magicButtons;
    private List<InventoryButton> toolBeltButtons;

    TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("items.atlas"));
    TextureAtlas inventoryAtlas = new TextureAtlas(Gdx.files.internal("inventory.atlas"));
    TextureAtlas buttonsAtlas = new TextureAtlas(Gdx.files.internal("buttons.atlas"));


    public InventoryScreen(Game game, SpriteBatch spriteBatch, BitmapFont font, Screen gameScreen, World world) {
        this.game = game;
        this.spriteBatch = spriteBatch;
        this.font = font;
        this.world = world;
        this.inventory = world.getBob().getInventory();
        this.toolBelt = world.getBob().getToolBelt();

        OrthographicCamera camera = new OrthographicCamera();
        Viewport viewport = new FitViewport(Gdx.app.getGraphics().getWidth(), Gdx.app.getGraphics().getHeight(), camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        stage = new Stage(viewport, spriteBatch);
        this.gameScreen = gameScreen;
    }

    @Override
    public void show(){
        Gdx.input.setCursorCatched(false);
        Gdx.input.setInputProcessor(stage);
        stage.getViewport().update(Gdx.app.getGraphics().getWidth(), Gdx.app.getGraphics().getHeight(),true);

        Table mainTable = new Table();
        Table topTableLeft = new Table();
        Table topTableRight = new Table();
        Table topTableRightTop = new Table();
        Table topTableRightBottom = new Table();
        Table middleTable = new Table();
        Table bottomTable = new Table();

        //Set table to fill stage
        mainTable.setFillParent(true);
        topButtons = createInventoryButtons(true);
        bottomButtons = createOtherButtons();
        leftButtons = createLeftButtons(true);
        magicButtons = createRightButtons(true);
        toolBeltButtons = createToolBeltButtons(true);

        addButtonToTable(topTableRightTop, topButtons, 5);
        addButtonToTable(topTableRightBottom, magicButtons, 5);
        addButtonToTable(middleTable, toolBeltButtons, 9);

        topTableRight.add(topTableRightTop).padBottom(10);
        topTableRight.row();
        topTableRight.add(topTableRightBottom);

        topTableLeft.add(leftButtons.get("head")).width(60).height(60);
        topTableLeft.add(leftButtons.get("strong")).width(60).height(60);
        topTableLeft.row();
        topTableLeft.add(leftButtons.get("torso")).width(60).height(60);
        topTableLeft.add(leftButtons.get("weak")).width(60).height(60);
        topTableLeft.row();
        topTableLeft.add(leftButtons.get("feet")).width(60).height(60);

        for (Button button : bottomButtons) {
            bottomTable.add(button).width(70).height(70);
        }

        mainTable.center();

        Pixmap bgPixmap = new Pixmap(1,1, Pixmap.Format.RGB565);
        bgPixmap.setColor(Color.RED);
        bgPixmap.fill();
        TextureRegionDrawable textureRegionDrawableBg = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));
        topTableRight.setBackground(textureRegionDrawableBg);

        bgPixmap.setColor(Color.BLUE);
        bgPixmap.fill();
        textureRegionDrawableBg = new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap)));
        topTableLeft.setBackground(textureRegionDrawableBg);
        mainTable.add(topTableLeft).padLeft(50);
        mainTable.add(topTableRight);
        mainTable.row();
        mainTable.add();
        mainTable.add(middleTable).padTop(10).padBottom(10);
        mainTable.add();
        mainTable.row();
        mainTable.add();
        mainTable.add(bottomTable);
        mainTable.add();


        bgPixmap.dispose();
        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, .25f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.getViewport().update(Gdx.app.getGraphics().getWidth(), Gdx.app.getGraphics().getHeight(),true);
        stage.act();
        stage.draw();
        resetAllButtons();

        spriteBatch.begin();
        font.getLineHeight();

        font.setColor(Color.BLACK);
        if (Gdx.app.getType().equals(Application.ApplicationType.Android)) font.getData().setScale(4);
//        System.out.println("Printing buttons");
        drawTopButtonNumbers(topButtons);
        drawButtonNumbers(toolBeltButtons);
//        drawButtonNumbers(Collections.singletonList((InventoryButton) leftButtons.get("strong")));

        InventoryButton buttonToNumber = leftButtons.get("strong");
        if (buttonToNumber.getItem() instanceof Material && ((Material) buttonToNumber.getItem()).getMaxPerStack() > 1) {
            int quantity = ((Material) buttonToNumber.getItem()).getQuantity();
            if (quantity > 1) {
                Vector2 pos = buttonToNumber.localToAscendantCoordinates(buttonToNumber.getParent(), new Vector2(buttonToNumber.getParent().getX(), buttonToNumber.getParent().getY()));
                getFont().draw(getSpriteBatch(), String.valueOf(quantity), pos.x+45, pos.y+20);
            }
        }
//        font.draw(spriteBatch, "INVENTORY SCREEN!", Gdx.graphics.getWidth() * .05f, Gdx.graphics.getHeight() * .75f);
//        font.draw(spriteBatch, "Run around and don't die!!!", Gdx.graphics.getWidth() * .35f, Gdx.graphics.getHeight() * .65f);
//        font.draw(spriteBatch, "Click start to play", Gdx.graphics.getWidth() * .35f, Gdx.graphics.getHeight() * .55f);

        spriteBatch.end();
    }

    protected List<InventoryButton> createInventoryButtons(boolean selectable) {
        //Create buttons

        final List<InventoryButton> buttons = new ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) {

            final InventoryButton button = new InventoryButton(null);
            setButtonBackground(button, new TextureRegionDrawable(buttonsAtlas.findRegion("inventoryBox")));

            Object inventoryObject = inventory.getSlots().get(i);
            if (inventoryObject != null) {
                if (inventoryObject instanceof Item) {
                    Item item = (Item)inventoryObject;
                    button.setItem(item);
                    if (item instanceof Fillable && ((Fillable) item).isFilled()) {
                        setButtonImage(button, "inv_full", selectable);
                    } else {
                        setButtonImage(button, item.getName(), selectable);
                    }
                }
                if (inventoryObject instanceof Material && !(inventoryObject instanceof Item) ) {
                    Material material = (Material)inventoryObject;
                    button.setItem(material);
                    setButtonImage(button, material.getName(), selectable);
                }
            }
            if (selectable) {
                button.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        selected(button);
                    }
                });
            }
            buttons.add(button);
        }
        return buttons;
    }

    protected List<InventoryButton> createToolBeltButtons(boolean selectable) {
        final List<InventoryButton> buttons = new ArrayList<>();
        for (int i = 0; i < toolBelt.getSize(); i++) {

            final InventoryButton button = new InventoryButton(null);
            setButtonBackground(button, new TextureRegionDrawable(buttonsAtlas.findRegion("inventoryBox")));

            Object inventoryObject = toolBelt.getSlots().get(i);
            if (inventoryObject != null) {
                if (inventoryObject instanceof Item) {
                    Item item = (Item)inventoryObject;
                    button.setItem(item);
                    if (item instanceof Fillable && ((Fillable) item).isFilled()) {
                        setButtonImage(button, "inv_full", selectable);
                    } else {
                        setButtonImage(button, item.getName(), selectable);
                    }
                }
                if (inventoryObject instanceof Material && !(inventoryObject instanceof Item) ) {
                    Material material = (Material)inventoryObject;
                    button.setItem(material);
                    setButtonImage(button, material.getName(), selectable);
                }
            }
            if (selectable) {
                button.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        selected(button);
                    }
                });
            }
            buttons.add(button);
        }
        return buttons;
    }

    protected List<InventoryButton> createRightButtons(boolean selectable) {
        //Create buttons

        final List<InventoryButton> buttons = new ArrayList<>();
        for (int i = 0; i < world.getBob().getSpells().size(); i++) {

            final InventoryButton button = new InventoryButton(null);
            setButtonBackground(button, new TextureRegionDrawable(buttonsAtlas.findRegion("inventoryBox")));

            Magic inventoryObject = world.getBob().getSpells().get(i);
            if (inventoryObject != null) {
                button.setItem(inventoryObject);
                if (inventoryObject.getProjectileType() != null && inventoryObject.getProjectileType().equals(Projectile.ProjectileType.FIREBALL)) {
                    setButtonImage(button, "fireball-01", selectable);
                } else if (inventoryObject.getElement() != null && inventoryObject.getElement().equals(Magic.Element.ELECTRIC)) {
                    setButtonImage(button, "lightning-01", selectable);
                } else {
                    setButtonImage(button, inventoryObject.getName(), selectable);
                }
            }
            if (selectable) {
                button.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        selected(button);
                    }
                });
            }
            buttons.add(button);
        }
        return buttons;
    }


    public void selected(InventoryButton button) {
        if (button.equals(getSelectedButton())) {
            button.setSelected(false);
            setSelectedButton(null);
            button.setChecked(false);
            setButtonBackground(button, new TextureRegionDrawable(new TextureRegion(buttonsAtlas.findRegion("inventoryBox"))));
        } else {
            if (getSelectedButton() != null) {
                getSelectedButton().setSelected(false);
                setButtonBackground(getSelectedButton(), new TextureRegionDrawable(new TextureRegion(buttonsAtlas.findRegion("inventoryBox"))));
                getSelectedButton().setChecked(false);
            }
            button.setSelected(true);
            setButtonBackground(button, new TextureRegionDrawable(new TextureRegion(atlas.findRegion("explodingBlockYellow"))));
            setSelectedButton(button);
        }
    }

    void setButtonBackground(InventoryButton button, TextureRegionDrawable drawable) {
        Button.ButtonStyle style = button.getStyle();
        style.up = drawable;
        style.down = drawable;
        style.checked = drawable;
    }

    void setButtonImage(InventoryButton button, String itemName) {
        setButtonImage(button, itemName, true);
    }

    void setButtonImage(InventoryButton button, String itemName, boolean selectable) {
        TextureRegion drawable;
        if (itemName == null) {
            drawable = null;
            ImageButton.ImageButtonStyle style = button.getStyle();
            style.imageUp      = null;
            style.imageDown    = null;
            style.imageChecked = null;
            return;
        } else {
            drawable = (inventoryAtlas.findRegion(itemName));
            if (drawable == null) drawable = atlas.findRegion(itemName);
            if (itemName.equals("fireball-01") || itemName.equals("fireball")) drawable = atlas.findRegion("fireball-01");
            if (itemName.equals("lightning-01") || itemName.equals("lightning")) drawable = atlas.findRegion("lightning-01");

        }
        TextureRegionDrawable drawableRegion = new TextureRegionDrawable(drawable);
        ImageButton.ImageButtonStyle style = button.getStyle();
        style.imageUp      = drawableRegion;
        style.imageDown    = drawableRegion;
        style.imageChecked = drawableRegion;
        button.getImage().setScale(0.6F, 0.6F);
        if (selectable) {
            style.unpressedOffsetY = 13; // to "not" center the icon
            style.unpressedOffsetX = 20; // to "not" center the icon
        }
    }

    public List<Button> createOtherButtons() {

        List<Button> buttons = new ArrayList<>();
        Button gameButton = new Button(new TextureRegionDrawable(buttonsAtlas.findRegion("game")));
        Button craftButton = new Button(new TextureRegionDrawable(buttonsAtlas.findRegion("craft")));
        Button useButton = new Button(new TextureRegionDrawable(buttonsAtlas.findRegion("use")));
        Button toolBeltButton = new Button(new TextureRegionDrawable(buttonsAtlas.findRegion("toolbelt")));
        Button emptyHandButton = new Button(new TextureRegionDrawable(atlas.findRegion("wood")));

        setButtonDimensions(gameButton);
        setButtonDimensions(craftButton);
        setButtonDimensions(useButton);
        setButtonDimensions(toolBeltButton);
        setButtonDimensions(emptyHandButton);

        gameButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //todo switch back to game screen needs to be smooth
                game.setScreen(gameScreen);
                ((GameScreen) gameScreen).getController().setFillableToShow(null);
                ((GameScreen) gameScreen).getController().startUnpauseTimer(5);
            }
        });
        craftButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new CraftScreen(game, spriteBatch, font, gameScreen, world, world.getBob().getRecipes()));
            }
        });
        useButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                useItem(selectedButton);
            }
        });
        toolBeltButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                switchToolBelt(selectedButton);
            }
        });
        emptyHandButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                useItem(null);
            }
        });

        buttons.add(gameButton);
        buttons.add(craftButton);
        buttons.add(useButton);
        buttons.add(toolBeltButton);
        buttons.add(emptyHandButton);
        return buttons;
    }

    public void setButtonDimensions(Button button) {
        button.setWidth(width/12f);
        button.setHeight(height/7f);
        button.setX(width * 0.25F);
        button.setY(height * 0.25F);
    }

    public Map<String,InventoryButton> createLeftButtons(boolean selectable) {
        Map<String,InventoryButton> buttons = new HashMap<>();
        //add inventory graphics
        final InventoryButton headButton = new InventoryButton(new TextureRegionDrawable(buttonsAtlas.findRegion("inventoryBox")));
        final InventoryButton torsoButton = new InventoryButton(new TextureRegionDrawable(buttonsAtlas.findRegion("inventoryBox")));
        final InventoryButton feetButton = new InventoryButton(new TextureRegionDrawable(buttonsAtlas.findRegion("inventoryBox")));
        final InventoryButton strongHandButton = new InventoryButton(new TextureRegionDrawable(buttonsAtlas.findRegion("inventoryBox")));
        final InventoryButton weakHandButton = new InventoryButton(new TextureRegionDrawable(buttonsAtlas.findRegion("inventoryBox")));
        setButtonBackground(headButton, new TextureRegionDrawable(buttonsAtlas.findRegion("inventoryBox")));
        setButtonBackground(torsoButton, new TextureRegionDrawable(buttonsAtlas.findRegion("inventoryBox")));
        setButtonBackground(feetButton, new TextureRegionDrawable(buttonsAtlas.findRegion("inventoryBox")));
        setButtonBackground(strongHandButton, new TextureRegionDrawable(buttonsAtlas.findRegion("inventoryBox")));
        setButtonBackground(weakHandButton, new TextureRegionDrawable(buttonsAtlas.findRegion("inventoryBox")));

        if (world.getBob().getTorso() != null) {
            setButtonImage(torsoButton, world.getBob().getTorso().getName(), selectable);
            torsoButton.setItem(world.getBob().getTorso());
        }
        if (world.getBob().getStrongHand() != null) {
            setButtonImage(strongHandButton, world.getBob().getStrongHand().getName(), selectable);
            strongHandButton.setItem(world.getBob().getStrongHand());
        }
        if (world.getBob().getWeakHand() != null) {
            setButtonImage(weakHandButton, world.getBob().getWeakHand().getName(), selectable);
            weakHandButton.setItem(world.getBob().getWeakHand());
        }
        //todo feet and head
//        if (world.getBob().getFeet() != null) {
//            setButtonImage(feetHandButton, world.getBob().getFeet().getName(), selectable);
//        }
//        if (world.getBob().getHead() != null) {
//            setButtonImage(headButton, world.getBob().getHead().getName(), selectable);
//        }
        headButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selected(headButton);
            }
        });
        torsoButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selected(torsoButton);
            }
        });
        feetButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selected(feetButton);
            }
        });
        strongHandButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selected(strongHandButton);
            }
        });
        weakHandButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selected(weakHandButton);
            }
        });

        setButtonDimensions(headButton);
        setButtonDimensions(torsoButton);
        setButtonDimensions(feetButton);
        setButtonDimensions(strongHandButton);
        setButtonDimensions(weakHandButton);


        buttons.put("head",headButton);
        buttons.put("torso", torsoButton);
        buttons.put("feet", feetButton);
        buttons.put("strong", strongHandButton);
        buttons.put("weak", weakHandButton);
        return buttons;
    }

    public void useItem(InventoryButton selectedButton) {
        if (selectedButton == null) {
            world.getBob().setStrongHand(null);
            world.getBob().setWeakHand(null);
            world.getBob().setTorso(null);
            leftButtons.get("torso").setItem(null);
            setButtonImage(leftButtons.get("torso"), null);
            leftButtons.get("strong").setItem(null);
            setButtonImage(leftButtons.get("strong"), null);
            leftButtons.get("weak").setItem(null);
            setButtonImage(leftButtons.get("weak"), null);
            return;
        }
        if (leftButtons.containsValue(selectedButton)) {
            //todo sort this out
            System.out.println("Here");
            Material selectedMaterial = (Material) selectedButton.getItem();
            if (selectedMaterial instanceof Magic || inventory.addInventory(selectedMaterial) != 0) {
                if (leftButtons.get("strong").equals(selectedButton)) world.getBob().setStrongHand(null);
                if (leftButtons.get("weak").equals(selectedButton)) world.getBob().setWeakHand(null);
                if (leftButtons.get("strong").equals(selectedButton)) world.getBob().setTorso(null);

                clearSelectedButton(selectedButton);
            }
        }

        if (topButtons.contains(selectedButton)) {
            if (selectedButton.getItem() instanceof Material) {
                Material selectedMaterial = (Material) selectedButton.getItem();
                Material handMaterial = null;
                if (selectedMaterial instanceof Item && (((Item) selectedMaterial).getItemType().equals(Item.ItemType.SHIELD) || ((Item) selectedMaterial).getItemType().equals(Item.ItemType.ARMOUR))) {
                    Item selectedItem = (Item) selectedMaterial;
                    int invQuantity = selectedItem.getQuantity();
                    if (selectedItem.getItemType().equals(Item.ItemType.SHIELD)) {
                        handMaterial = setBodyPart("weak", selectedButton);
                        world.getBob().setWeakHand((Item)selectedButton.getItem());
                    } else if (selectedItem.getItemType().equals(Item.ItemType.ARMOUR)) {
                        handMaterial = setBodyPart("torso", selectedButton);
                        world.getBob().setTorso((Item)selectedButton.getItem());
                    }
                    inventory.removeInventory(new Item(selectedItem));
                    selectedItem.setQuantity(invQuantity);
                    clearSelectedButton(selectedButton);
                    if (handMaterial != null && !(handMaterial instanceof Magic)) inventory.addInventory(handMaterial);
                } else {
                    if (selectedMaterial.isHoldable()) {
                        handMaterial = setBodyPart("strong", selectedButton);
                        int invQuantity = selectedMaterial.getQuantity();
                        world.getBob().setStrongHand(selectedMaterial);
                        inventory.removeInventory(new Material(selectedMaterial));
                        selectedMaterial.setQuantity(invQuantity);
                        clearSelectedButton(selectedButton);
                        if (handMaterial != null && !(handMaterial instanceof Magic)) inventory.addInventory(handMaterial);
                    }
                }
            }
        }

        if (toolBeltButtons.contains(selectedButton)) {
            if (selectedButton.getItem() instanceof Material) {
                Material selectedMaterial = (Material) selectedButton.getItem();
                Material handMaterial = null;
                if (selectedMaterial instanceof Item && (((Item) selectedMaterial).getItemType().equals(Item.ItemType.SHIELD) || ((Item) selectedMaterial).getItemType().equals(Item.ItemType.ARMOUR))) {
                    Item selectedItem = (Item) selectedMaterial;
                    int invQuantity = selectedItem.getQuantity();
                    if (selectedItem.getItemType().equals(Item.ItemType.SHIELD)) {
                        handMaterial = setBodyPart("weak", selectedButton);
                        world.getBob().setWeakHand((Item)selectedButton.getItem());
                    } else if (selectedItem.getItemType().equals(Item.ItemType.ARMOUR)) {
                        handMaterial = setBodyPart("torso", selectedButton);
                        world.getBob().setTorso((Item)selectedButton.getItem());
                    }
                    toolBelt.removeInventory(new Item(selectedItem));
                    selectedItem.setQuantity(invQuantity);
                    clearSelectedButton(selectedButton);
                    if (handMaterial != null && !(handMaterial instanceof Magic)) toolBelt.addInventory(handMaterial);
                } else {
                    selectedMaterial = (Material) selectedButton.getItem();
                    if (selectedMaterial.isHoldable()) {
                        int invQuantity = selectedMaterial.getQuantity();
                        handMaterial = setBodyPart("strong", selectedButton);
                        world.getBob().setStrongHand(selectedMaterial);
                        toolBelt.removeInventory(new Material(selectedMaterial));
                        selectedMaterial.setQuantity(invQuantity);
                        clearSelectedButton(selectedButton);
                        if (handMaterial != null && !(handMaterial instanceof Magic)) toolBelt.addInventory(handMaterial);
                    }
                }
            }
        }

        if (magicButtons.contains(selectedButton)) {
            Material selectedMaterial = (Material) selectedButton.getItem();
            Material handMaterial = null;
            if (selectedMaterial.isHoldable()) {
                handMaterial = setBodyPart("strong", selectedButton);
                world.getBob().setStrongHand(selectedMaterial);
//                clearSelectedButton(selectedButton);
                if (handMaterial != null && !(handMaterial instanceof Magic)) inventory.addInventory(handMaterial);
            }
        }
        resetAllButtons();
    }

    public void switchItems(Inventory switchingInventory) {

    }

    public Material setBodyPart(String bodyPart, InventoryButton button) {
        Material handMaterial = (Material) leftButtons.get(bodyPart).getItem();
        leftButtons.get(bodyPart).setItem(button.getItem());
        setButtonImage(leftButtons.get(bodyPart), ((Material) button.getItem()).getName());
        return handMaterial;
    }

    public void clearSelectedButton(InventoryButton button) {
        button.setItem(null);
        setButtonImage(button, null);
        setButtonBackground(button, new TextureRegionDrawable(new TextureRegionDrawable(buttonsAtlas.findRegion("inventoryBox"))));
    }

    public void switchToolBelt(InventoryButton button) {
        if (button == null) {
            return;
        }

        if (leftButtons.containsValue(button)) {
            //todo sort this out
            Material selectedMaterial = (Material) button.getItem();
            if (toolBelt.addInventory(selectedMaterial) != 0) {
                if (leftButtons.get("strong").equals(button)) world.getBob().setStrongHand(null);
                if (leftButtons.get("weak").equals(button)) world.getBob().setWeakHand(null);
                if (leftButtons.get("strong").equals(button)) world.getBob().setTorso(null);
                clearSelectedButton(button);
            }
        }

        if (toolBeltButtons.contains(button)) {
            if (button.getItem() instanceof Material) {
                Material selectedMaterial = (Material) button.getItem();
                int invQuantity = selectedMaterial.getQuantity();
                if (selectedMaterial.isHoldable()) {
                    if (selectedMaterial instanceof Magic || inventory.addInventory(selectedMaterial) != 0) {
                        toolBelt.removeInventory(selectedMaterial);
                        selectedMaterial.setQuantity(invQuantity);
                        clearSelectedButton(button);
                    }
                    else {
                        System.out.println("Inventory full");
                        return;
                    }
                }
            }
        }

        if (topButtons.contains(button)) {
            if (button.getItem() instanceof Material) {
                Material selectedMaterial = (Material) button.getItem();
                if (selectedMaterial.isHoldable()) {
                    int invQuantity = selectedMaterial.getQuantity();
                    if (toolBelt.addInventory(selectedMaterial) != 0) {
                        inventory.removeInventory(selectedMaterial);
                        selectedMaterial.setQuantity(invQuantity);
                        clearSelectedButton(button);
                    } else {
                        System.out.println("tool belt full");
                        return;
                    }
                }
            }
        }

        if (magicButtons.contains(button)) {
            Material selectedMaterial = (Material) button.getItem();
            if (!toolBelt.checkInventory(selectedMaterial) && toolBelt.addInventory(selectedMaterial) != 0) {
                setButtonBackground(button, new TextureRegionDrawable(new TextureRegionDrawable(buttonsAtlas.findRegion("inventoryBox"))));
                button.setSelected(false);
                setSelectedButton(null);
                button.setChecked(false);
            }
        }
        resetAllButtons();
    }

    public InventoryButton getSelectedButton() {
        return selectedButton;
    }

    public void setSelectedButton(InventoryButton selectedButton) {
        this.selectedButton = selectedButton;
    }

    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }

    public boolean keyDown (int keycode) {
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

    public Game getGame() {
        return game;
    }

    public BitmapFont getFont() {
        return font;
    }

    public Stage getStage() {
        return stage;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public World getWorld() {
        return world;
    }

    public List<InventoryButton> getTopButtons() {
        return topButtons;
    }

    public List<Button> getBottomButtons() {
        return bottomButtons;
    }

    public Map<String, InventoryButton> getLeftButtons() {
        return leftButtons;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void drawTopButtonNumbers(List<InventoryButton> buttons) {
        for (InventoryButton button : buttons) {
            if (button.getItem() instanceof Material && ((Material) button.getItem()).getMaxPerStack() > 1) {
                int quantity = ((Material) button.getItem()).getQuantity();
                if (quantity > 0) {
                    Vector2 pos = button.localToParentCoordinates(new Vector2(button.getParent().getParent().getX(), button.getParent().getParent().getY()));
                    Vector2 pos2 = button.localToAscendantCoordinates(button.getParent(), new Vector2(button.getParent().getX(), button.getParent().getY()));
                    Vector2 pos3 = button.localToAscendantCoordinates(button.getParent(), new Vector2(button.getParent().getX(), button.getParent().getY()));
                    getFont().draw(getSpriteBatch(), String.valueOf(quantity), pos.x + 35, pos.y + 70);
                }
            }
        }
    }

    public void drawButtonNumbers(List<InventoryButton> buttons) {
        for (InventoryButton button : buttons) {
            if (button.getItem() instanceof Material && ((Material) button.getItem()).getMaxPerStack() > 1) {
                int quantity = ((Material) button.getItem()).getQuantity();
                if (quantity > 1) {
                    Vector2 pos = button.localToParentCoordinates(new Vector2(button.getParent().getParent().getX(), button.getParent().getParent().getY()));
                    Vector2 pos2 = button.localToAscendantCoordinates(button.getParent(), new Vector2(button.getParent().getX(), button.getParent().getY()));
                    Vector2 pos3 = button.localToAscendantCoordinates(button.getParent(), new Vector2(button.getParent().getX(), button.getParent().getY()));
                    getFont().draw(getSpriteBatch(), String.valueOf(quantity), pos3.x+25, pos3.y+10);
                }
            }
        }
    }

    public void resetButtons(List<InventoryButton> buttons, Inventory inventory) {
        int i = 0;
        for (InventoryButton button : buttons) {
            Object inventoryObject = inventory.getSlots().get(i);
            fillButton(button, inventoryObject);
            i++;
        }
    }

    @SuppressWarnings("unchecked")
    public void resetAllButtons() {
        resetButtons(topButtons, inventory);
        resetButtons(toolBeltButtons, toolBelt);
    }

    public void fillButton(InventoryButton button, Object inventoryObject) {
        if (inventoryObject instanceof Material && ((Material) inventoryObject).getQuantity() > 0) {
            if (inventoryObject instanceof Item) {
                Item item = (Item)inventoryObject;
                button.setItem(item);
                if (item instanceof Fillable && ((Fillable) item).isFilled()) {
                    setButtonImage(button, "inv_full");
                } else {
                    setButtonImage(button, item.getName());
                }
            }
            if (!(inventoryObject instanceof Item) ) {
                Material material = (Material)inventoryObject;
                button.setItem(material);
                setButtonImage(button, material.getName());
            }
        } else  {
            if (selectedButton != null && selectedButton.equals(button)) {
                selectedButton.setItem(null);
                setButtonImage(selectedButton, null);
                setButtonBackground(selectedButton, new TextureRegionDrawable(new TextureRegionDrawable(buttonsAtlas.findRegion("inventoryBox"))));
            }

            button.setItem(null);
            setButtonImage(button, null);
            setButtonBackground(button, new TextureRegionDrawable(new TextureRegion(buttonsAtlas.findRegion("inventoryBox"))));
        }
    }

    public void addButtonToTable(Table table, List<InventoryButton> buttons, int rowCount) {
        int columnCount = 0;
        for (InventoryButton button : buttons) {
            columnCount++;
            table.add(button).width(50).height(50);
            if (columnCount == rowCount) {
                table.row();
                columnCount = 0;
            }
        }
    }

    public List<InventoryButton> createFillableButtons(Inventory inventory) {
        final List<InventoryButton> buttons = new ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            final InventoryButton button = new InventoryButton(null);
            setButtonBackground(button, new TextureRegionDrawable(buttonsAtlas.findRegion("inventoryBox")));

            button.addListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selected(button);
                }
            });
            buttons.add(button);
        }
        return buttons;
    }
}
