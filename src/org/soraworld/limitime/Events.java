package org.soraworld.limitime;

/* Created by Kami on 2016/5/29.*/

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Events implements Listener {
    @EventHandler
    public void onPlayerAction(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        // itemHeld 手持物 armors 护甲
        ItemStack itemHeld = player.getItemInHand();
        ItemStack[] armors = player.getInventory().getArmorContents();
        // 判断手持物
        if (itemHeld.hasItemMeta()) {
            ItemMeta itemMeta = itemHeld.getItemMeta();
            List<String> itemLore = itemMeta.getLore();
            for (int j = 0; j < itemLore.size(); j++) {
                if (itemLore.get(j).contains("duration")) {
                    // 阻止事件并处理duration时限
                    event.setCancelled(true);
                    itemLore.set(j, "§e[limitime:" + Limit.getLimit(itemLore.get(j)) + "]");
                    itemMeta.setLore(itemLore);
                    itemHeld.setItemMeta(itemMeta);
                    player.setItemInHand(itemHeld);
                    break;
                } else if (itemLore.get(j).contains("limitime")) {
                    if (Limit.isDeadline(itemLore.get(j))) {
                        Log.info("Time Out!! " + player.getName() + "'s "
                                + itemHeld.getItemMeta().getDisplayName()
                                + "[" + itemHeld.getType() + "] is GONE!");
                        // 确认已到达时限，阻止事件并消除物品
                        event.setCancelled(true);
                        player.setItemInHand(new ItemStack(Material.AIR));
                    }
                    break;
                }
            }
        }
        // 处理护甲
        for (int i = 0; i < armors.length; i++) {
            if (armors[i].hasItemMeta()) {
                ItemMeta itemMeta = armors[i].getItemMeta();
                List<String> itemLore = itemMeta.getLore();
                for (int j = 0; j < itemLore.size(); j++) {
                    if (itemLore.get(j).contains("duration")) {
                        itemLore.set(j, "§e[limitime:" + Limit.getLimit(itemLore.get(j)) + "]");
                        itemMeta.setLore(itemLore);
                        armors[i].setItemMeta(itemMeta);
                        break;
                    } else if (itemLore.get(j).contains("limitime")) {
                        if (Limit.isDeadline(itemLore.get(j))) {
                            Log.info("Time Out!! " + player.getDisplayName() + "'s "
                                    + armors[i].getItemMeta().getDisplayName()
                                    + "[" + armors[i].getType() + "] is GONE!");
                            armors[i] = new ItemStack(Material.AIR);
                        }
                        break;
                    }
                }
            }
        }
        player.getInventory().setArmorContents(armors);
    }
}
