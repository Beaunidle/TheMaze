package com.mygdx.game.model.moveable;

import static com.mygdx.game.model.items.Material.Type.BONE;
import static com.mygdx.game.model.items.Material.Type.BONEFRAGMENT;
import static com.mygdx.game.model.items.Material.Type.SCAPULA;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.ai.AnimalAi;
import com.mygdx.game.model.environment.AnimalSpawn;
import com.mygdx.game.model.environment.SpawnPoint;
import com.mygdx.game.model.environment.blocks.EnvironmentBlock;
import com.mygdx.game.model.items.Material;
import com.mygdx.game.model.items.Swingable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Animal extends Sprite {

    public enum AnimalType {
        COW,SPIDER
    }

    private final AnimalType animalType;
    private final AnimalAi ai;
    private final AnimalSpawn spawn;
    private final String damageName;

    public Animal(Vector2 position, String name, AnimalSpawn spawn, AnimalType animalType, float width, float height, float rotationSpeed, AnimalAi animalAi, int lives, int food, int water, int count, boolean child) {
        super(position, width, height, lives, food, water, name);
        this.animalType = animalType;
        this.ai = animalAi;
        this.spawn = spawn;
        setChild(child);
        switch (animalType) {
            case COW:
                damageName = "cow" + count;
                setHitTime(1F);
                break;
            case SPIDER:
                damageName = "spider" + count;
                setHitTime(0.1F);
                break;
            default:
                damageName = "";
        }
        updateHitCircle();
        setRotationSpeed(rotationSpeed);
    }

    public List<Projectile> decide(float delta, boolean nightTime, Player player, List<AIPlayer> aiPlayers, List<Animal> animals) {
        return ai.decide(delta, this, nightTime, player, aiPlayers, animals);
    }

    public void die() {
        spawn.setPopulation(spawn.getPopulation() - 1);
    }

    public Vector2 getSpawn() {
        return spawn.getPosition();
    }

    public AnimalSpawn getSpawnPoint() { return spawn; }

    public String getName() {
        StringBuilder animalString = new StringBuilder();
        animalString.append(super.getName());
        if (getState().equals(State.HIDING)) animalString.append("-hiding");
        return animalString.toString();
    }

    public String getDamageName() {
        return damageName;
    }

    public EnvironmentBlock getBody() {
        Vector2 pos = getCentrePosition();
        Map<Material, Integer> secondaryMaterials = new HashMap<>();
        String name = "";
        EnvironmentBlock blockToReturn = null;
        switch (animalType) {
            case COW:
                secondaryMaterials.put(new Swingable(Swingable.SwingableType.CLUB, 50, new Material(BONE, 1)), 9);
                secondaryMaterials.put(new Material(SCAPULA, 1), 9);
                secondaryMaterials.put(new Material(BONEFRAGMENT, 1), 6);
                name = "body-cow";
                blockToReturn = new EnvironmentBlock(new Vector2((float)Math.floor(pos.x), (float)Math.floor(pos.y)), new Material(Material.Type.MEAT, 1), secondaryMaterials, 2, 0, 1, 0,false, null, isChild() ? 5: 10, name, getWidth(), getHeight());
                break;
            case SPIDER:
                name="body-spider";
                blockToReturn =  new EnvironmentBlock(new Vector2((float)Math.floor(pos.x), (float)Math.floor(pos.y)), new Material(Material.Type.MEAT, 1), secondaryMaterials, 2, 0, 1, 0, false, null, 1, name, getWidth(), getHeight());
                break;
        }
        return blockToReturn;
    }

    public AnimalType getAnimalType() {
        return animalType;
    }

    public void updateHitCircle() {
        getHitCircle().setRadius(0.5f);

        if (getHitPhase() == 0 || getTarget() == null) getHitCircle().setRadius(0.0001F);
        Vector2 gridRef = getCentrePosition();
        float rotation = getRotation();

        if (rotation < 0) rotation = rotation + 360;
        if (rotation > 360) rotation = rotation - 360;
        float x = gridRef.x + (float)(getWidth() * Math.cos(rotation * Math.PI/180));
        float y = gridRef.y + (float)(getHeight() * Math.sin(rotation * Math.PI/180));
        getHitCircle().setPosition(new Vector2(x, y));
    }

    public AnimalAi getAi() {
        return ai;
    }
}
