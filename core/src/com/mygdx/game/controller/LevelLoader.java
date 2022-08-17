package com.mygdx.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.environment.AnimalSpawn;
import com.mygdx.game.model.environment.blocks.Block;
import com.mygdx.game.model.environment.blocks.EnvironmentBlock;
import com.mygdx.game.model.environment.blocks.Grower;
import com.mygdx.game.model.items.Food;
import com.mygdx.game.model.items.Material;
import com.mygdx.game.model.pads.BoostPad;
import com.mygdx.game.model.environment.blocks.ExplodableBlock;
import com.mygdx.game.model.pads.FloorPad;
import com.mygdx.game.model.pads.GunPad;
import com.mygdx.game.model.Level;
import com.mygdx.game.model.environment.SpawnPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.mygdx.game.model.items.Food.FoodType.*;
import static com.mygdx.game.model.items.Material.Type.*;
import static com.mygdx.game.model.items.Ranged.RangedType.*;

public class LevelLoader {


    public Level loadLevel(int number) {
        Level level = new Level();

        try {
             loadFile(level, number);
             for (int i = 5; i < level.getWidth() - 4; i++) {
                 level.getBlocks()[5][i] = new Block(new Vector2(5, i));
                 level.getBlocks()[level.getWidth() - 5][i] = new Block(new Vector2(level.getWidth() - 5, i));
             }
             for (int i = 6; i < level.getHeight() - 5; i++) {
                 level.getBlocks()[i][5] = new Block(new Vector2(i, 5));
                 level.getBlocks()[i][level.getHeight() - 5] = new Block(new Vector2(i, level.getHeight() - 5));
             }
        } catch (IOException e) {
            System.out.println(e.toString());
        }

        return level;
    }

    private void loadFile(Level level, int number) throws IOException{

        FileHandle file = Gdx.files.internal("Levels/level0" + number + ".csv");

        int spawnCount = 0;
        BufferedReader br = new BufferedReader(new InputStreamReader(file.read()));
        String st;
        while ((st = br.readLine()) != null) {
            if (st.contains("//")) continue;
            String[] values = st.split(",");

            switch (values[0]) {
                case "PLAYER":
                case "AI":
                    level.getSpawnPoints().put(spawnCount, new SpawnPoint(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2]))));
                    spawnCount++;
                    break;
                case "ANIMAL":
                    level.getAnimalSpawnPoints().add(new AnimalSpawn(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), 25));
                    break;
                case "BLOCK":
                    level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = new Block(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])));
                    break;
                case "EXPLODING":
                    ExplodableBlock eb = new ExplodableBlock(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])));
                    level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = eb;
                    level.getExplodableBlocks().add(eb);
                    break;
                case "ENVIRONMENT":
                    switch (values[3]) {
                        case "COAL":
                            EnvironmentBlock coal = new EnvironmentBlock(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), new Material(COAL, 1));
                            level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = coal;
//                            level.getEnvironmentBlocks().add(coal);
                            break;
                        case "STONE":
                            EnvironmentBlock stone = new EnvironmentBlock(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), new Material(STONE, 1));
                            level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = stone;
//                            level.getEnvironmentBlocks().add(stone);
                            break;
                        case "WOOD":
                            EnvironmentBlock wood = new EnvironmentBlock(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), new Material(WOOD, 1));
                            level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = wood;
//                            level.getEnvironmentBlocks().add(wood);
                            break;
                        case "BERRY":
                            EnvironmentBlock berry = new EnvironmentBlock(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), new Food(BERRY, 1));
                            level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = berry;
//                            level.getEnvironmentBlocks().add(berry);
                            break;
                        case "POTATO":
                            Grower potato = new Grower(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), Grower.CropType.POTATO);
                            level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = potato;
                            level.getGrowers().add(potato);
                            break;
                        case "GRASS":
                            EnvironmentBlock grass = new EnvironmentBlock(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), new Material(GRASS, 1));
                            level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = grass;
//                            level.getEnvironmentBlocks().add(grass);
                            break;
                        case "STICK":
                            EnvironmentBlock stick = new EnvironmentBlock(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), new Material(STICK, 1));
                            level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = stick;
//                            level.getEnvironmentBlocks().add(stick);
                            break;
                        case "PEBBLE":
                            EnvironmentBlock pebble = new EnvironmentBlock(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), new Material(PEBBLE, 1));
                            level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = pebble;
//                            level.getEnvironmentBlocks().add(pebble);
                            break;

                        case "TREE":
                            break;
                        default:
                            break;
                    }
                    break;
                case "PISTOL":
                    level.getGunPads().add(new GunPad(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), PISTOL));
                    break;
                case "SMG":
                    level.getGunPads().add(new GunPad(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), SMG));
                    break;
                case "SHOTGUN":
                    level.getGunPads().add(new GunPad(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), SHOTGUN));
                    break;
                case "ROCKET":
                    level.getGunPads().add(new GunPad(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), ROCKET));
                    break;
                case "BOOST":
                    level.getBoostPads().add(new BoostPad(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2]))));
                    break;
                case "SPIKE":
                    level.getFloorPads().add(new FloorPad(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), FloorPad.Type.SPIKE));
                    break;
                case "SLIME":
                    level.getFloorPads().add(new FloorPad(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), FloorPad.Type.SLIME));
                    break;
                case "WATER":
                    level.getFloorPads().add(new FloorPad(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), FloorPad.Type.WATER));
                    break;
                case "MOVERIGHT":
                    level.getFloorPads().add(new FloorPad(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), FloorPad.Type.WATERFLOW, 0));
                    break;
                case "MOVEUP":
                    level.getFloorPads().add(new FloorPad(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), FloorPad.Type.WATERFLOW,90));
                    break;
                case "MOVELEFT":
                    level.getFloorPads().add(new FloorPad(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), FloorPad.Type.WATERFLOW,180));
                    break;
                case "MOVEDOWN":
                    level.getFloorPads().add(new FloorPad(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), FloorPad.Type.WATERFLOW,270));
                    break;
                case "FLOOR":
                    break;
            }
        }
    }

}
