package com.mygdx.game;

import static com.mygdx.game.model.pads.FloorPad.Type.IRRIGATION;
import static com.mygdx.game.model.pads.FloorPad.Type.MOVE;
import static com.mygdx.game.model.pads.FloorPad.Type.WATERFLOW;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.GameButton;
import com.mygdx.game.model.Inventory;
import com.mygdx.game.model.World;
import com.mygdx.game.model.environment.AnimalSpawn;
import com.mygdx.game.model.environment.AreaAffect;
import com.mygdx.game.model.environment.BloodStain;
import com.mygdx.game.model.environment.Tilled;
import com.mygdx.game.model.environment.blocks.Block;
import com.mygdx.game.model.environment.blocks.EnvironmentBlock;
import com.mygdx.game.model.environment.blocks.ExplodableBlock;
import com.mygdx.game.model.environment.blocks.FillableBlock;
import com.mygdx.game.model.environment.blocks.Grower;
import com.mygdx.game.model.environment.blocks.Wall;
import com.mygdx.game.model.items.Consumable;
import com.mygdx.game.model.items.Fillable;
import com.mygdx.game.model.items.Consumable;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.items.Magic;
import com.mygdx.game.model.items.Material;
import com.mygdx.game.model.items.Placeable;
import com.mygdx.game.model.moveable.AIPlayer;
import com.mygdx.game.model.moveable.Animal;
import com.mygdx.game.model.moveable.Player;
import com.mygdx.game.model.moveable.Projectile;
import com.mygdx.game.model.pads.BoostPad;
import com.mygdx.game.model.pads.FloorPad;
import com.mygdx.game.model.pads.GunPad;
import com.mygdx.game.model.pads.Pad;
import com.mygdx.game.utils.JoyStick;

import java.awt.Point;
import java.util.Random;

public class MapRenderer {

    //0.033 if I am using 30fps
    private static final float RUNNING_FRAME_DURATION = 0.12f;
    private static final float EXPLODE_FRAME_DURATION = 0.12f;
    private static final float ITEM_USE_FRAME_DURATION = 0.065F;

    TextureAtlas itemAtlas;
    TextureAtlas inventoryAtlas;
    TextureAtlas buttonsAtlas;
    BitmapFont font;
    TextureLoader textureLoader;
    private TextureRegion fireButtonTexture, useButtonTexture;

    private static float CAMERA_WIDTH = 100;
    private static float CAMERA_HEIGHT = 100f;

    private final World world;
    private final OrthographicCamera cam;
    private OrthographicCamera textCamera;

    /** for debug rendering **/
    private final ShapeRenderer debugRenderer = new ShapeRenderer();

    private final SpriteBatch spriteBatch;
    private final boolean debug;

//    public void setSize (int w, int h) {
//         pixels per unit on the X axis
//        float ppuX = (float) w / CAMERA_WIDTH;
//         pixels per unit on the Y axis
//        float ppuY = (float) h / CAMERA_HEIGHT;
//    }

    public MapRenderer(World world, SpriteBatch spriteBatch, boolean debug, BitmapFont font, TextureLoader textureLoader) {
        this.world = world;
        this.textureLoader = textureLoader;

        float aspectRatio = (float) Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();
        float cameraViewPortWidth = 1024; // Set the size of the viewport for the text to something big
//        float cameraViewPortHeight = cameraViewPortWidth * aspectRatio;
        this.cam = new OrthographicCamera(CAMERA_WIDTH * 1.85F, CAMERA_HEIGHT);
//        textCamera = new OrthographicCamera(cameraViewPortWidth, cameraViewPortHeight);

//        this.cam.position.x = Math.round(world.getBob().getCentrePosition().x);
//        this.cam.position.y = Math.round(world.getBob().getCentrePosition().y);
//        this.cam.position.set(world.getBob().getPosition().x, world.getBob().getPosition().y + 0.5F, 0);
        this.cam.position.set(300F/2F, 300F/2F, 0);
        this.cam.update();
        this.debug = debug;
        this.spriteBatch = spriteBatch;
        loadTextures();
        this.font = font;
    }

