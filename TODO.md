# 可选项 & 现在有的bug

1. `eventHandler.java` 的 58 行，可以考虑给 pit 加个贴图(直接注释)//delete or creat image?
2. 主世界的史莱姆可以弄点红的，右上角的空地。可以用地图编辑器查坐标（fauchi）
3. **游戏失败后、传送到地牢 音乐会重叠，找到对应的地方把音乐停了** (QzlabQ)
4. 商人小屋门口的树太多了，可以只留几颗，加快游戏进度
5. 适当修改怪物参数，达到游戏平衡(fauchi)
6. 这个fq修好了 *Loadgame之后打开地牢的箱子游戏会炸*(fixed)
7. 游戏开太久了会炸（之后测试）
8. 改ai的寻路范围（胡）
9. 格挡（parry & defence 区别）（fauchi）
10. 石头推行（fauchi）
11. fire ball lighting & damage(胡)
12. delete career class（huang）
13. 最小化后，key一直输入（有机会？）
14. 增加assets editor

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
