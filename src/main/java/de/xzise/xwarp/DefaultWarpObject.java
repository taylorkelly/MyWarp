package de.xzise.xwarp;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import de.xzise.MinecraftUtil;
import de.xzise.wrappers.permissions.BufferPermission;
import de.xzise.wrappers.permissions.Permission;
import de.xzise.xwarp.editors.Editor;
import de.xzise.xwarp.editors.EditorPermissions;
import de.xzise.xwarp.editors.EditorPermissions.Type;
import de.xzise.xwarp.warpable.WarperFactory;
import de.xzise.xwarp.wrappers.permission.WarpEditorPermission;

public abstract class DefaultWarpObject<T extends Enum<T> & Editor> implements WarpObject<T> {

    private String name;
    private String owner;
    private String creator;
    private final Map<EditorPermissions.Type, Map<String, EditorPermissions<T>>> editors;
    private final Class<T> editorPermissionClass;

    public final T invitePermission;

    protected DefaultWarpObject(String name, String owner, String creator, Map<EditorPermissions.Type, Map<String, EditorPermissions<T>>> editors, Class<T> editorPermissionClass, T invitePermission) {
        this.name = name;
        this.owner = owner;
        this.creator = creator;
        this.editors = MinecraftUtil.createEnumMap(editors, EditorPermissions.Type.class);
        this.editorPermissionClass = editorPermissionClass;
        this.invitePermission = invitePermission;
    }

    // TODO: Restruct to objects with id and those without?
    public abstract void assignNewId();

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public final String getName() {
        return this.name;
    }

    @Override
    public final String getOwner() {
        return this.owner;
    }

    @Override
    public final String getCreator() {
        return this.creator;
    }

    @Override
    public void removeEditor(String name, EditorPermissions.Type type) {
        Map<String, EditorPermissions<T>> typePermissions = this.editors.get(type);
        if (typePermissions != null) {
            typePermissions.remove(name.toLowerCase());
        }
    }

    @Override
    public void addEditor(String name, EditorPermissions.Type type, ImmutableSet<T> permissions) {
        this.getEditorPermissions(name, true, type).putSet(permissions, true);
    }

    public ImmutableMap<String, EditorPermissions<T>> getEditorPermissions(EditorPermissions.Type type) {
        Map<String, EditorPermissions<T>> typePermissions = this.editors.get(type);
        if (typePermissions != null) {
            return ImmutableMap.copyOf(typePermissions);
        } else {
            return ImmutableMap.of();
        }
    }

    /**
     * Returns the editor permissions to the type and name. Doesn't create new
     * if there are no permissions.
     * 
     * @param name
     *            Name of the editor permissions holder.
     * @param type
     *            Type of the editor permissions holder.
     * @return The editor permissions. If there are no one to the name and type
     *         returns null.
     */
    public EditorPermissions<T> getEditorPermissions(String name, EditorPermissions.Type type) {
        return this.getEditorPermissions(name, false, type);
    }

    /**
     * Returns the editor permissions to the type and name. If
     * <code>create</code> is set to true, creates a new, if there are no editor
     * permissions. Otherwise null.
     * 
     * @param name
     *            Name of the editor permissions holder.
     * @param create
     *            Create new editor permissions, if doesn't exists.
     * @param type
     *            Type of the editor permissions holder.
     * @return The editor permissions. If there are no one to the name and type
     *         returns null.
     */
    public EditorPermissions<T> getEditorPermissions(String name, boolean create, EditorPermissions.Type type) {
        Map<String, EditorPermissions<T>> typePermissions = this.editors.get(type);
        if (typePermissions == null && create) {
            typePermissions = Maps.newHashMap();
            this.editors.put(type, typePermissions);
        }

        if (typePermissions != null) {
            EditorPermissions<T> editorPermissions = typePermissions.get(name.toLowerCase());
            if (editorPermissions == null && create) {
                editorPermissions = new EditorPermissions<T>(this.editorPermissionClass);
                typePermissions.put(name.toLowerCase(), editorPermissions);
            }
            return editorPermissions;
        } else {
            return null;
        }
    }

    public static class EditorPermissionEntry<T extends Enum<T> & Editor> {

        public final EditorPermissions<T> editorPermissions;
        public final String name;
        public final EditorPermissions.Type type;

        public EditorPermissionEntry(EditorPermissions<T> editorPermissions, String name, Type type) {
            this.editorPermissions = editorPermissions;
            this.name = name;
            this.type = type;
        }
    }

