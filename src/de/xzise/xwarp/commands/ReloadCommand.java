package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.command.CommandSender;

import de.xzise.xwarp.WarpManager;
import de.xzise.xwarp.PermissionWrapper.PermissionTypes;

public class ReloadCommand extends SubCommand {

    private WarpManager manager;

    public ReloadCommand(WarpManager manager) {
        super("reload");
        this.manager = manager;
    }

    @Override
    protected boolean internalExecute(CommandSender sender, String[] parameters) {
        if (parameters.length == 1) {
            this.manager.reload(sender);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected String[] getFullHelpText() {
        return new String[] { "Reloads xWarp's settings and warps." };
    }

    @Override
    protected String getSmallHelpText() {
        return "Reloads xWarp.";
    }

    @Override
    protected String getCommand() {
        return "warp reload";
    }

    @Override
    protected boolean listHelp(CommandSender sender) {
        return MyWarp.permissions.permission(sender, PermissionTypes.ADMIN_RELOAD);
    }

}
