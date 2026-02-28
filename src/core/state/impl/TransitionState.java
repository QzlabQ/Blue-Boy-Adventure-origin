package core.state.impl;

import java.awt.Graphics2D;
import core.state.AbstractGameState;
import core.state.GameStateManager;
import core.state.GameStateType;

/**
 * 过渡状态
 * 显示场景过渡效果（如淡入淡出）
 */
public class TransitionState extends AbstractGameState {

    public TransitionState(GameStateManager manager) {
        super(manager);
    }

    @Override
    public void enter(GameStateManager manager) {
        // 初始化过渡效果
    }

    @Override
    public void exit(GameStateManager manager) {
        // 清理过渡效果
    }

    @Override
    public void update(GameStateManager manager) {
        // 过渡逻辑（暂时空实现）
    }

    @Override
    public void render(GameStateManager manager, Graphics2D g2) {
        // 绘制过渡效果
        if (manager.getAdapter() != null) {
            manager.getAdapter().getGamePanel().ui.drawTransition();
        }
    }

    @Override
    public void handleInput(GameStateManager manager) {
        // 过渡过程中不处理输入
    }

    @Override
    public GameStateType getStateType() {
        return GameStateType.TRANSITION;
    }
}
