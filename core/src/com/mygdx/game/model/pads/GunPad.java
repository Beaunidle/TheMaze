package com.mygdx.game.model.pads;

import static com.mygdx.game.model.items.Ranged.RangedType.PISTOL;
import static com.mygdx.game.model.items.Ranged.RangedType.ROCKET;
import static com.mygdx.game.model.items.Ranged.RangedType.SHOTGUN;
import static com.mygdx.game.model.items.Ranged.RangedType.SMG;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.items.Ranged;

public class GunPad extends Pad {

    private final Ranged.RangedType type;

    public GunPad(Vector2 pos, Ranged.RangedType type) {
        super(type.equals(PISTOL) ? "gunPistol" : type.equals(SMG) ? "gunSMG" : type.equals(SHOTGUN) ? "gunShotgun" :  type.equals(ROCKET) ? "gunRocket" : "", pos);
        this.type = type;
    }

    public Ranged.RangedType getType() {
        return type;
    }

    public String getName() {
        return super.getName();
    }
}
