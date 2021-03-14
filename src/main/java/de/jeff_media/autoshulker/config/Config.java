package de.jeff_media.autoshulker.config;

import de.jeff_media.autoshulker.Main;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.FileConfiguration;
public class Config {

    private final Main main;
    private final FileConfiguration conf;

    public static final String CHECK_FOR_UPDATES = "check-for-updates";
    public static final String CHECK_FOR_UPDATES_INTERVAL = "update-check-interval";
    public static final String ITEM_MATERIAL = "item-material";
    public static final String ITEM_NAME = "item-name";
    public static final String ITEM_LORE = "item-lore";
    public static final String ITEM_LORE_LINE = "item-lore-line";
    public static final String SOUND_EFFECT = "sound-effect";
    public static final String SOUND_ENABLED = "sound-enabled";
    public static final String SOUND_GLOBAL = "sound-global";
    public static final String SOUND_VOLUME = "sound-volume";
    public static final String SOUND_PITCH = "sound-pitch";

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

        conf.addDefault(ITEM_MATERIAL, "BOOK");
        metrics.addCustomChart(new Metrics.SimplePie("item_material", () -> conf.getString(ITEM_MATERIAL).toUpperCase()));

        conf.addDefault(ITEM_NAME, "AutoShulker");
        metrics.addCustomChart(new Metrics.SimplePie("item_name", () -> conf.getString(ITEM_NAME)));

        conf.addDefault(ITEM_LORE,"This AutoShulker collects:");
        conf.addDefault(ITEM_LORE_LINE,"- %s");
        conf.addDefault(SOUND_GLOBAL,true);
        conf.addDefault(SOUND_ENABLED,true);
        conf.addDefault(SOUND_EFFECT,"ENTITY_ITEM_PICKUP");
        conf.addDefault(SOUND_VOLUME, 1.0);
        conf.addDefault(SOUND_PITCH, 1.0);
    }

}
