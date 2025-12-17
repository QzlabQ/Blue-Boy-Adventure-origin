# 可选项 & 现在有的bug

- [x]  `eventHandler.java` 的 58 行，可以考虑给 pit 加个贴图
- [x]  主世界的史莱姆可以弄点红的，右上角的空地。可以用地图编辑器查坐标（fauci）
- [x]  游戏失败后、传送到地牢 音乐会重叠，找到对应的地方把音乐停了 (QzlabQ)
- [ ]  适当修改怪物参数，达到游戏平衡(fauci)
- [x]  Loadgame之后打开地牢的箱子游戏会炸
- [ ]  格挡（parry & defence 区别）（fauci）
- [x]  石头推行
- [x]  fire ball lighting (胡)
- [x]  fire ball damage
- [x]  boss攻击范围的提示
- [x]  空气墙阻挡npc进入关键区域
- [x]  时间显示，绘制路径和开启无敌改为可以开关的调试功能，按G进入设置菜单

> 火球伤害已加强，伤害随等级升高（`entity/projectile.java` 第30行，伤害自定义为`attack * (gp.player.level * 2)`）

- [x]  delete career class（huang）
- [ ]  最小化后，key一直输入（有机会？）
- [x]  增加assets editor (已经完成，集成在MapEditor中；地图文件的格式也同步更新了)
- [ ]  制作展示视频（游戏引擎，mapeditor，...）
- [x]  检查新tile的添加方法
- [x]  boss battle
- [ ]  改改游戏文案，有点特色、剧情什么的。当然可以让ai跑
- [ ]  `monster/MON_SkeletonLord` 里面有石山代码，不过鉴于宝石只用一次，就不太想修这个了。当然可以修复成那种放入地图编辑器的那种，不过好像也没啥必要了

```java
// 这里有石山代码， 我绕过了地图编辑器，直接把钻石作为掉落物放到指定坐标。。
for (int i = 0; i < gp.obj[1].length; i++) {
    if (gp.obj[gp.currentMap][i] == null) {
        gp.obj[gp.currentMap][i] = new OBJ_Blueheart(gp);
        gp.obj[gp.currentMap][i].worldX = gp.tileSize * 25;
        gp.obj[gp.currentMap][i].worldY = gp.tileSize * 8;
        break;
    }
}
```

## 提示(有3条更新)

### 更新

1. 如果编辑了地图的特殊交互方块（目前只有商人小屋、传送楼梯、地刺、泉水），要到 `main/EventHandler.java` 的 `checkEvent()` 里面修改 对应的触发事件坐标。
2. `staff` 和 `acknowledgements` 的文案在 `main/CutsceneManager.java` 的 `构造方法` 里面，写清楚自己的分工。
3. `main/eventHandler` 75 76 行跳关，理论上应该是，`主世界->地牢一层->地牢二层`， 为方便体验游戏内容可以改成 `主世界->地牢二层`。

> 跳关可以直接看boss战，boss战结束后领取战利品，播放staff表，游戏结束。

### 原有的

1. 如果向 `object` 里面添加了新东西，要对应修改 `main/EntityGenerator.java` 的 switch 语句
2. `entity/player.java` 第494行取消注释 可以墙被破坏时 随机掉落物品

```java
if (gp.iTile[gp.currentMap][i].life == 0) {
    // gp.iTile[gp.currentMap][i].checkDrop(); // <= 这一行
    gp.iTile[gp.currentMap][i] = gp.iTile[gp.currentMap][i].getDestroyedForm();
}
```

3. 按 G 进入调试菜单，可以开关调试功能
4. `environment/Light.java` 可以把屏幕上的时间注释掉
5. `main/eventHandler` 75 76 行跳关，理论上应该是，`主世界->地牢一层->地牢二层`， 为方便体验游戏内容可以改成 `主世界->地牢二层`
