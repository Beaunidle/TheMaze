package com.mygdx.game.ai;

import com.mygdx.game.model.moveable.AIPlayer;
import com.mygdx.game.model.moveable.Animal;
import com.mygdx.game.model.moveable.Player;
import com.mygdx.game.model.moveable.Projectile;
import com.mygdx.game.model.moveable.Sprite;

import java.util.List;

public class FireBeast4Ai extends AnimalAi {
    public List<Projectile> decide(float delta, Sprite s, boolean nightTime, Player player, List<AIPlayer> aiPlayers, List<Animal> animals) {
        chooseTarget(s, player, aiPlayers, animals);

        if (s.getTarget() != null) {
            if (moveToTarget(s, 6F, s.getTarget(), true, false)) {
                if (!s.isUseDelayOn()) {
                    if (s.getDodge() == 0) {
                        s.setAcceleration(5F);
                        s.setDodge(100);
                    }
                    s.startUseDelayTimer(6F);
                    s.hitPhaseIncrease(10);
                    s.stop();
                }
            }
            s.updateHitCircle();
            return null;
        } else {
            s.setIntent(AIPlayer.Intent.SEARCHING);
        }
        s.updateHitCircle();
        return super.decide(delta, s, nightTime, player, aiPlayers, animals);
    }
}
