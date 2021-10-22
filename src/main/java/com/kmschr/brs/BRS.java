package com.kmschr.brs;

import com.kmschr.brs.enums.Direction;
import com.kmschr.brs.enums.Rotation;
import com.kmschr.brs.enums.ScreenshotFormat;
import com.kmschr.brs.enums.Version;
import com.kmschr.brs.io.BitReader;
import com.kmschr.brs.io.BitWriter;
import com.kmschr.brs.io.Reader;
import com.kmschr.brs.io.Writer;
import me.zombie_striker.omeggajava.JOmegga;

import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * The main interface for brs-java. The two operations are reading a save and writing a save.
 */
public final class BRS {

    static final byte[] MAGIC = new byte[]{(byte) 'B', (byte) 'R', (byte) 'S'};
    static final short LATEST_VERSION = 8;
    static final int DEFAULT_GAME_VERSION = 3642;

    private BRS() {
    }

    /**
     * Writes Brickadia save data to a given filename
     *
     * @param filename - path to .brs file to be created, used to create FileOutputStream
     * @param data     - save data to be written
     * @throws IOException when something goes wrong writing the save
     * @see SaveData
     */
    public static void writeSave(String filename, SaveData data) throws IOException {
        writeSave(new FileOutputStream(filename), data);
    }

    /**
     * Writes Brickadia save data to a given output stream
     *
     * @param os   - any output stream, most commonly a FileOutputStream
     * @param data - save data to be written
     * @throws IOException when something goes wrong writing the save
     * @see SaveData
     */
    public static void writeSave(OutputStream os, SaveData data) throws IOException {
        writePreamble(os);
        writeHeader1(os, data);
        writeHeader2(os, data);
        writeScreenshot(os, data);
        writeBricks(os, data);
        writeComponents(os, data);
    }


    /**
     * Reads Brickadia save data from a given filename
     *
     * @param filename path to .brs file to be read from, used to create FileInputStream
     * @return save data from the .brs file
     * @throws IOException when something goes wrong reading the save
     */
    public static SaveData readSave(String filename) throws IOException {
        return readSave(new FileInputStream(filename));
    }

    /**
     * Reads Brickadia save data from a given input stream
     *
     * @param is input stream to be read from
     * @return save data from the input stream
     * @throws IOException when something goes wrong reading the save
     */
    public static SaveData readSave(InputStream is) throws IOException {
        SaveData data = new SaveData();
        readPreamble(is, data);
        readHeader1(is, data);
        readHeader2(is, data);
        readScreenshot(is, data);
        readBricks(is, data);
        readComponents(is, data);
        return data;
    }

    private static void writePreamble(OutputStream os) throws IOException {
        com.kmschr.brs.io.Writer w = new com.kmschr.brs.io.Writer();
        w.writeAll(MAGIC);
        w.writeShort(LATEST_VERSION);
        if (LATEST_VERSION >= Version.AddedGameVersionAndHostAndOwnerDataAndImprovedMaterials.ordinal())
            w.writeInt(5674);
        w.writeToStream(os);
    }

    private static void readPreamble(InputStream is, SaveData data) throws IOException {
        com.kmschr.brs.io.Reader r = new com.kmschr.brs.io.Reader(is);
        byte[] magic = r.readNBytes(3);
        if (!Arrays.equals(magic, MAGIC))
            throw new IOException("Invalid starting bytes");
        data.version = Version.getVersion(r.readShort());
        if (data.version.ordinal() >= Version.AddedGameVersionAndHostAndOwnerDataAndImprovedMaterials.ordinal())
            data.gameVersion = r.readInt();
        else
            data.gameVersion = 3642;
    }

    private static void writeHeader1(OutputStream os, SaveData data) throws IOException {
        com.kmschr.brs.io.Writer w = new com.kmschr.brs.io.Writer();
        w.writeString(data.map);
        w.writeString(data.author.name);
        w.writeString(data.description);
        w.writeUUID(data.author.id);
        if (LATEST_VERSION >= Version.AddedGameVersionAndHostAndOwnerDataAndImprovedMaterials.ordinal())
            w.writeUserNameFirst(data.host);
        if (LATEST_VERSION >= Version.AddedDateTime.ordinal())
            w.writeDateTime(data.saveTime);
        w.writeInt(data.bricks.size());
        w.compressToStream(os);
    }

