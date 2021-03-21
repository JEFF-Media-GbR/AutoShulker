package de.jeff_media.autoshulker.config;

import de.jeff_media.autoshulker.enums.ShulkerType;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public class Permissions {

    public static final String PREFIX = "AutoShulker.";

    public static final String ALLOW_RELOAD = PREFIX + "reload";

    public static final String CRAFT_NODE = PREFIX + "craft.";

    public static final String PICKUP_WITH_NODE = PREFIX + "pickup.with.";

    public static void registerPermissions() {

        Bukkit.getServer().getPluginManager().addPermission(new Permission(ALLOW_RELOAD, PermissionDefault.OP));
        for(ShulkerType shulkerType : ShulkerType.values()) {

            Bukkit.getServer().getPluginManager().addPermission(
                    new Permission(Permissions.CRAFT_NODE + shulkerType.name().toLowerCase(), PermissionDefault.TRUE)
            );

            Bukkit.getServer().getPluginManager().addPermission(
                    new Permission(Permissions.PICKUP_WITH_NODE + shulkerType.name().toLowerCase(), PermissionDefault.TRUE)
            );
        }
    }

}
