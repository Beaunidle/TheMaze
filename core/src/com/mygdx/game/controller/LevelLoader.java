package com.mygdx.game.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.AIPlayer;
import com.mygdx.game.model.Block;
import com.mygdx.game.model.BoostPad;
import com.mygdx.game.model.ExplodableBlock;
import com.mygdx.game.model.GunPad;
import com.mygdx.game.model.Level;
import com.mygdx.game.model.Player;
import com.mygdx.game.model.SpawnPoint;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static com.mygdx.game.model.Gun.Type.PISTOL;
import static com.mygdx.game.model.Gun.Type.ROCKET;
import static com.mygdx.game.model.Gun.Type.SHOTGUN;
import static com.mygdx.game.model.Gun.Type.SMG;

public class LevelLoader {


    public Level loadLevel(int number) {
        Level level = new Level();

        try {
             loadFile(level, number);
        } catch (IOException e) {
            System.out.println(e.toString());
        }

        return level;
    }

    private void loadFile(Level level, int number) throws IOException{

        FileHandle file = Gdx.files.internal("Levels/level0" + number + ".txt");

        int spawnCount = 0;
        BufferedReader br = new BufferedReader(new InputStreamReader(file.read()));
        String st;
        while ((st = br.readLine()) != null) {
            String[] values = st.split(",");

            switch (values[0]) {
                case "PLAYER":
                case "AI":
                    level.getSpawnPoints().put(spawnCount, new SpawnPoint(new Vector2(Integer.valueOf(values[1]), Integer.valueOf(values[2]))));
                    spawnCount++;
                    break;
                case "BLOCK":
                    level.getBlocks()[Integer.valueOf(values[1])][Integer.valueOf(values[2])] = new Block(new Vector2(Integer.valueOf(values[1]), Integer.valueOf(values[2])));
                    break;
                case "EXPLODING":
                    ExplodableBlock eb = new ExplodableBlock(new Vector2(Integer.valueOf(values[1]), Integer.valueOf(values[2])));
                    level.getBlocks()[Integer.valueOf(values[1])][Integer.valueOf(values[2])] = eb;
                    level.getExplodableBlocks().add(eb);
                    break;
                case "PISTOL":
                    level.getGunPads().add(new GunPad(new Vector2(Integer.valueOf(values[1]), Integer.valueOf(values[2])), PISTOL));
                    break;
                case "SMG":
                    level.getGunPads().add(new GunPad(new Vector2(Integer.valueOf(values[1]), Integer.valueOf(values[2])), SMG));
                    break;
                case "SHOTGUN":
                    level.getGunPads().add(new GunPad(new Vector2(Integer.valueOf(values[1]), Integer.valueOf(values[2])), SHOTGUN));
                    break;
                case "ROCKET":
                    level.getGunPads().add(new GunPad(new Vector2(Integer.valueOf(values[1]), Integer.valueOf(values[2])), ROCKET));
                    break;
                case "BOOST":
                    level.getBoostPads().add(new BoostPad(new Vector2(Integer.valueOf(values[1]), Integer.valueOf(values[2]))));
                case "FLOOR":
                    break;
            }
        }
    }

}
