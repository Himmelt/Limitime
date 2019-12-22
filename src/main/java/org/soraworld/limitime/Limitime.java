package org.soraworld.limitime;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Limitime extends JavaPlugin implements Listener {

    private static final String PLUGIN_ID = "limitime";
    private static final String PLUGIN_NAME = "Limitime";
    private static final String PERM_ADMIN = "limitime.admin";
    private static final Pattern PATTERN = Pattern.compile("([+-]\\d+[mhd])+");
    private static final Pattern DEADLINE = Pattern.compile("\\[deadline:[0-9-:]+]");
    private static final Pattern DURATION = Pattern.compile("\\[duration:([+-]\\d+[mhd])+]");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
    private static final Pattern DATE = Pattern.compile("[0-9]{4}(-[0-9]{2}){3}:[0-9]{2}");

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && args.length >= 1) {
            Player player = (Player) sender;
            switch (args[0]) {
                case "append":
                case "add":
                case "plus": {
                    if (args.length == 1 && PATTERN.matcher(args[0]).matches()) {
                        ItemStack item = player.getItemInHand();
                        if (item != null) {
                            ItemMeta meta = item.getItemMeta();
                            List<String> lore = meta.getLore();
                            if (lore != null) {
                                for (int i = 0; i < lore.size(); i++) {
                                    String line = lore.get(i);
                                    Matcher matcher = DEADLINE.matcher(line)
                                    if (matcher.find()) {
                                        Matcher mc = DATE.matcher(line);
                                        if (mc.find()) {
                                            try {
                                                Date date = DATE_FORMAT.parse(mc.group());
                                                date.setTime(date.getTime() + limitime(args[0]));
                                                lore.set(i, deadLore(date));
                                                setLore(player, item, meta, lore);
                                                // config.send(player, "append deadline");
                                                return true
                                            } catch (Throwable e) {
                                                // config.send(player, "parseException")
                                            }
                                        }
                                        // config.send(player, "invalid date format");
                                        return true;
                                    }
                                    matcher = DURATION.matcher(line);
                                    if (matcher.find()) {
                                        Matcher mc = PATTERN.matcher(line)
                                        if (mc.find()) {
                                            lore.set(i, limeLore(mc.group() + args[0]));
                                            setLore(player, item, meta, lore);
                                            // config.send(player, "append limitime");
                                            return true;
                                        }
                                        // config.send(player, "not match")
                                        return false;
                                    }
                                }
                                lore.add(limeLore(args[0]));
                                setLore(player, item, meta, lore);
                                // config.send(player, "set limitime");
                            }

                        } else {
                            // config.send(player, "emptyHand")
                        }
                    }
                }
            }
        }
        return true;
    }

    public int parse(String exp) {
        val last = exp.length - 1
        var `val` =Integer.valueOf(exp.substring(1, last))
        if (exp[0] == '-') `val` *=-1
        if (exp[last] == 'h') `val` *=60
        if (exp[last] == 'd') `val` *=1440
        return `val`
    }

    public long limitime(String limitime) {
        var time:Long = 0
        val matcher = PATTERN.matcher(limitime)
        while (matcher.find()) {
            time += parse(matcher.group()).toLong()
        }
        return time * 60000
    }

    private boolean deadline(String deadline) {
        val matcher = DATE.matcher(deadline)
        if (matcher.find()) {
            try {
                if (DATE_FORMAT.parse(matcher.group()).before(Date())) {
                    return true
                }
            } catch (e:Throwable){
                println("parseException$deadline")
            }

        }
        return false
    }

    public String deadLore(Date date) {
        return "[deadline:" + DATE_FORMAT.format(date) + "]"
    }

    public String limeLore(String limitime) {
        return "[limitime:$limitime]"
    }

    public void setLore(Player player, ItemStack item, ItemMeta meta, List<String> lore) {
        meta.lore = lore;
        item.itemMeta = meta;
        player.itemInHand = item;
    }

    public boolean isDead(ItemStack item) {
        if (item == null || item.itemMeta == null) return false
        val lore = item.itemMeta.lore
        var i = 0
        while (lore != null && i < lore.size) {
            val line = lore[i]
            val matcher = DEADLINE.matcher(line)
            if (matcher.find() && deadline(matcher.group())) {
                return true
            }
            i++
        }
        return false
    }

    public boolean hasLimitime(ItemStack item) {
        if (item != null && item.itemMeta != null) {
            val meta = item.itemMeta
            val lore = meta.lore
            var i = 0
            while (lore != null && i < lore.size) {
                val line = lore[i]
                val matcher = LIMITIME.matcher(line)
                if (matcher.find()) {
                    val date = Date()
                    date.time = date.time + limitime(matcher.group())
                    lore[i] = deadLore(date)
                    meta.lore = lore
                    item.itemMeta = meta
                    return true
                }
                i++
            }
        }
        return false
    }


    /***************************** EventListener **********************************/


    @EventHandler(priority = EventPriority.LOWEST)
    public void checkDeadline(PlayerItemDamageEvent event) {
        if (LimitUtils.isDead(event.getPlayer().getItemInHand())) {
            event.player.itemInHand = null
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void checkDeadline(EntityDamageEvent event) {
        if (event.entity instanceof Player) {
            checkDeadline(event, (event.entity as Player).inventory, false)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void checkDeadline(PlayerItemHeldEvent event) {
        val inv = event.getPlayer().getInventory();
        if (LimitUtils.isDead(inv.getItem(event.previousSlot))) {
            inv.setItem(event.previousSlot, null)
        }
        if (LimitUtils.isDead(inv.getItem(event.newSlot))) {
            inv.setItem(event.newSlot, null)
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void checkDeadline(PlayerInteractEvent event) {
        checkDeadline(event, event.getPlayer().getInventory(), config.deathGone);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void checkLimitime(PlayerInteractEvent event) {
        checkLimitime(event, event.getPlayer().getInventory());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void checkLimitime(PlayerItemDamageEvent event) {
        checkLimitime(event, event.player.inventory);
    }

    private void checkDeadline(Cancellable event, PlayerInventory inv, boolean deathGone) {
        if (isDead(inv.getItemInHand())) {
            inv.setItemInHand(null);
            if (deathGone) event.isCancelled = true;
        }
        if (isDead(inv.helmet)) inv.helmet = null;
        if (isDead(inv.chestplate)) inv.chestplate = null;
        if (isDead(inv.leggings)) inv.leggings = null;
        if (isDead(inv.boots)) inv.boots = null;
    }

    private void checkLimitime(Cancellable event, PlayerInventory inv) {
        var item = inv.getItemInHand();
        if (LimitUtils.hasLimitime(item)) {
            inv.itemInHand = item
            event.isCancelled = true
        }
        item = inv.helmet
        if (hasLimitime(item)) inv.helmet = item;
        item = inv.chestplate
        if (hasLimitime(item)) inv.chestplate = item;
        item = inv.leggings
        if (hasLimitime(item)) inv.leggings = item;
        item = inv.boots
        if (hasLimitime(item)) inv.boots = item;
    }
}
