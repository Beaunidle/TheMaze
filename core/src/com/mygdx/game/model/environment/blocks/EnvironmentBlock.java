package com.mygdx.game.model.environment.blocks;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.items.Consumable;
import com.mygdx.game.model.items.Material;
import com.mygdx.game.model.items.Swingable;
import com.mygdx.game.model.items.Throwable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EnvironmentBlock extends Block{

    private final int returnPerHit;
    private final Material material;
    private final Map<Material, Integer> secondaryMaterials;
    private final Swingable.SwingableType weakness;
    private final long replenishTime;
    private long lastReplenished;
    private final long degradeTime;
    private long lastDegraded;
    private final long spreadTime;
    private long lastSpread;
    private boolean wantToSpread = false;

    public EnvironmentBlock(Vector2 pos, Material material, Map<Material, Integer> secondaryMaterials, int returnPerHit, int replenishTime, int degradeTime, int spreadTime, boolean colidible, Swingable.SwingableType swingableType, double maxDurability, String name, float width, float height) {
        super(pos, maxDurability, width, height, 0, name);
        this.material = material;
        this.secondaryMaterials = secondaryMaterials;
        this.returnPerHit = returnPerHit;
        this.weakness = swingableType;
        this.replenishTime = replenishTime;
        this.lastReplenished = 0;
        this.degradeTime = degradeTime;
        this.lastDegraded = 0;
        this.spreadTime = spreadTime;
        this.lastSpread = System.currentTimeMillis();
        setColibible(colidible);
        setBlockType(BlockType.ENVIRONMENT);
        material.setQuantity(returnPerHit);
    }

    public List<Material> hit(Swingable swingable) {
        List<Material> materials = new ArrayList<>();
        if (getDurability() <=0) return materials;

        if (material.isMineable()) {
            material.setQuantity(0);
            if (swingable != null) {
                material.setQuantity(weakness == null || swingable.getSwingableType().equals(weakness) ? returnPerHit : 1);
                if (secondaryMaterials != null && !secondaryMaterials.isEmpty()) {
                    checkSecondaries(materials);
                }
            } else if (material.isMineableByHand()) {
                material.setQuantity(1);
            }
        }
        if (material.getQuantity() > 0) {
            materials.add(getMaterial());
            decreaseDurability(1);
        }
        return materials;
    }

    public Material getMaterial() {
        return castMaterial(material);
    }

    private void checkSecondaries(List<Material> materials) {
        for(Iterator<Map.Entry<Material, Integer>> it = secondaryMaterials.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<Material, Integer> entry = it.next();
            Random rand = new Random();
            if (rand.nextInt(10) > entry.getValue()) {
                materials.add(castMaterial(entry.getKey()));
                it.remove();
            }
        }
    }

    private Material castMaterial(Material materialToCast) {
        if (materialToCast instanceof Throwable) {
            return new Throwable(((Throwable) materialToCast).getThrowableType(), materialToCast.getQuantity());
        }
        if (materialToCast instanceof Consumable) {
            return new Consumable(((Consumable) materialToCast).getConsumableType(), materialToCast.getQuantity());
        }
        if (materialToCast instanceof Swingable) {
            return new Swingable(((Swingable) materialToCast).getSwingableType(), materialToCast.getQuantity(), ((Swingable) materialToCast).getMaterial());
        }
        return new Material(materialToCast.getType(), materialToCast.getQuantity());
    }

    public void replenish(long gameTime) {
        if ((replenishTime > 0 && getDurability() < getMaxDurability()) && (lastReplenished == 0 || (gameTime - lastReplenished)/1000 > replenishTime)) {
            increaseDurability(1);
            lastReplenished = gameTime;
        }
    }

    public void degrade(long gameTime) {
        if (degradeTime > 0 && getDurability() > 0 && (lastDegraded == 0 || (gameTime - lastDegraded)/1000 > degradeTime)) {
            decreaseDurability(1);
            lastDegraded = gameTime;
        }
    }

    public void checkSpread(long gameTime) {
        if (spreadTime > 0 && !wantToSpread && (gameTime - lastSpread)/1000 > spreadTime) {
            wantToSpread = true;
        }
    }

    public boolean isWantToSpread() {
        return wantToSpread;
    }

    public void setWantToSpread(boolean wantToSpread) {
        this.wantToSpread = wantToSpread;
    }

    public long getReplenishTime() {
        return replenishTime;
    }

    public void setLastSpread(long lastSpread) {
        this.lastSpread = lastSpread;
    }
}
