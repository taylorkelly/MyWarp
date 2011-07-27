package de.xzise.xwarp;

import org.bukkit.command.CommandSender;

import de.xzise.xwarp.editors.Editor;

public interface WarpObject<T extends Editor> {

    String getName();
    String getOwner();
    String getCreator();
    String getWorld();
    boolean list(CommandSender sender);
    boolean canModify(CommandSender sender, T permission);
}
