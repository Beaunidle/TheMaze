package com.mygdx.game;




import static com.mygdx.game.model.items.Swingable.SwingableType.HOE;
import static com.mygdx.game.model.items.Swingable.SwingableType.SHOVEL;
import static com.mygdx.game.model.items.Throwable.ThrowableType.SPEAR;
import static com.mygdx.game.model.moveable.Projectile.ProjectileType.FIREBALL;
import static com.mygdx.game.model.pads.FloorPad.Type.*;

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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.ai.CowAi;
import com.mygdx.game.model.Inventory;
import com.mygdx.game.model.Recipe;
import com.mygdx.game.model.environment.AnimalSpawn;
import com.mygdx.game.model.environment.Tilled;
import com.mygdx.game.model.environment.blocks.Building;
import com.mygdx.game.model.environment.blocks.EnvironmentBlock;
import com.mygdx.game.model.environment.blocks.FillableBlock;
import com.mygdx.game.model.environment.blocks.Grower;
import com.mygdx.game.model.environment.blocks.Wall;
import com.mygdx.game.model.items.Fillable;
import com.mygdx.game.model.items.Consumable;
import com.mygdx.game.model.items.Item;
import com.mygdx.game.model.items.Magic;
import com.mygdx.game.model.items.Material;
import com.mygdx.game.model.items.Placeable;
import com.mygdx.game.model.items.Swingable;
import com.mygdx.game.model.items.Throwable;
import com.mygdx.game.model.moveable.AIPlayer;
import com.mygdx.game.model.environment.blocks.Block;
import com.mygdx.game.model.environment.BloodStain;
import com.mygdx.game.model.moveable.Animal;
import com.mygdx.game.model.moveable.Projectile;
import com.mygdx.game.model.moveable.Sprite;
import com.mygdx.game.model.pads.BoostPad;
import com.mygdx.game.model.environment.blocks.ExplodableBlock;
import com.mygdx.game.model.environment.AreaAffect;
import com.mygdx.game.model.pads.FloorPad;
import com.mygdx.game.model.GameButton;
import com.mygdx.game.model.pads.GunPad;
import com.mygdx.game.model.pads.Pad;
import com.mygdx.game.model.moveable.Player;
import com.mygdx.game.model.World;
import com.mygdx.game.utils.JoyStick;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


public class WorldRenderer {

    private static final float RUNNING_FRAME_DURATION = 0.12f;
    private static final float EXPLODE_FRAME_DURATION = 0.12f;
    private static final float ITEM_USE_FRAME_DURATION = 0.065F;

    TextureAtlas itemAtlas;
    TextureAtlas inventoryAtlas;
    TextureAtlas buttonsAtlas;
    BitmapFont font;
    TextureLoader textureLoader;
    //Textures
    private TextureRegion fireButtonTexture, useButtonTexture;

    //Animations
    private Animation walkAnimation;
    private Animation armourAnimation;
    private  Animation walkInjuredAnimation;
    private  Animation explodeAnimation;
    private Animation shieldAnimation;
    private Animation pickAnimation, swordAnimation;

    private static final float CAMERA_WIDTH = 30f;
    private static final float CAMERA_HEIGHT = 17f;

    private final World world;
    private final OrthographicCamera cam;
    private final OrthographicCamera textCamera;

    /** for debug rendering **/
    private final ShapeRenderer debugRenderer = new ShapeRenderer();

    private final SpriteBatch spriteBatch;
    private final boolean debug;

    public WorldRenderer(World world, SpriteBatch spriteBatch, boolean debug, BitmapFont font, TextureLoader textureLoader) {
        this.world = world;
        this.textureLoader = textureLoader;

        float aspectRatio = (float)Gdx.graphics.getHeight() / (float)Gdx.graphics.getWidth();
        float cameraViewPortWidth = 512; // Set the size of the viewport for the text to something big
        float cameraViewPortHeight = cameraViewPortWidth * aspectRatio;
        this.cam = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
        textCamera = new OrthographicCamera(cameraViewPortWidth, cameraViewPortHeight);

        this.cam.position.x = Math.round(world.getBob().getCentrePosition().x);
        this.cam.position.y = Math.round(world.getBob().getCentrePosition().y);
        this.cam.update();
        this.textCamera.update();
        this.debug = debug;
        this.spriteBatch = spriteBatch;
        loadTextures();
        this.font = font;
        font.setColor(Color.BLACK);
    }

