package core.ecs;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 组件容器
 * 用于在实体上管理组件的增删查
 */
public class ComponentMap {
    private final Map<ComponentType, Component> components = new HashMap<>();

    /**
     * 添加组件并执行初始化
     *
     * @param component 组件实例
     * @param <T>       组件类型
     * @return 添加后的组件实例
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T addComponent(T component) {
        components.put(component.getComponentType(), component);
        component.initialize();
        return component;
    }

    /**
     * 按类型获取组件
     *
     * @param type 组件类型
     * @param <T>  组件类型
     * @return 组件实例，不存在则返回 null
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(ComponentType type) {
        return (T) components.get(type);
    }

    /**
     * 移除组件并执行销毁
     *
     * @param type 组件类型
     * @return 是否成功移除
     */
    public boolean removeComponent(ComponentType type) {
        Component component = components.remove(type);
        if (component != null) {
            component.destroy();
            return true;
        }
        return false;
    }

    /**
     * 检查是否存在指定类型组件
     *
     * @param type 组件类型
     * @return 是否存在
     */
    public boolean hasComponent(ComponentType type) {
        return components.containsKey(type);
    }

    /**
     * 获取所有组件
     *
     * @return 组件集合视图
     */
    public Collection<Component> getAllComponents() {
        return components.values();
    }
}
