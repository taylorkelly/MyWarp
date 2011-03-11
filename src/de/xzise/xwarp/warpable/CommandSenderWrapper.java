package de.xzise.xwarp.warpable;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

public abstract class CommandSenderWrapper<T extends CommandSender> implements CommandSender {

    protected final T sender;
    
    protected CommandSenderWrapper(T sender) {
        this.sender = sender;
    }
    
    @Override
    public void sendMessage(String message) {
        this.sender.sendMessage(message);
    }

    @Override
    public boolean isOp() {
        return this.sender.isOp();
    }

    @Override
    @Deprecated
    public boolean isPlayer() {
        return this.sender.isPlayer();
    }

    @Override
    public Server getServer() {
        return this.sender.getServer();
    }
}
