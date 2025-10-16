package net.pwing.brewmasters.listeners;

import net.pwing.brewmasters.BrewMasters;
import net.pwing.brewmasters.gui.AchievementsGUI;
import net.pwing.brewmasters.gui.RecipeBookGUI;
import net.pwing.brewmasters.gui.RecipeDetailsGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles GUI interactions for recipe books and achievements
 */
public class GUIListener implements Listener {

    private final BrewMasters plugin;
    private final Map<UUID, RecipeBookGUI> recipeBookGUIs;
    private final Map<UUID, RecipeDetailsGUI> recipeDetailsGUIs;
    private final Map<UUID, AchievementsGUI> achievementGUIs;

    public GUIListener(BrewMasters plugin) {
        this.plugin = plugin;
        this.recipeBookGUIs = new HashMap<>();
        this.recipeDetailsGUIs = new HashMap<>();
        this.achievementGUIs = new HashMap<>();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        // Check if it's one of our GUIs
        if (isRecipeBookGUI(title)) {
            event.setCancelled(true);
            handleRecipeBookClick(player, event.getSlot(), event.getCurrentItem());
        } else if (isRecipeDetailsGUI(title)) {
            event.setCancelled(true);
            handleRecipeDetailsClick(player, event.getSlot(), event.getCurrentItem());
        } else if (isAchievementsGUI(title)) {
            event.setCancelled(true);
            handleAchievementsClick(player, event.getSlot(), event.getCurrentItem());
        }
    }

    /**
     * Check if the inventory is a recipe book GUI
     */
    private boolean isRecipeBookGUI(String title) {
        return title.contains("Recipe Book");
    }

    /**
     * Check if the inventory is a recipe details GUI
     */
    private boolean isRecipeDetailsGUI(String title) {
        return title.contains("Recipe:");
    }

    /**
     * Check if the inventory is an achievements GUI
     */
    private boolean isAchievementsGUI(String title) {
        return title.contains("Achievements");
    }

    /**
     * Handle recipe book GUI clicks
     */
    private void handleRecipeBookClick(Player player, int slot, ItemStack clickedItem) {
        RecipeBookGUI gui = recipeBookGUIs.get(player.getUniqueId());
        if (gui != null) {
            gui.handleClick(slot, clickedItem);
        }
    }

    /**
     * Handle recipe details GUI clicks
     */
    private void handleRecipeDetailsClick(Player player, int slot, ItemStack clickedItem) {
        RecipeDetailsGUI gui = recipeDetailsGUIs.get(player.getUniqueId());
        if (gui != null) {
            gui.handleClick(slot, clickedItem);
        }
    }

    /**
     * Handle achievements GUI clicks
     */
    private void handleAchievementsClick(Player player, int slot, ItemStack clickedItem) {
        AchievementsGUI gui = achievementGUIs.get(player.getUniqueId());
        if (gui != null) {
            gui.handleClick(slot, clickedItem);
        }
    }

    /**
     * Open recipe book for a player
     */
    public void openRecipeBook(Player player) {
        if (!plugin.getDiscoveryManager().isDiscoveryEnabled()) {
            player.sendMessage(ChatColor.RED + "Recipe discovery is not enabled on this server.");
            return;
        }

        RecipeBookGUI gui = new RecipeBookGUI(plugin, player);
        recipeBookGUIs.put(player.getUniqueId(), gui);
        gui.open();
    }

    /**
     * Open recipe details for a player
     */
    public void openRecipeDetails(Player player, RecipeDetailsGUI detailsGUI) {
        recipeDetailsGUIs.put(player.getUniqueId(), detailsGUI);
    }

    /**
     * Open achievements for a player
     */
    public void openAchievements(Player player) {
        if (!plugin.getAchievementManager().isAchievementsEnabled()) {
            player.sendMessage(ChatColor.RED + "Achievements are not enabled on this server.");
            return;
        }

        AchievementsGUI gui = new AchievementsGUI(plugin, player);
        achievementGUIs.put(player.getUniqueId(), gui);
        gui.open();
    }

    /**
     * Clean up GUI references when player leaves
     */
    public void cleanupPlayer(UUID playerId) {
        recipeBookGUIs.remove(playerId);
        recipeDetailsGUIs.remove(playerId);
        achievementGUIs.remove(playerId);
    }
}
