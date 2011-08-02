package de.xzise.xwarp.commands.xwarp;

import org.bukkit.command.CommandSender;

import de.xzise.commands.CommonHelpableSubCommand;
import de.xzise.wrappers.economy.EconomyHandler;
import de.xzise.wrappers.permissions.PermissionsHandler;
import de.xzise.xwarp.WPAManager;
import de.xzise.xwarp.WarpManager;

public class StatusCommand extends CommonHelpableSubCommand {

    private final WarpManager warpManager;
    private final WPAManager wpaManager;
    private final EconomyHandler economy;
    private final PermissionsHandler permissions;
    
    public StatusCommand(EconomyHandler economy, PermissionsHandler permissions, WarpManager warpManager, WPAManager wpaManager) {
        super("status");
        this.warpManager = warpManager;
        this.wpaManager = wpaManager;
        this.economy = economy;
        this.permissions = permissions;
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length == 1) {
            sender.sendMessage("xWarp status:");
            sender.sendMessage("Number of warps: " + this.warpManager.getSize());
            sender.sendMessage("Number of warp protection areas: " + this.wpaManager.getSize());
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
