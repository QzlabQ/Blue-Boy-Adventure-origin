# 项目目录结构说明

**更新时间**：2026-02-28  
**阶段**：重构 v2 框架搭建完成

---

## 📁 完整目录树（标记重构相关）

```
Blue-Boy-Adventure-origin/
├── 📄 README.md                          # 游戏说明文档（已更新，新增重构链接）
├── 📄 REMAKE_TODO.md                     # ✨ 【新建】重构任务清单（详细的 32 项任务）
├── 📄 ARCHITECTURE_v2.md                 # ✨ 【新建】新架构设计文档（完整的设计说明）
├── 📄 TODO.md                            # 原有任务列表
├── 📄 config.txt                         # 配置文件
├── 📁 save/                              # 存档目录
│
├── 📁 res/                               # 资源目录（保留）
│   ├── font/
│   ├── maps/
│   │   ├── interior01.txt
│   │   ├── map.txt
│   │   ├── mydungeon01.txt
│   │   ├── mydungeon02.txt
│   │   └── worldV3.txt
│   ├── monster/
│   ├── npc/
│   ├── objects/
│   ├── player/
│   ├── projectile/
│   ├── sound/
│   ├── tiles/
│   └── tiles_interactive/
│
├── 📁 bin/                               # 编译输出目录
│   ├── ai/
│   ├── data/
│   ├── entity/
│   ├── environment/
│   ├── font/
│   ├── main/
│   ├── maps/
│   │   └── （同 res/maps/）
│   ├── maptool/
│   ├── monster/
│   ├── npc/
│   ├── object/
│   ├── objects/
│   ├── player/
│   ├── projectile/
│   ├── sound/
│   ├── tile/
│   ├── tile_interactive/
│   ├── tiles/
│   ├── tiles_interactive/
│   └── （输出的 .class 文件）
│
└── 📁 src/                               # 源代码目录
    │
    ├── 🔴 main/                          # 【v1 保留，未改动】原核心包
    │   ├── Main.java
    │   ├── GamePanel.java                # 🔧 仅添加了 Adapter 字段
    │   ├── Config.java
    │   ├── KeyHandler.java
    │   ├── EventHandler.java
    │   ├── CollisionChecker.java
    │   ├── UI.java
    │   ├── Sound.java
    │   ├── AssetSetter.java
    │   ├── UtilityTool.java
    │   ├── CutsceneManager.java
    │   ├── EnvironmentManager.java
    │   ├── EntityGenerator.java
    │   ├── GameMap.java
    │   ├── MapData.java
    │   ├── EventRect.java
    │   └── Lighting.java
    │
    ├── 🔴 entity/                        # 【v1 保留，未改动】实体基类
    │   ├── Entity.java
    │   ├── Player.java
    │   ├── PlayerDummy.java
    │   ├── Projectile.java
    │   └── Particle.java
    │
    ├── 🔴 monster/                       # 【v1 保留，未改动】怪物类
    │   ├── MON_Bat.java
    │   ├── MON_GreenSlime.java
    │   ├── MON_Orc.java
    │   ├── MON_RedSlime.java
    │   └── MON_SkeletonLord.java
    │
    ├── 🔴 npc/                           # 【v1 保留，未改动】NPC 类
    │   ├── NPC_BigRock.java
    │   ├── NPC_Merchant.java
    │   └── NPC_OldMan.java
    │
    ├── 🔴 object/                        # 【v1 保留，未改动】物品类
    │   ├── OBJ_AirWall.java
    │   ├── OBJ_Axe.java
    │   ├── OBJ_Blueheart.java
    │   ├── OBJ_Boots.java
    │   ├── OBJ_Chest.java
    │   ├── OBJ_Coin_Bronze.java
    │   ├── OBJ_Door.java
    │   ├── OBJ_Door_Iron.java
    │   ├── OBJ_Fireball.java
    │   ├── OBJ_Heart.java
    │   ├── OBJ_Key.java
    │   ├── OBJ_Lantern.java
    │   ├── OBJ_ManaCrystal.java
    │   ├── OBJ_Pickaxe.java
    │   ├── OBJ_Potion_Red.java
    │   ├── OBJ_Rock.java
    │   ├── OBJ_Shield_Blue.java
    │   ├── OBJ_Shield_Wood.java
    │   ├── OBJ_Sword_Normal.java
    │   └── OBJ_Tent.java
    │
    ├── 🔴 ai/                            # 【v1 保留，未改动】AI 系统
    │   ├── PathFinder.java
    │   └── Node.java
    │
    ├── 🔴 data/                          # 【v1 保留，未改动】存档系统
    │   ├── DataStorage.java
    │   ├── Progress.java
    │   └── SaveLoad.java
    │
    ├── 🔴 tile/                          # 【v1 保留，未改动】瓦片系统
    │   ├── GameMap.java
    │   ├── Tile.java
    │   └── TileManager.java
    │
    ├── 🔴 tile_interactive/              # 【v1 保留，未改动】交互瓦片
    │   ├── InteractiveTile.java
    │   ├── IT_DestructibleWall.java
    │   ├── IT_DryTree.java
    │   ├── IT_MetalPlate.java
    │   └── IT_Trunk.java
    │
    ├── 🔴 environment/                   # 【v1 保留，未改动】环境系统
    │   ├── EnvironmentManager.java
    │   └── Lighting.java
    │
    ├── 🔴 maptool/                       # 【v1 保留，未改动】地图编辑器
    │   ├── EntityEntry.java
    │   ├── EntityImageCache.java
    │   ├── MapEditor.java
    │   ├── MapEditorManager.java
    │   ├── TileInfo.java
    │   └── TileSet.java
    │
    │
    ├── ✨ 🟢 core/                       # 【新建】框架核心层
    │   │
    │   ├── 📁 event/                     # 事件系统（观察者模式）
    │   │   ├── GameEvent.java            # 事件基类
    │   │   ├── GameEventType.java        # 事件类型枚举
    │   │   ├── EventListener.java        # 监听器接口
    │   │   ├── EventBus.java             # 事件总线（单例）
    │   │   ├── 📁 events/                # 【阶段二创建】具体事件类
    │   │   │   ├── PlayerLevelUpEvent.java
    │   │   │   ├── ItemPickupEvent.java
    │   │   │   ├── EntityDeathEvent.java
    │   │   │   ├── DialogueStartEvent.java
    │   │   │   ├── StateChangeEvent.java
    │   │   │   ├── CollisionEvent.java
    │   │   │   ├── DamageTakenEvent.java
    │   │   │   ├── HealEvent.java
    │   │   │   ├── MapChangeEvent.java
    │   │   │   └── CutsceneEvent.java
    │   │   └── 📁 listeners/             # 【阶段二创建】具体监听器
    │   │       ├── ItemDropListener.java
    │   │       ├── UIUpdateListener.java
    │   │       ├── SoundEffectListener.java
    │   │       ├── ParticleEffectListener.java
    │   │       └── SaveGameListener.java
    │   │
    │   ├── 📁 state/                     # 状态管理（状态模式）
    │   │   ├── GameState.java            # 状态接口
    │   │   ├── GameStateType.java        # 状态类型枚举
    │   │   ├── GameStateManager.java     # 状态管理器（单例）
    │   │   ├── AbstractGameState.java    # 抽象基类
    │   │   └── 📁 impl/                  # 13 个具体状态实现
    │   │       ├── TitleState.java       # 标题画面
    │   │       ├── PlayState.java        # 游戏进行
    │   │       ├── PauseState.java       # 暂停菜单
    │   │       ├── DialogueState.java    # 对话框
    │   │       ├── CharacterState.java   # 角色面板
    │   │       ├── OptionState.java      # 选项菜单
    │   │       ├── GameOverState.java    # 游戏结束
    │   │       ├── TransitionState.java  # 过渡动画
    │   │       ├── TradeState.java       # 商人交易
    │   │       ├── SleepState.java       # 睡眠
    │   │       ├── MapState.java         # 全地图显示
    │   │       ├── CutsceneState.java    # 过场动画
    │   │       └── DebugState.java       # 调试模式
    │   │
    │   └── 📁 ecs/                       # 组件系统（组件架构）
    │       ├── Component.java            # 组件接口
    │       ├── ComponentType.java        # 组件类型枚举
    │       ├── ComponentMap.java         # 组件容器
    │       ├── 📁 components/            # 【阶段二创建】具体组件
    │       │   ├── TransformComponent.java    # 位置、速度、朝向
    │       │   ├── RenderComponent.java      # 图像资源、动画状态
    │       │   ├── PhysicsComponent.java     # 碰撞体、质量
    │       │   ├── HealthComponent.java      # 生命值、魔法值
    │       │   ├── AIComponent.java          # AI 状态机引用
    │       │   ├── CombatComponent.java      # 战斗属性
    │       │   ├── InventoryComponent.java   # 背包、装备槽
    │       │   └── DialogueComponent.java    # NPC 对话数据
    │       └── 📁 systems/               # 【阶段二创建】系统处理类
    │           ├── System.java           # 系统接口
    │           ├── PhysicsSystem.java    # 移动、碰撞检测
    │           ├── AISystem.java         # AI 决策执行
    │           ├── AnimationSystem.java  # 动画帧更新
    │           ├── RenderSystem.java     # 渲染管线
    │           └── CombatSystem.java     # 战斗计算
    │
    │
    ├── ✨ 🟢 manager/                    # 【新建】管理器层
    │   ├── Manager.java                  # Manager 接口
    │   ├── ServiceRegistry.java          # 服务定位器（单例）
    │   ├── EntityManager.java            # 实体生命周期管理
    │   ├── AssetManager.java             # 资源加载和缓存
    │   ├── RenderManager.java            # 渲染管理
    │   └── PhysicsManager.java           # 物理和碰撞管理
    │
    │
    ├── ✨ 🟢 adapter/                    # 【新建】适配层
    │   └── GamePanelAdapter.java         # 桥接 v1 和 v2
    │
    │
    ├── 📁 world/                         # 【阶段二创建】世界系统
    │   ├── GameWorld.java                # 世界管理器
    │   ├── Map.java                      # 地图抽象
    │   ├── EventTrigger.java             # 事件触发器（数据驱动）
    │   ├── TriggerConfig.java            # 触发器配置
    │   └── Cutscene.java                 # 过场动画数据
    │
    ├── 📁 physics/                       # 【阶段二创建】物理层
    │   ├── CollisionDetector.java        # 碰撞检测器
    │   ├── CollisionResolver.java        # 碰撞响应
    │   ├── Bounds.java                   # 碰撞体抽象
    │   └── PhysicsGrid.java              # 空间分割（可选）
    │
    ├── 📁 rendering/                     # 【阶段二创建】渲染层
    │   ├── Camera.java                   # 摄像机
    │   ├── RenderLayer.java              # 渲染层接口
    │   ├── LayerRenderer.java            # 分层渲染器
    │   ├── RenderCommand.java            # 渲染命令
    │   ├── SpriteSheet.java              # 精灵图管理
    │   └── Animation.java                # 动画序列
    │
    ├── 📁 ui/                            # 【阶段二/三改进】UI 系统
    │   ├── UIComponent.java              # UI 组件接口
    │   ├── UIManager.java                # UI 管理器
    │   ├── 📁 components/                # 拆分的 UI 组件
    │   │   ├── TitleUI.java
    │   │   ├── HudUI.java
    │   │   ├── DialogueUI.java
    │   │   ├── InventoryUI.java
    │   │   ├── MapUI.java
    │   │   └── GameOverUI.java
    │   └── 📁 widgets/                   # 可复用 UI 组件
    │       ├── Button.java
    │       ├── HealthBar.java
    │       └── ItemSlot.java
    │
    ├── 📁 input/                         # 【阶段二/三创建】输入系统
    │   ├── InputHandler.java             # 输入处理器
    │   ├── Command.java                  # 命令接口
    │   └── 📁 commands/                  # 具体命令
    │       ├── MoveCommand.java
    │       ├── AttackCommand.java
    │       └── InteractCommand.java
    │
    ├── 📁 util/                          # 【阶段二/三创建】工具类
    │   ├── AssetLoader.java              # 统一资源加载
    │   ├── ConfigLoader.java             # JSON/XML 配置加载
    │   └── MathUtils.java                # 数学工具
    │
    └── 📁 test/                          # 【可选】测试类
        └── FrameworkTest.java            # 集成测试

```

