package net.pwing.brewmasters.managers;

import net.pwing.brewmasters.BrewMasters;
import net.pwing.brewmasters.models.BrewingChain;
import net.pwing.brewmasters.models.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Manages multi-step brewing chains
 */
public class BrewingChainManager {

    private final BrewMasters plugin;
    private final Map<String, BrewingChain> chains;
    private final Map<String, Set<String>> recipeToChains; // Maps recipe IDs to chain IDs
    private boolean chainsEnabled;

    public BrewingChainManager(BrewMasters plugin) {
        this.plugin = plugin;
        this.chains = new HashMap<>();
        this.recipeToChains = new HashMap<>();
        loadConfiguration();
    }

    /**
     * Load brewing chains from configuration
     */
    public void loadConfiguration() {
        chains.clear();
        recipeToChains.clear();

        ConfigurationSection chainsSection = plugin.getConfig().getConfigurationSection("brewing-chains");
        if (chainsSection == null) {
            chainsEnabled = false;
            return;
        }

        chainsEnabled = chainsSection.getBoolean("enabled", false);
        if (!chainsEnabled) {
            return;
        }

        ConfigurationSection chainsList = chainsSection.getConfigurationSection("chains");
        if (chainsList == null) {
            return;
        }

        for (String chainId : chainsList.getKeys(false)) {
            try {
                ConfigurationSection chainSection = chainsList.getConfigurationSection(chainId);
                BrewingChain chain = BrewingChain.fromConfig(chainId, chainSection);
                
                if (chain != null) {
                    chains.put(chainId, chain);
                    
                    // Build recipe to chains mapping
                    for (BrewingChain.ChainStep step : chain.getSteps()) {
                        recipeToChains.computeIfAbsent(step.getRecipeId(), k -> new HashSet<>()).add(chainId);
                    }
                    
                    plugin.getLogger().info("Loaded brewing chain: " + chainId);
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load brewing chain '" + chainId + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Check if brewing chains are enabled
     */
    public boolean isChainsEnabled() {
        return chainsEnabled;
    }

    /**
     * Get all brewing chains
     */
    public Collection<BrewingChain> getAllChains() {
        return chains.values();
    }

    /**
     * Get a specific brewing chain
     */
    public BrewingChain getChain(String chainId) {
        return chains.get(chainId);
    }

    /**
     * Get chains that contain a specific recipe
     */
    public Set<String> getChainsForRecipe(String recipeId) {
        return recipeToChains.getOrDefault(recipeId, new HashSet<>());
    }

    /**
     * Handle when a player brews a recipe (check for chain progress)
     */
    public void onRecipeBrewed(Player player, String recipeId) {
        if (!chainsEnabled) {
            return;
        }

        Set<String> affectedChains = getChainsForRecipe(recipeId);
        if (affectedChains.isEmpty()) {
            return;
        }

        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        
        for (String chainId : affectedChains) {
            BrewingChain chain = chains.get(chainId);
            if (chain == null) {
                continue;
            }

            // Check if this recipe is the next step in the chain
            List<String> completedRecipes = getCompletedChainRecipes(player, chainId);
            BrewingChain.ChainStep nextStep = chain.getNextStep(completedRecipes);
            
            if (nextStep != null && nextStep.getRecipeId().equals(recipeId)) {
                // Player completed the next step
                markChainStepCompleted(player, chainId, recipeId);
                
                // Give step reward if any
                if (nextStep.getStepReward() != null) {
                    giveChainReward(player, nextStep.getStepReward());
                }
                
                // Check if chain is now completed
                if (chain.isCompleted(getCompletedChainRecipes(player, chainId))) {
                    onChainCompleted(player, chain);
                }
                
                // Notify player of progress
                notifyChainProgress(player, chain, recipeId);
            }
        }
    }

    /**
     * Get completed recipes for a specific chain
     */
    public List<String> getCompletedChainRecipes(Player player, String chainId) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        return playerData.getCompletedChainRecipes(chainId);
    }

    /**
     * Mark a chain step as completed
     */
    private void markChainStepCompleted(Player player, String chainId, String recipeId) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        playerData.completeChainStep(chainId, recipeId);
    }

    /**
     * Handle chain completion
     */
    private void onChainCompleted(Player player, BrewingChain chain) {
        // Give completion reward
        if (chain.getCompletionReward() != null) {
            giveChainReward(player, chain.getCompletionReward());
        }

        // Mark chain as completed
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        playerData.completeChain(chain.getId());

        // Notify player
        player.sendMessage(ChatColor.GOLD + "🎉 Chain Completed: " + ChatColor.YELLOW + chain.getName());
        if (chain.getDescription() != null) {
            player.sendMessage(ChatColor.GRAY + chain.getDescription());
        }

        // Trigger achievement if applicable
        plugin.getAchievementManager().onChainCompleted(player, chain.getId());
    }

    /**
     * Give a chain reward to a player
     */
    private void giveChainReward(Player player, BrewingChain.ChainReward reward) {
        if (reward.getExperience() > 0) {
            player.giveExp(reward.getExperience());
        }

        for (String command : reward.getCommands()) {
            String processedCommand = command.replace("{player}", player.getName());
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), processedCommand);
        }

        if (reward.getMessage() != null && !reward.getMessage().isEmpty()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', reward.getMessage()));
        }
    }

