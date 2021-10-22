package com.kmschr.brs.enums;

public enum ScreenshotFormat {
    None,
    Png;

    public static ScreenshotFormat getFormat(byte f) {
        ScreenshotFormat format = null;
        switch (f) {
            case (byte) 0:
                format = None;
                break;
            case (byte) 1:
                format = Png;
                break;
        }
        return format;
    }
}
