package de.jeff_media.autoshulker.config;

import de.jeff_media.autoshulker.Main;
import de.jeff_media.autoshulker.data.PickupResult;
import de.jeff_media.autoshulker.utils.ShulkerUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class Messages {

    public final String TEST1;
    public final String CONFIG_RELOADED;
    public final String MSG_ACTIONBAR, MSG_ACTIONBAR_GARBAGE;

    private final Main main;

    public Messages() {
        this.main = Main.getInstance();

        TEST1 = load("test","&aThis is a test message.");
        MSG_ACTIONBAR = load("actionbar","§aAutoShulker collected {amount}x {item}");
        MSG_ACTIONBAR_GARBAGE = load("actionbar-garbage","&cAutoShulker discarded {amount}x {item}");

        CONFIG_RELOADED = color(String.format("&a%s has been reloaded.",main.getName()));
    }

    public static void showActionBarMessage(Player player, String message) {
        if(message!= null) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
        }
    }

    private String load(String path, String defaultMessage) {
        String messagePrefix = "message-";
        return ChatColor.translateAlternateColorCodes('&', main.getConfig().getString(messagePrefix + path,defaultMessage));
    }

    private String color(String message) {
        return ChatColor.translateAlternateColorCodes('&',message);
    }

    public @Nullable String getActionbarMessage(PickupResult pickupResult) {
        ItemStack item = pickupResult.getOriginalItemStack();
        if(pickupResult.getCollected()>0) {
            return MSG_ACTIONBAR
                    .replaceAll("\\{amount}",String.valueOf(item.getAmount()))
                    .replaceAll("\\{item}",main.translatedMaterials.get(item.getType())) ;
        }
        if(pickupResult.getDiscarded()>0) {
            return MSG_ACTIONBAR_GARBAGE
                    .replaceAll("\\{amount}",String.valueOf(item.getAmount()))
                    .replaceAll("\\{item}",main.translatedMaterials.get(item.getType())) ;
        }
        return null;
    }

}
