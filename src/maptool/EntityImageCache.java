package maptool;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class EntityImageCache {
    private HashMap<String, BufferedImage> entityImages = new HashMap<>();

    public void loadEntityImages() {
        String[] entityTypes = {
                "OBJ_Coin_Bronze","OBJ_Blueheart", "OBJ_Key", "OBJ_Tent", "OBJ_Axe", "OBJ_Shield_Blue",
                "OBJ_Potion_Red", "OBJ_ManaCrystal", "OBJ_Door", "OBJ_Chest", "OBJ_Lantern",
                "OBJ_Pickaxe", "OBJ_Door_Iron", "OBJ_Heart", "OBJ_Fireball", "OBJ_Rock",
                "OBJ_Sword_Normal", "OBJ_Boots",
                "NPC_OldMan", "NPC_Merchant", "NPC_BigRock",
                "MON_GreenSlime", "MON_Orc", "MON_Bat", "MON_RedSlime", "MON_SkeletonLord",
                "IT_DryTree", "IT_DestructibleWall", "IT_MetalPlate", "IT_Trunk"
        };

        for (String entityType : entityTypes) {
            try {
                String imagePath = getEntityImagePath(entityType);
                if (imagePath != null) {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        BufferedImage image = ImageIO.read(imageFile);
                        entityImages.put(entityType, image);
                    } else {
                        System.out.println("警告: 实体图像文件不存在: " + imagePath);
                    }
                }
            } catch (IOException e) {
                System.out.println("无法加载实体图像: " + entityType);
            }
        }
    }

    private String getEntityImagePath(String entityType) {
        switch (entityType) {
            case "OBJ_Coin_Bronze":
                return "res/objects/coin_bronze.png";
            case "OBJ_Key":
                return "res/objects/key.png";
            case "OBJ_Tent":
                return "res/objects/tent.png";
            case "OBJ_Axe":
                return "res/objects/axe.png";
            case "OBJ_Shield_Blue":
                return "res/objects/shield_blue.png";
            case "OBJ_Potion_Red":
                return "res/objects/potion_red.png";
            case "OBJ_ManaCrystal":
                return "res/objects/manacrystal_full.png";
            case "OBJ_Door":
                return "res/objects/door.png";
            case "OBJ_Chest":
                return "res/objects/chest.png";
            case "OBJ_Lantern":
                return "res/objects/lantern.png";
            case "OBJ_Pickaxe":
                return "res/objects/pickaxe.png";
            case "OBJ_Door_Iron":
                return "res/objects/door_iron.png";
            case "OBJ_Heart":
                return "res/objects/heart_full.png";
            case "OBJ_Fireball":
                return "res/projectile/fireball_left_1.png";
            case "OBJ_Rock":
                return "res/projectile/rock_down_1.png";
            case "OBJ_Sword_Normal":
                return "res/objects/sword_normal.png";
            case "OBJ_Boots":
                return "res/objects/boots.png";

            case "NPC_OldMan":
                return "res/npc/oldman_down_1.png";
            case "NPC_Merchant":
                return "res/npc/merchant_down_1.png";
            case "NPC_BigRock":
                return "res/npc/bigrock.png";

            case "MON_GreenSlime":
                return "res/monster/greenslime_down_1.png";
            case "MON_Orc":
                return "res/monster/orc_down_1.png";
            case "MON_Bat":
                return "res/monster/bat_down_1.png";
            case "MON_RedSlime":
                return "res/monster/redslime_down_1.png";
            case "MON_SkeletonLord":
                return "res/monster/skeletonlord_down_1.png";

            case "IT_DryTree":
                return "res/tiles_interactive/drytree.png";
            case "IT_DestructibleWall":
                return "res/tiles_interactive/destructibleWall.png";
            case "IT_MetalPlate":
                return "res/tiles_interactive/metalplate.png";
            case "IT_Trunk":
                return "res/tiles_interactive/trunk.png";

            default:
                return null;
        }
    }

    public BufferedImage getImage(String entityType) {
        return entityImages.get(entityType);
    }
}