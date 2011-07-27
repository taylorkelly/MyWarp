package de.xzise.xwarp.commands.warp;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.commands.WarpCommand;

public class PriceCommand extends WarpCommand {

    public PriceCommand(WarpManager list, Server server) {
        super(list, server, "price", "price");
    }

    @Override
    protected boolean executeEdit(Warp warp, CommandSender sender, String[] parameters) {
        if (parameters.length == 1) {
            Double price = MinecraftUtil.tryAndGetDouble(parameters[0]);
            if (price == null) {
                sender.sendMessage(ChatColor.RED + "Invalid price given. The price has to be a double.");
            } else {
                this.manager.setPrice(warp, sender, price);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Sets the price for the warp." };
    }

    @Override
    public String getSmallHelpText() {
        return "Set price";
    }

}
