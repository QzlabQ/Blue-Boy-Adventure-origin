package main;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import object.OBJ_Key;

public class UI {

	GamePanel gp;
	Graphics2D g2;
	
	Font arial_40, arial_80B;
	public boolean messageOn = false;
	public String message = "";
	int messageCounter = 0;
	public boolean gameFinished = false;
	public String currentDialogue = "";
	public int commandNum = 0;
    public int titleScreenState = 0; // 0: the first screen, 1:the second screen

	public UI(GamePanel gp) {
		this.gp = gp;
		
		//INSTANTIATING 
		arial_40 = new Font("Arial", Font.PLAIN, 40);
		arial_80B = new Font("Arial", Font.BOLD, 80);
		
	}
	
	public void showMessage(String text) {
		
		message = text;
		messageOn = true;
	}
	public void draw(Graphics2D g2) {
		
		this.g2 = g2;
		
		g2.setFont(arial_40);
		g2.setColor(Color.white);

        //TITLE STATE
        if(gp.gameState == gp.titleState) {
            drawTitleScreen();
        }
		//PLAY STATE
		if(gp.gameState == gp.playState) {
			//DO PLAYSTATE STUFF LATER
		}
		//PAUSE STATE
		if(gp.gameState == gp.pauseState) {
			drawPauseScreen();
		}
		//DIALOGUE STATE
		if(gp.gameState == gp.dialogueState) {
			drawDialogueScreen();
		}
		
	}
    public void drawTitleScreen() {

        if(titleScreenState == 0) {
            g2.setColor(new Color(0,0,0));
            g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);

            //TITLE NAME
            g2.setFont(g2.getFont().deriveFont(Font.BOLD,96F));
            String text = "Blue Boy Adventure";
            int x=getXforCenteredText(text);
            int y=gp.tileSize*3;

            //SHADOW
            g2.setColor(Color.gray);
            g2.drawString(text, x+5, y+5);
            //MAIN COLOR
            g2.setColor(Color.white);
            g2.drawString(text, x, y);

            //BLUE BOY IMAGE
            x = gp.screenWidth/2 - (gp.tileSize*2)/2;
            y += gp.tiltSize*2;
            g2.drawImage(gp.player.down1, x, y, gp.tileSize*2, gp.tileSize*2, null);

            //MENU
            g2.setFont(g2.getFont().deriveFont(Font.BOLD.48F));

            text = "NEW GAME";
            x = getXforCenteredText(text);
            y += gp.tileSize*3.5;
            g2.drawString(text, x, y);
            if(commandNum == 0) {
                g2.drawString(">", x-gp.tileSize, y);
            }

            text = "LOAD GAME";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if(commandNum == 1) {
                g2.drawString(">", x-gp.tileSize, y);
            }

            text = "QUIT";
            x = getXforCenteredText(text);
            y += gp.tileSize;
            g2.drawString(text, x, y);
            if(commandNum == 2) {
                g2.drawString(">", x-gp.tileSize, y);
            }
        }
        else if(titleScreenState == 1) {

            g2.setColor(Color.white);
            g2.setFont(g2.getFont().deriveFont(42F));

            String text = "Select your class!";
            int x=getXforCenteredText(text);
            int y=gp.tileSize*3;
            g2.drawString(text, x, y);

            text = "Fighter";
            x=getXforCenteredText(text);
            y += gp.tileSize*3;
            ge.drawString(text, x, y);
            if(commandNum == 0) {
                ge.drawString(">", x-gp.tileSize, y);
            }

            text = "Thief";
            x=getXforCenteredText(text);
            y += gp.tileSize;
            ge.drawString(text, x, y);
            if(commandNum == 1) {
                ge.drawString(">", x-gp.tileSize, y);
            }

            text = "Sorcerer";
            x=getXforCenteredText(text);
            y += gp.tileSize;
            ge.drawString(text, x, y);
            if(commandNum == 2) {
                ge.drawString(">", x-gp.tileSize, y);
            }

            text = "Back";
            x=getXforCenteredText(text);
            y += gp.tileSize*2;
            ge.drawString(text, x, y);
            if(commandNum == 3) {
                ge.drawString(">", x-gp.tileSize, y);
            }


        }




    }
	public void drawPauseScreen() {
		
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 80F));
		String text = "PAUSED";
		int x = getXforCenteredText(text);
		int y = gp.screenHeight / 2;
		
		g2.drawString(text, x, y);
	}
	public void drawDialogueScreen() {
		
		//WINDOW
		int x = gp.tileSize * 2;
		int y = gp.tileSize / 2;
		int width = gp.screenWidth - (gp.tileSize * 4);
		int height = gp.tileSize * 4;
		
		drawSubWindow(x, y, width, height);
		
		g2.setFont(g2.getFont().deriveFont(Font.PLAIN, 28F));
		x += gp.tileSize;
		y += gp.tileSize;
		
		for (String line : currentDialogue.split("\n")) {
			g2.drawString(line, x, y);
			y += 40;
		}
		
	}
	public void drawSubWindow(int x, int y, int width, int height) {
		
		Color c = new Color(0, 0, 0, 210);
		g2.setColor(c);
		g2.fillRoundRect(x, y, width, height, 35, 35);
		
		c = new Color(255, 255, 255);
		g2.setColor(c);
		g2.setStroke(new BasicStroke(5));
		g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, 25, 25);
	}
	public int getXforCenteredText(String text) {
		int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
		int x = gp.screenWidth / 2 - length / 2;
		return x;
	}
}







