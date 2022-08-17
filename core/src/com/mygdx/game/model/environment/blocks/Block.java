package com.mygdx.game.model.environment.blocks;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Block {

    public enum BlockType {
        ENVIRONMENT,FILLABLE,GROWER,TILLED,WALL,BED
    }
    static final float SIZE = 1f;

    private Vector2 position = new Vector2();
    private Polygon bounds = new Polygon();
    private double durability;
    private double maxDurability;
    private boolean colibible = true;
    private BlockType blockType;

    public static float getSIZE() {
        return SIZE;
    }

    public Block(Vector2 pos) {
        this.position = pos;
        this.bounds = new Polygon(new float[]{0, 0, SIZE, SIZE, 0, SIZE, SIZE, 0});
        this.bounds.setPosition(pos.x, pos.y);
        this.maxDurability = 100;
        this.durability = maxDurability;
    }

    public Block(Vector2 pos, double maxDurability) {
        this.position = pos;
        this.bounds = new Polygon(new float[]{0, 0, 0, SIZE, SIZE, SIZE, SIZE, 0});
        this.bounds.setPosition(pos.x, pos.y);
        this.maxDurability = maxDurability;
        this.durability = maxDurability;
    }

    public Block(Vector2 pos, double maxDurability, float size, int rotation) {
        this.position = pos;
        if (size == 1) {
            this.bounds = new Polygon(new float[]{0, 0, 0, SIZE, size, SIZE, size, 0});
        } else if (size == 2) {
            this.bounds = new Polygon(new float[]{0, 0, 0, SIZE, size, SIZE, size, 0});
            this.bounds.setOrigin(SIZE/2, SIZE/2);
            this.bounds.setRotation(rotation);
        }
        this.bounds.setPosition(pos.x, pos.y);
        this.maxDurability = maxDurability;
        this.durability = maxDurability;
    }

    //for beds
    public Block(Vector2 pos, double maxDurability, float size, int rotation, BlockType blockType) {
        this.position = pos;
        if (size == 1) {
            this.bounds = new Polygon(new float[]{0, 0, 0, SIZE, size, SIZE, size, 0});
        } else if (size == 2) {
            this.bounds = new Polygon(new float[]{0, 0, 0, SIZE, size, SIZE, size, 0});
            this.bounds.setOrigin(SIZE/2, SIZE/2);
            this.bounds.setRotation(rotation);
        }
        this.bounds.setPosition(pos.x, pos.y);
        this.maxDurability = maxDurability;
        this.durability = maxDurability;
        this.blockType = blockType;
        this.colibible = false;
    }

    public Block(Vector2 pos, double maxDurability, float width, float height) {
        this.position = pos;
        this.bounds = new Polygon(new float[]{0, 0, width, height, 0, width, height, 0});
        this.bounds.setPosition(pos.x, pos.y);
        this.maxDurability = maxDurability;
        this.durability = maxDurability;

    }

    public Vector2 getPosition() {
        return position;
    }

    public Polygon getBounds() {
        return bounds;
    }

    public void setBounds(Polygon bounds) {
        this.bounds = bounds;
    }

    public double getDurability() {
        return durability;
    }

    public void setDurability(double durability) {
        this.durability = durability;
    }

    public double getMaxDurability() {
        return maxDurability;
    }

    public void setMaxDurability(double maxDurability) {
        this.maxDurability = maxDurability;
    }

    public int hit() {
        //do nothing
        return 0;
    }

    public boolean decreaseDurability(double hit) {
        if (durability <= 0) return false;

        durability = durability - hit;
        if (durability < 0) {
            durability = 0;
        }
        return true;
    }

    public boolean increaseDurability(double hit) {
        if (durability >= maxDurability) return false;

        durability = durability + hit;
        if (durability > maxDurability) {
            durability = maxDurability;
        }
        return true;
    }

    public boolean isColibible() {
        return colibible;
    }

    public void setColibible(boolean colibible) {
        this.colibible = colibible;
    }

    public BlockType getBlockType() {
        return blockType;
    }

    public void setBlockType(BlockType blockType) {
        this.blockType = blockType;
    }
}
