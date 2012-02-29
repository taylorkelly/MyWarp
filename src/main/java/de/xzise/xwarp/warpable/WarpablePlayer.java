package de.xzise.xwarp.warpable;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import de.xzise.metainterfaces.CommandSenderWrapper;
import de.xzise.metainterfaces.Nameable;

public class WarpablePlayer extends CommandSenderWrapper<Player> implements Warpable, Positionable, Nameable {

    public WarpablePlayer(Player player) {
        super(player);
    }

    @Override
    public boolean teleport(Location location, TeleportCause teleportCause) {
        return this.sender.teleport(location, teleportCause);
    }

    @Override
    public Location getLocation() {
        return this.sender.getLocation();
    }
}