package object;

import entity.Entity;
import main.GamePanel;

public class OBJ_AirWall extends Entity {

    public static final String objName = "Air Wall";

    public OBJ_AirWall(GamePanel gp) {
        super(gp);

        type = type_obstacle;
        name = objName;
        collision = true;

        solidArea.x = 0;
        solidArea.y = 0;
        solidArea.width = 48;
        solidArea.height = 48;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    @Override
    public void draw(java.awt.Graphics2D g2) {
        // Invisible
    }
}
