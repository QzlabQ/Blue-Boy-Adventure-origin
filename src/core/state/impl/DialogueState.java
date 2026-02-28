package core.state.impl;

import java.awt.Graphics2D;
import core.state.AbstractGameState;
import core.state.GameStateManager;
import core.state.GameStateType;

/**
 * 对话状态
 * 显示 NPC 对话框和对话内容
 */
public class DialogueState extends AbstractGameState {

    public DialogueState(GameStateManager manager) {
        super(manager);
    }

    @Override
    public void enter(GameStateManager manager) {
        // 初始化对话状态
    }

    @Override
    public void exit(GameStateManager manager) {
        // 清理对话资源
    }

    @Override
    public void update(GameStateManager manager) {
        // 对话逻辑（暂时空实现）
    }

    @Override
    public void render(GameStateManager manager, Graphics2D g2) {
        // 先绘制游戏画面，再绘制对话框
        if (manager.getAdapter() != null) {
            manager.getAdapter().getGamePanel().drawToTempScreen();
            manager.getAdapter().getGamePanel().ui.drawDialogueScreen();
        }
    }

    @Override
    public void handleInput(GameStateManager manager) {
        // 对话输入处理（暂时空实现）
    }

    @Override
    public GameStateType getStateType() {
        return GameStateType.DIALOGUE;
    }
}
