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
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.model.ScoreBoard;

public class LoadingScreen extends ScreenAdapter {

    private Game game;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private Stage stage;
    private ScoreBoard scoreBoard;

    LoadingScreen(Game game, SpriteBatch spriteBatch, BitmapFont font, ScoreBoard scoreBoard) {
        this.game = game;
        this.spriteBatch = spriteBatch;
        this.font = font;
        this.scoreBoard = scoreBoard;

        OrthographicCamera camera = new OrthographicCamera();
        Viewport viewport = new FitViewport(640, 480, camera);
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



        mainTable.center();
        mainTable.add(topTable).top();
        mainTable.add(bottomTable).bottom();



        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("buttons/buttons.atlas"));
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
                game.setScreen(new GameScreen(game, spriteBatch));
            }
        });
        exitButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        bottomTable.add(startButton).width(35).height(35);
        bottomTable.add(exitButton).width(35).height(35);
//        stage.addActor(startButton);
//        stage.addActor(exitButton);

        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, .25f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();

        spriteBatch.begin();
        font.draw(spriteBatch, scoreBoard.toString(), Gdx.graphics.getWidth() * .25f, Gdx.graphics.getHeight() * .75f);
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
