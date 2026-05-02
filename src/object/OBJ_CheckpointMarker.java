package object;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import entity.Entity;
import main.GamePanel;

public class OBJ_CheckpointMarker extends Entity {

    public static final String objName = "Checkpoint Marker";
    GamePanel gp;

    public OBJ_CheckpointMarker(GamePanel gp) {
        super(gp);
        this.gp = gp;

        type = type_obstacle;
        name = objName;
        collision = false;

        solidArea.x = 8;
        solidArea.y = 8;
        solidArea.width = 32;
        solidArea.height = 32;
        solidAreaDefaultX = solidArea.x;
        solidAreaDefaultY = solidArea.y;
    }

    @Override
    public void draw(Graphics2D g2) {
        if (inCamera() == false) {
            return;
        }

        int screenX = getScreenX();
        int screenY = getScreenY();
        int centerX = screenX + gp.tileSize / 2;
        int centerY = screenY + gp.tileSize / 2;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        float pulse = (float) ((Math.sin(System.nanoTime() / 180000000.0) + 1) * 0.5);
        int glowSize = 34 + Math.round(pulse * 8);
        int glowX = centerX - glowSize / 2;
        int glowY = centerY - glowSize / 2;

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
        g2.setColor(new Color(74, 210, 255));
        g2.fillOval(glowX, glowY, glowSize, glowSize);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.95f));
        g2.setColor(new Color(12, 25, 34));
        g2.fillRoundRect(screenX + 11, screenY + 11, 26, 26, 8, 8);

        g2.setStroke(new BasicStroke(3f));
        g2.setColor(new Color(132, 238, 255));
        g2.drawRoundRect(screenX + 11, screenY + 11, 26, 26, 8, 8);

        g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(centerX, screenY + 17, centerX, screenY + 31);
        g2.drawLine(screenX + 17, centerY, screenX + 31, centerY);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }
}
