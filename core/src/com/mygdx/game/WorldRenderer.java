package com.mygdx.game;




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
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.model.AIPlayer;
import com.mygdx.game.model.Block;
import com.mygdx.game.model.BloodStain;
import com.mygdx.game.model.BoostPad;
import com.mygdx.game.model.Bullet;
import com.mygdx.game.model.ExplodableBlock;
import com.mygdx.game.model.Explosion;
import com.mygdx.game.model.FloorPad;
import com.mygdx.game.model.GameButton;
import com.mygdx.game.model.GunPad;
import com.mygdx.game.model.Pad;
import com.mygdx.game.model.Player;
import com.mygdx.game.model.World;
import com.mygdx.game.utils.JoyStick;

import java.util.Arrays;


public class WorldRenderer {

    private static final float RUNNING_FRAME_DURATION = 0.12f;
    private static final float EXPLODE_FRAME_DURATION = 0.12f;

    //Textures
    private TextureRegion playerIdle;
    private TextureRegion playerInjured;
    private TextureRegion playerDead;
    private TextureRegion heartTexture;
    private  TextureRegion blockTexture;
    private TextureRegion blockExplodeRed, blockExplodeYellow, blockRubble;
    private TextureRegion pistolTexture, smgTexture, shotgunTexture, rocketTexture, boostPadTexture;
    private TextureRegion bulletTexture, homingBoostTexture, speedBoostTexture, shieldBoostTexture, damageBoostTexture;
    private TextureRegion spikeTexture, slimeTexture, moveTexture;
    private TextureRegion joystickOuterTexture, joystickInnerTexture;
    private TextureRegion floorTexture;
    private TextureRegion fireButtonTexture, useButtonTexture;
    private TextureRegion reloadingTexture;

    //Animations
    private Animation walkAnimation;
    private  Animation walkInjuredAnimation;
    private  Animation explodeAnimation;
    private Animation shieldAnimation;

    private static final float CAMERA_WIDTH = 14f;
    private static final float CAMERA_HEIGHT = 8f;

    private World world;
    private OrthographicCamera cam;

    /** for debug rendering **/
    private ShapeRenderer debugRenderer = new ShapeRenderer();

    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private boolean debug;

//    public void setSize (int w, int h) {
//         pixels per unit on the X axis
//        float ppuX = (float) w / CAMERA_WIDTH;
//         pixels per unit on the Y axis
//        float ppuY = (float) h / CAMERA_HEIGHT;
//    }

    public WorldRenderer(World world, SpriteBatch spriteBatch, boolean debug, BitmapFont font) {
        this.world = world;
        this.cam = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
        this.cam.position.set(world.getBob().getPosition().x, world.getBob().getPosition().y + 0.5F, 0);
//        this.cam.position.set(CAMERA_WIDTH/2, CAMERA_HEIGHT/2, 0);
        this.cam.update();
        this.debug = debug;
        this.spriteBatch = spriteBatch;
        this.font = font;
        loadTextures();
    }

    private void loadTextures() {

        TextureAtlas itemAtlas = new TextureAtlas(Gdx.files.internal("items.atlas"));
        playerIdle = itemAtlas.findRegion("sprite-01");
        playerInjured = itemAtlas.findRegion("sprite-01");
        playerDead = itemAtlas.findRegion("dead");
        heartTexture = itemAtlas.findRegion("heart");

        blockTexture = itemAtlas.findRegion("block");
        floorTexture = itemAtlas.findRegion("floor");
        blockExplodeRed = itemAtlas.findRegion("explodingBlockRed");
        blockExplodeYellow = itemAtlas.findRegion("explodingBlockYellow");
        blockRubble = itemAtlas.findRegion("rubbleBlock");
        pistolTexture = itemAtlas.findRegion("gunPistol");
        smgTexture = itemAtlas.findRegion("gunSMG");
        shotgunTexture = itemAtlas.findRegion("gunShotgun");
        rocketTexture = itemAtlas.findRegion("gunRocket");
        bulletTexture = itemAtlas.findRegion("bullet90");
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

        TextureRegion[] shieldFrames = new TextureRegion[7];
        shieldFrames[0] = itemAtlas.findRegion("shieldBlue");
        shieldFrames[1] = itemAtlas.findRegion("shieldPurple");
        shieldFrames[2] = itemAtlas.findRegion("shieldRed");
        shieldFrames[3] = itemAtlas.findRegion("shieldOrange");
        shieldFrames[4] = itemAtlas.findRegion("shieldYellow");
        shieldFrames[5] = itemAtlas.findRegion("shieldGreen");
        shieldFrames[6] = itemAtlas.findRegion("shieldTurquoise");

        shieldAnimation = new Animation(RUNNING_FRAME_DURATION, shieldFrames);
//        TextureRegion[] walkLeftFrames = new TextureRegion[6];
//
//        for (int i = 0; i < 6; i++) {
//            walkLeftFrames[i] = new TextureRegion(walkRightFrames[i]);
//            walkLeftFrames[i].flip(true, false);
//        }
//        walkLeftAnimation = new Animation(RUNNING_FRAME_DURATION, walkLeftFrames);

        TextureAtlas buttonAtlas = new TextureAtlas(Gdx.files.internal("buttons/buttons.atlas"));
        fireButtonTexture = buttonAtlas.findRegion("start");
        useButtonTexture = buttonAtlas.findRegion("exit");
    }

