package com.kmschr.brs.io;

import com.kmschr.brs.Vec3;

import java.io.*;

public class BitReader {
    byte[] buf;
    long pos;

    public BitReader(byte[] input) {
        buf = input;
        pos = 0;
    }

    public static BitReader fromCompressed(InputStream is) throws IOException {
        Reader r = Reader.fromCompressed(is);
        byte[] uncompressedBuf = r.readAllBytes();
        return new BitReader(uncompressedBuf);
    }

    public void eatByteAlign() {
        pos = (pos + 7) & (~0x07);
    }

    public boolean readBit() {
        boolean bit = (buf[(int) (pos >> 3)] & (1 << (pos & 7))) != 0;
        pos++;
        return bit;
    }

    public void readBits(byte[] dst, int len) {
        for (int bit=0; bit < len; bit++) {
            int shift = bit & 0x07;
            dst[bit >>> 3] = (byte) ((dst[bit >>> 3] & ~(1 << shift)) | ((readBit()?1:0) << shift));
        }
    }

    public short readShort() {
        byte[] bytes = new byte[2];
        readBits(bytes, 16);
        return (short) (((bytes[1] & 0xFF) << 8) | (bytes[0] & 0xFF));
    }

    public int readInt() {
        byte[] bytes = new byte[4];
        readBits(bytes, 32);
        return ((bytes[3] & 0xFF) << 24) | ((bytes[2] & 0xFF) << 16) | ((bytes[1] & 0xFF) << 8) | (bytes[0] & 0xFF);
    }

    public int readInt(int max) {
        int value = 0;
        int mask = 1;
        while ((value + mask) < max && mask != 0) {
            if (readBit())
                value |= mask;
            mask <<= 1;
        }
        return value;
    }

    public int readIntPacked() {
        int value = 0;
        for (int i=0; i < 5; i++) {
            boolean hasNext = readBit();
            int part = 0;
            for (int bitShift=0; bitShift < 7; bitShift++)
                part |= (readBit()?1:0) << bitShift;
            value |= part << (7*i);
            if (!hasNext)
                break;
        }
        return value;
    }

    public Vec3 readPositiveIntVectorPacked() {
        return new Vec3(readIntPacked(), readIntPacked(), readIntPacked());
    }

    public Vec3 readIntVectorPacked() {
        return new Vec3(readSignedIntPacked(), readSignedIntPacked(), readSignedIntPacked());
    }

    public String readString() throws IOException {
        int size = readInt();
        if (size >= 0)
            return readAscii(size);
        else
            return readUCS2(size);
    }

    private String readAscii(int size) {
        byte[] bytes = readNBytes(size);
        char[] string = new char[Math.max(size - 1, 0)];
        for (int i=0; i < string.length; i++)
            string[i] = (char) (bytes[i] & 0xFF);
        return new String(string);
    }

    private String readUCS2(int size) throws IOException {
        size = -size;
        if (size % 2 != 0)
            throw new IOException("Invalid UCS-2 data size");
        char[] string = new char[(size / 2) - 1];
        for (int i=0; i < string.length; i++)
            string[i] = (char) readShort();
        readShort();
        return new String(string);
    }

    public byte[] readNBytes(int n) {
        byte[] bytes = new byte[n];
        readBits(bytes, n * 8);
        return bytes;
    }

    private int readSignedIntPacked() {
        long value = Integer.toUnsignedLong(readIntPacked());
        return (int) ((value >>> 1) * ((value & 1) != 0 ? 1 : -1));
    }

    public void debug() throws IOException {
        FileOutputStream os = new FileOutputStream("debug2");
        os.write(buf);
    }

}
