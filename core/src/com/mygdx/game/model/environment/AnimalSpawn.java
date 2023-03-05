package com.mygdx.game.model.environment;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.ai.AnimalAi;
import com.mygdx.game.ai.CowAi;
import com.mygdx.game.ai.SpiderAI;
import com.mygdx.game.model.moveable.Animal;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AnimalSpawn extends SpawnPoint{

    private int population;
    private final int maxPopulation;
    private int animalCount = 0;
    private final int radius;
    Map<Integer, Animal> animals = new HashMap<>();
    Random rand = new Random();
    private boolean readyToAdd;
    private final Animal.AnimalType animalType;
    private final long spawnTime;
    private long lastSpawned;

    public AnimalSpawn(Vector2 pos, int radius, Animal.AnimalType animalType, int maxPopulation, long spawnTime) {
        super(pos);
        this.maxPopulation = maxPopulation;
        this.population = 0;
        this.radius = radius;
        this.animalType = animalType;
        this.spawnTime = spawnTime;
        this.lastSpawned = 0;
        readyToAdd = true;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public Map<Integer, Animal> getAnimals() {
        return animals;
    }

    public boolean isReadyToAdd() {
        return readyToAdd;
    }

    public Animal addAnimal() {
        if (population < maxPopulation) {
            float x = getPosition().x;
            float y = getPosition().y;
            String name = null;
            AnimalAi animalAi = null;
            float width = 0, height = 0, rotationSpeed = 0;
            int lives = 0, food = 0, water = 0;
            boolean child = false;

            switch (animalType) {
                case COW:
                    name = "cow";
                    width = 1;
                    height = 0.5F;
                    rotationSpeed = 80F;
                    animalAi = new CowAi();
                    lives = 10;
                    child = true;
                    food = 6;
                    water = 6;
                    break;
                case SPIDER:
                    name = "spider";
                    width = 0.7F;
                    height = 0.7F;
                    rotationSpeed = 120F;
                    animalAi = new SpiderAI();
                    lives = 2;
                    food = 10;
                    water = 10;
                    break;
            }
            float firstOne = rand.nextInt(radius) + x;
            float secondOne = rand.nextInt(radius) + y;
            return new Animal(new Vector2(firstOne,secondOne), name, this, animalType, width, height, rotationSpeed, animalAi, lives, food, water, animalCount, child);
        }
        return null;
    }

    public int getAnimalCount() {
        return animalCount;
    }

    public void addAnimalToSpawn(long gameTime) {
        animalCount++;
        population++;
        if (gameTime > 0) {
            lastSpawned = gameTime;
            readyToAdd = false;
        }
    }

    public void checkSpawn(long gameTime) {
        if (!readyToAdd && (gameTime - lastSpawned)/1000 > spawnTime) {
            readyToAdd = true;
        }
    }
}
