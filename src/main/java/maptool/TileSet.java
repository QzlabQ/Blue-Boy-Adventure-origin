package maptool;

import javax.imageio.ImageIO;

import main.MapData;

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
        for (int i = 0; i < MapData.getTileCount(); i++) {
            setup(i, MapData.getName(i), MapData.getCollision(i));
        }
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