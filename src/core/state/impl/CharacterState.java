package core.state.impl;

import java.awt.Graphics2D;
import core.state.AbstractGameState;
import core.state.GameStateManager;
import core.state.GameStateType;

/**
 * 角色属性状态
 * 显示玩家的角色属性和装备等信息
 */
public class CharacterState extends AbstractGameState {

    public CharacterState(GameStateManager manager) {
        super(manager);
    }

    @Override
    public void enter(GameStateManager manager) {
        // 初始化角色属性状态
    }

    @Override
    public void exit(GameStateManager manager) {
        // 清理角色属性界面
    }

    @Override
    public void update(GameStateManager manager) {
        // 角色属性逻辑（暂时空实现）
    }

    @Override
    public void render(GameStateManager manager, Graphics2D g2) {
        // 先绘制游戏画面，再绘制角色属性界面
        if (manager.getAdapter() != null) {
            manager.getAdapter().getGamePanel().drawToTempScreen();
            manager.getAdapter().getGamePanel().ui.drawCharacterScreen();
        }
    }

    @Override
    public void handleInput(GameStateManager manager) {
        // 角色属性界面输入处理（暂时空实现）
    }

    @Override
    public GameStateType getStateType() {
        return GameStateType.CHARACTER;
    }
}
