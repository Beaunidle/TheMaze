package com.mygdx.game;

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
import com.mygdx.game.model.items.Fillable;
import com.mygdx.game.model.items.Food;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.items.Magic;
import com.mygdx.game.model.items.Material;
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

    private static final float RUNNING_FRAME_DURATION = 0.12f;
    private static final float EXPLODE_FRAME_DURATION = 0.12f;
    private static final float ITEM_USE_FRAME_DURATION = 0.065F;

    TextureAtlas itemAtlas;
    TextureAtlas inventoryAtlas;
    TextureAtlas buttonsAtlas;
    BitmapFont font;
    //Textures
    private TextureRegion playerIdle, armourIdle, playerInjured, playerDead, playerHand, playerHead;
    private TextureRegion animalTexture;
    private TextureRegion heartTexture, manaTexture;
    private TextureRegion blockTexture, coalTexture, stoneTexture, woodTexture, meatTexture, cookedmeatTexture, berrypasteTexture, berryTexture, grassTexture, wallTexture, doorTexture, doorOpenTexture,
            stickTexture, pebbleTexture;
    private TextureRegion blockExplodeRed, blockExplodeYellow, blockRubble;
    private TextureRegion pistolTexture, smgTexture, shotgunTexture, rocketTexture, boostPadTexture;
    //    private TextureRegion bulletTexture,
    private TextureRegion homingBoostTexture, speedBoostTexture, shieldBoostTexture, damageBoostTexture;
    private TextureRegion spikeTexture, slimeTexture, moveTexture;
    private TextureRegion joystickOuterTexture, joystickInnerTexture;
    private TextureRegion floorTexture, tilledTexture;
    private TextureRegion fireButtonTexture, useButtonTexture;
    private TextureRegion reloadingTexture;
    private TextureRegion jarTexture, jarFullTexture, shieldTexture;

    //Animations
    private Animation walkAnimation;
    private Animation armourAnimation;
    private  Animation walkInjuredAnimation;
    private  Animation explodeAnimation;
    private Animation shieldAnimation;
    private Animation pickAnimation, swordAnimation;

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

    public MapRenderer(World world, SpriteBatch spriteBatch, boolean debug, BitmapFont font) {
        this.world = world;

        float aspectRatio = (float) Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();
        float cameraViewPortWidth = 1024; // Set the size of the viewport for the text to something big
//        float cameraViewPortHeight = cameraViewPortWidth * aspectRatio;
        this.cam = new OrthographicCamera(CAMERA_WIDTH * 1.85F, CAMERA_HEIGHT);
//        textCamera = new OrthographicCamera(cameraViewPortWidth, cameraViewPortHeight);

//        this.cam.position.x = Math.round(world.getBob().getCentrePosition().x);
//        this.cam.position.y = Math.round(world.getBob().getCentrePosition().y);
//        this.cam.position.set(world.getBob().getPosition().x, world.getBob().getPosition().y + 0.5F, 0);
        this.cam.position.set(300/2, 300/2, 0);
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
        playerIdle = itemAtlas.findRegion("sprite-01");
        armourIdle = itemAtlas.findRegion("armour-01");
        playerInjured = itemAtlas.findRegion("sprite-01");
        playerDead = itemAtlas.findRegion("dead");
        playerHand = itemAtlas.findRegion("hand-01");
        playerHead = itemAtlas.findRegion("head-01");
        heartTexture = itemAtlas.findRegion("heart");
        manaTexture = itemAtlas.findRegion("mana");

        animalTexture = itemAtlas.findRegion("animal");

        blockTexture = itemAtlas.findRegion("block");
        coalTexture = itemAtlas.findRegion("coal");
        stoneTexture = itemAtlas.findRegion("stone");
        woodTexture = itemAtlas.findRegion("wood");
        meatTexture = itemAtlas.findRegion("meat");
        cookedmeatTexture = itemAtlas.findRegion("cookedmeat");
        berrypasteTexture = itemAtlas.findRegion("berrypaste");
        berryTexture = itemAtlas.findRegion("berrybush");
        grassTexture = itemAtlas.findRegion("grassbush");
        stickTexture = itemAtlas.findRegion("stick");
        pebbleTexture = itemAtlas.findRegion("pebble");
        wallTexture = itemAtlas.findRegion("wall");
        doorTexture = itemAtlas.findRegion("door");
        doorOpenTexture = itemAtlas.findRegion("dooropen");
        floorTexture = itemAtlas.findRegion("floor");
        tilledTexture = itemAtlas.findRegion("tilled");
        blockExplodeRed = itemAtlas.findRegion("explodingBlockRed");
        blockExplodeYellow = itemAtlas.findRegion("explodingBlockYellow");
        blockRubble = itemAtlas.findRegion("rubbleBlock");
        pistolTexture = itemAtlas.findRegion("gunPistol");
        smgTexture = itemAtlas.findRegion("gunSMG");
        shotgunTexture = itemAtlas.findRegion("gunShotgun");
        rocketTexture = itemAtlas.findRegion("gunRocket");
//        bulletTexture = itemAtlas.findRegion("bullet90");
        boostPadTexture = itemAtlas.findRegion("boostPad");
        homingBoostTexture = itemAtlas.findRegion("homingBoost");
        speedBoostTexture = itemAtlas.findRegion("speedBoost");
        shieldBoostTexture = itemAtlas.findRegion("shieldBoost");
        damageBoostTexture = itemAtlas.findRegion("damageBoost");
        spikeTexture = itemAtlas.findRegion("padSpike");
        slimeTexture = itemAtlas.findRegion("padSticky");
        moveTexture = itemAtlas.findRegion("padMove");
        joystickOuterTexture = itemAtlas.findRegion("joystickOuter");
        joystickInnerTexture = itemAtlas.findRegion("joystickInner");
        reloadingTexture = itemAtlas.findRegion("reloading");

        TextureRegion[] walkFrames = new TextureRegion[3];
        for (int i = 0; i < 3; i++) {
            walkFrames[i] = itemAtlas.findRegion("sprite-01");
        }
        walkAnimation = new Animation(RUNNING_FRAME_DURATION, walkFrames);

        TextureRegion[] armourFrames = new TextureRegion[3];
        for (int i = 0; i < 3; i++) {
            armourFrames[i] = itemAtlas.findRegion("armour-01");
        }
        armourAnimation = new Animation(RUNNING_FRAME_DURATION, armourFrames);

        TextureRegion[] walkInjuredFrames = new TextureRegion[3];
        for (int i = 0; i < 3; i++) {
            walkInjuredFrames[i] = itemAtlas.findRegion("sprite-01");
        }
        walkInjuredAnimation = new Animation(RUNNING_FRAME_DURATION, walkInjuredFrames);

        TextureRegion[] explodeFrames = new TextureRegion[5];
        for (int i = 0; i < 3 ; i++) {
            explodeFrames[i] = itemAtlas.findRegion("explode-0" + (i + 1));
        }
        explodeFrames[3] = itemAtlas.findRegion("explode-02");
        explodeFrames[4] = itemAtlas.findRegion("explode-03");
        explodeAnimation = new Animation(EXPLODE_FRAME_DURATION, explodeFrames);

        TextureRegion[] shieldFrames = new TextureRegion[3];
        shieldFrames[0] = itemAtlas.findRegion("shieldBlue");
//        shieldFrames[1] = itemAtlas.findRegion("shieldPurple");
//        shieldFrames[2] = itemAtlas.findRegion("shieldRed");
        shieldFrames[1] = itemAtlas.findRegion("shieldOrange");
//        shieldFrames[4] = itemAtlas.findRegion("shieldYellow");
        shieldFrames[2] = itemAtlas.findRegion("shieldGreen");
//        shieldFrames[6] = itemAtlas.findRegion("shieldTurquoise");

        shieldAnimation = new Animation(RUNNING_FRAME_DURATION, shieldFrames);
//        TextureRegion[] walkLeftFrames = new TextureRegion[6];
//
//        for (int i = 0; i < 6; i++) {
//            walkLeftFrames[i] = new TextureRegion(walkRightFrames[i]);
//            walkLeftFrames[i].flip(true, false);
//        }
//        walkLeftAnimation = new Animation(RUNNING_FRAME_DURATION, walkLeftFrames);

        jarTexture = inventoryAtlas.findRegion("inv_jar");
        jarFullTexture = inventoryAtlas.findRegion("inv_jarFull");
        shieldTexture = itemAtlas.findRegion("shield-01");
        TextureRegion[] pickFrames = new TextureRegion[5];
        pickFrames[0] = inventoryAtlas.findRegion("inv_pick01");
        pickFrames[1] = inventoryAtlas.findRegion("inv_pick02");
        pickFrames[2] = inventoryAtlas.findRegion("inv_pick03");
        pickFrames[3] = inventoryAtlas.findRegion("inv_pick04");
        pickFrames[4] = inventoryAtlas.findRegion("inv_pick05");

        pickAnimation = new Animation(ITEM_USE_FRAME_DURATION, pickFrames);

        TextureRegion[] swordFrames = new TextureRegion[5];
        swordFrames[0] = inventoryAtlas.findRegion("inv_sword01");
        swordFrames[1] = inventoryAtlas.findRegion("inv_sword02");
        swordFrames[2] = inventoryAtlas.findRegion("inv_sword03");
        swordFrames[3] = inventoryAtlas.findRegion("inv_sword04");
        swordFrames[4] = inventoryAtlas.findRegion("inv_sword05");

        swordAnimation = new Animation(ITEM_USE_FRAME_DURATION, swordFrames);

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
        drawGunPads();
        drawFloorPads();
        drawBloodStains();
        drawAreaAffects();
        drawProjectiles();
        drawAnimals();
        drawAis();
        drawButtons();
        drawBob();
        if (world.getMoveJoystick() != null) drawJoystick(world.getMoveJoystick());
        if (world.getFireJoystick() != null) drawJoystick(world.getFireJoystick());
        //onscreen writing
        spriteBatch.end();
//        drawDebug();
//        aiDebug();
//        drawCollisionBlocks();
//        if (debug) drawDebug();
        debugRenderer.setProjectionMatrix(cam.combined);
        debugRenderer.setAutoShapeType(true);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.RED);
        for (AreaAffect areaAffect : world.getAreaAffects()) {
            debugRenderer.circle(areaAffect.getBounds().x, areaAffect.getBounds().y, areaAffect.getBounds().radius);
        }
        if (world.getLocateExplosion() != null) {
            debugRenderer.setColor(Color.GREEN);
            debugRenderer.circle(world.getLocateExplosion().x, world.getLocateExplosion().y, 5);
            debugRenderer.setColor(Color.PURPLE);
            debugRenderer.circle(world.getLocateExplosion().x, world.getLocateExplosion().y, 2);
        }
        debugRenderer.end();
    }

    private void aiDebug(){
        debugRenderer.setProjectionMatrix(cam.combined);
        debugRenderer.setAutoShapeType(true);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(Color.RED);
//        debugRenderer.polygon(world.getBob().getViewCircle().getTransformedVertices());
        for (AIPlayer aiPlayer :  world.getAIPlayers()) {
//            Rectangle rect = aiPlayer.getViewCircle().getBoundingRectangle();
            Block[][] blocks = aiPlayer.getView().getBlocks();
            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 8; j++) {
                    Block block = null;
                    if (blocks[i][j] != null) block = blocks[i][j];
                    if (block != null) debugRenderer.polygon(block.getBounds().getTransformedVertices());
                }
            }
            debugRenderer.setColor(Color.PURPLE);
            Block[] blocking = aiPlayer.getView().getBlockingWall();
            for (Block block : blocking) {
                if (block != null)
                    debugRenderer.polygon(block.getBounds().getTransformedVertices());
            }

            debugRenderer.setColor(Color.BLUE);
            if (aiPlayer.getTarget() != null) debugRenderer.circle(aiPlayer.getTarget().x, aiPlayer.getTarget().y, 2);
