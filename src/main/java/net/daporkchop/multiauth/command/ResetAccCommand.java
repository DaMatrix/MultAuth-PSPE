package net.daporkchop.multiauth.command;

import net.daporkchop.multiauth.MultiAuth;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ResetAccCommand implements CommandExecutor {
    public ResetAccCommand() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String cmdName = cmd.getName().toLowerCase();

        if (!cmdName.equals("resetacc")) {
            return false;
        }

        if (sender.hasPermission("bukkit.command.reload")) {
            if (args.length == 0) {
                sender.sendMessage("§cUsage: /resetacc <player name>");
                return true;
            }

            MultiAuth.registeredPlayers.remove(args[0]);
            sender.sendMessage("§9Account deleted!");
        } else {
            sender.sendMessage("§4wtf you can't delete other people's accounts");
        }

        return true;
    }
}
