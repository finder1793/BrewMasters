package net.pwing.brewmasters.models;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

/**
 * Represents player-specific data including discovered recipes, achievements,
 * and statistics
 */
public class PlayerData {

    private final UUID playerId;
    private final Set<String> discoveredRecipes;
    private final Map<String, Integer> brewingStats;
    private final Set<String> achievements;
    private final Map<String, List<String>> chainProgress; // chainId -> completed recipe IDs
    private final Set<String> completedChains;
    private long firstJoined;
    private long lastSeen;

    public PlayerData(UUID playerId) {
        this.playerId = playerId;
        this.discoveredRecipes = new HashSet<>();
        this.brewingStats = new HashMap<>();
        this.achievements = new HashSet<>();
        this.chainProgress = new HashMap<>();
        this.completedChains = new HashSet<>();
        this.firstJoined = System.currentTimeMillis();
        this.lastSeen = System.currentTimeMillis();
    }

    /**
     * Load player data from configuration
     */
    public static PlayerData fromConfig(FileConfiguration config) {
        UUID playerId = UUID.fromString(config.getString("player-id"));
        PlayerData data = new PlayerData(playerId);

        // Load discovered recipes
        List<String> recipes = config.getStringList("discovered-recipes");
        data.discoveredRecipes.addAll(recipes);

        // Load brewing statistics
        ConfigurationSection statsSection = config.getConfigurationSection("brewing-stats");
        if (statsSection != null) {
            for (String key : statsSection.getKeys(false)) {
                data.brewingStats.put(key, statsSection.getInt(key));
            }
        }

        // Load achievements
        List<String> achievementsList = config.getStringList("achievements");
        data.achievements.addAll(achievementsList);

        // Load chain progress
        ConfigurationSection chainSection = config.getConfigurationSection("chain-progress");
        if (chainSection != null) {
            for (String chainId : chainSection.getKeys(false)) {
                List<String> completedRecipes = chainSection.getStringList(chainId);
                data.chainProgress.put(chainId, new ArrayList<>(completedRecipes));
            }
        }

        // Load completed chains
        List<String> completedChainsList = config.getStringList("completed-chains");
        data.completedChains.addAll(completedChainsList);

        // Load timestamps
        data.firstJoined = config.getLong("first-joined", System.currentTimeMillis());
        data.lastSeen = config.getLong("last-seen", System.currentTimeMillis());

        return data;
    }

    /**
     * Save player data to configuration
     */
    public void saveToConfig(FileConfiguration config) {
        config.set("player-id", playerId.toString());
        config.set("discovered-recipes", new ArrayList<>(discoveredRecipes));

        // Save brewing statistics
        ConfigurationSection statsSection = config.createSection("brewing-stats");
        for (Map.Entry<String, Integer> entry : brewingStats.entrySet()) {
            statsSection.set(entry.getKey(), entry.getValue());
        }

        config.set("achievements", new ArrayList<>(achievements));

        // Save chain progress
        if (!chainProgress.isEmpty()) {
            ConfigurationSection chainSection = config.createSection("chain-progress");
            for (Map.Entry<String, List<String>> entry : chainProgress.entrySet()) {
                chainSection.set(entry.getKey(), entry.getValue());
            }
        }

        // Save completed chains
        config.set("completed-chains", new ArrayList<>(completedChains));

        config.set("first-joined", firstJoined);
        config.set("last-seen", lastSeen);
    }

    /**
     * Check if a recipe has been discovered
     */
    public boolean hasDiscoveredRecipe(String recipeId) {
        return discoveredRecipes.contains(recipeId);
    }

    /**
     * Discover a new recipe
     * 
     * @return true if the recipe was newly discovered, false if already known
     */
    public boolean discoverRecipe(String recipeId) {
        return discoveredRecipes.add(recipeId);
    }

    /**
     * Get all discovered recipes
     */
    public Set<String> getDiscoveredRecipes() {
        return new HashSet<>(discoveredRecipes);
    }

