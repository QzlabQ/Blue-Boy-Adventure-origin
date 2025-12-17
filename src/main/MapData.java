package main;

import java.util.ArrayList;

public class MapData {

    private static final ArrayList<TileInfo> tiles = new ArrayList<>();
    private static final ArrayList<String> maps = new ArrayList<>();

    static { // initialize tiles
        tiles.add(new TileInfo("voidimg", true));
        tiles.add(new TileInfo("voidimg", true));
        tiles.add(new TileInfo("stairs1", false));
        tiles.add(new TileInfo("stairs2", false));
        tiles.add(new TileInfo("spike", false));
        tiles.add(new TileInfo("grass00", false));
        tiles.add(new TileInfo("grass00", false));
        tiles.add(new TileInfo("grass00", false));
        tiles.add(new TileInfo("grass00", false));
        tiles.add(new TileInfo("grass00", false));
        // PLACEHOLDER

        tiles.add(new TileInfo("grass00", false));
        tiles.add(new TileInfo("grass01", false));
        tiles.add(new TileInfo("water00", true));
        tiles.add(new TileInfo("water01", true));
        tiles.add(new TileInfo("water02", true));
        tiles.add(new TileInfo("water03", true));
        tiles.add(new TileInfo("water04", true));
        tiles.add(new TileInfo("water05", true));
        tiles.add(new TileInfo("water06", true));
        tiles.add(new TileInfo("water07", true));
        tiles.add(new TileInfo("water08", true));
        tiles.add(new TileInfo("water09", true));
        tiles.add(new TileInfo("water10", true));
        tiles.add(new TileInfo("water11", true));
        tiles.add(new TileInfo("water12", true));
        tiles.add(new TileInfo("water13", true));
        tiles.add(new TileInfo("road00", false));
        tiles.add(new TileInfo("road01", false));
        tiles.add(new TileInfo("road02", false));
        tiles.add(new TileInfo("road03", false));
        tiles.add(new TileInfo("road04", false));
        tiles.add(new TileInfo("road05", false));
        tiles.add(new TileInfo("road06", false));
        tiles.add(new TileInfo("road07", false));
        tiles.add(new TileInfo("road08", false));
        tiles.add(new TileInfo("road09", false));
        tiles.add(new TileInfo("road10", false));
        tiles.add(new TileInfo("road11", false));
        tiles.add(new TileInfo("road12", false));
        tiles.add(new TileInfo("earth", false));
        tiles.add(new TileInfo("wall", true));
        tiles.add(new TileInfo("tree", true)); // 40
        tiles.add(new TileInfo("hut", false));
        tiles.add(new TileInfo("floor01", false));
        tiles.add(new TileInfo("table01", true));
        tiles.add(new TileInfo("table02", true));
    }

    static { // initialize maps
        maps.add("/maps/worldV3.txt");
        maps.add("/maps/interior01.txt");
        maps.add("/maps/mydungeon01.txt");
        maps.add("/maps/mydungeon02.txt");
    }

    public static String getName(int index) {
        return tiles.get(index).name;
    }

    public static boolean getCollision(int index) {
        return tiles.get(index).collision;
    }

    public static int getTileCount() {
        return tiles.size();
    }

    public static String getMap(int index) {
        return maps.get(index);
    }

    public static int getMapCount() {
        return maps.size();
    }
}

class TileInfo {
    String name;
    boolean collision;

    TileInfo(String name, boolean collision) {
        this.name = name;
        this.collision = collision;
    }
}