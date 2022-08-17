package com.mygdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MapRenderer;
import com.mygdx.game.WorldRenderer;
import com.mygdx.game.model.GameButton;
import com.mygdx.game.model.ScoreBoard;
import com.mygdx.game.model.World;
import com.mygdx.game.utils.JoyStick;

public class MapScreen extends ScreenAdapter implements InputProcessor {
    private Game game;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private Stage stage;
    private GameScreen gameScreen;
    private MapRenderer renderer;
    private World world;
    private int width, height;
    float xRatio;
    float yRatio;
    boolean camUp, camDown, camLeft, camRight, zoomIn, zoomOut;

    MapScreen(Game game, SpriteBatch spriteBatch, BitmapFont font, GameScreen gameScreen, World world) {
        this.game = game;
        this.spriteBatch = spriteBatch;
        this.font = font;
        this.gameScreen = gameScreen;
        this.world = world;

        this.width = Gdx.app.getGraphics().getWidth();
        this.height = Gdx.app.getGraphics().getHeight();
        xRatio = 30F / width;
        yRatio = 16F / height;

        OrthographicCamera camera = new OrthographicCamera();
        Viewport viewport = new FitViewport(Gdx.app.getGraphics().getWidth(), Gdx.app.getGraphics().getHeight(), camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
        camera.update();

        stage = new Stage(viewport, spriteBatch);
    }

    public void show(){
        renderer = new MapRenderer(world, spriteBatch, false, font);
        Gdx.input.setCursorCatched(false);
        Gdx.input.setInputProcessor(this);

//        Gdx.input.setCursorCatched(false);
//        Gdx.input.setInputProcessor(stage);

//        Set table to fill stage

//        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("buttons.atlas"));
//        Button startButton = new Button(new TextureRegionDrawable(atlas.findRegion("start")));
//        Button exitButton = new Button(new TextureRegionDrawable(atlas.findRegion("exit")));
//        //todo title button

//        startButton.setWidth(100);
//        startButton.setHeight(100);
//        startButton.setX(150);
//        startButton.setY(50);
//
//        exitButton.setWidth(100);
//        exitButton.setHeight(100);
//        exitButton.setX(400);
//        exitButton.setY(50);

//        startButton.addListener(new ClickListener(){
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                game.setScreen(gameScreen);
//                ((GameScreen) gameScreen).getController().setFillableToShow(null);
//                ((GameScreen) gameScreen).getController().startUnpauseTimer(5);
//            }
//        });
//        exitButton.addListener(new ClickListener(){
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                Gdx.app.exit();
//            }
//        });

//        bottomTable.add(startButton).width(50).height(50);
//        bottomTable.add(exitButton).width(50).height(50);
//        stage.addActor(startButton);
//        stage.addActor(exitButton);

//        mainTable.add(topTable).padBottom(1000);
//        mainTable.center();
//        mainTable.add(bottomTable);
//        stage.addActor(mainTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, .25f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (camUp) {
            renderer.moveCamY(1);
        }
        if (camDown) {
            renderer.moveCamY(-1);
        }
        if (camLeft) {
            renderer.moveCamX(-1);
        }
        if (camRight) {
            renderer.moveCamX(1);
        }

//        stage.act();
//        stage.draw();
        renderer.render();

//        spriteBatch.begin();
//        spriteBatch.end();
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

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        if (character == Input.Keys.W) {
            camUp = true;
        }
        if (character == Input.Keys.S) {
            camDown = true;
        }
        if (character == Input.Keys.A) {
            camLeft = true;
        }
        if (character == Input.Keys.D) {
            camRight = true;
        }
        return true;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.W) {
            camUp = true;
        }
        if (keycode == Input.Keys.S) {
            camDown = true;
        }
        if (keycode == Input.Keys.A) {
            camLeft = true;
        }
        if (keycode == Input.Keys.D) {
            camRight = true;
        }
        if (keycode == Input.Keys.Y) {
            renderer.adjustZoom(1);
        }
        if (keycode == Input.Keys.H) {
            renderer.adjustZoom(-1);
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.W) {
            camUp = false;
        }
        if (keycode == Input.Keys.S) {
            camDown = false;
        }
        if (keycode == Input.Keys.A) {
            camLeft = false;
        }
        if (keycode == Input.Keys.D) {
            camRight = false;
        }

        return true;
    }


    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
      for (GameButton butt : world.getButtons()) {
            if ((butt.getArea().contains(new Vector2(x * xRatio, y * yRatio))) || (button == Input.Buttons.LEFT)) {
                switch (butt.getType()) {
                    case FIRE:
                        game.setScreen(gameScreen);
                        ((GameScreen) gameScreen).getController().setFillableToShow(null);
                        ((GameScreen) gameScreen).getController().startUnpauseTimer(5);
                        break;
                    case USE:
                        game.setScreen(gameScreen);
                        ((GameScreen) gameScreen).getController().setFillableToShow(null);
                        ((GameScreen) gameScreen).getController().startUnpauseTimer(5);
                        break;
                }
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        return  false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean touchMoved(int x, int y) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean mouseMoved(int x, int y) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // TODO Auto-generated method stub
        return false;
    }

}
