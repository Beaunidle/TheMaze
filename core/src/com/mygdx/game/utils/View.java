package com.mygdx.game.utils;

import com.mygdx.game.model.Block;
import com.mygdx.game.model.BoostPad;
import com.mygdx.game.model.FloorPad;
import com.mygdx.game.model.Player;

import java.util.ArrayList;
import java.util.List;

public class View {

    private Block[][] blocks;
    private Block[] blockingWall;
    private List<Player> players;
    private List<FloorPad> floorPads;
    private List<BoostPad> boostPads;

    public View() {
        blocks = new Block[15][8];
        blockingWall = new Block[3];
        players = new ArrayList<>();
        floorPads = new ArrayList<>();
        boostPads = new ArrayList<>();
    }

    public Block[][] getBlocks() {
        return blocks;
    }

    public Block[] getBlockingWall() {
        return blockingWall;
    }

    public void setBlockingWall(Block[] blockingWall) {
        this.blockingWall = blockingWall;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<FloorPad> getFloorPads() {
        return floorPads;
    }

    public List<BoostPad> getBoostPads() {
        return boostPads;
    }

    public void printView() {
        System.out.println("Printing new View");
        for (Block[] block : blocks) {
            for (Block b : block) {
                if (b != null)System.out.println("BLOCK," + (int)Math.floor(b.getPosition().x) + "," + (int)Math.floor(b.getPosition().y));
            }
        }
    }
}
