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
import net.daporkchop.multiauth.util.QueuedUsernameCheck;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author DaPorkchop_
 */
public class RegisterCommmand implements CommandExecutor {
    public RegisterCommmand() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String cmdName = cmd.getName().toLowerCase();

        if (!cmdName.equals("register")) {
            return false;
        }

        if (args.length < 2)    {
            sender.sendMessage("§cUsage: /register <password> <password again>");
            return true;
        }

        if (!args[0].equals(args[1]))   {
            sender.sendMessage("§cPasswords don't match!");
            return true;
        }

        sender.sendMessage("§9Checking name availability...");

        MultiAuth.usernamesToCheck.add(new QueuedUsernameCheck(sender.getName(), (isTaken) -> {
            if (isTaken)    {
                sender.sendMessage("§cThat username is registered by Mojang. To prove that you own the account, please browse to §9http://anarchy.daporkchop.net:8888§c and follow the steps provided.");
            } else {
                MultiAuth.registeredPlayers.put(sender.getName(), MultiAuth.fakeHash(args[0]));
                Player p = Bukkit.getPlayer(sender.getName());
                MultiAuth.loggedInPlayers.add(p);
                MultiAuth.loggedInPlayersName.add(p.getName());
                p.sendMessage("§9Registered!");
                p.setHealth(0);
            }
        }));

        return true;
    }
}
