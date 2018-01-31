package org.soraworld.lime;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.soraworld.lime.command.CommandLimit;
import org.soraworld.lime.command.IICommand;
import org.soraworld.lime.config.Config;
import org.soraworld.lime.listener.EventListener;
import org.soraworld.lime.util.ListUtils;

public class Limitime extends JavaPlugin {

    private Config config;
    private IICommand command;

    @Override
    public void onEnable() {
        config = new Config(this.getDataFolder());
        config.load();
        config.save();
        this.getServer().getPluginManager().registerEvents(new EventListener(config), this);
        command = new CommandLimit("limit", this, config);
    }

    @Override
    public void onDisable() {
        config.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return command.execute(sender, ListUtils.arrayList(args));
    }

}
