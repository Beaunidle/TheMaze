package com.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LoadingScreen extends ScreenAdapter {

    private Game game;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private Stage stage;
    private int level;
    private GameScreen gameScreen;

    LoadingScreen(Game game, SpriteBatch spriteBatch, BitmapFont font, int level, GameScreen gameScreen) {
        this.game = game;
        this.spriteBatch = spriteBatch;
        this.font = font;
        this.level = level;
        this.gameScreen = gameScreen;
        Timer.Task timer = new Timer.Task() {
            @Override
            public void run() {
                endScreen();
            }
        };
        Timer.schedule(timer, 5);

        OrthographicCamera camera = new OrthographicCamera();
        Viewport viewport = new FitViewport(640, 480, camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        stage = new Stage(viewport, spriteBatch);
    }

    public void show(){

        //todo start the timer
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, .25f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();

        spriteBatch.begin();
        font.draw(spriteBatch, "Loading Level " + (level + 1), Gdx.graphics.getWidth() * .25f, Gdx.graphics.getHeight() * .75f);
        spriteBatch.end();
    }

    @Override
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }

    private void endScreen() {
        int number = level + 1;
        System.out.println("Loading screen level " + number);
        gameScreen.loadLevel(number);
        game.setScreen(gameScreen);
    }
}
