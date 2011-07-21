package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.WarpManager;

public class RenameCommand extends WarpCommand {

    public RenameCommand(WarpManager list, Server server) {
        super(list, server, "new name", "rename", "mv");
    }

    @Override
    protected boolean executeEdit(CommandSender sender, String warpName, String creator, String[] parameters) {
        this.list.rename(warpName, creator, sender, parameters[0]);
        return true;
    }

    @Override
    protected String[] getFullHelpText() {
        return new String[] { "Changes the name of the warp." };
    }

    @Override
    protected String getSmallHelpText() {
        return "Renames the warp";
    }
}
