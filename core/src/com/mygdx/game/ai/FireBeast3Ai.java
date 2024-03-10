package com.mygdx.game.ai;

import com.mygdx.game.model.moveable.AIPlayer;
import com.mygdx.game.model.moveable.Animal;
import com.mygdx.game.model.moveable.Player;
import com.mygdx.game.model.moveable.Projectile;
import com.mygdx.game.model.moveable.Sprite;

import java.util.ArrayList;
import java.util.List;

public class FireBeast3Ai extends AnimalAi {
    public List<Projectile> decide(float delta, Sprite s, boolean nightTime, Player player, List<AIPlayer> aiPlayers, List<Animal> animals) {
        chooseTarget(s, player, aiPlayers, animals);

        if (s.getTarget() != null) {
            if (moveToTarget(s, 7F, s.getTarget(), true, true)) {
                if (!s.isUseDelayOn()) {
                    s.startUseDelayTimer(3F);
                    s.hitPhaseIncrease(1);
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
