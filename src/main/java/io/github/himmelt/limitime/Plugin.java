package io.github.himmelt.limitime;
/* Created by Kami on 2016/5/27.*/

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
}