    /**
     * Increment a brewing statistic
     */
    public void incrementStat(String statName) {
        brewingStats.put(statName, brewingStats.getOrDefault(statName, 0) + 1);
    }

    /**
     * Get a brewing statistic value
     */
    public int getStat(String statName) {
        return brewingStats.getOrDefault(statName, 0);
    }
    
    /**
     * Set a brewing statistic value
     */
    public void setStat(String statName, int value) {
        brewingStats.put(statName, value);
    }

    /**
     * Get all brewing statistics
     */
    public Map<String, Integer> getBrewingStats() {
        return new HashMap<>(brewingStats);
    }

    /**
     * Check if an achievement has been unlocked
     */
    public boolean hasAchievement(String achievementId) {
        return achievements.contains(achievementId);
    }

    /**
     * Unlock an achievement
     * 
     * @return true if the achievement was newly unlocked, false if already unlocked
     */
    public boolean unlockAchievement(String achievementId) {
        return achievements.add(achievementId);
    }

    /**
     * Complete a step in a brewing chain
     */
    public void completeChainStep(String chainId, String recipeId) {
        chainProgress.computeIfAbsent(chainId, k -> new ArrayList<>()).add(recipeId);
    }

    /**
     * Get completed recipes for a chain
     */
    public List<String> getCompletedChainRecipes(String chainId) {
        return chainProgress.getOrDefault(chainId, new ArrayList<>());
    }

    /**
     * Mark a chain as completed
     */
    public void completeChain(String chainId) {
        completedChains.add(chainId);
    }

    /**
     * Check if a chain is completed
     */
    public boolean hasCompletedChain(String chainId) {
        return completedChains.contains(chainId);
    }

    /**
     * Get all completed chains
     */
    public Set<String> getCompletedChains() {
        return new HashSet<>(completedChains);
    }

    /**
     * Get all unlocked achievements
     */
    public Set<String> getAchievements() {
        return new HashSet<>(achievements);
    }

    /**
     * Update last seen timestamp
     */
    public void updateLastSeen() {
        this.lastSeen = System.currentTimeMillis();
    }

    // Getters
    public UUID getPlayerId() {
        return playerId;
    }

    public long getFirstJoined() {
        return firstJoined;
    }

    public long getLastSeen() {
        return lastSeen;
    }
    
    // ===== Convenience Methods for Common Statistics =====
    
    /**
     * Get total potions brewed
     */
    public int getTotalPotionsBrewed() {
        return getStat("total_brewed");
    }
    
    /**
     * Get total recipes discovered
     */
    public int getTotalRecipesDiscovered() {
        return discoveredRecipes.size();
    }
    
    /**
     * Get total achievements unlocked
     */
    public int getTotalAchievements() {
        return achievements.size();
    }
    
    /**
     * Get total chains completed
     */
    public int getTotalChainsCompleted() {
        return completedChains.size();
    }
    
    /**
     * Get times a specific recipe has been brewed
     */
    public int getRecipeBrewCount(String recipeId) {
        return getStat("recipe_" + recipeId + "_brewed");
    }
    
    /**
     * Get brewing rank based on total potions brewed
     */
    public String getBrewingRank() {
        int total = getTotalPotionsBrewed();
        if (total >= 10000) return "Grandmaster";
        if (total >= 5000) return "Master";
        if (total >= 2500) return "Expert";
        if (total >= 1000) return "Adept";
        if (total >= 500) return "Proficient";
        if (total >= 250) return "Skilled";
        if (total >= 100) return "Apprentice";
        if (total >= 50) return "Novice";
        if (total >= 10) return "Beginner";
        return "Newcomer";
    }
    
    /**
     * Get discovery percentage (0-100)
     */
    public double getDiscoveryPercentage(int totalRecipes) {
        if (totalRecipes == 0) return 0.0;
        return (double) discoveredRecipes.size() / totalRecipes * 100.0;
    }
    
    /**
     * Get achievement completion percentage (0-100)
     */
    public double getAchievementPercentage(int totalAchievements) {
        if (totalAchievements == 0) return 0.0;
        return (double) achievements.size() / totalAchievements * 100.0;
    }
}
