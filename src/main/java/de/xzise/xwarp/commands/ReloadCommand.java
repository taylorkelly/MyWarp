package de.xzise.xwarp.commands;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.command.CommandSender;

import de.xzise.commands.CommonHelpableSubCommand;
import de.xzise.xwarp.Manager;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;

public class ReloadCommand extends CommonHelpableSubCommand {

    private Manager<?> manager;

    public ReloadCommand(Manager<?> manager) {
        super("reload");
        this.manager = manager;
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length == 1) {
            this.manager.reload(sender);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Reloads xWarp's settings and warps." };
    }

    @Override
    public String getSmallHelpText() {
        return "Reloads xWarp.";
    }

    @Override
    public String getCommand() {
        return "warp reload";
    }

    @Override
    public boolean listHelp(CommandSender sender) {
        return MyWarp.permissions.permission(sender, PermissionTypes.ADMIN_RELOAD);
    }

}
