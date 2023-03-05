package com.mygdx.game.ai;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.items.Swingable;
import com.mygdx.game.model.moveable.AIPlayer;
import com.mygdx.game.model.moveable.Projectile;
import com.mygdx.game.model.moveable.Player;
import com.mygdx.game.model.moveable.Sprite;

import java.util.ArrayList;
import java.util.List;

public class PlayerAi extends BaseAi{

    public List<Projectile> decide(float delta, Sprite s) {
        AIPlayer sprite = (AIPlayer) s;
        List<Projectile> projectiles = new ArrayList<>();

        if (sprite.getState().equals(Sprite.State.DEAD)) return projectiles;

        if (sprite.getTarget() != null) {
            if (sprite.getViewCircle().contains(sprite.getTarget())) {
                sprite.setIntent(AIPlayer.Intent.HOMING);
                float dst = sprite.getTarget().dst(sprite.getCentrePosition());
                float deg = locator.getAngle(new Vector2(sprite.getTarget()).sub(sprite.getCentrePosition()));
                if (-20 < (deg - sprite.getRotation())  && (deg - sprite.getRotation()) < 20) {
                    if (dst > 1) {
                        sprite.moveForward();
                    } else {
                        if (sprite.getStrongHand() != null) {
                            if (sprite.getStrongHand() instanceof Swingable && ((Swingable) sprite.getStrongHand()).getSwingableType().equals(Swingable.SwingableType.SWORD)) {
                                sprite.setIntent(AIPlayer.Intent.KILLING);
                            }
                        }
                        sprite.stop();
                    }
                    if (Math.random() > 0.695) {
//                        bullets.addAll(fireBullet(getRotation()));
                    }
                } else {
                    sprite.stop();
                    int random = rand.nextInt(100);
                    if (random > 94) sprite.moveForward();
                }

                if (locator.locate(deg, sprite.getRotation()) < 0) {
//                    sprite.rotateAntiClockwise(delta);
                    sprite.setTurningAntiClockwise(true);
                    sprite.setTurningClcokwise(false);
                } else if (locator.locate(deg, sprite.getRotation()) > 0) {
//                    sprite.rotateClockwise(delta);
                    s.setTurningAntiClockwise(false);
                    s.setTurningClcokwise(true);
                } else {
                    s.setTurningAntiClockwise(false);
                    s.setTurningClcokwise(false);
                    sprite.stop();
                }
            } else {
                sprite.setTarget(null);
            }
        } else {
            checkRotateBy(sprite, delta);
//            if (sprite.getRotateBy() != 0) {
//                if (sprite.getRotateBy() < 0) {
//                    sprite.rotateAntiClockwise(delta);
//                    sprite.setRotateBy(sprite.getRotateBy()+1);
//                }
//                if (sprite.getRotateBy() > 0) {
//                    sprite.rotateClockwise(delta);
//                    sprite.setRotateBy(sprite.getRotateBy()-1);
//                }
//            }
            sprite.setIntent(AIPlayer.Intent.SEARCHING);
        }

        if (sprite.getIntent().equals(AIPlayer.Intent.SEARCHING)) {
            sprite.setState(Player.State.MOVING);
            int random = rand.nextInt(10);

            if (random > 2) {
                sprite.moveForward();
            } else {
                sprite.stop();
            }

            random = rand.nextInt(1000);
            if (random >= 980 && random < 990) {
//                sprite.rotateClockwise(delta);
                sprite.setTurningAntiClockwise(true);
                sprite.setTurningClcokwise(false);
            } else if (random >= 990) {
//                sprite.rotateAntiClockwise(delta);
                sprite.setTurningAntiClockwise(false);
                sprite.setTurningClcokwise(true);
            } else {
                sprite.setTurningAntiClockwise(false);
                sprite.setTurningClcokwise(false);
            }
        }
//        bullets.removeAll(bullets);
        return projectiles;
    }
}