    private void loadTextures() {

        itemAtlas = new TextureAtlas(Gdx.files.internal("items.atlas"));
        inventoryAtlas = new TextureAtlas(Gdx.files.internal("inventory.atlas"));
        buttonsAtlas = new TextureAtlas(Gdx.files.internal("buttons.atlas"));
        TextureRegion[] walkFrames = new TextureRegion[3];
        for (int i = 0; i < 3; i++) {
            walkFrames[i] = textureLoader.getRegion("sprite-01");
        }
        walkAnimation = new Animation(RUNNING_FRAME_DURATION, walkFrames);

        TextureRegion[] armourFrames = new TextureRegion[3];
        for (int i = 0; i < 3; i++) {
            armourFrames[i] = textureLoader.getRegion("armour-01");
        }
        armourAnimation = new Animation(RUNNING_FRAME_DURATION, armourFrames);

        TextureRegion[] walkInjuredFrames = new TextureRegion[3];
        for (int i = 0; i < 3; i++) {
            walkInjuredFrames[i] = textureLoader.getRegion("sprite-01");
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
        shieldFrames[1] = itemAtlas.findRegion("shieldOrange");
        shieldFrames[2] = itemAtlas.findRegion("shieldGreen");

        shieldAnimation = new Animation(RUNNING_FRAME_DURATION, shieldFrames);


//        TextureRegion[] pickFrames = new TextureRegion[5];
//        pickFrames[0] = inventoryAtlas.findRegion("inv_pick01");
//        pickFrames[1] = inventoryAtlas.findRegion("inv_pick02");
//        pickFrames[2] = inventoryAtlas.findRegion("inv_pick03");
//        pickFrames[3] = inventoryAtlas.findRegion("inv_pick04");
//        pickFrames[4] = inventoryAtlas.findRegion("inv_pick05");

//        pickAnimation = new Animation(ITEM_USE_FRAME_DURATION, pickFrames);

//        TextureRegion[] swordFrames = new TextureRegion[5];
//        swordFrames[0] = inventoryAtlas.findRegion("inv_sword01");
//        swordFrames[1] = inventoryAtlas.findRegion("inv_sword02");
//        swordFrames[2] = inventoryAtlas.findRegion("inv_sword03");
//        swordFrames[3] = inventoryAtlas.findRegion("inv_sword04");
//        swordFrames[4] = inventoryAtlas.findRegion("inv_sword05");
//
//        swordAnimation = new Animation(ITEM_USE_FRAME_DURATION, swordFrames);

        TextureAtlas buttonAtlas = new TextureAtlas(Gdx.files.internal("buttons.atlas"));
        fireButtonTexture = buttonAtlas.findRegion("start");
        useButtonTexture = buttonAtlas.findRegion("exit");
    }

    public void render() {
        spriteBatch.setProjectionMatrix(cam.combined);
        Player bob = world.getBob();

        if (cam.position.x > 500 && bob.getPosition().x < 500) {
            this.cam.position.x = bob.getCentrePosition().x;
            this.cam.position.y = bob.getCentrePosition().y;
        } else if (!bob.getShieldCircle().contains(this.cam.position.x, this.cam.position.y)) {
            Vector2 camPosition = new Vector2(cam.position.x, cam.position.y);
            Vector2 distance = new Vector2(bob.getCentrePosition()).sub(camPosition);
            this.cam.position.x +=distance.x/35;
            this.cam.position.y +=distance.y/35;
        }

        this.cam.update();
        this.textCamera.update();
        spriteBatch.begin();
//        spriteBatch.enableBlending();
        if (bob.isInHouse()) {
            this.cam.position.x = 1000 * bob.getHouseNumber() + 5;
            this.cam.position.y = 1000 * bob.getHouseNumber() + 5;
            drawHouseBlocks(world.getLevel().getBuildings().get(bob.getHouseNumber()));
        } else {
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
        }
        TextureRegion holdingFrame = drawBob();
        if (world.isNightTime() || world.isDuskTillDawn()) {
            drawDark(bob);
        }
        drawHUD(bob, holdingFrame);
        if (world.getMoveJoystick() != null) drawJoystick(world.getMoveJoystick());
        if (world.getFireJoystick() != null) drawJoystick(world.getFireJoystick());
//        drawButtons();
//        spriteBatch.disableBlending();
        spriteBatch.end();
        drawDebug();
//        aiDebug();
//        drawCollisionBlocks();
        if (debug) drawDebug();
    }

    private void aiDebug(){
        debugRenderer.setProjectionMatrix(cam.combined);
        debugRenderer.setAutoShapeType(true);
        debugRenderer.begin(ShapeType.Line);
        debugRenderer.setColor(Color.RED);
        for (AIPlayer aiPlayer :  world.getAIPlayers()) {
            Block[][] blocks = aiPlayer.getView().getBlocks();
            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 8; j++) {
                    Block block = null;
                    if (blocks[i][j] != null) block = blocks[i][j];
                    if (block != null) debugRenderer.polygon(block.getBounds().getTransformedVertices());
                }
            }
            debugRenderer.setColor(Color.BLACK);
            Block[] blocking = aiPlayer.getView().getBlockingWall();
            for (Block block : blocking) {
                if (block != null)
                    debugRenderer.polygon(block.getBounds().getTransformedVertices());
            }
            debugRenderer.setColor(Color.BLUE);
            if (aiPlayer.getTarget() != null) debugRenderer.circle(aiPlayer.getTarget().x, aiPlayer.getTarget().y, 2);
        }
        debugRenderer.end();
    }

    private void drawHouseBlocks(Building building) {
        for (int i = building.getNumber() * 1000; i < building.getNumber() * 1000 + building.getInternalWidth(); i++) {
            for (int j = building.getNumber() * 1000; j < building.getNumber() * 1000 + building.getInternalHeight(); j++) {
                //todo add tile texture
                spriteBatch.draw(textureLoader.getRegion("tile"), i, j, Block.getSIZE(), Block.getSIZE());
            }
        }
        for(Block block : world.getDrawableHouseBlocks()) {
            if (block.getBlockType() == null) {
                spriteBatch.draw(textureLoader.getRegion(block.getName()), block.getPosition().x, block.getPosition().y, Block.getSIZE(), Block.getSIZE());
            } else {
                switch (block.getBlockType()) {
                    case WALL:
                        for (float rotation : ((Wall) block).getWalls().keySet()) {
                            Wall.WallType wall = ((Wall) block).getWalls().get(rotation);
                            if (wall != null) {
                                Polygon polygon = wall.getBounds();
                                spriteBatch.draw(textureLoader.getRegion(wall.getName()), polygon.getX(), polygon.getY(), 0, 0, Block.getSIZE(), Block.getSIZE()/2, 1F, 0.5F, rotation);
                            }
                        }
                        break;
                    case FILLABLE:
                        Polygon polygon = block.getBounds();
                        float rotation = polygon.getRotation();
                        Rectangle rectangle = polygon.getBoundingRectangle();
                        if (rotation == 90 || rotation == 270) {
                            spriteBatch.draw(textureLoader.getRegion(block.getName()), polygon.getX(), polygon.getY(), polygon.getOriginX(), polygon.getOriginY(), polygon.getBoundingRectangle().height, polygon.getBoundingRectangle().width, 1F, 1F, rotation);
                        }
                        else {
                            spriteBatch.draw(textureLoader.getRegion(block.getName()), polygon.getX(), polygon.getY(), polygon.getOriginX(), polygon.getOriginY(), polygon.getBoundingRectangle().width, polygon.getBoundingRectangle().height, 1F, 1F, rotation);
                        }
                        break;
                    default:
                        spriteBatch.draw(textureLoader.getRegion(block.getName()), block.getPosition().x, block.getPosition().y, Block.getSIZE(), Block.getSIZE());
                        break;
                }
            }
        }
    }

    private void drawFloor() {
        for (int i = 0; i < world.getLevel().getWidth(); i++) {
            for (int j = 0; j < world.getLevel().getHeight(); j++) {
                if (i % 10 == 0 && j % 10 == 0) spriteBatch.draw(textureLoader.getRegion("floor5"), i, j, Block.getSIZE()*10, Block.getSIZE()*10);
            }
        }
    }

    private void drawDark(Player player) {
        //todo make this a separate method getFires()
        List<Block> campfires = populateCampfires(new ArrayList<>());
        List<Projectile> fireballs = populateProjectiles(new ArrayList<>());
        List<Sprite> burningSprites = populateBurningSprites(new ArrayList<>());
        List<AreaAffect> lightingAreas = populateAreaAffects(new ArrayList<>());

        for (int i = 0; i < world.getLevel().getWidth(); i++) {
            for (int j = 0; j < world.getLevel().getHeight(); j++) {

                if (inView(new Vector2(i, j))) {
                    //todo make separate effort, passing circle and i and j
                    Vector2 tileCentre = new Vector2(i+0.5F, j+0.5F);
                    Circle shieldCircle = player.getShieldCircle();
                    boolean holdingFire = player.isHoldingFire();
                    boolean missFire = holdingFire && shieldCircle.contains(tileCentre);
                    boolean nearMissFire = !missFire && (holdingFire && new Circle(shieldCircle.x, shieldCircle.y, shieldCircle.radius + 2).contains(tileCentre));
                    if (!nearMissFire && !missFire && !holdingFire && new Circle(shieldCircle.x, shieldCircle.y, 2).contains(tileCentre)) nearMissFire = true;

                    if (!missFire) {
                        for (Block block : campfires) {
                            if (new Circle(block.getPosition().x, block.getPosition().y, 7).contains(tileCentre)) {
                                missFire = true;
                                break;
                            } else if (new Circle(block.getPosition().x, block.getPosition().y, 9).contains(tileCentre)) {
                                nearMissFire = true;
                                break;
                            }
                        }
                    }
                    if (!missFire) {
                        for (Projectile fireball : fireballs) {
                            if (new Circle(fireball.getPosition().x, fireball.getPosition().y, 5).contains(tileCentre)) {
                                missFire = true;
                                break;
                            }else if (new Circle(fireball.getPosition().x, fireball.getPosition().y, 7).contains(tileCentre)) {
                                nearMissFire = true;
                                break;
                            }
                        }
                    }
                    if (!missFire) {
                        for (Sprite sprite : burningSprites) {
                            if (new Circle(sprite.getPosition().x, sprite.getPosition().y, 5).contains(tileCentre)) {
                                missFire = true;
                                break;
                            }else if (new Circle(sprite.getPosition().x, sprite.getPosition().y, 7).contains(tileCentre)) {
                                nearMissFire = true;
                                break;
                            }
                        }
                    }
                    if (!missFire) {
                        for (AreaAffect areaAffect : lightingAreas) {
                            if (areaAffect.getBoundingCircle().contains(tileCentre)) {
                                missFire = true;
                                break;
                            }else if (areaAffect.getBoundingCircle().contains(tileCentre)) {
                                nearMissFire = true;
                                break;
                            }
                        }
                    }

                    if(!missFire) {
                        Color color = spriteBatch.getColor();
                        float oldAlpha = color.a;
                        if (nearMissFire) {
                            color.a = -0.65F;
                        } else {
                            color.a = world.isNightTime() ? 0.85f : 0.65F;
                        }
                        spriteBatch.setColor(color);
                        spriteBatch.draw(textureLoader.getRegion("dark"), i, j, Block.getSIZE(), Block.getSIZE());
                        color.a = oldAlpha;
                        spriteBatch.setColor(color);
                    }
                }
            }
        }
    }

    public List<Block> populateCampfires(List<Block> campfires) {
        for (Block block : world.getDrawableBlocks((int) CAMERA_WIDTH, (int)CAMERA_HEIGHT)) {
            if (block instanceof FillableBlock &&
                    (((FillableBlock) block).getFillableType().equals(FillableBlock.FillableType.CAMPFIRE) || ((FillableBlock) block).getFillableType().equals(FillableBlock.FillableType.TORCH))
                    && ((FillableBlock) block).isActive()) {
                campfires.add((block));
            }
        }
        return campfires;
    }

    public List<Projectile> populateProjectiles(List<Projectile> fireballs) {
        for (Projectile projectile : world.getProjectiles()) {
            if (projectile.getProjectileType().equals(FIREBALL)) {
                fireballs.add(projectile);
            }
        }
        return fireballs;
    }

    public List<AreaAffect> populateAreaAffects(List<AreaAffect> areaAffects) {
        for (AreaAffect areaAffect : world.getAreaAffects()) {
            if (areaAffect.getAffectType().equals(AreaAffect.AffectType.LIGHTNING))  {
                areaAffects.add(areaAffect);
            }
        }
        return  areaAffects;
    }

    public List<Sprite> populateBurningSprites(List<Sprite> sprites) {
        for (Sprite sprite : world.getAnimals()) {
            if (sprite.isOnfire()) sprites.add(sprite);
        }
        for (Sprite sprite : world.getAIPlayers()) {
            if (sprite.isOnfire()) sprites.add(sprite);
        }
        if (world.getBob().isOnfire()) sprites.add(world.getBob());
        return sprites;
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
//                        spriteBatch.draw(textureLoader.getRegion("floor"), block.getPosition().x, block.getPosition().y, Block.getSIZE(), Block.getSIZE());
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
                case BUILDING:
                    Polygon polygon = block.getBounds();
                    float rotation = polygon.getRotation();
                    Rectangle rectangle = polygon.getBoundingRectangle();
                    if (rotation == 90 || rotation == 270) {
                        spriteBatch.draw(textureLoader.getRegion(block.getName()), polygon.getX(), polygon.getY(), polygon.getOriginX(), polygon.getOriginY(), polygon.getBoundingRectangle().height, polygon.getBoundingRectangle().width, 1F, 1F, rotation);
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

    private void drawGunPads() {
        for (GunPad gunPad : world.getLevel().getGunPads()) {
            spriteBatch.draw(textureLoader.getRegion(gunPad.getName()), gunPad.getPosition().x, gunPad.getPosition().y, GunPad.getSIZE(), GunPad.getSIZE());
        }
    }

    public boolean inView(Vector2 target) {
        return new Circle(world.getBob().getCentrePosition().x, world.getBob().getCentrePosition().y, 25).contains(target);
    }

    private void drawFloorPads() {
        for (FloorPad floorPad : world.getLevel().getFloorPads()) {
            if (inView(floorPad.getPosition())) {
                TextureRegion floorFrame = textureLoader.getRegion(floorPad.getName());
                if (floorFrame != null) {
                    if (floorPad.getType().equals(IRRIGATION)) {
                        spriteBatch.draw(floorFrame, floorPad.getPosition().x, floorPad.getPosition().y, Block.getSIZE()/2, Block.getSIZE()/2,
                                Block.getSIZE(), Block.getSIZE(), 1, 1, floorPad.isStraightPiece() ? floorPad.getRotation() : floorPad.getRotation() - 45);
                    } else if (floorPad.getType().equals(MOVE) || floorPad.getType().equals(WATERFLOW)) {
                        spriteBatch.draw(floorFrame, floorPad.getPosition().x, floorPad.getPosition().y, Block.getSIZE()/2, Block.getSIZE()/2,
                                Block.getSIZE(), Block.getSIZE(), 1, 1, floorPad.getRotation() + 90, true);
                    } else {
                        spriteBatch.draw(floorFrame, floorPad.getPosition().x, floorPad.getPosition().y, GunPad.getSIZE(), GunPad.getSIZE());
                    }
                }
            }
        }
    }

    private void drawBloodStains() {
        for (BloodStain bloodStain : world.getBloodStains()) {
            if (inView(bloodStain.getPosition())) {
                TextureRegion stainFrame = textureLoader.getRegion("dead");
                spriteBatch.draw(stainFrame, bloodStain.getPosition().x, bloodStain.getPosition().y, bloodStain.getWidth(), bloodStain.getHeight());
            }
        }
    }

    private void drawAreaAffects() {
        for (AreaAffect areaAffect : world.getAreaAffects()) {
            if (inView(areaAffect.getPosition())) {
                if (!areaAffect.isFinished() && !areaAffect.getAffectType().equals(AreaAffect.AffectType.DAMAGE)) {
                    spriteBatch.draw(textureLoader.getRegion(areaAffect.getName()), areaAffect.getPosition().x - areaAffect.getWidth()/2, areaAffect.getPosition().y - areaAffect.getHeight()/2,areaAffect.getWidth()/2, areaAffect.getHeight()/2, areaAffect.getWidth(), areaAffect.getHeight(), 1F, 1F, 0, true);
                }
            }
        }
    }

    private void drawProjectiles() {
        for (Projectile projectile : world.getProjectiles()) {
            if (inView(projectile.getPosition())) {
                TextureRegion bulletFrame = textureLoader.getRegion(projectile.getName());
                if (projectile.getProjectileType().equals(FIREBALL)) {
                    Random rand = new Random();
                    switch (rand.nextInt(4)) {
                        case 1:
                            bulletFrame = textureLoader.getRegion("fireball-01");
                            break;
                        case 2:
                            bulletFrame = textureLoader.getRegion("fireball-02");
                            break;
                        case 3:
                            bulletFrame = textureLoader.getRegion("fireball-03");
                            break;
                        default:
                            bulletFrame = textureLoader.getRegion("fireball-04");
                            break;
                    }
                }

                if (projectile.isExploding()) {
                    bulletFrame = (TextureRegion) (explodeAnimation.getKeyFrame(projectile.getStateTime(), true));
                    spriteBatch.draw(bulletFrame, projectile.getPosition().x, projectile.getPosition().y, 0, projectile.getHeight()/2,
                            projectile.getWidth(), projectile.getHeight(), 2.5F, 5, projectile.getRotation(), true);
                } else {
                    Polygon bounds = projectile.getBounds();
                    Vector2 pos = new Vector2(projectile.getBounds().getX(), projectile.getBounds().getY());
                    spriteBatch.draw(bulletFrame, pos.x, pos.y, bounds.getOriginX(), bounds.getOriginY(),
                            projectile.getWidth(), projectile.getHeight(), 1, 1, bounds.getRotation(), true);
                }
            }
        }
    }

    private void drawWall(float rotation, Point gridRef, Placeable wall) {
        String name = wall.getPlaceableType().equals(Placeable.PlaceableType.DOOR) ? "door" : "wall";
        TextureRegion region = textureLoader.getRegion(name);
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

    private void drawBench(float rotation, Point gridRef, Item bench, float width, float height) {
        String name = bench.getName();
        TextureRegion region = textureLoader.getRegion(name);
        spriteBatch.draw(region, gridRef.x, gridRef.y, 0.5F, 0.5F, width, height, 1F, 1F, rotation);
    }

    private void drawHandItem(Player sprite, float rotation, Vector2 gridRef, float width, float height, TextureRegion textureToDraw) {
        Vector2 drawVector = getDrawVector(sprite, rotation, gridRef);
        spriteBatch.draw(textureToDraw, drawVector.x, drawVector.y,0, 0, 1.5F, 1.5F, 1F, 1F, rotation - 45);
    }

    private void drawTargetCircle(Vector2 gridRef, float width, float height, TextureRegion textureToDraw) {
        spriteBatch.draw(textureToDraw, gridRef.x - width/2, gridRef.y - height/2,width/2, height/2, width, height, 1F, 1F, 0);
    }

    private void drawHand(Player sprite, float rotation, Vector2 gridRef, float width, float height, TextureRegion textureToDraw) {
        //todo sort out right hand
        Vector2 drawVector = getDrawVector(sprite, rotation, gridRef);
        spriteBatch.draw(textureToDraw, drawVector.x, drawVector.y, 0, 0, width, height, 1F, 1F, rotation);
    }

    public Vector2 getDrawVector(Player sprite, float rotation, Vector2 gridRef) {
        Vector2 drawVector = null;
        if (sprite.getHitPhase() == 0) {
            float x, y;
            x = gridRef.x + (float)(sprite.getWidth()/1.5 * Math.cos((rotation) * Math.PI/180));
            y = gridRef.y + (float)(sprite.getHeight()/1.5 * Math.sin((rotation) * Math.PI/180));
            drawVector = new Vector2(x,y);
        } else {
            if (sprite.getStrongHand() != null) {
                switch (sprite.getStrongHand().getType()) {
                    case ITEM:
                        Item item = (Item) sprite.getStrongHand();
                        switch (item.getItemType()) {
                            case SWINGABLE:
                                switch (((Swingable) item).getSwingableType()) {
                                    case SWORD:
                                    case HOE:
                                        if (sprite.getComboPhase() == 0) {
                                            drawVector = drawLunge(sprite, gridRef, rotation);
                                        } else if (sprite.getComboPhase() == 1) {
                                            drawVector = drawStab(sprite, gridRef, rotation);
                                        } else  {
                                            drawVector = drawSlash(sprite, gridRef, rotation);
                                        }
                                        break;
                                    case PICK:
                                    case SHOVEL:
                                    case CLUB:
                                    case HAMMER:
                                    case AXE:
                                        drawVector = drawSlash(sprite, gridRef, rotation);
                                        break;
                                }
                        }
                }
            } else {
                if (sprite.getComboPhase() == 0) {
                    drawVector = drawLunge(sprite, gridRef, rotation);
                } else if (sprite.getComboPhase() == 1) {
                    drawVector = drawStab(sprite, gridRef, rotation);
                } else  {
                    drawVector = drawSlash(sprite, gridRef, rotation);
                }
            }
        }
        return drawVector;
    }

    public Vector2 drawStab(Player sprite, Vector2 gridRef, float rotation) {
        return new Vector2(sprite.getHitCircle().x,sprite.getHitCircle().y);
    }

    public Vector2 drawLunge(Player sprite, Vector2 gridRef, float rotation) {
        return new Vector2(sprite.getHitCircle().x,sprite.getHitCircle().y);
    }

    public Vector2 drawSlash(Player sprite, Vector2 gridRef, float rotation) {
        return new Vector2(sprite.getHitCircle().x,sprite.getHitCircle().y);
    }

    private void drawHUD(Player bob, TextureRegion holdingFrame) {
        Vector2 externalPos = new Vector2(cam.position.x, cam.position.y);

        spriteBatch.setProjectionMatrix(textCamera.combined);
        font.setColor(Color.WHITE);
        if (world.getTime() != null) {
            font.draw(spriteBatch, "" + world.getTime(), textCamera.position.x + 180, textCamera.position.y + 135, 1, 1, true);
        }
        spriteBatch.setProjectionMatrix(cam.combined);
        for (float i = 0; i < bob.getLives(); i++) {
            float xPos = 12 + externalPos.x - (i/4);
            float yPos = externalPos.y + 6;
            spriteBatch.draw(textureLoader.getRegion("heart"), xPos, yPos, 1, 1, 1, 1, 0.25F, 0.25F, 0);
        }
        for (float i = 0; i < bob.getMana(); i++) {
            float xPos = 12 + externalPos.x - (i/4);
            float yPos = externalPos.y + 5.5F;
            spriteBatch.draw(textureLoader.getRegion("mana"), xPos, yPos, 1, 1, 1, 1, 0.25F, 0.25F, 0);
        }
        for (float i = 0; i < bob.getWater(); i++) {
            float xPos = 12 + externalPos.x - (i/4);
            float yPos = externalPos.y + 5F;
            spriteBatch.draw(textureLoader.getRegion("inv_jarFull"), xPos, yPos, 1, 1, 1, 1, 0.25F, 0.25F, 0);
        }
        for (float i = 0; i < bob.getFood(); i++) {
            float xPos = 12 + externalPos.x - (i/4);
            float yPos = externalPos.y + 4.5F;
            spriteBatch.draw(textureLoader.getRegion("meat"), xPos, yPos, 1, 1, 1, 1, 0.25F, 0.25F, 0);
        }

        if (holdingFrame != null) {
            float xPos = 12 + externalPos.x;
            float yPos = externalPos.y + 4;
            spriteBatch.draw(buttonsAtlas.findRegion("inventoryBox"), xPos, yPos,  1, 1, 1F, 1, 1F, 1F, 0);
            spriteBatch.draw(holdingFrame, xPos, yPos, 1, 1, 1F, 1, 1F, 1F, 0);
        }

        Inventory toolbelt = bob.getToolBelt();
        for (int i = 0; i < toolbelt.getSize(); i++) {
            float xPos = externalPos.x - 8 + 2*i;
            float yPos = externalPos.y - 7;
            spriteBatch.draw(buttonsAtlas.findRegion("inventoryBox"), xPos, yPos,  1, 1, 1F, 1, 1F, 1F, 0);
            if (i == bob.getSlotNo()) {
                spriteBatch.draw(textureLoader.getRegion("explodingBlockYellow"), xPos, yPos,  0.5F, 0.5F, 1.1F, 1.1F, 1.2F, 1.2F, 0);
            }
            Material material = (Material)toolbelt.getSlots().get(i);
            if (material != null) {
                TextureRegion toDraw = textureLoader.getRegion(material.getName());
                if (toDraw == null) toDraw = inventoryAtlas.findRegion(material.getName());
                if (toDraw == null && material instanceof Placeable && ((Placeable) material).getPlaceableType().equals(Placeable.PlaceableType.TORCH)) {
                    toDraw = textureLoader.getRegion(material.getName());
                }
                if (toDraw == null && material instanceof Magic) toDraw = textureLoader.getRegion(material.getName() + "-01");


                if (toDraw != null) spriteBatch.draw(toDraw, xPos, yPos,  1, 1, 1F, 1, 1F, 1F, 0);
                if (material instanceof Item && ((Item) material).getDurability() <= 0) {
                    Color color = spriteBatch.getColor();
                    float oldAlpha = color.a;

                    color.a = 0.65F;
                    spriteBatch.setColor(color);
                    spriteBatch.draw(textureLoader.getRegion("dark"), xPos, yPos,  1F, 1F, 1F, 1F, 1F, 1F, 0);
                    color.a = oldAlpha;
                    spriteBatch.setColor(color);
                }
                if (material.getMaxPerStack() > 1) {
                    spriteBatch.setProjectionMatrix(textCamera.combined);
                    font.draw(spriteBatch, material.getQuantity() + "", textCamera.position.x - 120 + i*34, textCamera.position.y - 115, 1, 1, true);
                    spriteBatch.setProjectionMatrix(cam.combined);
                }
            }
        }
        font.setColor(Color.BLACK);
    }

    private TextureRegion drawBob() {
        Player bob = world.getBob();
        TextureRegion bobFrame = bob.isInjured() ? textureLoader.getRegion("sprite-01")
                : (bob.getTorso() != null && bob.getTorso().getItemType().equals(Item.ItemType.ARMOUR)) ? textureLoader.getRegion("armour-01")
                : textureLoader.getRegion("sprite-01");

        TextureRegion holdingFrame = null;
        float xPos = bob.getCentrePosition().x;
        float yPos = bob.getCentrePosition().y;

        if(bob.getState().equals(Player.State.MOVING)) {
            if (bob.getTorso() != null && bob.getTorso().getItemType().equals(Item.ItemType.ARMOUR)) {
                bobFrame =  (armourAnimation.getKeyFrame(bob.getStateTime(), true));
            } else {
                bobFrame =  (walkAnimation.getKeyFrame(bob.getStateTime(), true));
            }
        }
        float drawAngle = bob.getRotation();
        drawHand(bob,bob.getRotation() - 35, bob.getCentrePosition(), bob.getWidth(), bob.getHeight(), textureLoader.getRegion("hand-01"));
        drawHand(bob,bob.getRotation() + 55, bob.getCentrePosition(), bob.getWidth(), bob.getHeight(), textureLoader.getRegion("hand-01"));
        spriteBatch.draw(bobFrame, bob.getPosition().x, bob.getPosition().y, bob.getWidth()/2, bob.getHeight()/2, bob.getWidth(), bob.getHeight(),
                1, 1, drawAngle, true);
        spriteBatch.draw(textureLoader.getRegion("head-01"), bob.getPosition().x, bob.getPosition().y, bob.getWidth()/2, bob.getHeight()/2, bob.getWidth(), bob.getHeight(),
                1, 1, drawAngle, true);
        if (bob.isOnfire()) {
            Random rand = new Random();
            TextureRegion burningFrame = textureLoader.getRegion("onfire-0" + (rand.nextInt(4) + 1));
            spriteBatch.draw(burningFrame, bob.getPosition().x, bob.getPosition().y, bob.getWidth()/2, bob.getHeight()/2, bob.getWidth(), bob.getHeight(),
                    1, 1, bob.getRotation(), true);
        }

        if (bob.getStrongHand() != null)  {
            Object o = bob.getStrongHand();
            if (o instanceof Item) {
                Item item = (Item) o;
                float rotation = item.getRotation();
                if (item instanceof Placeable) {
                    Placeable placeable = (Placeable) item;
                    Point gridRef = bob.getGridRef(bob.getRotation(), xPos, yPos);
                    Object ob = world.getLevel().getBlock(gridRef.x, gridRef.y);
                    if (placeable.getPlaceableType().equals(Placeable.PlaceableType.WALL) || placeable.getPlaceableType().equals(Placeable.PlaceableType.DOOR)) {
                        if (ob instanceof Wall) {
                            if (!((Wall) ob).isWallFull(rotation)) {
                                for (float wallRotation : ((Wall) ob).getWalls().keySet()) {
                                    if (((Wall) ob).getWalls().get(wallRotation) == null) {
                                        drawWall(rotation, gridRef, placeable);
                                    }
                                }
                            }
                        } else if (ob == null) {
                            drawWall(rotation, gridRef, placeable);
                        }
                    } else {
                        if (ob == null) {
                            drawBench(rotation, gridRef, placeable, placeable.getWidth(), placeable.getHeight());
                        }
                    }
                }

                {
                    TextureRegion itemFrame = null ;
                    float itemWidth = 0, itemHeight = 0, itemRotation = 0, itemRadius = 0;
                    boolean using = bob.isUseTimerOn();
                    if (bob.getStrongHand() instanceof Item) {
                        if (item instanceof Magic) {
                            Magic magic = (Magic) item;
                            switch (magic.getMagicType()) {
                                case PROJECTILE:
                                    switch (magic.getProjectileType()) {
                                        case FIREBALL:
                                            Random rand = new Random();
                                            switch (rand.nextInt(4)) {
                                                case 1:
                                                    itemFrame = textureLoader.getRegion("fireball-01");
                                                    break;
                                                case 2:
                                                    itemFrame = textureLoader.getRegion("fireball-02");
                                                    break;
                                                case 3:
                                                    itemFrame = textureLoader.getRegion("fireball-03");
                                                    break;
                                                default:
                                                    itemFrame = textureLoader.getRegion("fireball-04");
                                                    break;
                                            }
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                                case SELF:
                                    itemFrame = textureLoader.getRegion(item.getName());
                                    break;
                                case AREA:
                                    switch (magic.getElement()) {
                                        case ELECTRIC:
                                            Random rand = new Random();
                                            switch (rand.nextInt(4)) {
                                                case 1:
                                                    itemFrame = textureLoader.getRegion("lightning-01");
                                                    break;
                                                case 2:
                                                    itemFrame = textureLoader.getRegion("lightning-02");
                                                    break;
                                                case 3:
                                                    itemFrame = textureLoader.getRegion("lightning-03");
                                                    break;
                                                default:
                                                    itemFrame = textureLoader.getRegion("lightning-04");
                                                    break;
                                            }
                                            drawTargetCircle(bob.getLeftHandPosition(0, 6F), 3F, 3F, textureLoader.getRegion("target"));
                                            break;
                                        default:
                                            break;
                                    }
                                    break;
                            }
                            itemHeight = 0.5F;
                            itemWidth = 0.5f;
                        }
                        switch (item.getItemType()) {
                            case SWINGABLE:
                                assert item instanceof Swingable;
                                Swingable swingable = (Swingable) item;
                                switch (swingable.getSwingableType()) {
                                    default:
                                        itemWidth = bob.getWidth();
                                        itemHeight = bob.getHeight();
                                        break;
                                }
                                break;
                            case THROWABLE:
                                assert item instanceof Throwable;
                                Throwable throwable = (Throwable) item;
                                switch (throwable.getThrowableType()) {
                                    case SPEAR:
                                        itemHeight = 0.25F;
                                        itemWidth = 2f;
                                        break;
                                }
                                break;
                            case JAR:
                                assert item instanceof Fillable;
                                Fillable jar = (Fillable)item;
                                itemFrame = jar.isFilled() ? textureLoader.getRegion("inv_jarFull") : textureLoader.getRegion("inv_jar");
                                itemHeight = 0.25F;
                                itemWidth = 2f;
                                break;
                            case MAGIC:
                                break;
                            default:
                                itemFrame = textureLoader.getRegion(item.getName());
                                itemHeight = 1F;
                                itemWidth = 1f;
                                if (itemFrame == null) itemFrame = inventoryAtlas.findRegion(item.getName());
                        }
                        if (item instanceof Placeable && ((Placeable) item).getPlaceableType().equals(Placeable.PlaceableType.TORCH)) {
                            Random rand = new Random();
                            int myNum = rand.nextInt(4) + 1;
                            itemFrame = textureLoader.getRegion("torch-burning-0" + myNum);
                        }
                        if (itemFrame == null)itemFrame = textureLoader.getRegion(item.getName());
                        if (itemFrame == null) itemFrame = inventoryAtlas.findRegion(item.getName());

                    } else {
                        itemWidth = bob.getWidth()/2;
                        itemHeight = bob.getHeight()/2;
                    }
                    drawHandItem(bob,bob.getRotation() + 45, bob.getCentrePosition(), itemWidth, itemHeight, itemFrame);
                }

                if (item instanceof Swingable) {
                    Swingable swingable = (Swingable) item;
                    if (swingable.getSwingableType().equals(HOE) || swingable.getSwingableType().equals(SHOVEL)) {
                        Point gridRef = bob.getGridRef(bob.getRotation(), xPos, yPos);
                        Object ob = world.getLevel().getBlock(gridRef.x, gridRef.y);
                        TextureRegion toDraw = null;
                        boolean straightPiece = true;
                        switch (swingable.getSwingableType()) {
                            case HOE:
                                toDraw = new TextureRegion(textureLoader.getRegion("tilled"));
                                break;
                            case SHOVEL:
                                straightPiece = item.getRotation() == 0 || item.getRotation() == 90 || item.getRotation() == 180 || item.getRotation() == 270;
                                toDraw = straightPiece ? new TextureRegion(textureLoader.getRegion("irrigation-empty")) : new TextureRegion(textureLoader.getRegion("irrigationBend-empty"));
                                break;
                        }
                        if (ob == null) {
                            Color color = spriteBatch.getColor();
                            float oldAlpha = color.a;
                            color.a = 0.3f;
                            spriteBatch.setColor(color);
                            spriteBatch.draw(new TextureRegion(toDraw), gridRef.x, gridRef.y, 0.5F, 0.5F, 1F, Block.getSIZE(),
                                    1F, 1F, straightPiece ? item.getRotation() : item.getRotation() - 45);
                            color.a = oldAlpha;
                            spriteBatch.setColor(color);
                        }
                    }
                }
            } else {
                TextureRegion handFrame;
                handFrame = textureLoader.getRegion(bob.getStrongHand().getName());
                if (handFrame == null) inventoryAtlas.findRegion(bob.getStrongHand().getName());
                if (handFrame != null )drawHandItem(bob,bob.getRotation() + 45, bob.getCentrePosition(), bob.getWidth(), bob.getHeight(), handFrame);
                if (handFrame != null )drawHandItem(bob,bob.getRotation() + 45, bob.getCentrePosition(), bob.getWidth(), bob.getHeight(), handFrame);
            }
            holdingFrame = textureLoader.getRegion(bob.getStrongHand().getName());
            if (holdingFrame == null) holdingFrame = inventoryAtlas.findRegion(bob.getStrongHand().getName());
        }

        if (bob.getWeakHand() != null && bob.getWeakHand().getItemType().equals(Item.ItemType.SHIELD)) {
            if (bob.isLeftHanded()) {
                spriteBatch.draw(textureLoader.getRegion("shield-01"), bob.getRightHandPosition().x, bob.getRightHandPosition().y, 0, 0, 0.25F, 1, 1F, 1F, bob.getBlockRectangle().getRotation());
            } else {
                spriteBatch.draw(textureLoader.getRegion("shield-01"), bob.getLeftHandPosition(45, bob.getWidth()).x, bob.getLeftHandPosition(45, bob.getWidth()).y, 0, 0, 0.25F, -1, 1F, 1F, bob.getBlockRectangle().getRotation());
            }
        }

        if (!bob.getBoost().equals(Player.Boost.NOTHING)) {
            TextureRegion boostFrame = null;

            switch (bob.getBoost()) {
                case HOMING:
                    boostFrame = textureLoader.getRegion("homingBoost");
                    break;
                case SPEED:
                    boostFrame = textureLoader.getRegion("speedBoost");
                    break;
                case DAMAGE:
                    boostFrame = textureLoader.getRegion("damageBoost");
                    break;
                case SHIELD:
                    boostFrame = textureLoader.getRegion("shieldBoost");
                    TextureRegion shieldFrame = (shieldAnimation.getKeyFrame(bob.getStateTime(), true));
                    Circle circle = bob.getShieldCircle();
                    spriteBatch.draw(shieldFrame, circle.x - circle.radius/2, circle.y - circle.radius/2, 1, 1, 2F, 2, 2.00F, 2.00F, 0);
                    break;
                case HEALING:
                    boostFrame = textureLoader.getRegion("healing");
                    circle = bob.getShieldCircle();
                    spriteBatch.draw(boostFrame, circle.x - circle.radius/2, circle.y - circle.radius/2, 1, 1, 2F, 2, 2.00F, 2.00F, 0);
                    break;
            }

            xPos = bob.getPosition().x + 4.5F;
            if (boostFrame != null) {
                spriteBatch.draw(boostFrame, xPos, yPos, 1, 1, 1F, 1, 0.75F, 0.75F, 0);
            }

        }

        if (bob.getGun() != null) {
            if (bob.getGun().isReloading()) {
                float x = world.getBob().getPosition().x - (CAMERA_WIDTH/2) + 3;
                float y = world.getBob().getPosition().y + (CAMERA_HEIGHT/2) - 1.5F;
                spriteBatch.draw(textureLoader.getRegion("reloading"), x, y, 1, 1, 6.0F, 2.0F, 1.00F, 1.00F, 0);
            }
        }
//        if (world.getFireJoystick() != null && world.getFireJoystick().getDrag() != null) drawAngle = world.getFireJoystick().getAngle();
        return holdingFrame;
        //todo make injured sprites
    }

    private void drawAis() {
        for (AIPlayer aiPlayer : world.getAIPlayers()) {
            if (inView(aiPlayer.getCentrePosition())) {
                Vector2 livesPos = aiPlayer.getCentrePosition();
                for (float i = 0; i < aiPlayer.getLives(); i++) {
                    float xPos = livesPos.x - (i/15);
                    float yPos = livesPos.y;
                    spriteBatch.draw(textureLoader.getRegion("heart"), xPos, yPos, 1, 1, 1, 1, 0.05F, 0.05F, 0);
                }
                TextureRegion aiFrame = textureLoader.getRegion("sprite-01");
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
                    TextureRegion burningFrame = textureLoader.getRegion("onfire-0" + (rand.nextInt(4) + 1));
                    spriteBatch.draw(burningFrame, aiPlayer.getPosition().x, aiPlayer.getPosition().y, aiPlayer.getWidth()/2, aiPlayer.getHeight()/2, aiPlayer.getWidth(), aiPlayer.getHeight(),
                            1, 1, aiPlayer.getRotation(), true);
                }

                if (!aiPlayer.getBoost().equals(Player.Boost.NOTHING)) {

                    if (aiPlayer.getBoost() == Player.Boost.SHIELD) {
                        TextureRegion shieldFrame = (shieldAnimation.getKeyFrame(aiPlayer.getStateTime(), true));
                        Circle circle = aiPlayer.getShieldCircle();
                        spriteBatch.draw(shieldFrame, circle.x - circle.radius / 2, circle.y - circle.radius / 2, 1, 1, 2F, 2, 2.00F, 2.00F, 0);
                    }
                }
                if (aiPlayer.getStrongHand() != null)  {
                    if (aiPlayer.getStrongHand() instanceof Item) {
                        Item item = (Item) aiPlayer.getStrongHand();
                        if (aiPlayer.isUseTimerOn()) {
                            TextureRegion itemFrame = null;
                            switch (item.getItemType()) {
//                                case SWINGABLE:
//                                    Swingable swingable = (Swingable) item;
//                                    switch (swingable.getSwingableType()) {
//                                        case PICK:
//                                            itemFrame = pickAnimation.getKeyFrame(aiPlayer.getStateTime(), true);
//                                            break;
//                                        case SWORD:
//                                            itemFrame = swordAnimation.getKeyFrame(aiPlayer.getStateTime(), true);
//                                            break;
//                                    }
//                                    break;
                                case JAR:
                                    Fillable jar = (Fillable) item;
                                    itemFrame = jar.isFilled() ? textureLoader.getRegion("inv_jarFull") : textureLoader.getRegion("inv_jar");
                            }
                            drawHandItem(aiPlayer,aiPlayer.getRotation() + 45, aiPlayer.getCentrePosition(), aiPlayer.getWidth(), aiPlayer.getHeight(), textureLoader.getRegion("hand-01"));
                        }
                    }
                }
                if (aiPlayer.getWeakHand() != null && aiPlayer.getWeakHand().getItemType().equals(Item.ItemType.SHIELD)) {
                    if (aiPlayer.isLeftHanded()) {
                        spriteBatch.draw(textureLoader.getRegion("shield-01"), aiPlayer.getRightHandPosition().x, aiPlayer.getRightHandPosition().y, 0, 0, 0.25F, 1, 1F, 1F, aiPlayer.getBlockRectangle().getRotation());
                    } else {
                        spriteBatch.draw(textureLoader.getRegion("shield-02"), aiPlayer.getLeftHandPosition(45, aiPlayer.getWidth()).x, aiPlayer.getLeftHandPosition(45, aiPlayer.getWidth()).y, 0, 0, 0.25F, -1, 1F, 1F, aiPlayer.getBlockRectangle().getRotation());
                    }
                }
            }
        }
    }

    private void drawAnimals() {
        for (Animal animal : world.getAnimals()) {
            if (inView(animal.getCentrePosition())) {
                //draw the lives
                Vector2 livesPos = animal.getCentrePosition();

////            if (animal.getLives() < animal.getMaxLives()) {
                for (float i = 0; i < animal.getLives(); i++) {
                    float xPos = livesPos.x - (i/5);
                    float yPos = livesPos.y;
                    spriteBatch.draw(textureLoader.getRegion("heart"), xPos, yPos, 1, 1, 0.2F, 0.2F, 0.5F, 0.5F, 0);
                }
////            }
//            for (float i = 0; i < animal.getFood(); i++) {
//                float xPos = livesPos.x - (i/2);
//                float yPos = livesPos.y + 0.5F;
//                spriteBatch.draw(textureLoader.getRegion("meat"), xPos, yPos, 1, 1, 1, 1, 0.5F, 0.5F, 0);
//            }
//            for (float i = 0; i < animal.getWater(); i++) {
//                float xPos = livesPos.x - (i/2);
//                float yPos = livesPos.y + 1;
//                spriteBatch.draw(textureLoader.getRegion("inv_jarFull"), xPos, yPos, 1, 1, 1, 1, 0.5F, 0.5F, 0);
//            }

//            spriteBatch.setProjectionMatrix(textCamera.combined);
//            font.setColor(Color.WHITE);
//            if (world.getTime() != null) {
//                font.draw(spriteBatch, animal.getName() + world.getTime(), textCamera.position.x + 180, textCamera.position.y + 135, 1, 1, true);
//            }
//            spriteBatch.setProjectionMatrix(cam.combined);
                //draw the animal
                spriteBatch.draw(textureLoader.getRegion(animal.getName()), animal.getPosition().x, animal.getPosition().y, animal.getWidth()/2, animal.getHeight()/2, animal.getWidth(), animal.getHeight(),
                        1, 1, animal.getRotation(), true);

                if (animal.isOnfire()) {
                    //draw the animal flames
                    Random rand = new Random();
                    TextureRegion burningFrame = textureLoader.getRegion("onfire-0" + (rand.nextInt(4) + 1));
                    spriteBatch.draw(burningFrame, animal.getPosition().x, animal.getPosition().y, animal.getWidth()/2, animal.getHeight()/2, animal.getWidth(), animal.getHeight(),
                            1, 1, animal.getRotation(), true);
                }
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
        for (GameButton gameButton : world.getButtons()) {
            TextureRegion buttonFrame = null;
            switch (gameButton.getType()) {
                case FIRE:
                    buttonFrame = fireButtonTexture;
                    break;
                case USE:
                    buttonFrame = useButtonTexture;
                    break;
            }
            float xish, yish;
            if (Gdx.app.getType().equals(Application.ApplicationType.Android)) {
                xish = world.getBob().getPosition().x + gameButton.getArea().x - CAMERA_WIDTH/2 - gameButton.getArea().radius;
                yish = world.getBob().getPosition().y - gameButton.getArea().y + CAMERA_HEIGHT/2;
            } else {
                xish = world.getBob().getPosition().x + gameButton.getArea().x - CAMERA_WIDTH/2 - gameButton.getArea().radius;
                yish = world.getBob().getPosition().y - gameButton.getArea().y + CAMERA_HEIGHT/2;
            }

            if (buttonFrame != null) spriteBatch.draw(buttonFrame, xish, yish, 1, 1, 1.0F, 1.0F, 1.00F, 1.00F, 0);
        }
    }

    private void drawDebug() {
        debugRenderer.setProjectionMatrix(cam.combined);
        debugRenderer.setAutoShapeType(true);
        debugRenderer.begin(ShapeType.Line);
        debugRenderer.setColor(Color.RED);
        Player bob = world.getBob();

        //get position for the hitcircle for area magic

//        if (bob.getStrongHand() != null && bob.getStrongHand() instanceof Magic && ((Magic) bob.getStrongHand()).getMagicType().equals(Magic.MagicType.AREA)) {
//            debugRenderer.setColor(Color.NAVY);
//            Vector2 pos = bob.getLeftHandPosition(0, 6);
//            debugRenderer.circle(pos.x,pos.y, 1.5F);
//            debugRenderer.setColor(Color.BLACK);
//            debugRenderer.circle(bob.getCollideCircle().x, bob.getCollideCircle().y, bob.getCollideCircle().radius);
//        }

        for (AreaAffect areaAffect : world.getAreaAffects()) {
            debugRenderer.circle(areaAffect.getBoundingCircle().x, areaAffect.getBoundingCircle().y, areaAffect.getBoundingCircle().radius);
        }

//        debugRenderer.circle(bob.getCentrePosition().x, bob.getCentrePosition().y, 2);
//        debugRenderer.polygon(bob.getViewCircle().getTransformedVertices());
//        debugRenderer.circle(world.getBob().getViewCircle().getX(), world.getBob().getViewCircle().y, world.getBob().getViewCircle().radius);
//        for (AIPlayer aiPlayer :  world.getAIPlayers()) {
//            debugRenderer.polygon(aiPlayer.getViewCircle().getTransformedVertices());
////            debugRenderer.polygon(aiPlayer.getViewCircle().getX(), aiPlayer.getViewCircle().getY(), aiPlayer.getViewCircle().);
//            debugRenderer.setColor(Color.BLACK);
//            debugRenderer.polygon(aiPlayer.getBlockRectangle().getTransformedVertices());
//            debugRenderer.setColor(Color.ROYAL);
//            debugRenderer.circle(aiPlayer.getRightHandPosition().x, aiPlayer.getRightHandPosition().y, 1F);
//            debugRenderer.circle(aiPlayer.getLeftHandPosition(45, aiPlayer.getWidth()).x, aiPlayer.getLeftHandPosition(45, aiPlayer.getWidth()).y, 1F);
//            debugRenderer.setColor(Color.BLACK);
//            debugRenderer.circle(aiPlayer.getCollideCircle().x, aiPlayer.getCollideCircle().y, aiPlayer.getCollideCircle().radius);
//        }
        for (Animal animal : world.getAnimals()) {
            debugRenderer.setColor(Color.GREEN);
            debugRenderer.polygon(animal.getBounds().getTransformedVertices());
//            debugRenderer.setColor(Color.PURPLE);
//            debugRenderer.circle(animal.getHitCircle().x, animal.getHitCircle().y, animal.getHitCircle().radius);
////            debugRenderer.circle(animal.getHitCircle().x, animal.getHitCircle().y, animal.getHitCircle().radius);
//            debugRenderer.setColor(Color.BLACK);
////            debugRenderer.circle(animal.getCollideCircle().x, animal.getCollideCircle().y, animal.getCollideCircle().radius);
//            Block[] blocks = animal.getView().getBlockingWall();
//            for (Block block : blocks) {
//                if (block != null) debugRenderer.polygon(block.getBounds().getTransformedVertices());
//            }
//            Block[][] viewBlocks = animal.getView().getBlocks();
//            for (Block[] viewBlock : viewBlocks) {
//                for (int j = 0; j < viewBlocks[0].length; j++) {
//                    if (viewBlock[j] != null) {
//                        debugRenderer.setColor(Color.RED);
//                        debugRenderer.polygon(viewBlock[j].getBounds().getTransformedVertices());
//                    }
//                }
//            }
//            if (animal.getAi() != null) {
//                if (animal.getAi().getTargetCoordinates() != null) {
//                    for (Vector2 coor : animal.getAi().getTargetCoordinates()) {
//                        debugRenderer.setColor(Color.ORANGE);
//                        debugRenderer.circle(coor.x, coor.y, 2);
//                    }
//                }
//            }
//            if (animal.getTarget() != null) {
//                debugRenderer.setColor(Color.BLACK);
//                debugRenderer.circle(animal.getTarget().x, animal.getTarget().y, 2);
//            }
//        }
//        debugRenderer.setColor(Color.RED);
//        for (Projectile projectile : world.getProjectiles()) {
//            if (projectile.isHoming()) {
//                debugRenderer.circle(projectile.getViewCircle().x, projectile.getViewCircle().y, projectile.getViewCircle().radius);
//            }
//            debugRenderer.polygon(projectile.getBounds().getTransformedVertices());
        }
//        for (Block block : world.getDrawableBlocks((int)CAMERA_WIDTH, (int)CAMERA_HEIGHT)) {
//            if (block instanceof Wall) {
//                for (Wall.WallType wall : ((Wall) block).getWalls().values()) {
//                    if (wall != null) {
////                        debugRenderer.polygon(wall.getBounds().getTransformedVertices());
//                    }
//                }
//            } else {
////                debugRenderer.polygon(block.getBounds().getTransformedVertices());
//            }
//        }
        debugRenderer.setColor(Color.FIREBRICK);
//        debugRenderer.circle(bob.getHitCircle().x, bob.getHitCircle().y, bob.getHitCircle().radius);
//        debugRenderer.setColor(Color.BLACK);
//        debugRenderer.polygon(bob.getBlockRectangle().getTransformedVertices());
//        Rectangle rect = bob.getBlockRectangle().getBoundingRectangle();
//        debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
//        debugRenderer.circle(bob.getBlockRectangle().getX(), bob.getBlockRectangle().getY(), 2F);
//        debugRenderer.setColor(Color.YELLOW);
//        debugRenderer.circle(bob.getRightHandPosition().x, bob.getRightHandPosition().y, 1F);
//        debugRenderer.circle(bob.getLeftHandPosition(45, bob.getWidth()).x, bob.getLeftHandPosition(45, bob.getWidth()).y, 1F);
//        debugRenderer.setColor(Color.RED);
//        debugRenderer.circle(bob.getHitCircle().x, bob.getHitCircle().y, bob.getHitCircle().radius);

        //grab circle
        Circle circle = new Circle(bob.getLeftHandPosition(0, 0.5F), 0.8F);
        debugRenderer.circle(circle.x, circle.y, circle.radius);

        for (Block[] value : bob.getView().getBlocks()) {
            for (Block o : value) {
                if (o != null) debugRenderer.polygon(o.getBounds().getTransformedVertices());
            }
        }
//        if (bob.isUseTimerOn()) {
//            debugRenderer.setColor(Color.GREEN);
//            debugRenderer.polygon(bob.getBounds().getTransformedVertices());

//            Block[][] blocks = bob.getView().getBlocks();
//            for (Block[] value : blocks) {
//                for (Object o : value) {
//                    if (o instanceof EnvironmentBlock) {
//                        EnvironmentBlock block = (EnvironmentBlock) o;
//                        if (Intersector.overlaps(bob.getHitCircle(), block.getBounds().getBoundingRectangle()) && block.getDurability() > 0) {
//                            debugRenderer.polygon(block.getBounds().getTransformedVertices());
//                        }
//                    }
//                }
//            }
//            Object[] itemsInFront = bob.whatsInFront();
//                    if (inFront == null) return;
//            for (Object inFront : itemsInFront) {
//                if (inFront instanceof EnvironmentBlock) {
//                    if (((EnvironmentBlock) inFront).getDurability() > 0) {
//                        EnvironmentBlock block = (EnvironmentBlock) inFront;
//                        debugRenderer.polygon(block.getBounds().getTransformedVertices());
//                    }
//                }
//            }
//        }

        for (AnimalSpawn animalSpawn : world.getLevel().getAnimalSpawnPoints()) {
            debugRenderer.setColor(Color.ORANGE);
            debugRenderer.polygon(animalSpawn.getBounds().getTransformedVertices());
        }

        // render Bob
//        debugRenderer.setColor(Color.BLACK);
//        debugRenderer.polygon(bob.getBounds().getVertices());
//        debugRenderer.setColor(Color.RED);
//        debugRenderer.circle(bob.getShieldCircle().x, bob.getShieldCircle().y, bob.getShieldCircle().radius);

//        rect = bob.getBounds().getBoundingRectangle();
//        debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        debugRenderer.end();
    }

//    public void setDebug() {
//        debug = !debug;
//    }

//    private void drawCollisionBlocks() {
//        debugRenderer.setProjectionMatrix(cam.combined);
//        debugRenderer.begin(ShapeType.Filled);
//        debugRenderer.setColor(Color.WHITE);
//        for (Polygon rect : world.getCollisionRects()) {
//            debugRenderer.polygon(rect.getTransformedVertices());
//        }
//        debugRenderer.end();
//    }
}