---

## 📊 目录统计

### v1 代码（保留，冻结）

| 包                | 文件数 | 状态     |
| ----------------- | ------ | -------- |
| main/             | 14     | 🔴 冻结  |
| entity/           | 5      | 🔴 冻结  |
| monster/          | 5      | 🔴 冻结  |
| npc/              | 3      | 🔴 冻结  |
| object/           | 19     | 🔴 冻结  |
| ai/               | 2      | 🔴 冻结  |
| data/             | 3      | 🔴 冻结  |
| tile/             | 3      | 🔴 冻结  |
| tile_interactive/ | 5      | 🔴 冻结  |
| environment/      | 2      | 🔴 冻结  |
| maptool/          | 6      | 🔴 冻结  |
| **合计**          | **67** | **冻结** |

### v2 代码（新增）

| 包                    | 文件数 | 来源      | 完成度       |
| --------------------- | ------ | --------- | ------------ |
| core/event/           | 4      | 阶段一    | ✅ 完成      |
| core/event/events/    | 10     | 阶段二    | ⏳ 待建      |
| core/event/listeners/ | 5      | 阶段二    | ⏳ 待建      |
| core/state/           | 16     | 阶段一    | ✅ 完成      |
| core/ecs/             | 3      | 阶段一    | ✅ 完成      |
| core/ecs/components/  | 8      | 阶段二    | ⏳ 待建      |
| core/ecs/systems/     | 6      | 阶段二    | ⏳ 待建      |
| manager/              | 6      | 阶段一    | ✅ 完成      |
| adapter/              | 1      | 阶段一    | ✅ 完成      |
| world/                | 5      | 阶段二    | ⏳ 待建      |
| physics/              | 4      | 阶段二    | ⏳ 待建      |
| rendering/            | 6      | 阶段二    | ⏳ 待建      |
| ui/ 改进              | 11     | 阶段三    | ⏳ 待建      |
| input/                | 4      | 阶段二/三 | ⏳ 待建      |
| util/                 | 3      | 阶段二/三 | ⏳ 待建      |
| test/                 | 1      | 阶段一    | ✅ 完成      |
| **合计**              | **93** |           | **✅ 30/93** |

