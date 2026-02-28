package core.state.impl;

import java.awt.Graphics2D;
import core.state.AbstractGameState;
import core.state.GameStateManager;
import core.state.GameStateType;

/**
 * 过场动画状态
 * 显示游戏的过场动画（故事情节）
 */
public class CutsceneState extends AbstractGameState {

    public CutsceneState(GameStateManager manager) {
        super(manager);
    }

    @Override
    public void enter(GameStateManager manager) {
        // 初始化过场动画状态
    }

    @Override
    public void exit(GameStateManager manager) {
        // 清理过场动画资源
    }

    @Override
    public void update(GameStateManager manager) {
        // 过场动画逻辑（暂时空实现）
    }

    @Override
    public void render(GameStateManager manager, Graphics2D g2) {
        // 先绘制游戏画面
        if (manager.getAdapter() != null) {
            manager.getAdapter().getGamePanel().drawToTempScreen();
        }
    }

    @Override
    public void handleInput(GameStateManager manager) {
        // 过场动画过程中输入处理（暂时空实现）
    }

    @Override
    public GameStateType getStateType() {
        return GameStateType.CUTSCENE;
    }
}
