package de.xzise.xwarp;

import org.bukkit.command.CommandSender;

import me.taylorkelly.mywarp.Warp.Visibility;

public interface WarpObject {

    String getName();
    String getOwner();
    String getCreator();
    String getWorld();
    Visibility getVisibility();
    //TODO: Rename to list(CommandSender);
    boolean listWarp(CommandSender sender);
}
