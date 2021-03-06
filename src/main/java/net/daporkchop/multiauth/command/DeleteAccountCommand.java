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
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

/**
 * @author DaPorkchop_
 */
public class DeleteAccountCommand implements CommandExecutor {
    public DeleteAccountCommand() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to do that!");
            return true;
        }

        String cmdName = cmd.getName().toLowerCase();

        if (!cmdName.equals("deleteaccount")) {
            return false;
        }

        if (args.length == 0)    {
            sender.sendMessage("§cUsage: /deleteaccount <password>");
            return true;
        }

        if (!MultiAuth.isLoggedIn(sender.getName()))    {
            sender.sendMessage("§cYou're not logged in!");
            return true;
        }

        User user = MultiAuth.onlineUsers.get(sender.getName());
        if (user.passwordHash != null) {
            if (Arrays.equals(StringHasher.hashPassword(args[0]), user.passwordHash)) {
                user.passwordHash = null;
                user.loggedIn = false;
                sender.sendMessage("§9Account deleted!");
            } else {
                sender.sendMessage("§cInvalid password!");
            }
        } else {
            sender.sendMessage("§cYour account was already deleted!");
        }

        return true;
    }
}
