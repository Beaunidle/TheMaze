package com.mygdx.game;




import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.AIPlayer;
import com.mygdx.game.model.Block;
import com.mygdx.game.model.BloodStain;
import com.mygdx.game.model.BoostPad;
import com.mygdx.game.model.Bullet;
import com.mygdx.game.model.ExplodableBlock;
import com.mygdx.game.model.Explosion;
import com.mygdx.game.model.FloorPad;
import com.mygdx.game.model.GunPad;
import com.mygdx.game.model.Pad;
import com.mygdx.game.model.Player;
import com.mygdx.game.model.World;


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
    private TextureRegion floorTexture;

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
    private boolean debug;

//    public void setSize (int w, int h) {
//         pixels per unit on the X axis
//        float ppuX = (float) w / CAMERA_WIDTH;
//         pixels per unit on the Y axis
//        float ppuY = (float) h / CAMERA_HEIGHT;
//    }

    public WorldRenderer(World world, SpriteBatch spriteBatch, boolean debug) {
        this.world = world;
        this.cam = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
        this.cam.position.set(world.getBob().getPosition().x, world.getBob().getPosition().y + 0.5F, 0);
//        this.cam.position.set(CAMERA_WIDTH/2, CAMERA_HEIGHT/2, 0);
        this.cam.update();
        this.debug = debug;
        this.spriteBatch = spriteBatch;
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
        spriteBatch.end();
//        drawCollisionBlocks();
//        debugRenderer.setProjectionMatrix(cam.combined);
//        debugRenderer.setAutoShapeType(true);
//        debugRenderer.begin(ShapeType.Line);
//        debugRenderer.setColor(Color.RED);
//        debugRenderer.polygon(world.getBob().getViewCircle().getTransformedVertices());
//        for (AIPlayer aiPlayer :  world.getAIPlayers()) {
//            Rectangle rect = aiPlayer.getViewCircle().getBoundingRectangle();
//            debugRenderer.rect(rect.x, rect.y, rect.width, rect.height);
//        }
//        debugRenderer.end();
//        if (debug)
        drawDebug();
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
                spriteBatch.draw(explosionFrame, explosion.getPosition().x, explosion.getPosition().y, Explosion.getSIZE() / 2, Explosion.getSIZE() / 2,
                        Explosion.getSIZE(), Explosion.getSIZE(), 1F, 1F, 0, true);
            }
        }
    }

    private void drawBullets() {
        for (Bullet bullet : world.getBullets()) {
            TextureRegion bulletFrame = bulletTexture;
            if (bullet.isExploding()) {
                bulletFrame = (TextureRegion) (explodeAnimation.getKeyFrame(bullet.getStateTime(), true));
                spriteBatch.draw(bulletFrame, bullet.getPosition().x, bullet.getPosition().y, bullet.getWidth()/2, bullet.getHeight()/2,
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

        if(bob.getState().equals(Player.State.MOVING)) {
            if (bob.isInjured()) {
                bobFrame =  (TextureRegion) (walkInjuredAnimation.getKeyFrame(bob.getStateTime(), true));
            } else {
                bobFrame =  (TextureRegion) (walkAnimation.getKeyFrame(bob.getStateTime(), true));
            }
        }
        spriteBatch.draw(bobFrame, bob.getPosition().x, bob.getPosition().y, Player.WIDTH/2, Player.HEIGHT/2, Player.WIDTH, Player.HEIGHT,
        1, 1, bob.getRotation(), true);
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

    private void drawDebug() {
        debugRenderer.setProjectionMatrix(cam.combined);
        debugRenderer.setAutoShapeType(true);
        debugRenderer.begin(ShapeType.Line);
        debugRenderer.setColor(Color.RED);
        debugRenderer.polygon(world.getBob().getViewCircle().getTransformedVertices());
//        debugRenderer.circle(world.getBob().getViewCircle().getX(), world.getBob().getViewCircle().y, world.getBob().getViewCircle().radius);
        for (AIPlayer aiPlayer :  world.getAIPlayers()) {
            debugRenderer.polygon(aiPlayer.getViewCircle().getTransformedVertices());
//            debugRenderer.circle(aiPlayer.getViewCircle().getX(), aiPlayer.getViewCircle().y, aiPlayer.getViewCircle().radius);
        }

        for (Bullet bullet : world.getBullets()) {

            if (bullet.isHoming()) {
                debugRenderer.circle(bullet.getViewCircle().x, bullet.getViewCircle().y, bullet.getViewCircle().radius);
            }
        }

        // render Bob
        Player bob = world.getBob();
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
