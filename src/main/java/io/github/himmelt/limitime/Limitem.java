package io.github.himmelt.limitime;
/* Created by Kami on 2016/5/29.*/

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Limitem {
    ItemMeta itemMeta = null;
    ItemStack itemStack = null;
    long duration = 0;
    long limitime = 0;
    String lore = null;

    public Limitem(ItemStack itemStack) {
        if (itemStack.hasItemMeta()) {
            this.itemStack = itemStack;
            itemMeta = this.itemStack.getItemMeta();
            getDuration();
            getLimitime();
        }
    }

    public void getDuration() {
        try {
            duration = Long.parseLong(lore.substring(lore.indexOf("lit:duration:") + 1, lore.indexOf("|limitime:")));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void getLimitime() {
        try {
            limitime = Long.parseLong(lore.substring(lore.indexOf("limitime")));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public int getLimitem() {
        // 获取lore数据
        List<String> itemLore = itemMeta.getLore();
        int type = 3;//无标记
        for (int i = 0; i < itemLore.size(); i++) {
            String lore = itemLore.get(i);
            // 含有duration标记
            if (lore.contains("lit:duration:")) {
                // 含有limitime标记
                this.lore = lore;
                getDuration();
                getLimitime();
                long time_now = System.currentTimeMillis() / 3600000;//单位：小时
                if (lore.contains("|limitime:")) {
                    // 已经有limitime标记，检查是否到达期限
                    if (limitime < time_now) {
                        // 限制时限小于当前时间，已过期
                        type = 0;
                    } else {
                        type = 1;
                    }
                } else {
                    // 没有limitime标记，生成新的标记
                    lore = lore + "|limitime:" + Long.toString(time_now + duration);
                    itemLore.set(i, lore);
                    itemMeta.setLore(itemLore);
                    itemStack.setItemMeta(itemMeta);
                    type = 2;
                }
                break;
            }
        }
        return type;
    }
}
