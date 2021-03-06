package com.mygdx.game.model;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.controller.LevelLoader;
import com.mygdx.game.model.Block;
import com.mygdx.game.model.Level;
import com.mygdx.game.model.Player;
import com.mygdx.game.utils.JoyStick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class World {

    public enum JoystickState {
        STILL, SPIN, MOVE
    }

    private Player bob;
    private List<Bullet> bullets = new ArrayList<>();
    private List<Explosion> explosions = new ArrayList<>();
    private List<AIPlayer> AIPlayers = new ArrayList<>();
    private List<BloodStain> bloodStains = new ArrayList<>();
    private List<GameButton> buttons = new ArrayList<>();
    private Level level;
    private Array<Polygon> collisionRects = new Array<>();
    private LevelLoader levelLoader = new LevelLoader();
    private JoyStick moveJoystick, fireJoystick;

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

    public List<GameButton> getButtons() {
        return buttons;
    }

    public JoyStick getMoveJoystick() {
        return moveJoystick;
    }

    public void setMoveJoystick(JoyStick moveJoystick) {
        this.moveJoystick = moveJoystick;
    }

    public JoyStick getFireJoystick() {
        return fireJoystick;
    }

    public void setFireJoystick(JoyStick fireJoystick) {
        this.fireJoystick = fireJoystick;
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
        System.out.println("Loading level " + number);
        bloodStains.clear();
        explosions.clear();
        bullets.clear();
        level = levelLoader.loadLevel(number);
        Map<Integer, SpawnPoint> spawnPoints = level.getSpawnPoints();
        List<Integer> numbers = new ArrayList<>();
        Random rand = new Random();
        int rando = rand.nextInt(spawnPoints.size());
        SpawnPoint sp = spawnPoints.get(rando);
        bob = new Player(new Vector2(sp.getPosition()), "player", 20);
        numbers.add(rando);

        for (int i = 0; i < spawnPoints.size() -1; i++) {
            while (numbers.contains(rando)) {
                rando = rand.nextInt(spawnPoints.size());
            }
            sp = spawnPoints.get(rando);
            AIPlayers.add(new AIPlayer(new Vector2(sp.getPosition()), "ai-0" + i));
            numbers.add(rando);
        }
        buttons.add(new GameButton(new Vector2(12, 3), 0.5F, GameButton.Type.USE));
    }
}
