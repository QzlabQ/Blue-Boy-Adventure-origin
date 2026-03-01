# REMAKE_TODO.md - Java RPG 架构重构任务清单

**项目**：Blue Boy Adventure - 架构重构  
**开始日期**：2026-02-28  
**目标完成日期**：2026-05-31（约 13 周）  
**重构策略**：逐层递进式 + 版本并存 + 原子化提交

---

## � 进度汇总

**第一阶段进度**：🟢 **7/8 任务完成（87.5%）**

| 任务 | 名称 | 状态 | 完成度 |
|------|------|------|--------|
| 1.1 | 事件系统基础包 | ✅ 完成 | 100% |
| 1.2 | 状态管理系统 | ✅ 完成 | 100% |
| 1.3 | 组件系统基础 | ✅ 完成 | 100% |
| 1.4 | Manager 框架层 | ✅ 完成 | 100% |
| 1.5 | 具体状态类（1/2）| ✅ 完成 | 100% |
| 1.6 | 具体状态类（2/2）| ✅ 完成 | 100% |
| 1.7 | Adapter 适配层 | ✅ **完成** | 100% |
| 1.8 | 集成测试+文档 | ⏳ 待执行 | 0% |

**最后更新**：2026-02-28（任务 1.7 完成）  
**下一步**：执行任务 1.8（第一阶段最终验收）

---

## �📋 版本管理策略

### v1（旧架构）

- **位置**：`src/main/`, `src/entity/`, `src/data/` 等现有包
- **状态**：冻结，仅作为参考和兼容层
- **保留原因**：确保重构过程中游戏始终可运行

### v2（新架构）

- **位置**：`src/core/`, `src/manager/`, `src/framework/` 等新包
- **状态**：逐步演进，最终替代 v1
- **设计目标**：基于状态模式、观察者模式、组件系统

### Adapter 层（适配器）

- **位置**：`src/adapter/GamePanelAdapter.java`
- **职责**：桥接 v1 和 v2，逐步迁移调用

---

## 🚀 第一阶段：框架搭建（8 项任务，~2-3 周）

> **目标**：搭建核心基础设施，不破坏现有功能，新代码并行运行  
> **关键原则**：框架与现有代码完全隔离，所有修改仅在新包内

---

### ✅ 任务 1.1：创建事件系统基础包

**优先级**：🔴 P0（阻塞后续任务）  
**工作量**：1 天  
**任务描述**：建立 EventBus + GameEvent + EventType 基础类

**涉及文件**：

```
✨ 新建 src/core/event/GameEvent.java
✨ 新建 src/core/event/GameEventType.java
✨ 新建 src/core/event/EventListener.java
✨ 新建 src/core/event/EventBus.java
```

**详细实现要求**：

#### GameEvent.java

```java
package com.bluboy.core.event;

public abstract class GameEvent {
    private GameEventType eventType;
    private long timestamp;
    private Object source;

    public GameEvent(GameEventType eventType, Object source) {
        this.eventType = eventType;
        this.source = source;
        this.timestamp = System.currentTimeMillis();
    }

    public GameEventType getEventType() { return eventType; }
    public long getTimestamp() { return timestamp; }
    public Object getSource() { return source; }
}
```

#### GameEventType.java

```
枚举值：
- PLAYER_LEVEL_UP, ITEM_PICKUP, ENTITY_DEATH, DIALOGUE_START
- STATE_CHANGE, COLLISION_DETECTED, DAMAGE_TAKEN, HEAL
- GAME_OVER, MAP_CHANGE, CUTSCENE_START
```

#### EventListener.java

```java
package com.bluboy.core.event;

public interface EventListener {
    void onEvent(GameEvent event);
}
```

#### EventBus.java（单例）

```java
package com.bluboy.core.event;
import java.util.*;

public class EventBus {
    private static EventBus instance;
    private Map<GameEventType, List<EventListener>> listeners = new HashMap<>();

    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }

    public void subscribe(GameEventType type, EventListener listener) {
        listeners.computeIfAbsent(type, k -> new ArrayList<>()).add(listener);
    }

    public void unsubscribe(GameEventType type, EventListener listener) {
        if (listeners.containsKey(type)) {
            listeners.get(type).remove(listener);
        }
    }

    public void publish(GameEvent event) {
        if (listeners.containsKey(event.getEventType())) {
            for (EventListener listener : listeners.get(event.getEventType())) {
                listener.onEvent(event);
            }
        }
    }

    public void clear() {
        listeners.clear();
    }
}
```

**依赖关系**：无（基础设施）

**验收标准**：

- ✅ 编译无错
- ✅ EventBus 单例正常获取（`getInstance()`）
- ✅ `subscribe()` / `publish()` 工作正常，监听器能收到事件
- ✅ 不影响现有代码

**测试验证**：

```bash
# 编译检查
cd e:\project software\blue-boy-remake\Blue-Boy-Adventure-origin
javac -d bin src/core/event/*.java
```

**原子化提交**：

```
[CORE-1.1] feat: 建立事件系统基础类 (GameEvent, EventBus, EventListener)
```

**状态**：✅ 完成

---

### ✅ 任务 1.2：创建状态管理系统

**优先级**：🔴 P0（阻塞后续任务）  
**工作量**：1.5 天  
**任务描述**：实现 GameState 接口 + GameStateManager

**涉及文件**：

```
✨ 新建 src/core/state/GameState.java
✨ 新建 src/core/state/GameStateType.java
✨ 新建 src/core/state/GameStateManager.java
✨ 新建 src/core/state/AbstractGameState.java
```

**详细实现要求**：

#### GameState.java（接口）

```java
package com.bluboy.core.state;

import java.awt.Graphics2D;

public interface GameState {
    void enter(GameStateManager manager);
    void exit(GameStateManager manager);
    void update(GameStateManager manager);
    void render(GameStateManager manager, Graphics2D g2);
    void handleInput(GameStateManager manager);
    GameStateType getStateType();
}
```

#### GameStateType.java（枚举）

```
TITLE, PLAY, PAUSE, DIALOGUE, CHARACTER, OPTION,
GAME_OVER, TRANSITION, TRADE, SLEEP, MAP, CUTSCENE, DEBUG
```

#### GameStateManager.java（单例）

```java
package com.bluboy.core.state;

import java.awt.Graphics2D;
import java.util.*;
import com.bluboy.adapter.GamePanelAdapter;

public class GameStateManager {
    private static GameStateManager instance;
    private GameState currentState;
    private GameState previousState;
    private Map<GameStateType, GameState> states = new HashMap<>();
    private GamePanelAdapter adapter;

    public static GameStateManager getInstance() {
        if (instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }

    public void setAdapter(GamePanelAdapter adapter) {
        this.adapter = adapter;
    }

    public GamePanelAdapter getAdapter() {
        return adapter;
    }

    public void registerState(GameStateType type, GameState state) {
        states.put(type, state);
    }

    public void changeState(GameStateType type) {
        if (currentState != null) {
            currentState.exit(this);
        }
        previousState = currentState;
        currentState = states.get(type);
        if (currentState != null) {
            currentState.enter(this);
        }
    }

    public void update() {
        if (currentState != null) {
            currentState.update(this);
        }
    }

    public void render(Graphics2D g2) {
        if (currentState != null) {
            currentState.render(this, g2);
        }
    }

    public void handleInput() {
        if (currentState != null) {
            currentState.handleInput(this);
        }
    }

    public GameState getCurrentState() { return currentState; }
    public GameState getPreviousState() { return previousState; }
    public GameStateType getCurrentStateType() {
        return currentState != null ? currentState.getStateType() : null;
    }
}
```