    public Collection<EditorPermissionEntry<T>> getEditorPermissionsList() {
        List<EditorPermissionEntry<T>> allEntries = Lists.newArrayList();
        for (Entry<EditorPermissions.Type, Map<String, EditorPermissions<T>>> typeEntry : this.editors.entrySet()) {
            for (Entry<String, EditorPermissions<T>> editorEntry : typeEntry.getValue().entrySet()) {
                allEntries.add(new EditorPermissionEntry<T>(editorEntry.getValue(), editorEntry.getKey(), typeEntry.getKey()));
            }
        }
        return allEntries;
    }

    public static boolean isOwn(WarpObject<?> warpObject, String name) {
        return warpObject.getOwner().equals(name);
    }

    public boolean isOwn(String name) {
        return isOwn(this, name);
    }

    public static boolean isCreator(WarpObject<?> warpObject, String name) {
        return warpObject.getCreator().equals(name);
    }

    public boolean isCreator(String name) {
        return isCreator(this, name);
    }

    public static <T extends Editor> boolean isInvited(WarpObject<T> warpObject, String name) {
        return warpObject.hasPermission(name, warpObject.getInvitePermission());
    }

    public boolean isInvited(String name) {
        return isInvited(this, name);
    }

    public boolean hasPlayerPermission(String name, T permission) {
        EditorPermissions<T> ep = this.getEditorPermissions(name, Type.PLAYER);
        return ep != null && ep.get(permission);
    }

    public boolean hasGroupPermission(String name, T permission) {
        for (String group : XWarp.permissions.getGroup(this.getWorld(), name)) {
            EditorPermissions<T> ep = this.getEditorPermissions(group, Type.GROUP);
            if (ep != null && ep.get(permission)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasEditorPermission(String name, T permission) {
        Player player = Bukkit.getServer().getPlayer(name);
        return player != null && hasEditorPermission(player, permission);
    }

    public boolean hasEditorPermission(CommandSender sender, T permission) {
        return XWarp.permissions.permission(sender, new WarpEditorPermission(this, permission));
    }

    public boolean hasSpecificPermission(String name, T permission) {
        Player player = Bukkit.getServer().getPlayer(name);
        return player != null && hasSpecificPermission(player, permission);
    }

    public boolean hasSpecificPermission(CommandSender sender, T permission) {
        for (Entry<String, EditorPermissions<T>> permissionEntry : this.getEditorPermissions(Type.PERMISSIONS).entrySet()) {
            if (XWarp.permissions.permission(sender, new BufferPermission<Boolean>(permissionEntry.getKey(), false)) && permissionEntry.getValue().get(permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(String name, T permission) {
        Player player = Bukkit.getServer().getPlayer(name);
        return this.hasPlayerPermission(name, permission) || this.hasGroupPermission(name, permission) || (player != null && (this.hasEditorPermission(player, permission) || this.hasSpecificPermission(player, permission)));
    }

    public void invite(String player) {
        this.getEditorPermissions(player, true, Type.PLAYER).put(this.invitePermission, true);
    }

    @Override
    public T getInvitePermission() {
        return this.invitePermission;
    }

    public static boolean canModify(CommandSender sender, boolean defaultModification, Permission<Boolean> defaultPermission, Permission<Boolean> adminPermission) {
        if (defaultPermission != null) {
            return ((defaultModification && XWarp.permissions.permission(sender, defaultPermission)) || XWarp.permissions.permission(sender, adminPermission));
        } else {
            return (defaultModification || XWarp.permissions.permission(sender, adminPermission));
        }
    }

    public boolean playerCanModify(Player player, T permission) {
        if (this.isOwn(player.getName()))
            return true;
        EditorPermissions<T> ep = this.getEditorPermissions(player.getName().toLowerCase(), Type.PLAYER);
        if (ep != null && ep.get(permission)) {
            return true;
        }
        String[] groups = XWarp.permissions.getGroup(player.getWorld().getName(), player.getName());
        for (String group : groups) {
            EditorPermissions<T> groupPerm = this.getEditorPermissions(group, Type.GROUP);
            if (groupPerm != null && groupPerm.get(permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canModify(CommandSender sender, T permission) {
        Player player = WarperFactory.getPlayer(sender);
        boolean canModify = false;
        if (player != null) {
            canModify = this.playerCanModify(player, permission);
        }

        return canModify(sender, canModify, permission.getDefault(), permission.getAdmin());
    }
}
