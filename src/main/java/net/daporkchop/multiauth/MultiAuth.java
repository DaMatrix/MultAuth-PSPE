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

import net.daporkchop.multiauth.command.ChangePassCommand;
import net.daporkchop.multiauth.command.DeleteAccountCommand;
import net.daporkchop.multiauth.command.LoginCommand;
import net.daporkchop.multiauth.command.RegisterAccCommmand;
import net.daporkchop.multiauth.command.RegisterCommmand;
import net.daporkchop.multiauth.command.ResetAccCommand;
import net.daporkchop.multiauth.util.DataTag;
import net.daporkchop.multiauth.util.QueuedUsernameCheck;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class MultiAuth extends JavaPlugin {
    public static DataTag dataTag = new DataTag(new File(DataTag.HOME_FOLDER.getPath() + File.separatorChar + ".multiauth.dat"));
    public static ArrayList<String> loggedInPlayersName = new ArrayList<>();
    public static ArrayList<Player> loggedInPlayers = new ArrayList<>();
    public static HashMap<String, String> registeredPlayers = new HashMap<>();
    public static HashMap<String, Integer> loginAttempts = new HashMap<>();
    public static ArrayList<QueuedUsernameCheck> usernamesToCheck = new ArrayList<>();

    public static boolean isLoggedIn(Player p) {
        return loggedInPlayers.contains(p);
    }

    public static boolean isLoggedIn(String name) {
        return loggedInPlayersName.contains(name);
    }

    /**
     * not really a hash but who cares
     *
     * @param pass
     * @return
     */
    public static String fakeHash(String pass) {
        String hash = new BigInteger(pass.getBytes()).toString(16);
        return hash;
    }

    @Override
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers())  {
            if (!isLoggedIn(p)) {
                p.teleport(Listener.playerLocs.get(p.getName()));
            }
        }
        dataTag.setSerializable("registeredPlayers", registeredPlayers);
        dataTag.save();
        ServerManager.server.close();
        WebServer.INSTANCE.stop();
    }

    @Override
    public void onEnable() {
        new Timer().schedule(new TimerTask() {
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
                    } catch (Exception e)   {
                        e.printStackTrace();
                    }
                }

                if (++i == 10) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (!MultiAuth.isLoggedIn(p)) {
                            p.sendMessage("§cUse /login to log in!");
                        }
                    }

                    i = 0;
                }
            }
        }, 1000, 1000);
        new Thread() {
            public void run()   {
                ServerManager.init();
                try {
                    new WebServer();
                } catch (IOException e)   {
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
        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                Listener.loginLocation = new Location(Bukkit.getWorld("world"), 0d, 1d, 0d);
            }
        }, 0);
        registeredPlayers = (HashMap<String, String>) dataTag.getSerializable("registeredPlayers", new HashMap<String, String>());
    }
}
