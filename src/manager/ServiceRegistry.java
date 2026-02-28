package manager;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务定位器 - 单例模式
 * 集中管理所有 Manager 实例，提供全局访问点
 */
public class ServiceRegistry {
    private static ServiceRegistry instance;
    private Map<Class<? extends Manager>, Manager> managers = new HashMap<>();

    private ServiceRegistry() {
    }

    /**
     * 获取 ServiceRegistry 单例实例
     *
     * @return ServiceRegistry 实例
     */
    public static ServiceRegistry getInstance() {
        if (instance == null) {
            instance = new ServiceRegistry();
        }
        return instance;
    }

    /**
     * 注册一个 Manager
     *
     * @param type    Manager 类型
     * @param manager Manager 实例
     */
    public void register(Class<? extends Manager> type, Manager manager) {
        managers.put(type, manager);
    }

    /**
     * 获取指定类型的 Manager
     *
     * @param type Manager 类型
     * @param <T>  Manager 类型泛型
     * @return Manager 实例
     */
    @SuppressWarnings("unchecked")
    public <T extends Manager> T getManager(Class<T> type) {
        return (T) managers.get(type);
    }

    /**
     * 按正确顺序初始化所有 Manager
     * 初始化顺序：Asset → Entity → Physics → Render
     */
    public void initializeAll() {
        // 按正确顺序初始化：Asset → Entity → Physics → Render
        if (managers.containsKey(AssetManager.class)) {
            managers.get(AssetManager.class).initialize();
        }
        if (managers.containsKey(EntityManager.class)) {
            managers.get(EntityManager.class).initialize();
        }
        if (managers.containsKey(PhysicsManager.class)) {
            managers.get(PhysicsManager.class).initialize();
        }
        if (managers.containsKey(RenderManager.class)) {
            managers.get(RenderManager.class).initialize();
        }
    }

    /**
     * 关闭所有 Manager
     */
    public void shutdownAll() {
        for (Manager manager : managers.values()) {
            manager.shutdown();
        }
        managers.clear();
    }
}
