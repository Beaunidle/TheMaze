package com.mygdx.game.model;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.controller.LevelLoader;
import com.mygdx.game.model.Block;
import com.mygdx.game.model.Level;
import com.mygdx.game.model.Player;

import java.util.ArrayList;
import java.util.List;

public class World {

    /** Our player controlled hero **/
    private Player bob;
    private List<Bullet> bullets = new ArrayList<>();
    private List<Explosion> explosions = new ArrayList<>();
    private List<AIPlayer> AIPlayers;
    private List<BloodStain> bloodStains = new ArrayList<>();
    private Level level;
    private Array<Polygon> collisionRects = new Array<>();
    private LevelLoader levelLoader = new LevelLoader();

    public World() {
        loadWorld(1);
    }

    // Getters -----------

    public Array<Polygon> getCollisionRects() {
        return collisionRects;
    }

    public Player getBob() {
        return bob;
    }

    public Level getLevel() {
        return level;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public void setBullets(List<Bullet> bullets) {
        this.bullets = bullets;
    }

    public List<Explosion> getExplosions() {
        return explosions;
    }

    public void setExplosions(List<Explosion> explosions) {
        this.explosions = explosions;
    }

    public List<BloodStain> getBloodStains() {
        return bloodStains;
    }

    public void setBloodStains(List<BloodStain> bloodStains) {
        this.bloodStains = bloodStains;
    }

    public List<AIPlayer> getAIPlayers() {
        return AIPlayers;
    }

    public void setAIPlayers(List<AIPlayer> AIPlayers) {
        this.AIPlayers = AIPlayers;
    }

    // --------------------

    /** Return only the blocks that need to be drawn **/
    public List<Block> getDrawableBlocks(int width, int height) { //7 and 4
        int x = (int)bob.getPosition().x - width;
        int y = (int)bob.getPosition().y - height;
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }


        int x2 = x + 2 * width;
        int y2 = y + 2 * height;
        if (x2 > level.getWidth()) {
            x2 = level.getWidth() - 1;
        }
        if (y2 > level.getHeight()) {
            y2 = level.getHeight() - 1;
        }

        List<Block> blocks = new ArrayList<>();
        Block block;
        for (int col = x; col <= x2; col++) {
            for (int row = y; row <= y2; row++) {
                block = level.get(col, row);
                if (block != null) {
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }

    public void loadWorld(int number) {
        bloodStains.clear();
        explosions.clear();
        bullets.clear();
        level = levelLoader.loadLevel(number);
        bob = level.getPlayer();
        AIPlayers = level.getAiPlayers();
    }
}
