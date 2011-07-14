*EditorPermissions holding now an EnumMap; +EditorPermissions.Type enum to differ between player/group (maybe other) editor permissions; !uninvite revokes the warp permission; +commands support groups/offline editors; *updated list help; *SQLite has better support for new features (update, WPA missing); !deleted warps in yml now written; +warp.to.warp.<owner>.<name> permission; +write listed attribute; !yml save cd/wu in warps; *(remove,add)Editor now support Group/Player;
# Please enter the commit message for your changes. Lines starting
# with '#' will be ignored, and an empty message aborts the commit.
# On branch v3
# Changes to be committed:
#   (use "git reset HEAD <file>..." to unstage)
#
#	modified:   src/de/xzise/xwarp/EditorPermissions.java
#	deleted:    src/de/xzise/xwarp/PermissionWrapper.java
#	deleted:    src/de/xzise/xwarp/WarpList.java
#	modified:   src/de/xzise/xwarp/WarpManager.java
#	modified:   src/de/xzise/xwarp/commands/AddEditorCommand.java
#	modified:   src/de/xzise/xwarp/commands/ListCommand.java
#	modified:   src/de/xzise/xwarp/commands/RemoveEditorCommand.java
#	modified:   src/de/xzise/xwarp/dataconnections/HModConnection.java
#	modified:   src/de/xzise/xwarp/dataconnections/SQLiteConnection.java
#	modified:   src/de/xzise/xwarp/dataconnections/YmlConnection.java
#	modified:   src/de/xzise/xwarp/listeners/XWBlockListener.java
#	new file:   src/de/xzise/xwarp/wrappers/permission/WarpToWarpPermission.java
#	modified:   src/me/taylorkelly/mywarp/Warp.java
#
