package maptool;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class TileSet {
    private HashMap<Integer, TileInfo> tileSet;
    private String tilesFolder;

    public TileSet() {
        tileSet = new HashMap<>();
    }

    public void loadTileSet(String folder) {
        this.tilesFolder = folder;
        setup(0, "voidimg", false);
        setup(1, "stairs1", false);
        setup(2, "stairs2", false);
        setup(3, "grass00", false);
        setup(4, "grass00", false);
        setup(6, "grass00", false);
        setup(7, "grass00", false);
        setup(8, "grass00", false);
        setup(9, "grass00", false);

        setup(10, "grass00", false);
        setup(11, "grass01", false);
        setup(12, "water00", true);
        setup(13, "water01", true);
        setup(14, "water02", true);
        setup(15, "water03", true);
        setup(16, "water04", true);
        setup(17, "water05", true);
        setup(18, "water06", true);
        setup(19, "water07", true);
        setup(20, "water08", true);
        setup(21, "water09", true);
        setup(22, "water10", true);
        setup(23, "water11", true);
        setup(24, "water12", true);
        setup(25, "water13", true);
        setup(26, "road00", false);
        setup(27, "road01", false);
        setup(28, "road02", false);
        setup(29, "road03", false);
        setup(30, "road04", false);
        setup(31, "road05", false);
        setup(32, "road06", false);
        setup(33, "road07", false);
        setup(34, "road08", false);
        setup(35, "road09", false);
        setup(36, "road10", false);
        setup(37, "road11", false);
        setup(38, "road12", false);
        setup(39, "earth", false);
        setup(40, "wall", true);
        setup(41, "tree", true);
        setup(42, "hut", false);
        setup(43, "floor01", false);
        setup(44, "table01", true);
    }

    private void setup(int id, String name, boolean hasCollision) {
        TileInfo tile = new TileInfo(name, hasCollision);
        tileSet.put(id, tile);

        try {
            String imagePath = tilesFolder + name + ".png";
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                tile.setImage(ImageIO.read(imageFile));
            } else {
                System.out.println("警告: 图像文件不存在: " + imagePath);
            }
        } catch (IOException e) {
            System.out.println("无法加载图像: " + name + ".png");
        }
    }

    public TileInfo getTile(int id) {
        return tileSet.get(id);
    }

    public Iterable<Integer> getAllTileIds() {
        return tileSet.keySet();
    }
}