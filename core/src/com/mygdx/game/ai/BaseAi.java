package com.mygdx.game.ai;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.moveable.AIPlayer;
import com.mygdx.game.model.moveable.Animal;
import com.mygdx.game.model.moveable.Player;
import com.mygdx.game.model.moveable.Projectile;
import com.mygdx.game.model.moveable.Sprite;
import com.mygdx.game.utils.Locator;

import java.util.ArrayList;
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

    public void chooseTarget(Sprite s, Player player, List<AIPlayer> aiPlayers, List<Animal> animals) {

        List<Sprite> potentialTargets = new ArrayList<>();
        if (s.getViewCircle().contains(player.getCentrePosition())) potentialTargets.add(player);

        for (AIPlayer aiPlayer : aiPlayers) {
            if (!aiPlayer.getName().equals(s.getName())) {
                if (s.getViewCircle().contains(aiPlayer.getCentrePosition()))potentialTargets.add(aiPlayer);
            }
        }

        for (Animal animal : animals) {
            if (!(s instanceof Animal && ((Animal) s).getAnimalType().equals(animal.getAnimalType())) && !animal.getName().equals(s.getName())) {
                if (s.getViewCircle().contains(animal.getCentrePosition()))potentialTargets.add(animal);
            }
        }

        if (potentialTargets.isEmpty()) {
            s.setTargetSprite(null);
            s.setTarget(null);
//            s.getView().setBlockingWall(new Block[3]);
        } else {
            if (s.getTargetSprite() == null || rand.nextInt(1000) > 998)  {
                s.setTargetSprite(potentialTargets.get(rand.nextInt(potentialTargets.size())));
            }
            s.setTarget(s.getTargetSprite().getCentrePosition());
        }
    }
}
