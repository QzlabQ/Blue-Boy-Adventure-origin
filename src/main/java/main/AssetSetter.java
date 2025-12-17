package main;

import entity.*;
import monster.*;
import object.*;
import tile_interactive.*;

import java.io.*;
import java.util.*;

import data.Progress;

public class AssetSetter {

    GamePanel gp;
    private List<EntityData>[] entityData;

    // 存储实体数据的内部类
    private static class EntityData {
        String category;
        String typeName;
        int x;
        int y;
        String extra;

        EntityData(String category, String typeName, int x, int y, String extra) {
            this.category = category;
            this.typeName = typeName;
            this.x = x;
            this.y = y;
            this.extra = extra;
        }
    }

    public AssetSetter(GamePanel gp) {
        this.gp = gp;
        // 初始化实体数据数组
        entityData = new ArrayList[gp.maxMap];
        for (int i = 0; i < gp.maxMap; i++) {
            entityData[i] = new ArrayList<>();
        }

        // 加载所有地图的实体数据
        loadAllMapEntities();
    }

    // 加载所有地图的实体数据
    private void loadAllMapEntities() {
        // 根据MapData中的地图加载顺序来加载实体
        for (int i = 0; i < Math.min(gp.maxMap, MapData.getMapCount()); i++) {
            loadMapEntities(MapData.getMap(i), i);
        }
    }

