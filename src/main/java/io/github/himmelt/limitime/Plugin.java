package io.github.himmelt.limitime;
/* Created by Kami on 2016/5/27.*/

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugin extends JavaPlugin {

    public static Plugin plugin;

    @Override
    public void onLoad() {
        plugin = this;
        Log.info("Limitime is loaded!");
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Events(), this);
        Log.info("Limitime is enabled!");
    }

    @Override
    public void onDisable() {
        this.getServer().getScheduler().cancelAllTasks();
        Log.info("Limitime is disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("limitime")) {
            //如果玩家输入了/basic则执行如下内容...
            sender.sendMessage("§a========§e§l物品限时插件(v0.1)§r§a========");
            sender.sendMessage("§a===§ehttps://github.com/Himmelt/Limitime§r§a===");
            sender.sendMessage("§e添加时限lore的格式为:");
            sender.sendMessage("§a§l[duration:+1d+2m+3h]");
            sender.sendMessage("§6时限部分可以为:");
            sender.sendMessage("§a§l+1m,+1m+1d,+1d+1m+1h,+1d+1d,+1m+1m+1m");
            sender.sendMessage("§a§l+1h+1h+1d,§r§6全部为叠加状态.");
            return true;
        }
        return false;
    }
}
