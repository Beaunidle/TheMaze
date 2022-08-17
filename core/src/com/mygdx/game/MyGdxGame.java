package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.screens.TitleScreen;

public class MyGdxGame extends Game {

    SpriteBatch spriteBatch;
    BitmapFont font;

    @Override
    public void create() {
        spriteBatch = new SpriteBatch();
        font = new BitmapFont();
        setScreen(new TitleScreen(this, spriteBatch, font));
    }

    @Override
    public void dispose () {
        spriteBatch.dispose();
        font.dispose();
    }
}