    /**
     * Notify player of chain progress
     */
    private void notifyChainProgress(Player player, BrewingChain chain, String completedRecipe) {
        List<String> completed = getCompletedChainRecipes(player, chain.getId());
        double progress = chain.getProgress(completed);
        int completedSteps = completed.size();
        int totalSteps = chain.getSteps().size();

        player.sendMessage(ChatColor.GREEN + "Chain Progress: " + ChatColor.YELLOW + chain.getName());
        player.sendMessage(ChatColor.GRAY + "Completed: " + completedSteps + "/" + totalSteps + 
                          " (" + String.format("%.1f%%", progress * 100) + ")");

        // Show next step if not completed
        if (progress < 1.0) {
            BrewingChain.ChainStep nextStep = chain.getNextStep(completed);
            if (nextStep != null) {
                String recipeName = getRecipeName(nextStep.getRecipeId());
                player.sendMessage(ChatColor.AQUA + "Next: " + ChatColor.WHITE + recipeName);
                if (nextStep.getDescription() != null) {
                    player.sendMessage(ChatColor.GRAY + "  " + nextStep.getDescription());
                }
            }
        }
    }

    /**
     * Get display name for a recipe
     */
    private String getRecipeName(String recipeId) {
        var recipe = plugin.getRecipeManager().getRecipe(recipeId);
        if (recipe != null && recipe.getResultName() != null) {
            return ChatColor.stripColor(recipe.getResultName());
        }
        return recipeId;
    }

    /**
     * Get chain progress for a player
     */
    public double getChainProgress(Player player, String chainId) {
        BrewingChain chain = chains.get(chainId);
        if (chain == null) {
            return 0.0;
        }

        List<String> completed = getCompletedChainRecipes(player, chainId);
        return chain.getProgress(completed);
    }

    /**
     * Check if a player has completed a chain
     */
    public boolean hasCompletedChain(Player player, String chainId) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        return playerData.hasCompletedChain(chainId);
    }

    /**
     * Get all chains a player has completed
     */
    public Set<String> getCompletedChains(Player player) {
        PlayerData playerData = plugin.getPlayerDataManager().getPlayerData(player);
        return playerData.getCompletedChains();
    }

    /**
     * Get chains available to a player (has discovered at least one recipe in the chain)
     */
    public List<BrewingChain> getAvailableChains(Player player) {
        List<BrewingChain> available = new ArrayList<>();
        Set<String> discoveredRecipes = plugin.getPlayerDataManager().getDiscoveredRecipes(player);

        for (BrewingChain chain : chains.values()) {
            // Check if player has discovered any recipe in this chain
            boolean hasAnyRecipe = chain.getSteps().stream()
                    .anyMatch(step -> discoveredRecipes.contains(step.getRecipeId()));
            
            if (hasAnyRecipe) {
                available.add(chain);
            }
        }

        return available;
    }
}