//            debugRenderer.setColor(Color.GREEN);
//            debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }
        debugRenderer.end();
    }
    private void drawFloor() {
        for (int i = 0; i < world.getLevel().getWidth(); i++) {
            for (int j = 0; j < world.getLevel().getHeight(); j++) {
                spriteBatch.draw(floorTexture, i, j, Block.getSIZE(), Block.getSIZE());
            }
        }
    }
    private void drawBlocks() {
        for (Block block : world.getDrawableBlocks((int)CAMERA_WIDTH, (int)CAMERA_HEIGHT)) {
            if (block instanceof Grower) {
                Grower grower = (Grower) block;
                StringBuilder sb = new StringBuilder();
                switch (grower.getCropType()) {
                    case POTATO:
                        sb.append("potato-");
                        break;
                    default:
                        break;
                }
                switch (grower.getGrowthState()) {
                    case SEEDLING:
                        sb.append("seedling");
                        break;
                    case MIDDLING:
                        sb.append("middling");
                        break;
                    case MATURE:
                        sb.append("mature");
                        break;
                }
                TextureRegion growerTexture = itemAtlas.findRegion(sb.toString());
                spriteBatch.draw(growerTexture, block.getPosition().x, block.getPosition().y, Block.getSIZE(), Block.getSIZE());

            } else if (block instanceof Tilled) {
                spriteBatch.draw(tilledTexture, block.getPosition().x, block.getPosition().y, Block.getSIZE(), Block.getSIZE());
            } else if (block instanceof Wall) {
//                spriteBatch.draw(floorTexture, block.getPosition().x, block.getPosition().y, Block.getSIZE(), Block.getSIZE());
                for (float rotation : ((Wall) block).getWalls().keySet()) {
                    Wall.WallType wall = ((Wall) block).getWalls().get(rotation);
                    if (wall != null) {
                        Polygon polygon = wall.getBounds();
                        TextureRegion wallToDraw;
                        if (wall.isDoor()) {
                            if (wall.isOpen()) {
                                wallToDraw = doorOpenTexture;
                            } else {
                                wallToDraw = doorTexture;
                            }
                        } else {
                            wallToDraw = wallTexture;
                        }
                        spriteBatch.draw(wallToDraw, polygon.getX(), polygon.getY(), 0, 0, Block.getSIZE(), Block.getSIZE()/2, 1F, 0.5F, rotation);
                    }
                }
//                    if (((Wall) block).getWalls().get(rotation) != null) {
//                        spriteBatch.draw(wallTexture, block.getPosition().x, block.getPosition().y, 0, 0, Block.getSIZE(), Block.getSIZE()/2, 1F, 0.5F, rotation);
//                    }
//                }
//                spriteBatch.draw(wallTexture, block.getPosition().x, block.getPosition().y, 0, 0, Block.getSIZE(), Block.getSIZE()/2, 1F, 0.5F, ((Wall) block).getRotation());
            } else if (block instanceof FillableBlock) {
                Polygon polygon = block.getBounds();
                float rotation = polygon.getRotation();
                TextureRegion fillableToDraw = null;
//                switch (((FillableBlock) block).getType()) {
//                    case CAMPFIRE:
//                        fillableToDraw = new TextureRegion(itemAtlas.findRegion(((FillableBlock) block).isActive() ? "fire-burning" : "fire"));
//                        break;
//                    case BENCHHEALER:
//                        fillableToDraw = new TextureRegion(itemAtlas.findRegion("bench-healer"));
//                        break;
//                }
                if (((FillableBlock) block).getFillableType().equals(FillableBlock.FillableType.CAMPFIRE)) {
                    fillableToDraw = new TextureRegion(itemAtlas.findRegion(((FillableBlock) block).isActive() ? "fire-burning" : "fire"));
                } else {
                    fillableToDraw = new TextureRegion(itemAtlas.findRegion(((FillableBlock) block).getName()));
                }
                spriteBatch.draw(fillableToDraw, polygon.getX(), polygon.getY(), polygon.getOriginX(), polygon.getOriginY(), polygon.getBoundingRectangle().width, polygon.getBoundingRectangle().height, 1F, 1F,
                        rotation == 90 || rotation == 270 ? rotation - 90 : rotation);
//                spriteBatch.draw(new TextureRegion(itemAtlas.findRegion(((FillableBlock) block).isActive() ? "fire-burning" : "fire")), block.getPosition().x, block.getPosition().y, block.getBounds().getBoundingRectangle().getWidth(), block.getBounds().getBoundingRectangle().getHeight());
            } else if (block instanceof ExplodableBlock) {
                if (((ExplodableBlock) block).getState().equals(ExplodableBlock.State.RUBBLE)) {
                    spriteBatch.draw(blockRubble, block.getPosition().x, block.getPosition().y, Block.getSIZE(), Block.getSIZE());
                } else {
                    spriteBatch.draw(((ExplodableBlock) block).getState().equals(ExplodableBlock.State.RED) ? blockExplodeRed : blockExplodeYellow, block.getPosition().x, block.getPosition().y, Block.getSIZE(), Block.getSIZE());
                }
            } else if (block instanceof EnvironmentBlock) {
                //todo different conditions of coal as it is mined
                TextureRegion textureRegion = null;
                switch (((EnvironmentBlock) block).getMaterial().getType()) {
                    case COAL:
                        textureRegion = coalTexture;
                        break;
                    case STONE:
                        textureRegion = stoneTexture;
                        break;
                    case WOOD:
                        textureRegion = woodTexture;
                        break;
                    case MEAT:
                        textureRegion = meatTexture;
                        break;
                    case FOOD:
                        if (((EnvironmentBlock) block).getMaterial() instanceof Food) {
                            switch (((Food) ((EnvironmentBlock) block).getMaterial()).getFoodType()) {
                                case COOKEDMEAT:
                                    textureRegion = cookedmeatTexture;
                                    break;
                                case BERRY:
                                    textureRegion = berryTexture;
                                    break;
                                default:
                                    break;
                            }
                        }
                        break;
                    case BERRYPASTE:
                        textureRegion = berrypasteTexture;
                        break;
                    case GRASS:
                        textureRegion = grassTexture;
                        break;
                    case STICK:
                        textureRegion = stickTexture;
                        break;
                    case PEBBLE:
                        textureRegion = pebbleTexture;
                        break;
                }
                if (block.getDurability() > 0 ) {
                    spriteBatch.draw(textureRegion, block.getPosition().x, block.getPosition().y, Block.getSIZE(), Block.getSIZE());
                } else {
                    spriteBatch.draw(floorTexture, block.getPosition().x, block.getPosition().y, Block.getSIZE(), Block.getSIZE());
                }
            } else {
                spriteBatch.draw(blockTexture, block.getPosition().x, block.getPosition().y, Block.getSIZE(), Block.getSIZE());
            }
        }
    }

    private void drawBoostPads() {
        for (BoostPad boostPad : world.getLevel().getBoostPads()) {
            spriteBatch.draw(boostPadTexture, boostPad.getPos().x, boostPad.getPos().y, Pad.getSIZE(), Pad.getSIZE());
            if (boostPad.getBoost() != null) {
                TextureRegion boostFrame = null;
                switch (boostPad.getBoost()) {
                    case HOMING:
                        boostFrame = homingBoostTexture;
                        break;
                    case SPEED:
                        boostFrame = speedBoostTexture;
                        break;
                    case DAMAGE:
                        boostFrame = damageBoostTexture;
                        break;
                    case SHIELD:
                        boostFrame = shieldBoostTexture;
                        break;
                }
                if (boostFrame != null) {
                    spriteBatch.draw(boostFrame,
                            boostPad.getPos().x + 0.33F, boostPad.getPos().y + 0.35F,
                            Pad.getSIZE()*0.8F, Pad.getSIZE()*0.8F);
                }

            }
        }
    }

    private void drawGunPads() {
        for (GunPad gunPad : world.getLevel().getGunPads()) {
            TextureRegion gunFrame = null;
            switch (gunPad.getType()) {
                case PISTOL:
                    gunFrame = pistolTexture;
                    break;
                case SMG:
                    gunFrame = smgTexture;
                    break;
                case SHOTGUN:
                    gunFrame = shotgunTexture;
                    break;
                case ROCKET:
                    gunFrame = rocketTexture;
                    break;
            }
            if (gunFrame != null) {
                spriteBatch.draw(gunFrame, gunPad.getPos().x, gunPad.getPos().y, GunPad.getSIZE(), GunPad.getSIZE());
            }
        }
    }

    private void drawFloorPads() {
        for (FloorPad floorPad : world.getLevel().getFloorPads()) {
            TextureRegion floorFrame = null;
            switch (floorPad.getType()) {
                case SPIKE:
                    floorFrame = spikeTexture;
                    break;
                case SLIME:
                case WATER:
                    floorFrame = slimeTexture;
                    break;
                case MOVE:
                case WATERFLOW:
                    floorFrame = moveTexture;
                    break;
            }
            if (floorFrame != null) {
                if (floorPad.getType().equals(MOVE) || floorPad.getType().equals(WATERFLOW)) {
                    spriteBatch.draw(floorFrame, floorPad.getPos().x, floorPad.getPos().y, Block.getSIZE()/2, Block.getSIZE()/2,
                            Block.getSIZE(), Block.getSIZE(), 1, 1, floorPad.getRotation() + 90, true);
                } else {
                    spriteBatch.draw(floorFrame, floorPad.getPos().x, floorPad.getPos().y, GunPad.getSIZE(), GunPad.getSIZE());
                }
            }
        }
    }

    private void drawBloodStains() {
        for (BloodStain bloodStain : world.getBloodStains()) {
            TextureRegion stainFrame = playerDead;
            spriteBatch.draw(stainFrame, bloodStain.getPosition().x, bloodStain.getPosition().y, bloodStain.getWidth(), bloodStain.getHeight());
        }
    }



    private void drawAreaAffects() {
        for (AreaAffect areaAffect : world.getAreaAffects()) {
            if (!areaAffect.isFinished()) {
                TextureRegion affectFrame = null;
                switch (areaAffect.getAffectType()) {
                    case EXPLOSION:
                        affectFrame = (TextureRegion) explodeAnimation.getKeyFrame(world.getBob().getStateTime(), true);
                        break;
                    case LIGHTNING:
                        Random rand = new Random();
                        int r = 3;
                        affectFrame = itemAtlas.findRegion("lightning-0" + (rand.nextInt(4) + 1));
                }
                spriteBatch.draw(affectFrame, areaAffect.getPosition().x - areaAffect.getWidth()/2, areaAffect.getPosition().y - areaAffect.getHeight()/2, areaAffect.getWidth()/2, areaAffect.getHeight()/2,
                        areaAffect.getWidth(), areaAffect.getHeight(), 1F, 1F, 0, true);
            }
        }
    }

    private void drawProjectiles() {
        for (Projectile projectile : world.getProjectiles()) {
            TextureRegion bulletFrame = itemAtlas.findRegion(projectile.getName());
            if (projectile.getProjectileType().equals(Projectile.ProjectileType.FIREBALL)) {
                Random rand = new Random();
                switch (rand.nextInt(4)) {
                    case 1:
                        bulletFrame = itemAtlas.findRegion("fireball-01");
                        break;
                    case 2:
                        bulletFrame = itemAtlas.findRegion("fireball-02");
                        break;
                    case 3:
                        bulletFrame = itemAtlas.findRegion("fireball-03");
                        break;
                    default:
                        bulletFrame = itemAtlas.findRegion("fireball-04");
                        break;
                }
//                    itemHeight = 0.5F;
//                    itemWidth = 0.5f;
//                    itemRotation = 90;
//                    itemRadius = bob.getWidth()/2;
            }

//            if (projectile.isExploding()) {
//                bulletFrame = (TextureRegion) (explodeAnimation.getKeyFrame(projectile.getStateTime(), true));
//                spriteBatch.draw(bulletFrame, projectile.getPosition().x, projectile.getPosition().y, 0, projectile.getHeight()/2,
//                        projectile.getWidth(), projectile.getHeight(), 2.5F, 5, projectile.getRotation(), true);
//            } else {
            Polygon bounds = projectile.getBounds();
            Vector2 pos = new Vector2(projectile.getBounds().getX(), projectile.getBounds().getY());
            spriteBatch.draw(bulletFrame, pos.x, pos.y, bounds.getOriginX(), bounds.getOriginY(),
                    projectile.getWidth(), projectile.getHeight(), 1, 1, bounds.getRotation(), true);
//            }
        }
    }

    private void drawWall(float rotation, Player bob, Point gridRef, Item wall) {
        String name = wall.getItemType().equals(Item.ItemType.DOOR) ? "door" : "wall";
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

    private void drawHandItem(float rotation, Vector2 gridRef, float width, float height, TextureRegion textureToDraw) {
        spriteBatch.draw(textureToDraw, gridRef.x, gridRef.y,0, 0, width, height, 1F, 1F, rotation);
    }

    private void drawTargetCircle(Vector2 gridRef, float width, float height, TextureRegion textureToDraw) {
        spriteBatch.draw(textureToDraw, gridRef.x - width/2, gridRef.y - height/2,width/2, height/2, width, height, 1F, 1F, 0);
    }

    private void drawHand(Player sprite, float rotation, Vector2 gridRef, float width, float height, TextureRegion textureToDraw) {
        float x = gridRef.x + (float)(sprite.getWidth()/2 * Math.cos((rotation) * Math.PI/180));
        float y = gridRef.y + (float)(sprite.getHeight()/2 * Math.sin((rotation) * Math.PI/180));
        spriteBatch.draw(textureToDraw, x, y, 0, 0, width, height, 0.5F, 0.5F, rotation);
    }

    private void drawHUD(Player bob, TextureRegion holdingFrame) {
        Vector2 externalPos = new Vector2(bob.getCentrePosition().x, bob.getCentrePosition().y);

//        spriteBatch.setProjectionMatrix(textCamera.combined);
//        font.setColor(Color.BLACK);
//        font.draw(spriteBatch, "The-time-is: " + world.getTime(), textCamera.position.x - 50, textCamera.position.y + 120, 1, 1, true);
//        spriteBatch.setProjectionMatrix(cam.combined);
        for (float i = 0; i < bob.getLives(); i++) {
            float xPos = 12 + externalPos.x - (i/4);
            float yPos = externalPos.y + 6;
            spriteBatch.draw(heartTexture, xPos, yPos, 1, 1, 1, 1, 0.25F, 0.25F, 0);
        }
        for (float i = 0; i < bob.getMana(); i++) {
            float xPos = 12 + externalPos.x - (i/4);
            float yPos = externalPos.y + 5.5F;
            spriteBatch.draw(manaTexture, xPos, yPos, 1, 1, 1, 1, 0.25F, 0.25F, 0);
        }
        for (float i = 0; i < bob.getWater(); i++) {
            float xPos = 12 + externalPos.x - (i/4);
            float yPos = externalPos.y + 5F;
            spriteBatch.draw(jarFullTexture, xPos, yPos, 1, 1, 1, 1, 0.25F, 0.25F, 0);
        }
        for (float i = 0; i < bob.getFood(); i++) {
            float xPos = 12 + externalPos.x - (i/4);
            float yPos = externalPos.y + 4.5F;
            spriteBatch.draw(meatTexture, xPos, yPos, 1, 1, 1, 1, 0.25F, 0.25F, 0);
        }

        if (holdingFrame != null) {
            float xPos = 12 + externalPos.x;
            float yPos = externalPos.y + 4;
            spriteBatch.draw(holdingFrame, xPos, yPos, 1, 1, 1F, 1, 1F, 1F, 0);
//            spriteBatch.draw(holdingFrame, bob.getCentrePosition().x - 12, bob.getCentrePosition().y - 8F, 1, 1, 1F, 1, 0.75F, 0.75F, 0);
        }
        Inventory toolbelt = bob.getToolBelt();
        for (int i = 0; i < toolbelt.getSize(); i++) {
            TextureRegion box = buttonsAtlas.findRegion("inventoryBox");
            TextureRegion selectedBox = itemAtlas.findRegion("explodingBlockYellow");
            float xPos = externalPos.x - 8 + 2*i;
            float yPos = externalPos.y - 7;
            spriteBatch.draw(box, xPos, yPos,  1, 1, 1F, 1, 1F, 1F, 0);
            if (i == bob.getSlotNo()) {
                spriteBatch.draw(selectedBox, xPos, yPos,  0.5F, 0.5F, 1.1F, 1.1F, 1.2F, 1.2F, 0);
            }
            Material material = (Material)toolbelt.getSlots().get(i);
            if (material != null) {
                TextureRegion toDraw = itemAtlas.findRegion(material.getName());
                if (toDraw == null) toDraw = inventoryAtlas.findRegion(material.getName());
                if (toDraw == null && material instanceof Magic) toDraw = itemAtlas.findRegion(material.getName() + "-01");


                if (toDraw != null) spriteBatch.draw(toDraw, xPos, yPos,  1, 1, 1F, 1, 1F, 1F, 0);
            }
        }
    }

    private void drawBob() {
        Player bob = world.getBob();
        TextureRegion bobFrame = bob.isInjured() ? playerInjured  : (bob.getTorso() != null && bob.getTorso().getItemType().equals(Item.ItemType.ARMOUR)) ? armourIdle : playerIdle;

        TextureRegion holdingFrame = null;
        float xPos = bob.getCentrePosition().x;
        float yPos = bob.getCentrePosition().y;

        if(bob.getState().equals(Player.State.MOVING)) {
            if (bob.getTorso() != null && bob.getTorso().getItemType().equals(Item.ItemType.ARMOUR)) {
                bobFrame =  (TextureRegion) (armourAnimation.getKeyFrame(bob.getStateTime(), true));
            } else {
                bobFrame =  (TextureRegion) (walkAnimation.getKeyFrame(bob.getStateTime(), true));
            }
        }
        float drawAngle = bob.getRotation();
//        drawHand(bob,bob.getRotation() - 45, bob.getCentrePosition(), bob.getWidth(), bob.getHeight(), playerHand);
//        drawHand(bob,bob.getRotation() + 45, bob.getCentrePosition(), bob.getWidth(), bob.getHeight(), playerHand);
        spriteBatch.draw(bobFrame, bob.getPosition().x, bob.getPosition().y, bob.getWidth()/2, bob.getHeight()/2, bob.getWidth()*2, bob.getHeight()*2,
                2, 2, drawAngle, true);
//        spriteBatch.draw(playerHead, bob.getPosition().x, bob.getPosition().y, bob.getWidth()/2, bob.getHeight()/2, bob.getWidth(), bob.getHeight(),
//                1, 1, drawAngle, true);
        if (bob.isOnfire()) {
            Random rand = new Random();
            TextureRegion burningFrame = itemAtlas.findRegion("onfire-0" + (rand.nextInt(4) + 1));
            spriteBatch.draw(burningFrame, bob.getPosition().x, bob.getPosition().y, bob.getWidth()/2, bob.getHeight()/2, bob.getWidth(), bob.getHeight(),
                    1, 1, bob.getRotation(), true);
        }

//        if (bob.getStrongHand() != null)  {
//            Object o = bob.getStrongHand();
//            if (o instanceof Item) {
//                Item item = (Item) o;
//                float rotation = item.getRotation();
//                if (item.getItemType().equals(Item.ItemType.SPEAR)) {
//                    TextureRegion itemFrame = null ;
//                    itemFrame = itemAtlas.findRegion(bob.getStrongHand().getName());
//                }
//                if (item.getItemType().equals(Item.ItemType.WALL) || item.getItemType().equals(Item.ItemType.DOOR)) {
//
//                    Point gridRef = bob.getGridRef(bob.getRotation(), xPos, yPos);
//                    Object ob = world.getLevel().getBlock((int)gridRef.x, (int)gridRef.y);
//
//                    if (ob instanceof Wall) {
//                        if (!((Wall) ob).isWallFull(rotation)) {
//                            for (float wallRotation : ((Wall) ob).getWalls().keySet()) {
//                                if (((Wall) ob).getWalls().get(wallRotation) == null) {
//                                    drawWall(rotation, bob, gridRef, item);
//                                }
//                            }
//                        }
//                    } else if (ob == null) {
//                        drawWall(rotation, bob, gridRef, item);
//                    }
//                } else if (item.getItemType().equals(Item.ItemType.CAMPFIRE) || item.getItemType().equals(Item.ItemType.BENCHHEALER) ||
//                        item.getItemType().equals(Item.ItemType.STONEANVIL)) {
//                    Point gridRef = bob.getGridRef(bob.getRotation(), xPos, yPos);
//                    Object ob = world.getLevel().getBlock((int)gridRef.x, (int)gridRef.y);
//                    if (ob == null) {
//                        drawBench(rotation, bob, gridRef, item, item.getSize());
//                    }
//                } else {
//                    TextureRegion itemFrame = null ;
//                    float itemWidth = 0, itemHeight = 0, itemRotation = 0, itemRadius = 0;
//                    boolean using = bob.isUseTimerOn();
//                    if (bob.getStrongHand() instanceof Item) {
//                        if (item instanceof Magic) {
//                            Magic magic = (Magic) item;
//                            switch (magic.getMagicType()) {
//                                case PROJECTILE:
//                                    switch (magic.getProjectileType()) {
//                                        case FIREBALL:
//                                            Random rand = new Random();
//                                            switch (rand.nextInt(4)) {
//                                                case 1:
//                                                    itemFrame = itemAtlas.findRegion("fireball-01");
//                                                    break;
//                                                case 2:
//                                                    itemFrame = itemAtlas.findRegion("fireball-02");
//                                                    break;
//                                                case 3:
//                                                    itemFrame = itemAtlas.findRegion("fireball-03");
//                                                    break;
//                                                default:
//                                                    itemFrame = itemAtlas.findRegion("fireball-04");
//                                                    break;
//                                            }
//                                            break;
//                                        case LIGHNINGBOLT:
//                                            break;
//                                        default:
//                                            break;
//                                    }
//                                    break;
//                                case SELF:
//                                    itemFrame = itemAtlas.findRegion(item.getName());
//                                    break;
//                                case AREA:
//                                    switch (magic.getElement()) {
//                                        case ELECTRIC:
//                                            Random rand = new Random();
//                                            switch (rand.nextInt(4)) {
//                                                case 1:
//                                                    itemFrame = itemAtlas.findRegion("lightning-01");
//                                                    break;
//                                                case 2:
//                                                    itemFrame = itemAtlas.findRegion("lightning-02");
//                                                    break;
//                                                case 3:
//                                                    itemFrame = itemAtlas.findRegion("lightning-03");
//                                                    break;
//                                                default:
//                                                    itemFrame = itemAtlas.findRegion("lightning-04");
//                                                    break;
//                                            }
//                                            drawTargetCircle(bob.getLeftHandPosition(0, 6F), 3F, 3F, itemAtlas.findRegion("target"));
//                                            break;
//                                        default:
//                                            break;
//                                    }
//                                    break;
//                            }
//                            itemHeight = 0.5F;
//                            itemWidth = 0.5f;
//                            itemRotation = 90;
//                            itemRadius = bob.getWidth()/2;
//                        }
//                        switch (item.getItemType()) {
//                            case PICK:
//                                if (using) {
//                                    itemFrame = pickAnimation.getKeyFrame(bob.getStateTime(), true);
//                                }
//                                itemWidth = bob.getWidth()*1.5F;
//                                itemHeight = bob.getHeight()*1.5F;
//                                itemRotation = 90;
//                                itemRadius = bob.getWidth()/2;
//                                break;
//                            case SWORD:
//                                if (using) {
//                                    itemFrame = swordAnimation.getKeyFrame(bob.getStateTime(), true);
//                                }
//                                itemWidth = bob.getWidth()*1.5F;
//                                itemHeight = bob.getHeight()*1.5F;
//                                itemRotation = 45;
//                                itemRadius = bob.getWidth()/2;
//                                break;
//                            case SPEAR:
//                                itemHeight = 0.25F;
//                                itemWidth = 2f;
//                                itemRotation = 140;
//                                itemRadius = bob.getWidth()/2;
//                                break;
//                            case JAR:
//                                Fillable jar = (Fillable)item;
//                                itemFrame = jar.isFilled() ? jarFullTexture : jarTexture;
//                                itemHeight = 0.25F;
//                                itemWidth = 2f;
//                                itemRotation = 90;
//                                itemRadius = bob.getWidth()/2;
//                                break;
//                            case MAGIC:
//                                break;
//                            default:
//                                itemFrame = itemAtlas.findRegion(item.getName());
//                                itemHeight = 1F;
//                                itemWidth = 1f;
//                                itemRotation = 90;
//                                itemRadius = bob.getWidth()/2;
//                                if (itemFrame == null) itemFrame = inventoryAtlas.findRegion(item.getName());
//                        }
//                        if (itemFrame == null)itemFrame = itemAtlas.findRegion(item.getName());
//                        if (itemFrame == null) itemFrame = inventoryAtlas.findRegion(item.getName());
//
//                    } else {
//                        itemWidth = bob.getWidth()/2;
//                        itemHeight = bob.getHeight()/2;
//                        itemRotation = 90;
//                        itemRadius = bob.getWidth()/2;
//                    }
//                    drawHandItem(bob.getRotation(), bob.isLeftHanded() ? bob.getLeftHandPosition(itemRotation, itemRadius) : bob.getRightHandPosition(), itemWidth, itemHeight, itemFrame);
//                }
//            } else {
//                TextureRegion handFrame;
//                handFrame = itemAtlas.findRegion(bob.getStrongHand().getName());
//                if (handFrame == null) inventoryAtlas.findRegion(bob.getStrongHand().getName());
//                if (handFrame != null )drawHandItem(bob.getRotation(), bob.isLeftHanded() ? bob.getLeftHandPosition(45, bob.getWidth()/2) : bob.getRightHandPosition(), bob.getWidth(), bob.getHeight(), handFrame);
//            }
//            holdingFrame = itemAtlas.findRegion(bob.getStrongHand().getName());
//            if (holdingFrame == null) holdingFrame = inventoryAtlas.findRegion(bob.getStrongHand().getName());
//        }

//        if (bob.getWeakHand() != null && bob.getWeakHand().getItemType().equals(Item.ItemType.SHIELD)) {
//            if (bob.isLeftHanded()) {
//                spriteBatch.draw(shieldTexture, bob.getRightHandPosition().x, bob.getRightHandPosition().y, 0, 0, 0.25F, 1, 1F, 1F, bob.getBlockRectangle().getRotation());
//            } else {
//                spriteBatch.draw(shieldTexture, bob.getLeftHandPosition(45, bob.getWidth()).x, bob.getLeftHandPosition(45, bob.getWidth()).y, 0, 0, 0.25F, -1, 1F, 1F, bob.getBlockRectangle().getRotation());
//            }
//        }

//        if (!bob.getBoost().equals(Player.Boost.NOTHING)) {
//            TextureRegion boostFrame = null;
//
//            switch (bob.getBoost()) {
//                case HOMING:
//                    boostFrame = homingBoostTexture;
//                    break;
//                case SPEED:
//                    boostFrame = speedBoostTexture;
//                    break;
//                case DAMAGE:
//                    boostFrame = damageBoostTexture;
//                    break;
//                case SHIELD:
//                    boostFrame = shieldBoostTexture;
//                    TextureRegion shieldFrame = (TextureRegion) (shieldAnimation.getKeyFrame(bob.getStateTime(), true));
//                    Circle circle = bob.getShieldCircle();
//                    spriteBatch.draw(shieldFrame, circle.x - circle.radius/2, circle.y - circle.radius/2, 1, 1, 2F, 2, 2.00F, 2.00F, 0);
//                    break;
//                case HEALING:
//                    boostFrame = itemAtlas.findRegion("healing");
//                    circle = bob.getShieldCircle();
//                    spriteBatch.draw(boostFrame, circle.x - circle.radius/2, circle.y - circle.radius/2, 1, 1, 2F, 2, 2.00F, 2.00F, 0);
//                    break;
//            }
//
//            xPos = bob.getPosition().x + 4.5F;
//            if (boostFrame != null) {
//                spriteBatch.draw(boostFrame, xPos, yPos, 1, 1, 1F, 1, 0.75F, 0.75F, 0);
//            }
//
//        }

//        if (bob.getGun() != null) {
//            if (bob.getGun().isReloading()) {
//                float x = world.getBob().getPosition().x - (CAMERA_WIDTH/2) + 3;
//                float y = world.getBob().getPosition().y + (CAMERA_HEIGHT/2) - 1.5F;
//                spriteBatch.draw(reloadingTexture, x, y, 1, 1, 6.0F, 2.0F, 1.00F, 1.00F, 0);
//            }
//        }
//
//        if (world.getFireJoystick() != null && world.getFireJoystick().getDrag() != null) drawAngle = world.getFireJoystick().getAngle();
//        drawHUD(bob, holdingFrame);
        //todo make injured sprites
    }

    private void drawAis() {
        for (AIPlayer aiPlayer : world.getAIPlayers()) {

            Vector2 livesPos = aiPlayer.getCentrePosition();
            for (float i = 0; i < aiPlayer.getLives(); i++) {
                float xPos = livesPos.x - (i/15);
                float yPos = livesPos.y;
                spriteBatch.draw(heartTexture, xPos, yPos, 1, 1, 1, 1, 0.05F, 0.05F, 0);
            }
            TextureRegion aiFrame = aiPlayer.isInjured() ? playerInjured : playerIdle;
            if(aiPlayer.getState().equals(Player.State.MOVING)) {
                if (aiPlayer.isInjured()) {
                    aiFrame = (walkInjuredAnimation.getKeyFrame(aiPlayer.getStateTime(), true));
                } else {
                    aiFrame = (walkAnimation.getKeyFrame(aiPlayer.getStateTime(), true));
                }
            }
            spriteBatch.draw(aiFrame, aiPlayer.getPosition().x, aiPlayer.getPosition().y, aiPlayer.getWidth()/2, aiPlayer.getHeight()/2, aiPlayer.getWidth(), aiPlayer.getHeight(),
                    1, 1, aiPlayer.getRotation(), true);

            if (aiPlayer.isOnfire()) {
                Random rand = new Random();
                TextureRegion burningFrame = itemAtlas.findRegion("onfire-0" + (rand.nextInt(4) + 1));
                spriteBatch.draw(burningFrame, aiPlayer.getPosition().x, aiPlayer.getPosition().y, aiPlayer.getWidth()/2, aiPlayer.getHeight()/2, aiPlayer.getWidth(), aiPlayer.getHeight(),
                        1, 1, aiPlayer.getRotation(), true);
            }

            if (!aiPlayer.getBoost().equals(Player.Boost.NOTHING)) {

                if (aiPlayer.getBoost() == Player.Boost.SHIELD) {
                    TextureRegion shieldFrame = (TextureRegion) (shieldAnimation.getKeyFrame(aiPlayer.getStateTime(), true));
                    Circle circle = aiPlayer.getShieldCircle();
                    spriteBatch.draw(shieldFrame, circle.x - circle.radius / 2, circle.y - circle.radius / 2, 1, 1, 2F, 2, 2.00F, 2.00F, 0);
                }
            }
            if (aiPlayer.getStrongHand() != null)  {
                if (aiPlayer.getStrongHand() instanceof Item) {
                    Item item = (Item) aiPlayer.getStrongHand();
                    float rotation = item.getRotation();
                    if (aiPlayer.isUseTimerOn()) {
                        TextureRegion itemFrame = null;
                        switch (item.getItemType()) {
                            case PICK:
                                itemFrame = pickAnimation.getKeyFrame(aiPlayer.getStateTime(), true);
                                break;
                            case SWORD:
                                itemFrame = swordAnimation.getKeyFrame(aiPlayer.getStateTime(), true);
                                break;
                            case JAR:
                                Fillable jar = (Fillable) item;
                                itemFrame = jar.isFilled() ? jarFullTexture : jarTexture;
                        }
                        drawHandItem(aiPlayer.isLeftHanded() ? aiPlayer.getRotation() + 45 : aiPlayer.getRotation() - 45, aiPlayer.getCentrePosition(), aiPlayer.getWidth() * 2, aiPlayer.getHeight() * 2, itemFrame);
                    }
                }
            }
            if (aiPlayer.getWeakHand() != null && aiPlayer.getWeakHand().getItemType().equals(Item.ItemType.SHIELD)) {
                if (aiPlayer.isLeftHanded()) {
                    spriteBatch.draw(shieldTexture, aiPlayer.getRightHandPosition().x, aiPlayer.getRightHandPosition().y, 0, 0, 0.25F, 1, 1F, 1F, aiPlayer.getBlockRectangle().getRotation());
                } else {
                    spriteBatch.draw(shieldTexture, aiPlayer.getLeftHandPosition(45, aiPlayer.getWidth()).x, aiPlayer.getLeftHandPosition(45, aiPlayer.getWidth()).y, 0, 0, 0.25F, -1, 1F, 1F, aiPlayer.getBlockRectangle().getRotation());
                }
            }
        }
    }

    private void drawAnimals() {
        for (Animal animal : world.getAnimals()) {
            Vector2 livesPos = animal.getCentrePosition();
            for (float i = 0; i < animal.getLives(); i++) {
                float xPos = livesPos.x - (i/15);
                float yPos = livesPos.y;
                spriteBatch.draw(heartTexture, xPos, yPos, 1, 1, 1, 1, 0.05F, 0.05F, 0);
            }
            TextureRegion animalFrame = animalTexture;
//            if(aiPlayer.getState().equals(Player.State.MOVING)) {
//                if (aiPlayer.isInjured()) {
//                    aiFrame =  (TextureRegion) (walkInjuredAnimation.getKeyFrame(aiPlayer.getStateTime(), true));
//                } else {
//                    aiFrame =  (TextureRegion) (walkAnimation.getKeyFrame(aiPlayer.getStateTime(), true));
//                }
//            }
            spriteBatch.draw(animalFrame, animal.getPosition().x, animal.getPosition().y, animal.getWidth()/2, animal.getHeight()/2, animal.getWidth(), animal.getHeight(),
                    1, 1, animal.getRotation(), true);
            if (animal.isOnfire()) {
                Random rand = new Random();
                TextureRegion burningFrame = itemAtlas.findRegion("onfire-0" + (rand.nextInt(4) + 1));
                spriteBatch.draw(burningFrame, animal.getPosition().x, animal.getPosition().y, animal.getWidth()/2, animal.getHeight()/2, animal.getWidth(), animal.getHeight(),
                        1, 1, animal.getRotation(), true);
            }
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

//        if (world.getTouchPoint() != null) {
//
//            Vector2 midPoint = new Vector2(Gdx.app.getGraphics().getWidth()/2, Gdx.app.getGraphics().getHeight()/2);
//            Vector2 touchPoint = world.getTouchPoint();
//            float xDist = midPoint.x - touchPoint.x;
//            float yDist = midPoint.y - touchPoint.y ;
//            float xRatio = 7.5F / midPoint.x;
//            float yRatio = 4F / midPoint.y;
//
//            float xVal = world.getBob ().getPosition().x - (xDist * xRatio);
//            float yVal = world.getBob().getPosition().y - (yDist * yRatio);
//
//            Vector2 drawOuter;
//            if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
//                drawOuter = new Vector2(xVal - 0.5F, yVal + 0.5F);
//            } else {
//                drawOuter = new Vector2(xVal + 0.2F, yVal + 0.5F);
//            }
//            spriteBatch.draw(joystickOuterTexture, drawOuter.x - JoyStick.getWIDTH() / 2, drawOuter.y - JoyStick.getWIDTH() / 2, 1, 1, 2F, 2, 1.00F, 1.00F, 0);
//            if (world.getDragPoint() == null) {
//                spriteBatch.draw(joystickInnerTexture, drawOuter.x - JoyStick.getWIDTH() / 2, drawOuter.y - JoyStick.getWIDTH() / 2, 1, 1, 2.0F, 2.0F, 1.00F, 1.00F, 0);
//            } else {
//                Vector2 dragPoint = world.getDragPoint();
//                float xDist2 = midPoint.x - dragPoint.x;
//                float yDist2 = midPoint.y - dragPoint.y;
//
//                float xVal2 = world.getBob().getPosition().x - (xDist2 * xRatio);
//                float yVal2 = world.getBob().getPosition().y - (yDist2 * yRatio);
//                float difX = touchPoint.x - dragPoint.x;
//                float difY = touchPoint.y - dragPoint.y;
//                Vector2 drawInner;
//                if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
//                    drawInner = new Vector2(xVal2 - 0.5F, yVal2 + 0.5F);
//                } else  {
//                    drawInner = new Vector2(xVal2 + 0.2F, yVal2 + 0.5F);
//                }
//                if (difX > 45) drawInner.x = drawOuter.x - 1;
//                if (difX < -45) drawInner.x = drawOuter.x + 1;
//                if (difY > 55) drawInner.y = drawOuter.y - 1;
//                if (difY < -55) drawInner.y = drawOuter.y + 1;
//
//                spriteBatch.draw(joystickInnerTexture, drawInner.x - JoyStick.getWIDTH() / 2, drawInner.y - JoyStick.getWIDTH() / 2, 1, 1, 2.0F, 2.0F, 1.00F, 1.00F, 0);
//            }
//        }
    }

    private void drawButtons() {

        if (world.getButtons().isEmpty()) {
            world.getButtons().add(new GameButton(new Vector2(world.getBob().getCentrePosition().x, world.getBob().getCentrePosition().y), world.getBob().getWidth()*5, GameButton.Type.USE));
        }

        for (GameButton gameButton : world.getButtons()) {
            TextureRegion buttonFrame = null;
            buttonFrame =  (TextureRegion) (walkAnimation.getKeyFrame(world.getBob().getStateTime(), true));
            float xish, yish;

            if (buttonFrame != null) spriteBatch.draw(buttonFrame, gameButton.getArea().x, gameButton.getArea().y, 1, 1, gameButton.getArea().radius, gameButton.getArea().radius, 1.00F, 1.00F, 0);
        }
    }


}
