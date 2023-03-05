package com.mygdx.game.model;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.controller.LevelLoader;
import com.mygdx.game.model.environment.AnimalSpawn;
import com.mygdx.game.model.environment.Tilled;
import com.mygdx.game.model.environment.blocks.Block;
import com.mygdx.game.model.environment.BloodStain;
import com.mygdx.game.model.environment.AreaAffect;
import com.mygdx.game.model.environment.SpawnPoint;
import com.mygdx.game.model.environment.blocks.EnvironmentBlock;
import com.mygdx.game.model.moveable.AIPlayer;
import com.mygdx.game.model.moveable.Animal;
import com.mygdx.game.model.moveable.Projectile;
import com.mygdx.game.model.moveable.Player;
import com.mygdx.game.model.moveable.Sprite;
import com.mygdx.game.utils.JoyStick;
import com.mygdx.game.utils.RecipeHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class World {

    public enum JoystickState {
        STILL, SPIN, MOVE
    }

    private Player bob;
    private List<Projectile> projectiles = new ArrayList<>();
    private List<AreaAffect> areaAffects = new ArrayList<>();
    private final List<AIPlayer> AIPlayers = new ArrayList<>();
    private final List<Animal> animals = new ArrayList<>();
    private final List<BloodStain> bloodStains = new ArrayList<>();
    private final List<Tilled> tilled = new ArrayList<>();
    private List<EnvironmentBlock> bodies = new ArrayList<>();
    private final List<GameButton> buttons = new ArrayList<>();
    private Level level;
    private final Array<Polygon> collisionRects = new Array<>();
    private final Map<Sprite, Polygon> movementRects = new HashMap<>();
    private final LevelLoader levelLoader = new LevelLoader();
    private JoyStick moveJoystick, fireJoystick;
    private final RecipeHolder recipeHolder = new RecipeHolder();
    private String time;
    private boolean nightTime = false;
    private boolean duskTillDawn = false;
    private int minute = 0, hour = 10, day = 0;

    private Vector2 locateExplosion;

    public World() {
        loadWorld(1);
    }

    // Getters -----------

    public Array<Polygon> getCollisionRects() {
        return collisionRects;
    }

    public Map<Sprite, Polygon> getMovementRects() {
        return movementRects;
    }

    public Player getBob() {
        return bob;
    }

    public Level getLevel() {
        return level;
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public void setProjectiles(List<Projectile> projectiles) {
        this.projectiles = projectiles;
    }

    public List<AreaAffect> getAreaAffects() {
        return areaAffects;
    }

    public void setAreaAffects(List<AreaAffect> areaAffects) {
        this.areaAffects = areaAffects;
    }

    public List<BloodStain> getBloodStains() {
        return bloodStains;
    }

    public List<Tilled> getTilled() {
        return tilled;
    }

    public List<EnvironmentBlock> getBodies() {
        return bodies;
    }

    public void setBodies(List<EnvironmentBlock> bodies) {
        this.bodies = bodies;
    }

    public List<AIPlayer> getAIPlayers() {
        return AIPlayers;
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    public List<GameButton> getButtons() {
        return buttons;
    }

    public RecipeHolder getRecipeHolder() {
        return recipeHolder;
    }

    public JoyStick getMoveJoystick() {
        return moveJoystick;
    }

    public void setMoveJoystick(JoyStick moveJoystick) {
        this.moveJoystick = moveJoystick;
    }

    public JoyStick getFireJoystick() {
        return fireJoystick;
    }

    public void setFireJoystick(JoyStick fireJoystick) {
        this.fireJoystick = fireJoystick;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isNightTime() {
        return nightTime;
    }

    public void setNightTime(boolean nightTime) {
        this.nightTime = nightTime;
    }

    public boolean isDuskTillDawn() {
        return duskTillDawn;
    }

    public void setDuskTillDawn(boolean duskTillDawn) {
        this.duskTillDawn = duskTillDawn;
    }

    public void increaseMinute() {
        minute++;
        if (minute == 60) {
            hour++;
            minute = 0;
        }
        if (hour == 24) {
            day++;
            hour = 0;
        }

        if ((hour == 5) || (hour == 20)) {
            setDuskTillDawn(true);
            setNightTime(false);
        } else if (hour <= 4 || hour >= 21) {
            setNightTime(true);
            setDuskTillDawn(false);
        } else {
            setNightTime(false);
            setDuskTillDawn(false);
        }

        setTime((hour < 10 ? "0" : "") + hour + ":" + (minute < 10 ? "0" : "") + minute + " on day " + day);
    }

    // --------------------

    /** Return only the blocks that need to be drawn **/
    public List<Block> getDrawableBlocks(int width, int height) { //7 and 4
        int x = (int)bob.getPosition().x - width;
        int y = (int)bob.getPosition().y - height;
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }

        int x2 = x + 2 * width;
        int y2 = y + 2 * height;
        if (x2 > level.getWidth()) {
            x2 = level.getWidth() - 1;
        }
        if (y2 > level.getHeight()) {
            y2 = level.getHeight() - 1;
        }

        List<Block> blocks = new ArrayList<>();
        Block block;
        for (int col = x; col <= x2; col++) {
            for (int row = y; row <= y2; row++) {
                block = level.getBlock(col, row);
                if (block != null) {
                    blocks.add(block);
                }
            }
        }

        return blocks;
    }

    public void loadWorld(int number) {
        System.out.println("Loading level " + number);
        bloodStains.clear();
        bodies.clear();
        areaAffects.clear();
        projectiles.clear();
        level = levelLoader.loadLevel(number);
        Map<Integer, com.mygdx.game.model.environment.SpawnPoint> spawnPoints = level.getSpawnPoints();
        List<Integer> numbers = new ArrayList<>();
        Random rand = new Random();
        int rando = rand.nextInt(spawnPoints.size());
        SpawnPoint sp = spawnPoints.get(rando);
        bob = new Player(new Vector2(sp.getPosition()), "player", 30, recipeHolder);
        numbers.add(rando);

        for (int i = 0; i < spawnPoints.size() -1; i++) {
            while (numbers.contains(rando)) {
                rando = rand.nextInt(spawnPoints.size());
            }
            sp = spawnPoints.get(rando);
            AIPlayers.add(new AIPlayer(new Vector2(sp.getPosition()), "ai-0" + i, recipeHolder));
            numbers.add(rando);
        }
        for (AnimalSpawn animalSpawn : getLevel().getAnimalSpawnPoints()) {
            Animal animal = animalSpawn.addAnimal();
            Vector2 pos = animal.getPosition();
            if (pos.x <= 0 || pos.x >= level.getWidth() || pos.y <= 0 || pos.y >= level.getHeight()) animal.die();
            else animals.add(animal);
        }
//        buttons.add(new GameButton(new Vector2(12, 3), 0.5F, GameButton.Type.USE));
    }

    public Vector2 getLocateExplosion() {
        return locateExplosion;
    }

    public void setLocateExplosion(Vector2 locateExplosion) {
        this.locateExplosion = locateExplosion;
    }
}
