package net.pwing.brewmasters.managers;

import net.pwing.brewmasters.BrewMasters;
import net.pwing.brewmasters.models.ActivePotionEffect;
import net.pwing.brewmasters.models.BrewingRecipe;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages active custom potion effects and command execution
 * Handles drink commands, expire commands, and effect tracking
 */
public class PotionEffectManager {
    
    private final BrewMasters plugin;
    private final Map<UUID, List<ActivePotionEffect>> activeEffects;
    private final File dataFile;
    private int taskId = -1;
    
    public PotionEffectManager(BrewMasters plugin) {
        this.plugin = plugin;
        this.activeEffects = new ConcurrentHashMap<>();
        this.dataFile = new File(plugin.getDataFolder(), "active-effects.yml");
        
        loadActiveEffects();
        startExpirationTask();
    }
    
    /**
     * Handle potion consumption - run drink commands
     */
    public void onPotionDrunk(Player player, BrewingRecipe recipe) {
        List<String> drinkCommands = recipe.getDrinkCommands();
        if (drinkCommands != null && !drinkCommands.isEmpty()) {
            executeCommands(player, recipe, drinkCommands);
        }
        
        // Track active effects with expire commands
        List<String> expireCommands = recipe.getExpireCommands();
        if (expireCommands != null && !expireCommands.isEmpty()) {
            // Get longest effect duration from recipe
            long maxDuration = 0;
            for (PotionEffect effect : recipe.getEffects()) {
                long effectDuration = effect.getDuration() * 50L; // ticks to milliseconds
                if (effectDuration > maxDuration) {
                    maxDuration = effectDuration;
                }
            }
            
            if (maxDuration > 0) {
                String effectName = recipe.getResultName() != null ? recipe.getResultName() : recipe.getId();
                ActivePotionEffect activeEffect = new ActivePotionEffect(
                    player.getUniqueId(),
                    recipe.getId(),
                    effectName,
                    maxDuration,
                    expireCommands
                );
                
                addActiveEffect(player.getUniqueId(), activeEffect);
            }
        }
    }
    
