# 可选项

1. `eventHandler.java` 的 58 行，可以考虑给 pit 加个贴图
2. 史莱姆弄点红的，弄点绿的。现在绿的全变成红的了
3. **游戏失败后、传送到地牢 音乐会重叠，找到对应的地方把音乐停了** (我推测是选择角色页面不要播放音乐就行了)
4. 商人小屋门口的树太多了，可以只留几颗，加快游戏进度

## 提示

1. 如果向 `object` 里面添加了新东西，要对应修改 `main/EntityGenerator.java` 的 switch 语句
2. `entity/player.java` 第494行注释掉可以让破坏墙 随机掉落物品

```java
if (gp.iTile[gp.currentMap][i].life == 0) {
    // gp.iTile[gp.currentMap][i].checkDrop(); // <= 这一行
    gp.iTile[gp.currentMap][i] = gp.iTile[gp.currentMap][i].getDestroyedForm();
}
```