#### AbstractGameState.java（抽象基类）

```java
package com.bluboy.core.state;

import java.awt.Graphics2D;

public abstract class AbstractGameState implements GameState {
    protected GameStateManager manager;

    public AbstractGameState(GameStateManager manager) {
        this.manager = manager;
    }

    @Override
    public void enter(GameStateManager manager) {}

    @Override
    public void exit(GameStateManager manager) {}

    @Override
    public void update(GameStateManager manager) {}

    @Override
    public void render(GameStateManager manager, Graphics2D g2) {}

    @Override
    public void handleInput(GameStateManager manager) {}

    @Override
    public abstract GameStateType getStateType();
}
```

**依赖关系**：

- 依赖 EventBus（可选，用于发布 STATE_CHANGE 事件）
- 依赖 GamePanelAdapter（引用，后续任务创建）

**验收标准**：

- ✅ 编译无错
- ✅ 状态注册和切换正常
- ✅ enter() 和 exit() 被正确调用
- ✅ 不影响现有代码

**原子化提交**：

```
[CORE-1.2] feat: 实现状态管理系统 (GameState, GameStateManager)
```

**状态**：✅ 完成

---

### ✅ 任务 1.3：创建组件系统基础

**优先级**：🔴 P0（阻塞后续任务）  
**工作量**：1 天  
**任务描述**：建立 Component 接口 + ComponentType 枚举

**涉及文件**：

```
✨ 新建 src/core/ecs/Component.java
✨ 新建 src/core/ecs/ComponentType.java
✨ 新建 src/core/ecs/ComponentMap.java
```

**详细实现要求**：

#### Component.java（接口）

```java
package com.bluboy.core.ecs;

public interface Component {
    void initialize();
    void update(float deltaTime);
    void destroy();
    ComponentType getComponentType();
}
```

#### ComponentType.java（枚举）

```
TRANSFORM,      // 位置、速度、朝向
RENDER,         // 图像、动画状态
PHYSICS,        // 碰撞体、质量
AI,             // 行为决策
COMBAT,         // 攻击、防御
HEALTH,         // 生命值、魔法值
INVENTORY,      // 背包、装备
DIALOGUE        // NPC对话数据
```

#### ComponentMap.java（容器）

```java
package com.bluboy.core.ecs;

import java.util.*;

public class ComponentMap {
    private Map<ComponentType, Component> components = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Component> T addComponent(T component) {
        components.put(component.getComponentType(), component);
        component.initialize();
        return component;
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(ComponentType type) {
        return (T) components.get(type);
    }

    public boolean removeComponent(ComponentType type) {
        Component comp = components.remove(type);
        if (comp != null) {
            comp.destroy();
            return true;
        }
        return false;
    }

    public boolean hasComponent(ComponentType type) {
        return components.containsKey(type);
    }

    public Collection<Component> getAllComponents() {
        return components.values();
    }
}
```

**依赖关系**：无（基础设施）

**验收标准**：

- ✅ 编译无错
- ✅ 组件可以添加、获取、移除
- ✅ 组件类型检查正常
- ✅ 不影响现有代码

**原子化提交**：

```
[CORE-1.3] feat: 建立组件系统基础 (Component, ComponentType, ComponentMap)
```

**状态**：✅ 完成

---

### ✅ 任务 1.4：创建 Manager 框架层

**优先级**：🔴 P0（阻塞后续任务）  
**工作量**：2 天  
**任务描述**：实现 5 个核心 Manager 的接口和初始化

**涉及文件**：

```
✨ 新建 src/manager/Manager.java
✨ 新建 src/manager/ServiceRegistry.java
✨ 新建 src/manager/EntityManager.java
✨ 新建 src/manager/AssetManager.java
✨ 新建 src/manager/RenderManager.java
✨ 新建 src/manager/PhysicsManager.java
```

**详细实现要求**：

#### Manager.java（基接口）

```java
package com.bluboy.manager;

public interface Manager {
    void initialize();
    void update(float deltaTime);
    void shutdown();
}
```

#### ServiceRegistry.java（单例服务定位器）

```java
package com.bluboy.manager;

import java.util.*;

public class ServiceRegistry {
    private static ServiceRegistry instance;
    private Map<Class<? extends Manager>, Manager> managers = new HashMap<>();

    public static ServiceRegistry getInstance() {
        if (instance == null) {
            instance = new ServiceRegistry();
        }
        return instance;
    }

    public void register(Class<? extends Manager> type, Manager manager) {
        managers.put(type, manager);
    }

    @SuppressWarnings("unchecked")
    public <T extends Manager> T getManager(Class<T> type) {
        return (T) managers.get(type);
    }

    public void initializeAll() {
        // 按正确顺序初始化：Asset → Entity → Physics → Render
        if (managers.containsKey(AssetManager.class)) {
            managers.get(AssetManager.class).initialize();
        }
        if (managers.containsKey(EntityManager.class)) {
            managers.get(EntityManager.class).initialize();
        }
        if (managers.containsKey(PhysicsManager.class)) {
            managers.get(PhysicsManager.class).initialize();
        }
        if (managers.containsKey(RenderManager.class)) {
            managers.get(RenderManager.class).initialize();
        }
    }

    public void shutdownAll() {
        for (Manager manager : managers.values()) {
            manager.shutdown();
        }
        managers.clear();
    }
}
```

#### EntityManager.java

```java
package com.bluboy.manager;

import java.util.*;
import com.bluboy.entity.Entity;
import com.bluboy.core.ecs.ComponentType;

public class EntityManager implements Manager {
    private Map<Integer, Entity> entities = new HashMap<>();
    private int entityIdCounter = 0;
    private Map<ComponentType, List<Entity>> componentIndex = new HashMap<>();

    @Override
    public void initialize() {
        // 初始化索引
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

    public void registerEntity(Entity entity) {
        int id = entityIdCounter++;
        entity.setId(id);
        entities.put(id, entity);
        // 更新组件索引
        for (ComponentType type : ComponentType.values()) {
            if (entity.hasComponent(type)) {
                componentIndex.get(type).add(entity);
            }
        }
    }

    public void unregisterEntity(int entityId) {
        Entity entity = entities.remove(entityId);
        if (entity != null) {
            for (List<Entity> list : componentIndex.values()) {
                list.remove(entity);
            }
        }
    }

    public Entity getEntity(int entityId) {
        return entities.get(entityId);
    }

    public Collection<Entity> getAllEntities() {
        return entities.values();
    }

    public List<Entity> getEntitiesWithComponent(ComponentType type) {
        return new ArrayList<>(componentIndex.getOrDefault(type, new ArrayList<>()));
    }
}
```

