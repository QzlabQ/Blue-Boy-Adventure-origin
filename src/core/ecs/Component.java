package core.ecs;

/**
 * ECS 组件接口
 * 所有组件都应实现基础生命周期方法
 */
public interface Component {

    /**
     * 初始化组件
     */
    void initialize();

    /**
     * 更新组件
     *
     * @param deltaTime 帧间隔（秒）
     */
    void update(float deltaTime);

    /**
     * 销毁组件并释放资源
     */
    void destroy();

    /**
     * 获取组件类型
     *
     * @return 组件类型
     */
    ComponentType getComponentType();
}
