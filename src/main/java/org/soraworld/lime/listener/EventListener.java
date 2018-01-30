package org.soraworld.lime.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.soraworld.lime.config.Config;
import org.soraworld.lime.util.LimitUtils;

public class EventListener implements Listener {

    private final Config config;

    public EventListener(Config config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        player.setItemInHand(LimitUtils.analysis(player, player.getItemInHand(), false));
        //solveArmors(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        int pre = event.getPreviousSlot(), now = event.getNewSlot();
        Inventory inv = player.getInventory();
        inv.setItem(pre, LimitUtils.analysis(player, inv.getItem(pre), true));
        inv.setItem(now, LimitUtils.analysis(player, inv.getItem(now), true));
        solveArmors(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        player.setItemInHand(LimitUtils.analysis(player, player.getItemInHand(), false));
        //solveArmors(player);
    }

    private void solveArmors(Player player) {
        ItemStack[] armors = player.getInventory().getArmorContents();
        for (int i = 0; armors != null && i < armors.length; i++) {
            armors[i] = LimitUtils.analysis(player, armors[i], false);
        }
        player.getInventory().setArmorContents(armors);
    }

}
