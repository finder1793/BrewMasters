package net.pwing.brewmasters.integrations;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.pwing.brewmasters.BrewMasters;
import net.pwing.brewmasters.models.PlayerData;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

/**
 * PlaceholderAPI expansion for BrewMasters
 * Provides comprehensive brewing statistics placeholders
 */
public class BrewMastersExpansion extends PlaceholderExpansion {

    private final BrewMasters plugin;

    public BrewMastersExpansion(BrewMasters plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "brewmasters";
    }

    @Override
    public @NotNull String getAuthor() {
        return String.join(", ", plugin.getPluginMeta().getAuthors());
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // Required for PlaceholderAPI to keep the expansion loaded
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player.getUniqueId());
        
        // Total statistics
        switch (params.toLowerCase()) {
            case "total_brewed":
            case "potions_brewed":
                return String.valueOf(data.getTotalPotionsBrewed());
                
            case "recipes_discovered":
            case "discovered":
                return String.valueOf(data.getTotalRecipesDiscovered());
                
            case "total_recipes":
                return String.valueOf(plugin.getRecipeManager().getRecipeCount());
                
            case "achievements":
            case "achievements_unlocked":
                return String.valueOf(data.getTotalAchievements());
                
            case "total_achievements":
                return String.valueOf(plugin.getAchievementManager().getAllAchievements().size());
                
            case "chains_completed":
                return String.valueOf(data.getTotalChainsCompleted());
                
            case "total_chains":
                return String.valueOf(plugin.getBrewingChainManager().getAllChains().size());
                
            case "rank":
            case "brewing_rank":
                return data.getBrewingRank();
                
            // Percentages
            case "discovery_percent":
            case "discovery_percentage":
                int totalRecipes = plugin.getRecipeManager().getRecipeCount();
                return String.format("%.1f", data.getDiscoveryPercentage(totalRecipes));
                
            case "achievement_percent":
            case "achievement_percentage":
                int totalAchievements = plugin.getAchievementManager().getAllAchievements().size();
                return String.format("%.1f", data.getAchievementPercentage(totalAchievements));
                
            // Discovery status check (returns "true" or "false")
            case "discovery_enabled":
                return String.valueOf(plugin.getDiscoveryManager().isDiscoveryEnabled());
                
            case "achievements_enabled":
                return String.valueOf(plugin.getAchievementManager().isAchievementsEnabled());
        }
        
        // Recipe-specific brewing count: recipe_<recipe_id>
        if (params.startsWith("recipe_")) {
            String recipeId = params.substring(7);
            return String.valueOf(data.getRecipeBrewCount(recipeId));
        }
        
        // Check if recipe is discovered: discovered_<recipe_id>
        if (params.startsWith("discovered_")) {
            String recipeId = params.substring(11);
            return String.valueOf(data.hasDiscoveredRecipe(recipeId));
        }
        
        // Check if achievement is unlocked: achievement_<achievement_id>
        if (params.startsWith("achievement_")) {
            String achievementId = params.substring(12);
            return String.valueOf(data.hasAchievement(achievementId));
        }
        
        // Check if chain is completed: chain_<chain_id>
        if (params.startsWith("chain_")) {
            String chainId = params.substring(6);
            return String.valueOf(data.hasCompletedChain(chainId));
        }
        
        // Custom stat: stat_<stat_name>
        if (params.startsWith("stat_")) {
            String statName = params.substring(5);
            return String.valueOf(data.getStat(statName));
        }
        
        // Effect time remaining (in seconds): effect_time_<recipe_id>
        if (params.startsWith("effect_time_")) {
            String recipeId = params.substring(12);
            return String.valueOf(plugin.getPotionEffectManager().getTimeRemaining(player.getUniqueId(), recipeId));
        }
        
        // Effect time remaining (formatted MM:SS): effect_time_formatted_<recipe_id>
        if (params.startsWith("effect_time_formatted_")) {
            String recipeId = params.substring(22);
            return plugin.getPotionEffectManager().getFormattedTimeRemaining(player.getUniqueId(), recipeId);
        }
        
        // Check if player has active effect: effect_active_<recipe_id>
        if (params.startsWith("effect_active_")) {
            String recipeId = params.substring(14);
            int timeRemaining = plugin.getPotionEffectManager().getTimeRemaining(player.getUniqueId(), recipeId);
            return String.valueOf(timeRemaining > 0);
        }
        
        return null; // Placeholder not found
    }
}