    public void moveCamX(float x) {
        cam.position.x = cam.position.x + x;
    }

    public void moveCamY(float y) {
        cam.position.y = cam.position.y + y;
    }

    public void adjustZoom(float adjust) {
        if (adjust > 0) {
            if (CAMERA_WIDTH < 300) {
                CAMERA_WIDTH = CAMERA_WIDTH + 100;
                CAMERA_HEIGHT = CAMERA_HEIGHT + 100;
            }
        }
        if (adjust < 0) {
            if (CAMERA_WIDTH > 100) {
                CAMERA_WIDTH = CAMERA_WIDTH - 100;
                CAMERA_HEIGHT = CAMERA_HEIGHT - 100;
            }
        }
        cam.viewportWidth = CAMERA_WIDTH * 1.85F;
        cam.viewportHeight =CAMERA_HEIGHT;
    }
    private void loadTextures() {

        itemAtlas = new TextureAtlas(Gdx.files.internal("items.atlas"));
        inventoryAtlas = new TextureAtlas(Gdx.files.internal("inventory.atlas"));
        buttonsAtlas = new TextureAtlas(Gdx.files.internal("buttons.atlas"));

        TextureAtlas buttonAtlas = new TextureAtlas(Gdx.files.internal("buttons.atlas"));
        fireButtonTexture = buttonAtlas.findRegion("start");
        useButtonTexture = buttonAtlas.findRegion("exit");
    }

    public void render() {
        spriteBatch.setProjectionMatrix(cam.combined);
        Player bob = world.getBob();

//        if (!bob.getShieldCircle().contains(this.cam.position.x, this.cam.position.y)) {
//            Vector2 camPosition = new Vector2(cam.position.x, cam.position.y);
//            Vector2 distance = new Vector2(bob.getCentrePosition()).sub(camPosition);
//            this.cam.position.x +=distance.x/35;
//            this.cam.position.y +=distance.y/35;
//            this.textCamera.position.x +=distance.x/35;
//            this.textCamera.position.y +=distance.y/35;
//        }

        this.cam.update();
        spriteBatch.begin();
        drawFloor();
        drawBlocks();
        drawBoostPads();
        drawFloorPads();
//        drawBloodStains();
//        drawAreaAffects();
//        drawProjectiles();
        drawAnimals();
        drawAis();
        drawButtons();
        drawBob();
        if (world.getMoveJoystick() != null) drawJoystick(world.getMoveJoystick());
        if (world.getFireJoystick() != null) drawJoystick(world.getFireJoystick());
        //onscreen writing
        spriteBatch.end();
        debugRenderer.setProjectionMatrix(cam.combined);
        debugRenderer.setAutoShapeType(true);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.RED);

