package core.state.impl;

import java.awt.Graphics2D;
import core.state.AbstractGameState;
import core.state.GameStateManager;
import core.state.GameStateType;

/**
 * 暂停状态
 * 暂停游戏逻辑更新，显示暂停菜单
 */
public class PauseState extends AbstractGameState {

    public PauseState(GameStateManager manager) {
        super(manager);
    }

    @Override
    public void enter(GameStateManager manager) {
        // 暂停游戏
    }

    @Override
    public void exit(GameStateManager manager) {
        // 恢复游戏
    }

    @Override
    public void update(GameStateManager manager) {
        // 暂停时不更新游戏逻辑
    }

    @Override
    public void render(GameStateManager manager, Graphics2D g2) {
        // 显示原内容 + 暂停菜单
        if (manager.getAdapter() != null) {
            manager.getAdapter().getGamePanel().drawToTempScreen();
            manager.getAdapter().getGamePanel().ui.drawPauseScreen();
        }
    }

    @Override
    public void handleInput(GameStateManager manager) {
        // 暂时空实现
    }

    @Override
    public GameStateType getStateType() {
        return GameStateType.PAUSE;
    }
}
