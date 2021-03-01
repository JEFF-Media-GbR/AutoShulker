package de.jeff_media.PluginName.config;

import de.jeff_media.PluginName.Main;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
public class Config {

    private final Main main;
    private final FileConfiguration conf;

    public static final String CHECK_FOR_UPDATES = "check-for-updates";
    public static final String CHECK_FOR_UPDATES_INTERVAL = "update-check-interval";

    public static final String CONFIG_VERSION = "config-version";
    public static final String CONFIG_PLUGIN_VERSION = "plugin-version";
    public static final String DEBUG = "debug";

    public Config() {
        main = Main.getInstance();
        conf = main.getConfig();
        Metrics metrics = new Metrics(main,main.BSTATS_ID);

        conf.addDefault(CHECK_FOR_UPDATES, "true");
        metrics.addCustomChart(new Metrics.SimplePie("check_for_updates", () -> conf.getString(CHECK_FOR_UPDATES)));

        conf.addDefault(CHECK_FOR_UPDATES_INTERVAL, 4);
        metrics.addCustomChart(new Metrics.SimplePie("check_for_updates_interval", () -> String.valueOf(conf.getInt(CHECK_FOR_UPDATES_INTERVAL))));

        conf.addDefault(DEBUG, false);
        metrics.addCustomChart(new Metrics.SimplePie("debug", () -> String.valueOf(conf.getBoolean(DEBUG))));
    }

}
