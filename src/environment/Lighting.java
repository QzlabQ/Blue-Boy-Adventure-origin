package environment;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.image.BufferedImage;

import entity.Entity;
import main.GamePanel;

public class Lighting {
    GamePanel gp;
    BufferedImage darknessFilter;

    // 昼夜交替
    public int dayCounter;
    public float filterAlpha = 0f;
    public final int day = 0;
    public final int dusk = 1;
    public final int night = 2;
    public final int dawn = 3;
    public int dayState = 0;
    boolean lightSourceActive = false;

    public Lighting(GamePanel gp) {
        this.gp = gp;
        setLightSource();
    }

    public void setLightSource() {
        // create a buffered image
        if (darknessFilter == null) {
            darknessFilter = new BufferedImage(gp.screenWidth, gp.screenHeight, BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D g2 = (Graphics2D) darknessFilter.getGraphics();

        // Clear the image
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        g2.setComposite(AlphaComposite.SrcOver);

        // Draw the darkness
        g2.setColor(new Color(0, 0, 0.1f, 0.95f));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

        // Draw the lights
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.DST_OUT));

        // Player Light
        if (gp.player.currentLight != null) {
            int centerX = gp.player.screenX + (gp.tileSize / 2);
            int centerY = gp.player.screenY + (gp.tileSize / 2);
            drawLightCircle(g2, centerX, centerY, gp.player.currentLight.lightRadius);
        }

        // Projectile Lights
        if (gp.projectile[gp.currentMap] != null) {
            for (int i = 0; i < gp.projectile[gp.currentMap].length; i++) {
                Entity p = gp.projectile[gp.currentMap][i];
                if (p != null && p.alive && p.lightRadius > 0) {
                    int centerX = p.worldX - gp.player.worldX + gp.player.screenX + (gp.tileSize / 2);
                    int centerY = p.worldY - gp.player.worldY + gp.player.screenY + (gp.tileSize / 2);
                    drawLightCircle(g2, centerX, centerY, p.lightRadius);
                }
            }
        }

        g2.dispose();
    }

    public void drawLightCircle(Graphics2D g2, int centerX, int centerY, int radius) {
        Color color[] = new Color[5];
        float fraction[] = new float[5];

        color[0] = new Color(0, 0, 0, 0.95f);
        color[1] = new Color(0, 0, 0, 0.85f);
        color[2] = new Color(0, 0, 0, 0.6f);
        color[3] = new Color(0, 0, 0, 0.3f);
        color[4] = new Color(0, 0, 0, 0.0f);

        fraction[0] = 0f;
        fraction[1] = 0.25f;
        fraction[2] = 0.5f;
        fraction[3] = 0.75f;
        fraction[4] = 1f;

        RadialGradientPaint gPaint = new RadialGradientPaint(centerX, centerY, radius, fraction, color);
        g2.setPaint(gPaint);
        g2.fillOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
    }

    public void update() {
        if (gp.player.lightUpdated) {
            setLightSource();
            gp.player.lightUpdated = false;
        }

        // Check for active light sources (projectiles)
        boolean currentLightSourceActive = false;
        if (gp.projectile[gp.currentMap] != null) {
            for (int i = 0; i < gp.projectile[gp.currentMap].length; i++) {
                Entity p = gp.projectile[gp.currentMap][i];
                if (p != null && p.alive && p.lightRadius > 0) {
                    currentLightSourceActive = true;
                    break;
                }
            }
        }

        if (currentLightSourceActive) {
            setLightSource();
            lightSourceActive = true;
        } else if (lightSourceActive) {
            setLightSource();
            lightSourceActive = false;
        }

        if (dayState == day) {
            dayCounter++;
            // 白天持续 600 (10s)
            if (dayCounter > 600) {
                dayState = dusk;
                dayCounter = 0;
            }
        }
        if (dayState == dusk) {
            // 作为暗度滤镜，降低蒙版的透明度。最大值为1
            // fps = 60，渐暗持续 1000 / 60 = 16秒
            filterAlpha += 0.001f;
            if (filterAlpha > 1f) {
                filterAlpha = 1f;
                dayState = night;
            }
        }
        if (dayState == night) {
            dayCounter++;

            // 依旧维持600 (10s)
            if (dayCounter > 600) {
                dayState = dawn;
                dayCounter = 0;
            }
        }

        if (dayState == dawn) {
            // 渐亮
            filterAlpha -= 0.001f;
            if (filterAlpha < 0f) {
                filterAlpha = 0f;
                dayState = day;
            }
        }
    }

    public void resetDay() {
        dayState = day;
        filterAlpha = 0f;

    }

    public void draw(Graphics2D g2) {

        if (gp.currentArea == gp.outside) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, filterAlpha));
        }
        if (gp.currentArea == gp.outside || gp.currentArea == gp.dungeon) {
            g2.drawImage(darknessFilter, 0, 0, null);
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // 屏幕上显示时间，以下内容也可以全部注释掉
        String situation = "";

        switch (dayState) {
            case day:
                situation = "Day";
                break;
            case dusk:
                situation = "Dusk";
                break;
            case night:
                situation = "Night";
                break;
            case dawn:
                situation = "Dawn";
                break;
        }
        g2.setColor(Color.white);
        g2.setFont(g2.getFont().deriveFont(50f));
        g2.drawString(situation, 800, 500);
    }

}
