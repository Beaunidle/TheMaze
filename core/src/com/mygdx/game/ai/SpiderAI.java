package com.mygdx.game.ai;

import static com.mygdx.game.model.environment.AreaAffect.AffectType.DAMAGE;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.environment.blocks.Block;
import com.mygdx.game.model.items.Swingable;
import com.mygdx.game.model.moveable.AIPlayer;
import com.mygdx.game.model.moveable.Animal;
import com.mygdx.game.model.moveable.Player;
import com.mygdx.game.model.moveable.Projectile;
import com.mygdx.game.model.moveable.Sprite;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@SuppressWarnings("NewApi")
public class SpiderAI extends AnimalAi {

    private String shiftDirection;

    public List<Projectile> decide(float delta, Sprite s, boolean nightTime, Player player, List<AIPlayer> aiPlayers, List<Animal> animals) {
        s.updateHitCircle();
        if (!nightTime) {
            s.setIntent(AIPlayer.Intent.SEARCHING);
            s.setState(Sprite.State.HIDING);
            s.setAcceleration(0F);
            return null;
        }
        chooseTarget(s, player, aiPlayers, animals);
        List<Animal> spiders = animals.stream().filter(a -> a.getAnimalType().equals(Animal.AnimalType.SPIDER)).collect(Collectors.toList());
        for (Animal spider : spiders) {
            if (s != spider) {
                if (spider.getBounds().getBoundingRectangle().overlaps(s.getBounds().getBoundingRectangle())) {
                    Random rand = new Random();
                    if (shiftDirection == null) shiftDirection = rand.nextInt(3) > 1 ? "left" : "right";
                    if (shiftDirection.equals("left"))  {
                        s.moveLeft();
                    } else {
                        s.moveRight();
                    }
                } else {
                    shiftDirection = null;
                }
            }
        }
        if (s.getTarget() != null) {
            if (moveToTarget(s, 1, s.getTarget(), true, true)) {
                if (!s.isUseDelayOn()) {
//                        s.setIntent(AIPlayer.Intent.KILLING);
                    s.startUseDelayTimer(2F);
                    s.hitPhaseIncrease(1);
                    s.stop();
                }
            };
            return null;
        } else {
            s.setIntent(AIPlayer.Intent.SEARCHING);
        }
        return super.decide(delta, s, nightTime, player, aiPlayers, animals);
    }
}
