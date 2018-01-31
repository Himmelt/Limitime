package org.soraworld.lime.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.soraworld.lime.config.Config;
import org.soraworld.lime.config.LangKeys;
import org.soraworld.lime.util.LimitUtils;
import org.soraworld.lime.util.ServerUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandLimit extends IICommand {

    public CommandLimit(String name, final Plugin plugin, final Config config) {
        super(name);
        addSub(new IICommand("save") {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                config.save();
                ServerUtils.send(sender, LangKeys.format("configSaved"));
                return true;
            }
        });
        addSub(new IICommand("reload") {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                config.load();
                ServerUtils.send(sender, LangKeys.format("configReloaded"));
                return true;
            }
        });
        addSub(new IICommand("lang") {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.isEmpty()) {
                    ServerUtils.send(sender, LangKeys.format("language", config.lang()));
                } else {
                    config.lang(args.get(0));
                    ServerUtils.send(sender, LangKeys.format("language", config.lang()));
                }
                return true;
            }
        });
        addSub(new IICommand("death") {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (args.size() == 1) {
                    switch (args.get(0)) {
                        case "true":
                            config.deathGone(true);
                            break;
                        case "false":
                            config.deathGone(false);
                            break;
                        default:
                            ServerUtils.send(sender, getUsage());
                            break;
                    }
                } else ServerUtils.send(sender, LangKeys.format("deathGone", config.deathGone()));
                return true;
            }

            @Override
            public String getUsage() {
                return ChatColor.GOLD + "/limit death [true|false]";
            }
        });
        addSub(new IICommand("append", "app", "add") {

            private final Pattern PATTERN = Pattern.compile("([+-]\\d+[mhd])+");
            private final Pattern DEADLINE = Pattern.compile("\\[deadline:[0-9-:]+]");
            private final Pattern DURATION = Pattern.compile("\\[duration:([+-]\\d+[mhd])+]");
            private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
            private final Pattern DATE = Pattern.compile("[0-9]{4}(-[0-9]{2}){3}:[0-9]{2}");

            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (sender instanceof Player) {
                    if (args.size() == 1 && PATTERN.matcher(args.get(0)).matches()) {
                        Player player = (Player) sender;
                        ItemStack item = player.getItemInHand();
                        if (item != null) {
                            ItemMeta meta = item.getItemMeta();
                            if (meta == null) meta = Bukkit.getItemFactory().getItemMeta(item.getType());
                            List<String> lore = meta.getLore();
                            if (lore == null) lore = new ArrayList<>();
                            for (int i = 0; i < lore.size(); i++) {
                                String line = lore.get(i);
                                Matcher matcher = DEADLINE.matcher(line);
                                if (matcher.find()) {
                                    Matcher mc = DATE.matcher(line);
                                    if (mc.find()) {
                                        try {
                                            Date date = DATE_FORMAT.parse(mc.group());
                                            date.setTime(date.getTime() + LimitUtils.limitime(args.get(0)));
                                            lore.set(i, LimitUtils.deadLore(date));
                                            LimitUtils.setLore(player, item, meta, lore);
                                            ServerUtils.send(player, "append deadline");
                                            return true;
                                        } catch (ParseException e) {
                                            ServerUtils.send(player, LangKeys.format("parseException"));
                                        }
                                    }
                                    ServerUtils.send(player, "invalid date format");
                                    return true;
                                }
                                matcher = DURATION.matcher(line);
                                if (matcher.find()) {
                                    Matcher mc = PATTERN.matcher(line);
                                    if (mc.find()) {
                                        lore.set(i, LimitUtils.limeLore(mc.group() + args.get(0)));
                                        LimitUtils.setLore(player, item, meta, lore);
                                        ServerUtils.send(player, "append limitime");
                                        return true;
                                    }
                                    ServerUtils.send(player, "not match");
                                    return false;
                                }
                            }
                            lore.add(LimitUtils.limeLore(args.get(0)));
                            LimitUtils.setLore(player, item, meta, lore);
                            ServerUtils.send(player, "set limitime");
                        } else {
                            ServerUtils.send(player, LangKeys.format("emptyHand"));
                        }
                    } else {
                        ServerUtils.send(sender, getUsage());
                    }
                } else {
                    ServerUtils.send(sender, LangKeys.format("onlyPlayer"));
                }
                return true;
            }

            @Override
            public String getUsage() {
                return ChatColor.GOLD + "/limit append|app|add <limitime>";
            }
        });
    }

    public boolean execute(CommandSender sender, ArrayList<String> args) {
        if (args.size() >= 1) {
            IICommand sub = subs.get(args.remove(0));
            if (sub != null) {
                return sub.execute(sender, args);
            }
        }
        return false;
    }

}
