package monster;

import entity.Entity;
import main.GamePanel;
import object.OBJ_Blueheart;
import object.OBJ_Coin_Bronze;
import object.OBJ_Door_Iron;
import object.OBJ_Heart;
import object.OBJ_ManaCrystal;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import data.Progress;

public class MON_SkeletonLord extends Entity {

    GamePanel gp;
    public static final String monName = "Skeleton Lord";

    public MON_SkeletonLord(GamePanel gp) {
        super(gp);

        this.gp = gp;

        type = type_monster;
        boss = true;
        name = monName;
        defaultSpeed = 1;
        speed = defaultSpeed;
        maxLife = 150;
        life = maxLife;
        attack = 10;
        defense = 2;
        exp = 150;
        knockBackPower = 7;
        sleep = true;

        int size = gp.tileSize * 5;

        // 碰撞体积为下面的四行三列
        // 示意图 怪物贴图为5*5，体积为X的部分
        // .....
        // .XXX.
        // .XXX.
        // .XXX.
        // .XXX.

        solidArea.x = 40;
        solidArea.y = 40;
        solidArea.width = size - 48 * 2;
        solidArea.height = size - 48;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
        attackArea.width = 170;
        attackArea.height = 170;
        motion1_duration = 25;
        motion2_duration = 50;
        getImage();
        getAttackImage();
        setDialogue();
    }

    public void getImage() {
        int i = 5;
        if (inRage == false) {
            up1 = setup("/monster/skeletonlord_up_1", gp.tileSize * i, gp.tileSize * i);
            up2 = setup("/monster/skeletonlord_up_2", gp.tileSize * i, gp.tileSize * i);
            down1 = setup("/monster/skeletonlord_down_1", gp.tileSize * i, gp.tileSize * i);
            down2 = setup("/monster/skeletonlord_down_2", gp.tileSize * i, gp.tileSize * i);
            left1 = setup("/monster/skeletonlord_left_1", gp.tileSize * i, gp.tileSize * i);
            left2 = setup("/monster/skeletonlord_left_2", gp.tileSize * i, gp.tileSize * i);
            right1 = setup("/monster/skeletonlord_right_1", gp.tileSize * i, gp.tileSize * i);
            right2 = setup("/monster/skeletonlord_right_2", gp.tileSize * i, gp.tileSize * i);
        } else {
            up1 = setup("/monster/skeletonlord_phase2_up_1", gp.tileSize * i, gp.tileSize * i);
            up2 = setup("/monster/skeletonlord_phase2_up_2", gp.tileSize * i, gp.tileSize * i);
            down1 = setup("/monster/skeletonlord_phase2_down_1", gp.tileSize * i, gp.tileSize * i);
            down2 = setup("/monster/skeletonlord_phase2_down_2", gp.tileSize * i, gp.tileSize * i);
            left1 = setup("/monster/skeletonlord_phase2_left_1", gp.tileSize * i, gp.tileSize * i);
            left2 = setup("/monster/skeletonlord_phase2_left_2", gp.tileSize * i, gp.tileSize * i);
            right1 = setup("/monster/skeletonlord_phase2_right_1", gp.tileSize * i, gp.tileSize * i);
            right2 = setup("/monster/skeletonlord_phase2_right_2", gp.tileSize * i, gp.tileSize * i);
        }
    }

    public void getAttackImage() {
        int i = 5;
        if (inRage == false) {
            attackUp1 = setup("/monster/skeletonlord_attack_up_1", gp.tileSize * i, gp.tileSize * 2 * i);
            attackUp2 = setup("/monster/skeletonlord_attack_up_2", gp.tileSize * i, gp.tileSize * 2 * i);
            attackDown1 = setup("/monster/skeletonlord_attack_down_1", gp.tileSize * i, gp.tileSize * 2 * i);
            attackDown2 = setup("/monster/skeletonlord_attack_down_2", gp.tileSize * i, gp.tileSize * 2 * i);
            attackLeft1 = setup("/monster/skeletonlord_attack_left_1", gp.tileSize * 2 * i, gp.tileSize * i);
            attackLeft2 = setup("/monster/skeletonlord_attack_left_2", gp.tileSize * 2 * i, gp.tileSize * i);
            attackRight1 = setup("/monster/skeletonlord_attack_right_1", gp.tileSize * 2 * i, gp.tileSize * i);
            attackRight2 = setup("/monster/skeletonlord_attack_right_2", gp.tileSize * 2 * i, gp.tileSize * i);
        } else {
            attackUp1 = setup("/monster/skeletonlord_phase2_attack_up_1", gp.tileSize * i, gp.tileSize * 2 * i);
            attackUp2 = setup("/monster/skeletonlord_phase2_attack_up_2", gp.tileSize * i, gp.tileSize * 2 * i);
            attackDown1 = setup("/monster/skeletonlord_phase2_attack_down_1", gp.tileSize * i, gp.tileSize * 2 * i);
            attackDown2 = setup("/monster/skeletonlord_phase2_attack_down_2", gp.tileSize * i, gp.tileSize * 2 * i);
            attackLeft1 = setup("/monster/skeletonlord_phase2_attack_left_1", gp.tileSize * 2 * i, gp.tileSize * i);
            attackLeft2 = setup("/monster/skeletonlord_phase2_attack_left_2", gp.tileSize * 2 * i, gp.tileSize * i);
            attackRight1 = setup("/monster/skeletonlord_phase2_attack_right_1", gp.tileSize * 2 * i, gp.tileSize * i);
            attackRight2 = setup("/monster/skeletonlord_phase2_attack_right_2", gp.tileSize * 2 * i, gp.tileSize * i);

        }

    }

