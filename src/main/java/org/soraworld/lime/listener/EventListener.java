package org.soraworld.lime.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.soraworld.lime.config.Config;
import org.soraworld.lime.util.LimitUtils;

public class EventListener implements Listener {

    private final Config config;

    public EventListener(Config config) {
        this.config = config;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void checkDeadline(PlayerItemDamageEvent event) {
        if (LimitUtils.isDead(event.getPlayer().getItemInHand())) {
            event.getPlayer().setItemInHand(null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void checkDeadline(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            checkDeadline(event, ((Player) event.getEntity()).getInventory(), false);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void checkDeadline(PlayerItemHeldEvent event) {
        PlayerInventory inv = event.getPlayer().getInventory();
        if (LimitUtils.isDead(inv.getItem(event.getPreviousSlot()))) {
            inv.setItem(event.getPreviousSlot(), null);
        }
        if (LimitUtils.isDead(inv.getItem(event.getNewSlot()))) {
            inv.setItem(event.getNewSlot(), null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void checkDeadline(PlayerInteractEvent event) {
        checkDeadline(event, event.getPlayer().getInventory(), config.deathGone());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void checkLimitime(PlayerInteractEvent event) {
        checkLimitime(event, event.getPlayer().getInventory());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void checkLimitime(PlayerItemDamageEvent event) {
        checkLimitime(event, event.getPlayer().getInventory());
    }

    private void checkDeadline(Cancellable event, PlayerInventory inv, boolean deathGone) {
        if (LimitUtils.isDead(inv.getItemInHand())) {
            inv.setItemInHand(null);
            if (deathGone) event.setCancelled(true);
        }
        if (LimitUtils.isDead(inv.getHelmet())) inv.setHelmet(null);
        if (LimitUtils.isDead(inv.getChestplate())) inv.setChestplate(null);
        if (LimitUtils.isDead(inv.getLeggings())) inv.setLeggings(null);
        if (LimitUtils.isDead(inv.getBoots())) inv.setBoots(null);
    }

    private void checkLimitime(Cancellable event, PlayerInventory inv) {
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
