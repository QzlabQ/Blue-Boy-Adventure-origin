package entity;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Random;

import main.GamePanel;
import object.OBJ_Door_Iron;
import tile_interactive.IT_MetalPlate;
import tile_interactive.InteractiveTile;

public class NPC_BigRock extends Entity {

    public static final String npcName = "Big Rock";
    public NPC_BigRock(GamePanel gp) {
        super(gp);
        name = npcName;
        direction = "down";
        speed = 4;

        solidArea = new Rectangle();
        solidArea.x = 2;
        solidArea.y = 6;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        solidArea.width = 44;
        solidArea.height = 40;

        getImage();
        setDialogue();
    }

    public void getImage() {

        up1 = setup("/npc/bigrock", gp.tileSize, gp.tileSize);
        up2 = setup("/npc/bigrock", gp.tileSize, gp.tileSize);
        down1 = setup("/npc/bigrock", gp.tileSize, gp.tileSize);
        down2 = setup("/npc/bigrock", gp.tileSize, gp.tileSize);
        left1 = setup("/npc/bigrock", gp.tileSize, gp.tileSize);
        left2 = setup("/npc/bigrock", gp.tileSize, gp.tileSize);
        right1 = setup("/npc/bigrock", gp.tileSize, gp.tileSize);
        right2 = setup("/npc/bigrock", gp.tileSize, gp.tileSize);

    }

    public void setDialogue() {

        dialogues[0] = "It's a GIANT rock.";

    }

    public void setAction() {
    }

    public void update() {
    }

    public void speak() {
        super.speak();
    }

    public void move(String d){
        this.direction = d;
        checkCollision();
        if(collisionOn == false){
            switch (direction) {
                case "up" : worldY -= speed; break;
                case "down" : worldY += speed; break;
                case "left" : worldX -= speed; break;
                case "right" : worldX += speed; break;
            }
        }
        detectPlate();
    }
    
    public void detectPlate(){
        ArrayList<InteractiveTile> plateList = new ArrayList<>();
        ArrayList<Entity> rockList = new ArrayList<>(); 

        // plateList
        for(int i = 0; i < gp.iTile[1].length; i++){
            if(gp.iTile[gp.currentMap][i] != null &&
                    gp.iTile[gp.currentMap][i].name != null &&
                    gp.iTile[gp.currentMap][i].name.equals(IT_MetalPlate.itName)){
                plateList.add(gp.iTile[gp.currentMap][i]);
            }
        }
        // rockList
        for(int i = 0; i < gp.npc[1].length; i++){
            if(gp.npc[gp.currentMap][i] != null &&
                    gp.npc[gp.currentMap][i].name.equals(NPC_BigRock.npcName)){
                rockList.add(gp.npc[gp.currentMap][i]);
            }
        }

        int count = 0;

        // 检查plateList
        for(int i = 0; i < plateList.size(); i++){
            int xDistance = Math.abs(worldX - plateList.get(i).worldX);
            int yDistance = Math.abs(worldY - plateList.get(i).worldY);
            int distance = Math.max(xDistance,yDistance);
            if(distance < 8){
                if(linkedEntity == null){
                    linkedEntity = plateList.get(i);
                    gp.playSE(3);
                }
            } else {
                if(linkedEntity == plateList.get(i)){
                    linkedEntity = null;
                }
            }
        } 

        for(int i = 0; i < rockList.size(); i++){
            // count the rock on the plate 
            if(rockList.get(i).linkedEntity != null){
                count++;
            }
        }

        // 开门
        if(count == rockList.size()){
            // 1. 修正循环长度：使用 gp.obj[1].length 确保是固定的数组长度
            for(int i = 0; i < gp.obj[1].length; i++){
                
                // 2. 修正空指针检查：
                // 先检查 gp.obj[gp.currentMap][i] != null
                // 只有物体存在，才能去检查它的名字 (.name)
                if(gp.obj[gp.currentMap][i] != null && 
                   gp.obj[gp.currentMap][i].name.equals(OBJ_Door_Iron.objName)){
                    
                    gp.obj[gp.currentMap][i] = null; // 删除铁门
                    gp.playSE(21); // 播放开门音效
                }
            }
        }
    }

}