        debugRenderer.end();
    }

    private void drawFloor() {
        for (int i = 0; i < world.getLevel().getWidth(); i++) {
            for (int j = 0; j < world.getLevel().getHeight(); j++) {
                spriteBatch.draw(textureLoader.getRegion("floor"), i, j, Block.getSIZE(), Block.getSIZE());
            }
        }
    }

    private void drawBlocks() {
        for (Block block : world.getDrawableBlocks((int)CAMERA_WIDTH, (int)CAMERA_HEIGHT)) {
            if (block.getBlockType() == null) {
                spriteBatch.draw(textureLoader.getRegion(block.getName()), block.getPosition().x, block.getPosition().y, Block.getSIZE(), Block.getSIZE());
                continue;
            }
            switch (block.getBlockType()) {
                case ENVIRONMENT:
                    Material material = ((EnvironmentBlock) block).getMaterial();
                    if (block.getDurability() > 0 ) {
                        spriteBatch.draw(textureLoader.getRegion(block.getName()), block.getPosition().x, block.getPosition().y, block.getBounds().getBoundingRectangle().width, block.getBounds().getBoundingRectangle().height);
                    } else {
                        spriteBatch.draw(textureLoader.getRegion("floor"), block.getPosition().x, block.getPosition().y, Block.getSIZE(), Block.getSIZE());
                    }
                    break;
                case WALL:
                    for (float rotation : ((Wall) block).getWalls().keySet()) {
                        Wall.WallType wall = ((Wall) block).getWalls().get(rotation);
                        if (wall != null) {
                            Polygon polygon = wall.getBounds();
                            spriteBatch.draw(textureLoader.getRegion(wall.getName()), polygon.getX(), polygon.getY(), 0, 0, Block.getSIZE(), Block.getSIZE()/2, 1F, 0.5F, rotation);
                        }
                    }
                    break;
                case BED:
                case FILLABLE:
                    Polygon polygon = block.getBounds();
                    float rotation = polygon.getRotation();
                    Rectangle rectangle = polygon.getBoundingRectangle();
                    if (rotation == 90 || rotation == 270) {
                        spriteBatch.draw(textureLoader.getRegion(block.getName()), polygon.getX(), polygon.getY(), polygon.getOriginX(), polygon.getOriginY(), rectangle.width, rectangle.height, 1F, 1F, rotation);
                    }
                    else {
                        spriteBatch.draw(textureLoader.getRegion(block.getName()), polygon.getX(), polygon.getY(), polygon.getOriginX(), polygon.getOriginY(), polygon.getBoundingRectangle().width, polygon.getBoundingRectangle().height, 1F, 1F, rotation);
                    }
                    break;
                case EXPLODABLE:
                    if (((ExplodableBlock) block).getState().equals(ExplodableBlock.State.RUBBLE)) {
                        spriteBatch.draw(textureLoader.getRegion("rubbleBlock"), block.getPosition().x, block.getPosition().y, Block.getSIZE(), Block.getSIZE());
                    } else {
                        spriteBatch.draw(((ExplodableBlock) block).getState().equals(ExplodableBlock.State.RED) ? textureLoader.getRegion("explodingBlockRed") : textureLoader.getRegion("explodingBlockYellow"), block.getPosition().x, block.getPosition().y, Block.getSIZE(), Block.getSIZE());
                    }
                    break;
                default:
                    spriteBatch.draw(textureLoader.getRegion(block.getName()), block.getPosition().x, block.getPosition().y, Block.getSIZE(), Block.getSIZE());
                    break;
            }
        }
    }

    private void drawBoostPads() {
        for (BoostPad boostPad : world.getLevel().getBoostPads()) {
            spriteBatch.draw(textureLoader.getRegion("boostPad"), boostPad.getPosition().x, boostPad.getPosition().y, Pad.getSIZE(), Pad.getSIZE());
            if (boostPad.getBoost() != null) {
                spriteBatch.draw(textureLoader.getRegion(boostPad.getName()),boostPad.getPosition().x + 0.33F, boostPad.getPosition().y + 0.35F,Pad.getSIZE()*0.8F, Pad.getSIZE()*0.8F);
            }
        }
    }

