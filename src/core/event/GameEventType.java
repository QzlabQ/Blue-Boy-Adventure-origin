package core.event;

/**
 * 游戏事件类型枚举
 * 定义所有可能的游戏事件类型
 */
public enum GameEventType {
    // 玩家相关事件
    PLAYER_LEVEL_UP,
    PLAYER_HEALTH_CHANGED,
    
    // 物品相关事件
    ITEM_PICKUP,
    ITEM_DROP,
    ITEM_USE,
    
    // 实体相关事件
    ENTITY_DEATH,
    ENTITY_SPAWN,
    
    // 对话相关事件
    DIALOGUE_START,
    DIALOGUE_END,
    
    // 状态相关事件
    STATE_CHANGE,
    
    // 碰撞相关事件
    COLLISION_DETECTED,
    
    // 伤害相关事件
    DAMAGE_TAKEN,
    DAMAGE_DEALT,
    
    // 治疗相关事件
    HEAL,
    
    // 游戏状态事件
    GAME_OVER,
    GAME_WIN,
    
    // 地图相关事件
    MAP_CHANGE,
    MAP_LOADED,
    
    // 过场动画相关事件
    CUTSCENE_START,
    CUTSCENE_END,
    
    // 其他事件
    UNKNOWN
}
