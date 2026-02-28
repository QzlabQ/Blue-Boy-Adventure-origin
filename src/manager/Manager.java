package manager;

/**
 * Manager 基接口
 * 定义所有管理器的统一生命周期
 */
public interface Manager {
    /**
     * 初始化管理器
     */
    void initialize();

    /**
     * 更新管理器逻辑
     *
     * @param deltaTime 帧间隔（秒）
     */
    void update(float deltaTime);

    /**
     * 关闭管理器并释放资源
     */
    void shutdown();
}
