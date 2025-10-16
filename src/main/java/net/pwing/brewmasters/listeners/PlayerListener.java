package net.pwing.brewmasters.listeners;

import net.pwing.brewmasters.BrewMasters;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player join/leave events for data management
 */
public class PlayerListener implements Listener {

    private final BrewMasters plugin;

    public PlayerListener(BrewMasters plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayerDataManager().getPlayerData(player).updateLastSeen();
        
        // Handle any expired potion effects that occurred while player was offline
        plugin.getPotionEffectManager().onPlayerLogin(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Save and unload player data when they leave
        plugin.getPlayerDataManager().unloadPlayerData(event.getPlayer());

        // Clean up GUI references
        plugin.getGUIListener().cleanupPlayer(event.getPlayer().getUniqueId());
    }
}