    private static void readHeader1(InputStream is, SaveData data) throws IOException {
        com.kmschr.brs.io.Reader r = com.kmschr.brs.io.Reader.fromCompressed(is);
        data.map = r.readString();
        String authorName = r.readString();
        data.description = r.readString();
        UUID authorID = r.readUUID();
        data.author = new User(authorID, authorName);
        if (data.version.ordinal() >= Version.AddedGameVersionAndHostAndOwnerDataAndImprovedMaterials.ordinal())
            data.host = r.readUserNameFirst();
        if (data.version.ordinal() >= Version.AddedDateTime.ordinal())
            data.saveTime = r.readDateTime();
        data.brickCount = r.readInt();
    }

    private static void writeHeader2(OutputStream os, SaveData data) throws IOException {
        Writer w = new Writer();
        w.writeStrings(data.mods);
        w.writeStrings(data.brickAssets);
        w.writeInt(data.colors.size());
        for (Color c : data.colors)
            w.writeInt(c.getValue());
        w.writeStrings(data.materials);
        w.writeInt(data.brickOwners.size());
        processBricksForOwnerData(data);
        for (User user : data.brickOwners) {
            w.writeUUID(user.id);
            w.writeString(user.name);
            if (user instanceof BrickOwner)
                w.writeInt(((BrickOwner) user).brickcount);
        }
        if (data.version.ordinal() >= Version.t1.ordinal())
            w.writeStrings(Arrays.asList("BPMC_Default")); // physical mats
        w.compressToStream(os);
    }

    private static void processBricksForOwnerData(SaveData data) {
        boolean encounteredError = false;
        for (Brick brick : data.getBricks()) {
            Integer ownerIndex = brick.getOwnerIndex();
            if (ownerIndex != null) {
                if (ownerIndex >= data.brickOwners.size() || ownerIndex < 0) {
                    if (!encounteredError) {
                        System.out.println("\u001B[31mBRS WARNING:\u001B[0m Bricks had owner index outside of brick owner range, these bricks were written with no owner");
                        encounteredError = true;
                    }
                    brick.ownerIndex = null;
                } else {
                    User owner = data.brickOwners.get(ownerIndex);
                    if (owner instanceof BrickOwner) {
                        ((BrickOwner) owner).brickcount++;
                    } else {
                        data.brickOwners.set(ownerIndex, new BrickOwner(owner, 1));
                    }
                }
            }
        }
    }

    private static void readHeader2(InputStream is, SaveData data) throws IOException {
        Reader r = Reader.fromCompressed(is);
        data.mods = r.readStrings();
        data.brickAssets = r.readStrings();
        int colors = r.readInt();
        for (int i = 0; i < colors; i++)
            data.colors.add(new Color(r.readInt()));
        if (data.version.ordinal() >= Version.MaterialsAsStoredAsNames.ordinal())
            data.materials = r.readStrings();
        else
            data.materials = SaveData.defaultMaterials();
        int users = r.readInt();
        for (int i = 0; i < users; i++)
            if (data.version.ordinal() >= Version.AddedGameVersionAndHostAndOwnerDataAndImprovedMaterials.ordinal())
                data.brickOwners.add(r.readBrickOwner());
            else
                data.brickOwners.add(r.readUser());
        if (data.version.ordinal() >= Version.t1.ordinal()) {
            List<String> bpmc_default = r.readStrings();
        }
    }

