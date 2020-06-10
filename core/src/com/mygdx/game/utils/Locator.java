package com.mygdx.game.utils;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.Block;

public class Locator {

    public int locate(float deg, float rotation) {
        if (deg == rotation) {
            return 0;
        } else if (deg >= 90 && deg <= 270) {
            if (rotation >= 90 && rotation <= 270) {
                if (deg > rotation) {
                    //anti-clockwise
                    return -1;
                } else {
                    //clockwise
                    return 1;
                }
            } else {
                if (rotation < 90) {
                    if (deg - rotation <= 180) {
                        //anti-clockwise
                        return -1;
                    } else {
                        //clockwise
                        return 1;
                    }
                } else {
                    if (rotation - deg <= 180) {
                        //clockwise
                        return 1;
                    } else {
                        //anti-clockwise
                        return -1;
                    }
                }
            }
        } else if (deg < 90) {
            if (rotation <= 180) {
                if (deg > rotation) {
                    //anti-clockwise
                    return -1;
                } else {
                    //clockwise
                    return 1;
                }
            } else if (rotation >= 270) {
                //anti-clockwise
                return -1;
            } else {
                if (rotation - deg <= 180) {
                    //clockwise
                    return 1;
                } else {
                    //anti-clockwise
                    return -1;
                }
            }
        } else {
            if (rotation >= 180) {
                if (deg > rotation) {
                    //anti-clockwise
                    return -1;
                } else {
                    //clockwise
                    return 1;
                }
            } else if (rotation <= 90) {
                //clockwise
                return 1;
            } else {
                if (deg - rotation >= 180) {
                    //clockwise
                    return 1;
                } else {
                    //anti-clockwise
                    return -1;

                }
            }
        }
    }

    public float getAngle(Vector2 distance) {
        double rot = Math.atan2(distance.y, distance.x);
        float deg = (float) (rot * (180 / Math.PI));
        if (deg < 0) {
            deg = 360 - (-deg);
        }
        return  deg;
    }

    public Vector2 wallInbetween(Vector2 position, Vector2 target, Block[][] blocks) {

        float dst = target.dst(position);
        Vector2 distance = new Vector2(target).sub(position);
        float angle = getAngle(distance);
        Block block1 = null;
        Block block2 = null;
        Block block3 = null;
        if (angle < 45 || angle > 315) {
            System.out.println("I see you right");
            for (int j = 8; j <= 10; j++) {
                block1 = blocks[j][3];
                block2 = blocks[j][4];
                block3 = blocks[j][5];
                if (block2 != null || (block1 != null || block3 != null)) {
                    for (int i = 3; i >= 0; i--) {
                        if (blocks[j][i] == null) {
                            return new Vector2(position.x + 3 , position.y - 4);
                        }
                    }
                    for (int i = 5; i < 8; i++) {
                        if (blocks[j][i] == null) {
                            return new Vector2(position.x + 3, position.y + 4);
                        }
                    }
                }
            }
            return null;
        } else if (angle > 135 && angle < 225) {
            System.out.println("I see you left");
            for (int j = 7; j >= 5; j--) {
                block1 = blocks[j][3];
                block2 = blocks[j][4];
                block3 = blocks[j][5];
                if (block2 != null || (block1 != null || block3 != null)) {
                    for (int i = 3; i >= 0; i--) {
                        if (blocks[j][i] == null) {
                            return new Vector2(position.x - 2 , position.y - 4);
                        }
                    }
                    for (int i = 5; i < 8; i++) {
                        if (blocks[j][i] == null) {
                            return new Vector2(position.x - 2, position.y + 3);
                        }
                    }
                }
            }
            return null;
        }
        return target;
    }
}
