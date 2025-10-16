package net.pwing.brewmasters.gui;

import net.pwing.brewmasters.BrewMasters;
import net.pwing.brewmasters.models.BrewingRecipe;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * GUI for displaying discovered recipes to players
 */
public class RecipeBookGUI {

    private final BrewMasters plugin;
    private final Player player;
    private final List<BrewingRecipe> discoveredRecipes;
    private int currentPage;
    private final int recipesPerPage = 28; // 7x4 grid with navigation

    public RecipeBookGUI(BrewMasters plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.discoveredRecipes = new ArrayList<>();
        this.currentPage = 0;
        loadDiscoveredRecipes();
    }

    /**
     * Load recipes that the player has discovered
     */
    private void loadDiscoveredRecipes() {
        Set<String> discoveredIds = plugin.getPlayerDataManager().getDiscoveredRecipes(player);

        for (String recipeId : discoveredIds) {
            BrewingRecipe recipe = plugin.getRecipeManager().getRecipe(recipeId);
            if (recipe != null) {
                discoveredRecipes.add(recipe);
            }
        }

        // Sort recipes alphabetically
        discoveredRecipes.sort((r1, r2) -> {
            String name1 = r1.getResultName() != null ? ChatColor.stripColor(r1.getResultName()) : r1.getId();
            String name2 = r2.getResultName() != null ? ChatColor.stripColor(r2.getResultName()) : r2.getId();
            return name1.compareToIgnoreCase(name2);
        });
    }

    /**
     * Open the recipe book GUI
     */
    public void open() {
        Inventory gui = createGUI();
        player.openInventory(gui);
    }

    /**
     * Create the GUI inventory
     */
    private Inventory createGUI() {
        int totalPages = (int) Math.ceil((double) discoveredRecipes.size() / recipesPerPage);
        String title = ChatColor.DARK_GREEN + "Recipe Book " + ChatColor.GRAY + "(" + (currentPage + 1) + "/"
                + Math.max(1, totalPages) + ")";

        Inventory gui = Bukkit.createInventory(null, 54, title);

        // Add recipes for current page
        int startIndex = currentPage * recipesPerPage;
        int endIndex = Math.min(startIndex + recipesPerPage, discoveredRecipes.size());

        for (int i = startIndex; i < endIndex; i++) {
            BrewingRecipe recipe = discoveredRecipes.get(i);
            ItemStack recipeItem = createRecipeItem(recipe);
            gui.setItem(i - startIndex, recipeItem);
        }

        // Add navigation items
        addNavigationItems(gui, totalPages);

        // Add info item
        addInfoItem(gui);

        return gui;
    }

    /**
     * Create an item representing a recipe
     */
    private ItemStack createRecipeItem(BrewingRecipe recipe) {
        // Use the result potion as the display item
        ItemStack item = recipe.createResultPotion();
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            // Add brewing information to lore
            List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();

            lore.add("");
            lore.add(ChatColor.YELLOW + "Recipe:");
            lore.add(ChatColor.GRAY + "Base: " + ChatColor.AQUA + formatMaterialName(recipe.getBasePotion()));
            lore.add(ChatColor.GRAY + "Ingredient: " + ChatColor.GREEN + recipe.getIngredient().getDisplayName());

            if (recipe.getBrewTime() != 400) {
                lore.add(ChatColor.GRAY + "Brew Time: " + ChatColor.WHITE + (recipe.getBrewTime() / 20) + "s");
            }

            // Add conditions if any
            if (!recipe.getConditions().isEmpty()) {
                lore.add("");
                lore.add(ChatColor.RED + "Conditions:");
                recipe.getConditions()
                        .forEach(condition -> lore.add(ChatColor.GRAY + "• " + condition.getDescription()));
            }

            lore.add("");
            lore.add(ChatColor.DARK_GRAY + "Click to view details");

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Format material name for display
     */
    private String formatMaterialName(Material material) {
        return Arrays.stream(material.name().split("_"))
                .map(word -> word.charAt(0) + word.substring(1).toLowerCase())
                .reduce((a, b) -> a + " " + b)
                .orElse(material.name());
    }

    /**
     * Add navigation items to the GUI
     */
    private void addNavigationItems(Inventory gui, int totalPages) {
        // Previous page button
        if (currentPage > 0) {
            ItemStack prevButton = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevButton.getItemMeta();
            if (prevMeta != null) {
                prevMeta.setDisplayName(ChatColor.YELLOW + "Previous Page");
                prevMeta.setLore(Arrays.asList(ChatColor.GRAY + "Go to page " + currentPage));
                prevButton.setItemMeta(prevMeta);
            }
            gui.setItem(45, prevButton);
        }

        // Next page button
        if (currentPage < totalPages - 1) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            if (nextMeta != null) {
                nextMeta.setDisplayName(ChatColor.YELLOW + "Next Page");
                nextMeta.setLore(Arrays.asList(ChatColor.GRAY + "Go to page " + (currentPage + 2)));
                nextButton.setItemMeta(nextMeta);
            }
            gui.setItem(53, nextButton);
        }

        // Close button
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName(ChatColor.RED + "Close");
            closeMeta.setLore(Arrays.asList(ChatColor.GRAY + "Close the recipe book"));
            closeButton.setItemMeta(closeMeta);
        }
        gui.setItem(49, closeButton);
    }

    /**
     * Add info item to the GUI
     */
    private void addInfoItem(Inventory gui) {
        ItemStack infoItem = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoItem.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(ChatColor.GOLD + "Recipe Book");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Discovered Recipes: " + ChatColor.YELLOW + discoveredRecipes.size());
            lore.add(
                    ChatColor.GRAY + "Total Recipes: " + ChatColor.YELLOW + plugin.getRecipeManager().getRecipeCount());
            lore.add("");
            lore.add(ChatColor.GRAY + "Discover more recipes by:");
            lore.add(ChatColor.GRAY + "• Exploring different biomes");
            lore.add(ChatColor.GRAY + "• Reaching higher levels");
            lore.add(ChatColor.GRAY + "• Brewing existing recipes");
            lore.add("");
            lore.add(ChatColor.DARK_GRAY + "Click recipes for details");

            infoMeta.setLore(lore);
            infoItem.setItemMeta(infoMeta);
        }
        gui.setItem(4, infoItem);
    }

    /**
     * Handle GUI click events
     */
    public boolean handleClick(int slot, ItemStack clickedItem) {
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return false;
        }

        // Handle navigation
        if (slot == 45 && currentPage > 0) {
            // Previous page
            currentPage--;
            open();
            return true;
        }

        if (slot == 53 && currentPage < Math.ceil((double) discoveredRecipes.size() / recipesPerPage) - 1) {
            // Next page
            currentPage++;
            open();
            return true;
        }

        if (slot == 49) {
            // Close button
            player.closeInventory();
            return true;
        }

        // Handle recipe clicks
        if (slot < recipesPerPage) {
            int recipeIndex = currentPage * recipesPerPage + slot;
            if (recipeIndex < discoveredRecipes.size()) {
                BrewingRecipe recipe = discoveredRecipes.get(recipeIndex);
                openRecipeDetails(recipe);
                return true;
            }
        }

        return false;
    }

    /**
     * Open detailed view of a recipe
     */
    private void openRecipeDetails(BrewingRecipe recipe) {
        RecipeDetailsGUI detailsGUI = new RecipeDetailsGUI(plugin, player, recipe, this);
        detailsGUI.open();
    }

    /**
     * Get the current page
     */
    public int getCurrentPage() {
        return currentPage;
    }
}
