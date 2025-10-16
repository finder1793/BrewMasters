package net.pwing.brewmasters.ingredients;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.MythicItem;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * MythicMobs item ingredient
 */
public class MythicMobsIngredient extends BrewingIngredient {

    private final String mythicItemId;

    public MythicMobsIngredient(String mythicItemId, int amount) {
        super(mythicItemId, amount);
        this.mythicItemId = mythicItemId;
    }

    @Override
    public boolean matches(ItemStack item) {
        if (item == null) {
            return false;
        }

        try {
            // Check if MythicMobs is available
            if (!isMythicMobsAvailable()) {
                return false;
            }

            // Get the MythicMobs item type from the ItemStack
            String itemType = MythicBukkit.inst().getItemManager().getMythicTypeFromItem(item);
            return mythicItemId.equals(itemType);

        } catch (Exception e) {
            // MythicMobs not available or error occurred
            return false;
        }
    }

    @Override
    public String getDisplayName() {
        try {
            if (isMythicMobsAvailable()) {
                Optional<MythicItem> mythicItem = MythicBukkit.inst().getItemManager().getItem(mythicItemId);
                if (mythicItem.isPresent()) {
                    return mythicItem.get().getDisplayName();
                }
            }
        } catch (Exception e) {
            // Fallback to identifier
        }
        return mythicItemId;
    }

    @Override
    public ItemStack getExampleItem() {
        try {
            if (isMythicMobsAvailable()) {
                Optional<MythicItem> mythicItem = MythicBukkit.inst().getItemManager().getItem(mythicItemId);
                if (mythicItem.isPresent()) {
                    return BukkitAdapter.adapt(mythicItem.get().generateItemStack(amount));
                }
            }
        } catch (Exception e) {
            // MythicMobs not available or error occurred
        }
        return null;
    }

    @Override
    public IngredientType getType() {
        return IngredientType.MYTHIC_MOBS;
    }

    /**
     * Get the MythicMobs item ID
     * 
     * @return The item ID
     */
    public String getMythicItemId() {
        return mythicItemId;
    }

    /**
     * Check if MythicMobs is available
     * 
     * @return true if MythicMobs is available
     */
    public static boolean isMythicMobsAvailable() {
        try {
            Class.forName("io.lumine.mythic.bukkit.MythicBukkit");
            return MythicBukkit.inst() != null;
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return false;
        }
    }

    /**
     * Create a MythicMobsIngredient from a string
     * Supports formats: "mythic:ITEM_ID" or "mythic:ITEM_ID:amount"
     * 
     * @param input The input string
     * @return The MythicMobsIngredient
     */
    public static MythicMobsIngredient fromString(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }

        String[] parts = input.trim().split(":");
        if (parts.length < 2) {
            throw new IllegalArgumentException(
                    "MythicMobs ingredient must be in format 'mythic:ITEM_ID' or 'mythic:ITEM_ID:amount'");
        }

        String itemId = parts[1];
        int amount = 1;

        if (parts.length > 2) {
            try {
                amount = Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid amount: " + parts[2]);
            }
        }

        return new MythicMobsIngredient(itemId, amount);
    }
}