//    private void drawGunPads() {
//        for (GunPad gunPad : world.getLevel().getGunPads()) {
//            TextureRegion gunFrame = null;
//            switch (gunPad.getType()) {
//                case PISTOL:
//                    gunFrame = pistolTexture;
//                    break;
//                case SMG:
//                    gunFrame = smgTexture;
//                    break;
//                case SHOTGUN:
//                    gunFrame = shotgunTexture;
//                    break;
//                case ROCKET:
//                    gunFrame = rocketTexture;
//                    break;
//            }
//            if (gunFrame != null) {
//                spriteBatch.draw(gunFrame, gunPad.getPosition().x, gunPad.getPosition().y, GunPad.getSIZE(), GunPad.getSIZE());
//            }
//        }
//    }

    private void drawFloorPads() {
        for (FloorPad floorPad : world.getLevel().getFloorPads()) {
            TextureRegion floorFrame = textureLoader.getRegion(floorPad.getName());
            if (floorFrame != null) {
                if (floorPad.getType().equals(IRRIGATION)) {
                    spriteBatch.draw(floorFrame, floorPad.getPosition().x, floorPad.getPosition().y, Block.getSIZE() / 2, Block.getSIZE() / 2,
                            Block.getSIZE(), Block.getSIZE(), 1, 1, floorPad.isStraightPiece() ? floorPad.getRotation() : floorPad.getRotation() - 45);
                }
                if (floorPad.getType().equals(MOVE) || floorPad.getType().equals(WATERFLOW)) {
                    spriteBatch.draw(floorFrame, floorPad.getPosition().x, floorPad.getPosition().y, Block.getSIZE()/2, Block.getSIZE()/2,
                            Block.getSIZE(), Block.getSIZE(), 1, 1, floorPad.getRotation() + 90, true);
                } else {
                    spriteBatch.draw(floorFrame, floorPad.getPosition().x, floorPad.getPosition().y, GunPad.getSIZE(), GunPad.getSIZE());
                }
            }
        }
    }

//    private void drawAreaAffects() {
//        for (AreaAffect areaAffect : world.getAreaAffects()) {
//            if (!areaAffect.isFinished()) {
//                TextureRegion affectFrame = null;
//                switch (areaAffect.getAffectType()) {
//                    case EXPLOSION:
//                        affectFrame = (TextureRegion) explodeAnimation.getKeyFrame(world.getBob().getStateTime(), true);
//                        break;
//                    case LIGHTNING:
//                        Random rand = new Random();
//                        int r = 3;
//                        affectFrame = itemAtlas.findRegion("lightning-0" + (rand.nextInt(4) + 1));
//                }
//                spriteBatch.draw(affectFrame, areaAffect.getPosition().x - areaAffect.getWidth()/2, areaAffect.getPosition().y - areaAffect.getHeight()/2, areaAffect.getWidth()/2, areaAffect.getHeight()/2,
//                        areaAffect.getWidth(), areaAffect.getHeight(), 1F, 1F, 0, true);
//            }
//        }
//    }

