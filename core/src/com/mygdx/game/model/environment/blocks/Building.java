package com.mygdx.game.model.environment.blocks;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.GameObject;

public class Building extends Block{

    private final int houseType;
    private final int number;
    private GameObject[][] blocks;
    private int internalWidth, internalHeight;

    public Building(Vector2 pos, double maxDurability, float width, float height, int rotation, String name, int houseType, int number) {
        super(pos, maxDurability, width, height, rotation, name);
        this.houseType = houseType;
        this.number = number;
        setBlockType(BlockType.BUILDING);
        switch (this.houseType) {
            case 1:
                internalWidth = 6;
                internalHeight = 6;
                break;
            case 2:
                internalWidth = 10;
                internalHeight = 10;
            default:
                break;
        }
        initBlocks(internalWidth, internalHeight, number * 1000);
    }

    private void initBlocks(int width, int height, int offset) {
        blocks = new GameObject[width+1][height+1];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                blocks[col][row] = null;
            }
        }
        for (int i = 0; i < width; i++) {
            blocks[0][i] = new Block(new Vector2(offset, i + offset), "block");
            blocks[width - 1][i] = new Block(new Vector2(width - 1 + offset, i + offset), "block");
        }
        for (int i = 0; i < height; i++) {
            blocks[i][0] = new Block(new Vector2(i + offset, offset), "block");
            blocks[i][height - 1] = new Block(new Vector2(i + offset, height - 1 + offset), "block");
        }
        int doorX = (int)Math.floor(width/2F);
        blocks[doorX][0] = new Wall(new Vector2(doorX  + offset, offset), 180, Block.getSIZE(), Block.getSIZE()/4, true);
    }

    public Block getBlock(int x, int y) {

        int houseX = x - number*1000;
        int houseY = y - number*1000;
        if (houseX >= 0 && houseX < internalWidth && houseY >= 0 && houseY < internalHeight) {
            GameObject block = blocks[houseX][houseY];
            if (block instanceof Block) return (Block) block;
        }
        return null;
    }

    public void putBlock(int x, int y, GameObject block) {
        blocks[x - number*1000][y - number*1000] = block;
    }

    public int getNumber() {
        return number;
    }

    public int getInternalWidth() {
        return internalWidth;
    }

    public void setInternalWidth(int internalWidth) {
        this.internalWidth = internalWidth;
    }

    public int getInternalHeight() {
        return internalHeight;
    }

    public void setInternalHeight(int internalHeight) {
        this.internalHeight = internalHeight;
    }
}
