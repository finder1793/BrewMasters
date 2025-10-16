package net.pwing.brewmasters.listeners;

import net.pwing.brewmasters.BrewMasters;
import net.pwing.brewmasters.models.BrewingRecipe;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Listens for players drinking potions to trigger drink commands
 */
public class PotionDrinkListener implements Listener {
    
    private final BrewMasters plugin;
    
    public PotionDrinkListener(BrewMasters plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPotionDrink(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        
        // Check if it's a potion
        if (!isPotion(item.getType())) {
            return;
        }
        
        Player player = event.getPlayer();
        
        // Try to match the potion to a custom recipe by checking display name
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            String displayName = meta.displayName().toString();
            
            // Search for matching recipe
            for (BrewingRecipe recipe : plugin.getRecipeManager().getAllRecipes()) {
                if (recipe.getResultName() != null && !recipe.getResultName().isEmpty()) {
                    // Match by display name (basic check - could be enhanced)
                    if (displayName.contains(recipe.getResultName()) || 
                        displayName.contains(recipe.getId())) {
                        
                        // Trigger potion drink event
                        plugin.getPotionEffectManager().onPotionDrunk(player, recipe);
                        break;
                    }
                }
            }
        }
    }
    
    private boolean isPotion(Material material) {
        return material == Material.POTION ||
               material == Material.SPLASH_POTION ||
               material == Material.LINGERING_POTION;
    }
}