#### AssetManager.java

```java
package com.bluboy.manager;

import java.util.*;

public class AssetManager implements Manager {
    private Map<String, Object> assetCache = new HashMap<>();

    @Override
    public void initialize() {
        // 初始化加载器
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void shutdown() {
        assetCache.clear();
    }

    @SuppressWarnings("unchecked")
    public <T> T loadAsset(String path, Class<T> type) {
        if (assetCache.containsKey(path)) {
            return (T) assetCache.get(path);
        }
        // 实际加载逻辑后续实现
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getAsset(String path, Class<T> type) {
        return (T) assetCache.get(path);
    }

    public void unloadAsset(String path) {
        assetCache.remove(path);
    }
}
```

#### RenderManager.java

```java
package com.bluboy.manager;

import java.awt.Graphics2D;

public class RenderManager implements Manager {
    @Override
    public void initialize() {
    }

    @Override
    public void update(float deltaTime) {
    }

    @Override
    public void shutdown() {
    }

    public void render(Graphics2D g2) {
        // 委托给分层渲染器
    }
}
```

#### PhysicsManager.java

```java
package com.bluboy.manager;

public class PhysicsManager implements Manager {
    @Override
    public void initialize() {
    }

    @Override
    public void update(float deltaTime) {
        // 更新物理，检测碰撞
    }

    @Override
    public void shutdown() {
    }
}
```

**依赖关系**：

- EntityManager 依赖 Entity 类（已存在）
- 其他 Manager 暂无具体依赖（框架阶段）

**验收标准**：

- ✅ 编译无错
- ✅ ServiceRegistry 能注册和获取所有 Manager
- ✅ 初始化顺序正确（AssetManager → EntityManager → PhysicsManager → RenderManager）
- ✅ 不影响现有代码

**实施注意事项（2026-02-28）**：

⚠️ **EntityManager 中的暂时注释**：

- `registerEntity()` 方法中有两处代码被注释：
  1. `entity.setId(id)` - Entity 类暂无 setId() 方法
  2. 组件索引更新逻辑 - Entity 类暂无 hasComponent() 方法
- **后续任务需完成**：
  - 在 Entity 类中添加 `private int id` 字段和 `setId(int id)` 方法
  - 在 Entity 类中添加 `hasComponent(ComponentType type)` 方法（基于 ComponentMap）
  - 取消 EntityManager.registerEntity() 中的注释
- **关联任务**：任务 2.1（Entity 类组件化改造）

**原子化提交**：

```
[CORE-1.4] feat: 创建 Manager 框架层 (ServiceRegistry, 5个Manager接口)
```

**状态**：✅ 完成

---

### ✅ 任务 1.5：创建具体状态类（1/2）

**优先级**：🟡 P1  
**工作量**：1.5 天  
**任务描述**：实现 TitleState, PlayState, PauseState

**涉及文件**：

```
✨ 新建 src/core/state/impl/TitleState.java
✨ 新建 src/core/state/impl/PlayState.java
✨ 新建 src/core/state/impl/PauseState.java
```

**详细实现要求**：

#### TitleState.java

```java
package com.bluboy.core.state.impl;

import java.awt.Graphics2D;
import com.bluboy.core.state.AbstractGameState;
import com.bluboy.core.state.GameStateManager;
import com.bluboy.core.state.GameStateType;

public class TitleState extends AbstractGameState {

    public TitleState(GameStateManager manager) {
        super(manager);
    }

    @Override
    public void enter(GameStateManager manager) {
        // 初始化菜单
    }

    @Override
    public void exit(GameStateManager manager) {
    }

    @Override
    public void update(GameStateManager manager) {
        // 空实现，逻辑在 handleInput 中
    }

    @Override
    public void render(GameStateManager manager, Graphics2D g2) {
        // 委托给旧 UI（暂时兼容）
        manager.getAdapter().ui.drawTitleScreen(g2);
    }

    @Override
    public void handleInput(GameStateManager manager) {
        // 暂时空实现，后续迁移 KeyHandler.titleState()
    }

    @Override
    public GameStateType getStateType() {
        return GameStateType.TITLE;
    }
}
```

#### PlayState.java

```java
package com.bluboy.core.state.impl;

import java.awt.Graphics2D;
import com.bluboy.core.state.AbstractGameState;
import com.bluboy.core.state.GameStateManager;
import com.bluboy.core.state.GameStateType;

public class PlayState extends AbstractGameState {

    public PlayState(GameStateManager manager) {
        super(manager);
    }

    @Override
    public void enter(GameStateManager manager) {
        // 重置游戏状态
    }

    @Override
    public void exit(GameStateManager manager) {
    }

    @Override
    public void update(GameStateManager manager) {
        // 暂时空实现，后续调用各 System
    }

    @Override
    public void render(GameStateManager manager, Graphics2D g2) {
        // 委托给旧渲染管线
        manager.getAdapter().drawToTempScreen(g2);
    }

    @Override
    public void handleInput(GameStateManager manager) {
        // 暂时空实现，后续迁移 KeyHandler.playState()
    }

    @Override
    public GameStateType getStateType() {
        return GameStateType.PLAY;
    }
}
```

#### PauseState.java

```java
package com.bluboy.core.state.impl;

import java.awt.Graphics2D;
import com.bluboy.core.state.AbstractGameState;
import com.bluboy.core.state.GameStateManager;
import com.bluboy.core.state.GameStateType;

public class PauseState extends AbstractGameState {

    public PauseState(GameStateManager manager) {
        super(manager);
    }

    @Override
    public void enter(GameStateManager manager) {
        // 暂停游戏
    }

    @Override
    public void exit(GameStateManager manager) {
        // 恢复游戏
    }

    @Override
    public void update(GameStateManager manager) {
        // 暂停时不更新游戏逻辑
    }

    @Override
    public void render(GameStateManager manager, Graphics2D g2) {
        // 显示原内容 + 暂停菜单
        manager.getAdapter().drawToTempScreen(g2);
        manager.getAdapter().ui.drawPauseMenu(g2);
    }

    @Override
    public void handleInput(GameStateManager manager) {
        // 暂时空实现
    }

    @Override
    public GameStateType getStateType() {
        return GameStateType.PAUSE;
    }
}
```

**依赖关系**：

- 依赖 GameState 接口
- 依赖 AbstractGameState 基类
- 依赖现有 UI（复用渲染）
- 依赖 GamePanelAdapter（引用）

**验收标准**：

- ✅ 编译无错
- ✅ 能成功切换到各状态
- ✅ 状态生命周期正确（enter/exit）
- ✅ 不影响现有游戏循环

**原子化提交**：

```
[CORE-1.5] feat: 实现状态类 TitleState, PlayState, PauseState
```