    public void render() {
        spriteBatch.setProjectionMatrix(cam.combined);
        this.cam.position.set(world.getBob().getPosition().x, world.getBob().getPosition().y + 0.5F, 0);
        this.cam.update();
        spriteBatch.begin();
        drawFloor();
        drawBlocks();
        drawBoostPads();
        drawGunPads();
        drawFloorPads();
        drawBloodStains();
        drawExplosions();
        drawBullets();
        drawAis();
        drawBob();
        if (world.getMoveJoystick() != null) drawJoystick(world.getMoveJoystick());
        if (world.getFireJoystick() != null) drawJoystick(world.getFireJoystick());
        drawButtons();
        spriteBatch.end();
//        aiDebug();
//        drawCollisionBlocks();
//        if (debug)
//        drawDebug();
        debugRenderer.setProjectionMatrix(cam.combined);
        debugRenderer.setAutoShapeType(true);
        debugRenderer.begin(ShapeType.Line);
        debugRenderer.setColor(Color.RED);
        for (Explosion explosion : world.getExplosions()) {
            debugRenderer.circle(explosion.getBounds().x, explosion.getBounds().y, explosion.getBounds().radius);
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
        debugRenderer.begin(ShapeType.Line);
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
            for (int i = 0; i < blocking.length; i++) {
                if (blocking[i] != null) debugRenderer.polygon(blocking[i].getBounds().getTransformedVertices());
            }

            debugRenderer.setColor(Color.BLUE);
            if (aiPlayer.getTarget() != null) debugRenderer.circle(aiPlayer.getTarget().x, aiPlayer.getTarget().y, 2);
//            debugRenderer.setColor(Color.GREEN);
//            debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        }
        debugRenderer.end();

    }
    private void drawFloor() {
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 70; j++) {
                Block block = world.getLevel().get(i, j);
                if (block == null) {
                    spriteBatch.draw(floorTexture, i, j, Block.getSIZE(), Block.getSIZE());
                }
            }
        }
    }
    private void drawBlocks() {
        for (Block block : world.getDrawableBlocks((int)CAMERA_WIDTH, (int)CAMERA_HEIGHT)) {
            if (block instanceof ExplodableBlock) {
                if (((ExplodableBlock) block).getState().equals(ExplodableBlock.State.RUBBLE)) {
                    spriteBatch.draw(blockRubble, block.getPosition().x, block.getPosition().y, Block.getSIZE(), Block.getSIZE());
                } else {
                    spriteBatch.draw(((ExplodableBlock) block).getState().equals(ExplodableBlock.State.RED) ? blockExplodeRed : blockExplodeYellow, block.getPosition().x, block.getPosition().y, Block.getSIZE(), Block.getSIZE());
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
                    floorFrame = slimeTexture;
                    break;
                case MOVE:
                    floorFrame = moveTexture;
                    break;
            }
            if (floorFrame != null) {
                if (floorPad.getType().equals(FloorPad.Type.MOVE)) {
                    spriteBatch.draw(floorFrame, floorPad.getPos().x, floorPad.getPos().y, Block.getSIZE()/2, Block.getSIZE()/2,
                            Block.getSIZE(), Block.getSIZE(), 1, 1, floorPad.getRot() + 90, true);
                } else {
                    spriteBatch.draw(floorFrame, floorPad.getPos().x, floorPad.getPos().y, GunPad.getSIZE(), GunPad.getSIZE());
                }
            }
        }
    }

    private void drawBloodStains() {
        for (BloodStain bloodStain : world.getBloodStains()) {
                TextureRegion stainFrame = playerDead;
                spriteBatch.draw(stainFrame, bloodStain.getPosition().x, bloodStain.getPosition().y, BloodStain.getWIDTH(), BloodStain.getHEIGHT());
        }
    }
    private void drawExplosions() {
        for (Explosion explosion : world.getExplosions()) {
            if (!explosion.isFinished()) {
                TextureRegion explosionFrame = (TextureRegion) explodeAnimation.getKeyFrame(world.getBob().getStateTime(), true);
                spriteBatch.draw(explosionFrame, explosion.getPosition().x - Explosion.getSIZE()/2, explosion.getPosition().y - Explosion.getSIZE()/2, Explosion.getSIZE() / 2, Explosion.getSIZE() / 2,
                        Explosion.getSIZE(), Explosion.getSIZE(), 1F, 1F, 0, true);
            }
        }
    }

    private void drawBullets() {
        for (Bullet bullet : world.getBullets()) {
            TextureRegion bulletFrame = bulletTexture;
            if (bullet.isExploding()) {
                bulletFrame = (TextureRegion) (explodeAnimation.getKeyFrame(bullet.getStateTime(), true));
                spriteBatch.draw(bulletFrame, bullet.getPosition().x, bullet.getPosition().y, 0, bullet.getHeight()/2,
                        bullet.getWidth(), bullet.getHeight(), 2.5F, 5, bullet.getRotation(), true);
            } else {
                spriteBatch.draw(bulletFrame, bullet.getPosition().x, bullet.getPosition().y, bullet.getWidth()/2, bullet.getHeight()/2,
                        bullet.getWidth(), bullet.getHeight(), 1, 1, bullet.getRotation(), true);
            }
        }
    }

    private void drawBob() {
        Player bob = world.getBob();
        TextureRegion bobFrame = bob.isInjured() ? playerInjured : playerIdle;
        for (float i = 0; i < bob.getLives(); i++) {
            float xPos = 5 + bob.getPosition().x - (i/4);
            float yPos = bob.getPosition().y + 3;
            spriteBatch.draw(heartTexture, xPos, yPos, 1, 1, 1, 1, 0.25F, 0.25F, 0);
        }
        for (float i = 0; i < bob.getGun().getAmmo(); i++) {
            float xPos = 5 + bob.getPosition().x - (i/4);
            float yPos = bob.getPosition().y + 2.5F;
            spriteBatch.draw(bulletTexture, xPos, yPos, 1, 1, 0.5F, 1, 0.25F, 0.25F, 0);
        }

        for (float i = 0; i < bob.getGun().getClips(); i++) {
            float xPos = 5 + bob.getPosition().x - (i/4);
            float yPos = bob.getPosition().y + 2.0F;
            spriteBatch.draw(blockRubble, xPos, yPos, 1, 1, 0.5F, 1, 0.25F, 0.25F, 0);
        }

        TextureRegion gunFrame = null;
        switch (bob.getGun().getType()) {
            case PISTOL:
                gunFrame = pistolTexture;
                break;
            case SHOTGUN:
                gunFrame = shotgunTexture;
                break;
            case SMG:
                gunFrame = smgTexture;
                break;
            case ROCKET:
                gunFrame = rocketTexture;
                break;
        }

        float xPos = bob.getPosition().x + 5;
        float yPos = bob.getPosition().y + 1.75F;
        if (gunFrame != null) {
            spriteBatch.draw(gunFrame, xPos, yPos, 1, 1, 1F, 1, 0.75F, 0.75F, 0);
        }


        if (!bob.getBoost().equals(Player.Boost.NOTHING)) {
            TextureRegion boostFrame = null;

            switch (bob.getBoost()) {
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
                    TextureRegion shieldFrame = (TextureRegion) (shieldAnimation.getKeyFrame(bob.getStateTime(), true));
                    Circle circle = bob.getShieldCircle();
                    spriteBatch.draw(shieldFrame, circle.x - circle.radius/2, circle.y - circle.radius/2, 1, 1, 2F, 2, 2.00F, 2.00F, 0);
                    break;
            }

            xPos = bob.getPosition().x + 4.5F;
            if (boostFrame != null) {
                spriteBatch.draw(boostFrame, xPos, yPos, 1, 1, 1F, 1, 0.75F, 0.75F, 0);
            }

        }

        if (bob.getGun().isReloading()) {
            float x = world.getBob().getPosition().x - (CAMERA_WIDTH/2) + 3;
            float y = world.getBob().getPosition().y + (CAMERA_HEIGHT/2) - 1.5F;
            spriteBatch.draw(reloadingTexture, x, y, 1, 1, 6.0F, 2.0F, 1.00F, 1.00F, 0);
        }

        if(bob.getState().equals(Player.State.MOVING)) {
            if (bob.isInjured()) {
                bobFrame =  (TextureRegion) (walkInjuredAnimation.getKeyFrame(bob.getStateTime(), true));
            } else {
                bobFrame =  (TextureRegion) (walkAnimation.getKeyFrame(bob.getStateTime(), true));
            }
        }
        Float drawAngle = bob.getRotation();
        if (world.getFireJoystick() != null && world.getFireJoystick().getDrag() != null) drawAngle = world.getFireJoystick().getAngle();
        spriteBatch.draw(bobFrame, bob.getPosition().x, bob.getPosition().y, Player.WIDTH/2, Player.HEIGHT/2, Player.WIDTH, Player.HEIGHT,
        1, 1, drawAngle, true);
        //todo make injured sprites
    }

    private void drawAis() {
        for (AIPlayer aiPlayer : world.getAIPlayers()) {
            TextureRegion aiFrame = aiPlayer.isInjured() ? playerInjured : playerIdle;
            if(aiPlayer.getState().equals(Player.State.MOVING)) {
                if (aiPlayer.isInjured()) {
                    aiFrame =  (TextureRegion) (walkInjuredAnimation.getKeyFrame(aiPlayer.getStateTime(), true));
                } else {
                    aiFrame =  (TextureRegion) (walkAnimation.getKeyFrame(aiPlayer.getStateTime(), true));
                }
            }
            spriteBatch.draw(aiFrame, aiPlayer.getPosition().x, aiPlayer.getPosition().y, Player.WIDTH/2, Player.HEIGHT/2, Player.WIDTH, Player.HEIGHT,
                    1, 1, aiPlayer.getRotation(), true);

            if (!aiPlayer.getBoost().equals(Player.Boost.NOTHING)) {

                if (aiPlayer.getBoost() == Player.Boost.SHIELD) {
                    TextureRegion shieldFrame = (TextureRegion) (shieldAnimation.getKeyFrame(aiPlayer.getStateTime(), true));
                    Circle circle = aiPlayer.getShieldCircle();
                    spriteBatch.draw(shieldFrame, circle.x - circle.radius / 2, circle.y - circle.radius / 2, 1, 1, 2F, 2, 2.00F, 2.00F, 0);
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
        spriteBatch.draw(joystickOuterTexture, drawOuter.x - JoyStick.getWIDTH() / 2, drawOuter.y - JoyStick.getWIDTH() / 2, 1, 1, 2F, 2, 1.00F, 1.00F, 0);
        if (joyStick.getDrag() == null) {
            spriteBatch.draw(joystickInnerTexture, drawOuter.x - JoyStick.getWIDTH() / 2, drawOuter.y - JoyStick.getWIDTH() / 2, 1, 1, 2.0F, 2.0F, 1.00F, 1.00F, 0);
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

            spriteBatch.draw(joystickInnerTexture, drawInner.x - JoyStick.getWIDTH() / 2, drawInner.y - JoyStick.getWIDTH() / 2, 1, 1, 2.0F, 2.0F, 1.00F, 1.00F, 0);
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
//            spriteBatch.end();
//            debugRenderer.setProjectionMatrix(cam.combined);
//            debugRenderer.setAutoShapeType(true);
//            debugRenderer.begin(ShapeType.Line);
//            debugRenderer.setColor(Color.GREEN);
//            debugRenderer.circle(xish + gameButton.getArea().radius, yish + gameButton.getArea().radius, gameButton.getArea().radius);
//            debugRenderer.end();
//            spriteBatch.begin();
        }
    }
    private void drawDebug() {
        debugRenderer.setProjectionMatrix(cam.combined);
        debugRenderer.setAutoShapeType(true);
        debugRenderer.begin(ShapeType.Line);
        debugRenderer.setColor(Color.RED);
        Player bob = world.getBob();
        debugRenderer.polygon(bob.getViewCircle().getTransformedVertices());
//        debugRenderer.circle(world.getBob().getViewCircle().getX(), world.getBob().getViewCircle().y, world.getBob().getViewCircle().radius);
        for (AIPlayer aiPlayer :  world.getAIPlayers()) {
            debugRenderer.polygon(aiPlayer.getViewCircle().getTransformedVertices());
//            debugRenderer.circle(aiPlayer.getViewCircle().getX(), aiPlayer.getViewCircle().y, aiPlayer.getViewCircle().radius);
        }
        debugRenderer.setColor(Color.RED);
        for (Bullet bullet : world.getBullets()) {
//             debugRenderer.circle(bullet.getViewCircle().x, bullet.getViewCircle().y, bullet.getViewCircle().radius);
            debugRenderer.polygon(bullet.getBounds().getTransformedVertices());
        }

        // render Bob
        debugRenderer.setColor(Color.BLACK);
        debugRenderer.polygon(bob.getBounds().getTransformedVertices());
        debugRenderer.setColor(Color.RED);
        debugRenderer.circle(bob.getShieldCircle().x, bob.getShieldCircle().y, bob.getShieldCircle().radius);

        Rectangle rect = bob.getBounds().getBoundingRectangle();
        debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
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
