package org.soraworld.lime;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.soraworld.lime.command.CommandLime;
import org.soraworld.lime.command.IICommand;
import org.soraworld.lime.config.Config;
import org.soraworld.lime.listener.EventListener;
import org.soraworld.lime.util.ListUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Limitime extends JavaPlugin {


    private Config config;
    private IICommand command;

    @Override
    public void onEnable() {
        config = new Config(this.getDataFolder());
        config.load();
        config.save();
        this.getServer().getPluginManager().registerEvents(new EventListener(config), this);
        command = new CommandLime("lime", this, config);
    }

    @Override
    public void onDisable() {
        config.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return command.execute(sender, ListUtils.arrayList(args));
    }

    public static String getLimit(String lore) {
        // 格式 +3d+2m+1h
        if (lore.matches(".*\\[duration:(\\+\\d+[mhd]){1,3}].*")) {
            String duration = lore.substring(lore.indexOf(":") + 1, lore.indexOf("]"));
            String[] date = duration.split("\\+");
            date[0] = "qwq";
            long limit = 0;
            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
            boolean flag_m = false, flag_h = false;
            for (String time : date) {
                if (time.substring(time.length() - 1).equals("m")) {
                    int minute = Integer.parseInt(time.substring(0, time.length() - 1));
                    if (minute != 0) {
                        limit = limit + minute;
                        flag_m = true;
                    }
                }
                if (time.substring(time.length() - 1).equals("h")) {
                    int hour = Integer.parseInt(time.substring(0, time.length() - 1));
                    if (hour != 0) {
                        limit = limit + hour * 60;
                        flag_h = true;
                    }
                }
                if (time.substring(time.length() - 1).equals("d")) {
                    int day = Integer.parseInt(time.substring(0, time.length() - 1));
                    if (day != 0) {
                        limit = limit + day * 1440;
                    }
                }
            }
            if (flag_m) {
                format.applyPattern("yyyy年MM月dd日HH时mm分");
            } else if (flag_h) {
                format.applyPattern("yyyy年MM月dd日HH时");
            }
            Date limitDate = new Date();
            limitDate.setTime(limitDate.getTime() + limit * 60000);
            return format.format(limitDate);
        } else {
            return "不匹配的格式";
        }
    }

    public static boolean isDead(String lore) {
        if (lore.matches(".*\\[limitime:(\\d+[年月日时分]){3,5}\\].*")) {
            String limitime = lore.substring(lore.indexOf(":") + 1, lore.indexOf("]"));
            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
            if (limitime.contains("分")) {
                format.applyPattern("yyyy年MM月dd日HH时mm分");
            } else if (limitime.contains("时")) {
                format.applyPattern("yyyy年MM月dd日HH时");
            }
            try {
                Date limit = format.parse(limitime);
                if (limit.before(new Date())) {
                    return true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

}
