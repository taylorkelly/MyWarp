package de.xzise.xwarp;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import me.taylorkelly.mywarp.MyWarp;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import de.xzise.MinecraftUtil;
import de.xzise.xwarp.editors.Editor;
import de.xzise.xwarp.editors.EditorPermissions;
import de.xzise.xwarp.editors.EditorPermissions.Type;
import de.xzise.xwarp.warpable.WarperFactory;
import de.xzise.xwarp.wrappers.permission.PermissionTypes;

public abstract class DefaultWarpObject<T extends Enum<T> & Editor> implements WarpObject<T> {

    private String name;
    private String owner;
    private String creator;
    private final Map<EditorPermissions.Type, Map<String, EditorPermissions<T>>> editors;
    private final Class<T> editorPermissionClass;
    
    public DefaultWarpObject(String name, String owner, String creator, Map<EditorPermissions.Type, Map<String, EditorPermissions<T>>> editors, Class<T> editorPermissionClass) {
        this.name = name;
        this.owner = owner;
        this.creator = creator;
        this.editors = MinecraftUtil.createEnumMap(editors, EditorPermissions.Type.class);
        this.editorPermissionClass = editorPermissionClass;
    }

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

    public void removeEditor(String name, EditorPermissions.Type type) {
        Map<String, EditorPermissions<T>> typePermissions = this.editors.get(type);
        if (typePermissions != null) {
            typePermissions.remove(name.toLowerCase());
        }
    }

    public ImmutableMap<String, EditorPermissions<T>> getEditorPermissions(EditorPermissions.Type type) {
        Map<String, EditorPermissions<T>> typePermissions = this.editors.get(type);
        if (typePermissions != null) {
            return ImmutableMap.copyOf(typePermissions);
        } else {
            return null;
        }
    }
    
    /**
     * Returns the editor permissions to the type and name. Doesn't create new if there are no permissions.
     * @param name Name of the editor permissions holder.
     * @param type Type of the editor permissions holder.
     * @return The editor permissions. If there are no one to the name and type returns null.
     */
    public EditorPermissions<T> getEditorPermissions(String name, EditorPermissions.Type type) {
        return this.getEditorPermissions(name, false, type);
    }
    
    /**
     * Returns the editor permissions to the type and name. If <code>create</code> is set to true, creates a new, if there are no editor permissions. Otherwise null.
     * @param name Name of the editor permissions holder.
     * @param create Create new editor permissions, if doesn't exists.
     * @param type Type of the editor permissions holder.
     * @return The editor permissions. If there are no one to the name and type returns null.
     */
    public EditorPermissions<T> getEditorPermissions(String name, boolean create, EditorPermissions.Type type) {
        Map<String, EditorPermissions<T>> typePermissions = this.editors.get(type);
        EditorPermissions<T> editorPermissions = typePermissions.get(name.toLowerCase());
        if (editorPermissions == null && create) {
            editorPermissions = new EditorPermissions<T>(this.editorPermissionClass);
            typePermissions.put(name.toLowerCase(), editorPermissions);
        }
        return editorPermissions;
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

    public boolean isOwn(String name) {
        return this.getOwner().equals(name);
    }

    public static boolean canModify(CommandSender sender, boolean defaultModification, PermissionTypes defaultPermission, PermissionTypes adminPermission) {
        if (defaultPermission != null) {
            return ((defaultModification && MyWarp.permissions.permission(sender, defaultPermission)) || MyWarp.permissions.permission(sender, adminPermission));
        } else {
            return (defaultModification || MyWarp.permissions.permission(sender, adminPermission));
        }
    }

    public boolean playerCanModify(Player player, T permission) {
        if (this.isOwn(player.getName()))
            return true;
        EditorPermissions<T> ep = this.getEditorPermissions(player.getName().toLowerCase(), Type.PLAYER);
        if (ep != null && ep.get(permission)) {
            return true;
        }
        String[] groups = MyWarp.permissions.getGroup(player.getWorld().getName(), player.getName());
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
