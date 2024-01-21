package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

public class TextureLoader {

    private final Map<String, TextureRegion> textureMap = new HashMap<>();

    private TextureRegion tilledTexture, irrigationEmptyTexture, irrigationFullTexture, darkTexture;

    private TextureRegion playerIdle, armourIdle, playerInjured, playerDead, playerHand, playerHead;
    private TextureRegion pistolTexture, smgTexture, shotgunTexture, rocketTexture, boostPadTexture;

    public void initTextures() {
        TextureAtlas itemAtlas = new TextureAtlas(Gdx.files.internal("items.atlas"));
        TextureAtlas inventoryAtlas = new TextureAtlas(Gdx.files.internal("inventory.atlas"));
        textureMap.put("sprite-01", itemAtlas.findRegion("sprite-01"));
        textureMap.put("armour-01", itemAtlas.findRegion("armour-01"));
        textureMap.put("dead", itemAtlas.findRegion("dead"));
        textureMap.put("hand-01", itemAtlas.findRegion("hand-01"));
        textureMap.put("head-01", itemAtlas.findRegion("head-01"));
        textureMap.put("inv_shovel_bone", itemAtlas.findRegion("inv_shovel_bone"));
        textureMap.put("inv_hoe_bone", itemAtlas.findRegion("inv_hoe_bone"));
        textureMap.put("inv_club_bone", itemAtlas.findRegion("inv_club_bone"));
        textureMap.put("inv_hammer_stone", inventoryAtlas.findRegion("inv_hammer_stone"));
        textureMap.put("inv_axe_flint", inventoryAtlas.findRegion("inv_axe_flint"));
        textureMap.put("gunPistol", itemAtlas.findRegion("gunPistol"));
        textureMap.put("gunSMG", itemAtlas.findRegion("gunSMG"));
        textureMap.put("gunShotgun", itemAtlas.findRegion("gunShotgun"));
        textureMap.put("gunRocket", itemAtlas.findRegion("gunRocket"));
        textureMap.put("boostPad", itemAtlas.findRegion("boostPad"));
        textureMap.put("cow", itemAtlas.findRegion("cow"));
        textureMap.put("spider", itemAtlas.findRegion("spider"));
        textureMap.put("spider-hiding", itemAtlas.findRegion("spider-hiding"));
        textureMap.put("explodingBlockRed", itemAtlas.findRegion("explodingBlockRed"));
        textureMap.put("explodingBlockYellow", itemAtlas.findRegion("explodingBlockYellow"));
        textureMap.put("rubbleBlock", itemAtlas.findRegion("rubbleBlock"));
        textureMap.put("tilled", itemAtlas.findRegion("tilled"));
        textureMap.put("irrigation", itemAtlas.findRegion("irrigation"));
        textureMap.put("irrigationBend-full", itemAtlas.findRegion("irrigationBend-full"));
        textureMap.put("irrigation-empty", itemAtlas.findRegion("irrigation-empty"));
        textureMap.put("irrigation-full", itemAtlas.findRegion("irrigation-full"));
        textureMap.put("irrigationBend-empty", itemAtlas.findRegion("irrigationBend-empty"));
        textureMap.put("heart", itemAtlas.findRegion("heart"));
        textureMap.put("mana", itemAtlas.findRegion("mana"));
        textureMap.put("block", itemAtlas.findRegion("block"));
        textureMap.put("house", itemAtlas.findRegion("block"));
        textureMap.put("tipi", itemAtlas.findRegion("tipi"));
        textureMap.put("coal", itemAtlas.findRegion("coal"));
        textureMap.put("coal1", itemAtlas.findRegion("coal1"));
        textureMap.put("coal2", itemAtlas.findRegion("coal2"));
        textureMap.put("stone", itemAtlas.findRegion("stone"));
        textureMap.put("stone1", itemAtlas.findRegion("stone1"));
        textureMap.put("stone2", itemAtlas.findRegion("stone2"));
        textureMap.put("stone3", itemAtlas.findRegion("stone3"));
        textureMap.put("wood", itemAtlas.findRegion("wood"));
        textureMap.put("wood1", itemAtlas.findRegion("wood1"));
        textureMap.put("wood2", itemAtlas.findRegion("wood2"));
        textureMap.put("wood3", itemAtlas.findRegion("wood3"));
        textureMap.put("wood4", itemAtlas.findRegion("wood4"));
        textureMap.put("body-cow", itemAtlas.findRegion("body_cow"));
        textureMap.put("body-spider", itemAtlas.findRegion("body-spider"));
        textureMap.put("meat", itemAtlas.findRegion("meat"));
        textureMap.put("cookedmeat", itemAtlas.findRegion("cookedmeat"));
        textureMap.put("berrypaste", itemAtlas.findRegion("berrypaste"));
        textureMap.put("berry", itemAtlas.findRegion("berry"));
        textureMap.put("berrybush", itemAtlas.findRegion("berrybush"));
        textureMap.put("grass", itemAtlas.findRegion("grass"));
        textureMap.put("wall", itemAtlas.findRegion("wall"));
        textureMap.put("door", itemAtlas.findRegion("door"));
        textureMap.put("dooropen", itemAtlas.findRegion("dooropen"));
        textureMap.put("stick", itemAtlas.findRegion("stick"));
        textureMap.put("copper", itemAtlas.findRegion("copper"));
        textureMap.put("flint", itemAtlas.findRegion("flint"));
        textureMap.put("bed", itemAtlas.findRegion("bed"));
        textureMap.put("tile", itemAtlas.findRegion("tile"));
        textureMap.put("floor", itemAtlas.findRegion("floor"));
        textureMap.put("floor2", itemAtlas.findRegion("floor2"));
        textureMap.put("floor3", itemAtlas.findRegion("floor3"));
        textureMap.put("floor4", itemAtlas.findRegion("floor4"));
        textureMap.put("floor5", itemAtlas.findRegion("floor5"));
        textureMap.put("floor6", itemAtlas.findRegion("floor6"));
        textureMap.put("dark", itemAtlas.findRegion("dark"));
        textureMap.put("homingBoost", itemAtlas.findRegion("homingBoost"));
        textureMap.put("speedBoost", itemAtlas.findRegion("speedBoost"));
        textureMap.put("shieldBoost", itemAtlas.findRegion("shieldBoost"));
        textureMap.put("damageBoost", itemAtlas.findRegion("damageBoost"));
        textureMap.put("padSpike", itemAtlas.findRegion("padSpike"));
        textureMap.put("padSticky", itemAtlas.findRegion("padSticky"));
        textureMap.put("padSticky1", itemAtlas.findRegion("padSticky1"));
        textureMap.put("padSticky2", itemAtlas.findRegion("padSticky2"));
        textureMap.put("padSticky3", itemAtlas.findRegion("padSticky3"));
        textureMap.put("padMove1", itemAtlas.findRegion("padMove1"));
        textureMap.put("padMove2", itemAtlas.findRegion("padMove2"));
        textureMap.put("reloading", itemAtlas.findRegion("reloading"));
        textureMap.put("inv_jar", inventoryAtlas.findRegion("inv_jar"));
        textureMap.put("inv_jarFull", inventoryAtlas.findRegion("inv_jarFull"));
        textureMap.put("shield-01", itemAtlas.findRegion("shield-01"));
        textureMap.put("joystickOuter", itemAtlas.findRegion("joystickOuter"));
        textureMap.put("joystickInner", itemAtlas.findRegion("joystickInner"));

        textureMap.put("fire", itemAtlas.findRegion("fire"));
        textureMap.put("fire-burning", itemAtlas.findRegion("fire-burning"));
        textureMap.put("fire-burning1", itemAtlas.findRegion("fire-burning1"));
        textureMap.put("fire-burning2", itemAtlas.findRegion("fire-burning2"));
        textureMap.put("fire-burning3", itemAtlas.findRegion("fire-burning3"));
        textureMap.put("bench-healer", itemAtlas.findRegion("bench-healer"));
        textureMap.put("bench-stone", itemAtlas.findRegion("bench-stone"));
        textureMap.put("torch", itemAtlas.findRegion("torch"));
        textureMap.put("torch-burning-01", itemAtlas.findRegion("torch-burning-01"));
        textureMap.put("torch-burning-02", itemAtlas.findRegion("torch-burning-02"));
        textureMap.put("torch-burning-03", itemAtlas.findRegion("torch-burning-03"));
        textureMap.put("torch-burning-04", itemAtlas.findRegion("torch-burning-04"));
        textureMap.put("chest", itemAtlas.findRegion("chest"));

        textureMap.put("pebble", itemAtlas.findRegion("pebble"));
        textureMap.put("spear", itemAtlas.findRegion("spear"));
        textureMap.put("arrow", itemAtlas.findRegion("arrow"));
        textureMap.put("bullet", itemAtlas.findRegion("bullet"));
        textureMap.put("lightningBolt", itemAtlas.findRegion("lightningBolt"));

        textureMap.put("healing", itemAtlas.findRegion("healing"));
        textureMap.put("target", itemAtlas.findRegion("target"));

        textureMap.put("lightning-01", itemAtlas.findRegion("lightning-01"));
        textureMap.put("lightning-02", itemAtlas.findRegion("lightning-02"));
        textureMap.put("lightning-03", itemAtlas.findRegion("lightning-03"));
        textureMap.put("lightning-04", itemAtlas.findRegion("lightning-04"));

        textureMap.put("fireball-01", itemAtlas.findRegion("fireball-01"));
        textureMap.put("fireball-02", itemAtlas.findRegion("fireball-02"));
        textureMap.put("fireball-03", itemAtlas.findRegion("fireball-03"));
        textureMap.put("fireball-04", itemAtlas.findRegion("fireball-04"));

        textureMap.put("onfire-01", itemAtlas.findRegion("onfire-01"));
        textureMap.put("onfire-02", itemAtlas.findRegion("onfire-02"));
        textureMap.put("onfire-03", itemAtlas.findRegion("onfire-03"));
        textureMap.put("onfire-04", itemAtlas.findRegion("onfire-04"));

        textureMap.put("potato", itemAtlas.findRegion("potato"));
        textureMap.put("melon", itemAtlas.findRegion("melon"));

        textureMap.put("potato-seedling", itemAtlas.findRegion("potato-seedling"));
        textureMap.put("potato-middling", itemAtlas.findRegion("potato-middling"));
        textureMap.put("potato-mature", itemAtlas.findRegion("potato-mature"));
        textureMap.put("melon-seedling", itemAtlas.findRegion("melon-seedling"));
        textureMap.put("melon-middling", itemAtlas.findRegion("melon-middling"));
        textureMap.put("melon-mature", itemAtlas.findRegion("melon-mature"));

        textureMap.put("carrot", itemAtlas.findRegion("carrot"));
        textureMap.put("carrot-seedling", itemAtlas.findRegion("carrot-seedling"));
        textureMap.put("carrot-middling", itemAtlas.findRegion("carrot-middling"));
        textureMap.put("carrot-mature", itemAtlas.findRegion("carrot-mature"));
    }

    public TextureRegion getRegion(String key) {
        return textureMap.get(key);
    }
}
