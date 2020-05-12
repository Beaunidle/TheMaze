package com.mygdx.game.model;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Level {

    private int width =  100;
    private int height = 70;
    private Block[][] blocks;
    private List<ExplodableBlock> explodableBlocks = new ArrayList<>();
    private List<GunPad> gunPads = new ArrayList<>();
    private List<BoostPad> boostPads = new ArrayList<>();
    private Map<Integer, SpawnPoint> spawnPoints = new HashMap<>();

    private Vector2 spanPosition;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public Block[][] getBlocks() {
        return blocks;
    }

    public void setBlocks(Block[][] blocks) {
        this.blocks = blocks;
    }

    public Level() {
        blocks = new Block[width][height];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                blocks[col][row] = null;
            }
        }
    }

    public Block get(int x, int y) {
        if (x > 0 && x < width && y > 0 && y < height) {
            return blocks[x][y];
        }
        return null;
    }

    public List<ExplodableBlock> getExplodableBlocks() {
        return explodableBlocks;
    }

    public List<GunPad> getGunPads() {
        return gunPads;
    }

    public List<BoostPad> getBoostPads() {
        return boostPads;
    }

    public Map<Integer, SpawnPoint> getSpawnPoints() {
        return spawnPoints;
    }

    public void setSpawnPoints(Map<Integer, SpawnPoint> spawnPoints) {
        this.spawnPoints = spawnPoints;
    }

    public Vector2 getSpanPosition() {
        return spanPosition;
    }

    public void setSpanPosition(Vector2 spanPosition) {
        this.spanPosition = spanPosition;
    }

    private void loadDemoLevel() {
        width = 100;
        height = 100;
        blocks = new Block[width][height];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                blocks[col][row] = null;
            }
        }

        for (int col = 10; col <= 50; col++) {
            blocks[col][10] = new Block(new Vector2(col, 10));
            blocks[10][col] = new Block(new Vector2(10, col));
            blocks[col][50] = new Block(new Vector2(col, 50));
            blocks[50][col] = new Block(new Vector2(50, col));
        }
        ExplodableBlock eb = new ExplodableBlock(new Vector2(24, 12));
        blocks[24][12] = eb;
        explodableBlocks.add(eb);
        //todo add some gun pads
        gunPads.add(new GunPad(new Vector2(30, 11), Gun.Type.PISTOL));
        gunPads.add(new GunPad(new Vector2(11, 30), Gun.Type.SMG));
        gunPads.add(new GunPad(new Vector2(30, 49), Gun.Type.SHOTGUN));
        gunPads.add(new GunPad(new Vector2(49, 30), Gun.Type.ROCKET));
    }
}
