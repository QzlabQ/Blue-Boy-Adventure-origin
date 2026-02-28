package adapter;

import core.event.EventBus;
import core.state.GameStateManager;
import core.state.GameStateType;
import core.state.impl.CharacterState;
import core.state.impl.CutsceneState;
import core.state.impl.DebugState;
import core.state.impl.DialogueState;
import core.state.impl.GameOverState;
import core.state.impl.MapState;
import core.state.impl.OptionState;
import core.state.impl.PauseState;
import core.state.impl.PlayState;
import core.state.impl.SleepState;
import core.state.impl.TitleState;
import core.state.impl.TradeState;
import core.state.impl.TransitionState;
import manager.AssetManager;
import manager.EntityManager;
import manager.PhysicsManager;
import manager.RenderManager;
import manager.ServiceRegistry;
import main.GamePanel;
import main.KeyHandler;
import main.UI;
import entity.Player;

/**
 * GamePanelAdapter - 适配器层
 * 桥接 v1（旧架构 GamePanel）和 v2（新架构）
 * 逐步迁移调用，实现平滑的架构过渡
 */
public class GamePanelAdapter {
    private GamePanel gamePanel;
    private GameStateManager gameStateManager;
    private ServiceRegistry serviceRegistry;
    private EventBus eventBus;

    public GamePanelAdapter(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    /**
     * 初始化新框架（v2）
     */
    public void initializeNewFramework() {
        // 1. 初始化 EventBus
        this.eventBus = EventBus.getInstance();

        // 2. 初始化 ServiceRegistry 和各 Manager
        this.serviceRegistry = ServiceRegistry.getInstance();
        serviceRegistry.register(EntityManager.class, new EntityManager());
        serviceRegistry.register(AssetManager.class, new AssetManager());
        serviceRegistry.register(PhysicsManager.class, new PhysicsManager());
        serviceRegistry.register(RenderManager.class, new RenderManager());
        serviceRegistry.initializeAll();

        // 3. 初始化 GameStateManager
        this.gameStateManager = GameStateManager.getInstance();
        gameStateManager.setAdapter(this);

        // 4. 注册所有状态
        gameStateManager.registerState(GameStateType.TITLE, new TitleState(gameStateManager));
        gameStateManager.registerState(GameStateType.PLAY, new PlayState(gameStateManager));
        gameStateManager.registerState(GameStateType.PAUSE, new PauseState(gameStateManager));
        gameStateManager.registerState(GameStateType.DIALOGUE, new DialogueState(gameStateManager));
        gameStateManager.registerState(GameStateType.CHARACTER, new CharacterState(gameStateManager));
        gameStateManager.registerState(GameStateType.OPTION, new OptionState(gameStateManager));
        gameStateManager.registerState(GameStateType.GAME_OVER, new GameOverState(gameStateManager));
        gameStateManager.registerState(GameStateType.TRANSITION, new TransitionState(gameStateManager));
        gameStateManager.registerState(GameStateType.TRADE, new TradeState(gameStateManager));
        gameStateManager.registerState(GameStateType.SLEEP, new SleepState(gameStateManager));
        gameStateManager.registerState(GameStateType.MAP, new MapState(gameStateManager));
        gameStateManager.registerState(GameStateType.CUTSCENE, new CutsceneState(gameStateManager));
        gameStateManager.registerState(GameStateType.DEBUG, new DebugState(gameStateManager));

        // 5. 设置初始状态为 TITLE
        gameStateManager.changeState(GameStateType.TITLE);
    }

    /**
     * 获取被适配的 GamePanel
     *
     * @return GamePanel 实例
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    /**
     * 获取状态管理器
     *
     * @return GameStateManager 实例
     */
    public GameStateManager getStateManager() {
        return gameStateManager;
    }

    /**
     * 获取服务注册中心
     *
     * @return ServiceRegistry 实例
     */
    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    /**
     * 获取事件总线
     *
     * @return EventBus 实例
     */
    public EventBus getEventBus() {
        return eventBus;
    }

    /**
     * 获取当前 gameState（v1 int 状态）
     *
     * @return 状态值
     */
    public int getGameState() {
        return gamePanel.gameState;
    }

    /**
     * 设置当前 gameState（v1 int 状态）
     *
     * @param state 状态值
     */
    public void setGameState(int state) {
        gamePanel.gameState = state;
    }

    /**
     * 获取 UI
     *
     * @return UI 实例
     */
    public UI getUI() {
        return gamePanel.ui;
    }

    /**
     * 获取键盘输入处理器
     *
     * @return KeyHandler 实例
     */
    public KeyHandler getKeyHandler() {
        return gamePanel.keyH;
    }

    /**
     * 获取玩家
     *
     * @return Player 实例
     */
    public Player getPlayer() {
        return gamePanel.player;
    }

    /**
     * 初始化游戏
     */
    public void setupGame() {
        gamePanel.setupGame();
    }

    /**
     * 启动游戏线程
     */
    public void startGameThread() {
        gamePanel.startGameThread();
    }

    /**
     * 更新游戏逻辑
     */
    public void update() {
        // 预留接口，未来可集成新的状态管理系统
        gamePanel.update();
    }

    /**
     * 渲染游戏画面
     */
    public void render() {
        // 预留接口，未来可集成新的渲染系统
        gamePanel.repaint();
    }

    /**
     * 调用 v1 的临时屏渲染
     */
    public void drawToTempScreen() {
        gamePanel.drawToTempScreen();
    }

    /**
     * 调用 v1 的最终上屏渲染
     */
    public void drawToScreen() {
        gamePanel.drawToScreen();
    }

    /**
     * 获取游戏面板的宽度
     *
     * @return 宽度
     */
    public int getWidth() {
        return gamePanel.getWidth();
    }

    /**
     * 获取游戏面板的高度
     *
     * @return 高度
     */
    public int getHeight() {
        return gamePanel.getHeight();
    }
}
