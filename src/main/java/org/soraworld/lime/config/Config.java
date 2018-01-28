package org.soraworld.lime.config;

import org.bukkit.configuration.file.YamlConfiguration;
import org.soraworld.lime.util.ServerUtils;

import java.io.File;

public class Config {

    private String lang = "en_us";

    private final File file;
    private final LangKeys langKeys;
    private final YamlConfiguration config = new YamlConfiguration();

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
            config.save(file);
        } catch (Throwable e) {
            //e.printStackTrace();
            ServerUtils.console("config file save exception !!!");
        }
    }

    public void lang(String lang) {
        this.lang = lang;
    }

    public String lang() {
        return this.lang;
    }

}
