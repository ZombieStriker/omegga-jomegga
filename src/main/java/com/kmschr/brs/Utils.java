package com.kmschr.brs;

import com.kmschr.brs.enums.Direction;
import com.kmschr.brs.enums.Rotation;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;

public class Utils {

    protected static final Random rand = new Random();

    public static ZonedDateTime ue4DateTimeBase() {
        return ZonedDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneId.of("Z"));
    }

    public static byte combineOrientation(Direction direction, Rotation rotation) {
        return (byte) (((byte) direction.ordinal()) << 2 | (byte) rotation.ordinal());
    }

}