**状态**：⏳ 待执行

---

### ✅ 任务 1.6：创建具体状态类（2/2）

**优先级**：🟡 P1  
**工作量**：2 天  
**任务描述**：实现 DialogueState, GameOverState, 等其他状态

**涉及文件**：

```
✨ 新建 src/core/state/impl/DialogueState.java
✨ 新建 src/core/state/impl/GameOverState.java
✨ 新建 src/core/state/impl/CharacterState.java
✨ 新建 src/core/state/impl/OptionState.java
✨ 新建 src/core/state/impl/TradeState.java
✨ 新建 src/core/state/impl/MapState.java
✨ 新建 src/core/state/impl/CutsceneState.java
✨ 新建 src/core/state/impl/DebugState.java
✨ 新建 src/core/state/impl/TransitionState.java
```

**实现模板**（每个状态类结构相同）：

```java
// 示例：DialogueState
package com.bluboy.core.state.impl;

import java.awt.Graphics2D;
import com.bluboy.core.state.AbstractGameState;
import com.bluboy.core.state.GameStateManager;
import com.bluboy.core.state.GameStateType;

public class DialogueState extends AbstractGameState {

    public DialogueState(GameStateManager manager) {
        super(manager);
    }

    @Override
    public void enter(GameStateManager manager) {
        // 初始化对话状态
    }

    @Override
    public void exit(GameStateManager manager) {
        // 清理对话资源
    }

    @Override
    public void update(GameStateManager manager) {
        // 对话逻辑（暂时空实现）
    }

    @Override
    public void render(GameStateManager manager, Graphics2D g2) {
        manager.getAdapter().drawToTempScreen(g2);
        manager.getAdapter().ui.drawDialogueScreen(g2);
    }

    @Override
    public void handleInput(GameStateManager manager) {
        // 对话输入处理（暂时空实现）
    }

    @Override
    public GameStateType getStateType() {
        return GameStateType.DIALOGUE;
    }
}
```

**所有 9 个状态的映射关系**：

```
旧 gameState 值 → 新 GameStateType
─────────────────────────────────
0 (titleState)      → TITLE
1 (playState)       → PLAY
2 (pauseState)      → PAUSE
3 (dialogueState)   → DIALOGUE
4 (characterState)  → CHARACTER
5 (optionState)     → OPTION
6 (gameOverState)   → GAME_OVER
7 (transitionState) → TRANSITION
8 (tradeState)      → TRADE
9 (sleepState)      → SLEEP
10 (mapState)       → MAP
11 (cutsceneState)  → CUTSCENE
12 (debugState)     → DEBUG
```

**依赖关系**：

- 依赖 AbstractGameState
- 依赖现有 UI 类（复用渲染逻辑）

**验收标准**：

- ✅ 所有 9 个状态类编译无错
- ✅ 状态注册到 GameStateManager 成功
- ✅ 状态转换流程正确
- ✅ 不影响现有代码

**原子化提交**：

```
[CORE-1.6] feat: 实现状态类 DialogueState, GameOverState, 等7个状态
```

**状态**：⏳ 待执行

---

### ✅ 任务 1.7：创建 Adapter 适配层

**优先级**：🔴 P0（阻塞第一阶段完成）  
**工作量**：1 天  
**任务描述**：建立 GamePanelAdapter，桥接旧 GamePanel 和新框架

**涉及文件**：

```
✨ 新建 src/adapter/GamePanelAdapter.java
🔧 修改 src/main/GamePanel.java （仅添加 Adapter 字段）
```

**详细实现要求**：

#### GamePanelAdapter.java

```java
package com.bluboy.adapter;

import com.bluboy.core.state.GameStateManager;
import com.bluboy.core.state.GameStateType;
import com.bluboy.core.state.impl.*;
import com.bluboy.core.event.EventBus;
import com.bluboy.manager.ServiceRegistry;
import com.bluboy.manager.EntityManager;
import com.bluboy.manager.AssetManager;
import com.bluboy.manager.RenderManager;
import com.bluboy.manager.PhysicsManager;
import com.bluboy.main.GamePanel;
import java.awt.Graphics2D;

public class GamePanelAdapter {
    private GamePanel gamePanel;
    private GameStateManager gameStateManager;
    private ServiceRegistry serviceRegistry;
    private EventBus eventBus;

    public GamePanelAdapter(GamePanel gp) {
        this.gamePanel = gp;
    }

    public void initializeNewFramework() {
        // 1. 初始化 EventBus
        this.eventBus = EventBus.getInstance();

        // 2. 初始化 ServiceRegistry 和各 Manager
        this.serviceRegistry = ServiceRegistry.getInstance();
        serviceRegistry.register(EntityManager.class, new EntityManager());
        serviceRegistry.register(AssetManager.class, new AssetManager());
        serviceRegistry.register(RenderManager.class, new RenderManager());
        serviceRegistry.register(PhysicsManager.class, new PhysicsManager());
        serviceRegistry.initializeAll();

        // 3. 初始化 GameStateManager
        this.gameStateManager = GameStateManager.getInstance();
        gameStateManager.setAdapter(this);

        // 4. 注册所有状态
        gameStateManager.registerState(GameStateType.TITLE, new TitleState(gameStateManager));
        gameStateManager.registerState(GameStateType.PLAY, new PlayState(gameStateManager));
        gameStateManager.registerState(GameStateType.PAUSE, new PauseState(gameStateManager));
        gameStateManager.registerState(GameStateType.DIALOGUE, new DialogueState(gameStateManager));
        gameStateManager.registerState(GameStateType.CHARACTER, new CharacterState(gameStateManager));
        gameStateManager.registerState(GameStateType.OPTION, new OptionState(gameStateManager));
        gameStateManager.registerState(GameStateType.GAME_OVER, new GameOverState(gameStateManager));
        gameStateManager.registerState(GameStateType.TRANSITION, new TransitionState(gameStateManager));
        gameStateManager.registerState(GameStateType.TRADE, new TradeState(gameStateManager));
        gameStateManager.registerState(GameStateType.SLEEP, new SleepState(gameStateManager));
        gameStateManager.registerState(GameStateType.MAP, new MapState(gameStateManager));
        gameStateManager.registerState(GameStateType.CUTSCENE, new CutsceneState(gameStateManager));
        gameStateManager.registerState(GameStateType.DEBUG, new DebugState(gameStateManager));

        // 5. 设置初始状态为 TITLE
        gameStateManager.changeState(GameStateType.TITLE);
    }

    // 代理方法，便于访问 GamePanel 的字段和方法
    public GamePanel getGamePanel() { return gamePanel; }
    public GameStateManager getStateManager() { return gameStateManager; }
    public ServiceRegistry getServiceRegistry() { return serviceRegistry; }
    public EventBus getEventBus() { return eventBus; }

    // 以下为代理 GamePanel 的字段和方法（后续使用）
    public int getGameState() { return gamePanel.gameState; }
    public void setGameState(int state) { gamePanel.gameState = state; }

    // 屏幕和世界信息
    public int getScreenWidth() { return gamePanel.screenWidth; }
    public int getScreenHeight() { return gamePanel.screenHeight; }
    public int getMaxWorldCol() { return gamePanel.maxWorldCol; }
    public int getMaxWorldRow() { return gamePanel.maxWorldRow; }

    // UI 和输入
    public UI getUI() { return gamePanel.ui; }
    public KeyHandler getKeyHandler() { return gamePanel.keyH; }

    // 玩家
    public Player getPlayer() { return gamePanel.player; }

    // 渲染方法代理
    public void drawToTempScreen(Graphics2D g2) {
        gamePanel.drawToTempScreen();
    }

    public void drawToScreen(Graphics2D g2) {
        gamePanel.drawToScreen();
    }
}
```

