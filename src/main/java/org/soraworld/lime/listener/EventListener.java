package org.soraworld.lime.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.soraworld.lime.Limitime;
import org.soraworld.lime.config.Config;

import java.util.List;

public class EventListener implements Listener {

    private final Config config;

    public EventListener(Config config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerUsing(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();
        if (check(item)) event.setCancelled(true);

        ItemStack[] armors = player.getInventory().getArmorContents();
        for (int i = 0; armors != null && i < armors.length; i++) if (check(armors[i])) event.setCancelled(true);

        // ?? player.getInventory().setArmorContents(armors);
    }

    private static boolean check(ItemStack item) {
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.getLore();
                for (int i = 0; lore != null && i < lore.size(); i++) {
                    String line = lore.get(i);
                    if (line.contains("duration")) {
                        lore.set(i, "[deadline:{time}]");
                        meta.setLore(lore);
                        item.setItemMeta(meta);
                        // ?? player.setItemInHand(item);
                        return true;
                    } else if (line.contains("deadline")) {
                        if (Limitime.isDead(line)) {
                            item.setAmount(0);
                            return true;
                            // ??? player.setItemInHand(new ItemStack(Material.AIR));
                        }
                        return false;
                    }
                }
            }
        }
        return false;
    }

}
