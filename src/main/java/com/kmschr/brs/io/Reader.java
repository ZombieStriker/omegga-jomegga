package com.kmschr.brs.io;

import com.kmschr.brs.BrickOwner;
import com.kmschr.brs.User;
import com.kmschr.brs.Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * <p>
 *    A simple reader for retrieving various data types from an input stream or byte array. Also capable of reading from
 *    an input stream of compressed data in the DEFLATE format.
 * </p>
 */
public class Reader {
    private final InputStream is;

    public Reader(byte[] input) {
        this.is = new ByteArrayInputStream(input);
    }

    public Reader(InputStream is) {
        this.is = is;
    }

    /**
     * Constructs a reader for a compressed input stream. Only for the DEFLATE compression format. compressed data is
     * preceded by two little endian integer values, representing the length of the data in uncompressed format and the
     * length of the data in compressed format. If the length of the data in compressed format is zero it will not
     * decompress the data.
     * @param is an input stream containing potentially compressed data
     * @return a reader for the uncompressed data contained in the input stream
     * @throws IOException when the stream has an invalid format (probably not compressed)
     */
    public static Reader fromCompressed(InputStream is) throws IOException {
        Reader r = new Reader(is);
        int uncompressedSize = r.readInt();
        int compressedSize = r.readInt();
        if (compressedSize < 0 || compressedSize >= uncompressedSize)
            throw new IOException("Invalid compressed section size");
        if (compressedSize == 0) {
            return new Reader(r.readNBytes(uncompressedSize));
        } else {
            byte[] compressed = r.readNBytes(compressedSize);
            byte[] uncompressed = new byte[uncompressedSize];
            Inflater decoder = new Inflater();
            decoder.setInput(compressed, 0, compressedSize);
            try {
                decoder.inflate(uncompressed);
                decoder.end();
            } catch(DataFormatException e) {
                e.printStackTrace();
            }
            return new Reader(uncompressed);
        }
    }

    public byte readByte() throws IOException {
        return (byte) is.read();
    }

    public short readShort() throws IOException {
        int lsb = is.read();
        int msb = is.read();
        return (short) ((msb << 8) | lsb);
    }

    public int readInt() throws IOException {
        int a = is.read();
        int b = is.read();
        int c = is.read();
        int d = is.read();
        return (d << 24) | (c << 16) | (b << 8) | a;
    }

    public long readLong() throws IOException {
        long lsb = Integer.toUnsignedLong(readInt());
        long msb = Integer.toUnsignedLong(readInt());
        return (msb << 32) | lsb;
    }

    public String readString() throws IOException {
        int size = readInt();
        if (size >= 0)
            return readAscii(size);
        else
            return readUCS2(size);
    }

    public List<String> readStrings() throws IOException {
        int len = readInt();
        List<String> strings = new ArrayList<>(len);
        for (int i=0; i < len; i++)
            strings.add(readString());
        return strings;
    }

    public UUID readUUID() throws IOException {
        long[] sections = new long[4];
        for (int i=0; i < 4; i++)
            sections[i] = Integer.toUnsignedLong(readInt());
        long msb = (sections[0] << 32) | sections[1];
        long lsb = (sections[2] << 32) | sections[3];
        return new UUID(msb, lsb);
    }

    public User readBrickOwner() throws IOException {
        User user = readUser();
        int brickcount = readInt();
        return new BrickOwner(user, brickcount);
    }

    public User readUser() throws IOException {
        UUID id = readUUID();
        String name = readString();
        return new User(id, name);
    }

    public User readUserNameFirst() throws IOException {
        String name = readString();
        UUID id = readUUID();
        return new User(id, name);
    }

    public ZonedDateTime readDateTime() throws IOException {
        long ticks = readLong();
        long microseconds = (ticks / 10);
        long millis = microseconds / 1000;
        long microLeftOver = microseconds % 1000;
        long nanoseconds = (ticks % 10) * 100;
        ZonedDateTime time = Utils.ue4DateTimeBase();
        time = time.plus(Duration.ofMillis(millis));
        time = time.plus(Duration.ofNanos(microLeftOver * 1000));
        time = time.plus(Duration.ofNanos(nanoseconds));
        return time;
    }

    public void skipNBytes(long n) throws IOException {
        if (is.skip(n) < n)
            throw new IOException("Tried to skip beyond end of stream");
    }

    public byte[] readNBytes(int n) throws IOException {
        return is.readNBytes(n);
    }

    public byte[] readAllBytes() throws IOException {
        return is.readAllBytes();
    }

    private String readAscii(int size) throws IOException {
        byte[] bytes = is.readNBytes(size);
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

}
