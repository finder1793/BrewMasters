package net.pwing.brewmasters.ingredients;

import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythiccrucible.MythicCrucible;
import io.lumine.mythiccrucible.items.CrucibleItem;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

/**
 * MythicCrucible item ingredient
 */
public class CrucibleIngredient extends BrewingIngredient {

    private final String crucibleItemId;

    public CrucibleIngredient(String crucibleItemId, int amount) {
        super(crucibleItemId, amount);
        this.crucibleItemId = crucibleItemId;
    }

    @Override
    public boolean matches(ItemStack item) {
        if (item == null) {
            return false;
        }

        try {
            // Check if MythicCrucible is available
            if (!isCrucibleAvailable()) {
                return false;
            }

            // Get the Crucible item from the ItemStack
            Optional<CrucibleItem> crucibleItem = MythicCrucible.inst().getItemManager().getItem(item);
            if (crucibleItem.isPresent()) {
                return crucibleItemId.equals(crucibleItem.get().getMythicItem().getInternalName());
            }

            return false;

        } catch (Exception e) {
            // MythicCrucible not available or error occurred
            return false;
        }
    }

    @Override
    public String getDisplayName() {
        try {
            if (isCrucibleAvailable()) {
                Optional<CrucibleItem> crucibleItem = MythicCrucible.inst().getItemManager().getItem(crucibleItemId);
                if (crucibleItem.isPresent()) {
                    return crucibleItem.get().getMythicItem().getDisplayName();
                }
            }
        } catch (Exception e) {
            // Fallback to identifier
        }
        return crucibleItemId;
    }

    @Override
    public ItemStack getExampleItem() {
        try {
            if (isCrucibleAvailable()) {
                Optional<CrucibleItem> crucibleItem = MythicCrucible.inst().getItemManager().getItem(crucibleItemId);
                if (crucibleItem.isPresent()) {
                    return BukkitAdapter.adapt(crucibleItem.get().getMythicItem().generateItemStack(amount));
                }
            }
        } catch (Exception e) {
            // MythicCrucible not available or error occurred
        }
        return null;
    }

    @Override
    public IngredientType getType() {
        return IngredientType.CRUCIBLE;
    }

    /**
     * Get the Crucible item ID
     * 
     * @return The item ID
     */
    public String getCrucibleItemId() {
        return crucibleItemId;
    }

    /**
     * Check if MythicCrucible is available
     * 
     * @return true if MythicCrucible is available
     */
    public static boolean isCrucibleAvailable() {
        try {
            Class.forName("io.lumine.mythiccrucible.MythicCrucible");
            return MythicCrucible.inst() != null;
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return false;
        }
    }

    /**
     * Create a CrucibleIngredient from a string
     * Supports formats: "crucible:ITEM_ID" or "crucible:ITEM_ID:amount"
     * 
     * @param input The input string
     * @return The CrucibleIngredient
     */
    public static CrucibleIngredient fromString(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }

        String[] parts = input.trim().split(":");
        if (parts.length < 2) {
            throw new IllegalArgumentException(
                    "Crucible ingredient must be in format 'crucible:ITEM_ID' or 'crucible:ITEM_ID:amount'");
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

        return new CrucibleIngredient(itemId, amount);
    }
}
