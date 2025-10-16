package net.pwing.brewmasters.ingredients;

import org.bukkit.Material;

/**
 * Factory class for creating brewing ingredients from string representations
 */
public class IngredientFactory {
    
    /**
     * Create a BrewingIngredient from a string representation
     * 
     * Supported formats:
     * - Vanilla: "MATERIAL" or "MATERIAL:amount"
     * - MythicMobs: "mythic:ITEM_ID" or "mythic:ITEM_ID:amount"
     * - Crucible: "crucible:ITEM_ID" or "crucible:ITEM_ID:amount"
     * 
     * @param input The string representation
     * @return The BrewingIngredient
     * @throws IllegalArgumentException if the input is invalid
     */
    public static BrewingIngredient fromString(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Ingredient string cannot be null or empty");
        }
        
        String trimmed = input.trim();
        
        // Check for MythicMobs ingredient
        if (trimmed.toLowerCase().startsWith("mythic:")) {
            return MythicMobsIngredient.fromString(trimmed);
        }
        
        // Check for Crucible ingredient
        if (trimmed.toLowerCase().startsWith("crucible:")) {
            return CrucibleIngredient.fromString(trimmed);
        }
        
        // Default to vanilla ingredient
        return VanillaIngredient.fromString(trimmed);
    }
    
    /**
     * Create a BrewingIngredient from a Material (vanilla only)
     * @param material The material
     * @param amount The amount
     * @return The VanillaIngredient
     */
    public static VanillaIngredient fromMaterial(Material material, int amount) {
        return new VanillaIngredient(material, amount);
    }
    
    /**
     * Create a BrewingIngredient from a Material with amount 1 (vanilla only)
     * @param material The material
     * @return The VanillaIngredient
     */
    public static VanillaIngredient fromMaterial(Material material) {
        return new VanillaIngredient(material, 1);
    }
    
    /**
     * Create a MythicMobs ingredient
     * @param itemId The MythicMobs item ID
     * @param amount The amount
     * @return The MythicMobsIngredient
     */
    public static MythicMobsIngredient fromMythicMobs(String itemId, int amount) {
        return new MythicMobsIngredient(itemId, amount);
    }
    
    /**
     * Create a MythicMobs ingredient with amount 1
     * @param itemId The MythicMobs item ID
     * @return The MythicMobsIngredient
     */
    public static MythicMobsIngredient fromMythicMobs(String itemId) {
        return new MythicMobsIngredient(itemId, 1);
    }
    
    /**
     * Create a Crucible ingredient
     * @param itemId The Crucible item ID
     * @param amount The amount
     * @return The CrucibleIngredient
     */
    public static CrucibleIngredient fromCrucible(String itemId, int amount) {
        return new CrucibleIngredient(itemId, amount);
    }
    
    /**
     * Create a Crucible ingredient with amount 1
     * @param itemId The Crucible item ID
     * @return The CrucibleIngredient
     */
    public static CrucibleIngredient fromCrucible(String itemId) {
        return new CrucibleIngredient(itemId, 1);
    }
    
    /**
     * Check if the given string represents a MythicMobs ingredient
     * @param input The input string
     * @return true if it's a MythicMobs ingredient
     */
    public static boolean isMythicMobsIngredient(String input) {
        return input != null && input.trim().toLowerCase().startsWith("mythic:");
    }
    
    /**
     * Check if the given string represents a Crucible ingredient
     * @param input The input string
     * @return true if it's a Crucible ingredient
     */
    public static boolean isCrucibleIngredient(String input) {
        return input != null && input.trim().toLowerCase().startsWith("crucible:");
    }
    
    /**
     * Check if the given string represents a vanilla ingredient
     * @param input The input string
     * @return true if it's a vanilla ingredient
     */
    public static boolean isVanillaIngredient(String input) {
        if (input == null || input.trim().isEmpty()) {
            return false;
        }
        
        String trimmed = input.trim();
        
        // If it has a prefix, it's not vanilla
        if (trimmed.toLowerCase().startsWith("mythic:") || 
            trimmed.toLowerCase().startsWith("crucible:")) {
            return false;
        }
        
        // Check if it's a valid material
        String materialName = trimmed.split(":")[0];
        return Material.matchMaterial(materialName) != null;
    }
}
