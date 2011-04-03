package de.xzise.metainterfaces;

import org.bukkit.command.ConsoleCommandSender;

import de.xzise.MinecraftUtil;

public class ConsoleCommandWrapper extends ConsoleCommandSender implements LinesCountable, Nameable {

    public final static String NAME = "[CONSOLE]";
    
    public ConsoleCommandWrapper(ConsoleCommandSender sender) {
        super(sender.getServer());
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getMaxLinesVisible() {
        return MinecraftUtil.CONSOLE_LINES_COUNT;
    }

}
