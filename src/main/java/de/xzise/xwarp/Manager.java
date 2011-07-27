package de.xzise.xwarp;

import org.bukkit.command.CommandSender;

import de.xzise.xwarp.editors.EditorPermissions;

public interface Manager<T extends WarpObject<?>> {

    void reload(CommandSender sender);

    void delete(T warpObject, CommandSender sender);
    void setCreator(T warpObject, CommandSender sender, String creator);
    void setOwner(T warpObject, CommandSender sender, String owner);
    void setName(T warpObject, CommandSender sender, String name);

    void invite(T warpObject, CommandSender sender, String inviteeName);
    void uninvite(T warpObject, CommandSender sender, String inviteeName);

    void addEditor(T warpObject, CommandSender sender, String editor, EditorPermissions.Type type, String permissions);
    void removeEditor(T warpObject, CommandSender sender, String editor, EditorPermissions.Type type);

    boolean isNameAvailable(T warpObject);
    boolean isNameAvailable(String name, String owner);

    T getWarpObject(String name, String owner, String playerName);

    void missing(String name, String owner, CommandSender sender);

}