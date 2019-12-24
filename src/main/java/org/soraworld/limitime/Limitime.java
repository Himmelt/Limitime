package org.soraworld.limitime;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import org.bukkit.inventory.Inventory;
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

/**
 * @author Himmelt
 */
public final class Limitime extends JavaPlugin implements Listener {

    private static final String PLUGIN_ID = "limitime";
    private static final String PLUGIN_NAME = "Limitime";
    private static final String PERM_ADMIN = "limitime.admin";
    private static final Pattern DURATION_ARG = Pattern.compile("([+-]\\d+[mhd])+");
    private static final Pattern DEADLINE = Pattern.compile("\\[deadline:[0-9-:]+]");
    private static final Pattern DURATION = Pattern.compile("\\[duration:([+-]\\d+[mhd])+]");
    private static final Pattern LIMITIME = Pattern.compile("\\[limitime:([+-]\\d+[mhd])+]");
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
    private static final Pattern DATE = Pattern.compile("[0-9]{4}(-[0-9]{2}){3}:[0-9]{2}");

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player && args.length >=1) {
            Player player = (Player) sender;

            ItemStack stack = player.getItemInHand();

            if (stack==null || stack.getType()==null||stack.getType()== Material.AIR){
                player.sendMessage("请手持一个有效物品.");
                return true;
            }

            if (args.length==1){
                switch (args[0]){
                    case "time":

                        break;
                    case "dead":
                        break;
                    default:
                        player.sendMessage(command.getUsage());
                }
                return true;
            }

            switch (args[0]){
                case "time":
                    String time = args[1];
                    if (DURATION_ARG.matcher(time).matches()){
                        ItemMeta meta = stack.getItemMeta();
                        List<String> lore = meta.getLore();
                        if (lore != null) {
                            for (int i = 0; i < lore.size(); i++) {
                                String line = lore.get(i);
                                Matcher matcher = DEADLINE.matcher(line);
                                if (matcher.find()) {
                                    Matcher mc = DATE.matcher(line);
                                    if (mc.find()) {
                                        try {
                                            Date date = DATE_FORMAT.parse(mc.group());
                                            date.setTime(date.getTime() + limitime(args[0]));
                                            lore.set(i, deadLore(date));
                                            setLore(player, item, meta, lore);
                                            // config.send(player, "append deadline");
                                            return true;
                                        } catch (Throwable e) {
                                            // config.send(player, "parseException")
                                        }
                                    }
                                    // config.send(player, "invalid date format");
                                    return true;
                                }
                                matcher = DURATION.matcher(line);
                                if (matcher.find()) {
                                    Matcher mc = DURATION_ARG.matcher(line);
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
                    }
                    break;
                case "dead":
                    break;
                default:
                    player.sendMessage(command.getUsage());
            }

            switch (args[0]) {
                case "+": {
                    if (args.length == 1 && DURATION_ARG.matcher(args[0]).matches()) {
                        ItemStack item = player.getItemInHand();
                        if (item != null) {


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
        int last = exp.length() - 1;
        int val = Integer.parseInt(exp.substring(1, last));
        if (exp.charAt(0) == '-') {
            val *= -1;
        }
        if (exp.charAt(last) == 'h') {
            val *= 60;
        }
        if (exp.charAt(last) == 'd') {
            val *= 1440;
        }
        return val;
    }

    public long limitime(String limitime) {
        long time = 0;
        Matcher matcher = DURATION_ARG.matcher(limitime);
        while (matcher.find()) {
            time += parse(matcher.group());
        }
        return time * 60000;
    }

    private boolean deadline(String deadline) {
        Matcher matcher = DATE.matcher(deadline);
        if (matcher.find()) {
            try {
                if (DATE_FORMAT.parse(matcher.group()).before(new Date())) {
                    return true;
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

        }
        return false;
    }

    public String deadLore(Date date) {
        return "[deadline:" + DATE_FORMAT.format(date) + "]";
    }

    public String limeLore(String limitime) {
        return "[limitime:$limitime]";
    }

    public void setLore(Player player, ItemStack item, ItemMeta meta, List<String> lore) {
        meta.setLore(lore);
        item.setItemMeta(meta);
        player.setItemInHand(item);
    }

    public boolean isDead(ItemStack item) {
        if (item == null || item.getItemMeta() == null) {
            return false;
        }
        List<String> lore = item.getItemMeta().getLore();
        int i = 0;
        while (lore != null && i < lore.size()) {
            String line = lore.get(i);
            Matcher matcher = DEADLINE.matcher(line);
            if (matcher.find() && deadline(matcher.group())) {
                return true;
            }
            i++;
        }
        return false;
    }

    public boolean hasLimitime(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();
            int i = 0;
            while (lore != null && i < lore.size()) {
                String line = lore.get(i);
                Matcher matcher = LIMITIME.matcher(line);
                if (matcher.find()) {
                    Date date = new Date();
                    date.setTime(date.getTime() + limitime(matcher.group()));
                    lore.set(i, deadLore(date));
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    return true;
                }
                i++;
            }
        }
        return false;
    }


    /***************************** EventListener **********************************/


    @EventHandler(priority = EventPriority.LOWEST)
    public void checkDeadline(PlayerItemDamageEvent event) {
        if (isDead(event.getPlayer().getItemInHand())) {
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
        Inventory inv = event.getPlayer().getInventory();
        if (isDead(inv.getItem(event.getPreviousSlot()))) {
            inv.setItem(event.getPreviousSlot(), null);
        }
        if (isDead(inv.getItem(event.getNewSlot()))) {
            inv.setItem(event.getNewSlot(), null);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void checkDeadline(PlayerInteractEvent event) {
        checkDeadline(event, event.getPlayer().getInventory(), false/*config.deathGone*/);
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
        if (isDead(inv.getItemInHand())) {
            inv.setItemInHand(null);
            if (deathGone) {
                event.setCancelled(true);
            }
        }
        if (isDead(inv.getHelmet())) {
            inv.setHelmet(null);
        }
        if (isDead(inv.getChestplate())) {
            inv.setChestplate(null);
        }
        if (isDead(inv.getLeggings())) {
            inv.setLeggings(null);
        }
        if (isDead(inv.getBoots())) {
            inv.setBoots(null);
        }
    }

    private void checkLimitime(Cancellable event, PlayerInventory inv) {
        ItemStack item = inv.getItemInHand();
        if (hasLimitime(item)) {
            inv.setItemInHand(item);
            event.setCancelled(true);
        }
        item = inv.getHelmet();
        if (hasLimitime(item)) {
            inv.setHelmet(item);
        }
        item = inv.getChestplate();
        if (hasLimitime(item)) {
            inv.setChestplate(item);
        }
        item = inv.getLeggings();
        if (hasLimitime(item)) {
            inv.setLeggings(item);
        }
        item = inv.getBoots();
        if (hasLimitime(item)) {
            inv.setBoots(item);
        }
    }
}
