# Limitime
A Minecraft Bukkit Plugin, which adds using duration to item with lore.

# 限时物品

### 指令
```
/limitime|limit|lime              主命令
/lime append|add|app <duration>   给手持物品添加/减去 <duration> 的使用时间
```

### 格式
1. 使用时长：`\\[duration:([+-]\\d+[mhd])+]`
2. 使用期限：`\[deadline:[0-9-:]+]`
3. 增减时长：`([+-]\d+[mhd])+`

### 注意
1. 只对第一条包含时限的lore有效果
2. 除了使用 `/lime add <duration>` 指令，也可以直接按格式添加lore
3. 添加时限lore的示例格式：`[duration:+1d+2m+3h]`
4. 时限部分可以为：(最终时间为各段时长按加减运算后的结果)
 + `+1m`
 + `-1m+1d`
 + `+1d-1m+1h`
 + `+1d+1d`
 + `+1m+1m-1m`
 + `+1h-1h+1d`
 + `+0d+0m+2h`
