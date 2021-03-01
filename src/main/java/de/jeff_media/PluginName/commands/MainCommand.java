package de.jeff_media.PluginName.commands;

import de.jeff_media.PluginName.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MainCommand implements CommandExecutor {

    private final Main main;

    public MainCommand() {
        this.main=Main.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        if(args.length == 0) {
            return false;
        }

        switch (args[0].toLowerCase()) {

            case "reload":
                return ReloadCommand.run(commandSender, command, alias, args);

            default:
                return false;
        }
    }
}
