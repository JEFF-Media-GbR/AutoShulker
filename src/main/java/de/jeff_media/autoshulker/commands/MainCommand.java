package de.jeff_media.autoshulker.commands;

import de.jeff_media.autoshulker.Main;
import de.jeff_media.autoshulker.utils.ShulkerUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

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
        }

        Player player = (Player) commandSender;
        ItemStack paper = player.getInventory().getItemInMainHand();

        if(args[0].equalsIgnoreCase("get")) {
            HashSet<Material> mats = ShulkerUtils.getMaterialSetFromPaper(paper);
            for(Material mat : mats) {
                player.sendMessage(mat.name());
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("add")) {
            ShulkerUtils.addMaterialToPaper(paper,Material.matchMaterial(args[1]));
            return true;
        }

        return true;
    }
}
