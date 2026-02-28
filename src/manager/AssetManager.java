package manager;

import java.util.HashMap;
import java.util.Map;

/**
 * 资源管理器
 * 管理游戏资源的加载和缓存
 */
public class AssetManager implements Manager {
    private Map<String, Object> assetCache = new HashMap<>();

    @Override
    public void initialize() {
        // 初始化加载器
    }

    @Override
    public void update(float deltaTime) {
        // 资源管理器通常不需要每帧更新
    }

    @Override
    public void shutdown() {
        assetCache.clear();
    }

    /**
     * 加载资源
     *
     * @param path 资源路径
     * @param type 资源类型
     * @param <T>  资源类型泛型
     * @return 资源实例
     */
    @SuppressWarnings("unchecked")
    public <T> T loadAsset(String path, Class<T> type) {
        if (assetCache.containsKey(path)) {
            return (T) assetCache.get(path);
        }
        // 实际加载逻辑后续实现
        return null;
    }

    /**
     * 获取已加载的资源
     *
     * @param path 资源路径
     * @param type 资源类型
     * @param <T>  资源类型泛型
     * @return 资源实例
     */
    @SuppressWarnings("unchecked")
    public <T> T getAsset(String path, Class<T> type) {
        return (T) assetCache.get(path);
    }

    /**
     * 卸载资源
     *
     * @param path 资源路径
     */
    public void unloadAsset(String path) {
        assetCache.remove(path);
    }
}