#### GamePanel.java 修改

在 GamePanel 类中添加以下代码：

在类定义中添加字段：

```java
public GamePanelAdapter adapter;
```

在 `setupGame()` 方法末尾添加：

```java
// 初始化新框架（v2）
adapter = new GamePanelAdapter(this);
try {
    adapter.initializeNewFramework();
    System.out.println("[INFO] 新框架 (v2) 初始化成功");
} catch (Exception e) {
    System.err.println("[ERROR] 新框架初始化失败，将使用旧框架 (v1)");
    e.printStackTrace();
}
```

**依赖关系**：

- 依赖 GameStateManager
- 依赖 ServiceRegistry
- 依赖 EventBus
- 依赖 GamePanel

**验收标准**：

- ✅ Adapter 能成功初始化新框架
- ✅ GamePanel 的现有功能不受影响
- ✅ 可从 Adapter 获取和设置状态
- ✅ 编译无错，游戏能正常启动
- ✅ 运行时能看到"新框架初始化成功"日志

**原子化提交**：

```
[CORE-1.7] feat: 创建 GamePanelAdapter 适配层，初始化新框架
```

**状态**：✅ 完成

**完成报告**：[TASK_1.7_COMPLETION_REPORT.md](TASK_1.7_COMPLETION_REPORT.md)

---

### ⏳ 任务 1.8：第一阶段集成测试 + 文档

**优先级**：🟡 P1  
**工作量**：1.5 天  
**任务描述**：第一阶段完整集成测试和文档编写

**涉及文件**：

```
✨ 新建 ARCHITECTURE_v2.md
✨ 新建 docs/PHASE1_SUMMARY.md
✨ 新建 src/test/FrameworkTest.java （可选）
```

**详细实现要求**：

#### ARCHITECTURE_v2.md

```markdown
# Blue Boy Adventure - 新架构设计文档 (v2)

## 概述

本文档描述了蓝男孩冒险游戏的重构架构设计，基于以下设计模式：

- **状态模式 (State Pattern)**：管理游戏场景状态
- **观察者模式 (Observer Pattern)**：事件驱动系统
- **组件系统 (Component Architecture)**：实体属性管理
- **服务定位器模式 (Service Locator)**：管理器集中管理

## 架构层次

### 1. 核心框架层 (core/)

#### 事件系统 (core/event/)

- `EventBus`：事件总线单例，支持发布-订阅
- `GameEvent`：事件基类
- `EventListener`：监听器接口
- `GameEventType`：事件类型枚举

#### 状态管理 (core/state/)

- `GameState`：状态接口
- `GameStateManager`：状态管理器单例
- `AbstractGameState`：抽象基类
- `impl/`：13 个具体状态类

#### 组件系统 (core/ecs/)

- `Component`：组件接口
- `ComponentType`：组件类型枚举
- `ComponentMap`：组件容器
- `components/`：具体组件类
- `systems/`：系统处理类

### 2. 管理器层 (manager/)

5 个核心管理器：

- `EntityManager`：实体生命周期管理
- `AssetManager`：资源加载和缓存
- `RenderManager`：渲染管理
- `PhysicsManager`：物理和碰撞管理
- `ServiceRegistry`：服务定位器

### 3. 适配层 (adapter/)

- `GamePanelAdapter`：桥接 v1 和 v2，实现平滑迁移

## 设计类图

### 状态模式类图

\`\`\`
GameStateManager
↓ 持有 currentState
GameState (interface)
↑ 实现
AbstractGameState
↑ 继承
TitleState, PlayState, PauseState, ...（13个具体状态）
\`\`\`

### 观察者模式类图

\`\`\`
EventBus (singleton)
↓ contains
Map<GameEventType, List<EventListener>>
↓ contains
EventListener (interface)
↑ 实现
各具体监听器类
\`\`\`

### 组件系统类图

\`\`\`
Entity
↓ has
ComponentMap
↓ contains
Component (interface)
↑ 实现
TransformComponent, RenderComponent, PhysicsComponent, ...
\`\`\`

## 初始化顺序

1. GamePanel.setupGame()
2. GamePanelAdapter.initializeNewFramework()
3. ServiceRegistry.initializeAll()
   - AssetManager.initialize()
   - EntityManager.initialize()
   - PhysicsManager.initialize()
   - RenderManager.initialize()
4. GameStateManager.registerState() × 13
5. GameStateManager.changeState(TITLE)

## 状态转换流程

\`\`\`
TITLE → (ENTER) → PLAY
↓ ↓
(ESC) (ESC) → PAUSE → (ENTER) → PLAY
↓
Player dead → GAME_OVER
\`\`\`

## 与 v1 的兼容性

- v1 代码保留，冻结不修改
- Adapter 层逐步转发调用
- 配置文件可切换 useNewFramework 标志

## 后续迁移计划

- 阶段二：系统迁移（状态切换、事件驱动、数据配置化）
- 阶段三：深度优化（Entity 轻量化、AI 系统、性能优化）
```

#### docs/PHASE1_SUMMARY.md

```markdown
# 第一阶段总结报告

## 项目进度

**开始日期**：2026-02-28
**完成日期**：2026-03-??
**总工作量**：12 天
**状态**：已完成 ✅

## 任务清单

| #   | 任务              | 状态 | 提交       |
| --- | ----------------- | ---- | ---------- |
| 1.1 | 事件系统基础      | ✅   | [CORE-1.1] |
| 1.2 | 状态管理系统      | ✅   | [CORE-1.2] |
| 1.3 | 组件系统基础      | ✅   | [CORE-1.3] |
| 1.4 | Manager 框架层    | ✅   | [CORE-1.4] |
| 1.5 | 具体状态类（1/2） | ✅   | [CORE-1.5] |
| 1.6 | 具体状态类（2/2） | ✅   | [CORE-1.6] |
| 1.7 | Adapter 适配层    | ✅   | [CORE-1.7] |
| 1.8 | 集成测试 + 文档   | ✅   | [CORE-1.8] |

## 新增文件统计

### 新建包结构

\`\`\`
src/core/ # 框架核心
├── event/ # 事件系统（4个类）
│ ├── GameEvent.java
│ ├── GameEventType.java
│ ├── EventListener.java
│ └── EventBus.java
├── state/ # 状态管理（4个核心类）
│ ├── GameState.java
│ ├── GameStateType.java
│ ├── GameStateManager.java
│ ├── AbstractGameState.java
│ └── impl/ # 13 个状态实现
│ ├── TitleState.java
│ ├── PlayState.java
│ ├── PauseState.java
│ ├── DialogueState.java
│ ├── CharacterState.java
│ ├── OptionState.java
│ ├── GameOverState.java
│ ├── TransitionState.java
│ ├── TradeState.java
│ ├── SleepState.java
│ ├── MapState.java
│ ├── CutsceneState.java
│ └── DebugState.java
└── ecs/ # 组件系统（3个核心类）
├── Component.java
├── ComponentType.java
└── ComponentMap.java

src/manager/ # 管理器层（6个类）
├── Manager.java
├── ServiceRegistry.java
├── EntityManager.java
├── AssetManager.java
├── RenderManager.java
└── PhysicsManager.java

src/adapter/ # 适配层（1个类）
└── GamePanelAdapter.java
```

