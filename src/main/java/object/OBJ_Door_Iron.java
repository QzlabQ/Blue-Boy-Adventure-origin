package object;

import entity.Entity;

import main.GamePanel;

public class OBJ_Door_Iron extends Entity {
    public static final String objName = "Iron Door";

    GamePanel gp;
    public OBJ_Door_Iron(GamePanel gp) {
        super(gp);
        this.gp = gp;

        type = type_obstacle;
        name = objName;
        down1 = setup("/objects/door_iron", gp.tileSize, gp.tileSize);
        collision = true;

        solidArea.x = 0;
        solidArea.y = 16;
        solidArea.width = 48;
        solidArea.height = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;

    }

    public void interact() {
        gp.gameState = gp.dialogueState;
        gp.ui.currentDialogue = "It won't budge.";
    }
}
