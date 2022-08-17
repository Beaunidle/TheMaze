package com.mygdx.game.ai;

import com.mygdx.game.model.moveable.Projectile;
import com.mygdx.game.model.moveable.Sprite;
import com.mygdx.game.utils.Locator;

import java.util.List;
import java.util.Random;

public class BaseAi {

    Random rand = new Random();
    Locator locator = new Locator();


    List<Projectile> decide(float delta, Sprite sprite) {
        return null;
    }

    public void checkRotateBy(Sprite s, float delta) {
        if (s.getRotateBy() < 0) {
            s.rotateAntiClockwise(delta);
            s.setRotateBy(s.getRotateBy()+1);
        }
        if (s.getRotateBy() > 0) {
            s.rotateClockwise(delta);
            s.setRotateBy(s.getRotateBy()-1);
        }
    }
}
