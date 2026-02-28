package manager;

/**
 * 物理管理器
 * 管理物理更新和碰撞检测
 */
public class PhysicsManager implements Manager {

    @Override
    public void initialize() {
        // 初始化物理系统
    }

    @Override
    public void update(float deltaTime) {
        // 更新物理，检测碰撞（后续实现）
    }

    @Override
    public void shutdown() {
        // 清理物理资源
    }
}
