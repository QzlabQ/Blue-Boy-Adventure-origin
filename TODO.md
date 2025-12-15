# 可选项 & 现在有的bug

- [ ]  `eventHandler.java` 的 58 行，可以考虑给 pit 加个贴图
- [x]  主世界的史莱姆可以弄点红的，右上角的空地。可以用地图编辑器查坐标（fauchi）
- [ ]  游戏失败后、传送到地牢 音乐会重叠，找到对应的地方把音乐停了 (QzlabQ)
- [ ]  适当修改怪物参数，达到游戏平衡(fauchi)
- [x]  Loadgame之后打开地牢的箱子游戏会炸
- [ ]  游戏开太久了会炸（之后测试）
- [ ]  改ai的寻路范围（胡）
- [ ]  格挡（parry & defence 区别）（fauchi）
- [x]  石头推行
- [ ]  fire ball lighting (胡)
- [x]  fire ball damage

> 火球伤害已加强，伤害随等级升高（`entity/projectile.java` 第30行，伤害自定义为`attack * (gp.player.level * 2)`）

- [x]  delete career class（huang）
- [ ]  最小化后，key一直输入（有机会？）
- [x]  增加assets editor (已经完成，集成在MapEditor中；地图文件的格式也同步更新了)

## 提示

1. 如果向 `object` 里面添加了新东西，要对应修改 `main/EntityGenerator.java` 的 switch 语句
2. `entity/player.java` 第494行取消注释 可以墙被破坏时 随机掉落物品

```java
if (gp.iTile[gp.currentMap][i].life == 0) {
    // gp.iTile[gp.currentMap][i].checkDrop(); // <= 这一行
    gp.iTile[gp.currentMap][i] = gp.iTile[gp.currentMap][i].getDestroyedForm();
}
```

3. 按 G 开无敌
4. `environment/Light.java` 可以把屏幕上的时间注释掉
5. `main/eventHandler` 68 69 70 行跳关，理论上应该是，`主世界->地牢一层->地牢二层`， 为方便体验游戏内容可以改成 `主世界->地牢二层`
