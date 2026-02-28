package core.ecs;

/**
 * ECS 组件类型枚举
 */
public enum ComponentType {
    TRANSFORM, // 位置、速度、朝向
    RENDER, // 图像、动画状态
    PHYSICS, // 碰撞体、质量
    AI, // 行为决策
    COMBAT, // 攻击、防御
    HEALTH, // 生命值、魔法值
    INVENTORY, // 背包、装备
    DIALOGUE // NPC 对话数据
}
