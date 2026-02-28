package test;

import core.event.*;
import core.state.*;
import core.ecs.*;
import manager.*;

/**
 * 新架构测试类
 * 用于验证任务 1.1-1.4 创建的新代码是否正常工作
 * 
 * 运行方式：
 * javac -d bin -cp bin src/test/NewArchitectureTest.java
 * java -cp bin test.NewArchitectureTest
 */
public class NewArchitectureTest {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("新架构测试开始");
        System.out.println("========================================\n");

        // 测试 1: 事件系统
        testEventSystem();

        // 测试 2: 状态管理系统
        testStateManagement();

        // 测试 3: 组件系统
        testComponentSystem();

        // 测试 4: Manager 框架
        testManagerFramework();

        System.out.println("\n========================================");
        System.out.println("所有测试完成！");
        System.out.println("========================================");
    }

    /**
     * 测试事件系统
     */
    private static void testEventSystem() {
        System.out.println("【测试 1】事件系统（EventBus）");
        System.out.println("----------------------------------------");

        // 获取 EventBus 单例
        EventBus eventBus = EventBus.getInstance();
        System.out.println("✓ EventBus 单例创建成功");

        // 创建测试监听器
        EventListener testListener = new EventListener() {
            @Override
            public void onEvent(GameEvent event) {
                System.out.println("  → 接收到事件: " + event.getEventType() + 
                                   " (时间戳: " + event.getTimestamp() + ")");
            }
        };

        // 订阅事件
        eventBus.subscribe(GameEventType.PLAYER_LEVEL_UP, testListener);
        System.out.println("✓ 事件监听器订阅成功");

        // 发布事件
        GameEvent testEvent = new GameEvent(GameEventType.PLAYER_LEVEL_UP, "TestSource") {};
        eventBus.publish(testEvent);
        System.out.println("✓ 事件发布成功");

        // 清理
        eventBus.clear();
        System.out.println("✓ EventBus 清理完成\n");
    }

    /**
     * 测试状态管理系统
     */
    private static void testStateManagement() {
        System.out.println("【测试 2】状态管理系统（GameStateManager）");
        System.out.println("----------------------------------------");

        // 获取 GameStateManager 单例
        GameStateManager stateManager = GameStateManager.getInstance();
        System.out.println("✓ GameStateManager 单例创建成功");

        // 创建测试状态
        GameState testState = new AbstractGameState(stateManager) {
            @Override
            public void enter(GameStateManager manager) {
                System.out.println("  → 进入测试状态");
            }

            @Override
            public void exit(GameStateManager manager) {
                System.out.println("  → 退出测试状态");
            }

            @Override
            public GameStateType getStateType() {
                return GameStateType.TITLE;
            }
        };

        // 注册状态
        stateManager.registerState(GameStateType.TITLE, testState);
        System.out.println("✓ 状态注册成功");

        // 切换状态
        stateManager.changeState(GameStateType.TITLE);
        System.out.println("✓ 状态切换成功");

        // 验证当前状态
        GameStateType currentType = stateManager.getCurrentStateType();
        System.out.println("✓ 当前状态: " + currentType + "\n");
    }

    /**
     * 测试组件系统
     */
    private static void testComponentSystem() {
        System.out.println("【测试 3】组件系统（Component & ComponentMap）");
        System.out.println("----------------------------------------");

        // 创建组件容器
        ComponentMap componentMap = new ComponentMap();
        System.out.println("✓ ComponentMap 创建成功");

        // 创建测试组件
        Component testComponent = new Component() {
            @Override
            public void initialize() {
                System.out.println("  → 组件初始化");
            }

            @Override
            public void update(float deltaTime) {
                System.out.println("  → 组件更新 (deltaTime: " + deltaTime + ")");
            }

            @Override
            public void destroy() {
                System.out.println("  → 组件销毁");
            }

            @Override
            public ComponentType getComponentType() {
                return ComponentType.TRANSFORM;
            }
        };

        // 添加组件（会自动调用 initialize）
        componentMap.addComponent(testComponent);
        System.out.println("✓ 组件添加成功");

        // 检查组件是否存在
        boolean hasComponent = componentMap.hasComponent(ComponentType.TRANSFORM);
        System.out.println("✓ 组件存在检查: " + hasComponent);

        // 获取组件
        Component retrievedComponent = componentMap.getComponent(ComponentType.TRANSFORM);
        System.out.println("✓ 组件获取成功");

        // 更新组件
        retrievedComponent.update(0.016f);

        // 移除组件（会自动调用 destroy）
        componentMap.removeComponent(ComponentType.TRANSFORM);
        System.out.println("✓ 组件移除成功\n");
    }

    /**
     * 测试 Manager 框架
     */
    private static void testManagerFramework() {
        System.out.println("【测试 4】Manager 框架（ServiceRegistry）");
        System.out.println("----------------------------------------");

        // 获取 ServiceRegistry 单例
        ServiceRegistry registry = ServiceRegistry.getInstance();
        System.out.println("✓ ServiceRegistry 单例创建成功");

        // 创建并注册各个 Manager
        AssetManager assetManager = new AssetManager();
        EntityManager entityManager = new EntityManager();
        PhysicsManager physicsManager = new PhysicsManager();
        RenderManager renderManager = new RenderManager();

        registry.register(AssetManager.class, assetManager);
        registry.register(EntityManager.class, entityManager);
        registry.register(PhysicsManager.class, physicsManager);
        registry.register(RenderManager.class, renderManager);
        System.out.println("✓ 4 个 Manager 注册成功");

        // 测试获取 Manager
        AssetManager retrievedAssetManager = registry.getManager(AssetManager.class);
        System.out.println("✓ AssetManager 获取成功: " + (retrievedAssetManager != null));

        EntityManager retrievedEntityManager = registry.getManager(EntityManager.class);
        System.out.println("✓ EntityManager 获取成功: " + (retrievedEntityManager != null));

        // 初始化所有 Manager（按正确顺序）
        System.out.println("\n初始化所有 Manager（Asset → Entity → Physics → Render）:");
        registry.initializeAll();
        System.out.println("✓ 所有 Manager 初始化成功");

        // 测试 Manager 的 update 方法
        System.out.println("\n测试 Manager 更新:");
        entityManager.update(0.016f);
        physicsManager.update(0.016f);
        System.out.println("✓ Manager 更新测试完成");

        // 关闭所有 Manager
        registry.shutdownAll();
        System.out.println("✓ 所有 Manager 关闭成功");
    }
}
