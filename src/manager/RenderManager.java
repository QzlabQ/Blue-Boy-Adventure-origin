package manager;

import java.awt.Graphics2D;

/**
 * 渲染管理器
 * 统一管理游戏渲染逻辑
 */
public class RenderManager implements Manager {

    @Override
    public void initialize() {
        // 初始化渲染器
    }

    @Override
    public void update(float deltaTime) {
        // 渲染管理器通常不需要逻辑更新
    }

    @Override
    public void shutdown() {
        // 清理渲染资源
    }

    /**
     * 执行渲染
     *
     * @param g2 图形上下文
     */
    public void render(Graphics2D g2) {
        // 委托给分层渲染器（后续实现）
    }
}
