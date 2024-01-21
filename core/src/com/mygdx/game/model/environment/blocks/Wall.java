package com.mygdx.game.model.environment.blocks;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.items.Material;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wall extends Block{

//    private int rotation;
    private final Map<Float, WallType> walls;
    private boolean wallFull;

    public Wall(Vector2 pos, int rotation, float width, float height, boolean door) {
        super(pos, 20);
        setBlockType(BlockType.WALL);
        walls = new HashMap<>();
        walls.put(90f, null);
        walls.put(180f, null);
        walls.put(270f, null);
        walls.put(0f, null);
        addWall(width, height, rotation, door);
//        setBounds(new Polygon(new float[]{0, 0, width, height, width, 0, 0, height}));
//        this.getBounds().setPosition(pos.x, pos.y);
//        this.rotation = rotation;
    }

    public void addWall(float width, float height, float rotation, boolean door) {

        Polygon polygon;
        if (rotation >= 45 && rotation < 135) {
            polygon = new Polygon(new float[]{0, 0, -height, 0, -height, width, 0, width});
            polygon.setPosition(getPosition().x + 1, getPosition().y);
        } else if (rotation >= 135 && rotation < 225) {
            polygon = new Polygon(new float[]{0, 0, -width, 0, -width, -height, 0, -height});
            polygon.setPosition(getPosition().x + 1, getPosition().y + 1);
        } else if (rotation >= 225 && rotation < 315) {
            polygon = new Polygon(new float[]{0, 0,  height, 0, height, -width, 0, -width});
            polygon.setPosition(getPosition().x, getPosition().y + 1);
        } else { //if (rotation < 45 || rotation >= 315) {
            polygon = new Polygon(new float[]{0, 0, width, 0, width, height, 0, height});
            polygon.setPosition(getPosition().x, getPosition().y);
        }
        walls.put(rotation, new WallType(door, polygon));
    }

    public List<Material> hit() {
        if (getDurability() > 0) decreaseDurability(1);
        return new ArrayList<>();

    }

//    public int getRotation() {
//        return rotation;
//    }

//    public void setRotation(int rotation) {
//        this.rotation = rotation;
//    }

    public boolean isWallFull(float rotation) {
        return walls.get(rotation) != null;
    }

    public Map<Float, WallType> getWalls() {
        return walls;
    }

    public static class WallType {

        private final boolean door;
        private boolean open;
        private Polygon bounds;

        public WallType(boolean door, Polygon bounds) {
            this.door = door;
            this.bounds = bounds;
            open = false;
        }

        public boolean isDoor() {
            return door;
        }

        public boolean isOpen() {
            return open;
        }

        public void setOpen(boolean open) {
            this.open = open;
        }

        public Polygon getBounds() {
            return bounds;
        }

        public void setBounds(Polygon bounds) {
            this.bounds = bounds;
        }

        public void toggleOpen() {

            open = !open;
        }

        public String getName() {
            if (isDoor()) {
                if (isOpen()) {
                    return "dooropen";
                } else {
                    return "door";
                }
            } else {
                return "wall";
            }
        }
    }

}
