package maptool;

import java.awt.*;
import java.awt.image.BufferedImage;

public class TileInfo {
    String name;
    boolean hasCollision;
    BufferedImage image;
    Color displayColor;

    public TileInfo(String name, boolean hasCollision) {
        this.name = name;
        this.hasCollision = hasCollision;
        this.image = null;
        this.displayColor = generateColor(name);
    }

    private Color generateColor(String name) {
        int hash = name.hashCode();
        return new Color(
                Math.abs((hash & 0xFF0000) >> 16) % 200 + 55,
                Math.abs((hash & 0x00FF00) >> 8) % 200 + 55,
                Math.abs(hash & 0x0000FF) % 200 + 55);
    }

    // Getters
    public String getName() {
        return name;
    }

    public boolean hasCollision() {
        return hasCollision;
    }

    public BufferedImage getImage() {
        return image;
    }

    public Color getDisplayColor() {
        return displayColor;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}