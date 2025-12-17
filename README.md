# 🎮 游戏按键说明

## 解密玩法简介

枯树要用斧头砍，碎石要用稿子挖

商人不卖稿子，可以在地牢探索找到稿子。

地牢里面有三个大石头，可以直接推。如果石头被卡死了，重新进入地牢即可刷新石头位置。

把地牢中所有石头全部推到对应的压力板上，即可触发机关，打开下一层铁门



## 格挡玩法简介

1. 普通防御 (Block)

    操作：*按住* 空格键。

    效果：抵挡 66% 伤害。

2. 完美格挡 (Perfect Guard) ✨

    触发：观察怪物攻击的前摇动作，把握时机，在怪物攻击命中的 **瞬间** (0.16秒/10帧内) 按下 空格键。

    >`/src/entity/Entity.java`里面的方法`damagePlayer`修改相应参数。

    效果：0 伤害 + 击飞敌人。

3. 反击时刻 (Counter)

    机制：触发完美格挡后，敌人会被弹开并僵直 1秒。

    策略：利用这一秒的硬直时间，松盾反击！

### 战斗数据详解 (Technical Specs)

对于追求极致操作的玩家，以下是详细的判定帧数数据（基于 60 FPS 运行）：

| 动作阶段 | 时长/窗口 | 说明 |
| :--- | :--- | :--- |
| 怪物前摇 (Windup) | 约 0.5 秒 | 怪物举起武器，此时还没有伤害，**不要急着挡**。 |
| 怪物攻击 (Attack) | 瞬间 | 武器挥下的瞬间。 |
| 完美格挡判定窗 | 10 帧 (约 0.16秒) | 从按下空格开始计算的前10帧。 |
| 失衡僵直时间 | 60 帧 (1.00秒) | 怪物被弹开后的不可行动时间。 |
| 普通格挡惩罚 | 持续 | 超过10帧后判定为普通格挡，此时受击会掉血。 |

## 通用控制（适用于多个界面）

| 按键 | 功能说明 |
|------|---------|
| **W** 或 **↑** | 向上移动/选择 |
| **A** 或 **←** | 向左移动/选择 |
| **S** 或 **↓** | 向下移动/选择 |
| **D** 或 **→** | 向右移动/选择 |
| **ENTER** | 确认/交互/对话继续 |
| **ESC** | 返回上级/关闭菜单 |

## 各状态按键说明

### 🎮 游戏进行中 (`playState`)

| 按键 | 功能说明 |
|------|---------|
| **W/A/S/D** 或 **↑/←/↓/→** | 角色移动 |
| **C** | 打开角色状态/背包界面 |
| **F** | 射击/攻击 |
| **space** | 格挡 |
| **P** | 暂停/继续游戏 |
| **M** | 打开/关闭地图界面 |
| **X** | 切换小地图显示/隐藏 |
| **ESC** | 打开选项菜单 |

## 🔧 调试功能（开发用）

| 按键 | 功能说明 |
|------|---------|
| **T** | 切换绘制时间显示 |
| **R** | 重新加载当前地图 |

**温馨提示：**

- 大部分界面都支持 **ESC** 键返回
- 菜单界面通常使用 **W/S** 导航，**ENTER** 确认
- 游戏中可以随时打开地图(**M**)和角色界面(**C**)

## 🛠️ 开发指南 (Development Guide)

本项目使用 Gradle 进行构建管理。无论你是 Linux 还是 Windows 用户，都可以通过项目根目录下的 Gradle Wrapper (`gradlew`) 轻松运行和构建，无需手动安装 Gradle。

### 1. 常用开发命令

**启动游戏:**
*   **Linux / macOS:** `./gradlew run`
*   **Windows (CMD/PowerShell):** `.\gradlew.bat run`

**启动地图编辑器 (WIP):**
> ⚠️ **注意**: 地图编辑器目前处于 **Work in Progress (开发中)** 状态。虽然可以构建和运行，但功能可能尚不完整或存在已知问题。
*   **Linux / macOS:** `./gradlew runEditor`
*   **Windows:** `.\gradlew.bat runEditor`

---

### 2. 构建与发布 (Build & Distribute)

#### 生成 JAR 文件
JAR 文件包含编译后的代码和资源。
*   **JAR 文件本身是跨平台的**：同一个 JAR 文件可以在 Linux、Windows 或 macOS 上运行（只要有 Java 环境）。
*   运行以下命令后，文件会生成在 `build/libs/` 目录下：

```bash
# 生成游戏本体 (BlueBoyAdventure.jar)
./gradlew gameJar
# 对于 Windows 用户，可以运行以下命令：
.\gradlew.bat gameJar

# 生成编辑器 (MapEditor.jar)
./gradlew editorJar
# 对于 Windows 用户，可以运行以下命令：
.\gradlew.bat editorJar
```

#### 关于精简版 JRE (Runtime Image)
为了让没有安装 Java 的用户也能运行游戏，我们可以使用 `jlink` 生成一个精简版的 Java 运行时环境。

*   **命令:** `./gradlew createRuntime` 或 `.\gradlew.bat createRuntime`
*   **输出目录:** `build/runtime/`

**关键说明:**
1.  **Runtime 是平台相关的**: 这一点与 JAR 不同。如果你在 Linux 上运行此命令，生成的 Runtime 只能在 Linux 上使用；在 Windows 上运行则生成 Windows 版 Runtime。
2.  **只需构建一次**: 通常只需要在打包发布给最终用户时构建一次 Runtime。日常开发调试直接使用 `./gradlew run` 即可，无需频繁构建 Runtime。

---

### 3. 如何运行构建好的 JAR

你可以根据情况选择两种方式来运行生成的 JAR 文件：

**方式 A: 使用系统 Java (开发者)**
如果你电脑上已经安装了 Java 21+，直接运行：
```bash
java -jar build/libs/BlueBoyAdventure.jar
```

Windows用户直接双击 jar 即可，但注意 `config.ini` 的路径。

**方式 B: 使用精简版 Runtime (最终用户)**
使用项目生成的自带环境运行，无需系统安装 Java：
*   **Linux:**
    ```bash
    ./build/runtime/bin/java -jar build/libs/BlueBoyAdventure.jar
    # 缩放窗口（可选）
    ./build/runtime/bin/java -Dsun.java2d.uiScale=2 -jar ./build/libs/BlueBoyAdventure.jar
    ```
*   **Windows:**
    ```cmd
    build\runtime\bin\java.exe -jar build\libs\BlueBoyAdventure.jar
    ```
> 注意 `config.txt` 的位置，应与工作目录（运行命令时所在目录）一致。

> 后续工作：
> 1. 使用脚本复制 runtime 和 jar 到发布目录，简化最终用户的运行步骤。
> 2. 处理地图编辑器的资源路径问题。最终jar需要能读取资源，开发也不受影响。

祝您游戏愉快！ 🎯
