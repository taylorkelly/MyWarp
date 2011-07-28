package de.xzise.xwarp.commands.warp;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Warp;
import de.xzise.xwarp.WarpManager;

public class PublicizeCommand extends WarpCommand {

    public PublicizeCommand(WarpManager list, Server server) {
        super(list, server, "", "public");
    }

    @Override
    protected boolean executeEdit(Warp warp, CommandSender sender, String[] parameters) {
        this.manager.publicize(warp, sender);
        return true;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Publizices the warp so everybody could visit it." };
    }

    @Override
    public String getSmallHelpText() {
        return "Publizices the warp";
    }
}
