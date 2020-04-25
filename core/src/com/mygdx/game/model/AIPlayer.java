package com.mygdx.game.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;

public class AIPlayer extends Player {

    public enum Intent {
        SEARCHING, HOMING, KILLING, FLEEING
    }

    private Intent intent = Intent.SEARCHING;

    public AIPlayer(Vector2 position, String name) {
        super(position, name, 20);
        Timer.Task intentTimer = new Timer.Task() {
            @Override
            public void run() {
                switchIntent();
            }
        };
        Timer.schedule(intentTimer, 2, 2);
        switch ((int)Math.floor(Math.random() * Math.floor(4))) {
            case 0:
                this.getGun().setType(Gun.Type.PISTOL);
                break;
            case 1:
                this.getGun().setType(Gun.Type.SMG);
                break;
            case 2:
                this.getGun().setType(Gun.Type.SHOTGUN);
                break;
            case 3:
                this.getGun().setType(Gun.Type.ROCKET);
                break;
        }
    }

    private void switchIntent() {
        if (intent.equals(Intent.SEARCHING)) {
            intent = Intent.HOMING;
        } else if (intent.equals(Intent.HOMING)) {
            intent = Intent.SEARCHING;
        }
    }

    public Intent getIntent() {
        return intent;
    }
}
