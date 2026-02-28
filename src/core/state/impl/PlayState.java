package core.state.impl;

import java.awt.Graphics2D;
import core.state.AbstractGameState;
import core.state.GameStateManager;
import core.state.GameStateType;

/**
 * 游戏进行状态
 * 游戏的主要玩法状态，处理玩家操作、物理更新等
 */
public class PlayState extends AbstractGameState {

    public PlayState(GameStateManager manager) {
        super(manager);
    }

    @Override
    public void enter(GameStateManager manager) {
        // 重置游戏状态
    }

    @Override
    public void exit(GameStateManager manager) {
    }

    @Override
    public void update(GameStateManager manager) {
        // 暂时空实现，后续调用各 System
    }

    @Override
    public void render(GameStateManager manager, Graphics2D g2) {
        // 委托给旧渲染管线
        if (manager.getAdapter() != null) {
            manager.getAdapter().getGamePanel().drawToTempScreen();
        }
    }

    @Override
    public void handleInput(GameStateManager manager) {
        // 暂时空实现，后续迁移 KeyHandler.playState()
    }

    @Override
    public GameStateType getStateType() {
        return GameStateType.PLAY;
    }
}
