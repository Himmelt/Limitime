package io.github.himmelt.limitime;
/* Created by Kami on 2016/5/29.*/

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
        final Player player = event.getPlayer();
        logger.info("interact!!");
        logger.info(player.getDisplayName());
        logger.info(player.getItemInHand().toString());
        logger.info("ItemMeta:" + player.getItemInHand().hasItemMeta());
        if (player.getItemInHand() != null && player.getItemInHand().hasItemMeta()) {
            ItemStack itemStack = player.getItemInHand();
            ItemMeta itemMeta = itemStack.getItemMeta();
            List<String> itemLore = itemMeta.getLore();
            itemLore.add("&a&lThis is a test string! &e&lif you interact with sth.");
            //itemMeta.setLore(itemLore);
            itemMeta.setLore(itemLore);
            itemStack.setItemMeta(itemMeta);
            player.setItemInHand(itemStack);
            logger.info("replace ok!");
        }
    }
}
