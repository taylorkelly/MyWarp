package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.WarpManager;

public class PublicizeCommand extends WarpCommand {

	public PublicizeCommand(WarpManager list, Server server) {
		super(list, server, "", "public");
	}

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String[] parameters) {
		this.list.publicize(warpName, creator, sender);
		return true;
	}

	@Override
	protected String[] getFullHelpText() {
		return new String[] { "Publizices the warp so everybody could visit it." };
	}

	@Override
	protected String getSmallHelpText() {
		return "Publizices the warp";
	}
}
