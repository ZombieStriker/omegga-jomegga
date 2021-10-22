package com.kmschr.brs;

import com.kmschr.brs.enums.Direction;
import com.kmschr.brs.enums.Rotation;

public class Brick {
    int assetNameIndex = 0;
    Vec3 size = new Vec3();
    Vec3 position = new Vec3();
    Direction direction = Direction.ZPositive;
    Rotation rotation = Rotation.Deg0;
    boolean collision = true;
    boolean weaponcollision = true;
    boolean interactioncollision = true;
    boolean toolcollision = true;
    boolean visibility = true;
    int materialIndex = 0;
    int physicalIndex=0;
    int materialIntensity=5;
    ColorMode color = new ColorMode(0);
    Integer ownerIndex;

    public Brick() {}

    public Brick(ColorMode color) {
        this.color = color;
    }

    public String toString() {
        return "Asset Name Index: " + assetNameIndex + '\n' +
                "Size: " + size + '\n' +
                "Position: " + position + '\n' +
                "Direction: " + direction + '\n' +
                "Rotation: " + rotation + '\n' +
                "Collision: " + collision + '\n' +
                "Visibility: " + visibility + '\n' +
                "Material Index: " + materialIndex + '\n' +
                "Physical Index: " + physicalIndex + '\n' +
                "Material Intensity: " + materialIntensity + '\n' +
                "Color: " + color + '\n' +
                "Owner Index: " + ownerIndex;
    }

    public int getAssetNameIndex() {
        return assetNameIndex;
    }

    public void setAssetNameIndex(int assetNameIndex) {
        this.assetNameIndex = assetNameIndex;
    }

    public Vec3 getSize() {
        return size;
    }

    public void setSize(Vec3 size) {
        this.size = size;
    }

    public void setSize(int x, int y, int z) {
        this.size = new Vec3(x, y, z);
    }

    public Vec3 getPosition() {
        return position;
    }

    public void setPosition(Vec3 position) {
        this.position = position;
    }

    public void shift(int x, int y, int z) {
        position.x += x;
        position.y += y;
        position.z += z;
    }

    public void setPosition(int x, int y, int z) {
        this.position = new Vec3(x, y, z);
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Rotation getRotation() {
        return rotation;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    public boolean isCollision() {
        return collision;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public int getMaterialIndex() {
        return materialIndex;
    }

    public void setMaterialIndex(int materialIndex) {
        this.materialIndex = materialIndex;
    }

    public ColorMode getColor() {
        return color;
    }

    public void setColor(ColorMode color) {
        this.color = color;
    }

    public Integer getOwnerIndex() {
        return ownerIndex;
    }

    public void setOwnerIndex(Integer ownerIndex) {
        this.ownerIndex = ownerIndex;
    }

    public int getMaterialIntensity() {
        return materialIntensity;
    }

    public int getPhysicalIndex() {
        return physicalIndex;
    }

    public void setInteractioncollision(boolean interactioncollision) {
        this.interactioncollision = interactioncollision;
    }

    public void setMaterialIntensity(int materialIntensity) {
        this.materialIntensity = materialIntensity;
    }

    public void setPhysicalIndex(int physicalIndex) {
        this.physicalIndex = physicalIndex;
    }

    public void setWeaponcollision(boolean weaponcollision) {
        this.weaponcollision = weaponcollision;
    }

    public void setToolcollision(boolean toolcollision) {
        this.toolcollision = toolcollision;
    }
}
