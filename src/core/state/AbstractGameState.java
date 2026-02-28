package core.state;

import java.awt.Graphics2D;

/**
 * 游戏状态抽象基类
 * 为所有具体状态提供默认实现
 */
public abstract class AbstractGameState implements GameState {
    protected GameStateManager manager;

    public AbstractGameState(GameStateManager manager) {
        this.manager = manager;
    }

    @Override
    public void enter(GameStateManager manager) {
        // 默认实现：无操作
    }

    @Override
    public void exit(GameStateManager manager) {
        // 默认实现：无操作
    }

    @Override
    public void update(GameStateManager manager) {
        // 默认实现：无操作
    }

    @Override
    public void render(GameStateManager manager, Graphics2D g2) {
        // 默认实现：无操作
    }

    @Override
    public void handleInput(GameStateManager manager) {
        // 默认实现：无操作
    }

    @Override
    public abstract GameStateType getStateType();
}
