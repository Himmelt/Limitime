package org.soraworld.lime.util;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.soraworld.lime.config.LangKeys;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LimitUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
    private static final Pattern PATTERN = Pattern.compile("[+-]\\d+[mhd]");
    private static final Pattern LIMITIME = Pattern.compile("\\[limitime:([+-]\\d+[mhd])+]");
    private static final Pattern DEADLINE = Pattern.compile("\\[deadline:[0-9-:]+]");
    private static final Pattern DATE = Pattern.compile("[0-9]{4}(-[0-9]{2}){3}:[0-9]{2}");

    private static int parse(String exp) {
        int last = exp.length() - 1;
        int val = Integer.valueOf(exp.substring(1, last));
        if (exp.charAt(0) == '-') val *= -1;
        if (exp.charAt(last) == 'h') val *= 60;
        if (exp.charAt(last) == 'd') val *= 1440;
        return val;
    }

    public static long limitime(String limitime) {
        long time = 0;
        Matcher matcher = PATTERN.matcher(limitime);
        while (matcher.find()) {
            time += parse(matcher.group());
        }
        return time * 60000;
    }

    private static boolean deadline(String deadline) {
        Matcher matcher = DATE.matcher(deadline);
        if (matcher.find()) {
            try {
                if (DATE_FORMAT.parse(matcher.group()).before(new Date())) {
                    return true;
                }
            } catch (Throwable e) {
                ServerUtils.console(LangKeys.format("parseException", deadline));
            }
        }
        return false;
    }

    public static String deadLore(Date date) {
        return "[deadline:" + DATE_FORMAT.format(date) + "]";
    }

    public static String limeLore(String limitime) {
        return "[limitime:" + limitime + "]";
    }

    public static void setLore(Player player, ItemStack item, ItemMeta meta, List<String> lore) {
        meta.setLore(lore);
        item.setItemMeta(meta);
        player.setItemInHand(item);
    }

    public static boolean isDead(ItemStack item) {
        if (item == null || item.getItemMeta() == null) return false;
        List<String> lore = item.getItemMeta().getLore();
        for (int i = 0; lore != null && i < lore.size(); i++) {
            String line = lore.get(i);
            Matcher matcher = DEADLINE.matcher(line);
            if (matcher.find() && deadline(matcher.group())) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasLimitime(ItemStack item) {
        if (item != null && item.getItemMeta() != null) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();
            for (int i = 0; lore != null && i < lore.size(); i++) {
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
            }
        }
        return false;
    }
}
