package net.pwing.brewmasters.utils;

import net.pwing.brewmasters.ingredients.CrucibleIngredient;
import net.pwing.brewmasters.ingredients.MythicMobsIngredient;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Utility class for checking plugin integrations
 */
public class IntegrationUtils {
    
    private static Boolean mythicMobsAvailable = null;
    private static Boolean crucibleAvailable = null;
    
    /**
     * Check if MythicMobs is available and enabled
     * @return true if MythicMobs is available
     */
    public static boolean isMythicMobsAvailable() {
        if (mythicMobsAvailable == null) {
            mythicMobsAvailable = checkMythicMobs();
        }
        return mythicMobsAvailable;
    }
    
    /**
     * Check if MythicCrucible is available and enabled
     * @return true if MythicCrucible is available
     */
    public static boolean isCrucibleAvailable() {
        if (crucibleAvailable == null) {
            crucibleAvailable = checkCrucible();
        }
        return crucibleAvailable;
    }
    
    /**
     * Force refresh the integration status
     */
    public static void refreshIntegrations() {
        mythicMobsAvailable = null;
        crucibleAvailable = null;
    }
    
    /**
     * Get integration status as a formatted string
     * @return Integration status string
     */
    public static String getIntegrationStatus() {
        StringBuilder status = new StringBuilder();
        status.append("§6Integration Status:");
        
        if (isMythicMobsAvailable()) {
            status.append("\n§a✓ MythicMobs: Available");
        } else {
            status.append("\n§c✗ MythicMobs: Not Available");
        }
        
        if (isCrucibleAvailable()) {
            status.append("\n§a✓ MythicCrucible: Available");
        } else {
            status.append("\n§c✗ MythicCrucible: Not Available");
        }
        
        return status.toString();
    }
    
    /**
     * Log integration status to console
     * @param plugin The plugin instance for logging
     */
    public static void logIntegrationStatus(Plugin plugin) {
        plugin.getLogger().info("Checking plugin integrations...");
        
        if (isMythicMobsAvailable()) {
            plugin.getLogger().info("✓ MythicMobs integration enabled");
        } else {
            plugin.getLogger().info("✗ MythicMobs not found - MythicMobs ingredients disabled");
        }
        
        if (isCrucibleAvailable()) {
            plugin.getLogger().info("✓ MythicCrucible integration enabled");
        } else {
            plugin.getLogger().info("✗ MythicCrucible not found - Crucible ingredients disabled");
        }
    }
    
    private static boolean checkMythicMobs() {
        try {
            // Check if plugin is loaded
            Plugin mythicMobs = Bukkit.getPluginManager().getPlugin("MythicMobs");
            if (mythicMobs == null || !mythicMobs.isEnabled()) {
                return false;
            }
            
            // Check if classes are available
            return MythicMobsIngredient.isMythicMobsAvailable();
            
        } catch (Exception e) {
            return false;
        }
    }
    
    private static boolean checkCrucible() {
        try {
            // Check if plugin is loaded
            Plugin crucible = Bukkit.getPluginManager().getPlugin("MythicCrucible");
            if (crucible == null || !crucible.isEnabled()) {
                return false;
            }
            
            // Check if classes are available
            return CrucibleIngredient.isCrucibleAvailable();
            
        } catch (Exception e) {
            return false;
        }
    }
}
