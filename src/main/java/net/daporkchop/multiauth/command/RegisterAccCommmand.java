package net.daporkchop.multiauth.command;

import net.daporkchop.multiauth.MultiAuth;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        MultiAuth.registeredPlayers.put(args[0], MultiAuth.fakeHash(args[1]));
        sender.sendMessage("§9Registered!");

        return true;
    }
}