    // 从地图文件中加载实体数据
    public void loadMapEntities(String filepath, int mapIndex) {
        try {
            // 清空之前的数据
            entityData[mapIndex].clear();

            InputStream is = getClass().getResourceAsStream(filepath);
            if (is == null) {
                System.err.println("无法找到地图文件: " + filepath);
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            boolean entitySection = false;

            // 读取文件直到找到#Entity标记
            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.equals("#Entity")) {
                    entitySection = true;
                    continue;
                }

                // 如果进入了实体部分，则解析实体数据
                if (entitySection) {
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue; // 跳过空行和注释行
                    }

                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        String category = parts[0].trim();
                        String typeName = parts[1].trim();
                        int x = Integer.parseInt(parts[2].trim());
                        int y = Integer.parseInt(parts[3].trim());
                        String extra = parts.length > 4 ? parts[4].trim() : null;

                        entityData[mapIndex].add(new EntityData(category, typeName, x, y, extra));
                    }
                }
            }

            br.close();
        } catch (Exception e) {
            System.err.println("加载地图实体时出错: " + filepath);
            e.printStackTrace();
        }
    }

    // 加载所有类型的实体
    public void setAll() {
        setObject();
        setNPC();
        setMonster();
        setInteractiveTile();
    }

    // 加载OBJ类实体
    public void setObject() {
        for (int mapNum = 0; mapNum < gp.maxMap; mapNum++) {
            int index = 0;
            for (EntityData data : entityData[mapNum]) {
                if (!data.category.equals("OBJ"))
                    continue;

                if (index >= gp.obj[mapNum].length) {
                    System.err.println("地图 " + mapNum + " 的OBJ实体数组已满");
                    break;
                }

                Entity obj = createObject(data.typeName);
                if (obj != null) {
                    obj.worldX = data.x * gp.tileSize;
                    obj.worldY = data.y * gp.tileSize;

                    // 特殊处理箱子
                    if (obj instanceof OBJ_Chest && data.extra != null && !data.extra.isEmpty()) {
                        Entity loot = createObject(data.extra);
                        if (loot != null) {
                            ((OBJ_Chest) obj).SetLoot(loot);
                        } else {
                            System.err.println("无法创建箱子内的物品: " + data.extra + " 在位置 (" + data.x + ", " + data.y + ")");
                        }
                    }

                    gp.obj[mapNum][index] = obj;
                    index++;
                }
            }
        }
    }

    // 加载NPC类实体
    public void setNPC() {
        for (int mapNum = 0; mapNum < gp.maxMap; mapNum++) {
            int index = 0;
            for (EntityData data : entityData[mapNum]) {
                if (!data.category.equals("NPC"))
                    continue;

                if (index >= gp.npc[mapNum].length) {
                    System.err.println("地图 " + mapNum + " 的NPC实体数组已满");
                    break;
                }

                Entity npc = createNPC(data.typeName);
                if (npc != null) {
                    npc.worldX = data.x * gp.tileSize;
                    npc.worldY = data.y * gp.tileSize;

                    gp.npc[mapNum][index] = npc;
                    index++;
                }
            }
        }
    }

    // 加载Monster类实体
    public void setMonster() {
        for (int mapNum = 0; mapNum < gp.maxMap; mapNum++) {
            int index = 0;
            for (EntityData data : entityData[mapNum]) {
                if (!data.category.equals("MON"))
                    continue;

                if (index >= gp.monster[mapNum].length) {
                    System.err.println("地图 " + mapNum + " 的Monster实体数组已满");
                    break;
                }

                Entity monster = createMonster(data.typeName);
                if (monster != null) {
                    if (monster.name.equals(MON_SkeletonLord.monName) && Progress.skeletonLordDefeated == true) {
                        index++;
                        continue;
                    }
                    monster.worldX = data.x * gp.tileSize;
                    monster.worldY = data.y * gp.tileSize;

                    gp.monster[mapNum][index] = monster;
                    index++;
                }
            }
        }
    }

    // 加载InteractiveTile类实体
    public void setInteractiveTile() {
        for (int mapNum = 0; mapNum < gp.maxMap; mapNum++) {
            int index = 0;
            for (EntityData data : entityData[mapNum]) {
                if (!data.category.equals("IT"))
                    continue;

                if (index >= gp.iTile[mapNum].length) {
                    System.err.println("地图 " + mapNum + " 的InteractiveTile实体数组已满");
                    break;
                }

                InteractiveTile iTile = createInteractiveTile(data.typeName, data.x, data.y);
                if (iTile != null) {
                    gp.iTile[mapNum][index] = iTile;
                    index++;
                }
            }
        }
    }

    // 创建OBJ对象
    private Entity createObject(String typeName) {
        try {
            switch (typeName) {
                case "OBJ_Boots":
                    return new OBJ_Boots(gp);
                case "OBJ_AirWall":
                    return new OBJ_AirWall(gp);
                case "OBJ_Chest":
                    return new OBJ_Chest(gp);
                case "OBJ_Axe":
                    return new OBJ_Axe(gp);
                case "OBJ_Coin_Bronze":
                    return new OBJ_Coin_Bronze(gp);
                case "OBJ_Door_Iron":
                    return new OBJ_Door_Iron(gp);
                case "OBJ_Door":
                    return new OBJ_Door(gp);
                case "OBJ_Heart":
                    return new OBJ_Heart(gp);
                case "OBJ_Lantern":
                    return new OBJ_Lantern(gp);
                case "OBJ_Fireball":
                    return new OBJ_Fireball(gp);
                case "OBJ_Key":
                    return new OBJ_Key(gp);
                case "OBJ_ManaCrystal":
                    return new OBJ_ManaCrystal(gp);
                case "OBJ_Pickaxe":
                    return new OBJ_Pickaxe(gp);
                case "OBJ_Potion_Red":
                    return new OBJ_Potion_Red(gp);
                case "OBJ_Rock":
                    return new OBJ_Rock(gp);
                case "OBJ_Shield_Blue":
                    return new OBJ_Shield_Blue(gp);
                case "OBJ_Shield_Wood":
                    return new OBJ_Shield_Wood(gp);
                case "OBJ_Sword_Normal":
                    return new OBJ_Sword_Normal(gp);
                case "OBJ_Tent":
                    return new OBJ_Tent(gp);
                default:
                    System.err.println("未知的OBJ类型: " + typeName);
                    return null;
            }
        } catch (Exception e) {
            System.err.println("创建OBJ对象失败: " + typeName);
            e.printStackTrace();
            return null;
        }
    }

    // 创建NPC对象
    private Entity createNPC(String typeName) {
        try {
            switch (typeName) {
                case "NPC_BigRock":
                    return new NPC_BigRock(gp);
                case "NPC_Merchant":
                    return new NPC_Merchant(gp);
                case "NPC_OldMan":
                    return new NPC_OldMan(gp);
                default:
                    System.err.println("未知的NPC类型: " + typeName);
                    return null;
            }
        } catch (Exception e) {
            System.err.println("创建NPC对象失败: " + typeName);
            e.printStackTrace();
            return null;
        }
    }

    // 创建Monster对象
    private Entity createMonster(String typeName) {
        try {
            switch (typeName) {
                case "MON_Bat":
                    return new MON_Bat(gp);
                case "MON_GreenSlime":
                    return new MON_GreenSlime(gp);
                case "MON_Orc":
                    return new MON_Orc(gp);
                case "MON_RedSlime":
                    return new MON_RedSlime(gp);
                case "MON_SkeletonLord":
                    return new MON_SkeletonLord(gp);
                default:
                    System.err.println("未知的Monster类型: " + typeName);
                    return null;
            }
        } catch (Exception e) {
            System.err.println("创建Monster对象失败: " + typeName);
            e.printStackTrace();
            return null;
        }
    }

    // 创建InteractiveTile对象
    private InteractiveTile createInteractiveTile(String typeName, int x, int y) {
        try {
            switch (typeName) {
                case "IT_DestructibleWall":
                    return new IT_DestructibleWall(gp, x, y);
                case "IT_DryTree":
                    return new IT_DryTree(gp, x, y);
                case "IT_MetalPlate":
                    return new IT_MetalPlate(gp, x, y);
                case "IT_Trunk":
                    return new IT_Trunk(gp, x, y);
                default:
                    System.err.println("未知的InteractiveTile类型: " + typeName);
                    return null;
            }
        } catch (Exception e) {
            System.err.println("创建InteractiveTile对象失败: " + typeName);
            e.printStackTrace();
            return null;
        }
    }
}