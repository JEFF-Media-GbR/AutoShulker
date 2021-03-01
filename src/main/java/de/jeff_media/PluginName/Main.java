package de.jeff_media.PluginName;

import de.jeff_media.PluginName.commands.MainCommand;
import de.jeff_media.PluginName.config.Config;
import de.jeff_media.PluginName.config.ConfigUpdater;
import de.jeff_media.PluginName.config.Messages;
import de.jeff_media.PluginUpdateChecker.PluginUpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static final String SPIGOT_RESOURCE_ID = "123456789";
    public static final int BSTATS_ID = 9991;
    private static final String UPDATECHECKER_LINK_API = "https://api.spigotmc.org/legacy/update.php?resource="+SPIGOT_RESOURCE_ID;
    private static final String UPDATECHECKER_LINK_DOWNLOAD = "https://www.spigotmc.org/resources/"+SPIGOT_RESOURCE_ID;
    private static final String UPDATECHECKER_LINK_CHANGELOG = "https://www.spigotmc.org/resources/"+SPIGOT_RESOURCE_ID+"/updates";
    private static final String UPDATECHECKER_LINK_DONATE = "https://paypal.me/mfnalex";

    private PluginUpdateChecker updateChecker;
    private static Main instance;

    public Messages messages;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        reload();
        getCommand("pluginname").setExecutor(new MainCommand());
    }

    public void reload() {
        saveDefaultConfig();
        ConfigUpdater.updateConfig(this);
        reloadConfig();
        initUpdateChecker();

        messages = new Messages();
    }

    private void initUpdateChecker() {
        if(updateChecker == null) {
            updateChecker = new PluginUpdateChecker(this,
                    UPDATECHECKER_LINK_API,
                    UPDATECHECKER_LINK_DOWNLOAD,
                    UPDATECHECKER_LINK_CHANGELOG,
                    UPDATECHECKER_LINK_DONATE);
        } else {
            updateChecker.stop();
        }

        switch(getConfig().getString(Config.CHECK_FOR_UPDATES).toLowerCase()) {
            case "true":
                updateChecker.check((long) (getConfig().getDouble(Config.CHECK_FOR_UPDATES_INTERVAL) * 60 * 60));
                break;
            case "false":
                break;
            default:
                updateChecker.check();
        }
    }
}
