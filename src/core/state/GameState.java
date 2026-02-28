package core.state;

import java.awt.Graphics2D;

/**
 * 游戏状态接口
 * 所有游戏状态都应实现此接口
 */
public interface GameState {
    /**
     * 进入状态时调用
     *
     * @param manager 状态管理器
     */
    void enter(GameStateManager manager);

    /**
     * 离开状态时调用
     *
     * @param manager 状态管理器
     */
    void exit(GameStateManager manager);

    /**
     * 更新状态逻辑
     *
     * @param manager 状态管理器
     */
    void update(GameStateManager manager);

    /**
     * 渲染状态内容
     *
     * @param manager 状态管理器
     * @param g2      图形上下文
     */
    void render(GameStateManager manager, Graphics2D g2);

    /**
     * 处理用户输入
     *
     * @param manager 状态管理器
     */
    void handleInput(GameStateManager manager);

    /**
     * 获取当前状态类型
     *
     * @return 状态类型
     */
    GameStateType getStateType();
}
