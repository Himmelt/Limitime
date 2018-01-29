package org.soraworld.lime.util;

import org.bukkit.ChatColor;
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
    private static final Pattern DURATION = Pattern.compile("\\[duration:([+-]\\d+[mhd])+]");
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

    private static long duration(String duration) {
        long time = 0;
        Matcher matcher = PATTERN.matcher(duration);
        while (matcher.find()) {
            time += parse(matcher.group());
        }
        return time;
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

    public static ItemStack analysis(Player player, ItemStack item, boolean onlyCheck) {
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.getLore();
                for (int i = 0; lore != null && i < lore.size(); i++) {
                    String line = lore.get(i);
                    Date now = new Date();
                    Matcher matcher = DEADLINE.matcher(line);
                    if (matcher.find() && deadline(matcher.group())) {
                        ServerUtils.send(player, "your item is dead ,server will cycle it.");
                        return null;
                    }
                    if (!onlyCheck) {
                        matcher = DURATION.matcher(line);
                        if (matcher.find()) {
                            now.setTime(now.getTime() + duration(matcher.group()) * 60000);
                            String deadline = DATE_FORMAT.format(now);
                            lore.set(i, ChatColor.YELLOW + "[deadline:" + deadline + "]");
                            meta.setLore(lore);
                            item.setItemMeta(meta);
                            ServerUtils.send(player, "set deadline:" + deadline + " for your item");
                            return item;
                        }
                    }
                }
            }
        }
        return item;
    }

}