//    private void drawProjectiles() {
//        for (Projectile projectile : world.getProjectiles()) {
//            TextureRegion bulletFrame = itemAtlas.findRegion(projectile.getName());
//            if (projectile.getProjectileType().equals(Projectile.ProjectileType.FIREBALL)) {
//                Random rand = new Random();
//                switch (rand.nextInt(4)) {
//                    case 1:
//                        bulletFrame = itemAtlas.findRegion("fireball-01");
//                        break;
//                    case 2:
//                        bulletFrame = itemAtlas.findRegion("fireball-02");
//                        break;
//                    case 3:
//                        bulletFrame = itemAtlas.findRegion("fireball-03");
//                        break;
//                    default:
//                        bulletFrame = itemAtlas.findRegion("fireball-04");
//                        break;
//                }
////                    itemHeight = 0.5F;
////                    itemWidth = 0.5f;
////                    itemRotation = 90;
////                    itemRadius = bob.getWidth()/2;
//            }
//
////            if (projectile.isExploding()) {
////                bulletFrame = (TextureRegion) (explodeAnimation.getKeyFrame(projectile.getStateTime(), true));
////                spriteBatch.draw(bulletFrame, projectile.getPosition().x, projectile.getPosition().y, 0, projectile.getHeight()/2,
////                        projectile.getWidth(), projectile.getHeight(), 2.5F, 5, projectile.getRotation(), true);
////            } else {
//            Polygon bounds = projectile.getBounds();
//            Vector2 pos = new Vector2(projectile.getBounds().getX(), projectile.getBounds().getY());
//            spriteBatch.draw(bulletFrame, pos.x, pos.y, bounds.getOriginX(), bounds.getOriginY(),
//                    projectile.getWidth(), projectile.getHeight(), 1, 1, bounds.getRotation(), true);
////            }
//        }
//    }

    private void drawWall(float rotation, Player bob, Point gridRef, Placeable wall) {
        String name = wall.getPlaceableType().equals(Placeable.PlaceableType.DOOR) ? "door" : "wall";
        TextureRegion region = itemAtlas.findRegion(name);
        if (rotation >= 45 && rotation < 135) {
            spriteBatch.draw(region, gridRef.x + 1, gridRef.y, 0, 0, 1F, 0.5F, 1F, 0.5F, 90);
        } else if (rotation >= 135 && rotation < 225) {
            spriteBatch.draw(region, gridRef.x + 1, gridRef.y + 1, 0, 0, 1F, 0.5F, 1F, 0.5F, 180);
        } else if (rotation >= 225 && rotation < 315) {
            spriteBatch.draw(region, gridRef.x, gridRef.y + 1, 0, 0, 1F, 0.5F, 1F, 0.5F, 270);
        } else { //if (rotation < 45 || rotation >= 315) {
            spriteBatch.draw(region, gridRef.x, gridRef.y, 0, 0, 1F, 0.5F, 1F, 0.5F, 0);
        }
    }

    private void drawBench(float rotation, Player player, Point gridRef, Item bench, float size) {
        String name = bench.getName();
        TextureRegion region = itemAtlas.findRegion(name);

        if (rotation >= 45 && rotation < 135) {
            spriteBatch.draw(region, gridRef.x, gridRef.y, 0.5F, 0.5F, 1F, size, 1F, 1F, 0);
        } else if (rotation >= 135 && rotation < 225) {
            spriteBatch.draw(region, gridRef.x, gridRef.y, 0.5F, 0.5F, 1F, size, 1F, 1F, 90);
        } else if (rotation >= 225 && rotation < 315) {
            spriteBatch.draw(region, gridRef.x, gridRef.y, 0.5F, 0.5F, 1F, size, 1F, 1F, 180);
        } else { //if (rotation < 45 || rotation >= 315) {
            spriteBatch.draw(region, gridRef.x, gridRef.y, 0.5F, 0.5F, 1F, size, 1F, 1F, 270);
        }
    }

    private void drawTargetCircle(Vector2 gridRef, float width, float height, TextureRegion textureToDraw) {
        spriteBatch.draw(textureToDraw, gridRef.x - width/2, gridRef.y - height/2,width/2, height/2, width, height, 1F, 1F, 0);
    }

    private void drawBob() {
        Player bob = world.getBob();
        TextureRegion bobFrame = bob.isInjured() ? textureLoader.getRegion("sprite-01")
                : (bob.getTorso() != null && bob.getTorso().getItemType().equals(Item.ItemType.ARMOUR)) ? textureLoader.getRegion("armour-01")
                : textureLoader.getRegion("sprite-01");

        float drawAngle = bob.getRotation();
        spriteBatch.draw(bobFrame, bob.getPosition().x, bob.getPosition().y, bob.getWidth()/2, bob.getHeight()/2, bob.getWidth()*2, bob.getHeight()*2,
                2, 2, drawAngle, true);
    }

    private void drawAis() {
        for (AIPlayer aiPlayer : world.getAIPlayers()) {

            TextureRegion aiFrame = textureLoader.getRegion("sprite-01");

            spriteBatch.draw(aiFrame, aiPlayer.getPosition().x, aiPlayer.getPosition().y, aiPlayer.getWidth()/2, aiPlayer.getHeight()/2, aiPlayer.getWidth(), aiPlayer.getHeight(),
                    1, 1, aiPlayer.getRotation(), true);
        }
    }

    private void drawAnimals() {
        for (Animal animal : world.getAnimals()) {
            Vector2 livesPos = animal.getCentrePosition();
            TextureRegion animalFrame = textureLoader.getRegion(animal.getName());
            spriteBatch.draw(animalFrame, animal.getPosition().x, animal.getPosition().y, animal.getWidth()/2, animal.getHeight()/2, animal.getWidth(), animal.getHeight(),
                    1, 1, animal.getRotation(), true);
        }
    }

    private void drawJoystick(JoyStick joyStick) {

        Vector2 midPoint = new Vector2(Gdx.app.getGraphics().getWidth()/2f, Gdx.app.getGraphics().getHeight()/2f);
        Vector2 touchPoint = joyStick.getPosition();
        float xDist = midPoint.x - touchPoint.x;
        float yDist = midPoint.y - touchPoint.y;
        float xRatio = 7.5F / midPoint.x;
        float yRatio = 4F / midPoint.y;

        float xVal = world.getBob().getPosition().x - (xDist * xRatio);
        float yVal = world.getBob().getPosition().y - (yDist * yRatio);

        Vector2 drawOuter;
        if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
            drawOuter = new Vector2(xVal - 0.5F, yVal + 0.5F);
        } else {
            drawOuter = new Vector2(xVal + 0.2F, yVal + 0.5F);
        }
