package com.kmschr.brs;

/**
 * One of the possible representations of a brick's color in the Brickadia save format. A ColorMode is an index into
 * the list of colors in the save's metadata.
 */
public class ColorMode implements Cloneable {

    protected int color;

    public static final int WHITE = 0;
    public static final int LIGHT_GRAY = 2;
    public static final int DARK_GRAY = 5;
    public static final int BLACK = 7;
    public static final int DARK_RED = 8;
    public static final int RED = 9;
    public static final int ORANGE = 10;
    public static final int YELLOW = 11;
    public static final int GREEN = 12;
    public static final int BLUE = 13;
    public static final int PINK = 14;
    public static final int DARK_PINK = 15;
    public static final int BROWN = 16;
    public static final int FOREST_GREEN = 24;
    public static final int DARK_BLUE = 39;

    public ColorMode(int value) {
        this.color = value;
    }

    public static ColorMode randomDefault() {
        return new ColorMode(Utils.rand.nextInt(55));
    }

    public int getValue() {
        return color;
    }

    public String toString() {
        return "index " + color;
    }

    public ColorMode clone() {
        if (this instanceof Color)
            return new Color(this.getValue());
        else
            return new ColorMode(this.getValue());
    }

}
