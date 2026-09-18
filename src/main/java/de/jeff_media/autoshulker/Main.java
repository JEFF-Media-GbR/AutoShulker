package de.jeff_media.autoshulker;

import com.google.common.base.Enums;
import com.google.gson.Gson;
import de.jeff_media.autoshulker.commands.MainCommand;
import de.jeff_media.autoshulker.config.Config;
import de.jeff_media.autoshulker.config.ConfigUpdater;
import de.jeff_media.autoshulker.config.Messages;
import de.jeff_media.autoshulker.config.Permissions;
import de.jeff_media.autoshulker.listeners.CraftingListener;
import de.jeff_media.autoshulker.listeners.InventoryClickListener;
import de.jeff_media.autoshulker.listeners.PickUpListener;
import de.jeff_media.autoshulker.listeners.ShulkerBlockListener;
import de.jeff_media.autoshulker.utils.SoundUtils;
import com.jeff_media.updatechecker.UpdateChecker;
import com.jeff_media.updatechecker.UserAgentBuilder;
import org.bukkit.Material;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class Main extends JavaPlugin {

    private static final int SPIGOT_RESOURCE_ID = 89807;
    public static final int BSTATS_ID = 9991;
    private static final String UPDATECHECKER_LINK_API = "https://api.spigotmc.org/legacy/update.php?resource="+SPIGOT_RESOURCE_ID;
    private static final String UPDATECHECKER_LINK_DONATE = "https://paypal.me/mfnalex";
    public Map<Material,String> translatedMaterials;

    private static Main instance;

    public SoundUtils soundUtils;
    public Messages messages;
    public EventPriority eventPriority;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        reload();
        translatedMaterials = getTranslatedMaterialMap(new File(getDataFolder(), "items.json"));
        getCommand("AutoShulker").setExecutor(new MainCommand());
        getServer().getPluginManager().registerEvents(new PickUpListener(),this);
        getServer().getPluginManager().registerEvents(new CraftingListener(),this);
        getServer().getPluginManager().registerEvents(new InventoryClickListener(), this);
        getServer().getPluginManager().registerEvents(new ShulkerBlockListener(), this);
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
        if(getConfig().getBoolean("debug",false)) {
            getLogger().warning(s);
        }
    }

    private static Map<Material, String> getTranslatedMaterialMap(File translationFile) {
        Map<Material, String> map = new HashMap<>();
        for (Material material : Material.values()) {
            map.put(material, getNiceMaterialName(material));
        }

        try (FileReader reader = new FileReader(translationFile)) {
            Map<?, ?> translations = new Gson().fromJson(reader, Map.class);
            for (Material material : Material.values()) {
                Object translation = translations.get(getMinecraftNamespacedName(material));
                if (translation instanceof String) {
                    map.put(material, (String) translation);
                }
            }
        } catch (FileNotFoundException ignored) {
            // The human-readable material name is used when no client file is present.
        } catch (IOException exception) {
            Main.getInstance().getLogger().warning("Could not read items.json: " + exception.getMessage());
        }
        return map;
    }

    private static String getNiceMaterialName(Material material) {
        StringBuilder result = new StringBuilder();
        Iterator<String> words = Arrays.asList(material.name().split("_")).iterator();
        while (words.hasNext()) {
            String word = words.next().toLowerCase(Locale.ROOT);
            result.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            if (words.hasNext()) {
                result.append(' ');
            }
        }
        return result.toString();
    }

    private static String getMinecraftNamespacedName(Material material) {
        String type = material.isBlock() ? "block" : "item";
        return type + ".minecraft." + material.name().toLowerCase(Locale.ROOT);
    }
}
