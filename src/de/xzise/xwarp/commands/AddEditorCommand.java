package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.WarpManager;

public class AddEditorCommand extends WarpCommand {

	public AddEditorCommand(WarpManager list, Server server) {
		super(list, server, new String[] {"editor", "permissions" }, "add-editor");
	}

	@Override
	protected boolean executeEdit(CommandSender sender, String warpName, String creator, String[] parameters) {
		this.list.addEditor(warpName, creator, sender, this.getPlayer(parameters[0]), parameters[1]);
		return true;
	}

	@Override
	protected String[] getFullHelpText() {
		return new String[] { "Adds the editor to the warps editors list.", "The permissions define the allowed commands.", "Update (l), Rename (r), Uninvite (u), Invite (i), Private (-), Public (+), Global (!), Give (g), Delete (d), Warp (w).", "* allows all commands, s sets lruiw, all after a slash removes the permission", "Example: */d allows everthing except delete." };
	}

	@Override
	protected String getSmallHelpText() {
		return "Adds the " + this.getParameterText(true, false, 0);
	}
	
	protected boolean listHelp(CommandSender sender) {
		return true;
	}

}
