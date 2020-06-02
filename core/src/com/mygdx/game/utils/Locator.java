package com.mygdx.game.utils;

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
}