    private static void writeBricks(OutputStream os, SaveData data) throws IOException {
        BitWriter w = new BitWriter();
        for (Brick brick : data.bricks) {
            w.flushByte();
            w.writeInt(brick.assetNameIndex,Math.max(2,data.brickAssets.size()));
            if (w.writeBit(!brick.size.isZero()))
                w.writePositiveIntVectorPacked(brick.size);
            w.writeIntVectorPacked(brick.position);
            byte orientation = Utils.combineOrientation(brick.direction, brick.rotation);
            w.writeInt(orientation & 0xFF, 24);
            w.writeBit(brick.collision);
            if (data.version.ordinal() >= Version.t1.ordinal()) {
                w.writeBit(brick.weaponcollision);
                w.writeBit(brick.interactioncollision);
                w.writeBit(brick.toolcollision);
            }
            w.writeBit(brick.visibility);
            w.writeInt(brick.materialIndex, data.materials.size());
            if (data.version.ordinal() >= Version.t1.ordinal()) {
                w.writeInt(0,2);
                w.writeInt(6,11);
            }
            if (brick.color instanceof Color) {
                w.writeBit(true);
                w.writeInt(brick.color.getValue());
            } else {
                w.writeBit(false);
                if(data.version.ordinal() >= Version.t1.ordinal()){
                    w.writeByte((byte) 5);
                    w.writeByte((byte) 5);
                    w.writeByte((byte) 7);
                }else {
                    w.writeInt(brick.color.getValue(), data.colors.size());
                }
            }
            w.writeIntPacked(brick.ownerIndex == null ? 0 : brick.ownerIndex + 1);
        }
        w.flushByte();
        w.compressToStream(os);
    }

    private static void readBricks(InputStream is, SaveData data) throws IOException {
        BitReader r = BitReader.fromCompressed(is);
        r.debug();
        data.bricks = new ArrayList<>(data.brickCount);
        for (int i = 0; i < data.brickCount; i++) {
            try {
                Brick brick = new Brick();
                r.eatByteAlign();
                brick.assetNameIndex = r.readInt(data.brickAssets.size());
                Vec3 size = new Vec3();
                if (r.readBit())
                    size = r.readPositiveIntVectorPacked();
                brick.size = size;
                brick.position = r.readIntVectorPacked();
                byte orientation = (byte) r.readInt(24);
                brick.direction = Direction.getDirection(orientation);
                brick.rotation = Rotation.getRotation(orientation);
                brick.collision = r.readBit();
                if (data.version.ordinal() >= Version.t1.ordinal()) {
                    brick.weaponcollision = r.readBit();
                    brick.interactioncollision = r.readBit();
                    brick.toolcollision = r.readBit();
                }
                brick.visibility = r.readBit();
                if (data.version.ordinal() >= Version.t1.ordinal()) {
                    brick.materialIndex = r.readInt(data.materials.size());
                    r.readInt(2);
                    r.readInt(11);
                } else if (data.version.ordinal() >= Version.AddedGameVersionAndHostAndOwnerDataAndImprovedMaterials.ordinal())
                    brick.materialIndex = r.readInt(data.materials.size());
                else if (r.readBit())
                    brick.materialIndex = r.readIntPacked();
                else
                    brick.materialIndex = 1;

                if (!r.readBit())
                    brick.color = new ColorMode(r.readInt(data.colors.size()));
                else if (data.version.ordinal() >= Version.t1.ordinal()) {
                    brick.color = new Color(r.readNBytes(3));
                } else {
                    brick.color = new Color(r.readInt());
                }

                if (data.version.ordinal() >= Version.AddedOwnerData.ordinal())
                    brick.ownerIndex = r.readIntPacked();
                else
                    brick.ownerIndex = 0;

                if (brick.ownerIndex == 0) {
                    brick.ownerIndex = null;
                } else {
                    brick.ownerIndex--;
                }

                data.bricks.add(brick);
                JOmegga.log(brick.assetNameIndex+" "+brick.materialIndex+" "+brick.size+"  "+brick.position+" "+brick.collision+" "+brick.visibility+" "+brick.materialIndex+" "+brick.color.color+" "+brick.ownerIndex);

            }catch(Exception e4){
                JOmegga.log("Failed to load a brick.");
            }
        }
    }

