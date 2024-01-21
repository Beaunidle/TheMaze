package com.mygdx.game.model.pads;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.model.environment.blocks.Irrigation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FloorPad extends Pad {

    public enum Type {
        SPIKE, SLIME, MOVE, WATER, WATERFLOW, IRRIGATION
    }

    public enum Connection {
        N,E,S,W
    }

    private final Timer.Task notifyTimer;

    private final Type type;
    private int rotation;
    private boolean connected;
    private List<Connection> connections;
    private boolean notify, notifyTiming;
    private boolean straightPiece = true;

    public FloorPad(String name, Vector2 pos, Type type) {
        super(name, pos);
        this.type = type;
        notifyTimer = new Timer.Task() {
            @Override
            public void run() {
                notifyTimerStop();
            }
        };
    }

    public FloorPad(String name, Vector2 pos, Type type, int rotation) {
        super(name, pos);
        this.type = type;
        this.rotation = rotation;
        this.connected = false;

        if (type == Type.IRRIGATION) {
            if (rotation == 90 || rotation == 270) {
                straightPiece = true;
                connections = new ArrayList<>(Arrays.asList(Connection.N,Connection.S));
            } else if (rotation == 0 || rotation == 180) {
                straightPiece = true;
                connections = new ArrayList<>(Arrays.asList(Connection.E,Connection.W));
            } else if (rotation == 45) {
                straightPiece = false;
                connections = new ArrayList<>(Arrays.asList(Connection.E,Connection.S));
            } else if (rotation == 135) {
                straightPiece = false;
                connections = new ArrayList<>(Arrays.asList(Connection.N,Connection.E));
            } else if (rotation == 225) {
                straightPiece = false;
                connections = new ArrayList<>(Arrays.asList(Connection.N,Connection.W));
            } else if (rotation == 315) {
                straightPiece = false;
                connections = new ArrayList<>(Arrays.asList(Connection.W,Connection.S));
            }
        }
        notifyTimer = new Timer.Task() {
            @Override
            public void run() {
                notifyTimerStop();
            }
        };
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

    public boolean isStraightPiece() {
        return straightPiece;
    }

    private void notifyTimerStop() {
        notifyTimer.cancel();
        notify = true;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public void startNotify(float time) {
        Timer.schedule(notifyTimer, time);
    }

    public void checkConnections(List<FloorPad> floorPads) {
        for (FloorPad floorPad : floorPads) {
            switch (floorPad.getType()) {
                case WATER:
                case WATERFLOW:
                    boolean connected = false;
                    for (FloorPad.Connection connection : getConnections()) {
                        if (connection.equals(FloorPad.Connection.N) && floorPad.getPosition().y == getPosition().y + 1 && floorPad.getPosition().x == getPosition().x) {
                            connected = true;
                            break;
                        }
                        if (connection.equals(FloorPad.Connection.S) && floorPad.getPosition().y == getPosition().y - 1 && floorPad.getPosition().x == getPosition().x) {
                            connected = true;
                            break;
                        }
                        if (connection.equals(FloorPad.Connection.E) && floorPad.getPosition().x == getPosition().x + 1 && floorPad.getPosition().y == getPosition().y) {
                            connected = true;
                            break;
                        }
                        if (connection.equals(FloorPad.Connection.W) && floorPad.getPosition().x == getPosition().x - 1 && floorPad.getPosition().y == getPosition().y) {
                            connected = true;
                            break;
                        }
                    }
                    if (connected) {
                        setConnected(true);
                        startNotify(3F);
                    }
                    break;
                case IRRIGATION:
                    if (floorPad.isConnected()) {
                        connected = false;
                        for (FloorPad.Connection connection : getConnections()) {
                            if (connection.equals(FloorPad.Connection.N) && floorPad.getConnections().contains(FloorPad.Connection.S) && floorPad.getPosition().y == getPosition().y + 1 && floorPad.getPosition().x == getPosition().x) {
                                connected = true;
                                break;
                            }
                            if (connection.equals(FloorPad.Connection.S) && floorPad.getConnections().contains(FloorPad.Connection.N) && floorPad.getPosition().y == getPosition().y - 1 && floorPad.getPosition().x == getPosition().x) {
                                connected = true;
                                break;
                            }
                            if (connection.equals(FloorPad.Connection.E) && floorPad.getConnections().contains(FloorPad.Connection.W) && floorPad.getPosition().x == getPosition().x + 1 && floorPad.getPosition().y == getPosition().y) {
                                connected = true;
                                break;
                            }
                            if (connection.equals(FloorPad.Connection.W) && floorPad.getConnections().contains(FloorPad.Connection.E) && floorPad.getPosition().x == getPosition().x - 1 && floorPad.getPosition().y == getPosition().y) {
                                connected = true;
                                break;
                            }
                        }
                        if (connected) {
                            setConnected(true);
                            startNotify(3F);
                        }
                    }
                    break;
            }
        }
    }

    public String getName() {
        StringBuilder padString = new StringBuilder();
        padString.append(super.getName());
        if (type.equals(Type.IRRIGATION)) {
            if (!isStraightPiece()) padString.append("Bend");
            if (isConnected()) padString.append("-full");
            else padString.append("-empty");
        }
        if (type.equals(Type.WATERFLOW)) {
            Random rand = new Random();
            int num = rand.nextInt(2) + 1;
            padString.append(1);
        }
        if (type.equals(Type.WATER)) {
            Random rand = new Random();
            int num = rand.nextInt(3) + 1;
            padString.append(1);
        }
        return padString.toString();
    }
}
