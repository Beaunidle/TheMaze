package com.mygdx.game.model.pads;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.environment.blocks.Irrigation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FloorPad extends Pad {

    public enum Type {
        SPIKE, SLIME, MOVE, WATER, WATERFLOW, IRRIGATION;
    }

    public enum Connection {
        N,E,S,W
    }

    private final Type type;
    private int rotation;
    private boolean connected;
    private List<Connection> connections;

    public FloorPad(Vector2 pos, Type type) {
        super(pos);
        this.type = type;
    }

    public FloorPad(Vector2 pos, Type type, int rotation) {
        super(pos);
        this.type = type;
        this.rotation = rotation;
        this.connected = false;

        if (rotation == 90 || rotation == 270) {
            connections = new ArrayList<>(Arrays.asList(Connection.N,Connection.S));
        } else if (rotation == 0 || rotation == 180) {
            connections = new ArrayList<>(Arrays.asList(Connection.E,Connection.W));
        } else if (rotation == 45) {
            connections = new ArrayList<>(Arrays.asList(Connection.E,Connection.S));
        } else if (rotation == 135) {
            connections = new ArrayList<>(Arrays.asList(Connection.N,Connection.E));
        } else if (rotation == 225) {
            connections = new ArrayList<>(Arrays.asList(Connection.N,Connection.W));
        } else if (rotation == 315) {
            connections = new ArrayList<>(Arrays.asList(Connection.E,Connection.S));
        }
    }

    public Type getType() {
        return type;
    }

    public int getRotation() {
        return rotation;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
