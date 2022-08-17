package com.mygdx.game.model.environment;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.model.moveable.Animal;
import com.mygdx.game.model.moveable.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AnimalSpawn extends SpawnPoint{

    private int population;
    private final int maxPopulation;
    private final int radius;
    Map<Integer, Animal> animals = new HashMap<>();
    Random rand = new Random();
    private boolean readyToAdd;
    private final Timer.Task newAnimalTimer = new Timer.Task() {
        @Override
        public void run() {

            stopNewAnimalTimer();
        }
    };


    public AnimalSpawn(Vector2 pos, int radius) {
        super(pos);
        this.maxPopulation = 20;
        this.population = 0;
        this.radius = radius;
        readyToAdd = true;
    }

    public int getMaxPopulation() {
        return maxPopulation;
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

    public Animal addAnimal(String name) {
        if (population < maxPopulation) {
            float x = getPosition().x;
            float y = getPosition().y;

            float firstOne = rand.nextInt(radius) + x;
            float secondOne = rand.nextInt(radius) + y;
            population++;
            startNewAnimalTimer(5);
            return new Animal(new Vector2(firstOne,secondOne), name, this);
        }
        return null;
    }

    public void startNewAnimalTimer(float delay) {
        readyToAdd = false;
        Timer.schedule(newAnimalTimer, delay);
    }
    private void stopNewAnimalTimer() {
        readyToAdd = true;
        newAnimalTimer.cancel();
    }
}
