package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.screens.TitleScreen;

public class MyGdxGame extends Game {

    @Override
    public void create() {
        SpriteBatch spriteBatch = new SpriteBatch();
        BitmapFont font = new BitmapFont();
        setScreen(new TitleScreen(this, spriteBatch, font));
    }
}
