package org.soraworld.lime.command;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.soraworld.lime.config.Config;
import org.soraworld.lime.config.LangKeys;
import org.soraworld.lime.util.ServerUtils;

import java.util.ArrayList;
import java.util.List;

public class CommandLime extends IICommand {

    public CommandLime(String name, final Plugin plugin, final Config config) {
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
        addSub(new IICommand("set") {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {
                if (sender instanceof Player) {
                    if (args.size() == 1) {
                        Player player = (Player) sender;
                        ItemStack item = player.getItemInHand();
                        if (item != null) {
                            /// already has duration lore !!!
                            ItemMeta meta = item.getItemMeta();
                            List<String> lore = meta.getLore();
                            if (lore == null) lore = new ArrayList<>();
                            lore.add(args.get(0).replace('&', ChatColor.COLOR_CHAR));
                            meta.setLore(lore);
                            item.setItemMeta(meta);
                            player.setItemInHand(item);
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
                return ChatColor.GOLD + "/lime set <duration>";
            }
        });
        addSub(new IICommand("delay") {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {

                return true;
            }
        });
        addSub(new IICommand("cancel") {
            @Override
            public boolean execute(CommandSender sender, ArrayList<String> args) {

                return true;
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
