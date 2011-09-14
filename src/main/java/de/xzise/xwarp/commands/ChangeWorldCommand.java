package de.xzise.xwarp.commands;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.xzise.xwarp.Manager;
import de.xzise.xwarp.WarpObject;
import de.xzise.xwarp.editors.Editor;

public class ChangeWorldCommand extends DefaultSubCommand<Manager<? extends WarpObject<? extends Editor>>> {

    private final String label;

    public ChangeWorldCommand(Manager<? extends WarpObject<? extends Editor>> manager, Server server, String label) {
        super(manager, server, "change-world", "chwrld");
        this.label = label;
    }

    @Override
    public String[] getFullHelpText() {
        return new String[] { "Changes the world of all warp objects in a specific world." };
    }

    @Override
    public String getSmallHelpText() {
        return "Changes the world";
    }

    @Override
    public String getCommand() {
        return label + " change-world <old world> <new world>";
    }

    @Override
    public boolean execute(CommandSender sender, String[] parameters) {
        if (parameters.length == 3) {
            String oldWorld = parameters[1];
            String newWorld = parameters[2];
            this.manager.changeWorld(sender, oldWorld, newWorld);
            return true;
        } else {
            return false;
        }
    }

}
