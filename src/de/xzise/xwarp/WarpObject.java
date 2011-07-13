package de.xzise.xwarp;

import org.bukkit.command.CommandSender;

public interface WarpObject {

    String getName();
    String getOwner();
    String getCreator();
    String getWorld();
    //TODO: Rename to list(CommandSender);
    boolean listWarp(CommandSender sender);
}
