package io.github.himmelt.limitime;
/* Created by Kami on 2016/5/27.*/

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Limitime extends JavaPlugin {

    public static Logger logger = Logger.getLogger("Limitime");
    public static Limitime plugin;

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new Events(), this);
    }

    @Override
    public void onDisable() {
        // 插件卸载时要执行的代码（略）
        this.getServer().getScheduler().cancelAllTasks();
    }
}
