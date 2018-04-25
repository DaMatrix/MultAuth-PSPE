package net.daporkchop.multiauth.command;

import net.daporkchop.multiauth.Listener;
import net.daporkchop.multiauth.MultiAuth;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class LoginCommand implements CommandExecutor {
    public LoginCommand() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String cmdName = cmd.getName().toLowerCase();

        if (!cmdName.equals("login")) {
            return false;
        }

        if (args.length == 0)    {
            sender.sendMessage("Usage: /login <password>");
            return true;
        }

        String pass = MultiAuth.registeredPlayers.getOrDefault(sender.getName(), null);
        if (pass == null)   {
            sender.sendMessage("§cYou're not registered! Use /register to register (duh)");
            return true;
        } else {
            Player p = Bukkit.getPlayer(sender.getName());
            String hashed = MultiAuth.fakeHash(args[0]);
            if (hashed.equals(pass))    {
                sender.sendMessage("Logged in!");
                MultiAuth.loggedInPlayersName.add(p.getName());
                MultiAuth.loggedInPlayers.add(p);
                p.teleport(Listener.playerLocs.remove(p.getName()));
            } else {
                sender.sendMessage("§4§lINVALID PASSWORD!");
                int attempts = MultiAuth.loginAttempts.put(sender.getName(), MultiAuth.loginAttempts.containsKey(sender.getName()) ? MultiAuth.loginAttempts.get(sender.getName()) + 1 : 1);
                sender.sendMessage("§4§lATTEMPTS: " + attempts);
                if (attempts >= 3)  {
                    p.kickPlayer("§cToo many login attempts");
                }
            }
        }

        return true;
    }
}
