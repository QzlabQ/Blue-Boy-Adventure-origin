package app;

import main.GamePanel;
import adapter.GamePanelAdapter;

/**
 * Game - Application Main Entry Class
 * 
 * 这是新的应用入口，替代旧的 Main.java
 * 职责：
 * - 创建并配置游戏窗口
 * - 初始化游戏面板
 * - 启动游戏线程
 * - 为后续集成新的框架（事件系统、状态管理等）预留接口
 */
public class Game {
    private GameWindow window;
    private GamePanel gamePanel;
    private GamePanelAdapter adapter;

    /**
     * 构造函数
     */
    public Game() {
        this.window = new GameWindow();
    }

    /**
     * 初始化游戏
     */
    public void initialize() {
        // 1. 创建游戏面板（v1 代码）
        gamePanel = new GamePanel();

        // 2. 为窗口配置游戏面板
        window.addComponent(gamePanel);

        // 3. 根据 GamePanel 的配置设置全屏
        // 注：GamePanel 在构造时已自动加载配置
        if (gamePanel.fullScreenOn) {
            window.setFullScreen(true);
        }

        // 4. 打包窗口
        window.pack();

        // 5. 在屏幕中央显示窗口
        window.centerOnScreen();

        // 6. 显示窗口
        window.show();

        // 7. 设置游戏（内部会初始化 v2 适配层）
        gamePanel.setupGame();

        // 8. 复用 GamePanel 中创建的唯一适配器实例，避免双 adapter 并存
        adapter = gamePanel.adapter;
    }

    /**
     * 启动游戏
     */
    public void start() {
        // 启动游戏线程
        gamePanel.startGameThread();
    }

    /**
     * 获取游戏面板适配器
     *
     * @return GamePanelAdapter 实例
     */
    public GamePanelAdapter getAdapter() {
        return adapter;
    }

    /**
     * 获取游戏窗口
     *
     * @return GameWindow 实例
     */
    public GameWindow getWindow() {
        return window;
    }

    /**
     * 获取游戏面板（v1 代码）
     *
     * @return GamePanel 实例
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    /**
     * 应用入口点
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 创建游戏实例
        Game game = new Game();

        // 初始化游戏
        game.initialize();

        // 启动游戏
        game.start();
    }
}
