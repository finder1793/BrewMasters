package net.pwing.brewmasters.managers;

import net.pwing.brewmasters.BrewMasters;
import net.pwing.brewmasters.models.BrewingRecipe;
import net.pwing.brewmasters.models.DiscoveryMethod;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

/**
 * Manages recipe discovery mechanics and notifications
 */
public class DiscoveryManager {

    private final BrewMasters plugin;
    private final Map<String, DiscoveryMethod> discoveryMethods;
    private boolean discoveryEnabled;
    private boolean showDiscoveryNotifications;

    public DiscoveryManager(BrewMasters plugin) {
        this.plugin = plugin;
        this.discoveryMethods = new HashMap<>();
        loadDiscoveryConfig();
    }

    /**
     * Load discovery configuration from config.yml
     */
    private void loadDiscoveryConfig() {
        ConfigurationSection discoverySection = plugin.getConfig().getConfigurationSection("discovery");

        if (discoverySection == null) {
            // Discovery system disabled
            discoveryEnabled = false;
            return;
        }

        discoveryEnabled = discoverySection.getBoolean("enabled", true);
        showDiscoveryNotifications = discoverySection.getBoolean("show-notifications", true);

        // Load discovery methods for each recipe
        ConfigurationSection methodsSection = discoverySection.getConfigurationSection("methods");
        if (methodsSection != null) {
            for (String recipeId : methodsSection.getKeys(false)) {
                ConfigurationSection recipeSection = methodsSection.getConfigurationSection(recipeId);
                if (recipeSection != null) {
                    DiscoveryMethod method = loadDiscoveryMethod(recipeSection);
                    if (method != null) {
                        discoveryMethods.put(recipeId, method);
                    }
                }
            }
        }
    }

    /**
     * Load a discovery method from configuration
     */
    private DiscoveryMethod loadDiscoveryMethod(ConfigurationSection section) {
        String type = section.getString("type", "AUTOMATIC");

        try {
            DiscoveryMethod.Type methodType = DiscoveryMethod.Type.valueOf(type.toUpperCase());
            DiscoveryMethod.Builder builder = new DiscoveryMethod.Builder(methodType);

            // Load type-specific parameters
            switch (methodType) {
                case BIOME_VISIT:
                    List<String> biomes = section.getStringList("biomes");
                    builder.biomes(biomes);
                    break;

                case ITEM_CRAFT:
                case ITEM_OBTAIN:
                    String material = section.getString("material");
                    int amount = section.getInt("amount", 1);
                    builder.material(material).amount(amount);
                    break;

                case KILL_MOB:
                    String mobType = section.getString("mob-type");
                    int killCount = section.getInt("count", 1);
                    builder.mobType(mobType).amount(killCount);
                    break;

                case LEVEL_REACH:
                    int level = section.getInt("level", 1);
                    builder.amount(level);
                    break;

                case PERMISSION:
                    String permission = section.getString("permission");
                    builder.permission(permission);
                    break;

                case RECIPE_BREW:
                    List<String> requiredRecipes = section.getStringList("required-recipes");
                    int brewCount = section.getInt("count", 1);
                    builder.requiredRecipes(requiredRecipes).amount(brewCount);
                    break;
            }

            return builder.build();

        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid discovery method type: " + type);
            return null;
        }
    }

    /**
     * Check if discovery system is enabled
     */
    public boolean isDiscoveryEnabled() {
        return discoveryEnabled;
    }

    /**
     * Check if a player can access a recipe (either discovered or discovery
     * disabled)
     */
    public boolean canAccessRecipe(Player player, String recipeId) {
        if (!discoveryEnabled) {
            return true; // Discovery disabled, all recipes available
        }

        return plugin.getPlayerDataManager().hasDiscoveredRecipe(player, recipeId);
    }

    /**
     * Attempt to discover a recipe for a player
     */
    public boolean tryDiscoverRecipe(Player player, String recipeId) {
        if (!discoveryEnabled) {
            return true; // Discovery disabled
        }

        // Check if already discovered
        if (plugin.getPlayerDataManager().hasDiscoveredRecipe(player, recipeId)) {
            return true;
        }

        DiscoveryMethod method = discoveryMethods.get(recipeId);
        if (method == null) {
            // No discovery method defined, auto-discover
            return discoverRecipe(player, recipeId);
        }

        // Check if discovery conditions are met
        if (checkDiscoveryConditions(player, method)) {
            return discoverRecipe(player, recipeId);
        }

        return false;
    }

    /**
     * Force discover a recipe for a player
     */
    public boolean discoverRecipe(Player player, String recipeId) {
        if (plugin.getPlayerDataManager().hasDiscoveredRecipe(player, recipeId)) {
            return false; // Already discovered
        }

        plugin.getPlayerDataManager().discoverRecipe(player, recipeId);

        // Trigger achievement check
        plugin.getAchievementManager().onRecipeDiscovered(player, recipeId);

        if (showDiscoveryNotifications) {
            sendDiscoveryNotification(player, recipeId);
        }

        return true;
    }

    /**
     * Check if discovery conditions are met
     */
    private boolean checkDiscoveryConditions(Player player, DiscoveryMethod method) {
        // This is a simplified check - in a full implementation, you'd track
        // player actions and check against the method requirements
        switch (method.getType()) {
            case AUTOMATIC:
                return true;

            case PERMISSION:
                return player.hasPermission(method.getPermission());

            case LEVEL_REACH:
                return player.getLevel() >= method.getAmount();

            // Other types would require tracking player actions
            default:
                return false;
        }
    }

    /**
     * Send discovery notification to player
     */
    private void sendDiscoveryNotification(Player player, String recipeId) {
        BrewingRecipe recipe = plugin.getRecipeManager().getRecipe(recipeId);
        if (recipe == null) {
            return;
        }

        String recipeName = recipe.getResultName() != null ? ChatColor.stripColor(recipe.getResultName())
                : "Custom Recipe";

        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "✨ " + ChatColor.YELLOW + "Recipe Discovered!" + ChatColor.GOLD + " ✨");
        player.sendMessage(ChatColor.WHITE + "You have discovered: " + ChatColor.AQUA + recipeName);
        player.sendMessage(ChatColor.GRAY + "Use " + ChatColor.GREEN + "/brewmasters recipes" +
                ChatColor.GRAY + " to view your recipes");
        player.sendMessage("");

        // Play discovery sound
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
    }

    /**
     * Get discovery method for a recipe
     */
    public DiscoveryMethod getDiscoveryMethod(String recipeId) {
        return discoveryMethods.get(recipeId);
    }

    /**
     * Get all recipes that can be discovered
     */
    public Set<String> getDiscoverableRecipes() {
        return new HashSet<>(discoveryMethods.keySet());
    }
}
