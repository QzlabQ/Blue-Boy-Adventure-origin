package object;

import entity.Entity;
import main.GamePanel;

public class OBJ_Potion_Red extends Entity {
    public static final String objName = "Red Potion";
    GamePanel gp;

    public OBJ_Potion_Red(GamePanel gp) {
        super(gp);

        this.gp = gp;

        type = type_consumable;
        name = objName;
        value = 5;
        down1 = setup("/objects/potion_red", gp.tileSize, gp.tileSize);
        description = "[Red Potion]\nHeals your life by " + value + ".";
        price = 20;
        stackable = true;
    }

    public boolean use(Entity entity) {

        gp.gameState = gp.dialogueState;
        gp.ui.currentDialogue = "You drink the " + name + "!\n"
                + "Your life has been recovered by " + value + ".";
        entity.life += value;
        if (gp.player.life > gp.player.maxLife) {
            gp.player.life = gp.player.maxLife;
        }
        gp.playSE(2);
        return true;
    }
}
