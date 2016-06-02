package io.github.himmelt.limitime;
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
        ItemStack[] itemStacks = new ItemStack[5];
        ItemStack[] armors = player.getInventory().getArmorContents();
        itemStacks[0] = player.getItemInHand();
        itemStacks[1] = armors[0];
        itemStacks[2] = armors[1];
        itemStacks[3] = armors[2];
        itemStacks[4] = armors[3];

        for (int i = 0; i < itemStacks.length; i++) {
            if (itemStacks[i].hasItemMeta()) {
                ItemMeta itemMeta = itemStacks[i].getItemMeta();
                List<String> itemLore = itemMeta.getLore();
                for (int j = 0; j < itemLore.size(); j++) {
                    if (itemLore.get(j).contains("duration")) {
                        itemLore.set(j, "§e§l[limitime:" + Limit.getLimit(itemLore.get(j)) + "]");
                        itemMeta.setLore(itemLore);
                        itemStacks[i].setItemMeta(itemMeta);
                        break;
                    } else if (itemLore.get(j).contains("limitime")) {
                        if (Limit.isDeadline(itemLore.get(j))) {
                            Log.info("Time Out!! " + player.getDisplayName() + "'s "
                                    + itemStacks[i].getItemMeta().getDisplayName()
                                    + "[" + itemStacks[i].getType() + " is GONE!");
                            itemStacks[i] = new ItemStack(Material.AIR);
                        }
                        break;
                    }
                }
            }
        }

        armors[0] = itemStacks[1];
        armors[1] = itemStacks[2];
        armors[2] = itemStacks[3];
        armors[3] = itemStacks[4];

        player.setItemInHand(itemStacks[0]);
        player.getInventory().setArmorContents(armors);
    }
}
