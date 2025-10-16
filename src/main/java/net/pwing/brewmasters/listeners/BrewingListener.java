package net.pwing.brewmasters.listeners;

import net.pwing.brewmasters.BrewMasters;
import net.pwing.brewmasters.models.BrewingRecipe;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

public class BrewingListener implements Listener {

    private final BrewMasters plugin;

    public BrewingListener(BrewMasters plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBrew(BrewEvent event) {
        BrewerInventory inventory = event.getContents();
        ItemStack ingredient = inventory.getIngredient();

        if (ingredient == null || ingredient.getType() == Material.AIR) {
            return;
        }

        Block block = event.getBlock();
        Location location = block.getLocation();

        // Try to find the player who is brewing
        Player brewer = findNearbyPlayer(location);

        // Check each potion slot
        for (int i = 0; i < 3; i++) {
            ItemStack potion = inventory.getItem(i);

            if (potion == null || potion.getType() == Material.AIR) {
                continue;
            }

            // Check if it's a potion type (normal, splash, or lingering)
            if (!isPotion(potion.getType())) {
                continue;
            }

            // Find matching recipe using the new ingredient system
            BrewingRecipe recipe = plugin.getRecipeManager().findRecipe(potion.getType(), ingredient);

            if (recipe != null) {
                // Check if player has discovered this recipe
                if (brewer != null && !plugin.getDiscoveryManager().canAccessRecipe(brewer, recipe.getId())) {
                    // Recipe not discovered yet
                    continue;
                }

                // Check conditions
                if (!recipe.checkConditions(brewer, location)) {
                    // Conditions not met - cancel this slot's brewing
                    if (brewer != null) {
                        brewer.sendMessage("§cConditions not met for brewing this recipe!");
                    }
                    continue;
                }

                // Apply custom brewing speed if enabled
                if (plugin.getBrewingSpeedManager().isSpeedSystemEnabled()) {
                    plugin.getBrewingSpeedManager().applySpeedToBrewingStand(location, recipe, brewer);
                }

                // Schedule the replacement after brewing completes
                final int slot = i;
                final String recipeId = recipe.getId();

                // Calculate delay based on custom brewing time
                int brewingTime = plugin.getBrewingSpeedManager().calculateBrewingTime(recipe, brewer, location);
                long delayTicks = Math.max(1L, brewingTime);

                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    ItemStack result = recipe.createResultPotion();
                    inventory.setItem(slot, result);

                    // Update the brewing stand
                    if (event.getBlock().getState() instanceof BrewingStand) {
                        BrewingStand stand = (BrewingStand) event.getBlock().getState();
                        stand.update();
                    }

                    // Trigger achievement check
                    if (brewer != null) {
                        plugin.getAchievementManager().onPotionBrewed(brewer, recipeId);

                        // Trigger chain progress tracking
                        plugin.getBrewingChainManager().onRecipeBrewed(brewer, recipeId);
                    }
                }, delayTicks);
            }
        }
    }

    /**
     * Find a player near the brewing stand (within 5 blocks)
     * 
     * @param location The brewing stand location
     * @return The nearest player, or null if none found
     */
    private Player findNearbyPlayer(Location location) {
        return location.getWorld().getNearbyEntities(location, 5, 5, 5).stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .findFirst()
                .orElse(null);
    }

    private boolean isPotion(Material material) {
        return material == Material.POTION ||
                material == Material.SPLASH_POTION ||
                material == Material.LINGERING_POTION;
    }
}
