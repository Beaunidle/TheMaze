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
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.model.AIPlayer;
import com.mygdx.game.model.Block;
import com.mygdx.game.model.BloodStain;
import com.mygdx.game.model.Bullet;
import com.mygdx.game.model.ExplodableBlock;
import com.mygdx.game.model.Explosion;
import com.mygdx.game.model.GunPad;
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
    private TextureRegion pistolTexture, smgTexture, shotgunTexture, rocketTexture;
    private TextureRegion bulletTexture;
    private TextureRegion floorTexture;

    //Animations
    private Animation walkAnimation;
    private  Animation walkInjuredAnimation;
    private  Animation explodeAnimation;

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
        playerInjured = itemAtlas.findRegion("injured-01");
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
        TextureRegion[] walkFrames = new TextureRegion[3];
        for (int i = 0; i < 3; i++) {
            walkFrames[i] = itemAtlas.findRegion("sprite-0" + (i + 1));
        }

        
        walkAnimation = new Animation(RUNNING_FRAME_DURATION, walkFrames);

        TextureRegion[] walkInjuredFrames = new TextureRegion[3];
        for (int i = 0; i < 3; i++) {
            walkInjuredFrames[i] = itemAtlas.findRegion("injured-0" + (i + 1));
        }
        walkInjuredAnimation = new Animation(RUNNING_FRAME_DURATION, walkInjuredFrames);

        TextureRegion[] explodeFrames = new TextureRegion[5];
        for (int i = 0; i < 3 ; i++) {
            explodeFrames[i] = itemAtlas.findRegion("explode-0" + (i + 1));
        }
        explodeFrames[3] = itemAtlas.findRegion("explode-02");
        explodeFrames[4] = itemAtlas.findRegion("explode-03");
        explodeAnimation = new Animation(EXPLODE_FRAME_DURATION, explodeFrames);

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
        drawGunPads();
        drawBloodStains();
        drawExplosions();
        drawBullets();
        drawAis();
        drawBob();
        spriteBatch.end();
//        drawCollisionBlocks();
        if (debug)
            drawDebug();
    }

    private void drawFloor() {
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
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
        for (int i = 0; i < bob.getLives(); i++) {
            float xPos = bob.getPosition().x  + i;
            float yPos = bob.getPosition().y + 3;
            spriteBatch.draw(heartTexture, xPos, yPos, 1, 1, 1, 1, 0.5F, 0.5F, 0);
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
        }
    }

    private void drawDebug() {
        // render blocks
        debugRenderer.setProjectionMatrix(cam.combined);
        debugRenderer.begin(ShapeType.Line);
        for (Block block : world.getDrawableBlocks((int)CAMERA_WIDTH, (int)CAMERA_HEIGHT)) {
            Rectangle rect = block.getBounds().getBoundingRectangle();
            float x1 = block.getPosition().x + rect.x;
            float y1 = block.getPosition().y + rect.y;
            debugRenderer.setColor(new Color(1, 0, 0, 1));
            debugRenderer.rect(x1, y1, rect.width, rect.height);
        }
        // render Bob
        Player bob = world.getBob();
        debugRenderer.setColor(Color.BLACK);
        debugRenderer.polygon(bob.getBounds().getTransformedVertices());
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
