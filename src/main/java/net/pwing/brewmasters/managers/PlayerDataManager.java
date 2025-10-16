package net.pwing.brewmasters.managers;

import net.pwing.brewmasters.BrewMasters;
import net.pwing.brewmasters.models.PlayerData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages player data including discovered recipes, achievements, and statistics
 */
public class PlayerDataManager {

    private final BrewMasters plugin;
    private final File playerDataFolder;
    private final Map<UUID, PlayerData> playerDataCache;

    public PlayerDataManager(BrewMasters plugin) {
        this.plugin = plugin;
        this.playerDataFolder = new File(plugin.getDataFolder(), "playerdata");
        this.playerDataCache = new ConcurrentHashMap<>();
        
        // Create playerdata folder if it doesn't exist
        if (!playerDataFolder.exists()) {
            playerDataFolder.mkdirs();
        }
    }

    /**
     * Get player data for a player, loading from file if not cached
     */
    public PlayerData getPlayerData(Player player) {
        return getPlayerData(player.getUniqueId());
    }

    /**
     * Get player data by UUID, loading from file if not cached
     */
    public PlayerData getPlayerData(UUID playerId) {
        PlayerData data = playerDataCache.get(playerId);
        if (data == null) {
            data = loadPlayerData(playerId);
            playerDataCache.put(playerId, data);
        }
        return data;
    }

    /**
     * Load player data from file
     */
    private PlayerData loadPlayerData(UUID playerId) {
        File playerFile = new File(playerDataFolder, playerId.toString() + ".yml");
        
        if (!playerFile.exists()) {
            // Create new player data
            return new PlayerData(playerId);
        }

        try {
            FileConfiguration config = YamlConfiguration.loadConfiguration(playerFile);
            return PlayerData.fromConfig(config);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load player data for " + playerId + ": " + e.getMessage());
            return new PlayerData(playerId);
        }
    }

    /**
     * Save player data to file
     */
    public void savePlayerData(UUID playerId) {
        PlayerData data = playerDataCache.get(playerId);
        if (data == null) {
            return;
        }

        File playerFile = new File(playerDataFolder, playerId.toString() + ".yml");
        
        try {
            FileConfiguration config = new YamlConfiguration();
            data.saveToConfig(config);
            config.save(playerFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save player data for " + playerId + ": " + e.getMessage());
        }
    }

    /**
     * Save player data for a player
     */
    public void savePlayerData(Player player) {
        savePlayerData(player.getUniqueId());
    }

    /**
     * Save all cached player data
     */
    public void saveAllPlayerData() {
        for (UUID playerId : playerDataCache.keySet()) {
            savePlayerData(playerId);
        }
    }

    /**
     * Remove player from cache (call when player leaves)
     */
    public void unloadPlayerData(UUID playerId) {
        savePlayerData(playerId);
        playerDataCache.remove(playerId);
    }

    /**
     * Remove player from cache (call when player leaves)
     */
    public void unloadPlayerData(Player player) {
        unloadPlayerData(player.getUniqueId());
    }

    /**
     * Check if a player has discovered a recipe
     */
    public boolean hasDiscoveredRecipe(Player player, String recipeId) {
        return getPlayerData(player).hasDiscoveredRecipe(recipeId);
    }

    /**
     * Mark a recipe as discovered for a player
     */
    public void discoverRecipe(Player player, String recipeId) {
        PlayerData data = getPlayerData(player);
        if (data.discoverRecipe(recipeId)) {
            // Recipe was newly discovered
            savePlayerData(player);
        }
    }

    /**
     * Get the number of recipes discovered by a player
     */
    public int getDiscoveredRecipeCount(Player player) {
        return getPlayerData(player).getDiscoveredRecipes().size();
    }

    /**
     * Get all discovered recipes for a player
     */
    public java.util.Set<String> getDiscoveredRecipes(Player player) {
        return getPlayerData(player).getDiscoveredRecipes();
    }
}
