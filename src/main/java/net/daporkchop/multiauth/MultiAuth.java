/*
 * Adapted from the Wizardry License
 *
 * Copyright (c) 2018-2018 DaPorkchop_ and contributors
 *
 * Permission is hereby granted to any persons and/or organizations using this software to copy, modify, merge, publish, and distribute it. Said persons and/or organizations are not allowed to use the software or any derivatives of the work for commercial use or any other means to generate income, nor are they allowed to claim this software as their own.
 *
 * The persons and/or organizations are also disallowed from sub-licensing and/or trademarking this software without explicit permission from DaPorkchop_.
 *
 * Any persons and/or organizations using this software must disclose their source code and have it publicly available, include this license, provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.daporkchop.multiauth;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.daporkchop.lib.db.DBBuilder;
import net.daporkchop.lib.db.IOManager;
import net.daporkchop.lib.db.PorkDB;
import net.daporkchop.lib.db.entry.impl.treemap.DBTreeMap;
import net.daporkchop.lib.db.entry.impl.treemap.TreeMapInitializer;
import net.daporkchop.lib.db.serializer.Serializer;
import net.daporkchop.lib.db.serializer.nbt.NBTObjectSerializer;
import net.daporkchop.lib.db.serializer.nbt.ZLIBCompoundTagSerializer;
import net.daporkchop.lib.hash.HashAlg;
import net.daporkchop.multiauth.command.ChangePassCommand;
import net.daporkchop.multiauth.command.DeleteAccountCommand;
import net.daporkchop.multiauth.command.LoginCommand;
import net.daporkchop.multiauth.command.RegisterAccCommmand;
import net.daporkchop.multiauth.command.RegisterCommmand;
import net.daporkchop.multiauth.command.ResetAccCommand;
import net.daporkchop.multiauth.util.QueuedUsernameCheck;
import net.daporkchop.multiauth.util.StringHasher;
import net.daporkchop.multiauth.util.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

/**
 * @author DaPorkchop_
 */
public class MultiAuth extends JavaPlugin {
    public static Map<String, User> onlineUsers = new Hashtable<>();
    public static DBTreeMap<String, User> registeredPlayers;
    public static TObjectIntMap<String> loginAttempts = new TObjectIntHashMap<>();
    public static List<QueuedUsernameCheck> usernamesToCheck = new ArrayList<>();
    private PorkDB db;

    public static boolean isLoggedIn(Player p) {
        return isLoggedIn(p.getName());
    }

    public static boolean isLoggedIn(String name) {
        User user = onlineUsers.get(name);
        return user != null && user.loggedIn;
    }

    @Override
    public void onDisable() {
        if (this.db != null) {
            ServerManager.server.close();
            WebServer.INSTANCE.stop();
            this.saveConfig();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!isLoggedIn(p)) {
                    p.teleport(Listener.playerLocs.get(p.getName()));
                }
            }

            this.db.shutdownBlocking();
            this.db = null;
        }
    }

    @Override
    public void onEnable() {
        Config.init(this);

        getLogger().info("Opening database...");
        this.db = new DBBuilder()
                .setFile(new File(".", "multiauth.db"))
                .setForceLoad(true)
                .setHashAlg(HashAlg.MD5)
                .build();

        registeredPlayers = this.db.newTreeMap("registeredPlayers",
                new TreeMapInitializer<>(
                        new Serializer<String>() {
                            @Override
                            public long write(long existingFlag, String val, RandomAccessFile file, IOManager manager) {
                                throw new UnsupportedOperationException();
                            }

                            @Override
                            public String read(long pos, RandomAccessFile file, IOManager manager) {
                                throw new UnsupportedOperationException();
                            }

                            @Override
                            public int getBytesSize() {
                                throw new UnsupportedOperationException();
                            }
                        },
                        new StringHasher(HashAlg.MD5),
                        new NBTObjectSerializer<>(
                                ZLIBCompoundTagSerializer.INSTANCE,
                                (user, tag) -> {
                                    tag.putLong("uuidMost", user.uuid.getMostSignificantBits());
                                    tag.putLong("uuidLeast", user.uuid.getLeastSignificantBits());
                                    tag.putByteArray("passwordHash", user.passwordHash);
                                },
                                tag -> {
                                    byte[] passwordHash = tag.getByteArray("passwordHash");
                                    UUID uuid = new UUID(tag.getLong("uuidMost"), tag.getLong("uuidLeast"));
                                    User user = new User(uuid);
                                    user.passwordHash = passwordHash;
                                    return user;
                                }
                        ), false));
        getLogger().info("Opened database!");

        //this should run async to prevent ticks from being delayed by HTTP requests
        new Timer("MultiAuth account fetcher thread").schedule(new TimerTask() {
            int i = 0;

            @Override
            public void run() {
                if (usernamesToCheck.size() > 0) {
                    QueuedUsernameCheck check = usernamesToCheck.remove(0);
                    try {
                        String url = "https://api.mojang.com/users/profiles/minecraft/" + check.username;

                        URL obj = new URL(url);
                        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                        // optional default is GET
                        con.setRequestMethod("GET");

                        int responseCode = con.getResponseCode();
                        con.disconnect();
                        check.func.accept(responseCode != 204);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (++i == 5) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!MultiAuth.isLoggedIn(p)) {
                            p.sendMessage("§cUse /login to log in!");
                        }
                    }
                    i = 0;
                }
            }
        }, 1000, 1500);

        //launch web server async
        new Thread() {
            public void run() {
                ServerManager.init();
                try {
                    new WebServer();
                } catch (IOException e) {
                    e.printStackTrace();
                    Bukkit.shutdown();
                }
            }
        }.start();
        getServer().getPluginManager().registerEvents(new Listener(), this);
        getCommand("register").setExecutor(new RegisterCommmand());
        getCommand("login").setExecutor(new LoginCommand());
        getCommand("changepass").setExecutor(new ChangePassCommand());
        getCommand("deleteaccount").setExecutor(new DeleteAccountCommand());
        getCommand("resetacc").setExecutor(new ResetAccCommand());
        getCommand("registeracc").setExecutor(new RegisterAccCommmand());
    }
}
