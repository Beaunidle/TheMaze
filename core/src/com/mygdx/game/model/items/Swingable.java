package com.mygdx.game.model.items;

public class Swingable extends Item {

    public enum SwingableType{
        PICK,SWORD,HAMMER,AXE,HOE,SHOVEL,CLUB
    }

    private final SwingableType swingableType;
    private final Material material;
    private float damage;

    public Swingable(SwingableType type, double durability, Material material) {
        super(ItemType.SWINGABLE, durability + material.getBaseDurability());
        this.swingableType = type;
        this.material = material;
        damage = 0;
        switch (type) {
            case PICK:
                damage = 1 + material.getBaseDamage();
                setName("inv_pick");
                setUseTime(0.5F);
                setUseDelay(0.5F);
                break;
            case AXE:
                damage = 1 + material.getBaseDamage();
                setName("inv_axe");
                setUseTime(0.5F);
                setUseDelay(0.5F);
                break;
            case SWORD:
                damage = 3 + material.getBaseDamage();
                setName("inv_sword");
                setUseTime(0.20F);
                setUseDelay(0.30F);
                break;
            case HOE:
                setName("inv_hoe");
                setUseTime(0.5F);
                setUseDelay(0.5F);
                break;
            case SHOVEL:
                setName("inv_shovel");
                setUseTime(0.5F);
                setUseDelay(0.5F);
                break;
            case HAMMER:
                setName("inv_hammer");
                damage = 1 + material.getBaseDamage();
                setUseTime(0.5F);
                setUseDelay(0.5F);
                break;
            case CLUB:
                damage = 4 + material.getBaseDamage();
                setName("inv_club");
                setUseTime(0.5F);
                setUseDelay(0.5F);
                break;

        }
    }

    public SwingableType use() {
        setDurability(getDurability()-1);
        return null;
    }

    public SwingableType getSwingableType() {
        return swingableType;
    }

    public String getName() {
        StringBuilder swingableString = new StringBuilder();
        swingableString.append(super.getName());
        swingableString.append("_").append(material.getName());
        return swingableString.toString();
    }

    public float getDamage() {
        return damage;
    }

    public Material getMaterial() {
        return material;
    }
}
