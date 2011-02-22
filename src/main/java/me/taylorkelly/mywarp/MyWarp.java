package me.taylorkelly.mywarp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.taylorkelly.mywarp.griefcraft.Updater;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.java.JavaPlugin;

public class MyWarp extends JavaPlugin {

    private WarpList warpList;
    private MWPlayerListener playerListener;
    private MWBlockListener blockListener;
    public String name;
    public String version;
    private Updater updater;
    public static final Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onDisable() {
        ConnectionManager.closeConnection();
    }

    @Override
    public void onEnable() {
        name = this.getDescription().getName();
        version = this.getDescription().getVersion();

        updater = new Updater();
        try {
            updater.check();
            updater.update();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File newDatabase = new File(getDataFolder(), "warps.db");
        File oldDatabase = new File("homes-warps.db");
        if (!newDatabase.exists() && oldDatabase.exists()) {
            updateFiles(oldDatabase, newDatabase);
        }


        Connection conn = ConnectionManager.initialize(getDataFolder());
        if (conn == null) {
            log.log(Level.SEVERE, "[MYWARP] Could not establish SQL connection. Disabling MyWarp");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        warpList = new WarpList(getServer());
        blockListener = new MWBlockListener(warpList);
        playerListener = new MWPlayerListener(warpList);

        WarpPermissions.initialize(getServer());
        WarpSettings.initialize(getDataFolder());

        getServer().getPluginManager().registerEvent(Type.PLAYER_CHAT, playerListener, Priority.Low, this);
        getServer().getPluginManager().registerEvent(Type.BLOCK_RIGHTCLICKED, blockListener, Priority.High, this);
        log.info(name + " " + version + " enabled");
    }


    private void updateFiles(File oldDatabase, File newDatabase) {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        if (newDatabase.exists()) {
            newDatabase.delete();
        }
        try {
            newDatabase.createNewFile();
        } catch (IOException ex) {
            severe("Could not create new database file", ex);
        }
        copyFile(oldDatabase, newDatabase);
    }

    /**
     * File copier from xZise
     * @param fromFile
     * @param toFile
     */
    private static void copyFile(File fromFile, File toFile) {
        FileInputStream from = null;
        FileOutputStream to = null;
        try {
            from = new FileInputStream(fromFile);
            to = new FileOutputStream(toFile);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = from.read(buffer)) != -1) {
                to.write(buffer, 0, bytesRead);
            }
        } catch (IOException ex) {
            Logger.getLogger(MyWarp.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (from != null) {
                try {
                    from.close();
                } catch (IOException e) {
                }
            }
            if (to != null) {
                try {
                    to.close();
                } catch (IOException e) {
                }
            }
        }
    }
    
    private boolean warning;

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        String[] split = args;
        String commandName = command.getName().toLowerCase();

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (commandName.equals("warp")) {
                /**
                 * /warp convert
                 */
                if (split.length == 1 && split[0].equalsIgnoreCase("convert") && WarpPermissions.isAdmin(player)) {
                    if (!warning) {
                        player.sendMessage(ChatColor.RED + "Warning: " + ChatColor.WHITE + "Only use a copy of warps.txt.");
                        player.sendMessage("This will delete the warps.txt it uses");
                        player.sendMessage("Use " + ChatColor.RED + "'/warp convert'" + ChatColor.WHITE + " again to confirm.");
                        warning = true;
                    } else {
                        Converter.convert(player, getServer(), warpList);
                        warning = false;
                    }
                    /**
                     * /warp list or /warp list #
                     */
                } else if ((split.length == 1 || (split.length == 2 && isInteger(split[1]))) && split[0].equalsIgnoreCase("list")
                        && WarpPermissions.list(player)) {
                    Lister lister = new Lister(warpList);
                    lister.addPlayer(player);

                    if (split.length == 2) {
                        int page = Integer.parseInt(split[1]);
                        if (page < 1) {
                            player.sendMessage(ChatColor.RED + "Page number can't be below 1.");
                            return true;
                        } else if (page > lister.getMaxPages(player)) {
                            player.sendMessage(ChatColor.RED + "There are only " + lister.getMaxPages(player) + " pages of warps");
                            return true;
                        }
                        lister.setPage(page);
                    } else {
                        lister.setPage(1);
                    }
                    lister.list();

                    /**
                     * /warp slist
                     */
                } else if (split.length == 1 && split[0].equalsIgnoreCase("slist") && WarpPermissions.list(player)) {
                    warpList.list(player);
                    /**
                     * /warp search <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("search") && WarpPermissions.search(player)) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    Searcher searcher = new Searcher(warpList);
                    searcher.addPlayer(player);
                    searcher.setQuery(name);
                    searcher.search();
                    /**
                     * /warp create <name>
                     */
                } else if (split.length > 1 && (split[0].equalsIgnoreCase("create") || split[0].equalsIgnoreCase("set"))
                        && (WarpPermissions.publicCreate(player) || WarpPermissions.privateCreate(player))) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }
                    if (WarpPermissions.publicCreate(player)) {
                        warpList.addWarp(name, player);
                    } else {
                        warpList.addWarpPrivate(name, player);
                    }
                    /**
                     * /warp point <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("point") && WarpPermissions.compass(player)) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }
                    warpList.point(name, player);
                    /**
                     * /warp pcreate <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("pcreate") && WarpPermissions.privateCreate(player)) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.addWarpPrivate(name, player);
                    /**
                     * /warp delete <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("delete") && WarpPermissions.delete(player)) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.deleteWarp(name, player);
                    /**
                     * /warp welcome <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("welcome") && WarpPermissions.welcome(player)) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.welcomeMessage(name, player);
                    /**
                     * /warp private <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("private") && WarpPermissions.canPrivate(player)) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.privatize(name, player);
                    /**
                     * /warp public <name>
                     */
                } else if (split.length > 1 && split[0].equalsIgnoreCase("public") && WarpPermissions.canPublic(player)) {
                    String name = "";
                    for (int i = 1; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.publicize(name, player);

                    /**
                     * /warp give <player> <name>
                     */
                } else if (split.length > 2 && split[0].equalsIgnoreCase("give") && WarpPermissions.give(player)) {
                    Player givee = getServer().getPlayer(split[1]);
                    // TODO Change to matchPlayer
                    String giveeName = (givee == null) ? split[1] : givee.getName();

                    String name = "";
                    for (int i = 2; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.give(name, player, giveeName);

                    /**
                     * /warp invite <player> <name>
                     */
                } else if (split.length > 2 && split[0].equalsIgnoreCase("invite") && WarpPermissions.invite(player)) {
                    Player invitee = getServer().getPlayer(split[1]);
                    // TODO Change to matchPlayer
                    String inviteeName = (invitee == null) ? split[1] : invitee.getName();

                    String name = "";
                    for (int i = 2; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.invite(name, player, inviteeName);
                    /**
                     * /warp uninvite <player> <name>
                     */
                } else if (split.length > 2 && split[0].equalsIgnoreCase("uninvite") && WarpPermissions.uninvite(player)) {
                    Player invitee = getServer().getPlayer(split[1]);
                    String inviteeName = (invitee == null) ? split[1] : invitee.getName();

                    String name = "";
                    for (int i = 2; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }

                    warpList.uninvite(name, player, inviteeName);

                    /**
                     * /warp player <player> <name>
                     */
                } else if (split.length > 2 && split[0].equalsIgnoreCase("player") && WarpPermissions.isAdmin(player)) {
                    Player invitee = getServer().getPlayer(split[1]);
                    String inviteeName = (invitee == null) ? split[1] : invitee.getName();

                    // TODO ChunkLoading
                    String name = "";
                    for (int i = 2; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }
                    warpList.adminWarpTo(name, invitee, player);

                    /**
                     * /warp help
                     */
                } else if (split.length == 1 && split[0].equalsIgnoreCase("help")) {
                    ArrayList<String> messages = new ArrayList<String>();
                    messages.add(ChatColor.RED + "-------------------- " + ChatColor.WHITE + "/WARP HELP" + ChatColor.RED + " --------------------");
                    if (WarpPermissions.warp(player)) {
                        messages.add(ChatColor.RED + "/warp <name>" + ChatColor.WHITE + "  -  Warp to " + ChatColor.GRAY + "<name>");
                    }
                    if (WarpPermissions.publicCreate(player) || WarpPermissions.privateCreate(player)) {
                        messages.add(ChatColor.RED + "/warp create <name>" + ChatColor.WHITE + "  -  Create warp " + ChatColor.GRAY + "<name>");
                    }
                    if (WarpPermissions.privateCreate(player)) {
                        messages.add(ChatColor.RED + "/warp pcreate <name>" + ChatColor.WHITE + "  -  Create warp " + ChatColor.GRAY + "<name>");
                    }

                    if (WarpPermissions.delete(player)) {
                        messages.add(ChatColor.RED + "/warp delete <name>" + ChatColor.WHITE + "  -  Delete warp " + ChatColor.GRAY + "<name>");
                    }

                    if (WarpPermissions.welcome(player)) {
                        messages.add(ChatColor.RED + "/warp welcome <name>" + ChatColor.WHITE + "  -  Change the welcome message of " + ChatColor.GRAY
                                + "<name>");
                    }

                    if (WarpPermissions.list(player)) {
                        messages.add(ChatColor.RED + "/warp list <#>" + ChatColor.WHITE + "  -  Views warp page " + ChatColor.GRAY + "<#>");
                    }

                    if (WarpPermissions.search(player)) {
                        messages.add(ChatColor.RED + "/warp search <query>" + ChatColor.WHITE + "  -  Search for " + ChatColor.GRAY + "<query>");
                    }
                    if (WarpPermissions.give(player)) {
                        messages.add(ChatColor.RED + "/warp give <player> <name>" + ChatColor.WHITE + "  -  Give " + ChatColor.GRAY + "<player>"
                                + ChatColor.WHITE + " your " + ChatColor.GRAY + "<name>");
                    }
                    if (WarpPermissions.invite(player)) {
                        messages.add(ChatColor.RED + "/warp invite <player> <name>" + ChatColor.WHITE + "  -  Invite " + ChatColor.GRAY + "<player>"
                                + ChatColor.WHITE + " to " + ChatColor.GRAY + "<name>");
                    }
                    if (WarpPermissions.uninvite(player)) {
                        messages.add(ChatColor.RED + "/warp uninvite <player> <name>" + ChatColor.WHITE + "  -  Uninvite " + ChatColor.GRAY + "<player>"
                                + ChatColor.WHITE + " to " + ChatColor.GRAY + "<name>");
                    }
                    if (WarpPermissions.canPublic(player)) {
                        messages.add(ChatColor.RED + "/warp public <name>" + ChatColor.WHITE + "  -  Makes warp " + ChatColor.GRAY + "<name>" + ChatColor.WHITE
                                + " public");
                    }
                    if (WarpPermissions.canPrivate(player)) {
                        messages.add(ChatColor.RED + "/warp private <name>" + ChatColor.WHITE + "  -  Makes warp " + ChatColor.GRAY + "<name>"
                                + ChatColor.WHITE + " private");
                    }
                    for (String message : messages) {
                        player.sendMessage(message);
                    }

                    /**
                     * /warp <name>
                     */
                } else if (split.length > 0 && WarpPermissions.warp(player)) {
                    // TODO ChunkLoading
                    String name = "";
                    for (int i = 0; i < split.length; i++) {
                        name += split[i];
                        if (i + 1 < split.length) {
                            name += " ";
                        }
                    }
                    warpList.warpTo(name, player);
                } else {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public static boolean isInteger(String string) {
        try {
            Integer.parseInt(string);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static void severe(String string, Exception ex) {
        log.log(Level.SEVERE, "[MYHOME]" + string, ex);

    }

    public static void severe(String string) {
        log.log(Level.SEVERE, "[MYHOME]" + string);
    }
}
