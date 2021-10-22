package com.kmschr.brs.io;

import com.kmschr.brs.Vec3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class BitWriter extends Writer {
    private byte cur;
    private byte bit;

    public BitWriter() {
        cur = 0;
        bit = 0;
    }

    public boolean writeBit(boolean bit) {
        cur |= (bit?1:0) << this.bit;
        this.bit++;
        if (this.bit >= Byte.SIZE)
            flushByte();
        return bit;
    }

    public void writeBits(int src, int len) {
        for (int bit=0; bit < len; bit++)
            writeBit((src & (1 << (bit & 7))) != 0);
    }

    public void writeBits(byte[] src, int len) {
        for (int bit=0; bit < len; bit++)
            writeBit(((src[bit >>> 3] & 0xFF) & (1 << (bit & 7))) != 0);
    }

    public void writeInt(int i) throws IOException {
        Writer w = new Writer();
        w.writeInt(i);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        w.writeToStream(bytes);
        byte[] intBytes = bytes.toByteArray();
        writeBits(intBytes, intBytes.length * 8);
    }

    public void writeInt(int value, int max) throws IOException {
        if (max < 2)
            throw new IOException("max must be at least 2");
        if (value > max)
            throw new IOException("value cant be greater than max in writeInt");
        int newValue = 0;
        int mask = 1;
        while ((newValue + mask) < max && mask != 0) {
            writeBit((value & mask) != 0);
            if ((value & mask) != 0) {
                newValue |= mask;
            }
            mask <<= 1;
        }
    }

    public void writeIntPacked(int value) {
        // fits an integer in as little bytes as possible
        // splits into 7 bit sections, with leading bit telling whether or not there is another section coming up
        do {
            int src = value & 0x7F;
            value >>>= 7;
            writeBit(value != 0);
            writeBits(src, 7);
        } while (value != 0);
    }

    public void writePositiveIntVectorPacked(Vec3 v) {
        writeIntPacked(v.x);
        writeIntPacked(v.y);
        writeIntPacked(v.z);
    }

    public void writeIntVectorPacked(Vec3 v) {
        writeIntPacked(map(v.x));
        writeIntPacked(map(v.y));
        writeIntPacked(map(v.z));
    }

    private int map(int n) {
        return (Math.abs(n) << 1) | (n > 0?1:0);
    }

    public void flushByte() {
        if (bit > 0) {
            writeByte(cur);
            cur = 0;
            bit = 0;
        }
    }

}
