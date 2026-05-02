package main;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Main {

    public static JFrame window;

    public static void main(String[] args) {
        window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("2D Adventure");
        new Main().setIcon();
        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        gamePanel.config.loadConfig();
        if (gamePanel.fullScreenOn) {
            window.setUndecorated(true);
        }
        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

        gamePanel.setupGame();
        gamePanel.startGameThread();
    }
    public void setIcon(){
        java.net.URL iconUrl = getClass().getResource("/player/boy_down_1.png");
        
        if (iconUrl != null) {
            ImageIcon icon = new ImageIcon(iconUrl);
            window.setIconImage(icon.getImage());
        } else {
            System.out.println("Icon Image not found!");
        }
    }
}