    public void setDialogue() {
        dialogues[0] = "No one can steal my treasure!";
        dialogues[1] = "You will DIE here!";
        dialogues[2] = "WELCOME TO YOUR DOOM!";
    }

    public void setAction() {

        if (sleep == true) {
            return;
        }
        // 二阶段：血量折半时，移速加快，攻击加倍
        if (inRage == false && life < maxLife / 2) {
            inRage = true;
            getImage();
            getAttackImage();
            defaultSpeed++;
            speed = defaultSpeed;
            attack *= 2;
        }
        if (getTileDistance(gp.player) < 10) {
            moveTowordPlayer(60);
        } else {
            getRandomDirection(120);
        }
        if (attacking == false) {
            // 靠近boss才会追
            checkAttackOrNot(60, gp.tileSize * 7, gp.tileSize * 5);
        }
    }

    public void damageReaction() {
        actionLockCounter = 0;
    }

    public void checkDrop() {

        gp.bossBattleOn = false;
        gp.stopMusic();
        gp.playMusic(19);
        Progress.skeletonLordDefeated = true;

        for (int i = 0; i < gp.obj[1].length; i++) {
            if (gp.obj[gp.currentMap][i] != null && gp.obj[gp.currentMap][i].name.equals(OBJ_Door_Iron.objName)) {
                gp.playSE(21);
                gp.obj[gp.currentMap][i] = null;
            }
        }

        // 这里有石山代码， 我绕过了地图编辑器，直接把钻石作为掉落物放到指定坐标。。
        for (int i = 0; i < gp.obj[1].length; i++) {
            if (gp.obj[gp.currentMap][i] == null) {
                gp.obj[gp.currentMap][i] = new OBJ_Blueheart(gp);
                gp.obj[gp.currentMap][i].worldX = gp.tileSize * 25; // the dead monster's position
                gp.obj[gp.currentMap][i].worldY = gp.tileSize * 8;
                break;
            }
        }

        // Cast a die
        int i = new Random().nextInt(100) + 1;

        // Set the monster drop
        if (i < 50) {
            dropItem(new OBJ_Coin_Bronze(gp));
        }
        if (i >= 50 && i < 75) {
            dropItem(new OBJ_Heart(gp));
        }
        if (i >= 75 && i < 100) {
            dropItem(new OBJ_ManaCrystal(gp));
        }
    }

    @Override
    public void draw(Graphics2D g2) {
        super.draw(g2);

        if (attacking && spriteCounter < motion1_duration) {
            int tempWorldX = worldX;
            int tempWorldY = worldY;

            switch (direction) {
                case "up":
                    tempWorldY -= attackArea.height;
                    break;
                case "down":
                    tempWorldY += attackArea.height;
                    break;
                case "left":
                    tempWorldX -= attackArea.width;
                    break;
                case "right":
                    tempWorldX += attackArea.width;
                    break;
            }

            int screenX = tempWorldX - gp.player.worldX + gp.player.screenX;
            int screenY = tempWorldY - gp.player.worldY + gp.player.screenY;

            int drawX = screenX + solidArea.x;
            int drawY = screenY + solidArea.y;
            int drawWidth = attackArea.width;
            int drawHeight = attackArea.height;

            if (spriteCounter % 10 < 5) {
                g2.setColor(new Color(255, 0, 0, 100));
                g2.fillRect(drawX, drawY, drawWidth, drawHeight);
                g2.setColor(Color.RED);
                g2.drawRect(drawX, drawY, drawWidth, drawHeight);
            }
        }
    }
}
