package com.mygdx.game.screens;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.TextureLoader;
import com.mygdx.game.model.GameButton;
import com.mygdx.game.model.World;
import com.mygdx.game.WorldRenderer;
import com.mygdx.game.controller.WorldController;
import com.mygdx.game.model.environment.blocks.FillableBlock;
import com.mygdx.game.utils.JoyStick;
import com.mygdx.game.utils.Locator;

import java.util.HashMap;
import java.util.Map;

public class GameScreen implements Screen, InputProcessor {

    public enum touchType {
        MOVE, FIRE, USE
    }

    private Game game;
    private World world;
    private WorldRenderer renderer;
    private TextureLoader textureLoader;
    private WorldController controller;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private Locator locator;
    private Map<Integer, touchType> touchTypes = new HashMap<>();


    private int width, height;
    float xRatio;
    float yRatio;

    GameScreen(Game game, SpriteBatch spriteBatch, BitmapFont font) {
        this.game = game;
        this.spriteBatch = spriteBatch;
        this.textureLoader = new TextureLoader();
        textureLoader.initTextures();
        this.font = font;
        world = new World();
        locator = new Locator();
        this.width = Gdx.app.getGraphics().getWidth();
        this.height = Gdx.app.getGraphics().getHeight();

        xRatio = 30F / width;
        yRatio = 16F / height;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.5f, 0.1f, 0.5f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (controller.isLevelFinished()) {
            BitmapFont font = new BitmapFont();
            game.setScreen(new LoadingScreen(game, spriteBatch, font, controller.getScoreBoard()));
        }
        if (controller.isPaused() && controller.getFillableToShow() == null) {
            game.setScreen(new InstructionsScreen(game, spriteBatch, font, this));
            return;
        }
        if (controller.getFillableToShow() != null) {
            if (controller.getFillableToShow().getFillableType().equals(FillableBlock.FillableType.INVSCREEN)) {
                game.setScreen(new InventoryScreen(game, spriteBatch, font, this, world));
                return;
            }
            if (controller.getFillableToShow().getFillableType().equals(FillableBlock.FillableType.MAPSCREEN)) {
                game.setScreen(new MapScreen(game, spriteBatch, font, this, world));
                return;
            }
            if (!controller.getFillableToShow().isRecipeSelect())game.setScreen(new FillableScreen(game, spriteBatch, font, this, world, controller.getFillableToShow()));
            else game.setScreen(new CraftScreen(game, spriteBatch, font, this, world, controller.getFillableToShow().getRecipes()));
        }
        controller.update(delta);
        renderer.render();
    }

    @Override
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void show() {
//        Gdx.input.setCursorCatched(true);
        renderer = new WorldRenderer(world, spriteBatch, false, font, textureLoader);
        controller = new WorldController(world, game);
        controller.leftReleased();
        controller.rightReleased();
        controller.strafeLeftReleased();
        controller.strafeRightReleased();
        controller.upReleased();
        controller.downReleased();
        controller.useReleased();
        controller.fireReleased();
        controller.shiftReleased();
        controller.invReleased();
        controller.mapReleased();
        controller.slotLeftReleased();
        controller.slotRightReleased();
        controller.slotUseReleased();
        controller.pauseReleased();
        Gdx.input.setInputProcessor(this);
    }