//        spriteBatch.draw(joystickOuterTexture, drawOuter.x - JoyStick.getWIDTH() / 2, drawOuter.y - JoyStick.getWIDTH() / 2, 1, 1, 2F, 2, 1.00F, 1.00F, 0);
        if (joyStick.getDrag() == null) {
//            spriteBatch.draw(joystickInnerTexture, drawOuter.x - JoyStick.getWIDTH() / 2, drawOuter.y - JoyStick.getWIDTH() / 2, 1, 1, 2.0F, 2.0F, 1.00F, 1.00F, 0);
        } else {
            Vector2 dragPoint = joyStick.getDrag();
            float xDist2 = midPoint.x - dragPoint.x;
            float yDist2 = midPoint.y - dragPoint.y;

            float xVal2 = world.getBob().getPosition().x - (xDist2 * xRatio);
            float yVal2 = world.getBob().getPosition().y - (yDist2 * yRatio);
            float difX = touchPoint.x - dragPoint.x;
            float difY = touchPoint.y - dragPoint.y;
            Vector2 drawInner;
            if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
                drawInner = new Vector2(xVal2 - 0.5F, yVal2 + 0.5F);
            } else  {
                drawInner = new Vector2(xVal2 + 0.2F, yVal2 + 0.5F);
            }
            if (difX > 45) drawInner.x = drawOuter.x - 1;
            if (difX < -45) drawInner.x = drawOuter.x + 1;
            if (difY > 55) drawInner.y = drawOuter.y - 1;
            if (difY < -55) drawInner.y = drawOuter.y + 1;

//            spriteBatch.draw(joystickInnerTexture, drawInner.x - JoyStick.getWIDTH() / 2, drawInner.y - JoyStick.getWIDTH() / 2, 1, 1, 2.0F, 2.0F, 1.00F, 1.00F, 0);
        }
    }

    private void drawButtons() {

        if (world.getButtons().isEmpty()) {
            world.getButtons().add(new GameButton(new Vector2(world.getBob().getCentrePosition().x, world.getBob().getCentrePosition().y), world.getBob().getWidth()*5, GameButton.Type.USE));
        }

        for (GameButton gameButton : world.getButtons()) {
            TextureRegion buttonFrame = null;
            buttonFrame = useButtonTexture;
            float xish, yish;

            if (buttonFrame != null) spriteBatch.draw(buttonFrame, gameButton.getArea().x, gameButton.getArea().y, 1, 1, 1, 1, 5.00F, 5.00F, 0);
        }
    }


}
