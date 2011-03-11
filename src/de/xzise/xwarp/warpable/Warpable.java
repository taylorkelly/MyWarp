package de.xzise.xwarp.warpable;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

public interface Warpable extends CommandSender {

    void teleport(Location location);
    
}
