package net.daporkchop.multiauth.command;

import net.daporkchop.multiauth.MultiAuth;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeleteAccountCommand implements CommandExecutor {
    public DeleteAccountCommand() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
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

        if (MultiAuth.fakeHash(args[0]).equals(MultiAuth.registeredPlayers.get(sender.getName())))  {
            MultiAuth.registeredPlayers.remove(sender.getName());
            sender.sendMessage("§9Account deleted!");
        } else {
            sender.sendMessage("§cInvalid password!");
        }

        return true;
    }
}
