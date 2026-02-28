package core.state.impl;

import java.awt.Graphics2D;
import core.state.AbstractGameState;
import core.state.GameStateManager;
import core.state.GameStateType;

/**
 * 地图状态
 * 显示全世界地图界面
 */
public class MapState extends AbstractGameState {

    public MapState(GameStateManager manager) {
        super(manager);
    }

    @Override
    public void enter(GameStateManager manager) {
        // 初始化地图状态
    }

    @Override
    public void exit(GameStateManager manager) {
        // 清理地图界面
    }

    @Override
    public void update(GameStateManager manager) {
        // 地图逻辑（暂时空实现）
    }

    @Override
    public void render(GameStateManager manager, Graphics2D g2) {
            // 绘制全世界地图（通过 GamePanel 的 drawToTempScreen）
        if (manager.getAdapter() != null) {
                manager.getAdapter().getGamePanel().drawToTempScreen();
        }
    }

    @Override
    public void handleInput(GameStateManager manager) {
            // 地图界面输入处理（暂时空实现）
    }

    @Override
    public GameStateType getStateType() {
        return GameStateType.MAP;
    }
}
