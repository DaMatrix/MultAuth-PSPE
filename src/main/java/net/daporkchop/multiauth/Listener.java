package net.daporkchop.multiauth;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

import java.util.HashMap;

public class Listener implements org.bukkit.event.Listener {
    public static HashMap<String, Location> playerLocs = new HashMap<>();

    public static Location loginLocation;

    @EventHandler
    public void onJoin(PlayerJoinEvent event)   {
        playerLocs.put(event.getPlayer().getName(), event.getPlayer().getLocation());
        event.getPlayer().teleport(loginLocation);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event)   {
        MultiAuth.loggedInPlayers.remove(event.getPlayer());
        MultiAuth.loggedInPlayersName.remove(event.getPlayer().getName());
        MultiAuth.loginAttempts.remove(event.getPlayer().getName());
        if (!MultiAuth.isLoggedIn(event.getPlayer())) {
            try {
                event.getPlayer().teleport(playerLocs.remove(event.getPlayer().getName()));
            } catch (NullPointerException e)    {
                //player isn't registered
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMove(PlayerMoveEvent event)   {
        if (!MultiAuth.isLoggedIn(event.getPlayer()))   {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDropItem(PlayerDropItemEvent event)   {
        if (!MultiAuth.isLoggedIn(event.getPlayer()))   {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFillBucket(PlayerBucketFillEvent event)   {
        if (!MultiAuth.isLoggedIn(event.getPlayer()))   {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEmptyBucket(PlayerBucketEmptyEvent event)   {
        if (!MultiAuth.isLoggedIn(event.getPlayer()))   {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBookEdit(PlayerEditBookEvent event)   {
        if (!MultiAuth.isLoggedIn(event.getPlayer()))   {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFish(PlayerFishEvent event)   {
        if (!MultiAuth.isLoggedIn(event.getPlayer()))   {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event)   {
        if (!MultiAuth.isLoggedIn(event.getPlayer()))   {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onConsume(PlayerItemConsumeEvent event)   {
        if (!MultiAuth.isLoggedIn(event.getPlayer()))   {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDamage(PlayerItemDamageEvent event)   {
        if (!MultiAuth.isLoggedIn(event.getPlayer()))   {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSwap(PlayerSwapHandItemsEvent event)   {
        if (!MultiAuth.isLoggedIn(event.getPlayer()))   {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event)   {
        if (!MultiAuth.isLoggedIn(event.getPlayer()))   {
            event.setCancelled(true);
        }

        event.setFormat("%1$s§7:§r %2$s");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event)   {
        if (event.getEntityType() == EntityType.PLAYER) {
            if (!MultiAuth.isLoggedIn((Player) event.getEntity()))   {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommand(PlayerCommandPreprocessEvent event)   {
        if (!MultiAuth.isLoggedIn(event.getPlayer())) {
            if (!(event.getMessage().startsWith("login") || event.getMessage().startsWith("/login") || event.getMessage().startsWith("register") || event.getMessage().startsWith("/register"))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAttack(EntityDamageByEntityEvent entityEvent) {
        if (entityEvent.getDamager() instanceof Player) {
            if (!MultiAuth.isLoggedIn(((Player) entityEvent.getDamager()))) {
                entityEvent.setCancelled(true);
            }
        }
    }
}
