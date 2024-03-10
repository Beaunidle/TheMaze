package com.mygdx.game.model.environment.blocks;

import static com.mygdx.game.model.items.Material.Type.FIRESTONE;
import static com.mygdx.game.model.items.Swingable.SwingableType.PICK;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.GameObject;
import com.mygdx.game.model.environment.AnimalSpawn;
import com.mygdx.game.model.items.Consumable;
import com.mygdx.game.model.items.Fillable;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.items.Material;
import com.mygdx.game.model.moveable.Animal;
import com.mygdx.game.model.moveable.Sprite;
import com.mygdx.game.model.pads.FloorPad;
import com.mygdx.game.utils.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public class Building extends Block {

    public enum BuildingType {
        TIPI, DUNGEON
    }

    private final BuildingType buildingType;
    private final int number;
    private Block[][] blocks;
    private List<FloorPad> floorPads;
    private List<Statue> statues;
    private List<Sprite> sprites;
    private List<AnimalSpawn> spawns;
    private int internalWidth, internalHeight;

    public Building(Vector2 pos, double maxDurability, float width, float height, int rotation, String name, BuildingType houseType, int number) {
        super(pos, maxDurability, width, height, rotation, name);
        this.buildingType = houseType;
        this.number = number;
        setBlockType(BlockType.BUILDING);
        switch (this.buildingType) {
            case TIPI:
                internalWidth = 6;
                internalHeight = 6;
                break;
            case DUNGEON:
                internalWidth = 50;
                internalHeight = 50;
                break;
            default:
                break;
        }
        initBlocks(internalWidth, internalHeight, number * 1000);
    }

    private void initBlocks(int width, int height, int offset) {
        blocks = new Block[width + 1][height + 1];
        floorPads = new ArrayList<>();
        statues = new ArrayList<>();
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                blocks[col][row] = null;
            }
        }
        for (int i = 0; i < width; i++) {
            blocks[0][i] = new Block(new Vector2(offset, i + offset), "block");
            blocks[width - 1][i] = new Block(new Vector2(width - 1 + offset, i + offset), "block");
        }
        for (int i = 0; i < height; i++) {
            blocks[i][0] = new Block(new Vector2(i + offset, offset), "block");
            blocks[i][height - 1] = new Block(new Vector2(i + offset, height - 1 + offset), "block");
        }
        int doorX = (int) Math.floor(width / 2F);
        blocks[doorX][0] = new Wall(new Vector2(doorX + offset, offset), 180, Block.getSIZE(), Block.getSIZE() / 4, Wall.WallType.Type.DUNGEON, false);

        switch (buildingType) {
            case TIPI:
//                System.out.println("Do nothing for now");
                break;
            case DUNGEON:
                blocks[23][1] = new Block(new Vector2(23 + offset, 1 + offset), "block");
                blocks[23][2] = new Block(new Vector2(23 + offset, 2 + offset), "block");
                blocks[23][3] = new Block(new Vector2(23 + offset, 3 + offset), "block");
                blocks[23][4] = new Block(new Vector2(23 + offset, 4 + offset), "block");
                blocks[22][4] = new Block(new Vector2(22 + offset, 4 + offset), "block");
                blocks[22][5] = new Block(new Vector2(22 + offset, 5 + offset), "block");
                blocks[22][6] = new Block(new Vector2(22 + offset, 6 + offset), "block");
                blocks[23][5] = new FillableBlock(new Vector2(23 + offset, 5 + offset), 10, FillableBlock.FillableType.TORCH, 1, 1, 0, null, "torch");
                blocks[23][6] = new Block(new Vector2(23 + offset, 6 + offset), "block");
                blocks[23][8] = new Block(new Vector2(23 + offset, 8 + offset), "block");
                blocks[24][8] = new Block(new Vector2(24 + offset, 8 + offset), "block");
                blocks[25][8] = new Block(new Vector2(25 + offset, 8 + offset), "block");
                blocks[26][8] = new Block(new Vector2(26 + offset, 8 + offset), "block");
                blocks[27][8] = new Block(new Vector2(27 + offset, 8 + offset), "block");
                blocks[22][8] = new Block(new Vector2(22 + offset, 8 + offset), "block");
                blocks[21][8] = new Block(new Vector2(21 + offset, 8 + offset), "block");
                blocks[20][8] = new Block(new Vector2(20 + offset, 8 + offset), "block");
                blocks[19][8] = new Block(new Vector2(19 + offset, 8 + offset), "block");
                blocks[18][8] = new Block(new Vector2(18 + offset, 8 + offset), "block");
                blocks[17][8] = new Block(new Vector2(17 + offset, 8 + offset), "block");
                blocks[16][8] = new Block(new Vector2(16 + offset, 8 + offset), "block");
                blocks[15][8] = new Block(new Vector2(15 + offset, 8 + offset), "block");
                blocks[14][8] = new Block(new Vector2(14 + offset, 8 + offset), "block");
                blocks[13][8] = new Block(new Vector2(13 + offset, 8 + offset), "block");
                blocks[12][8] = new Block(new Vector2(12 + offset, 8 + offset), "block");
                blocks[11][8] = new Block(new Vector2(11 + offset, 8 + offset), "block");
                blocks[10][8] = new Block(new Vector2(10 + offset, 8 + offset), "block");
                blocks[9][8] = new Block(new Vector2(9 + offset, 8 + offset), "block");
                blocks[8][8] = new Block(new Vector2(8 + offset, 8 + offset), "block");
                blocks[7][8] = new Block(new Vector2(7 + offset, 8 + offset), "block");
                blocks[6][8] = new Block(new Vector2(6 + offset, 8 + offset), "block");
                blocks[5][8] = new Block(new Vector2(5 + offset, 8 + offset), "block");
                blocks[4][8] = new Block(new Vector2(4 + offset, 8 + offset), "block");
                blocks[2][8] = new Block(new Vector2(2 + offset, 8 + offset), "block");
                blocks[1][8] = new Block(new Vector2(1 + offset, 8 + offset), "block");

                blocks[15][9] = new Block(new Vector2(15 + offset, 9 + offset), "block");
                blocks[15][10] = new Block(new Vector2(15 + offset, 10 + offset), "block");
                blocks[15][11] = new Block(new Vector2(15 + offset, 11 + offset), "block");
                blocks[15][12] = new Block(new Vector2(15 + offset, 12 + offset), "block");
                blocks[15][13] = new Block(new Vector2(15 + offset, 13 + offset), "block");
                blocks[15][14] = new Block(new Vector2(15 + offset, 14 + offset), "block");
                blocks[15][15] = new Block(new Vector2(15 + offset, 15 + offset), "block");
                blocks[15][16] = new Block(new Vector2(15 + offset, 16 + offset), "block");
                blocks[15][17] = new Block(new Vector2(15 + offset, 17 + offset), "block");
                blocks[15][18] = new Block(new Vector2(15 + offset, 18 + offset), "block");
                blocks[15][19] = new Block(new Vector2(15 + offset, 19 + offset), "block");
                blocks[15][20] = new Block(new Vector2(15 + offset, 20 + offset), "block");
                blocks[15][21] = new Block(new Vector2(15 + offset, 21 + offset), "block");
                blocks[15][22] = new Block(new Vector2(15 + offset, 22 + offset), "block");
                blocks[15][23] = new Block(new Vector2(15 + offset, 23 + offset), "block");
                blocks[16][23] = new Block(new Vector2(16 + offset, 23 + offset), "block");
                blocks[16][24] = new Block(new Vector2(16 + offset, 24 + offset), "block");
                blocks[16][25] = new Block(new Vector2(16 + offset, 25 + offset), "block");
                blocks[16][26] = new Block(new Vector2(16 + offset, 26 + offset), "block");
                blocks[16][27] = new Block(new Vector2(16 + offset, 27 + offset), "block");
                blocks[16][28] = new Block(new Vector2(16 + offset, 28 + offset), "block");
                blocks[16][29] = new Block(new Vector2(16 + offset, 29 + offset), "block");
                blocks[15][29] = new Block(new Vector2(15 + offset, 29 + offset), "block");
                blocks[14][29] = new Block(new Vector2(14 + offset, 29 + offset), "block");
                blocks[13][29] = new Block(new Vector2(13 + offset, 29 + offset), "block");
                blocks[12][29] = new Block(new Vector2(12 + offset, 29 + offset), "block");
                blocks[11][29] = new Block(new Vector2(11 + offset, 29 + offset), "block");
                blocks[9][29] = new Block(new Vector2(9 + offset, 29 + offset), "block");
                blocks[8][29] = new Block(new Vector2(8 + offset, 29 + offset), "block");
                blocks[7][29] = new Block(new Vector2(7 + offset, 29 + offset), "block");
                blocks[6][29] = new Block(new Vector2(6 + offset, 29 + offset), "block");
                blocks[5][29] = new Block(new Vector2(5 + offset, 29 + offset), "block");
                blocks[4][29] = new Block(new Vector2(4 + offset, 29 + offset), "block");
                blocks[3][29] = new Block(new Vector2(3 + offset, 29 + offset), "block");
                blocks[1][29] = new Block(new Vector2(1 + offset, 29 + offset), "block");

                blocks[5][30] = new Block(new Vector2(5 + offset, 30 + offset), "block");
                blocks[5][31] = new Block(new Vector2(5 + offset, 31 + offset), "block");
                blocks[5][32] = new Block(new Vector2(5 + offset, 32 + offset), "block");
                blocks[5][33] = new Block(new Vector2(5 + offset, 33 + offset), "block");
                blocks[5][34] = new Block(new Vector2(5 + offset, 34 + offset), "block");
                blocks[5][35] = new Block(new Vector2(5 + offset, 35 + offset), "block");
                blocks[4][35] = new Block(new Vector2(4 + offset, 35 + offset), "block");
                blocks[3][35] = new Block(new Vector2(3 + offset, 35 + offset), "block");
                blocks[2][35] = new Block(new Vector2(2 + offset, 35 + offset), "block");
                blocks[1][35] = new Block(new Vector2(1 + offset, 35 + offset), "block");

                blocks[16][29] = new Block(new Vector2(16 + offset, 29 + offset), "block");
                blocks[17][29] = new Block(new Vector2(17 + offset, 29 + offset), "block");
                blocks[18][29] = new Block(new Vector2(18 + offset, 29 + offset), "block");
                blocks[18][30] = new Block(new Vector2(18 + offset, 30 + offset), "block");
                blocks[18][31] = new Block(new Vector2(18 + offset, 31 + offset), "block");
                blocks[18][32] = new Block(new Vector2(18 + offset, 32 + offset), "block");
                blocks[18][33] = new Block(new Vector2(18 + offset, 33 + offset), "block");
                blocks[18][34] = new Block(new Vector2(18 + offset, 34 + offset), "block");
                blocks[18][35] = new Block(new Vector2(18 + offset, 35 + offset), "block");
                blocks[18][36] = new Block(new Vector2(18 + offset, 36 + offset), "block");
                blocks[18][37] = new Block(new Vector2(18 + offset, 37 + offset), "block");
                blocks[18][38] = new Block(new Vector2(18 + offset, 38 + offset), "block");
                blocks[18][39] = new Block(new Vector2(18 + offset, 39 + offset), "block");
                blocks[18][40] = new Block(new Vector2(18 + offset, 40 + offset), "block");
                blocks[18][41] = new Block(new Vector2(18 + offset, 41 + offset), "block");
                blocks[18][42] = new Block(new Vector2(18 + offset, 42 + offset), "block");
                blocks[18][43] = new Block(new Vector2(18 + offset, 43 + offset), "block");
                blocks[18][44] = new Block(new Vector2(18 + offset, 44 + offset), "block");
                blocks[18][46] = new Block(new Vector2(18 + offset, 46 + offset), "block");
                blocks[18][47] = new Block(new Vector2(18 + offset, 47 + offset), "block");
                blocks[18][48] = new Block(new Vector2(18 + offset, 48 + offset), "block");
                blocks[18][49] = new Block(new Vector2(18 + offset, 49 + offset), "block");

                floorPads.add(new FloorPad("firetile-01", new Vector2(17 + offset, 32 + offset), FloorPad.Type.FIRE, 0));
                floorPads.add(new FloorPad("firetile-01", new Vector2(16 + offset, 32 + offset), FloorPad.Type.FIRE, 0));
                floorPads.add(new FloorPad("firetile-01", new Vector2(15 + offset, 32 + offset), FloorPad.Type.FIRE, 0));
                floorPads.add(new FloorPad("firetile-01", new Vector2(14 + offset, 32 + offset), FloorPad.Type.FIRE, 0));
                floorPads.add(new FloorPad("firetile-01", new Vector2(13 + offset, 32 + offset), FloorPad.Type.FIRE, 0));
                floorPads.add(new FloorPad("firetile-01", new Vector2(12 + offset, 32 + offset), FloorPad.Type.FIRE, 0));
                floorPads.add(new FloorPad("firetile-01", new Vector2(11 + offset, 32 + offset), FloorPad.Type.FIRE, 0));
                floorPads.add(new FloorPad("firetile-01", new Vector2(10 + offset, 32 + offset), FloorPad.Type.FIRE, 0));
                floorPads.add(new FloorPad("firetile-01", new Vector2(9 + offset, 32 + offset), FloorPad.Type.FIRE, 0));
                floorPads.add(new FloorPad("firetile-01", new Vector2(8 + offset, 32 + offset), FloorPad.Type.FIRE, 0));
                floorPads.add(new FloorPad("firetile-01", new Vector2(8 + offset, 33 + offset), FloorPad.Type.FIRE, 0));
                floorPads.add(new FloorPad("firetile-01", new Vector2(8 + offset, 34 + offset), FloorPad.Type.FIRE, 0));
                floorPads.add(new FloorPad("firetile-01", new Vector2(8 + offset, 35 + offset), FloorPad.Type.FIRE, 0));
                floorPads.add(new FloorPad("firetile-01", new Vector2(8 + offset, 36 + offset), FloorPad.Type.FIRE, 0));
                floorPads.add(new FloorPad("firetile-01", new Vector2(8 + offset, 37 + offset), FloorPad.Type.FIRE, 0));

                Statue statue = new Statue(new Vector2(1 + offset, 39 + offset), 10, 1, 1, 0, "statue-01", Sprite.Effect.FIRE);
                statues.add(statue);
                blocks[1][39] = statue;
                statue = new Statue(new Vector2(1 + offset, 44 + offset), 10, 1, 1, 0, "statue-01", Sprite.Effect.FIRE);
                statues.add(statue);
                blocks[1][44] = statue;

                statue = new Statue(new Vector2(6 + offset, 30 + offset), 10, 1, 1, 90, "statue-01", Sprite.Effect.FIRE);
                statues.add(statue);
                blocks[6][30] = statue;
                statue = new Statue(new Vector2(7 + offset, 48 + offset), 10, 1, 1, 270, "statue-01", Sprite.Effect.FIRE);
                statues.add(statue);
                blocks[7][48] = statue;

                statue = new Statue(new Vector2(17 + offset, 39 + offset), 10, 1, 1, 180, "statue-01", Sprite.Effect.FIRE);
                statues.add(statue);
                blocks[17][39] = statue;
                statue = new Statue(new Vector2(17 + offset, 44 + offset), 10, 1, 1, 180, "statue-01", Sprite.Effect.FIRE);
                statues.add(statue);
                blocks[17][44] = statue;

                Wall wall = new Wall(new Vector2(18 + offset, 45 + offset), 90, Block.getSIZE(), Block.getSIZE() / 4, Wall.WallType.Type.DOOR, true);
                blocks[18][45] = wall;
                List<GameObject> triggerObjects = new ArrayList<>();
                FillableBlock torch1 = new FillableBlock(new Vector2(10 + offset, 48 + offset), 10, FillableBlock.FillableType.TORCH, 1, 1, 0, null, "torch");
                FillableBlock torch2 = new FillableBlock(new Vector2(10 + offset, 35 + offset), 10, FillableBlock.FillableType.TORCH, 1, 1, 0, null, "torch");
                torch1.toggleActive();
                torch2.toggleActive();
                triggerObjects.add(wall);
                triggerObjects.add(torch1);
                triggerObjects.add(torch2);
                FloorPad floorPad = new FloorPad("buttonTile-01", new Vector2(1 + offset, 48 + offset), FloorPad.Type.TRIGGER, triggerObjects);
                floorPads.add(floorPad);
                blocks[10][48] = torch1;
                blocks[10][35] = torch2;

                blocks[19][32] = new Block(new Vector2(19 + offset, 32 + offset), "block");
                blocks[20][32] = new Block(new Vector2(20 + offset, 32 + offset), "block");
                blocks[21][32] = new Block(new Vector2(21 + offset, 32 + offset), "block");
                blocks[22][32] = new Block(new Vector2(22 + offset, 32 + offset), "block");
                blocks[23][32] = new Block(new Vector2(23 + offset, 32 + offset), "block");
                blocks[24][32] = new Block(new Vector2(24 + offset, 32 + offset), "block");
                blocks[25][32] = new Block(new Vector2(25 + offset, 32 + offset), "block");
                blocks[26][32] = new Block(new Vector2(26 + offset, 32 + offset), "block");
                blocks[27][32] = new Block(new Vector2(27 + offset, 32 + offset), "block");
                blocks[28][32] = new Block(new Vector2(28 + offset, 32 + offset), "block");
                blocks[29][32] = new Block(new Vector2(29 + offset, 32 + offset), "block");
                blocks[30][32] = new Block(new Vector2(30 + offset, 32 + offset), "block");
                blocks[31][32] = new Block(new Vector2(31 + offset, 32 + offset), "block");
                blocks[32][32] = new Block(new Vector2(32 + offset, 32 + offset), "block");
                blocks[33][32] = new Block(new Vector2(33 + offset, 32 + offset), "block");
                blocks[34][32] = new Block(new Vector2(34 + offset, 32 + offset), "block");
                blocks[35][32] = new Block(new Vector2(35 + offset, 32 + offset), "block");
                blocks[36][32] = new Block(new Vector2(36 + offset, 32 + offset), "block");
                blocks[37][32] = new Block(new Vector2(37 + offset, 32 + offset), "block");
                blocks[38][32] = new Block(new Vector2(38 + offset, 32 + offset), "block");
                blocks[39][32] = new Block(new Vector2(39 + offset, 32 + offset), "block");
                blocks[40][32] = new Block(new Vector2(40 + offset, 32 + offset), "block");
                blocks[41][32] = new Block(new Vector2(41 + offset, 32 + offset), "block");
                blocks[42][32] = new Block(new Vector2(42 + offset, 32 + offset), "block");
                blocks[43][32] = new Block(new Vector2(43 + offset, 32 + offset), "block");
                blocks[45][32] = new Block(new Vector2(45 + offset, 32 + offset), "block");
                blocks[46][32] = new Block(new Vector2(46 + offset, 32 + offset), "block");
                blocks[47][32] = new Block(new Vector2(47 + offset, 32 + offset), "block");
                blocks[48][32] = new Block(new Vector2(48 + offset, 32 + offset), "block");

                blocks[39][31] = new Block(new Vector2(39 + offset, 31 + offset), "block");
                blocks[39][30] = new Block(new Vector2(39 + offset, 30 + offset), "block");
                blocks[39][29] = new Block(new Vector2(39 + offset, 29 + offset), "block");
                blocks[39][28] = new Block(new Vector2(39 + offset, 28 + offset), "block");
                blocks[39][27] = new Block(new Vector2(39 + offset, 27 + offset), "block");
                blocks[39][26] = new Block(new Vector2(39 + offset, 26 + offset), "block");
                blocks[39][25] = new Block(new Vector2(39 + offset, 25 + offset), "block");
                blocks[39][24] = new Block(new Vector2(39 + offset, 24 + offset), "block");
                blocks[39][23] = new Block(new Vector2(39 + offset, 23 + offset), "block");
                blocks[39][22] = new Block(new Vector2(39 + offset, 22 + offset), "block");
                blocks[39][21] = new Block(new Vector2(39 + offset, 21 + offset), "block");
                blocks[39][20] = new Block(new Vector2(39 + offset, 20 + offset), "block");
                blocks[39][19] = new Block(new Vector2(39 + offset, 19 + offset), "block");
                blocks[39][18] = new Block(new Vector2(39 + offset, 18 + offset), "block");
                blocks[39][17] = new Block(new Vector2(39 + offset, 17 + offset), "block");
                blocks[39][16] = new Block(new Vector2(39 + offset, 16 + offset), "block");
                blocks[39][15] = new Block(new Vector2(39 + offset, 15 + offset), "block");
                blocks[39][14] = new Block(new Vector2(39 + offset, 14 + offset), "block");
                blocks[39][13] = new Block(new Vector2(39 + offset, 13 + offset), "block");
                blocks[39][12] = new Block(new Vector2(39 + offset, 12 + offset), "block");
                blocks[39][11] = new Block(new Vector2(39 + offset, 11 + offset), "block");
                blocks[39][10] = new Block(new Vector2(39 + offset, 10 + offset), "block");
                blocks[39][9] = new Block(new Vector2(39 + offset, 9 + offset), "block");
                blocks[39][8] = new Block(new Vector2(39 + offset, 8 + offset), "block");
                blocks[39][7] = new Block(new Vector2(39 + offset, 7 + offset), "block");
                blocks[39][6] = new Block(new Vector2(39 + offset, 6 + offset), "block");
                blocks[39][3] = new Block(new Vector2(39 + offset, 3 + offset), "block");
                blocks[39][2] = new Block(new Vector2(39 + offset, 2 + offset), "block");
                blocks[39][1] = new Block(new Vector2(39 + offset, 1 + offset), "block");


                FillableBlock chest = new FillableBlock(new Vector2(4 + offset, 34 + offset), 10, FillableBlock.FillableType.CHEST, 1, 1, 0, new RecipeHolder(), "chest");
                chest.getInput().addInventory(new Consumable(Consumable.ConsumableType.POTATO, 10));
                Fillable jar = new Fillable(Item.ItemType.JAR, 10);
                jar.setFilled(true);
                chest.getInput().addInventory(jar);
                blocks[4][34] = chest;

                blocks[27][1] = new Block(new Vector2(27 + offset, 1 + offset), "block");
                blocks[27][2] = new Block(new Vector2(27 + offset, 2 + offset), "block");
                blocks[27][3] = new Block(new Vector2(27 + offset, 3 + offset), "block");
                blocks[27][4] = new Block(new Vector2(27 + offset, 4 + offset), "block");
                blocks[27][5] = new FillableBlock(new Vector2(27 + offset, 5 + offset), 10, FillableBlock.FillableType.TORCH, 1, 1, 0, null, "torch");
                blocks[27][6] = new Block(new Vector2(27 + offset, 6 + offset), "block");
                blocks[27][7] = new Block(new Vector2(27 + offset, 7 + offset), "block");
                blocks[28][4] = new Block(new Vector2(28 + offset, 4 + offset), "block");
                blocks[28][5] = new Block(new Vector2(28 + offset, 5 + offset), "block");
                blocks[28][6] = new Block(new Vector2(28 + offset, 6 + offset), "block");

//                floorPads.add(new FloorPad("firetile-01", new Vector2(25 + offset, 2 + offset), FloorPad.Type.FIRE));
                floorPads.add(new FloorPad("firetile-01", new Vector2(25 + offset, 3 + offset), FloorPad.Type.FIRE, 0));
                floorPads.add(new FloorPad("firetile-01", new Vector2(25 + offset, 4 + offset), FloorPad.Type.FIRE, 0));

                floorPads.add(new FloorPad("firetile-01", new Vector2(20 + offset, 4 + offset), FloorPad.Type.FIRE, 0));
                floorPads.add(new FloorPad("firetile-01", new Vector2(15 + offset, 4 + offset), FloorPad.Type.FIRE, 0));
                floorPads.add(new FloorPad("firetile-01", new Vector2(10 + offset, 4 + offset), FloorPad.Type.FIRE, 0));
                floorPads.add(new FloorPad("firetile-01", new Vector2(5 + offset, 4 + offset), FloorPad.Type.FIRE, 0));

                blocks[10][4] = new FillableBlock(new Vector2(10 + offset, 5 + offset), 10, FillableBlock.FillableType.TORCH, 1, 1, 0, null, "torch");
                blocks[10][25] = new FillableBlock(new Vector2(10 + offset, 25 + offset), 10, FillableBlock.FillableType.TORCH, 1, 1, 0, null, "torch");
                blocks[20][45] = new FillableBlock(new Vector2(20 + offset, 45 + offset), 10, FillableBlock.FillableType.TORCH, 1, 1, 0, null, "torch");
                blocks[20][35] = new FillableBlock(new Vector2(20 + offset, 35 + offset), 10, FillableBlock.FillableType.TORCH, 1, 1, 0, null, "torch");
                blocks[30][45] = new FillableBlock(new Vector2(30 + offset, 45 + offset), 10, FillableBlock.FillableType.TORCH, 1, 1, 0, null, "torch");
                blocks[30][35] = new FillableBlock(new Vector2(30 + offset, 35 + offset), 10, FillableBlock.FillableType.TORCH, 1, 1, 0, null, "torch");
                blocks[30][25] = new FillableBlock(new Vector2(10 + offset, 15 + offset), 10, FillableBlock.FillableType.TORCH, 1, 1, 0, null, "torch");
                blocks[48][5] = new FillableBlock(new Vector2(48 + offset, 5 + offset), 10, FillableBlock.FillableType.TORCH, 1, 1, 0, null, "torch");
                blocks[48][45] = new FillableBlock(new Vector2(48 + offset, 45 + offset), 10, FillableBlock.FillableType.TORCH, 1, 1, 0, null, "torch");
                blocks[48][35] = new FillableBlock(new Vector2(48 + offset, 35 + offset), 10, FillableBlock.FillableType.TORCH, 1, 1, 0, null, "torch");
                blocks[48][25] = new FillableBlock(new Vector2(48 + offset, 25 + offset), 10, FillableBlock.FillableType.TORCH, 1, 1, 0, null, "torch");
                blocks[48][15] = new FillableBlock(new Vector2(48 + offset, 15 + offset), 10, FillableBlock.FillableType.TORCH, 1, 1, 0, null, "torch");

//                blocks[30][7] = new EnvironmentBlock(new Vector2(30 + offset, 7 + offset), new Material(FIRESTONE, 1), null, 1, 0, 0, 0,true, PICK, 10, "firestone-01", 2, 2);
//                blocks[25][9] = new EnvironmentBlock(new Vector2(25 + offset, 9 + offset), new Material(FIRESTONE, 1), null, 1, 0, 0, 0,true, PICK, 10, "firestone-02", 2, 2);
//                blocks[35][12] = new EnvironmentBlock(new Vector2(35 + offset, 12 + offset), new Material(FIRESTONE, 1), null, 1, 0, 0, 0,true, PICK, 10, "firestone-03", 2, 2);
        }
    }

    public Block[][] getBlocks() {
        return blocks;
    }

    public Block getBlock(int x, int y) {

        int houseX = x - number * 1000;
        int houseY = y - number * 1000;
        if (houseX >= 0 && houseX < internalWidth && houseY >= 0 && houseY < internalHeight) {
            GameObject block = blocks[houseX][houseY];
            if (block instanceof Block) return (Block) block;
        }
        return null;
    }

    public void putBlock(int x, int y, Block block) {
        blocks[x - number * 1000][y - number * 1000] = block;
    }

    public int getNumber() {
        return number;
    }

    public int getInternalWidth() {
        return internalWidth;
    }

    public void setInternalWidth(int internalWidth) {
        this.internalWidth = internalWidth;
    }

    public int getInternalHeight() {
        return internalHeight;
    }

    public void setInternalHeight(int internalHeight) {
        this.internalHeight = internalHeight;
    }

    public BuildingType getBuildingType() {
        return buildingType;
    }

    public List<Sprite> getSprites() {
        return sprites;
    }

    public List<FloorPad> getFloorPads() {
        return floorPads;
    }

    public List<Statue> getStatues() {
        return statues;
    }

    public void initSprites() {
        int offset = number * 1000;
        if (buildingType == BuildingType.DUNGEON) {
            sprites = new ArrayList<>();
            spawns = new ArrayList<>();
            AnimalSpawn animalSpawn = new AnimalSpawn(new Vector2(5 + offset, 5 + offset), 1, Animal.AnimalType.FIREBEAST1, 3, 1, number);
            while (animalSpawn.getPopulation() < animalSpawn.getMaxPopulation()) {
                sprites.add(animalSpawn.addAnimal());
                animalSpawn.addAnimalToSpawn(0);
            }
            spawns.add(animalSpawn);

            animalSpawn = new AnimalSpawn(new Vector2(7 + offset, 20 + offset), 1, Animal.AnimalType.FIREBEAST2, 5, 1, number);
            while (animalSpawn.getPopulation() < animalSpawn.getMaxPopulation()) {
                sprites.add(animalSpawn.addAnimal());
                animalSpawn.addAnimalToSpawn(0);
            }
            spawns.add(animalSpawn);

            animalSpawn = new AnimalSpawn(new Vector2(40 + offset, 40 + offset), 1, Animal.AnimalType.FIREBEAST3, 8, 1, number);
            while (animalSpawn.getPopulation() < animalSpawn.getMaxPopulation()) {
                sprites.add(animalSpawn.addAnimal());
                animalSpawn.addAnimalToSpawn(0);
            }
            spawns.add(animalSpawn);

            animalSpawn = new AnimalSpawn(new Vector2(42 + offset, 5 + offset), 1, Animal.AnimalType.FIREBEAST4, 2, 1, number);
            while (animalSpawn.getPopulation() < animalSpawn.getMaxPopulation()) {
                sprites.add(animalSpawn.addAnimal());
                animalSpawn.addAnimalToSpawn(0);
            }
            spawns.add(animalSpawn);

            AnimalSpawn animalSpawnWithTrigger = new AnimalSpawn(new Vector2(25 + offset, 25 + offset), 1, Animal.AnimalType.FIREBEAST5, 1, 1, number);

            List<GameObject> triggerObjects = new ArrayList<>();
            FillableBlock torch1 = new FillableBlock(new Vector2(20 + offset, 25 + offset), 10, FillableBlock.FillableType.TORCH, 1, 1, 0, null, "torch");
            FillableBlock torch2 = new FillableBlock(new Vector2(20 + offset, 15 + offset), 10, FillableBlock.FillableType.TORCH, 1, 1, 0, null, "torch");
            FillableBlock torch3 = new FillableBlock(new Vector2(30 + offset, 25 + offset), 10, FillableBlock.FillableType.TORCH, 1, 1, 0, null, "torch");
            FillableBlock torch4 = new FillableBlock(new Vector2(30 + offset, 15 + offset), 10, FillableBlock.FillableType.TORCH, 1, 1, 0, null, "torch");

            torch1.toggleActive();
            torch2.toggleActive();
            torch3.toggleActive();
            torch4.toggleActive();
            triggerObjects.add(animalSpawnWithTrigger);
            triggerObjects.add(torch1);
            triggerObjects.add(torch2);
            triggerObjects.add(torch3);
            triggerObjects.add(torch4);
            blocks[20][25] = torch1;
            blocks[20][15] = torch2;
            blocks[10][15] = torch3;
            blocks[30][15] = torch4;

            FloorPad floorPad = new FloorPad("buttonTile-01", new Vector2(25 + offset, 20 + offset), FloorPad.Type.TRIGGER, triggerObjects);
            floorPads.add(floorPad);
            spawns.add(animalSpawnWithTrigger);
        }
    }

    public void triggerAnimalSpawn(AnimalSpawn animalSpawnWithTrigger) {
        while (animalSpawnWithTrigger.getPopulation() < animalSpawnWithTrigger.getMaxPopulation()) {
            sprites.add(animalSpawnWithTrigger.addAnimal());
            animalSpawnWithTrigger.addAnimalToSpawn(0);
        }
    }
}
