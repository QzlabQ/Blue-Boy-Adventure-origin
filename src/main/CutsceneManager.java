package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

import entity.PlayerDummy;
import monster.MON_SkeletonLord;
import object.OBJ_Blueheart;
import object.OBJ_Door_Iron;

public class CutsceneManager {
    GamePanel gp;
    Graphics2D g2;
    public int sceneNum;
    public int scenePhase;
    int counter = 0;
    float alpha = 0f;
    int y;
    String endCredit;

    public final int NA = 0;
    public final int skeletonLord = 1;
    public final int ending = 2;
    public CutsceneManager(GamePanel gp){
        this.gp = gp;
        endCredit = "DEVELOPMENT TEAM\n"
                  + "Zhang Chi (@QzlabQ)  Huang Haicheng (@CodeAstronauth)\n"
                  + "Hu Yunfan (@Qwqwqwert)  Fu Qi (@SteveFauci)\n"
                  + "\n\n\n\n\n\n\n\n\n\n\n" 
                  + "Lead Engine Architect & Project Manager\n"
                  + "Zhang Chi (@QzlabQ)\n"
                  + "\n\n"
                  + "Core Gameplay Implementation\n"
                  + "Huang Haicheng (@CodeAstronauth)\n"
                  + "\n\n"
                  + "Tools Developer & System Polish\n"
                  + "Hu Yunfan (@Qwqwqwert)\n"
                  + "\n\n"
                  + "Level Design & Boss Mechanics\n"
                  + "Fu Qi (@SteveFauci)\n"
                  + "\n\n\n\n"
                  + "ACKNOWLEDGEMENT\n"
                  + "\n\n"
                  + "Original Tutorial & Assets\n"
                  + "RyiSnow (YouTube)\n"
                  + "\n\n\n\n"
                  + "SPECIAL THANKS\n"
                  + "\n\n"
                  + "Tester Name 1\n" // 这里填测试人员名字
                  + "Tester Name 2\n" // 这里填测试人员名字
                  + "\n\n\n\n\n\n"
                  + "Thank you for playing!"
                  + "\n\n"
                  + "December 2025"
                  + "\n\n"
                  + "Beihang University, School of Software";
    }
    public void draw(Graphics2D g2){
        this.g2 = g2;
        switch (sceneNum) {
            case skeletonLord: scene_skeletonLord(); break;
            case ending: scene_ending();break;
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
    public void scene_ending(){
        
        // Phase 0: 初始化
        if(scenePhase == 0){
            gp.stopMusic();
            
            // 注意：这里新建了一个 OBJ_Blueheart，记得确保它的 dialogues 已经初始化了
            // 建议在 OBJ_Blueheart 的构造函数里直接调用 setDialogues()
            gp.ui.npc = new OBJ_Blueheart(gp); 
            
            scenePhase++;
            // gp.ui.npc.speak(); 
        }
        
        // Phase 1: 显示获得物品的对话
        if(scenePhase == 1){
            gp.ui.drawDialogueScreen();
            gp.ui.npc.speak(); 

            if(gp.keyH.enterPressed){
                // 如果话说完了，进入下一阶段
                // 这里的逻辑可以简化，假设只有一句“你获得了蓝心”
                if(gp.ui.npc.dialogues[gp.ui.npc.dialogueIndex] != null){
                     gp.ui.npc.speak();
                } else {
                     scenePhase++; // 进入 Phase 2
                     gp.ui.npc.dialogueIndex = 0;
                }
                gp.keyH.enterPressed = false;
            }
        }
        
        // Phase 2: 播放音效
        if(scenePhase == 2){
            gp.playSE(4); // 胜利音效
            scenePhase++;
        }
        
        // Phase 3: 等待 5 秒
        if(scenePhase == 3){
            if(counterReached(300) == true){ 
                scenePhase++;
            }
        }
        
        // Phase 4: 渐黑转场
        if(scenePhase == 4){
            alpha += 0.005f; 
            if(alpha > 1f){
                alpha = 1f;
            }
            drawBlackBackground(alpha);
            
            if(alpha == 1f){
                alpha = 0;
                scenePhase++;
            }
        }
        
        // Phase 5: 游戏结束显示文本
        if(scenePhase == 5){
             drawBlackBackground(1f); // 保持全黑
            alpha += 0.005f; 
            if(alpha > 1f){
                alpha = 1f;
            }
            String text = "After a fierce battle with the Skeleton Lord,\n"
                        + "the Blue Boy finally found the legendary treasure.\n" 
                        + "But his journey does not end here.\n"
                        + "The true adventure has just begun.";
            
            drawString(alpha, 38f, 200, text, 70);
            // 以上文字放7秒
            if(counterReached(420) == true){
                gp.playMusic(0);
                scenePhase++;
            }
        }

        // Phase 6: 显示标题
        if(scenePhase == 6){
            drawBlackBackground(1f); // 保持全黑
            drawString(1f, 120f, gp.screenHeight / 2, "Blue Boy Adventure", 40);
            // 以上文字放5秒
            if(counterReached(300) == true){
                scenePhase++;
            }
        }

        // Phase 7: credit
        if(scenePhase == 7){
            drawBlackBackground(1f); // 保持全黑

            y = gp.screenHeight * 2 / 5;
            drawString(1f, 38f, gp.screenHeight * 2 / 5, endCredit, 40);
            // 以上文字放5秒
            if(counterReached(300) == true){
                scenePhase++;
            }
        }
        // Phase 8: credit 滚动
        if(scenePhase == 8){
            drawBlackBackground(1f);
            
            // 1. 滚动字幕
            y--;
            drawString(1f, 38f, y, endCredit, 40);
            
            // 2. 固定显示的提示 (放在屏幕底部)
            // 单独画一行，不随 y 滚动
            String exitText = "Press ENTER to return to Title";
            g2.setFont(g2.getFont().deriveFont(24f)); // 字体稍微小一点
            g2.setColor(Color.gray); // 颜色暗一点，不喧宾夺主
            int x = gp.ui.getXforCenteredText(exitText);
            g2.drawString(exitText, x, gp.screenHeight - 40); 

            if(gp.keyH.enterPressed){
                 sceneNum = NA;
                 scenePhase = 0;
                 gp.gameState = gp.titleState;
                 gp.resetGame(true);
                 gp.stopMusic();
            }
        }
    }

    // 一个计时器，用于等待
    public boolean counterReached(int target){
        boolean counterReached = false;
        counter++;
        if(counter > target){
            counterReached = true;
            counter = 0;
        }
        return counterReached;
    }
    public void drawBlackBackground(float alpha){
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setColor(Color.black);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
    public void drawString(float alpha, float fontSize, int y, String text, int lineHeight){
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(fontSize));
        for(String line: text.split("\n")){
            int x = gp.ui.getXforCenteredText(line);
            g2.drawString(line, x, y);
            y += lineHeight;
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        
    }
}
