package com.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import java.util.ArrayList;
import java.util.List;

public class TitleScreen extends ScreenAdapter implements InputProcessor {

    private Game game;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private Stage stage;


    public TitleScreen(Game game, SpriteBatch spriteBatch, BitmapFont font) {
        this.game = game;
        this.spriteBatch = spriteBatch;
        this.font = font;

        OrthographicCamera camera = new OrthographicCamera();
        Viewport viewport = new FitViewport(640, 480, camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        stage = new Stage(viewport, spriteBatch);
    }
    @Override
    public void show(){
        Gdx.input.setCursorCatched(false);
        Gdx.input.setInputProcessor(stage);

//        Table mainTable = new Table();
//        Table topTable = new Table();
//        Table bottomTable = new Table();

        //Set table to fill stage
//        mainTable.setFillParent(true);

//        for (Button button : createButtons()) {
//            topTable.add(button).width(35).height(35);
//        }
//        for (Button button : createOtherButtons()) {
//            bottomTable.add(button).width(35).height(35);
//        }

//        mainTable.center();
//        mainTable.add(topTable).top();
//        mainTable.add(bottomTable).bottom();

//        int i = 1;
//        for (Button button : createButtons()) {
//            button.setWidth(35);
//            button.setHeight(35);
//            button.setX(50*i);
//            button.setY(50*i);
//            stage.addActor(button);
//            i++;
//        }

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("buttons/buttons.atlas"));
        Button startButton = new Button(new TextureRegionDrawable(atlas.findRegion("start")));
        Button exitButton = new Button(new TextureRegionDrawable(atlas.findRegion("exit")));

        startButton.setWidth(100);
        startButton.setHeight(100);
        startButton.setX(150);
        startButton.setY(100);

        exitButton.setWidth(100);
        exitButton.setHeight(100);
        exitButton.setX(400);
        exitButton.setY(100);

        startButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, spriteBatch));
            }
        });
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        stage.addActor(startButton);
        stage.addActor(exitButton);

//        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, .25f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();

        spriteBatch.begin();
        font.getLineHeight();
        font.draw(spriteBatch, "Moody Mayhem!", Gdx.graphics.getWidth() * .25f, Gdx.graphics.getHeight() * .75f);
        font.draw(spriteBatch, "Run around and don't die!!!", Gdx.graphics.getWidth() * .25f, Gdx.graphics.getHeight() * .65f);
        font.draw(spriteBatch, "Click start to play", Gdx.graphics.getWidth() * .25f, Gdx.graphics.getHeight() * .55f);
        spriteBatch.end();
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

    private List<Button> createButtons() {
        //Create buttons
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("items.atlas"));
        Button blockButton = new Button(new TextureRegionDrawable(atlas.findRegion("block")));
        blockButton.setWidth(blockButton.getWidth()/2);
        blockButton.setHeight(blockButton.getHeight()/2);

        Button shotgunPadButton = new Button(new TextureRegionDrawable(atlas.findRegion("gunShotgun")));
        Button rocketPadButton = new Button(new TextureRegionDrawable(atlas.findRegion("gunRocket")));
        Button floorButton = new Button(new TextureRegionDrawable(atlas.findRegion("floor")));
        Button rubbleButton = new Button(new TextureRegionDrawable(atlas.findRegion("rubbleBlock")));

        //Add listeners to buttons

        floorButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        List<Button> buttons = new ArrayList<>();
        //Add buttons to table
        buttons.add(blockButton);
        buttons.add(shotgunPadButton);
        buttons.add(rocketPadButton);
        buttons.add(floorButton);
        buttons.add(rubbleButton);

        return buttons;
    }

    public List<Button> createOtherButtons() {

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("items.atlas"));
        Button blockButton = new Button(new TextureRegionDrawable(atlas.findRegion("block")));
        blockButton.setWidth(blockButton.getWidth()/2);
        blockButton.setHeight(blockButton.getHeight()/2);


        Button redButton = new Button(new TextureRegionDrawable(atlas.findRegion("explodingBlockRed")));
        Button aiButton = new Button(new TextureRegionDrawable(atlas.findRegion("injured-01")));
        Button pistolPadButton = new Button(new TextureRegionDrawable(atlas.findRegion("gunPistol")));
        Button smgPadButton = new Button(new TextureRegionDrawable(atlas.findRegion("gunSMG")));

        List<Button> buttons = new ArrayList<>();
        //Add buttons to table
        buttons.add(blockButton);
        buttons.add(redButton);
        buttons.add(aiButton);
        buttons.add(pistolPadButton);
        buttons.add(smgPadButton);

        return buttons;
    }
}
