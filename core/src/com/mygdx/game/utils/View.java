package com.mygdx.game.utils;

import com.mygdx.game.model.environment.blocks.Block;
import com.mygdx.game.model.pads.BoostPad;
import com.mygdx.game.model.pads.FloorPad;
import com.mygdx.game.model.moveable.Player;

import java.util.ArrayList;
import java.util.List;

public class View {

    private final Block[][] blocks;
    private Block[] blockingWall;
    private final List<Player> players;
    private final List<FloorPad> floorPads;
    private final List<BoostPad> boostPads;

    public View() {
        blocks = new Block[30][16];
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
