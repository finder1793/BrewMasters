package net.pwing.brewmasters.ingredients;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Vanilla Minecraft material ingredient
 */
public class VanillaIngredient extends BrewingIngredient {
    
    private final Material material;
    
    public VanillaIngredient(Material material, int amount) {
        super(material.name(), amount);
        this.material = material;
    }
    
    public VanillaIngredient(String materialName, int amount) {
        super(materialName, amount);
        this.material = Material.matchMaterial(materialName);
        if (this.material == null) {
            throw new IllegalArgumentException("Invalid material: " + materialName);
        }
    }
    
    @Override
    public boolean matches(ItemStack item) {
        return item != null && item.getType() == material;
    }
    
    @Override
    public String getDisplayName() {
        return material.name().toLowerCase().replace('_', ' ');
    }
    
    @Override
    public ItemStack getExampleItem() {
        return new ItemStack(material, amount);
    }
    
    @Override
    public IngredientType getType() {
        return IngredientType.VANILLA;
    }
    
    /**
     * Get the material for this ingredient
     * @return The material
     */
    public Material getMaterial() {
        return material;
    }
    
    /**
     * Create a VanillaIngredient from a string
     * Supports formats: "MATERIAL" or "MATERIAL:amount"
     * @param input The input string
     * @return The VanillaIngredient
     */
    public static VanillaIngredient fromString(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        
        String[] parts = input.trim().split(":");
        String materialName = parts[0];
        int amount = 1;
        
        if (parts.length > 1) {
            try {
                amount = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid amount: " + parts[1]);
            }
        }
        
        return new VanillaIngredient(materialName, amount);
    }
}