### 文件数量统计

- **新建 Java 类**：30+ 个
- **新建包**：5 个（event, state, state/impl, ecs, manager, adapter）
- **修改现有类**：1 个（GamePanel.java，仅添加 Adapter 字段）

### 代码行数统计

- **新增代码**：~2500 行
- **修改代码**：<10 行（仅在 GamePanel 中添加 Adapter）

## 关键成果

✅ **框架隔离**

- v1 代码完全保留，无任何修改
- v2 代码独立在 src/core 和 src/manager 包中
- 通过 Adapter 实现平滑过渡

✅ **完整性**

- 所有 13 个游戏状态都有对应的 GameState 实现
- EventBus 事件系统完全可用
- Manager 框架和 ServiceRegistry 已就位

✅ **兼容性**

- GamePanel 仍能使用 v1 代码正常运行
- Adapter 未强制使用，可通过配置启用/禁用
- 为后续平滑迁移奠定基础

✅ **可测试性**

- EventBus 单元测试可通过
- GameStateManager 单元测试可通过
- ComponentMap 单元测试可通过
- ServiceRegistry 集成测试可通过

## 架构验证

- ✅ 编译无错
- ✅ GamePanel 启动无异常
- ✅ 游戏可正常运行（v1 模式）
- ✅ Adapter 初始化成功，v2 框架就位
- ✅ 无性能回退

## 下一步计划

### 第二阶段（~23 天）

1. 迁移 KeyHandler 状态切换逻辑到各 GameState.handleInput()
2. 创建具体事件类和监听器
3. 完善 Manager 实现（EntityManager、AssetManager 等）
4. 实现 Component 和 System 类
5. 数据驱动化 EventHandler（events.json）
6. 整合 PlayState，调用各 Manager
7. 更新 GamePanel.run()，使用新状态管理器
8. 创建框架配置系统

### 第三阶段（~12 天）

1. Entity 轻量化
2. 分离 Player/Monster/NPC 特有逻辑
3. 物品系统 GameObject 化
4. AI 状态机完整实现
5. UI 组件化
6. 输入系统命令模式化
7. 性能优化（空间分割、对象池）
8. 移除 v1 代码

## 风险评估

### 低风险 ✅

- GamePanel 修改最小化（仅添加一个字段）
- v1 和 v2 完全隔离
- 无破坏性修改

### 中等风险 ⚠️

- 阶段二迁移输入系统时需注意兼容性
- 事件配置化需确保与旧硬编码逻辑一致

### 高风险 ❌

- （目前无高风险项）

## 建议

1. **备份**：在开始阶段二前，提交当前代码到 Git
2. **分支**：为第二、三阶段分别创建 feature 分支
3. **测试**：每个阶段完成后进行集成测试
4. **文档**：同步更新设计文档

---

**报告生成日期**：2026-03-??
**编制人**：系统重构小组

```

**依赖关系**：依赖前面 7 个任务

**验收标准**：
- ✅ ARCHITECTURE_v2.md 清晰完整
- ✅ PHASE1_SUMMARY.md 准确总结进度
- ✅ 所有集成测试通过
- ✅ 游戏能正常启动和运行

**原子化提交**：
```

[CORE-1.8] test: 第一阶段集成测试 + 文档

[DOC-1] docs: 编写 ARCHITECTURE_v2.md 和 PHASE1_SUMMARY.md

```

**状态**：⏳ 待执行

---

## ✅ 第一阶段总结

| 任务编号 | 任务名称 | 优先级 | 工作量 | 状态 |
|---------|---------|--------|--------|------|
| 1.1 | 事件系统基础 | P0 | 1 天 | ⏳ |
| 1.2 | 状态管理系统 | P0 | 1.5 天 | ⏳ |
| 1.3 | 组件系统基础 | P0 | 1 天 | ⏳ |
| 1.4 | Manager 框架层 | P0 | 2 天 | ⏳ |
| 1.5 | 具体状态类（1/2） | P1 | 1.5 天 | ⏳ |
| 1.6 | 具体状态类（2/2） | P1 | 2 天 | ⏳ |
| 1.7 | Adapter 适配层 | P0 | 1 天 | ⏳ |
| 1.8 | 集成测试 + 文档 | P1 | 1.5 天 | ⏳ |
| **合计** | | | **12 天** | |

**第一阶段产物**：
- ✨ 30+ 个新 Java 类
- ✨ 5 个新包结构
- ✨ 完整的事件、状态、组件、管理器框架
- ✨ 与旧代码完全隔离

**关键成果**：
- ✅ 新框架与旧代码完全隔离，互不影响
- ✅ GamePanel 仅添加 Adapter 字段，无其他改动
- ✅ 所有新代码可编译、可测试、可独立运行
- ✅ 为第二阶段的系统迁移奠定坚实基础

---

## 🔄 第二阶段：系统迁移（14 项任务，~3-4 周）

> **目标**：将现有系统逐步迁移到新框架，保持功能不变
> **关键原则**：确保每一步都可运行验证，无破坏性修改

### ✅ 任务 2.1：迁移 KeyHandler 到新状态系统

**优先级**：🔴 P0
**工作量**：2 天
**预期完成**：第 13-14 天

**任务描述**：将 KeyHandler 的状态切换逻辑迁移到各 GameState

**涉及文件**：
```

🔧 修改 src/core/state/impl/TitleState.java
🔧 修改 src/core/state/impl/PlayState.java
🔧 修改 src/core/state/impl/PauseState.java
🔧 修改 src/core/state/impl/DialogueState.java
🔧 修改 src/core/state/impl/CharacterState.java
🔧 修改 src/core/state/impl/OptionState.java
🔧 修改 src/core/state/impl/GameOverState.java
🔧 修改 src/core/state/impl/TradeState.java
🔧 修改 src/core/state/impl/MapState.java
🔧 修改 src/core/state/impl/CutsceneState.java
📌 不修改 src/main/KeyHandler.java （保留兼容）

