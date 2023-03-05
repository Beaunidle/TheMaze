package com.mygdx.game.model.environment.blocks;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.GameObject;
import com.mygdx.game.model.items.Material;
import com.mygdx.game.model.items.Swingable;

import java.util.ArrayList;
import java.util.List;

public class Block extends GameObject {

    public enum BlockType {
        ENVIRONMENT,FILLABLE,GROWER,TILLED,WALL,BED,EXPLODABLE
    }
    static final float SIZE = 1f;

    private double durability;
    private double maxDurability;
    private boolean colibible = true;
    private BlockType blockType;

    public static float getSIZE() {
        return SIZE;
    }

    public Block(Vector2 pos, String name) {
        super(name, pos, new Polygon(new float[]{0, 0, SIZE, SIZE, 0, SIZE, SIZE, 0}));
        this.maxDurability = 100;
        this.durability = maxDurability;
    }

    public Block(Vector2 pos, double maxDurability) {
        super("", pos, new Polygon(new float[]{0, 0, SIZE, SIZE, 0, SIZE, SIZE, 0}));
        this.maxDurability = maxDurability;
        this.durability = maxDurability;
    }

    public Block(Vector2 pos, double maxDurability, float width, float height, int rotation, String name) {
        super(name, pos, new Polygon((new float[]{0, 0, width, 0, width, height, 0, height})));
        getBounds().setOrigin(SIZE/2, SIZE/2);
        getBounds().setRotation(rotation);
        this.maxDurability = maxDurability;
        this.durability = maxDurability;
    }

    //for beds
    public Block(Vector2 pos, double maxDurability, float size, int rotation, BlockType blockType) {
        super("bed", pos, new Polygon(new float[]{0, 0, 0, SIZE, size, SIZE, size, 0}));
        if (size == 2) {
            getBounds().setOrigin(SIZE/2, SIZE/2);
            getBounds().setRotation(rotation);
        }
        this.maxDurability = maxDurability;
        this.durability = maxDurability;
        this.blockType = blockType;
        this.colibible = false;
    }

    public Block(Vector2 pos, double maxDurability, float width, float height) {
        super("", pos, new Polygon(new float[]{0, 0, SIZE, SIZE, 0, SIZE, SIZE, 0}));
        this.maxDurability = maxDurability;
        this.durability = maxDurability;

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

    public List<Material> hit(Swingable swingable) {
        //do nothing
        return new ArrayList<>();
    }

    public void decreaseDurability(double hit) {
        if (durability <= 0) return;

        durability = durability - hit;
        if (durability < 0) {
            durability = 0;
        }
    }

    public void increaseDurability(double hit) {
        if (durability >= maxDurability) return;

        durability = durability + hit;
        if (durability > maxDurability) {
            durability = maxDurability;
        }
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
