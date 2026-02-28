package core.state.impl;

import java.awt.Graphics2D;
import core.state.AbstractGameState;
import core.state.GameStateManager;
import core.state.GameStateType;

/**
 * 游戏结束状态
 * 显示游戏结束画面（死亡或通关）
 */
public class GameOverState extends AbstractGameState {

    public GameOverState(GameStateManager manager) {
        super(manager);
    }

    @Override
    public void enter(GameStateManager manager) {
        // 初始化游戏结束状态
    }

    @Override
    public void exit(GameStateManager manager) {
        // 清理游戏结束界面
    }

    @Override
    public void update(GameStateManager manager) {
        // 游戏结束逻辑（暂时空实现）
    }

    @Override
    public void render(GameStateManager manager, Graphics2D g2) {
        // 先绘制游戏画面，再绘制游戏结束画面
        if (manager.getAdapter() != null) {
            manager.getAdapter().getGamePanel().drawToTempScreen();
            manager.getAdapter().getGamePanel().ui.drawGameOverScreen();
        }
    }

    @Override
    public void handleInput(GameStateManager manager) {
        // 游戏结束画面输入处理（暂时空实现）
    }

    @Override
    public GameStateType getStateType() {
        return GameStateType.GAME_OVER;
    }
}
