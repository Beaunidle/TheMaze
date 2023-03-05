package com.mygdx.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.environment.AnimalSpawn;
import com.mygdx.game.model.environment.blocks.Block;
import com.mygdx.game.model.environment.blocks.EnvironmentBlock;
import com.mygdx.game.model.environment.blocks.Grower;
import com.mygdx.game.model.items.Consumable;
import com.mygdx.game.model.items.Material;
import com.mygdx.game.model.items.Swingable;
import com.mygdx.game.model.items.Throwable;
import com.mygdx.game.model.moveable.Animal;
import com.mygdx.game.model.pads.BoostPad;
import com.mygdx.game.model.environment.blocks.ExplodableBlock;
import com.mygdx.game.model.pads.FloorPad;
import com.mygdx.game.model.pads.GunPad;
import com.mygdx.game.model.Level;
import com.mygdx.game.model.environment.SpawnPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.mygdx.game.model.items.Consumable.ConsumableType.*;
import static com.mygdx.game.model.items.Material.Type.*;
import static com.mygdx.game.model.items.Ranged.RangedType.*;
import static com.mygdx.game.model.items.Swingable.SwingableType.AXE;
import static com.mygdx.game.model.items.Swingable.SwingableType.PICK;
import static com.mygdx.game.model.moveable.Animal.AnimalType.COW;
import static com.mygdx.game.model.moveable.Animal.AnimalType.SPIDER;

public class LevelLoader {


    public Level loadLevel(int number) {
        Level level = new Level();

        try {
             loadFile(level, number);
             for (int i = 5; i < level.getWidth() - 4; i++) {
                 level.getBlocks()[5][i] = new Block(new Vector2(5, i), "block");
                 level.getBlocks()[level.getWidth() - 5][i] = new Block(new Vector2(level.getWidth() - 5, i), "block");
             }
             for (int i = 6; i < level.getHeight() - 5; i++) {
                 level.getBlocks()[i][5] = new Block(new Vector2(i, 5), "block");
                 level.getBlocks()[i][level.getHeight() - 5] = new Block(new Vector2(i, level.getHeight() - 5), "block");
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
                case "COW":
                    level.getAnimalSpawnPoints().add(new AnimalSpawn(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), 6, COW, 15, 5));
                    break;
                case "SPIDER":
//                    level.getAnimalSpawnPoints().add(new AnimalSpawn(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), 15, SPIDER, 30, 5));
                    break;
                case "BLOCK":
                    level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = new Block(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), "block");
                    break;
                case "EXPLODING":
                    ExplodableBlock eb = new ExplodableBlock(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])));
                    level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = eb;
                    level.getExplodableBlocks().add(eb);
                    break;
                case "ENVIRONMENT":
                    switch (values[3]) {
                        case "COAL":
                            EnvironmentBlock coal = new EnvironmentBlock(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), new Material(COAL, 1), null, 3, 5, 0, 0,true, PICK, 10, "coal", 1, 1);
                            level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = coal;
//                            level.getEnvironmentBlocks().add(coal);
                            break;
                        case "STONE":
                            EnvironmentBlock stone = new EnvironmentBlock(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), new Material(STONE, 1), null, 3, 5, 0, 0,true, PICK, 10, "stone", 1, 1);
                            level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = stone;
//                            level.getEnvironmentBlocks().add(stone);
                            break;
                        case "WOOD":
                            EnvironmentBlock wood = new EnvironmentBlock(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), new Material(WOOD, 1), null, 3, 5, 0, 0,true, AXE, 10, "wood", 1, 1);
                            level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = wood;
//                            level.getEnvironmentBlocks().add(wood);
                            break;
                        case "BERRY":
                            EnvironmentBlock berry = new EnvironmentBlock(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), new Consumable(BERRY, 1), null, 2, 5, 0, 0,false, null, 10, "berry", 1, 1);
                            level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = berry;
//                            level.getEnvironmentBlocks().add(berry);
                            break;
                        case "POTATO":
                            Grower potato = new Grower(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), Grower.CropType.POTATO, "potato");
                            level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = potato;
                            level.getGrowers().add(potato);
                            break;
                        case "MELON":
                            Grower melon = new Grower(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), Grower.CropType.MELON, "melon");
                            level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = melon;
                            level.getGrowers().add(melon);
                            break;
                        case "CARROT":
                            Grower carrot = new Grower(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), Grower.CropType.CARROT, "carrot");
                            level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = carrot;
                            level.getGrowers().add(carrot);
                            break;
                        case "GRASS":
                            EnvironmentBlock grass = new EnvironmentBlock(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), new Material(GRASS, 1), null, 2, 5, 0, 5,false, null, 10, "grass", 1, 1);
                            level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = grass;
//                            level.getEnvironmentBlocks().add(grass);
                            break;
                        case "STICK":
                            EnvironmentBlock stick = new EnvironmentBlock(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), new Material(STICK, 1), null, 1, 5, 0, 0,false, null, 1, "stick", 1, 1);
                            level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = stick;
                            break;
                        case "COPPER":
                            EnvironmentBlock copper = new EnvironmentBlock(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), new Material(COPPER, 1), null, 1, 5, 0, 0,false, null, 1, "copper", 1, 1);
                            level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = copper;
                            break;
                        case "FLINT":
                            EnvironmentBlock flint = new EnvironmentBlock(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), new Material(FLINT, 1), null, 1, 5, 0, 0,false, null, 1, "flint", 1, 1);
                            level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = flint;
                            break;
                        case "PEBBLE":
                            EnvironmentBlock pebble = new EnvironmentBlock(new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), new Throwable(Throwable.ThrowableType.PEBBLE, 1), null, 1, 5, 0, 0,false, null, 1, "pebble", 1, 1);
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
                    level.getBoostPads().add(new BoostPad("boostpad", new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2]))));
                    break;
                case "SPIKE":
                    level.getFloorPads().add(new FloorPad("padSpike", new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), FloorPad.Type.SPIKE));
                    break;
                case "SLIME":
                    level.getFloorPads().add(new FloorPad("slime", new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), FloorPad.Type.SLIME));
                    break;
                case "WATER":
                    FloorPad water = new FloorPad("padSticky", new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), FloorPad.Type.WATER);
                    level.getFloorPads().add(water);
                    level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = water;
                    break;
                case "MOVERIGHT":
                    FloorPad moveRight = new FloorPad("padMove", new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), FloorPad.Type.WATERFLOW, 0);
                    level.getFloorPads().add(moveRight);
                    level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = moveRight;
                    break;
                case "MOVEUP":
                    FloorPad moveUp = new FloorPad("padMove", new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), FloorPad.Type.WATERFLOW,90);
                    level.getFloorPads().add(moveUp);
                    level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = moveUp;
                    break;
                case "MOVELEFT":
                    FloorPad moveLeft = new FloorPad("padMove", new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), FloorPad.Type.WATERFLOW,180);
                    level.getFloorPads().add(moveLeft);
                    level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = moveLeft;
                    break;
                case "MOVEDOWN":
                    FloorPad moveDown = new FloorPad("padMove", new Vector2(Integer.parseInt(values[1]), Integer.parseInt(values[2])), FloorPad.Type.WATERFLOW,270);
                    level.getFloorPads().add(moveDown);
                    level.getBlocks()[Integer.parseInt(values[1])][Integer.parseInt(values[2])] = moveDown;
                    break;
                case "FLOOR":
                    break;
            }
        }
    }

}
