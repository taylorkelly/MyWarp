package de.xzise.xwarp.warpable;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

public interface Positionable extends CommandSender {

    Location getLocation();
    
}
