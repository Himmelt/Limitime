package org.soraworld.lime.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.soraworld.lime.util.ServerUtils;

import java.io.File;

public class Config {

    private String lang = "en_us";

    private final File file;
    private final LangKeys langKeys;
    private final YamlConfiguration config = new YamlConfiguration();
    private boolean deathGone = true;

    public Config(File file) {
        this.file = new File(file, "config.yml");
        this.langKeys = new LangKeys(new File(file, "lang"));
    }

    public void load() {
        if (!file.exists()) {
            save();
            return;
        }
        try {
            config.load(file);
            lang = config.getString("lang");
            deathGone = config.getBoolean("deathGone");
            if (lang == null || lang.isEmpty()) {
                lang = "en_us";
            }
        } catch (Throwable e) {
            //e.printStackTrace();
            ServerUtils.console("config file load exception !!!");
        }
        langKeys.setLang(lang);
    }

    public void save() {
        try {
            config.set("lang", lang);
            config.set("deathGone", deathGone);
            config.save(file);
        } catch (Throwable e) {
            //e.printStackTrace();
            ServerUtils.console("config file save exception !!!");
        }
    }

    public void lang(String lang) {
        if (lang != null && !lang.isEmpty()) {
            this.lang = lang;
            langKeys.setLang(lang);
        }
    }

    public String lang() {
        return this.lang;
    }

    public boolean deathGone() {
        return this.deathGone;
    }

    public void deathGone(boolean deathGone) {
        this.deathGone = deathGone;
    }
}
