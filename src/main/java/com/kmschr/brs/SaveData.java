package com.kmschr.brs;

import com.kmschr.brs.enums.Version;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Data contained within a Brickadia save file
 */
public class SaveData {
    String map;
    User author;
    User host;
    String description;
    ZonedDateTime saveTime;
    List<String> mods = new ArrayList<>();
    List<String> brickAssets = new ArrayList<>();
    List<ColorMode> colors;
    List<String> materials;
    List<String> physicalMaterials;
    List<User> brickOwners = new ArrayList<>();
    List<Brick> bricks = new ArrayList<>();
    List<Component> components = new ArrayList<>();
    RenderedImage screenshot;

    // read-only data
    Version version = Version.getVersion(BRS.LATEST_VERSION);
    int gameVersion = BRS.DEFAULT_GAME_VERSION;
    int brickCount;

    private static final List<String> SUPPORTED_MAPS = Arrays.asList("Plate", "Peaks", "Studio","Space");
    private static final List<String> DEFAULT_MATERIALS = Arrays.asList("BMC_Plastic", "BMC_Glow", "BMC_Metallic", "BMC_Hologram", "BMC_Ghost");

    public SaveData() {
        map = "Plate";
        author = new User("8efaeb23-5e82-428e-b575-0dd30270146e", "Smallguy");
        host = new User("8efaeb23-5e82-428e-b575-0dd30270146e", "brs-java");
        description = "Generated using brs-java";
        saveTime = ZonedDateTime.now(ZoneId.of("Z"));
        colors = defaultColors();
        materials = defaultMaterials();
        try {
            ClassLoader classloader = Thread.currentThread().getContextClassLoader();
            screenshot = ImageIO.read(Objects.requireNonNull(classloader.getResourceAsStream("preview.png")));
        } catch (IOException e) {
            System.out.println("Error reading default save preview");
        }
    }

    /**
     * Get a list of the default colors for a Brickadia save file
     * @return list of colors for default colorset
     */
    public static List<ColorMode> defaultColors() {
        List<ColorMode> defaults = new ArrayList<>();
        InputStream inputStream = SaveData.class.getResourceAsStream("/defaultColorset");
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);
        try {
            for (String line; (line = reader.readLine()) != null; ) {
                int color = (int) Long.parseLong(line, 16);
                byte r = (byte) (color >> 24);
                byte g = (byte) (color >> 16);
                byte b = (byte) (color >> 8);
                byte a = (byte) color;
                defaults.add(new Color(r, g, b, a));
            }
        } catch (IOException e) {
            System.out.println("Error reading default colorset");
        }
        return defaults;
    }

    /**
     * Get a list of the default colors for a Brickadia save file
     * @return list of default materials
     */
    public static List<String> defaultMaterials() {
        return new ArrayList<>(DEFAULT_MATERIALS);
    }

    /**
     * Get the map for a Brickadia save file. e.g.: "Plate" or "Peaks"
     * @return the save's map
     */
    public String getMap() {
        return map;
    }

    /**
     * Set the map for the save, must be the name of an actual map
     * @param map the save's new map name
     */
    public void setMap(String map) {
        if (!SUPPORTED_MAPS.contains(map))
            throw new IllegalArgumentException("Unsupported map: " + map);
        this.map = map;
    }

    /**
     * Gets the 'author' of this save file. This is not always who built the save, but is often the user who saved the build
     * onto their own machine.
     * @return the save's author
     */
    public User getAuthor() {
        return author;
    }

    /**
     * Sets the 'author' of this save file
     * @param author user to be set as this save's author
     */
    public void setAuthor(User author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ZonedDateTime getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(ZonedDateTime saveTime) {
        this.saveTime = saveTime;
    }

    public void setSaveTimeToNow() {
        this.saveTime = ZonedDateTime.now(ZoneId.of("Z"));
    }

    public List<String> getMods() {
        return mods;
    }

    public void setMods(List<String> mods) {
        this.mods = mods;
    }

    public List<String> getBrickAssets() {
        return brickAssets;
    }

    public void setBrickAssets(List<String> brickAssets) {
        this.brickAssets = brickAssets;
    }

    public List<ColorMode> getColors() {
        return colors;
    }

    public void setColors(List<ColorMode> colors) {
        this.colors = colors;
    }

    public void useDefaultColors() {
        this.colors = defaultColors();
    }

    public List<String> getMaterials() {
        return materials;
    }

    public void setMaterials(List<String> materials) {
        this.materials = materials;
    }

    public void useDefaultMaterials() {
        this.materials = defaultMaterials();
    }

    public List<User> getBrickOwners() {
        return brickOwners;
    }

    public void setBrickOwners(List<User> brickOwners) {
        this.brickOwners = brickOwners;
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    public void setBricks(List<Brick> bricks) {
        this.bricks = bricks;
    }

    public void clearBricks() {
        this.bricks.clear();
    }

    public RenderedImage getScreenshot() {
        return this.screenshot;
    }

    public void setScreenshot(RenderedImage img) {
        this.screenshot = img;
    }

    public List<Component> getComponents() {
        return components;
    }

    public Version getVersion() {
        return version;
    }

    public int getGameVersion() {
        return gameVersion;
    }

    public User getHost() {
        return host;
    }

    public String toString() {
        return "Map: " + map + '\n' +
               "Description: " + description + '\n' +
               "Author: " + author.name + '\n' +
               "Save Time: " + saveTime + '\n' +
               "Mods: " + mods + '\n' +
               "Materials: " + materials + '\n' +
               "Brick Assets: " + brickAssets + '\n' +
               "Colors: " + colors + '\n' +
               "Brick Owners: " + brickOwners + '\n' +
               "Brick Count: " + bricks.size();
    }

}
