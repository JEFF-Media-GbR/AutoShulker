package de.jeff_media.autoshulker;

import com.google.common.base.Enums;
import de.jeff_media.autoshulker.commands.MainCommand;
import de.jeff_media.autoshulker.config.Config;
import de.jeff_media.autoshulker.config.ConfigUpdater;
import de.jeff_media.autoshulker.config.Messages;
import de.jeff_media.autoshulker.config.Permissions;
import de.jeff_media.autoshulker.listeners.CraftingListener;
import de.jeff_media.autoshulker.listeners.InventoryClickListener;
import de.jeff_media.autoshulker.listeners.PickUpListener;
import de.jeff_media.autoshulker.utils.SoundUtils;
import de.jeff_media.daddy.Stepsister;
import de.jeff_media.updatechecker.UpdateChecker;
import de.jeff_media.updatechecker.UserAgentBuilder;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private static final int SPIGOT_RESOURCE_ID = 89807;
    public static final int BSTATS_ID = 9991;
    private static final String UPDATECHECKER_LINK_API = "https://api.spigotmc.org/legacy/update.php?resource="+SPIGOT_RESOURCE_ID;
    private static final String UPDATECHECKER_LINK_DONATE = "https://paypal.me/mfnalex";

    private static Main instance;

    public SoundUtils soundUtils;
    public Messages messages;
    public EventPriority eventPriority;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        Stepsister.init(this);
        instance = this;
        reload();
        getCommand("AutoShulker").setExecutor(new MainCommand());
        getServer().getPluginManager().registerEvents(new PickUpListener(),this);
        getServer().getPluginManager().registerEvents(new CraftingListener(),this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        Permissions.registerPermissions();
    }

    public void reload() {
        saveDefaultConfig();
        ConfigUpdater.updateConfig(this);
        reloadConfig();
        initUpdateChecker();

        messages = new Messages();
        soundUtils = new SoundUtils();
        eventPriority = Enums.getIfPresent(EventPriority.class,getConfig().getString(Config.EVENT_PRIORITY).toUpperCase()).or(EventPriority.HIGHEST);
    }

    private void initUpdateChecker() {
        UpdateChecker.init(this,UPDATECHECKER_LINK_API)
                .setDownloadLink(SPIGOT_RESOURCE_ID)
                .setChangelogLink(SPIGOT_RESOURCE_ID)
                .setDonationLink(UPDATECHECKER_LINK_DONATE)
                .setUserAgent(UserAgentBuilder.getDefaultUserAgent().addSpigotUserId())
                .setUsingPaidVersion(true)
                .setColoredConsoleOutput(true)
                .suppressUpToDateMessage(true);

        switch(getConfig().getString(Config.CHECK_FOR_UPDATES).toLowerCase()) {
            case "true":
                UpdateChecker.getInstance().checkEveryXHours(getConfig().getDouble(Config.CHECK_FOR_UPDATES_INTERVAL)).checkNow();
                break;
            case "false":
                break;
            default:
                UpdateChecker.getInstance().checkNow();
        }
    }

    public void debug(String s) {
        if(false) {
            getLogger().warning(s);
        }
    }
}
