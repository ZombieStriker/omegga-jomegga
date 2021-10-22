package com.kmschr.brs;

/**
 * Simple 3D vector, used to represent size and position. Default is (0,0,0)
 */
public class Vec3 {
    public int x;
    public int y;
    public int z;

    public Vec3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public boolean tooBig() {
        return Math.abs(x) > 30_000_000 || Math.abs(y) > 30_000_000 || Math.abs(z) > 30_000_000;
    }

    public Vec3() {
        this(0, 0, 0);
    }

    /**
     * Check if vector is at the origin
     * @return true if vector is at the origin
     */
    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    /**
     * string representation of vector, as "(x,y,z)"
     * @return the vector as a string
     */
    public String toString() {
        return "(" + x + "," + y + "," + z + ")";
    }
}
