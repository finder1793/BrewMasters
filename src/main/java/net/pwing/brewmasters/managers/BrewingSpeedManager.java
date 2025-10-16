package net.pwing.brewmasters.managers;

import net.pwing.brewmasters.BrewMasters;
import net.pwing.brewmasters.models.BrewingRecipe;
import org.bukkit.Location;
import org.bukkit.block.BrewingStand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Manages custom brewing speeds for brewing stands and recipes
 */
public class BrewingSpeedManager {

    private final BrewMasters plugin;
    private final Map<Location, BrewingSpeedData> brewingStands;
    private boolean speedSystemEnabled;
    private double globalSpeedMultiplier;
    private Map<String, Double> permissionSpeedMultipliers;
    private Map<String, Double> biomeSpeedMultipliers;
    private Map<String, Double> recipeSpeedMultipliers;

    public BrewingSpeedManager(BrewMasters plugin) {
        this.plugin = plugin;
        this.brewingStands = new HashMap<>();
        this.permissionSpeedMultipliers = new HashMap<>();
        this.biomeSpeedMultipliers = new HashMap<>();
        this.recipeSpeedMultipliers = new HashMap<>();
        loadConfiguration();
    }

    /**
     * Load speed configuration from config.yml
     */
    public void loadConfiguration() {
        loadSpeedSettings();
    }
    
    /**
     * Load speed settings (public for reload)
     */
    public void loadSpeedSettings() {
        ConfigurationSection speedSection = plugin.getConfig().getConfigurationSection("brewing-speeds");
        if (speedSection == null) {
            speedSystemEnabled = false;
            return;
        }

        speedSystemEnabled = speedSection.getBoolean("enabled", false);
        globalSpeedMultiplier = speedSection.getDouble("global-multiplier", 1.0);

        // Load permission-based speed multipliers
        ConfigurationSection permSection = speedSection.getConfigurationSection("permission-multipliers");
        if (permSection != null) {
            for (String permission : permSection.getKeys(false)) {
                double multiplier = permSection.getDouble(permission);
                permissionSpeedMultipliers.put(permission, multiplier);
            }
        }

        // Load biome-based speed multipliers
        ConfigurationSection biomeSection = speedSection.getConfigurationSection("biome-multipliers");
        if (biomeSection != null) {
            for (String biome : biomeSection.getKeys(false)) {
                double multiplier = biomeSection.getDouble(biome);
                biomeSpeedMultipliers.put(biome.toUpperCase(), multiplier);
            }
        }

        // Load recipe-specific speed multipliers
        ConfigurationSection recipeSection = speedSection.getConfigurationSection("recipe-multipliers");
        if (recipeSection != null) {
            for (String recipeId : recipeSection.getKeys(false)) {
                double multiplier = recipeSection.getDouble(recipeId);
                recipeSpeedMultipliers.put(recipeId, multiplier);
            }
        }
    }

    /**
     * Check if the speed system is enabled
     */
    public boolean isSpeedSystemEnabled() {
        return speedSystemEnabled;
    }

    /**
     * Calculate the effective brewing time for a recipe
     */
    public int calculateBrewingTime(BrewingRecipe recipe, Player brewer, Location location) {
        if (!speedSystemEnabled) {
            return recipe.getBrewTime();
        }

        double baseTime = recipe.getBrewTime();
        double totalMultiplier = globalSpeedMultiplier;

        // Apply recipe-specific multiplier
        String recipeId = recipe.getId();
        if (recipeSpeedMultipliers.containsKey(recipeId)) {
            totalMultiplier *= recipeSpeedMultipliers.get(recipeId);
        }

        // Apply permission-based multipliers (highest permission wins)
        if (brewer != null) {
            double bestPermissionMultiplier = 1.0;
            for (Map.Entry<String, Double> entry : permissionSpeedMultipliers.entrySet()) {
                if (brewer.hasPermission(entry.getKey())) {
                    bestPermissionMultiplier = Math.min(bestPermissionMultiplier, entry.getValue());
                }
            }
            totalMultiplier *= bestPermissionMultiplier;
        }

        // Apply biome-based multipliers
        if (location != null && location.getWorld() != null) {
            String biomeName = location.getBlock().getBiome().name();
            if (biomeSpeedMultipliers.containsKey(biomeName)) {
                totalMultiplier *= biomeSpeedMultipliers.get(biomeName);
            }
        }

        // Apply brewing stand specific multipliers
        BrewingSpeedData speedData = brewingStands.get(location);
        if (speedData != null) {
            totalMultiplier *= speedData.getSpeedMultiplier();
        }

        // Calculate final time (minimum 1 tick)
        int finalTime = Math.max(1, (int) (baseTime * totalMultiplier));
        return finalTime;
    }

    /**
     * Set custom speed multiplier for a specific brewing stand
     */
    public void setBrewingStandSpeed(Location location, double speedMultiplier, UUID setBy) {
        BrewingSpeedData data = new BrewingSpeedData(speedMultiplier, setBy, System.currentTimeMillis());
        brewingStands.put(location, data);
    }

    /**
     * Remove custom speed for a brewing stand
     */
    public void removeBrewingStandSpeed(Location location) {
        brewingStands.remove(location);
    }

    /**
     * Get speed data for a brewing stand
     */
    public BrewingSpeedData getBrewingStandSpeed(Location location) {
        return brewingStands.get(location);
    }

    /**
     * Get all brewing stands with custom speeds
     */
    public Map<Location, BrewingSpeedData> getAllBrewingStands() {
        return new HashMap<>(brewingStands);
    }

    /**
     * Apply speed to a brewing stand block
     */
    public void applySpeedToBrewingStand(Location location, BrewingRecipe recipe, Player brewer) {
        if (!speedSystemEnabled) {
            return;
        }

        if (!(location.getBlock().getState() instanceof BrewingStand)) {
            return;
        }

        BrewingStand stand = (BrewingStand) location.getBlock().getState();
        int customTime = calculateBrewingTime(recipe, brewer, location);
        
        // Set the brewing time
        stand.setBrewingTime(customTime);
        stand.update();
    }

    /**
     * Get speed multiplier description for display
     */
    public String getSpeedDescription(BrewingRecipe recipe, Player brewer, Location location) {
        if (!speedSystemEnabled) {
            return "Standard speed";
        }

        int customTime = calculateBrewingTime(recipe, brewer, location);
        int baseTime = recipe.getBrewTime();
        
        if (customTime == baseTime) {
            return "Standard speed (" + (baseTime / 20) + "s)";
        } else if (customTime < baseTime) {
            double speedup = (double) baseTime / customTime;
            return String.format("%.1fx faster (%ds)", speedup, customTime / 20);
        } else {
            double slowdown = (double) customTime / baseTime;
            return String.format("%.1fx slower (%ds)", slowdown, customTime / 20);
        }
    }

    /**
     * Clean up old brewing stand data
     */
    public void cleanupOldData() {
        long cutoffTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // 24 hours
        brewingStands.entrySet().removeIf(entry -> entry.getValue().getTimestamp() < cutoffTime);
    }

    /**
     * Data class for brewing stand speed information
     */
    public static class BrewingSpeedData {
        private final double speedMultiplier;
        private final UUID setBy;
        private final long timestamp;

        public BrewingSpeedData(double speedMultiplier, UUID setBy, long timestamp) {
            this.speedMultiplier = speedMultiplier;
            this.setBy = setBy;
            this.timestamp = timestamp;
        }

        public double getSpeedMultiplier() {
            return speedMultiplier;
        }

        public UUID getSetBy() {
            return setBy;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
