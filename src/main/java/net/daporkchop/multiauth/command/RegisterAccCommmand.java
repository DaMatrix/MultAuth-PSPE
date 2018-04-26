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

package net.daporkchop.multiauth.command;

import net.daporkchop.multiauth.MultiAuth;
import net.daporkchop.multiauth.util.StringHasher;
import net.daporkchop.multiauth.util.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * @author DaPorkchop_
 */
public class RegisterAccCommmand implements CommandExecutor {
    public RegisterAccCommmand() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String cmdName = cmd.getName().toLowerCase();

        if (!cmdName.equals("registeracc")) {
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /registeracc <username> <password>");
            return true;
        }

        User user = new User(Bukkit.getOfflinePlayer(args[0]).getUniqueId());
        user.passwordHash = StringHasher.hashPassword(args[1]);
        MultiAuth.registeredPlayers.put(args[0], user);
        if (MultiAuth.onlineUsers.containsKey(args[0])) {
            MultiAuth.onlineUsers.put(args[0], user);
        }
        sender.sendMessage("§9Registered!");

        return true;
    }
}
