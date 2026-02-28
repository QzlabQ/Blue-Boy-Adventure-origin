package core.state;

/**
 * 游戏状态类型枚举
 */
public enum GameStateType {
    TITLE,           // 标题屏幕
    PLAY,            // 游戏进行中
    PAUSE,           // 游戏暂停
    DIALOGUE,        // 对话中
    CHARACTER,       // 人物界面/背包界面
    OPTION,          // 选项界面
    GAME_OVER,       // 游戏结束
    TRANSITION,      // 过渡
    TRADE,           // 交易
    SLEEP,           // 睡眠
    MAP,             // 地图
    CUTSCENE,        // 过场动画
    DEBUG            // 调试模式
}
