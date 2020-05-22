package com.mygdx.game.model;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

public class Explosion {

    static final float SIZE = 4f;

    private Vector2 position = new Vector2();
    private Polygon bounds = new Polygon();
    private boolean finished = false;
    private Timer.Task exploding = new Timer.Task() {
        @Override
        public void run() {
            finish();
        }
    };
    private String name;

    public static float getSIZE() {
        return SIZE;
    }

    public Explosion(Vector2 pos, String name) {
        this.position = pos;
        this.bounds = new Polygon(new float[]{0, 0, SIZE, SIZE, 0, SIZE, SIZE, 0});
        this.bounds.setPosition(pos.x, pos.y);
        Timer.schedule(exploding, 2, 2);
        this.name = name;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Polygon getBounds() {
        return bounds;
    }

    public String getName() {
        return name;
    }

    public boolean isFinished() {
        return finished;
    }

    private void finish() {
        finished = true;
        exploding.cancel();
    }
}
