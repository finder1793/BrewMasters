package net.pwing.brewmasters.ingredients;

import org.bukkit.inventory.ItemStack;

/**
 * Abstract base class for brewing ingredients
 * Supports vanilla materials, MythicMobs items, and Crucible items
 */
public abstract class BrewingIngredient {
    
    protected final String identifier;
    protected final int amount;
    
    public BrewingIngredient(String identifier, int amount) {
        this.identifier = identifier;
        this.amount = amount;
    }
    
    /**
     * Check if the given ItemStack matches this ingredient
     * @param item The ItemStack to check
     * @return true if the item matches this ingredient
     */
    public abstract boolean matches(ItemStack item);
    
    /**
     * Get the display name for this ingredient
     * @return The display name
     */
    public abstract String getDisplayName();
    
    /**
     * Get an example ItemStack for this ingredient (for GUI display)
     * @return An example ItemStack, or null if not available
     */
    public abstract ItemStack getExampleItem();
    
    /**
     * Get the ingredient type
     * @return The ingredient type
     */
    public abstract IngredientType getType();
    
    /**
     * Get the identifier for this ingredient
     * @return The identifier string
     */
    public String getIdentifier() {
        return identifier;
    }
    
    /**
     * Get the required amount
     * @return The amount
     */
    public int getAmount() {
        return amount;
    }
    
    /**
     * Check if the given ItemStack has enough quantity
     * @param item The ItemStack to check
     * @return true if the item has enough quantity
     */
    public boolean hasEnoughQuantity(ItemStack item) {
        return item != null && item.getAmount() >= amount;
    }
    
    /**
     * Consume the ingredient from the given ItemStack
     * @param item The ItemStack to consume from
     * @return The modified ItemStack (may be null if fully consumed)
     */
    public ItemStack consume(ItemStack item) {
        if (item == null || item.getAmount() < amount) {
            return item;
        }
        
        int newAmount = item.getAmount() - amount;
        if (newAmount <= 0) {
            return null;
        }
        
        ItemStack result = item.clone();
        result.setAmount(newAmount);
        return result;
    }
    
    public enum IngredientType {
        VANILLA,
        MYTHIC_MOBS,
        CRUCIBLE
    }
    
    @Override
    public String toString() {
        return getType() + ":" + identifier + ":" + amount;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        BrewingIngredient that = (BrewingIngredient) obj;
        return amount == that.amount && 
               identifier.equals(that.identifier) && 
               getType() == that.getType();
    }
    
    @Override
    public int hashCode() {
        return identifier.hashCode() * 31 + amount;
    }
}
