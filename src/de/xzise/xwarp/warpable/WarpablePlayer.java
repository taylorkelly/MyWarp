package de.xzise.xwarp.warpable;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WarpablePlayer extends CommandSenderWrapper<Player> implements Warpable, Positionable {

    public WarpablePlayer(Player player) {
        super(player);
    }
    
    public String getName() {
        return this.sender.getName();
    }
    
    @Override
    public void teleport(Location location) {
        this.sender.teleportTo(location);
    }

    @Override
    public Location getLocation() {
        return this.sender.getLocation();
    }
}