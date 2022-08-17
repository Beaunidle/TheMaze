package com.mygdx.game.model.environment;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

public class AreaAffect extends Decoration {

    public enum AffectType {
        EXPLOSION, LIGHTNING
    }

    private Circle bounds = new Circle();
    private boolean finished = false;
    private final Timer.Task expireTimer = new Timer.Task() {
        @Override
        public void run() {
            finish();
        }
    };
    private AffectType affectType;

    public AreaAffect(Vector2 pos, String name, float radius, float expires, AffectType type) {
        super(pos, name, radius, radius);
        this.bounds = new Circle(getPosition().x, getPosition().y, radius);
        this.bounds.setPosition(pos.x, pos.y);
        this.affectType = type;
        Timer.schedule(expireTimer, expires);
    }

    public Circle getBounds() {
        return bounds;
    }

    public boolean isFinished() {
        return finished;
    }

    private void finish() {
        finished = true;
        expireTimer.cancel();
    }

    public AffectType getAffectType() {
        return affectType;
    }
}
