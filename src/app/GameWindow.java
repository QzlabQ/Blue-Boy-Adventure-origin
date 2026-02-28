package app;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * GameWindow - 游戏窗口管理类
 * 负责创建和配置主游戏窗口
 */
public class GameWindow {
    private JFrame window;

    public GameWindow() {
        initializeWindow();
    }

    /**
     * 初始化窗口
     */
    private void initializeWindow() {
        window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("2D Adventure");
        setIcon();
    }

    /**
     * 为窗口设置图标
     */
    private void setIcon() {
        java.net.URL iconUrl = getClass().getResource("/player/boy_down_1.png");

        if (iconUrl != null) {
            ImageIcon icon = new ImageIcon(iconUrl);
            window.setIconImage(icon.getImage());
        } else {
            System.out.println("Icon Image not found!");
        }
    }

    /**
     * 添加内容面板到窗口
     *
     * @param component 要添加的组件
     */
    public void addComponent(java.awt.Component component) {
        window.add(component);
    }

    /**
     * 打包窗口（调整大小以适应内容）
     */
    public void pack() {
        window.pack();
    }

    /**
     * 设置窗口在屏幕中央显示
     */
    public void centerOnScreen() {
        window.setLocationRelativeTo(null);
    }

    /**
     * 显示窗口
     */
    public void show() {
        window.setVisible(true);
    }

    /**
     * 设置窗口全屏模式
     *
     * @param fullScreen 是否全屏
     */
    public void setFullScreen(boolean fullScreen) {
        if (fullScreen) {
            window.setUndecorated(true);
        } else {
            window.setUndecorated(false);
        }
    }

    /**
     * 获取 JFrame 实例
     *
     * @return JFrame 窗口
     */
    public JFrame getFrame() {
        return window;
    }
}
