package de.xzise.xwarp.warpable;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public interface Warpable extends CommandSender {

    boolean teleport(Location location, TeleportCause teleportCause);

}
