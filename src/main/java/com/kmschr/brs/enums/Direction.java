package com.kmschr.brs.enums;

/**
 * The direction that a brick is facing
 */
public enum Direction {
    /**
     * Facing North
     */
    XPositive,

    /**
     * Facing South
     */
    XNegative,

    /**
     * Facing East
     */
    YPositive,

    /**
     * Facing West
     */
    YNegative,

    /**
     * Facing the sky (default)
     */
    ZPositive,

    /**
     * Facing the ground
     */
    ZNegative;

    private static final Direction[] directions = Direction.values();

    /**
     * Extract the direction from an orientation byte
     * @param orientation the bricks orientation
     * @return the extracted direction
     */
    public static Direction getDirection(byte orientation) {
        return directions[(orientation >>> 2) % 6];
    }

    /**
     * Flip this direction, from negative to positive and vise versa
     * @return the flipped direction
     */
    public Direction flip() {
        return directions[this.ordinal() ^ 1];
    }
}
