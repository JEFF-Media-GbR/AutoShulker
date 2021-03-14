package de.jeff_media.autoshulker;

import de.jeff_media.autoshulker.commands.MainCommand;
import de.jeff_media.autoshulker.config.Config;
import de.jeff_media.autoshulker.config.ConfigUpdater;
import de.jeff_media.autoshulker.config.Messages;
import de.jeff_media.PluginUpdateChecker.PluginUpdateChecker;
import de.jeff_media.autoshulker.listeners.CraftingListener;
import de.jeff_media.autoshulker.listeners.PickUpListener;
import de.jeff_media.autoshulker.utils.SoundUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class Main extends JavaPlugin {

    private static final String SPIGOT_RESOURCE_ID = "123456789";
    public static final int BSTATS_ID = 9991;
    private static final String UPDATECHECKER_LINK_API = "https://api.spigotmc.org/legacy/update.php?resource="+SPIGOT_RESOURCE_ID;
    private static final String UPDATECHECKER_LINK_DOWNLOAD = "https://www.spigotmc.org/resources/"+SPIGOT_RESOURCE_ID;
    private static final String UPDATECHECKER_LINK_CHANGELOG = "https://www.spigotmc.org/resources/"+SPIGOT_RESOURCE_ID+"/updates";
    private static final String UPDATECHECKER_LINK_DONATE = "https://paypal.me/mfnalex";

    private PluginUpdateChecker updateChecker;
    private static Main instance;

    public SoundUtils soundUtils;
    public Messages messages;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        reload();
        getCommand("AutoShulker").setExecutor(new MainCommand());
        getServer().getPluginManager().registerEvents(new PickUpListener(),this);
        getServer().getPluginManager().registerEvents(new CraftingListener(),this);
    }

    public void reload() {
        saveDefaultConfig();
        ConfigUpdater.updateConfig(this);
        reloadConfig();
        initUpdateChecker();

        messages = new Messages();
        soundUtils = new SoundUtils();
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
