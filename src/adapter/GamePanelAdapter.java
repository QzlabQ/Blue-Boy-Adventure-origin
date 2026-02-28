package adapter;

import main.GamePanel;

/**
 * GamePanelAdapter - 适配器层
 * 桥接 v1（旧架构 GamePanel）和 v2（新架构）
 * 逐步迁移调用，实现平滑的架构过渡
 */
public class GamePanelAdapter {
    private GamePanel gamePanel;

    public GamePanelAdapter(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    /**
     * 获取被适配的 GamePanel
     *
     * @return GamePanel 实例
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    /**
     * 初始化游戏
     */
    public void setupGame() {
        gamePanel.setupGame();
    }

    /**
     * 启动游戏线程
     */
    public void startGameThread() {
        gamePanel.startGameThread();
    }

    /**
     * 更新游戏逻辑
     */
    public void update() {
        // 预留接口，未来可集成新的状态管理系统
        gamePanel.update();
    }

    /**
     * 渲染游戏画面
     */
    public void render() {
        // 预留接口，未来可集成新的渲染系统
        gamePanel.repaint();
    }

    /**
     * 获取游戏面板的宽度
     *
     * @return 宽度
     */
    public int getWidth() {
        return gamePanel.getWidth();
    }

    /**
     * 获取游戏面板的高度
     *
     * @return 高度
     */
    public int getHeight() {
        return gamePanel.getHeight();
    }
}
