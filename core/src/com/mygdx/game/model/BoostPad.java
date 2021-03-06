package com.mygdx.game.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

import java.util.Random;

public class BoostPad extends Pad {

    private Player.Boost boost;
    private Timer.Task chargeTimer;


    public BoostPad(Vector2 position) {
        super(position);
        chargeTimer = new Timer.Task() {
            @Override
            public void run() {
                chooseBoost();
                chargeTimer.cancel();
            }
        };
        Timer.schedule(chargeTimer, 5);
    }

    public Player.Boost getBoost() {
        return boost;
    }

    public Player.Boost collectBoost() {
        Player.Boost temp = boost;
        boost = null;
        chargeTimer = new Timer.Task() {
            @Override
            public void run() {
                chooseBoost();
                chargeTimer.cancel();
            }
        };
        Timer.schedule(chargeTimer, 5);
        return temp;
    }

    private void chooseBoost() {
        Random rand = new Random();
        switch (rand.nextInt(4)) {
            case 0:
                boost = Player.Boost.HOMING;
                break;
            case 1:
                boost = Player.Boost.SPEED;
                break;
            case 2:
                boost = Player.Boost.SHIELD;
                break;
            case 3:
                boost = Player.Boost.DAMAGE;
                break;
        }
    }
}
