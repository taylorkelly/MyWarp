package de.xzise.xwarp.commands;

import org.bukkit.command.CommandSender;

import de.xzise.commands.CommonHelpableSubCommand;
import de.xzise.wrappers.economy.EconomyHandler;
import de.xzise.wrappers.permissions.PermissionsHandler;
import de.xzise.xwarp.WarpManager;

public class StatusCommand extends CommonHelpableSubCommand {

    private final WarpManager m;
    private final EconomyHandler economy;
    private final PermissionsHandler permissions;
    
    public StatusCommand(WarpManager list, EconomyHandler economy, PermissionsHandler permissions) {
        super("status");
        this.m = list;
        this.economy = economy;
        this.permissions = permissions;
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length == 1) {
            sender.sendMessage("xWarp status:");
            sender.sendMessage("Number of warps: " + this.m.getSize(null, null));
            sender.sendMessage("Economy: " + this.economy.getWrapperName());
            sender.sendMessage("Permissions: " + this.permissions.getWrapperName());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Shows the status of xWarp." };
    }

    @Override
    public String getSmallHelpText() {
        return "Shows status";
    }

    @Override
    public String getCommand() {
        return "warp status";
    }

}
