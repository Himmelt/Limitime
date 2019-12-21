package org.soraworld.lime.listener

import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemDamageEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.inventory.PlayerInventory
import org.soraworld.lime.config.LimitConfig
import org.soraworld.lime.util.LimitUtils

class EventListener(private val config: LimitConfig) : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun checkDeadline(event: PlayerItemDamageEvent) {
        if (LimitUtils.isDead(event.player.itemInHand)) {
            event.player.itemInHand = null
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun checkDeadline(event: EntityDamageEvent) {
        if (event.entity is Player) {
            checkDeadline(event, (event.entity as Player).inventory, false)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun checkDeadline(event: PlayerItemHeldEvent) {
        val inv = event.player.inventory
        if (LimitUtils.isDead(inv.getItem(event.previousSlot))) {
            inv.setItem(event.previousSlot, null)
        }
        if (LimitUtils.isDead(inv.getItem(event.newSlot))) {
            inv.setItem(event.newSlot, null)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun checkDeadline(event: PlayerInteractEvent) {
        checkDeadline(event, event.player.inventory, config.deathGone)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun checkLimitime(event: PlayerInteractEvent) {
        checkLimitime(event, event.player.inventory)
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun checkLimitime(event: PlayerItemDamageEvent) {
        checkLimitime(event, event.player.inventory)
    }

    private fun checkDeadline(event: Cancellable, inv: PlayerInventory, deathGone: Boolean) {
        if (LimitUtils.isDead(inv.itemInHand)) {
            inv.itemInHand = null
            if (deathGone) event.isCancelled = true
        }
        if (LimitUtils.isDead(inv.helmet)) inv.helmet = null
        if (LimitUtils.isDead(inv.chestplate)) inv.chestplate = null
        if (LimitUtils.isDead(inv.leggings)) inv.leggings = null
        if (LimitUtils.isDead(inv.boots)) inv.boots = null
    }

    private fun checkLimitime(event: Cancellable, inv: PlayerInventory) {
        var item = inv.itemInHand
        if (LimitUtils.hasLimitime(item)) {
            inv.itemInHand = item
            event.isCancelled = true
        }
        item = inv.helmet
        if (LimitUtils.hasLimitime(item)) inv.helmet = item
        item = inv.chestplate
        if (LimitUtils.hasLimitime(item)) inv.chestplate = item
        item = inv.leggings
        if (LimitUtils.hasLimitime(item)) inv.leggings = item
        item = inv.boots
        if (LimitUtils.hasLimitime(item)) inv.boots = item
    }

}
