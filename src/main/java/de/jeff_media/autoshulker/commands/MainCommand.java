package de.jeff_media.autoshulker.commands;

import de.jeff_media.autoshulker.Main;
import de.jeff_media.autoshulker.nbt.NBTTags;
import de.jeff_media.autoshulker.utils.ShulkerUtils;
import com.jeff_media.customblockdata.CustomBlockData;
import com.jeff_media.morepersistentdatatypes.DataType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
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

            case "debug":
                if(commandSender.isOp()) {
                    if(!(commandSender instanceof Player)) {
                        commandSender.sendMessage("This command is only available for players.");
                        return true;
                    }
                    if(ShulkerUtils.isAutoShulkerBox(((Player)commandSender).getInventory().getItemInMainHand())) {
                        commandSender.sendMessage("§aThe item in your hand is an AutoShulker.");
                    } else {
                        commandSender.sendMessage("§cThe item in your hand is NOT an AutoShulker.");
                    }
                    return true;
                }

            case "block":
                if(commandSender.isOp()) {
                    if(!(commandSender instanceof Player)) {
                        commandSender.sendMessage("This command is only available for players.");
                        return true;
                    }
                    Player player = (Player) commandSender;
                    Block block = player.getTargetBlockExact(5);
                    CustomBlockData cbd = new CustomBlockData(block, main);
                    main.getLogger().info(""+cbd.get(new NamespacedKey(main,NBTTags.PAPER), DataType.ITEM_STACK));
                    if(cbd.has(new NamespacedKey(main, NBTTags.PAPER), DataType.ITEM_STACK)) {
                        commandSender.sendMessage("§aThe block you're looking at is an AutoShulker.");
                    } else {
                        commandSender.sendMessage("§cThe block you're looking at is NOT an AutoShulker.");
                    }
                    return true;
                }
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