### 总体统计

- **v1 代码**：67 个文件（保留，未修改）
- **v2 代码**：93 个文件（新增，逐步建设）
- **总计**：160 个源代码文件
- **现状**：阶段一完成，32% 的 v2 代码已实现

---

## 🚀 文件位置导航

### 开始阶段一开发

1. **查看任务清单**：[REMAKE_TODO.md](../REMAKE_TODO.md)
2. **了解架构设计**：[ARCHITECTURE_v2.md](../ARCHITECTURE_v2.md)
3. **创建 core/event 包**：`src/core/event/`
4. **创建 core/state 包**：`src/core/state/`
5. **创建 core/ecs 包**：`src/core/ecs/`
6. **创建 manager 包**：`src/manager/`
7. **创建 adapter 包**：`src/adapter/`

### 编译输出

- 编译后的 `.class` 文件输出到 `bin/` 目录
- 运行命令：`java -cp bin com.bluboy.main.Main`

### 资源文件

- 游戏资源（图片、声音、字体）：`res/`
- 地图数据：`res/maps/`
- 配置文件：`res/config/`（阶段二创建）

### 存档文件

- 游戏存档：`save/`

---

## 🔄 包名规范

所有新 v2 代码遵循以下包名规范：

```
com.bluboy.core.event      # 事件系统
com.bluboy.core.state      # 状态管理
com.bluboy.core.ecs        # 组件系统
com.bluboy.manager         # 管理器层
com.bluboy.adapter         # 适配层
com.bluboy.world           # 世界系统
com.bluboy.physics         # 物理层
com.bluboy.rendering       # 渲染层
com.bluboy.ui              # UI 系统
com.bluboy.input           # 输入系统
com.bluboy.util            # 工具类
```

**注意**：v1 代码保持原有包名（如 `com.bluboy.main`, `com.bluboy.entity` 等）。

---

## 📝 提交规范

每次提交遵循以下格式：

```bash
# 核心框架提交
[CORE-1.1] feat: 建立事件系统基础类

# 状态系统提交
[STATE-2.1] refactor: 迁移状态切换逻辑

# 管理器提交
[MANAGER-2.5] refactor: 完善 AssetManager

# 测试提交
[TEST-2.14] test: 第二阶段集成测试

# 文档提交
[DOC-1] docs: 编写架构设计文档
```

---

**文档版本**：1.0  
**最后更新**：2026-02-28
