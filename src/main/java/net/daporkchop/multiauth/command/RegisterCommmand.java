package net.daporkchop.multiauth.command;

import net.daporkchop.multiauth.MultiAuth;
import net.daporkchop.multiauth.util.QueuedUsernameCheck;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
