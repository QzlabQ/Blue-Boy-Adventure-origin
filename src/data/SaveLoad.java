package data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import entity.Entity;
import main.GamePanel;
import object.OBJ_Axe;
import object.OBJ_Boots;
import object.OBJ_Chest;
import object.OBJ_Coin_Bronze;
import object.OBJ_Door;
import object.OBJ_Fireball;
import object.OBJ_Heart;
import object.OBJ_Key;
import object.OBJ_Lantern;
import object.OBJ_ManaCrystal;
import object.OBJ_Potion_Red;
import object.OBJ_Rock;
import object.OBJ_Shield_Blue;
import object.OBJ_Shield_Wood;
import object.OBJ_Sword_Normal;
import object.OBJ_Tent;

public class SaveLoad {
    GamePanel gp;
    public SaveLoad(GamePanel gp){
        this.gp = gp;
    }
    public Entity getObject(String itemName){
        Entity obj = null;
        switch (itemName) {
            case "Woodcutter's Axe": obj = new OBJ_Axe(gp); break;
            case "Boots": obj = new OBJ_Boots(gp); break;
            case "Key": obj = new OBJ_Key(gp); break;
            case "Lantern": obj = new OBJ_Lantern(gp); break;
            case "Red Potion": obj = new OBJ_Potion_Red(gp); break;
            case "Blue Shield": obj = new OBJ_Shield_Blue(gp); break;
            case "Wood Shield": obj = new OBJ_Shield_Wood(gp); break;
            case "Normal Sword": obj = new OBJ_Sword_Normal(gp); break;
            case "Tent": obj = new OBJ_Tent(gp); break;
            case "Door": obj = new OBJ_Door(gp); break;
            case "Chest": obj = new OBJ_Chest(gp); break;
            case "Bronze Coin": obj = new OBJ_Coin_Bronze(gp); break;
            case "Mana Crystal": obj = new OBJ_ManaCrystal(gp); break;
            case "Heart": obj = new OBJ_Heart(gp); break; // 怪物可能会掉落爱心，建议加上
            case "Fireball": obj = new OBJ_Fireball(gp); break; 
        }
        return obj;
    }
    public void save(){
        try{
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("save")));
            DataStorage ds = new DataStorage();

            // stats
            ds.level = gp.player.level;
            ds.maxLife = gp.player.maxLife;
            ds.life = gp.player.life;
            ds.maxMana = gp.player.maxMana;
            ds.mana = gp.player.mana;
            ds.strength = gp.player.strength;
            ds.exp = gp.player.exp;
            ds.nextLevelExp = gp.player.nextLevelExp;
            ds.dexterity = gp.player.dexterity;
            ds.coin = gp.player.coin;

            // inventory
            for(int i = 0; i < gp.player.inventory.size(); i++){
                ds.itemNames.add(gp.player.inventory.get(i).name);
                ds.itemAmounts.add(gp.player.inventory.get(i).amount);
            }

            // equipment
            ds.currentWeaponSlot = gp.player.getCurrentWeaponSlot();
            ds.currentShieldSlot = gp.player.getCurrentShieldSlot();

            // object on map
            ds.mapObjectNames = new String[gp.maxMap][gp.obj[1].length];
            ds.mapObjectWorldX = new int[gp.maxMap][gp.obj[1].length];
            ds.mapObjectWorldY = new int[gp.maxMap][gp.obj[1].length];
            ds.mapObjectLootNames = new String[gp.maxMap][gp.obj[1].length];
            ds.mapObjectOpened = new boolean[gp.maxMap][gp.obj[1].length];

            for(int mapNum = 0; mapNum < gp.maxMap; mapNum++){
                for(int i = 0; i < gp.obj[1].length; i++){
                    if(gp.obj[mapNum][i] == null){
                        ds.mapObjectNames[mapNum][i] = "NA";
                    }
                    else{
                        ds.mapObjectNames[mapNum][i] = gp.obj[mapNum][i].name;
                        ds.mapObjectWorldX[mapNum][i] = gp.obj[mapNum][i].worldX;
                        ds.mapObjectWorldY[mapNum][i] = gp.obj[mapNum][i].worldY;
                        if(gp.obj[mapNum][i].loot != null){
                            ds.mapObjectLootNames[mapNum][i] = gp.obj[mapNum][i].loot.name;
                        }
                        ds.mapObjectOpened[mapNum][i] = gp.obj[mapNum][i].opened;
                    }
                }
            }


            oos.writeObject(ds);
        }
        catch(Exception e){
            System.out.println("Save Exception!");
            e.printStackTrace();
        }
    }
    public void load(){
        try{
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("save")));
            DataStorage ds = (DataStorage)ois.readObject();
            gp.player.level = ds.level;
            gp.player.maxLife = ds.maxLife;
            gp.player.life = ds.life;
            gp.player.maxMana  = ds.maxMana;
            gp.player.mana = ds.mana;
            gp.player.strength = ds.strength;
            gp.player.dexterity = ds.dexterity;
            gp.player.exp = ds.exp;
            gp.player.nextLevelExp = ds.nextLevelExp ;
            gp.player.coin = ds.coin;

            // 读取背包
            gp.player.inventory.clear();
            for(int i = 0; i < ds.itemNames.size(); i++){
                Entity item = getObject(ds.itemNames.get(i));
                // 只有当 item 不为 null 时才加入背包
                if(item != null){
                    item.amount = ds.itemAmounts.get(i);
                    gp.player.inventory.add(item);
                } else {
                    // 如果 item 是 null，说明存档里有个物品代码里没注册，跳过它防止炸游戏
                    System.out.println("Skipped loading null item."); 
                }
            }
            // System.out.println("Game Loaded!"); // 提示读取成功

            gp.player.currentWeapon = gp.player.inventory.get(ds.currentWeaponSlot);
            gp.player.currentShield = gp.player.inventory.get(ds.currentShieldSlot);
            gp.player.getAttack();
            gp.player.getDefense();
            gp.player.getAttackImage();


            for(int mapNum = 0; mapNum < gp.maxMap; mapNum++){
                for(int i = 0; i < gp.obj[1].length; i++){
                    if(ds.mapObjectNames[mapNum][i].equals("NA")){
                        gp.obj[mapNum][i] = null;
                    }
                    else{
                        Entity obj = getObject(ds.mapObjectNames[mapNum][i]);
                        
                        // 如果 getObject 因为名字拼写错误或漏写 case 返回了 null，
                        // 这里必须跳过，否则下面 obj.worldX 就会炸
                        if(obj == null) {
                            System.out.println("Warning: Loading skipped unknown object: " + ds.mapObjectNames[mapNum][i]);
                            continue; 
                        }
                        
                        gp.obj[mapNum][i] = obj;
                        gp.obj[mapNum][i].worldX = ds.mapObjectWorldX[mapNum][i];
                        gp.obj[mapNum][i].worldY = ds.mapObjectWorldY[mapNum][i];
                        
                        if(ds.mapObjectLootNames[mapNum][i] != null){
                            gp.obj[mapNum][i].loot = getObject(ds.mapObjectLootNames[mapNum][i]);
                        }
                        
                        gp.obj[mapNum][i].opened = ds.mapObjectOpened[mapNum][i];
                        if(gp.obj[mapNum][i].opened == true){
                            gp.obj[mapNum][i].down1 = gp.obj[mapNum][i].image2;
                        }
                    }
                }
            }
        }
        catch(Exception e){
            System.out.println("Load Exception!");
            e.printStackTrace();
        }
    }
}
