package main;

import data.Progress;
import entity.Entity;

public class EventHandler {

    GamePanel gp;
    EventRect eventRect[][][];

    int previousEventX, previousEventY;
    boolean canTouchEvent = true;
    int tempMap, tempCol, tempRow;

    public EventHandler(GamePanel gp) {
        this.gp = gp;

        eventRect = new EventRect[gp.maxMap][gp.maxWorldCol][gp.maxWorldRow];

        int map = 0;
        int col = 0;
        int row = 0;
        while (map < gp.maxMap && col < gp.maxWorldCol && row < gp.maxWorldRow) {

            eventRect[map][col][row] = new EventRect();
            eventRect[map][col][row].x = 23;
            eventRect[map][col][row].y = 23;
            eventRect[map][col][row].width = 2;
            eventRect[map][col][row].height = 2;
            eventRect[map][col][row].eventRectDefaultX = eventRect[map][col][row].x;
            eventRect[map][col][row].eventRectDefaultY = eventRect[map][col][row].y;

            col++;
            if (col == gp.maxWorldCol) {
                col = 0;
                row++;

                if (row == gp.maxWorldRow) {
                    row = 0;
                    map++;
                }
            }
        }

    }

    public void checkEvent() {

        // Check if the player character is more than 1 tile away from the last event
        int xDistance = Math.abs(gp.player.worldX - previousEventX);
        int yDistance = Math.abs(gp.player.worldY - previousEventY);
        int distance = Math.max(xDistance, yDistance);
        if (distance > gp.tileSize) {
            canTouchEvent = true;
        }

        if (canTouchEvent == true) {
            if (hit(2, 27, 11, "any") == true) {
                damagePit(gp.dialogueState); // 这个pit没有贴图，可以考虑加上贴图，或者直接注释掉
            } else if (hit(2, 14, 28, "any") == true) {
                damagePit(gp.dialogueState);
            } else if (hit(2, 15, 28, "any") == true) {
                damagePit(gp.dialogueState);
            } else if (hit(2, 15, 40, "any") == true) {
                damagePit(gp.dialogueState);
            } else if (hit(2, 16, 40, "any") == true) {
                damagePit(gp.dialogueState);
            } else if (hit(0, 23, 12, "up") == true) {
                healingPool(gp.dialogueState);
            } else if (hit(0, 13, 38, "any") == true) {
                teleport(1, 12, 13, gp.indoor); // 商人小屋
            } else if (hit(1, 12, 13, "any") == true) {
                teleport(0, 13, 38, gp.outside); // 如果改了商人小屋的坐标，这一行也要改。不然传送回来会卡墙
            } else if (hit(0, 12, 9, "any") == true) {
                // teleport(3, 26, 41, gp.dungeon); // 主世界 传送到 dungeon02
                teleport(2, 9, 41, gp.dungeon); // 主世界 传送到 dungeon01
            } else if (hit(2, 9, 41, "any") == true) {
                teleport(0, 12, 9, gp.outside); // dungeon01 传送到 主世界
            } else if (hit(2, 8, 7, "any") == true) {
                teleport(3, 26, 41, gp.dungeon); // 01 传送到dungeon02
            } else if (hit(3, 26, 41, "any") == true) {
                teleport(2, 8, 7, gp.dungeon); // 02 传送到dungeon01
            } else if (hit(3, 27, 41, "up") == true) {
                bossCheckpoint(gp.dialogueState);
            } else if (hit(3, 25, 27, "any") == true) {
                skeletonLord();
            }
            if (hit(1, 12, 9, "up") == true) {
                speak(gp.npc[1][0]);
            }
        }

    }

    public boolean hit(int map, int col, int row, String reqDirection) {

        boolean hit = false;

        if (map == gp.currentMap) {
            gp.player.solidArea.x = gp.player.worldX + gp.player.solidArea.x;
            gp.player.solidArea.y = gp.player.worldY + gp.player.solidArea.y;
            eventRect[map][col][row].x = col * gp.tileSize + eventRect[map][col][row].x;
            eventRect[map][col][row].y = row * gp.tileSize + eventRect[map][col][row].y;

            if (gp.player.solidArea.intersects(eventRect[map][col][row])
                    && eventRect[map][col][row].eventDone == false) {
                if (gp.player.direction.contentEquals(reqDirection) || reqDirection.contentEquals("any")) {
                    hit = true;

                    previousEventX = gp.player.worldX;
                    previousEventY = gp.player.worldY;
                }
            }

            gp.player.solidArea.x = gp.player.solidAreaDefaultX;
            gp.player.solidArea.y = gp.player.solidAreaDefaultY;
            eventRect[map][col][row].x = eventRect[map][col][row].eventRectDefaultX;
            eventRect[map][col][row].y = eventRect[map][col][row].eventRectDefaultY;
        }
        return hit;
    }

    public void damagePit(int gameState) {

        gp.gameState = gameState;
        gp.playSE(6);
        gp.ui.currentDialogue = "You are hurt by spikes!";
        gp.player.life -= 1;
        canTouchEvent = false;

    }

    public void healingPool(int gameState) {

        if (gp.keyH.enterPressed == true) {
            gp.gameState = gameState;
            gp.player.attackCanceled = true;
            gp.playSE(2);
            gp.ui.currentDialogue = "You drink the water.\nYour life and mana have been recovered.\n"
                    + "(The progress has been saved)";
            gp.player.life = gp.player.maxLife;
            gp.player.mana = gp.player.maxMana;
            gp.aSetter.setMonster();
            gp.saveLoad.save();
        }

    }

    public void bossCheckpoint(int gameState) {
        if (gp.keyH.enterPressed == true) {
            gp.gameState = gameState;
            gp.player.attackCanceled = true;
            gp.playSE(2);
            gp.ui.currentDialogue = "Checkpoint activated.\nYour life and mana have been recovered.\n"
                    + "(The progress has been saved)";
            gp.player.life = gp.player.maxLife;
            gp.player.mana = gp.player.maxMana;
            gp.saveLoad.save();
            canTouchEvent = false;
        }
    }

    public void teleport(int map, int col, int row, int area) {
        gp.gameState = gp.transitionState;
        gp.nextArea = area;
        tempMap = map;
        tempCol = col;
        tempRow = row;
        canTouchEvent = false;
        gp.playSE(13);
    }

    public void speak(Entity entity) {

        if (gp.keyH.enterPressed) {
            gp.gameState = gp.dialogueState;
            gp.player.attackCanceled = true;
            entity.speak();
        }
    }

    public void skeletonLord() {
        if (gp.bossBattleOn == false && Progress.skeletonLordDefeated == false) {
            gp.gameState = gp.cutsceneState;
            gp.csManager.sceneNum = gp.csManager.skeletonLord;
        }
    }
}
