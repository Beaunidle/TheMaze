package com.mygdx.game.utils;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.environment.blocks.Block;
import com.mygdx.game.model.moveable.Player;
import com.mygdx.game.model.moveable.Sprite;

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

    public Vector2 wallInbetween(Player player, Vector2 target) {

        if (target == null) return null;
        Vector2 position = player.getCentrePosition();
        View view = player.getView();
        Block[][] blocks = view.getBlocks();
        Block[] block = view.getBlockingWall();
        block[0] = null;
        block[1] = null;
        block[2] = null;
        float dst = target.dst(position);
        Vector2 distance = new Vector2(target).sub(position);
        float angle = getAngle(distance);

        if (angle < 45 || angle >= 315) {
            //to the right
            for (int j = 8; j <= 11; j++) {
                block[0] = blocks[j][3];
                block[1] = blocks[j][4];
                block[2] = blocks[j][5];
                view.setBlockingWall(block);
                if (block[1] != null) {
                    System.out.println("Block distance: " + target.dst(block[1].getPosition()));
                    for (int i = 3; i >= 0; i--) {
                        if (blocks[j][i] == null) {
                            if (j == 8 && blocks[7][3] != null) return null;
                            return new Vector2(position.x + 3 , position.y - 1);
                        }
                    }
                    for (int i = 5; i < 8; i++) {
                        if (blocks[j][i] == null) {
                            if ( j == 8 && blocks[7][5] != null) return null;
                            return new Vector2(position.x + 3, position.y + 1);
                        }
                    }
                    if (j == 8) return null;
                } else {
                    if (block[0] != null) return new Vector2(position.x + 3 , position.y - 1);
                    else if (block[2] != null) return new Vector2(position.x + 3 , position.y + 1);
                }
            }
        } else if (angle >= 135 && angle < 225) {
            //to the left
            for (int j = 7; j >= 4; j--) {
                block[0] = blocks[j][3];
                block[1] = blocks[j][4];
                block[2] = blocks[j][5];
                view.setBlockingWall(block);
                if (block[1] != null) {
                    for (int i = 3; i >= 0; i--) {
                        if (blocks[j][i] == null) {
                            if (j == 7 && blocks[8][3] != null) return null;
                            return new Vector2(position.x - 2 , position.y - 1);
                        }
                    }
                    for (int i = 5; i < 8; i++) {
                        if (blocks[j][i] == null) {
                            if (j == 7 && blocks[8][5] != null) return null;
                            return new Vector2(position.x - 2, position.y + 1);
                        }
                    }
                    if (j == 7) return null;
                } else {
                    if (block[0] != null) return new Vector2(position.x - 3 , position.y - 1);
                    else if (block[2] != null) return new Vector2(position.x - 3 , position.y + 1);
                }
            }
        } else if (angle >= 45 && angle < 135) {
            //up
            for (int j = 5; j <= 7; j++) {
                block[0] = blocks[7][j];
                block[1] = blocks[8][j];
                block[2] = blocks[9][j];
                view.setBlockingWall(block);
                if (block[1] != null) {
                    for (int i = 3; i >= 0; i--) {
                        if (blocks[j][i] == null) return new Vector2(position.x - 1 , position.y + 3);
                    }
                    for (int i = 5; i < 8; i++) {
                        if (blocks[j][i] == null) return new Vector2(position.x + 1, position.y + 3);
                    }
                    if ( j == 7) return null;
                } else {
                    if (block[0] != null) return new Vector2(position.x + 1 , position.y + 3);
                    else if (block[2] != null) return new Vector2(position.x - 1 , position.y + 3);
                }
            }
        } else if (angle >= 225 && angle < 315) {
            //down
            for (int j = 4; j >= 2; j--) {
                block[0] = blocks[7][j];
                block[1] = blocks[8][j];
                block[2] = blocks[9][j];
                view.setBlockingWall(block);
                if (block[1] != null) {
                    for (int i = 3; i >= 0; i--) {
                        if (blocks[j][i] == null) return new Vector2(position.x - 1 , position.y - 3);
                    }
                    for (int i = 5; i < 8; i++) {
                        if (blocks[j][i] == null) return new Vector2(position.x + 1, position.y - 3);
                    }
                    if (j == 2) return null;
                } else {
                    if (block[0] != null) return new Vector2(position.x + 1 , position.y - 3);
                    else if (block[2] != null) return new Vector2(position.x - 1 , position.y - 3);
                }
            }
        }
        return target;
    }

    public boolean wallInbetweenExplosion(Sprite player, Vector2 explosion) {

        Vector2 position = player.getCentrePosition();
        View view = player.getView();
        Block[][] blocks = view.getBlocks();
//        block[0] = null;
        Block block1 = null;
//        block[2] = null;
        float dst = explosion.dst(position);
        Vector2 distance = new Vector2(explosion).sub(position);
        float angle = getAngle(distance);

        if (angle < 45 || angle >= 315) {
            //to the right
            for (int j = 8; j <= 11; j++) {

//                System.out.println("j is: " + j + ". dst is: " + dst);
//                System.out.println();
//                block[0] = blocks[j][3];
                block1 = blocks[j][4];
//                block[2] = blocks[j][5];
                if (block1 != null) {
                    return true;
                }
            }
        }
        return false;
    }
}
