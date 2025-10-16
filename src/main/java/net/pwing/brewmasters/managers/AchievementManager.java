package net.pwing.brewmasters.managers;

import net.pwing.brewmasters.BrewMasters;
import net.pwing.brewmasters.models.Achievement;
import net.pwing.brewmasters.models.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Manages brewing achievements and progress tracking
 */
public class AchievementManager {

    private final BrewMasters plugin;
    private final Map<String, Achievement> achievements;
    private boolean achievementsEnabled;
    private boolean showAchievementNotifications;

    public AchievementManager(BrewMasters plugin) {
        this.plugin = plugin;
        this.achievements = new HashMap<>();
        loadAchievements();
    }

    /**
     * Load achievements from configuration
     */
    public void loadAchievements() {
        achievements.clear();

        ConfigurationSection achievementSection = plugin.getConfig().getConfigurationSection("achievements");
        if (achievementSection == null) {
            achievementsEnabled = false;
            return;
        }

        achievementsEnabled = achievementSection.getBoolean("enabled", true);
        showAchievementNotifications = achievementSection.getBoolean("show-notifications", true);

        ConfigurationSection achievementsSection = achievementSection.getConfigurationSection("list");
        if (achievementsSection != null) {
            for (String achievementId : achievementsSection.getKeys(false)) {
                ConfigurationSection section = achievementsSection.getConfigurationSection(achievementId);
                if (section != null) {
                    try {
                        Achievement achievement = Achievement.fromConfig(achievementId, section);
                        achievements.put(achievementId, achievement);
                        plugin.getLogger().info("Loaded achievement: " + achievementId);
                    } catch (Exception e) {
                        plugin.getLogger()
                                .warning("Failed to load achievement '" + achievementId + "': " + e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Check if achievements are enabled
     */
    public boolean isAchievementsEnabled() {
        return achievementsEnabled;
    }

    /**
     * Handle recipe discovery event
     */
    public void onRecipeDiscovered(Player player, String recipeId) {
        if (!achievementsEnabled)
            return;

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        // Check first discovery achievement
        if (data.getDiscoveredRecipes().size() == 1) {
            checkAchievement(player, Achievement.AchievementTrigger.FIRST_DISCOVERY);
        }

        // Check recipes discovered milestones
        checkAchievement(player, Achievement.AchievementTrigger.RECIPES_DISCOVERED);

        // Check master brewer achievement
        int totalRecipes = plugin.getRecipeManager().getRecipeCount();
        if (data.getDiscoveredRecipes().size() >= totalRecipes) {
            checkAchievement(player, Achievement.AchievementTrigger.MASTER_BREWER);
        }

        // Check recipe set discoveries
        checkRecipeSetAchievements(player);
    }

    /**
     * Handle potion brewing event
     */
    public void onPotionBrewed(Player player, String recipeId) {
        if (!achievementsEnabled)
            return;

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        // Increment brewing stats
        data.incrementStat("total_brewed");
        data.incrementStat("recipe_" + recipeId + "_brewed");

        // Check first brew achievement
        if (data.getStat("total_brewed") == 1) {
            checkAchievement(player, Achievement.AchievementTrigger.FIRST_BREW);
        }

        // Check potions brewed milestones
        checkAchievement(player, Achievement.AchievementTrigger.POTIONS_BREWED);

        // Check specific recipe brewing achievements
        checkSpecificRecipeAchievements(player, recipeId);
    }

    /**
     * Handle when a player completes a brewing chain
     */
    public void onChainCompleted(Player player, String chainId) {
        if (!achievementsEnabled)
            return;

        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        // Increment chain completion stats
        data.incrementStat("chains_completed");
        data.incrementStat("chain_" + chainId + "_completed");

        // Check first chain completion achievement
        if (data.getStat("chains_completed") == 1) {
            checkAchievement(player, Achievement.AchievementTrigger.FIRST_CHAIN);
        }

        // Check chain completion milestones
        checkAchievement(player, Achievement.AchievementTrigger.CHAINS_COMPLETED);
    }

    /**
     * Check achievements for a specific trigger
     */
    private void checkAchievement(Player player, Achievement.AchievementTrigger trigger) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        for (Achievement achievement : achievements.values()) {
            if (achievement.getTrigger() != trigger)
                continue;
            if (data.hasAchievement(achievement.getId()))
                continue;

            boolean unlocked = false;

            switch (trigger) {
                case FIRST_DISCOVERY:
                case FIRST_BREW:
                case FIRST_CHAIN:
                case MASTER_BREWER:
                    unlocked = true;
                    break;

                case RECIPES_DISCOVERED:
                    unlocked = data.getDiscoveredRecipes().size() >= achievement.getTargetValue();
                    break;

                case POTIONS_BREWED:
                    unlocked = data.getStat("total_brewed") >= achievement.getTargetValue();
                    break;

                case CHAINS_COMPLETED:
                    unlocked = data.getStat("chains_completed") >= achievement.getTargetValue();
                    break;
            }

            if (unlocked) {
                unlockAchievement(player, achievement);
            }
        }
    }

    /**
     * Check specific recipe brewing achievements
     */
    private void checkSpecificRecipeAchievements(Player player, String recipeId) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        for (Achievement achievement : achievements.values()) {
            if (achievement.getTrigger() != Achievement.AchievementTrigger.SPECIFIC_RECIPE_BREWED)
                continue;
            if (data.hasAchievement(achievement.getId()))
                continue;
            if (!recipeId.equals(achievement.getTargetRecipe()))
                continue;

            int brewCount = data.getStat("recipe_" + recipeId + "_brewed");
            if (brewCount >= achievement.getTargetValue()) {
                unlockAchievement(player, achievement);
            }
        }
    }

    /**
     * Check recipe set achievements
     */
    private void checkRecipeSetAchievements(Player player) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);
        Set<String> discoveredRecipes = data.getDiscoveredRecipes();

        for (Achievement achievement : achievements.values()) {
            if (achievement.getTrigger() != Achievement.AchievementTrigger.RECIPE_SET_DISCOVERED)
                continue;
            if (data.hasAchievement(achievement.getId()))
                continue;

            List<String> targetRecipes = achievement.getTargetRecipes();
            if (targetRecipes != null && discoveredRecipes.containsAll(targetRecipes)) {
                unlockAchievement(player, achievement);
            }
        }
    }

    /**
     * Unlock an achievement for a player
     */
    private void unlockAchievement(Player player, Achievement achievement) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        if (data.unlockAchievement(achievement.getId())) {
            plugin.getPlayerDataManager().savePlayerData(player);

            if (showAchievementNotifications) {
                sendAchievementNotification(player, achievement);
            }

            // Give reward if present
            if (achievement.getReward() != null) {
                achievement.getReward().giveReward(player);
            }
        }
    }

    /**
     * Send achievement notification to player
     */
    private void sendAchievementNotification(Player player, Achievement achievement) {
        player.sendMessage("");
        player.sendMessage(
                ChatColor.GOLD + "🏆 " + ChatColor.YELLOW + "Achievement Unlocked!" + ChatColor.GOLD + " 🏆");
        player.sendMessage(ChatColor.WHITE + achievement.getName());
        player.sendMessage(ChatColor.GRAY + achievement.getDescription());

        if (achievement.getReward() != null) {
            player.sendMessage(ChatColor.GREEN + "You received a reward!");
        }

        player.sendMessage("");

        // Play achievement sound
        player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
    }

    /**
     * Get all achievements
     */
    public Collection<Achievement> getAllAchievements() {
        return achievements.values();
    }

    /**
     * Get achievement by ID
     */
    public Achievement getAchievement(String id) {
        return achievements.get(id);
    }

    /**
     * Get unlocked achievements for a player
     */
    public Set<String> getUnlockedAchievements(Player player) {
        return plugin.getPlayerDataManager().getPlayerData(player).getAchievements();
    }

    /**
     * Get achievement progress for a player
     */
    public int getAchievementProgress(Player player, Achievement achievement) {
        PlayerData data = plugin.getPlayerDataManager().getPlayerData(player);

        switch (achievement.getTrigger()) {
            case RECIPES_DISCOVERED:
                return data.getDiscoveredRecipes().size();
            case POTIONS_BREWED:
                return data.getStat("total_brewed");
            case SPECIFIC_RECIPE_BREWED:
                return data.getStat("recipe_" + achievement.getTargetRecipe() + "_brewed");
            case RECIPE_SET_DISCOVERED:
                List<String> targetRecipes = achievement.getTargetRecipes();
                if (targetRecipes != null) {
                    Set<String> discovered = data.getDiscoveredRecipes();
                    return (int) targetRecipes.stream().filter(discovered::contains).count();
                }
                return 0;
            default:
                return data.hasAchievement(achievement.getId()) ? 1 : 0;
        }
    }
}
