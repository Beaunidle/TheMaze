package com.mygdx.game.model.pads;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.model.moveable.Player;
import com.mygdx.game.model.moveable.Sprite;

import java.util.Random;

public class BoostPad extends Pad {

    private Sprite.Effect boost;
    private Timer.Task chargeTimer;


    public BoostPad(String name, Vector2 position) {
        super(name, position);
        chargeTimer = new Timer.Task() {
            @Override
            public void run() {
                chooseBoost();
                chargeTimer.cancel();
            }
        };
        Timer.schedule(chargeTimer, 5);
    }

    public Sprite.Effect getBoost() {
        return boost;
    }

    public Sprite.Effect collectBoost() {
        Sprite.Effect temp = boost;
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
                boost = Sprite.Effect.HOMING;
                break;
            case 1:
                boost = Sprite.Effect.SPEED;
                break;
            case 2:
                boost = Sprite.Effect.SHIELD;
                break;
            case 3:
                boost = Sprite.Effect.DAMAGE;
                break;
        }
    }

    public String getName() {
        switch (getBoost()) {
            case HOMING:
                return "homingBoost";
            case SPEED:
                return "speedBoost";
            case DAMAGE:
                return "damageBoost";
            case SHIELD:
                return "shieldBoost";
            default:
                return "";
        }
    }
}
