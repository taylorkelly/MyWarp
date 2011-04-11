package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.WarpManager;

public class ChangeCreatorCommand extends WarpCommand {

    public ChangeCreatorCommand(WarpManager list, Server server) {
        super(list, server, "player", "change-creator", "chcre");
    }

    @Override
    protected boolean executeEdit(CommandSender sender, String warpName, String creator, String[] parameters) {
        this.list.changeCreator(warpName, creator, sender, this.getPlayer(parameters[0]));
        return true;
    }

    @Override
    protected String[] getFullHelpText() {
        return new String[] { "Changes the creator of the warp." };
    }

    @Override
    protected String getSmallHelpText() {
        return "Change the creator";
    }

}
