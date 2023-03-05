package com.mygdx.game.ai;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.moveable.Projectile;
import com.mygdx.game.model.moveable.Sprite;
import com.mygdx.game.utils.Locator;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

public class BaseAi {

    Random rand = new Random();
    Locator locator = new Locator();
    List<Vector2> targetCoordinates;
    Set<Vector2> dontGoThere;


    List<Projectile> decide(float delta, Sprite sprite) {
        return null;
    }

    public void checkRotateBy(Sprite s, float delta) {
        if (s.getRotateBy() < 0) {
//            s.rotateAntiClockwise(delta);
//            s.setRotateBy(s.getRotateBy()+1);
            s.setTurningAntiClockwise(true);
            s.setTurningClcokwise(false);
        } else if (s.getRotateBy() > 0) {
//            s.rotateClockwise(delta);
//            s.setRotateBy(s.getRotateBy()-1);
            s.setTurningClcokwise(true);
            s.setTurningAntiClockwise(false);
        } else {
            s.setTurningClcokwise(false);
            s.setTurningAntiClockwise(false);
        }
    }

    public List<Vector2> getTargetCoordinates() {
        return targetCoordinates;
    }

    public void setTargetCoordinates(List<Vector2> targetCoordinates) {
        this.targetCoordinates = targetCoordinates;
    }

    public Set<Vector2> getDontGoThere() {
        return dontGoThere;
    }

    public void setDontGoThere(Set<Vector2> dontGoThere) {
        this.dontGoThere = dontGoThere;
    }

    public Locator getLocator() {
        return locator;
    }
}