    private static void skipScreenshot(InputStream is, SaveData data) throws IOException {
        Reader r = new Reader(is);
        int screenshotDataLength = 0;
        if (data.version.ordinal() >= Version.AddedScreenshotsData.ordinal()) {
            ScreenshotFormat format = ScreenshotFormat.getFormat(r.readByte());
            if (format != ScreenshotFormat.None) {
                screenshotDataLength = r.readInt();
                r.skipNBytes(screenshotDataLength);
            }
        }
    }

    private static void readScreenshot(InputStream is, SaveData data) throws IOException {
        Reader r = new Reader(is);
        int screenshotDataLength = 0;
        if (data.version.ordinal() >= Version.AddedScreenshotsData.ordinal()) {
            ScreenshotFormat format = ScreenshotFormat.getFormat(r.readByte());
            if (format != ScreenshotFormat.None) {
                screenshotDataLength = r.readInt();
                byte[] screenshotData = r.readNBytes(screenshotDataLength);
                data.screenshot = ImageIO.read(new ByteArrayInputStream(screenshotData));
            }
        }
    }

    private static void writeScreenshot(OutputStream os, SaveData data) throws IOException {
        if (LATEST_VERSION < Version.AddedScreenshotsData.ordinal())
            return;
        Writer w = new Writer();
        if (data.screenshot == null) {
            w.writeByte((byte) ScreenshotFormat.None.ordinal());
        } else {
            w.writeByte((byte) ScreenshotFormat.Png.ordinal());
            ByteArrayOutputStream screenshotData = new ByteArrayOutputStream();
            ImageIO.write(data.screenshot, "png", screenshotData);
            w.writeInt(screenshotData.size());
            w.writeAll(screenshotData.toByteArray());
        }
        w.writeToStream(os);
    }

    private static void readComponents(InputStream is, SaveData data) throws IOException {
        if (data.version.ordinal() < Version.AddedComponentsData.ordinal())
            return;
        BitReader r = BitReader.fromCompressed(is);
        int filledComponents = r.readInt();
        data.components = new ArrayList<>(filledComponents);
        for (int i = 0; i < filledComponents; i++) {
            Component component = new Component();
            r.eatByteAlign();
            component.name = r.readString();
            if (data.version.ordinal() < Version.RenamedComponentDescriptors.ordinal())
                component.name = component.name.replace("BTD", "BCD");
            int dataLen = r.readInt();
            component.version = r.readInt();
            int brickIndicesLength = r.readInt();
            component.brickIndices = new ArrayList<>(brickIndicesLength);
            for (int j = 0; j < brickIndicesLength; j++)
                component.brickIndices.add(r.readInt(Math.max(data.brickCount, 2)));
            int propertiesLen = r.readInt();
            component.properties = new ArrayList<>(propertiesLen);
            for (int j = 0; j < propertiesLen; j++) {
                String s1 = r.readString();
                String s2 = r.readString();
                component.properties.add(new String[]{s1, s2});
            }
            component.data = r.readNBytes(dataLen);
            data.components.add(component);
        }
    }

    private static void writeComponents(OutputStream os, SaveData data) throws IOException {
        if (data.version.ordinal() < Version.t1.ordinal())
            return;
        Writer w = new Writer();
        w.writeInt(0);
        /*w.writeInt(1);
        w.writeString("BCD_PointLight");
        BitWriter br = new BitWriter();
        br.writeInt(1);
        br.writeInt(data.brickCount);
        for(int i = 0; i < data.brickCount;i++){
            br.writeInt(Math.max(i,data.bricks.size()));
        }
        br.writeInt(6);
        br.writeString("Brightness");
        br.writeString("Float");
        br.writeString("Color");
        br.writeString("Color");
        br.writeString("Radius");
        br.writeString("Float");
        br.writeString("bCastShadows");
        br.writeString("Boolean");
        br.writeString("bMatchBrickShape");
        br.writeString("Boolean");
        br.writeString("bUseBrickColor");
        br.writeString("Boolean");*/

    }
}
