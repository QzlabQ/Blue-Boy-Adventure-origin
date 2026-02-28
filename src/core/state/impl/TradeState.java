package core.state.impl;

import java.awt.Graphics2D;
import core.state.AbstractGameState;
import core.state.GameStateManager;
import core.state.GameStateType;

/**
 * 交易状态
 * 显示商店/NPC 交易界面
 */
public class TradeState extends AbstractGameState {

    public TradeState(GameStateManager manager) {
        super(manager);
    }

    @Override
    public void enter(GameStateManager manager) {
        // 初始化交易状态
    }

    @Override
    public void exit(GameStateManager manager) {
        // 清理交易界面
    }

    @Override
    public void update(GameStateManager manager) {
        // 交易逻辑（暂时空实现）
    }

    @Override
    public void render(GameStateManager manager, Graphics2D g2) {
        // 先绘制游戏画面，再绘制交易界面
        if (manager.getAdapter() != null) {
            manager.getAdapter().getGamePanel().drawToTempScreen();
            manager.getAdapter().getGamePanel().ui.drawTradeScreen();
        }
    }

    @Override
    public void handleInput(GameStateManager manager) {
        // 交易界面输入处理（暂时空实现）
    }

    @Override
    public GameStateType getStateType() {
        return GameStateType.TRADE;
    }
}
