# Limitime
A Minecraft Bukkit Plugin, which adds using duration to item with lore.

# 物品限时使用插件:

========物品限时插件(v0.2)========

===只对第一条包含时限的lore有效果===

添加时限lore的格式为:

```
[duration:+1d+2m+3h]
```

时限部分可以为:

```
+1m,+1m+1d,+1d+1m+1h,+1d+1d,
+1m+1m+1m,+1h+1h+1d,+0d+0m+2h
```

以上时间全部为叠加状态

+ 待添加功能: 指令 /limitime delay [+/-时限]
                 /limitime cancel
                 
```
"§a========§e§l物品限时插件(v0.2)§r§a========"
"§a===§ehttps://github.com/Himmelt/Limitime§r§a==="
"§a===§b§l只对第一条包含时限的lore有效果§r§a==="
"§e添加时限lore的格式为:"
"§a§l[duration:+1d+2m+3h]"
"§6时限部分可以为:"
"§a§l+1h+1h+1d,§r§6全部为叠加状态."
```
        // 检查 弓 在右键时(有箭/无箭)是否duration转deadline