    void loadLevel(int level) {
        controller.leftReleased();
        controller.rightReleased();
        controller.strafeLeftReleased();
        controller.strafeRightReleased();
        controller.upReleased();
        controller.downReleased();
        controller.useReleased();
        controller.fireReleased();
        controller.shiftReleased();
        controller.invReleased();
        controller.mapReleased();
        controller.slotLeftReleased();
        controller.slotRightReleased();
        controller.slotUseReleased();
        controller.pauseReleased();
        System.out.println("game screen Loading level " + level);
        controller.loadLevel(level);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.NUMPAD_4)
            controller.leftPressed();
        if (keycode == Input.Keys.NUMPAD_6)
            controller.rightPressed();
        if (keycode == Input.Keys.W)
            controller.upPressed();
        if (keycode == Input.Keys.S)
            controller.downPressed();
        if (keycode == Input.Keys.A)
            controller.strafeLeftPressed();
        if (keycode == Input.Keys.D)
            controller.strafeRightPressed();
        if (keycode == Input.Keys.ENTER)
            controller.usePressed();
        if (keycode == Input.Keys.SPACE)
            controller.firePressed();
        if (keycode == Input.Keys.C)
            controller.shiftPressed();
        if (keycode == Input.Keys.I)
            controller.invPressed();
        if (keycode == Input.Keys.M) {
            controller.mapPressed();
        }
        if (keycode == Input.Keys.P)
            controller.pausePressed();
        if (keycode == Input.Keys.NUMPAD_2)
            controller.slotLeftPressed();
        if (keycode == Input.Keys.NUMPAD_8)
            controller.slotRightPressed();
        if (keycode == Input.Keys.NUMPAD_5)
            controller.slotUsePressed();
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.NUMPAD_4)
            controller.leftReleased();
        if (keycode == Input.Keys.NUMPAD_6)
            controller.rightReleased();
        if (keycode == Input.Keys.W)
            controller.upReleased();
        if (keycode == Input.Keys.S)
            controller.downReleased();
        if (keycode == Input.Keys.A)
            controller.strafeLeftReleased();
        if (keycode == Input.Keys.D)
            controller.strafeRightReleased();
        if (keycode == Input.Keys.ENTER)
            controller.useReleased();
        if (keycode == Input.Keys.SPACE)
            controller.fireReleased();
        if (keycode == Input.Keys.C)
            controller.shiftReleased();
        if (keycode == Input.Keys.I)
            controller.invReleased();
        if (keycode == Input.Keys.M) {
            controller.mapReleased();
        }
        if (keycode == Input.Keys.NUMPAD_2)
            controller.slotLeftReleased();
        if (keycode == Input.Keys.NUMPAD_8)
            controller.slotRightReleased();
        if (keycode == Input.Keys.NUMPAD_5)
            controller.slotUseReleased();
        if (keycode == Input.Keys.P)
            controller.pauseReleased();
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int pointer, int button) {
//        if (!Gdx.app.getType().equals(Application.ApplicationType.Android))
//            return false;
        if (x < width / 2 && y > height / 2) {
            world.setMoveJoystick(new JoyStick(new Vector2(x, height - y)));
//            world.setTouchPoint(new Vector2(x, height - y));
            touchTypes.put(pointer, touchType.MOVE);
        }

        if (x > width / 2 && y > height / 2) {
            world.setFireJoystick(new JoyStick(new Vector2(x, height - y)));
            touchTypes.put(pointer, touchType.FIRE);
        }

        for (GameButton butt : world.getButtons()) {
            if (butt.getArea().contains(new Vector2(x * xRatio, y * yRatio))) {
                switch (butt.getType()) {
                    case FIRE:
                        controller.firePressed();
                        touchTypes.put(pointer, touchType.FIRE);
                        break;
                    case USE:
                        controller.usePressed();
                        touchTypes.put(pointer, touchType.USE);
                }
            }
        }
        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
//        if (!Gdx.app.getType().equals(Application.ApplicationType.Android))
//            return false;

        if (touchTypes.get(pointer) != null && touchTypes.get(pointer).equals(touchType.MOVE)) {
            world.setMoveJoystick(null);
//            world.setTouchPoint(null);
//            world.setDragPoint(null);
//            world.setTouchCircle(null);
            touchTypes.remove(pointer);
        }

        if (touchTypes.get(pointer) != null && touchTypes.get(pointer).equals(touchType.FIRE)) {
            world.setFireJoystick(null);
            touchTypes.remove(pointer);
        }

        if (touchTypes.get(pointer) != null && touchTypes.get(pointer).equals(touchType.USE)) {
            controller.useReleased();
            touchTypes.remove(pointer);
        }
        return true;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        // TODO Auto-generated method stub
        if (touchTypes.get(pointer) != null) {
            if (touchTypes.get(pointer).equals(touchType.MOVE) && world.getMoveJoystick() != null) {
                if (world.getMoveJoystick().getTouchCircle().contains(new Vector2(x, height - y)))  {
                    world.getMoveJoystick().setDrag(new Vector2(x, height - y));
                }
            }

            if (touchTypes.get(pointer).equals(touchType.FIRE) && world.getFireJoystick() != null) {
                if (world.getFireJoystick().getTouchCircle().contains(new Vector2(x, height - y))) {
                    world.getFireJoystick().setDrag(new Vector2(x, height - y));
                }
            }
//            if (world.getTouchPoint() != null) {
//                if (world.getTouchCircle().contains(new Vector2(x, height - y))) {
//                    world.setDragPoint(new Vector2(x, height - y));
//                }
                return true;
//            }
        }
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

    public WorldController getController() {
        return controller;
    }
}