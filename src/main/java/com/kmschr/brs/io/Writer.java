package com.kmschr.brs.io;

import com.kmschr.brs.User;
import com.kmschr.brs.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.zip.Deflater;

/**
 * <p>
 *     A simple writer capable of writing various data types to a byte buffer. This byte buffer can then be written to
 *     an output stream. It can also compress it's buffer to the DEFLATE format and write that to an output stream.
 *     Primitive types are written in little endian byte ordering, while more complex types may have their own byte
 *     ordering schemes.
 * </p>
 */
public class Writer {
    private final List<Byte> buf;

    public Writer() {
        buf = new ArrayList<>(256);
    }

    /**
     * Writes the current contents of the buffer to the given output stream.
     * @param os the output stream
     * @throws IOException if an I/O error occurs. In particular, an IOException may be thrown if the output stream has been closed.
     */
    public void writeToStream(OutputStream os) throws IOException {
        os.write(bufToByteArray());
    }

    /**
     * Compresses the contents of the buffer and writes to the given output stream.
     * <p>
     *     This will write the uncompressed contents of the buffer in the scenario that the compressed version
     *     of the buffer is in fact larger than the uncompressed version. Either way, the buffer is preceded by
     *     the uncompressed buffer size and the compressed buffer size. If using the uncompressed buffer, the
     *     compressed buffer size will be written as 0.
     * </p>
     * @param os the output stream
     * @return whether or not the buffer benefited from compression
     * @throws IOException if an I/O error occurs.
     */
    public boolean compressToStream(OutputStream os) throws IOException {
        Writer internal = new Writer();
        internal.writeInt(buf.size());
        byte[] compressed = compress();
        if (compressed == null) {
            internal.writeInt(0);
            internal.writeAll(buf);
            internal.writeToStream(os);
            return false;
        } else {
            internal.writeInt(compressed.length);
            internal.writeAll(compressed);
            internal.writeToStream(os);
            return true;
        }
    }

    private byte[] compress() {
        Deflater encoder = new Deflater();
        encoder.setInput(bufToByteArray());
        encoder.finish();
        byte[] naiveBuffer = new byte[buf.size() + 1];
        int compressedSize = encoder.deflate(naiveBuffer);
        if (compressedSize >= buf.size())
            return null;
        else
            return Arrays.copyOf(naiveBuffer, compressedSize);
    }

    /**
     * Writes a single byte to the end of the buffer
     * @param b - the byte
     */
    public void writeByte(byte b) {
        buf.add(b);
    }

    /**
     * Writes a single short (2 bytes) to the end of the buffer in little endian byte order
     * @param s - the short
     */
    public void writeShort(short s) {
        writeByte((byte) s);
        writeByte((byte) (s >> 8));
    }

    /**
     * Writes a single integer (4 bytes) to the end of the buffer in little endian byte order
     * @param i - the integer
     */
    public void writeInt(int i) throws IOException {
        writeShort((short) i);
        writeShort((short) (i >> 16));
    }

    /**
     * Writes a single long (8 bytes) to the end of the buffer in little endian byte order
     * @param l - the long
     */
    public void writeLong(long l) throws IOException {
        writeInt((int) l);
        writeInt((int) (l >> 32));
    }

    /**
     * Writes every byte of a given byte array to the end of the buffer
     * @param bytes - an array of bytes
     */
    public void writeAll(byte[] bytes) {
        for (byte b : bytes)
            writeByte(b);
    }

    /**
     * Writes a string (ASCII or UCS-2 encoding) to the buffer.
     * @param s - the string
     * @throws IOException when the string contains unsupported characters
     */
    public void writeString(String s) throws IOException {
        char[] string = s.toCharArray();
        if (isAscii(string))
            writeAscii(string);
        else
            writeUCS2(string);
    }

    /**
     * Writes a list of strings to the end of the buffer.
     * @param strings - the list of strings
     * @throws IOException when any of the strings contain unsupported characters
     */
    public void writeStrings(List<String> strings) throws IOException {
        writeInt(strings.size());
        for (String s : strings)
            writeString(s);
    }

    /**
     * Writes a UUID (universally unique identifier) to the end of the buffer.
     * @param uuid - the uuid
     */
    public void writeUUID(UUID uuid) throws IOException {
        ByteBuffer bb = ByteBuffer.allocateDirect(16);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        bb.flip();
        for (int i=0; i < 4; i++)
            writeInt(bb.getInt());
    }

    /**
     * Writes a date time to the end of the buffer.
     * @param dateTime - the datetime
     */
    public void writeDateTime(ZonedDateTime dateTime) throws IOException {
        Duration duration = Duration.between(Utils.ue4DateTimeBase(), dateTime);
        long ticksSecs = duration.toSeconds() * 10_000_000L;
        long ticksNanos = duration.toNanosPart() / 100L;
        writeLong(ticksSecs + ticksNanos);
    }

    /**
     * Write a User first by their username and then their UUID
     * @param user the user to write
     * @throws IOException when the username contains unsupported characters
     */
    public void writeUserNameFirst(User user) throws IOException {
        writeString(user.getName());
        writeUUID(user.getId());
    }

    /**
     * Gets the internal buffer of the writer as a byte array
     * @return the byte array
     */
    public byte[] bufToByteArray() {
        byte[] bytes = new byte[buf.size()];
        for (int i=0; i < buf.size(); i++)
            bytes[i] = buf.get(i);
        return bytes;
    }

    private void writeAscii(char[] string) throws IOException {
        int len = string.length + 1;
        writeInt(len);
        for (char c : string)
            writeByte((byte) c);
        writeByte((byte) 0);
    }

    private void writeUCS2(char[] string) throws IOException {
        int len = -((string.length + 1) * 2);
        writeInt(len);
        for (char c : string)
            if (isUCS2(c))
                writeShort((short) c);
            else
                throw new IOException("String contains non UCS-2 characters");
        writeShort((short) 0);
    }

    private boolean isAscii(char[] string) {
        for (char c : string)
            if (c > 0x7f)
                return false;
        return true;
    }

    private boolean isUCS2(char c) {
        return c <= 0xd7ff || c >= 0xe000;
    }

    private void writeAll(List<Byte> bytes) {
        buf.addAll(bytes);
    }

}
