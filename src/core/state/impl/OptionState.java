package core.state.impl;

import java.awt.Graphics2D;
import core.state.AbstractGameState;
import core.state.GameStateManager;
import core.state.GameStateType;

/**
 * 选项菜单状态
 * 显示游戏菜单选项（如装备选择、物品使用等）
 */
public class OptionState extends AbstractGameState {

    public OptionState(GameStateManager manager) {
        super(manager);
    }

    @Override
    public void enter(GameStateManager manager) {
        // 初始化选项菜单状态
    }

    @Override
    public void exit(GameStateManager manager) {
        // 清理选项菜单资源
    }

    @Override
    public void update(GameStateManager manager) {
        // 选项菜单逻辑（暂时空实现）
    }

    @Override
    public void render(GameStateManager manager, Graphics2D g2) {
        // 先绘制游戏画面，再绘制选项菜单
        if (manager.getAdapter() != null) {
            manager.getAdapter().getGamePanel().drawToTempScreen();
            manager.getAdapter().getGamePanel().ui.drawOptionScreen();
        }
    }

    @Override
    public void handleInput(GameStateManager manager) {
        // 选项菜单输入处理（暂时空实现）
    }

    @Override
    public GameStateType getStateType() {
        return GameStateType.OPTION;
    }
}
