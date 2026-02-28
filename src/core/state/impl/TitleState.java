package core.state.impl;

import java.awt.Graphics2D;
import core.state.AbstractGameState;
import core.state.GameStateManager;
import core.state.GameStateType;

/**
 * 标题屏幕状态
 * 显示游戏标题和菜单
 */
public class TitleState extends AbstractGameState {

    public TitleState(GameStateManager manager) {
        super(manager);
    }

    @Override
    public void enter(GameStateManager manager) {
        // 初始化菜单
    }

    @Override
    public void exit(GameStateManager manager) {
    }

    @Override
    public void update(GameStateManager manager) {
        // 空实现，逻辑在 handleInput 中
    }

    @Override
    public void render(GameStateManager manager, Graphics2D g2) {
        // 委托给旧 UI
        if (manager.getAdapter() != null) {
            manager.getAdapter().getGamePanel().ui.drawTitleScreen();
        }
    }

    @Override
    public void handleInput(GameStateManager manager) {
        // 暂时空实现，后续迁移 KeyHandler.titleState()
    }

    @Override
    public GameStateType getStateType() {
        return GameStateType.TITLE;
    }
}
