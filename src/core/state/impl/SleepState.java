package core.state.impl;

import java.awt.Graphics2D;
import core.state.AbstractGameState;
import core.state.GameStateManager;
import core.state.GameStateType;

/**
 * 睡眠/存档状态
 * 显示睡眠界面，用于存档和恢复生命值
 */
public class SleepState extends AbstractGameState {

    public SleepState(GameStateManager manager) {
        super(manager);
    }

    @Override
    public void enter(GameStateManager manager) {
        // 初始化睡眠状态
    }

    @Override
    public void exit(GameStateManager manager) {
        // 清理睡眠界面
    }

    @Override
    public void update(GameStateManager manager) {
        // 睡眠逻辑（暂时空实现）
    }

    @Override
    public void render(GameStateManager manager, Graphics2D g2) {
        // 先绘制游戏画面，再绘制睡眠界面
        if (manager.getAdapter() != null) {
            manager.getAdapter().getGamePanel().drawToTempScreen();
            manager.getAdapter().getGamePanel().ui.drawSleepScreen();
        }
    }

    @Override
    public void handleInput(GameStateManager manager) {
        // 睡眠界面输入处理（暂时空实现）
    }

    @Override
    public GameStateType getStateType() {
        return GameStateType.SLEEP;
    }
}