```

**详细需求**：

将 KeyHandler.java 中的每个状态方法逻辑迁移到对应状态类的 handleInput()：

```

原 KeyHandler.java → 新 GameState.handleInput()
─────────────────────────────────────────────────────────
titleState() → TitleState.handleInput()
playState() → PlayState.handleInput()
pauseState() → PauseState.handleInput()
dialogueState() → DialogueState.handleInput()
characterState() → CharacterState.handleInput()
optionState() → OptionState.handleInput()
gameOverState() → GameOverState.handleInput()
tradeState() → TradeState.handleInput()
mapState() → MapState.handleInput()
cutsceneState() → CutsceneState.handleInput()

````

**示例（TitleState.handleInput() 的完整实现）**：

从 KeyHandler.titleState() 中提取逻辑：
```java
if(keyHandler.upPressed) {
    ui.commandNum--;
    if (ui.commandNum < 0) ui.commandNum = 2;
    upPressed = false;
}
if(keyHandler.downPressed) {
    ui.commandNum++;
    if (ui.commandNum > 2) ui.commandNum = 0;
    downPressed = false;
}
if(keyHandler.enterPressed) {
    if (ui.commandNum == 0) {
        gameState = playState;
    }
    if (ui.commandNum == 1) {
        // 打开设置菜单
    }
    if (ui.commandNum == 2) {
        System.exit(0);
    }
    enterPressed = false;
}
````

迁移到：

```java
@Override
public void handleInput(GameStateManager manager) {
    GamePanelAdapter adapter = manager.getAdapter();
    KeyHandler keyH = adapter.getKeyHandler();
    UI ui = adapter.getUI();

    if (keyH.upPressed) {
        ui.commandNum--;
        if (ui.commandNum < 0) ui.commandNum = 2;
        keyH.upPressed = false;
    }
    if (keyH.downPressed) {
        ui.commandNum++;
        if (ui.commandNum > 2) ui.commandNum = 0;
        keyH.downPressed = false;
    }
    if (keyH.enterPressed) {
        if (ui.commandNum == 0) {
            manager.changeState(GameStateType.PLAY);
        }
        if (ui.commandNum == 1) {
            // TODO: 打开设置菜单
        }
        if (ui.commandNum == 2) {
            System.exit(0);
        }
        keyH.enterPressed = false;
    }
}
```

**PlayState.handleInput() 的框架**：

```java
@Override
public void handleInput(GameStateManager manager) {
    GamePanelAdapter adapter = manager.getAdapter();
    KeyHandler keyH = adapter.getKeyHandler();
    GamePanel gp = adapter.getGamePanel();

    // 暂时完全代理给旧 KeyHandler.playState()
    // 后续可逐步迁移细节
    if (keyH.upPressed) gp.player.direction = "up";
    if (keyH.downPressed) gp.player.direction = "down";
    // ... 其他输入处理
}
```

**依赖关系**：

- 依赖 GameState 接口
- 依赖 KeyHandler（读取输入标志）
- 依赖 GameStateManager（状态转换）
- 依赖 GamePanelAdapter（访问 GamePanel 资源）

**验收标准**：

- ✅ 所有状态的 handleInput() 实现完整
- ✅ 可从 TitleState 切换到 PlayState
- ✅ 暂停/继续游戏正常工作
- ✅ 对话/菜单导航正常
- ✅ 旧 KeyHandler 行为不变（兼容）
- ✅ 编译无错，游戏可玩

**原子化提交**：

```
[STATE-2.1] refactor: 迁移 KeyHandler 状态切换到各 GameState.handleInput()
```

**状态**：⏳ 待执行

---

### 更多第二、三阶段任务...

_（由于篇幅限制，以下是任务概览，完整详情见最后的附录）_

**第二阶段剩余任务**：

- 2.2 创建事件类库（1 天）
- 2.3 创建事件监听器库（1.5 天）
- 2.4 实现 EntityManager 核心逻辑（2 天）
- 2.5 实现 AssetManager 核心逻辑（1.5 天）
- 2.6 实现 RenderManager 核心逻辑（2 天）
- 2.7 实现 PhysicsManager 核心逻辑（2 天）
- 2.8 创建基础 Component 实现（1.5 天）
- 2.9 创建基础 System 实现（2 天）
- 2.10 迁移 EventHandler 硬编码事件到 JSON 配置（2 天）
- 2.11 集成 PlayState 与新框架（1.5 天）
- 2.12 更新 GamePanel.run() 游戏循环（1 天）
- 2.13 创建框架过渡配置系统（0.5 天）
- 2.14 第二阶段集成测试 + 文档（1.5 天）

**第三阶段任务**：

- 3.1 Entity 轻量化
- 3.2 分离 Player/Monster/NPC 特有逻辑
- 3.3 物品系统 GameObject 化
- 3.4 投射物和粒子系统升级
- 3.5 行为树/AI 状态机完整实现
- 3.6 UI 组件化
- 3.7 输入系统命令模式化
- 3.8 性能优化：空间分割和对象池
- 3.9 移除 v1 代码和清理
- 3.10 最终优化和性能基准测试

---

## 📊 全体任务进度表

| 阶段        | 任务数 | 总工作量  | 状态      | 完成日期预期   |
| ----------- | ------ | --------- | --------- | -------------- |
| 🟦 第一阶段 | 8      | 12 天     | ⏳ 未开始 | 2026-03-12     |
| 🟥 第二阶段 | 14     | 23 天     | ⏳ 未开始 | 2026-04-04     |
| 🟩 第三阶段 | 10     | 12 天     | ⏳ 未开始 | 2026-04-16     |
| **总计**    | **32** | **47 天** |           | **2026-04-16** |

---

## 🎯 使用指南

### 开始第一阶段

1. **克隆仓库并创建分支**：

   ```bash
   cd e:\project software\blue-boy-remake\Blue-Boy-Adventure-origin
   git branch feature/refactor-phase-1
   git checkout feature/refactor-phase-1
   ```

2. **依次完成任务 1.1 至 1.8**：
   - 每个任务都是独立的，按顺序执行
   - 每个任务的验收标准必须全部满足

3. **提交每个任务**：

   ```bash
   git add -A
   git commit -m "[CORE-1.1] feat: 建立事件系统基础类"
   ```

4. **测试运行**：

   ```bash
   # 编译
   cd bin
   javac -encoding UTF-8 -d . ../src/**/*.java

   # 运行
   java -cp . com.bluboy.main.Main
   ```

### 每个任务的流程

1. **理解需求**：仔细阅读任务描述和实现要求
2. **创建文件**：按照"涉及文件"列表创建新文件或修改现有文件
3. **实现代码**：参照"详细实现要求"的代码模板
4. **验证编译**：确保代码编译无错
5. **运行测试**：根据"验收标准"进行测试
6. **提交代码**：按"原子化提交"的消息格式提交
7. **标记完成**：在本文件中更新任务状态（⏳ → ✅）

### 问题排查

**编译错误**：

- 检查包名是否正确
- 检查 import 语句是否完整
- 检查依赖文件是否已创建

**运行错误**：

- 检查 GamePanel 是否正确初始化 Adapter
- 检查日志输出，查找异常堆栈
- 检查 GamePanel 现有逻辑是否被破坏

**兼容性问题**：

- 确保旧代码（src/main, src/entity 等）未被修改
- 确保新代码（src/core, src/manager 等）独立存在
- 使用 Adapter 进行桥接

---

## 📚 附录：完整设计参考

### 包结构设计

```
src/
├── main/                          # 保留：程序入口和旧核心
│   ├── Main.java
│   ├── GamePanel.java            # 修改：添加 Adapter 字段
│   ├── Config.java
│   ├── KeyHandler.java
│   ├── EventHandler.java
│   ├── CollisionChecker.java
│   ├── UI.java
│   ├── Sound.java
│   └── ...
│
├── core/                          # 新增：框架核心
│   ├── event/                     # 事件系统
│   │   ├── GameEvent.java
│   │   ├── GameEventType.java
│   │   ├── EventListener.java
│   │   ├── EventBus.java
│   │   ├── events/               # 具体事件
│   │   └── listeners/            # 具体监听器
│   │
│   ├── state/                     # 状态管理
│   │   ├── GameState.java
│   │   ├── GameStateType.java
│   │   ├── GameStateManager.java
│   │   ├── AbstractGameState.java
│   │   └── impl/                 # 13 个状态实现
│   │
│   └── ecs/                       # 组件系统
│       ├── Component.java
│       ├── ComponentType.java
│       ├── ComponentMap.java
│       ├── components/           # 具体组件
│       └── systems/              # 系统处理
│
├── manager/                       # 新增：管理器层
│   ├── Manager.java
│   ├── ServiceRegistry.java
│   ├── EntityManager.java
│   ├── AssetManager.java
│   ├── RenderManager.java
│   └── PhysicsManager.java
│
├── adapter/                       # 新增：适配层
│   └── GamePanelAdapter.java
│
├── entity/                        # 保留：实体类（第三阶段重构）
│   ├── Entity.java
│   ├── Player.java
│   ├── Projectile.java
│   ├── Particle.java
│   └── ...
│
├── monster/                       # 保留：怪物类
├── npc/                           # 保留：NPC 类
├── object/                        # 保留：物品类
├── ai/                            # 保留：AI 类
├── data/                          # 保留：存档系统
├── tile/                          # 保留：瓦片系统
├── util/                          # 保留：工具类
└── test/                          # 新增：测试类（可选）
```

### 关键类间通信方式

**GamePanel → Adapter → StateManager → State**

```
GamePanel.run()
    ↓
