package com.kmschr.brs.enums;

/**
 * How much a brick is rotated along the axis it is facing
 */
public enum Rotation {
    /**
     * No rotation (0 Degrees)
     */
    Deg0,

    /**
     * Quarter turn rotation (90 Degrees)
     */
    Deg90,

    /**
     * Half turn rotation (180 Degrees)
     */
    Deg180,

    /**
     * Three quarters turn rotation (270 Degrees)
     */
    Deg270;

    private static final Rotation[] rotations = Rotation.values();

    /**
     * Extract the rotation from an orientation byte
     * @param orientation the bricks orientation
     * @return the extracted rotation
     */
    public static Rotation getRotation(byte orientation) {
        return rotations[orientation & 0x03];
    }
}
