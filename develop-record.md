2026/2/28

src/
├── main/ (旧代码，保持冻结)
│ └── Main.java (待迁移)
│ └── GamePanel.java
│ └── ... (其他旧类)
│
├── app/ ✨ 新建 - 应用入口
│ └── Game.java (新主入口类)
│ └── GameWindow.java (窗口管理)
│
├── core/ ✨ 新建 - 核心框架
│ ├── event/
│ │ ├── GameEvent.java
│ │ ├── GameEventType.java
│ │ ├── EventListener.java
│ │ └── EventBus.java
│ └── state/
│ ├── GameState.java
│ ├── GameStateType.java
│ ├── GameStateManager.java
│ └── AbstractGameState.java
│
└── adapter/ ✨ 新建 - 适配层
└── GamePanelAdapter.java (桥接 v1 和 v2)
