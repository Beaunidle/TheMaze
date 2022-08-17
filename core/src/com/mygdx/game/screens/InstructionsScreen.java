package com.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.model.ScoreBoard;

public class InstructionsScreen extends ScreenAdapter {

    private Game game;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private Stage stage;
    private ScoreBoard scoreBoard;
    private GameScreen gameScreen;

    InstructionsScreen(Game game, SpriteBatch spriteBatch, BitmapFont font, GameScreen gameScreen) {
        this.game = game;
        this.spriteBatch = spriteBatch;
        this.font = font;
        this.gameScreen = gameScreen;
        this.scoreBoard = scoreBoard;

        OrthographicCamera camera = new OrthographicCamera();
        Viewport viewport = new FitViewport(Gdx.app.getGraphics().getWidth(), Gdx.app.getGraphics().getHeight(), camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        stage = new Stage(viewport, spriteBatch);
    }

    public void show(){

        Gdx.input.setCursorCatched(false);
        Gdx.input.setInputProcessor(stage);

        Table mainTable = new Table();
        Table topTable = new Table();
        Table bottomTable = new Table();

//        Set table to fill stage
        mainTable.setFillParent(true);

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("buttons.atlas"));
        Button startButton = new Button(new TextureRegionDrawable(atlas.findRegion("start")));
        Button exitButton = new Button(new TextureRegionDrawable(atlas.findRegion("exit")));
        //todo title button

//        startButton.setWidth(100);
//        startButton.setHeight(100);
//        startButton.setX(150);
//        startButton.setY(50);
//
//        exitButton.setWidth(100);
//        exitButton.setHeight(100);
//        exitButton.setX(400);
//        exitButton.setY(50);

        startButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(gameScreen);
                ((GameScreen) gameScreen).getController().setFillableToShow(null);
                ((GameScreen) gameScreen).getController().startUnpauseTimer(5);
            }
        });
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        bottomTable.add(startButton).width(50).height(50);
        bottomTable.add(exitButton).width(50).height(50);
//        stage.addActor(startButton);
//        stage.addActor(exitButton);

        mainTable.center();
        mainTable.add(topTable).padBottom(1000);
        mainTable.add(bottomTable);
        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, .25f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();

        spriteBatch.begin();
        float xPos = Gdx.graphics.getWidth() * 0.25F;
        float yPos = Gdx.graphics.getHeight() * 0.75F;
        font.getData().setScale(1.5F);
        font.draw(spriteBatch, "A, W, S, D to Move", xPos, yPos + 100);
        font.draw(spriteBatch, "Arrow keys to Turn", xPos, yPos + 80);
        font.draw(spriteBatch, "Z, C to Switch Tool Belt Slots", xPos, yPos + 60);
        font.draw(spriteBatch, "X to Switch Between Tool Belt and Hand", xPos, yPos + 40);
        font.draw(spriteBatch, "Space to Use Strong Hand", xPos, yPos + 20);
        font.draw(spriteBatch, "Enter to Use Weak Hand/Pick up", xPos, yPos);
        font.draw(spriteBatch, "Shift to Dodge", xPos, yPos - 20);

        spriteBatch.end();
    }

    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }

    private void endScreen() {
//        System.out.println("Loading screen level " + number);
//        gameScreen.loadLevel(number);
//        game.setScreen(gameScreen);
    }
}
