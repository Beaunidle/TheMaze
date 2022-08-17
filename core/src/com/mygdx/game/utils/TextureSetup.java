package com.mygdx.game.utils;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class TextureSetup {

    public static void main(String[] args) {
        TexturePacker.process("./android/assets/", "./android/assets/", "textures.pack");
    }
}