adapter.getStateManager().handleInput()
adapter.getStateManager().update()
adapter.getStateManager().render(g2)
    ↓
GameState.handleInput() / update() / render()
```

**State → EventBus → Listeners**

```
GameState.update()
    ↓
EventBus.publish(new PlayerLevelUpEvent(...))
    ↓
ItemDropListener.onEvent(event)
UIUpdateListener.onEvent(event)
SoundEffectListener.onEvent(event)
```

**State → Manager → Entity/Component**

```
PlayState.update()
    ↓
ServiceRegistry.getManager(EntityManager.class)
    ↓
EntityManager.getEntitiesWithComponent(PHYSICS)
    ↓
PhysicsSystem.update(entity)
```

---

**文件生成时间**：2026-02-28  
**文件位置**：项目根目录  
**最后更新**：2026-02-28

---

## 🧪 新架构测试说明

### 测试文件位置

`src/test/NewArchitectureTest.java` - 独立测试类，验证任务 1.1-1.4 的新架构代码

### 测试使用方法

#### 方法 1：命令行运行（推荐）

```bash
# 进入项目目录
cd "e:\project software\blue-boy-remake\Blue-Boy-Adventure-origin"

# 编译测试类
javac -d bin -cp bin src/test/NewArchitectureTest.java

# 运行测试
java -cp bin test.NewArchitectureTest
```

#### 方法 2：一键运行

```bash
# Windows PowerShell
e:; cd 'e:\project software\blue-boy-remake\Blue-Boy-Adventure-origin'; javac -d bin -cp bin src/test/NewArchitectureTest.java; java -cp bin test.NewArchitectureTest
```

### 测试覆盖内容

| 测试项 | 测试内容                        | 验证点                                               |
| ------ | ------------------------------- | ---------------------------------------------------- |
| 测试 1 | 事件系统（EventBus）            | 单例创建、事件订阅、事件发布、事件清理               |
| 测试 2 | 状态管理（GameStateManager）    | 单例创建、状态注册、状态切换、状态查询               |
| 测试 3 | 组件系统（Component）           | ComponentMap 创建、组件添加、获取、移除、生命周期    |
| 测试 4 | Manager 框架（ServiceRegistry） | 单例创建、Manager 注册、获取、初始化顺序、更新和关闭 |

### 预期输出示例

```
========================================
新架构测试开始
========================================

【测试 1】事件系统（EventBus）
----------------------------------------
✓ EventBus 单例创建成功
✓ 事件监听器订阅成功
  → 接收到事件: PLAYER_LEVEL_UP (时间戳: 1772277947462)
✓ 事件发布成功
✓ EventBus 清理完成

【测试 2】状态管理系统（GameStateManager）
----------------------------------------
✓ GameStateManager 单例创建成功
✓ 状态注册成功
  → 进入测试状态
✓ 状态切换成功
✓ 当前状态: TITLE

【测试 3】组件系统（Component & ComponentMap）
----------------------------------------
✓ ComponentMap 创建成功
  → 组件初始化
✓ 组件添加成功
✓ 组件存在检查: true
✓ 组件获取成功
  → 组件更新 (deltaTime: 0.016)
  → 组件销毁
✓ 组件移除成功

【测试 4】Manager 框架（ServiceRegistry）
----------------------------------------
✓ ServiceRegistry 单例创建成功
✓ 4 个 Manager 注册成功
✓ AssetManager 获取成功: true
✓ EntityManager 获取成功: true

初始化所有 Manager（Asset → Entity → Physics → Render）:
✓ 所有 Manager 初始化成功

测试 Manager 更新:
✓ Manager 更新测试完成
✓ 所有 Manager 关闭成功

========================================
所有测试完成！
========================================
```

### 测试状态

- ✅ 所有测试通过（2026-02-28）
- ✅ 新架构代码工作正常
- ✅ 与现有游戏代码完全隔离
- ⏳ 等待后续任务集成到游戏主循环

### 相关文档

详细测试说明请参考：[docs/NEW_ARCHITECTURE_TESTING.md](docs/NEW_ARCHITECTURE_TESTING.md)

---

## 🚀 立即开始

**下一步行动**：按顺序执行任务 1.1 至 1.8

**预计耗时**：12 天（约 2 周）

**联系方式**：如有疑问，请参考本文档或项目 README.md
