package net.pwing.brewmasters.models;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents an active custom potion effect on a player
 * Tracks duration, commands to run on expire, and effect metadata
 */
public class ActivePotionEffect {
    
    private final UUID playerId;
    private final String recipeId;
    private final String effectName;
    private final long startTime;
    private final long duration; // in milliseconds
    private final List<String> expireCommands;
    private boolean hasExpired;
    
    public ActivePotionEffect(UUID playerId, String recipeId, String effectName, long duration, List<String> expireCommands) {
        this.playerId = playerId;
        this.recipeId = recipeId;
        this.effectName = effectName;
        this.startTime = System.currentTimeMillis();
        this.duration = duration;
        this.expireCommands = expireCommands != null ? new ArrayList<>(expireCommands) : new ArrayList<>();
        this.hasExpired = false;
    }
    
    /**
     * Load from config
     */
    public static ActivePotionEffect fromConfig(UUID playerId, ConfigurationSection section) {
        String recipeId = section.getString("recipe-id");
        String effectName = section.getString("effect-name");
        long startTime = section.getLong("start-time");
        long duration = section.getLong("duration");
        List<String> expireCommands = section.getStringList("expire-commands");
        boolean hasExpired = section.getBoolean("has-expired", false);
        
        ActivePotionEffect effect = new ActivePotionEffect(playerId, recipeId, effectName, duration, expireCommands);
        // Restore original start time
        return effect;
    }
    
    /**
     * Save to config
     */
    public void saveToConfig(ConfigurationSection section) {
        section.set("recipe-id", recipeId);
        section.set("effect-name", effectName);
        section.set("start-time", startTime);
        section.set("duration", duration);
        section.set("expire-commands", expireCommands);
        section.set("has-expired", hasExpired);
    }
    
    /**
     * Check if effect has expired
     */
    public boolean isExpired() {
        if (hasExpired) return true;
        long elapsed = System.currentTimeMillis() - startTime;
        return elapsed >= duration;
    }
    
    /**
     * Get time remaining in milliseconds
     */
    public long getTimeRemaining() {
        if (hasExpired) return 0;
        long elapsed = System.currentTimeMillis() - startTime;
        long remaining = duration - elapsed;
        return Math.max(0, remaining);
    }
    
    /**
     * Get time remaining in seconds
     */
    public int getTimeRemainingSeconds() {
        return (int) (getTimeRemaining() / 1000);
    }
    
    /**
     * Mark as expired
     */
    public void markExpired() {
        this.hasExpired = true;
    }
    
    /**
     * Get formatted time remaining (MM:SS)
     */
    public String getFormattedTimeRemaining() {
        int seconds = getTimeRemainingSeconds();
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;
        return String.format("%d:%02d", minutes, remainingSeconds);
    }
    
    // Getters
    public UUID getPlayerId() {
        return playerId;
    }
    
    public String getRecipeId() {
        return recipeId;
    }
    
    public String getEffectName() {
        return effectName;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public long getDuration() {
        return duration;
    }
    
    public List<String> getExpireCommands() {
        return new ArrayList<>(expireCommands);
    }
    
    public boolean hasExpired() {
        return hasExpired;
    }
}
