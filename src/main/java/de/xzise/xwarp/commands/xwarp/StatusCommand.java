package de.xzise.xwarp.commands.xwarp;

import org.bukkit.command.CommandSender;

import de.xzise.commands.CommonHelpableSubCommand;
import de.xzise.wrappers.economy.EconomyHandler;
import de.xzise.wrappers.permissions.PermissionsHandler;
import de.xzise.xwarp.Manager;
import de.xzise.xwarp.WPAManager;
import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.WarpObject;

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
            sender.sendMessage("Number of warps: " + managerSize(this.warpManager));
            sender.sendMessage("Number of warp protection areas: " + managerSize(this.wpaManager));
            sender.sendMessage("Economy: " + this.economy.getWrapperName());
            sender.sendMessage("Permissions: " + this.permissions.getWrapperName());
            if (this.warpManager.isLinkedWithMarkerAPI()) {
                sender.sendMessage("Linked with dynmap marker API");
            } else {
                sender.sendMessage("Not linked with dynmap marker API");
            }
            return true;
        } else {
            return false;
        }
    }

    private static String managerSize(Manager<?> manager) {
        String result = Integer.toString(manager.getSize());
        int invalidCount = 0;
        for (WarpObject<?> warpObject : manager.getWarpObjects()) {
            if (!warpObject.isValid()) {
                invalidCount++;
            }
        }
        if (invalidCount > 0) {
            result += " (invalid: " + invalidCount + ")";
        }
        return result;
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
