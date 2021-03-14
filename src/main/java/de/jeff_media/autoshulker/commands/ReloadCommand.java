package de.jeff_media.autoshulker.commands;

import de.jeff_media.autoshulker.Main;
import de.jeff_media.autoshulker.config.Permissions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadCommand {

    public static boolean run(CommandSender commandSender, Command command, String alias, String[] args) {

        Main main = Main.getInstance();

        if(!commandSender.hasPermission(Permissions.ALLOW_RELOAD)) {
            commandSender.sendMessage(command.getPermissionMessage());
            return true;
        }
        main.reload();
        commandSender.sendMessage(main.messages.CONFIG_RELOADED);
        return true;
    }

}
