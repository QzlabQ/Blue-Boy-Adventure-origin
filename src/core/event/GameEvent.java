package core.event;

/**
 * 游戏事件基础抽象类
 * 所有游戏事件都应继承此类
 */
public abstract class GameEvent {
    private GameEventType eventType;
    private long timestamp;
    private Object source;

    public GameEvent(GameEventType eventType, Object source) {
        this.eventType = eventType;
        this.source = source;
        this.timestamp = System.currentTimeMillis();
    }

    public GameEventType getEventType() {
        return eventType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Object getSource() {
        return source;
    }
}
