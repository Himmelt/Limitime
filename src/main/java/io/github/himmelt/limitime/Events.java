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
import java.util.logging.Logger;

public class Events implements Listener {

    public static Logger logger = Logger.getLogger("Limitime");

    @EventHandler
    public void onPlayerAction(PlayerInteractEvent event) {
        logger.info("[Limitime]:onPlayerAction");
        Player player = event.getPlayer();
        if (player.getItemInHand().hasItemMeta()) {
            ItemStack itemStack = player.getItemInHand();
            ItemMeta itemMeta = itemStack.getItemMeta();
            logger.info("[Limitime]:hasItemMeta" + itemMeta);
            List<String> itemLore = itemMeta.getLore();
            String lore;
            for (int i = 0; i < itemLore.size(); i++) {
                lore = itemLore.get(i);
                logger.info("[Limitime]:lore-" + i + lore);
                if (lore.contains("lit:duration:")) {
                    logger.info("[Limitime]:duration");
                    long time_now = System.currentTimeMillis() / 60000;//单位：小时
                    if (lore.contains(":limitime:")) {
                        long limitime = getLimitime(lore);
                        logger.info("[Limitime]:time" + time_now + ":" + limitime);
                        if (limitime < time_now) {
                            logger.info("[Limitime]:timeout");
                            player.setItemInHand(new ItemStack(Material.AIR));
                        }
                    } else {
                        logger.info("[Limitime]:newlimit");
                        long duration = getDuration(lore);
                        lore = lore + ":limitime:" + Long.toString(time_now + duration);
                        logger.info("[Limitime]:newlore-" + lore);
                        itemLore.set(i, lore);
                        itemMeta.setLore(itemLore);
                        itemStack.setItemMeta(itemMeta);
                        player.setItemInHand(itemStack);
                    }
                    break;
                }
            }
        }
    }

    private long getDuration(String lore) {
        long duration = 0;
        try {
            duration = Long.parseLong(lore.substring(lore.indexOf("lit:duration:") + 13));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return duration;
    }

    private long getLimitime(String lore) {
        long limitime = 0;
        try {
            limitime = Long.parseLong(lore.substring(lore.indexOf(":limitime:") + 10));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return limitime;
    }
}
