package de.xzise.xwarp.commands;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.WarpManager;

public class PriceCommand extends WarpCommand {

    public PriceCommand(WarpManager list, Server server) {
        super(list, server, "price", "price");
    }

    @Override
    protected boolean executeEdit(CommandSender sender, String warpName, String owner, String[] parameters) {
        if (parameters.length == 1) {
            Integer price = MinecraftUtil.tryAndGetInteger(parameters[0]);
            if (price == null) {
                sender.sendMessage(ChatColor.RED + "Invalid price given. The price has to be a integer.");
            } else {
                this.list.setPrice(warpName, owner, sender, price);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected String[] getFullHelpText() {
        return new String[] { "Sets the price for the warp." };
    }

    @Override
    protected String getSmallHelpText() {
        return "Set price";
    }

}
