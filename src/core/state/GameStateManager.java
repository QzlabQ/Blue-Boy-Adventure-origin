package core.state;

import java.awt.Graphics2D;
import java.util.*;

/**
 * 游戏状态管理器 - 单例模式
 * 管理游戏的所有状态切换和状态逻辑
 */
public class GameStateManager {
    private static GameStateManager instance;
    private GameState currentState;
    private GameState previousState;
    private Map<GameStateType, GameState> states = new HashMap<>();
    private adapter.GamePanelAdapter adapter;

    private GameStateManager() {
    }

    /**
     * 获取 GameStateManager 单例实例
     *
     * @return GameStateManager 实例
     */
    public static GameStateManager getInstance() {
        if (instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }

    /**
     * 注册一个游戏状态
     *
     * @param type  状态类型
     * @param state 游戏状态实例
     */
    public void registerState(GameStateType type, GameState state) {
        states.put(type, state);
    }

    /**
     * 切换到指定的游戏状态
     *
     * @param type 要切换的状态类型
     */
    public void changeState(GameStateType type) {
        if (currentState != null) {
            currentState.exit(this);
        }
        previousState = currentState;
        currentState = states.get(type);
        if (currentState != null) {
            currentState.enter(this);
        }
    }

    /**
     * 更新当前状态
     */
    public void update() {
        if (currentState != null) {
            currentState.update(this);
        }
    }

    /**
     * 渲染当前状态
     *
     * @param g2 图形上下文
     */
    public void render(Graphics2D g2) {
        if (currentState != null) {
            currentState.render(this, g2);
        }
    }

    /**
     * 处理用户输入
     */
    public void handleInput() {
        if (currentState != null) {
            currentState.handleInput(this);
        }
    }

    /**
     * 获取当前状态
     *
     * @return 当前游戏状态
     */
    public GameState getCurrentState() {
        return currentState;
    }

    /**
     * 获取前一个状态
     *
     * @return 前一个游戏状态
     */
    public GameState getPreviousState() {
        return previousState;
    }

    /**
     * 获取当前状态的类型
     *
     * @return 当前状态类型
     */
    public GameStateType getCurrentStateType() {
        return currentState != null ? currentState.getStateType() : null;
    }

    /**
     * 设置 GamePanelAdapter（用于访问 v1 组件）
     *
     * @param adapter GamePanelAdapter 实例
     */
    public void setAdapter(adapter.GamePanelAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * 获取 GamePanelAdapter
     *
     * @return GamePanelAdapter 实例
     */
    public adapter.GamePanelAdapter getAdapter() {
        return adapter;
    }
}
