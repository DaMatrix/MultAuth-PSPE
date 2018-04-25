package net.daporkchop.multiauth.command;

import net.daporkchop.multiauth.MultiAuth;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChangePassCommand implements CommandExecutor {
    public ChangePassCommand() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String cmdName = cmd.getName().toLowerCase();

        if (!cmdName.equals("changepass")) {
            return false;
        }

        if (args.length == 0)    {
            sender.sendMessage("§cUsage: /changepass <new password>");
            return true;
        }

        if (!MultiAuth.isLoggedIn(sender.getName()))    {
            sender.sendMessage("§cYou're not logged in!");
            return true;
        }

        MultiAuth.registeredPlayers.put(sender.getName(), MultiAuth.fakeHash(args[0]));
        sender.sendMessage("§9Changed password to: " + args[0]);

        return true;
    }
}
