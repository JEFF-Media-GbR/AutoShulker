package de.jeff_media.PluginName.config;

import de.jeff_media.PluginName.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Messages {

    public final String TEST1;
    public final String CONFIG_RELOADED;

    private final Main main;

    public Messages() {
        this.main = Main.getInstance();

        TEST1 = load("test","&aThis is a test message.");

        CONFIG_RELOADED = color(String.format("&a%s has been reloaded.",main.getName()));
    }

    public static void showActionBarMessage(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }

    private String load(String path, String defaultMessage) {
        String messagePrefix = "message-";
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString(messagePrefix + path,defaultMessage));
    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&',message);
    }

}
