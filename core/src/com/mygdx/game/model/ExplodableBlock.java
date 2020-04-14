package com.mygdx.game.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

public class ExplodableBlock extends Block {

    public enum State {
        RED, YELLOW, BANG, RUBBLE
    }

    int tickCount = 0;
    boolean exploding = false, hit = false;
    private State state;
    private Timer.Task delay = new Timer.Task() {
        @Override
        public void run() {
            beginExplosion();
        }
    };
    private Timer.Task count = new Timer.Task() {
        @Override
        public void run() {
            bang();
        }
    };

    public ExplodableBlock(Vector2 pos) {
        super(pos);
        state = State.RED;
    }

    public void explode(float delayTime) {
        if (!hit) {
            hit = true;
            Timer.schedule(delay, delayTime, 0);
        }
    }

    private void beginExplosion() {
        delay.cancel();
        if (!exploding) {
            exploding = true;
            Timer.schedule(count, 4, 2);
        }
        if (state.equals(State.RED)) {
            state = State.YELLOW;
        } else if (state.equals(State.YELLOW)) {
            state = State.RED;
        }
        Timer.schedule(delay, 0.25F, 0.25F);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public boolean isExploding() {
        return exploding;
    }

    public void bang() {
        delay.cancel();
        count.cancel();
        state = State.BANG;
    }

    private void rubble() {
        state = State.RUBBLE;
    }
}
