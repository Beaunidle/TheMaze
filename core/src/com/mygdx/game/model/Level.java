package com.mygdx.game.model;

import static com.mygdx.game.model.items.Ranged.RangedType.PISTOL;
import static com.mygdx.game.model.items.Ranged.RangedType.ROCKET;
import static com.mygdx.game.model.items.Ranged.RangedType.SHOTGUN;
import static com.mygdx.game.model.items.Ranged.RangedType.SMG;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.environment.AnimalSpawn;
import com.mygdx.game.model.environment.blocks.Block;
import com.mygdx.game.model.environment.blocks.Building;
import com.mygdx.game.model.environment.blocks.ExplodableBlock;
import com.mygdx.game.model.environment.SpawnPoint;
import com.mygdx.game.model.environment.blocks.Grower;
import com.mygdx.game.model.items.Ranged;
import com.mygdx.game.model.pads.BoostPad;
import com.mygdx.game.model.pads.FloorPad;
import com.mygdx.game.model.pads.GunPad;
import com.mygdx.game.model.pads.Pad;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Level {

    private int width =  300;
    private int height = 300;
    private GameObject[][] blocks;
//    private GameObject[][] houseBlocks;
    private Map<Integer, Building> buildings = new HashMap<>();
    private List<Block> environmentBlocks = new ArrayList<>();
    private final List<ExplodableBlock> explodableBlocks = new ArrayList<>();
    private final List<GunPad> gunPads = new ArrayList<>();
    private final List<BoostPad> boostPads = new ArrayList<>();
    private final List<FloorPad> floorPads = new ArrayList<>();
    private final List<Grower> growers = new ArrayList<>();
    private final Map<Integer, SpawnPoint> spawnPoints = new HashMap<>();
    private final List<AnimalSpawn> animalSpawnPoints = new ArrayList<>();

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

    public GameObject[][] getBlocks() {
        return blocks;
    }

//    public GameObject[][] getHouseBlocks() {
//        return houseBlocks;
//    }

//    public void setHouseBlocks(GameObject[][] houseBlocks) {
//        this.houseBlocks = houseBlocks;
//    }


    public Map<Integer, Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(Map<Integer, Building> buildings) {
        this.buildings = buildings;
    }

    public void setBlocks(GameObject[][] blocks) {
        this.blocks = blocks;
    }

//    public List<Block> getEnvironmentBlocks() {
//        return environmentBlocks;
//    }


    public List<Grower> getGrowers() {
        return growers;
    }

    public Level() {
        blocks = new GameObject[width][height];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                blocks[col][row] = null;
            }
        }
//        houseBlocks = new GameObject[10][10];
//        for (int col = 0; col < 10; col++) {
//            for (int row = 0; row < 10; row++) {
//                houseBlocks[col][row] = null;
//            }
//        }
    }

    public Block getBlock(int x, int y) {
        if (x > 0 && x < width && y > 0 && y < height) {
            if (blocks[x][y] != null && blocks[x][y] instanceof Block) return (Block)blocks[x][y];
        }
        return null;
    }

//    public Block getHouseBlock(int x, int y) {
//        int houseX = x - 1000;
//        int houseY = y - 1000;
//        if (houseX >= 0 && houseX < 10 && houseY >= 0 && houseY < 10) {
//            if (houseBlocks[houseX][houseY] != null && houseBlocks[houseX][houseY] instanceof Block) return (Block)houseBlocks[houseX][houseY];
//        }
//        return null;
//    }

    public FloorPad getPad(int x, int y) {
        if (x > 0 && x < width && y > 0 && y < height) {
            if (blocks[x][y] != null && blocks[x][y] instanceof FloorPad) return (FloorPad) blocks[x][y];
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

    public List<FloorPad> getFloorPads() {
        return floorPads;
    }

    public Map<Integer, SpawnPoint> getSpawnPoints() {
        return spawnPoints;
    }

    public List<AnimalSpawn> getAnimalSpawnPoints() {
        return animalSpawnPoints;
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
        blocks = new GameObject[width][height];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                blocks[col][row] = null;
            }
        }

        for (int col = 10; col <= 50; col++) {
            blocks[col][10] = new Block(new Vector2(col, 10), "block");
            blocks[10][col] = new Block(new Vector2(10, col), "block");
            blocks[col][50] = new Block(new Vector2(col, 50), "block");
            blocks[50][col] = new Block(new Vector2(50, col), "block");
        }
        ExplodableBlock eb = new ExplodableBlock(new Vector2(24, 12));
        blocks[24][12] = eb;
        explodableBlocks.add(eb);
        //todo add some gun pads
        gunPads.add(new com.mygdx.game.model.pads.GunPad(new Vector2(30, 11), PISTOL));
        gunPads.add(new com.mygdx.game.model.pads.GunPad(new Vector2(11, 30), SMG));
        gunPads.add(new com.mygdx.game.model.pads.GunPad(new Vector2(30, 49), SHOTGUN));
        gunPads.add(new GunPad(new Vector2(49, 30), ROCKET));
    }
}
