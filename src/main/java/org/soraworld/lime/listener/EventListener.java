package org.soraworld.lime.listener;

import org.bukkit.Material;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.soraworld.lime.config.Config;
import org.soraworld.lime.util.LimitUtils;

public class EventListener implements Listener {

    private final Config config;
    private static final ItemStack ITEM_AIR = new ItemStack(Material.AIR, 0);

    public EventListener(Config config) {
        this.config = config;
    }

    @EventHandler
    public void checkDeadline(InventoryClickEvent event) {
        System.out.println("InventoryClickEvent" + event.getClick());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void checkDeadline(PlayerItemHeldEvent event) {
        PlayerInventory inv = event.getPlayer().getInventory();
        if (LimitUtils.isDead(inv.getItem(event.getPreviousSlot()))) {
            inv.setItem(event.getPreviousSlot(), ITEM_AIR);
        }
        if (LimitUtils.isDead(inv.getItem(event.getNewSlot()))) {
            inv.setItem(event.getNewSlot(), ITEM_AIR);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void checkDeadline(PlayerInteractEvent event) {
        checkDeadline(event, event.getPlayer().getInventory());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void checkDuration(PlayerInteractEvent event) {
        checkDuration(event, event.getPlayer().getInventory());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void checkDuration(PlayerItemDamageEvent event) {
        checkDuration(event, event.getPlayer().getInventory());
    }

    private void checkDeadline(Cancellable event, PlayerInventory inv) {
        if (LimitUtils.isDead(inv.getItemInHand())) {
            inv.setItemInHand(ITEM_AIR);
            if (config.deathGone()) event.setCancelled(true);
        }
        System.out.println(inv.getItemInHand());
        ItemStack item = inv.getHelmet();
        if (item != null && LimitUtils.isDead(item)) {
            item.setAmount(0);
            inv.setHelmet(item);
        }
        System.out.println(inv.getHelmet());
        item = inv.getChestplate();
        if (item != null && LimitUtils.isDead(item)) {
            item.setAmount(0);
            inv.setChestplate(item);
        }
        System.out.println(inv.getChestplate());
        item = inv.getLeggings();
        if (item != null && LimitUtils.isDead(item)) {
            item.setAmount(0);
            inv.setLeggings(item);
        }
        System.out.println(inv.getLeggings());
        item = inv.getBoots();
        if (item != null && LimitUtils.isDead(item)) {
            item.setAmount(0);
            inv.setBoots(item);
        }
        System.out.println(inv.getBoots());
    }

    private void checkDuration(Cancellable event, PlayerInventory inv) {
        ItemStack item = inv.getItemInHand();
        if (LimitUtils.hasLimitime(item)) {
            inv.setItemInHand(item);
            event.setCancelled(true);
        }
        item = inv.getHelmet();
        if (LimitUtils.hasLimitime(item)) inv.setHelmet(item);
        item = inv.getChestplate();
        if (LimitUtils.hasLimitime(item)) inv.setChestplate(item);
        item = inv.getLeggings();
        if (LimitUtils.hasLimitime(item)) inv.setLeggings(item);
        item = inv.getBoots();
        if (LimitUtils.hasLimitime(item)) inv.setBoots(item);
    }
}
