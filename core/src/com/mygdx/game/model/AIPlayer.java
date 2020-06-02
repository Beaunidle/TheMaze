package com.mygdx.game.model;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.utils.Locator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AIPlayer extends Player {

    public enum Intent {
        SEARCHING, HOMING, KILLING, FLEEING
    }

    private Intent intent = Intent.SEARCHING;
    private Random rand = new Random();
    private Player target;
    private int rotateBy = 0;
    Locator locator;

    public AIPlayer(Vector2 position, String name) {
        super(position, name, 20);
        locator = new Locator();
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
            intent = Intent.KILLING;
        } else if (intent.equals(Intent.KILLING)) {
            intent = Intent.SEARCHING;
        }
    }

    private Intent getIntent() {
        return intent;
    }

    private void setIntent(Intent intent) {
        this.intent = intent;
    }

    public int getRotateBy() {
        return rotateBy;
    }

    public void setRotateBy(int rotateBy) {
        this.rotateBy = rotateBy;
    }

    public Vector2 chooseTarget(Player player, List<AIPlayer> aiPlayers) {

        List<Player> players = new ArrayList<>();
        if (getViewCircle().contains(player.getCentrePosition())) players.add(player);

        for (AIPlayer aiPlayer : aiPlayers) {
            if (!aiPlayer.getName().equals(getName())) {
                if (getViewCircle().contains(aiPlayer.getCentrePosition()))players.add(aiPlayer);
            }
        }
        if (players.isEmpty()) {
            target = null;
            return null;
        } else {
            if (target != null && rand.nextInt(1000) < 999) return target.getCentrePosition();

            target =  players.get(rand.nextInt(players.size()));
            return target.getCentrePosition();
        }
    }

    public List<Bullet> decide(float delta) {

        List<Bullet> bullets = new ArrayList<>();

        if (rotateBy != 0) {
            if (rotateBy < 0) {
                rotateAntiClockwise(delta);
                rotateBy++;
            }
            if (rotateBy > 0) {
                rotateClockwise(delta);
                rotateBy--;
            }
        }

        if (target != null) {
            if (getViewCircle().contains(target.getCentrePosition())) {
                setIntent(Intent.HOMING);
                Vector2 distance = new Vector2(target.getCentrePosition()).sub(getCentrePosition());
                float dst = target.getCentrePosition().dst(getCentrePosition());
                double rot = Math.atan2(distance.y, distance.x);
                float deg = (float) (rot * (180 / Math.PI));
                if (deg < 0) {
                    deg = 360 - (-deg);
                }
                if (-20 < (deg - getRotation())  && (deg - getRotation()) < 20) {
                    if (dst > 2) {
                        moveForward();
                    } else {
                        stop();
                    }
                    if (Math.random() > 0.695) {
                        bullets.addAll(fireBullet(getRotation()));
                    }
                } else {
                    stop();
                    int random = rand.nextInt(100);
                    if (random > 94) moveForward();
                }

                if (locator.locate(deg, getRotation()) < 0) {
                    rotateAntiClockwise(delta);
                } else if (locator.locate(deg, getRotation()) > 0) {
                    rotateClockwise(delta);
                } else {
                    stop();
                }
            } else {
                target = null;
            }
        } else {
            setIntent(Intent.SEARCHING);
        }

        if (getIntent().equals(Intent.SEARCHING)) {
            {
                setState(Player.State.MOVING);
                int random = rand.nextInt(10);

                if (random > 2) {
                    moveForward();
                } else {
                    stop();
                }

                random = rand.nextInt(10);
                if (random >= 4 && random <= 6) rotateClockwise(delta);
                if (random >= 7 && random <= 9) rotateAntiClockwise(delta);
            }
        }
        return bullets;
    }
}
