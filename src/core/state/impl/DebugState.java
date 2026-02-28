package core.state.impl;

import java.awt.Graphics2D;
import core.state.AbstractGameState;
import core.state.GameStateManager;
import core.state.GameStateType;

/**
 * 调试状态
 * 显示游戏的调试信息和工具
 */
public class DebugState extends AbstractGameState {

    public DebugState(GameStateManager manager) {
        super(manager);
    }

    @Override
    public void enter(GameStateManager manager) {
        // 初始化调试状态
    }

    @Override
    public void exit(GameStateManager manager) {
        // 清理调试信息
    }

    @Override
    public void update(GameStateManager manager) {
        // 调试逻辑（暂时空实现）
    }

    @Override
    public void render(GameStateManager manager, Graphics2D g2) {
        // 先绘制游戏画面，再绘制调试信息
        if (manager.getAdapter() != null) {
            manager.getAdapter().getGamePanel().drawToTempScreen();
            manager.getAdapter().getGamePanel().ui.drawDebugScreen();
        }
    }

    @Override
    public void handleInput(GameStateManager manager) {
        // 调试界面输入处理（暂时空实现）
    }

    @Override
    public GameStateType getStateType() {
        return GameStateType.DEBUG;
    }
}
