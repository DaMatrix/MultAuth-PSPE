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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author DaPorkchop_
 */
public class Config {
    public static int webPort;
    public static int verificationPort;
    public static String webAddress;
    public static String verificationAddress;
    public static String name;
    public static Location loginLocation;

    public static void init(MultiAuth plugin) {
        plugin.saveDefaultConfig();
        FileConfiguration configuration = plugin.getConfig();

        webPort = configuration.getInt("server.web.port", 8888);
        verificationPort = configuration.getInt("server.verification.port", 10293);
        webAddress = configuration.getString("server.web.address", "play.your-server.com") + (webPort == 80 ? "" : ":" + String.valueOf(webPort));
        verificationAddress = configuration.getString("server.verification.address", "play.your-server.com") + (webPort == 25565 ? "" : ":" + String.valueOf(webPort));
        name = configuration.getString("server.name", "Your cool server name");

        loginLocation = new Location(
                Bukkit.getWorld(configuration.getString("player.spawn-location.world", "world")),
                configuration.getDouble("player.spawn-location.x", 0.0d),
                configuration.getDouble("player.spawn-location.y", 1000.0d),
                configuration.getDouble("player.spawn-location.z", 0.0d)
        );
    }
}
