package com.mygdx.game.model;

public class Attributes {

    //todo map each attribute with the stats it increases and the activities that increase it. Start with Health
    private float strength, vitality, adeptness, ruggedness, agility, dexterity, luck, will, intelligence, perception;

    public Attributes(float vitality, float adeptness) {
        this.strength = 1;
        this.vitality = vitality;
        this.adeptness = adeptness;
        this.ruggedness = 0.5F;
        this.agility = 1;
        this.dexterity = 1;
        this.luck = 1;
        this.will = 1;
        this.intelligence = 1;
        this.perception = 1;
    }

    public void increaseStrength(float buff) {
        strength += buff;
    }
    public void increaseVitality(float buff) {
        vitality += buff/(vitality*2);
        System.out.println("Vitality is now " + vitality);
        System.out.println("Max health is: " + getMaxHealth());
    }
    public void increaseAdeptness(float buff) {
        adeptness += buff;
    }
    public void increaseRuggedness(float buff) {
        ruggedness += buff;
    }
    public void increaseAgility(float buff) {
        agility += buff;
    }
    public void increaseDexterity(float buff) { dexterity += buff; }
    public void increaseLuck(float buff) {
        luck += buff;
    }
    public void increaseWill(float buff) {
        will += buff;
    }
    public void increaseIntelligence(float buff) {
        intelligence += buff;
    }
    public void increasePerception(float buff) {
        perception += buff;
    }

    //determined by vitality and ruggedness
    public float getMaxHealth() {
        return (float)Math.floor(vitality + ruggedness);
    }

    //determined by vitality and ruggedness
    public float getHealing() {
//        System.out.println("Healing by: " + (ruggedness/4 + vitality/20));
        return ruggedness/4 + vitality/20;
    }

    //determined by adeptness, will and intelligence
    public float getMaxMana() {
        return adeptness;
    }
    //determined by strength, agility and intelligence
    public float getHitPower() {
        return strength;
    }
    public float getMaxFood() {
        return (float)Math.floor(vitality);
    }
    public float getMagicPower() {
        return vitality;
    }
    public float getDash() {
        return strength;
    }
    public float getDodge() {
        return vitality;
    }
    public float getBlock() {
        return strength;
    }
    public float getMiningSkill() {
        return vitality;
    }
    public float getCraftingSkill() {
        return strength;
    }
    public float getMagicSkill() {
        return vitality;
    }
    public float getCombatSkill() {
        return strength;
    }
    public float getLockPickingSkill() {
        return strength;
    }
}
