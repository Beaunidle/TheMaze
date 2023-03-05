package com.mygdx.game.model.moveable;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.model.items.Material;
import com.mygdx.game.model.moveable.AIPlayer;
import com.mygdx.game.model.moveable.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Projectile extends Material {

    public enum ProjectileType {
        PEBBLE,SPEAR,ARROW,BULLET,FIREBALL,LIGHNINGBOLT
    }

    private final Vector2 position;
    private final Polygon bounds;
    private float speed;
    private float momentum;
    private float stateTime = 0;
    private final float height;
    private final float width;
    private float rotation;
    private final float damage;
    private Vector2 velocity;
    private Timer.Task explodeTimer;
    private boolean exploding = false;
    private final float explodeTime = 0.5f;
    private final String playerName;
    private boolean explosive = false;
    private final float rotationSpeed;
    private final ProjectileType projectileType;
    private boolean homing = false;
    private boolean activated;
    private final Circle viewCircle;
    private Timer.Task homingTimer;
    private Sprite target;

    public Projectile(Vector2 position, float rotation, String playerName, float damage, ProjectileType type, float addedMomentum, boolean poweredUp) {
        this.projectileType = type;
        switch (type) {
            case PEBBLE:
                this.width = 0.4F;
                this.height = 0.4f;
                this.damage = 1 + (poweredUp ? damage*3 : damage);
                speed = 5F;
                momentum = 300 + (poweredUp ? 250 : 10);
                rotationSpeed = 25;
                explosive = false;
                setName("pebble");
                break;
            case SPEAR:
                this.width = 2F;
                this.height = 0.25f;
                this.damage = 2 + (poweredUp ? damage*2 : damage);
                speed = 5F;
                momentum = 400 + (poweredUp ? 250 : 100);
                rotationSpeed = 80;
                setName("spear");
                break;
            case ARROW:
                this.width = 0.04F;
                this.height = 1f;
                this.damage = 3 + damage;
                speed = 4F;
                momentum = 1000;
                rotationSpeed = 150;
                setName("arrow");
                break;
            case FIREBALL:
                this.width = poweredUp ? 1F : 0.5F;
                this.height = poweredUp ? 1F : 0.5F;
                this.damage = 5 + (poweredUp ? damage*2 : damage);
                speed = poweredUp ? 3.5F : 5F;
                momentum = 1000;
                rotationSpeed = 25;
                explosive = false;
                setName("fireball");
                break;
            case BULLET:
                this.width = 0.04F;
                this.height = 0.15f;
                this.damage = damage + 5;
                speed = 5.5F;
                momentum = 1000000;
                rotationSpeed = 250;
                setName("bullet");
                break;
            default:
                this.width = 0.04F;
                this.height = 0.15f;
                this.damage = damage;
                speed = 1F;
                momentum = 1;
                rotationSpeed = 1;
                setName(null);
        }
        if (isHoming()) {
            homingTimer =  new Timer.Task() {
                @Override
                public void run() {
                    activated = true;
                    homingTimer.cancel();
                }
            };
            Timer.schedule(homingTimer, 1);
        }
        viewCircle = new Circle(position.x, position.y, 8.5F);
        bounds = new Polygon(new float[]{0, 0, width, 0, width, height, 0, height});
        bounds.setOrigin(0, 0);
        bounds.setRotation(rotation);
        bounds.setPosition(position.x, position.y);
        this.position = position;
        this.rotation = rotation;
        this.playerName = playerName;
    }

    public ProjectileType getProjectileType() {
        return projectileType;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public float getRotation() {
        return rotation;
    }

    public Polygon getBounds() {
        return bounds;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getDamage() {
        return damage;
    }

    public float getStateTime() {
        return stateTime;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public float getExplodeTime() {
        return explodeTime;
    }

    public String getPlayerName() {
        return playerName;
    }

    public float getMomentum() {
        return momentum;
    }

    public void setMomentum(float momentum) {
        this.momentum = momentum;
    }

    public boolean isExploding() {
        return exploding;
    }

    public boolean isExplosive() {
        return explosive;
    }

    public void setExplosive() {
        this.explosive = true;
    }

    public void update(float delta) {
//		position.add(velocity.x * delta, velocity.y * delta);
        this.bounds.setPosition(position.x, position.y);
        stateTime += delta;
    }

    public void startExplodeTimer() {
        exploding = true;
        explodeTimer =  new Timer.Task() {
            @Override
            public void run() {
                stopExplodeTimer();
            }
        };
        Timer.schedule(explodeTimer, explodeTime, 0);
    }

    private void stopExplodeTimer() {
        exploding = false;
        explodeTimer.cancel();
    }

    public void rotateAntiClockwise(float delta) {
        rotation = rotation + (rotationSpeed * delta);
        if (getRotation() > 360) {
            rotation = rotation - 360;
        }
    }

    public void rotateClockwise(float delta){
        rotation = rotation - (rotationSpeed * delta);
        if (getRotation() < 0) {
            rotation = rotation + 360;
        }
    }

    public boolean isHoming() {
        return homing;
    }

    public boolean isActivated() {
        return activated;
    }

    public Sprite getTarget() {
        return target;
    }

    public Circle getViewCircle() {
        return viewCircle;
    }

    public void chooseTarget(Player player, Collection<? extends Sprite> sprites) {

        List<Sprite> targetSprites = new ArrayList<>();
        if (!player.getName().equals(getPlayerName()) && getViewCircle().contains(player.getCentrePosition())) targetSprites.add(player);

        for (Sprite sprite : sprites) {
            if (!sprite.getName().equals(getPlayerName())) {
                if (getViewCircle().contains(sprite.getCentrePosition()))targetSprites.add(sprite);
            }
        }
        if (targetSprites.isEmpty()) {
            target = null;
        } else {
            Random rand = new Random();
            if (target != null && rand.nextInt(1000) < 999) return;

            target =  targetSprites.get(rand.nextInt(targetSprites.size()));
        }
    }
}
