package core.event;

import java.util.*;

/**
 * 事件总线 - 单例模式
 * 管理所有游戏事件的发布和订阅
 */
public class EventBus {
    private static EventBus instance;
    private Map<GameEventType, List<EventListener>> listeners = new HashMap<>();

    private EventBus() {
    }

    /**
     * 获取 EventBus 单例实例
     *
     * @return EventBus 实例
     */
    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    /**
     * 订阅特定类型的事件
     *
     * @param type     事件类型
     * @param listener 事件监听器
     */
    public void subscribe(GameEventType type, EventListener listener) {
        listeners.computeIfAbsent(type, k -> new ArrayList<>()).add(listener);
    }

    /**
     * 取消订阅特定类型的事件
     *
     * @param type     事件类型
     * @param listener 事件监听器
     */
    public void unsubscribe(GameEventType type, EventListener listener) {
        if (listeners.containsKey(type)) {
            listeners.get(type).remove(listener);
        }
    }

    /**
     * 发布事件，通知所有订阅者
     *
     * @param event 要发布的事件
     */
    public void publish(GameEvent event) {
        if (listeners.containsKey(event.getEventType())) {
            for (EventListener listener : listeners.get(event.getEventType())) {
                listener.onEvent(event);
            }
        }
    }

    /**
     * 清除所有事件监听器
     */
    public void clear() {
        listeners.clear();
    }

    /**
     * 清除特定类型的所有监听器
     *
     * @param type 事件类型
     */
    public void clearListeners(GameEventType type) {
        listeners.remove(type);
    }
}
