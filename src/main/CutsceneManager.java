package main;

import java.awt.Graphics2D;

import entity.PlayerDummy;
import monster.MON_SkeletonLord;
import object.OBJ_Door_Iron;

public class CutsceneManager {
    GamePanel gp;
    Graphics2D g2;
    public int sceneNum;
    public int scenePhase;

    public final int NA = 0;
    public final int skeletonLord = 1;
    public CutsceneManager(GamePanel gp){
        this.gp = gp;
    }
    public void draw(Graphics2D g2){
        this.g2 = g2;
        switch (sceneNum) {
            case skeletonLord: scene_skeletonLord(); break;
        }
    }
    public void scene_skeletonLord() {

        // Phase 0: 初始化 (关门、放Dummy、玩家隐身)
        if (scenePhase == 0) {
            gp.bossBattleOn = true;

            // 关门
            for (int i = 0; i < gp.obj[1].length; i++) {
                if (gp.obj[gp.currentMap][i] == null) {
                    gp.obj[gp.currentMap][i] = new OBJ_Door_Iron(gp);
                    gp.obj[gp.currentMap][i].worldX = gp.tileSize * 25;
                    gp.obj[gp.currentMap][i].worldY = gp.tileSize * 28;
                    gp.obj[gp.currentMap][i].temp = true;
                    gp.playSE(21);
                    break;
                }
            }
            // 给dummy找一个空位
            for (int i = 0; i < gp.npc[1].length; i++) {
                if (gp.npc[gp.currentMap][i] == null) {
                    gp.npc[gp.currentMap][i] = new PlayerDummy(gp);
                    gp.npc[gp.currentMap][i].worldX = gp.player.worldX;
                    gp.npc[gp.currentMap][i].worldY = gp.player.worldY;
                    gp.npc[gp.currentMap][i].direction = gp.player.direction;
                    break;
                }
            }

            gp.player.drawing = false;
            scenePhase++;
        }

        // Phase 1: 镜头上移
        if (scenePhase == 1) {
            gp.player.worldY -= 2;
            if (gp.player.worldY < gp.tileSize * 16) {
                scenePhase++;
            }
        }

        // Phase 2: 准备对话
        if (scenePhase == 2) {
            for (int i = 0; i < gp.monster[1].length; i++) {
                if (gp.monster[gp.currentMap][i] != null &&
                        gp.monster[gp.currentMap][i].name.equals(MON_SkeletonLord.monName)) {

                    // 注意：这里不要设置 sleep = false，否则 Boss 可能会开始根据 AI 转向或移动
                    // 我们只设置 UI 目标
                    gp.ui.npc = gp.monster[gp.currentMap][i];
                    scenePhase++;
                    gp.ui.npc.speak(); 
                    break;
                }
            }
        }

        // Phase 3: 对话进行中
        if (scenePhase == 3) {
            
            // 持续绘制对话框
            gp.ui.drawDialogueScreen();

            // 检测按键
            if (gp.keyH.enterPressed) {
                // 如果一段话还没说完（打字机效果没走完），这部分逻辑通常在 UI.drawDialogueScreen 里处理
                // 这里假设 UI 已经显示完毕，按回车翻页
                
                // 检查是否还有下一句对话
                // 注意：这里假设你的 dialogues 数组长度正好是 3，或者检测 null
                if (gp.ui.npc.dialogues[gp.ui.npc.dialogueIndex] != null) {
                    gp.ui.npc.speak(); // 翻页，说下一句
                } else {
                    // 如果下一句是 null，说明说完了
                    scenePhase++;
                    gp.ui.npc.dialogueIndex = 0; // 重置索引，防止下次报错
                }
                
                gp.keyH.enterPressed = false; // 消耗按键，防止连点
            }
        }

        // Phase 4: 战斗开始
        if (scenePhase == 4) {
            // 恢复玩家控制
            gp.gameState = gp.playState;

            // 唤醒 Boss
            for (int i = 0; i < gp.monster[1].length; i++) {
                if (gp.monster[gp.currentMap][i] != null &&
                        gp.monster[gp.currentMap][i].name.equals(MON_SkeletonLord.monName)) {

                    gp.monster[gp.currentMap][i].sleep = false; // Boss 醒来，AI 开始运作
                    break;
                }
            }

            // 恢复玩家位置并删除 Dummy
            for (int i = 0; i < gp.npc[1].length; i++) {
                if (gp.npc[gp.currentMap][i] != null && 
                    gp.npc[gp.currentMap][i].name.equals(PlayerDummy.npcName)) {
                    
                    gp.player.worldX = gp.npc[gp.currentMap][i].worldX;
                    gp.player.worldY = gp.npc[gp.currentMap][i].worldY;
                    gp.npc[gp.currentMap][i] = null; // 删除假人
                    break;
                }
            }

            gp.player.drawing = true; // 重新画出玩家
            
            // 结束过场
            sceneNum = NA;
            scenePhase = 0;
            gp.csManager.sceneNum = gp.csManager.NA;

            // 换音乐
            gp.stopMusic();
            gp.playMusic(22);
        }
    }
}