    /**
     * Execute commands with placeholder replacement
     */
    private void executeCommands(Player player, BrewingRecipe recipe, List<String> commands) {
        for (String command : commands) {
            String processedCommand = command
                .replace("{player}", player.getName())
                .replace("{uuid}", player.getUniqueId().toString())
                .replace("{recipe_id}", recipe.getId())
                .replace("{recipe_name}", recipe.getResultName() != null ? recipe.getResultName() : recipe.getId());
            
            // Support both console and player commands
            if (processedCommand.startsWith("[player]")) {
                String playerCommand = processedCommand.substring(8).trim();
                Bukkit.getScheduler().runTask(plugin, () -> 
                    player.performCommand(playerCommand)
                );
            } else if (processedCommand.startsWith("[console]")) {
                String consoleCommand = processedCommand.substring(9).trim();
                Bukkit.getScheduler().runTask(plugin, () -> 
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleCommand)
                );
            } else {
                // Default to console command
                Bukkit.getScheduler().runTask(plugin, () -> 
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand)
                );
            }
        }
    }
    
    /**
     * Add active effect for tracking
     */
    private void addActiveEffect(UUID playerId, ActivePotionEffect effect) {
        activeEffects.computeIfAbsent(playerId, k -> new ArrayList<>()).add(effect);
        saveActiveEffects();
    }
    
    /**
     * Start expiration checking task
     */
    private void startExpirationTask() {
        // Check every second for expired effects
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Map.Entry<UUID, List<ActivePotionEffect>> entry : activeEffects.entrySet()) {
                UUID playerId = entry.getKey();
                List<ActivePotionEffect> effects = entry.getValue();
                Player player = Bukkit.getPlayer(playerId);
                
                Iterator<ActivePotionEffect> iterator = effects.iterator();
                while (iterator.hasNext()) {
                    ActivePotionEffect effect = iterator.next();
                    
                    if (effect.isExpired() && !effect.hasExpired()) {
                        effect.markExpired();
                        
                        // Run expire commands if player is online
                        if (player != null && player.isOnline()) {
                            BrewingRecipe recipe = plugin.getRecipeManager().getRecipe(effect.getRecipeId());
                            if (recipe != null) {
                                executeCommands(player, recipe, effect.getExpireCommands());
                            }
                        }
                        
                        iterator.remove();
                    }
                }
            }
            
            saveActiveEffects();
        }, 20L, 20L); // Run every second
    }
    
    /**
     * Handle player login - run expired commands that were missed
     */
    public void onPlayerLogin(Player player) {
        UUID playerId = player.getUniqueId();
        List<ActivePotionEffect> effects = activeEffects.get(playerId);
        
        if (effects != null) {
            Iterator<ActivePotionEffect> iterator = effects.iterator();
            while (iterator.hasNext()) {
                ActivePotionEffect effect = iterator.next();
                
                // Check if effect expired while player was offline
                if (effect.isExpired() && !effect.hasExpired()) {
                    effect.markExpired();
                    
                    BrewingRecipe recipe = plugin.getRecipeManager().getRecipe(effect.getRecipeId());
                    if (recipe != null) {
                        executeCommands(player, recipe, effect.getExpireCommands());
                    }
                    
                    iterator.remove();
                }
            }
            
            saveActiveEffects();
        }
    }
    
    /**
     * Get active effects for a player
     */
    public List<ActivePotionEffect> getActiveEffects(UUID playerId) {
        return new ArrayList<>(activeEffects.getOrDefault(playerId, new ArrayList<>()));
    }
    
    /**
     * Get time remaining for a specific effect
     */
    public int getTimeRemaining(UUID playerId, String recipeId) {
        List<ActivePotionEffect> effects = activeEffects.get(playerId);
        if (effects != null) {
            for (ActivePotionEffect effect : effects) {
                if (effect.getRecipeId().equals(recipeId)) {
                    return effect.getTimeRemainingSeconds();
                }
            }
        }
        return 0;
    }
    
    /**
     * Get formatted time remaining for a specific effect
     */
    public String getFormattedTimeRemaining(UUID playerId, String recipeId) {
        List<ActivePotionEffect> effects = activeEffects.get(playerId);
        if (effects != null) {
            for (ActivePotionEffect effect : effects) {
                if (effect.getRecipeId().equals(recipeId)) {
                    return effect.getFormattedTimeRemaining();
                }
            }
        }
        return "0:00";
    }
    
    /**
     * Load active effects from file
     */
    private void loadActiveEffects() {
        if (!dataFile.exists()) {
            return;
        }
        
        try {
            FileConfiguration config = YamlConfiguration.loadConfiguration(dataFile);
            ConfigurationSection section = config.getConfigurationSection("active-effects");
            
            if (section != null) {
                for (String playerIdStr : section.getKeys(false)) {
                    try {
                        UUID playerId = UUID.fromString(playerIdStr);
                        ConfigurationSection playerSection = section.getConfigurationSection(playerIdStr);
                        
                        if (playerSection != null) {
                            List<ActivePotionEffect> effects = new ArrayList<>();
                            
                            for (String effectKey : playerSection.getKeys(false)) {
                                ConfigurationSection effectSection = playerSection.getConfigurationSection(effectKey);
                                if (effectSection != null) {
                                    ActivePotionEffect effect = ActivePotionEffect.fromConfig(playerId, effectSection);
                                    effects.add(effect);
                                }
                            }
                            
                            if (!effects.isEmpty()) {
                                activeEffects.put(playerId, effects);
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid UUID in active effects: " + playerIdStr);
                    }
                }
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load active effects: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Save active effects to file
     */
    private void saveActiveEffects() {
        try {
            FileConfiguration config = new YamlConfiguration();
            ConfigurationSection section = config.createSection("active-effects");
            
            for (Map.Entry<UUID, List<ActivePotionEffect>> entry : activeEffects.entrySet()) {
                ConfigurationSection playerSection = section.createSection(entry.getKey().toString());
                List<ActivePotionEffect> effects = entry.getValue();
                
                for (int i = 0; i < effects.size(); i++) {
                    ConfigurationSection effectSection = playerSection.createSection("effect-" + i);
                    effects.get(i).saveToConfig(effectSection);
                }
            }
            
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save active effects: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Shutdown - save all data and cancel task
     */
    public void shutdown() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        saveActiveEffects();
    }
}
