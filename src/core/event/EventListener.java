package core.event;

/**
 * 事件监听器接口
 * 所有事件监听器都应实现此接口
 */
public interface EventListener {
    /**
     * 当事件发布时调用此方法
     *
     * @param event 发布的事件
     */
    void onEvent(GameEvent event);
}
