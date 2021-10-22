package com.kmschr.brs;

public class Color extends ColorMode {

    public Color(int color) {
        super(color);
    }

    public Color(int r, int g, int b, int a) {
        this((byte) convert(r), (byte) convert(g), (byte) convert(b), (byte) convert(a));
    }

    public Color(byte r, byte g, byte b, byte a) {
         super((b & 0xFF) | ((g & 0xFF) << 8) | ((r & 0xFF) << 16) | ((a & 0xFF) << 24));
    }

    public Color(byte[] readNBytes) {
        super((readNBytes[0]&0xFF)|(((readNBytes[1]&0xFF) << 8))| ((readNBytes[2]&0xFF)<<16));
    }

    public byte r() {
        return (byte) ((color >>> 16) & 0xFF);
    }

    public byte g() {
        return (byte) ((color >>> 8) & 0xFF);
    }

    public byte b() {
        return (byte) (color & 0xFF);
    }

    public byte a() {
        return (byte) ((color >>> 24) & 0xFF);
    }

    public void multiply(int factor) {
        int r = r();
        int g = g();
        int b = b();
        int a = a();
        r = (int) ((double) r * ((double) factor / 255.0));
        g = (int) ((double) g * ((double) factor / 255.0));
        b = (int) ((double) b * ((double) factor / 255.0));
        this.color = (b & 0xFF) | ((g & 0xFF) << 8) | ((r & 0xFF) << 16) | ((a & 0xFF) << 24);
    }

    @Override
    public String toString() {
        return String.format("#%02x%02x%02x%02x", r(), g(), b(), a());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Color))
            return false;
        return this.color == ((Color) o).color;
    }

    private static int convert(int n) {
        double u = ((double) n) / 255.0;
        if (u <= 0.04045)
            return (int) (((u * 25) / 323) * 255.0);
        double p = (u + 0.055) / 1.055;
        return (int) (Math.pow(p, 2.4) * 255.0);
    }

}
