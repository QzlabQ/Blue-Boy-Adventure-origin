package manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import core.ecs.ComponentType;
import entity.Entity;

/**
 * 实体管理器
 * 管理所有游戏实体的注册、查询、销毁
 */
public class EntityManager implements Manager {
    private Map<Integer, Entity> entities = new HashMap<>();
    private int entityIdCounter = 0;
    private Map<ComponentType, List<Entity>> componentIndex = new HashMap<>();

    @Override
    public void initialize() {
        // 初始化组件索引
        for (ComponentType type : ComponentType.values()) {
            componentIndex.put(type, new ArrayList<>());
        }
    }

    @Override
    public void update(float deltaTime) {
        // Entity 更新由各 System 负责
    }

    @Override
    public void shutdown() {
        entities.clear();
        componentIndex.clear();
    }

    /**
     * 注册一个实体
     *
     * @param entity 实体实例
     */
    public void registerEntity(Entity entity) {
        int id = entityIdCounter++;
        // TODO: 等待 Entity 类添加 setId() 方法后取消注释
        // entity.setId(id);
        entities.put(id, entity);

        // TODO: 等待 Entity 类添加 hasComponent() 方法后取消注释
        // 更新组件索引
        // for (ComponentType type : ComponentType.values()) {
        //     if (entity.hasComponent(type)) {
        //         componentIndex.get(type).add(entity);
        //     }
        // }
    }

    /**
     * 注销一个实体
     *
     * @param entityId 实体 ID
     */
    public void unregisterEntity(int entityId) {
        Entity entity = entities.remove(entityId);
        if (entity != null) {
            // 从组件索引中移除
            for (List<Entity> list : componentIndex.values()) {
                list.remove(entity);
            }
        }
    }

    /**
     * 根据 ID 获取实体
     *
     * @param entityId 实体 ID
     * @return 实体实例
     */
    public Entity getEntity(int entityId) {
        return entities.get(entityId);
    }

    /**
     * 获取所有实体
     *
     * @return 实体集合
     */
    public Collection<Entity> getAllEntities() {
        return entities.values();
    }

    /**
     * 获取拥有指定组件的所有实体
     *
     * @param type 组件类型
     * @return 实体列表
     */
    public List<Entity> getEntitiesWithComponent(ComponentType type) {
        return new ArrayList<>(componentIndex.getOrDefault(type, new ArrayList<>()));
    }
}
