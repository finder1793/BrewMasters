package net.pwing.brewmasters.gui;

import net.kyori.adventure.text.Component;
import net.pwing.brewmasters.BrewMasters;
import net.pwing.brewmasters.gui.config.RecipeBookGUIConfig;
import net.pwing.brewmasters.models.BrewingRecipe;
import net.pwing.brewmasters.utils.InventoryUtils;
import net.pwing.brewmasters.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * GUI for displaying discovered recipes to players
 * Fully configurable with MiniMessage support, visibility filtering, and advanced features
 */
@SuppressWarnings("deprecation")
public class RecipeBookGUI {

    private final BrewMasters plugin;
    private final Player player;
    private final RecipeBookGUIConfig config;
    private final List<BrewingRecipe> allRecipes;
    private final List<BrewingRecipe> visibleRecipes;
    private int currentPage;
    private final Map<Material, String> formattedMaterialCache = new HashMap<>();

    public RecipeBookGUI(BrewMasters plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.config = plugin.getGUIConfigManager().getRecipeBookConfig();
        this.allRecipes = new ArrayList<>();
        this.visibleRecipes = new ArrayList<>();
        this.currentPage = 0;
        
        loadRecipes();
    }

    /**
     * Load all recipes and filter based on visibility settings
     */
    private void loadRecipes() {
        // Get all recipes from the manager
        Collection<BrewingRecipe> recipes = plugin.getRecipeManager().getAllRecipes();
        
        for (BrewingRecipe recipe : recipes) {
            allRecipes.add(recipe);
            
            // Check if this recipe should be visible to the player
            if (shouldShowRecipe(player, recipe)) {
                visibleRecipes.add(recipe);
            }
        }

        // Sort visible recipes alphabetically by name
        visibleRecipes.sort((r1, r2) -> {
            String name1 = r1.getResultName() != null ? TextUtils.stripColor(r1.getResultName()) : r1.getId();
            String name2 = r2.getResultName() != null ? TextUtils.stripColor(r2.getResultName()) : r2.getId();
            return name1.compareToIgnoreCase(name2);
        });
    }
    
    /**
     * Determine if a recipe should be shown to the player based on visibility settings
     */
    private boolean shouldShowRecipe(Player player, BrewingRecipe recipe) {
        // Check discovery status
        boolean discovered = plugin.getDiscoveryManager().canAccessRecipe(player, recipe.getId());
        if (!discovered && config.shouldHideUndiscovered()) {
            return false; // Hide undiscovered recipes
        }
        
        // Check permission requirements (if recipe has them)
        // Note: We'll need to add permission support to BrewingRecipe in the future
        // For now, we skip permission checks
        
        // Check conditions
        if (!recipe.getConditions().isEmpty()) {
            Location playerLoc = player.getLocation();
            boolean conditionsMet = recipe.checkConditions(player, playerLoc);
            if (!conditionsMet && config.shouldHideUnmetConditions()) {
                return false; // Hide recipes with unmet conditions
            }
        }
        
        return true; // Show this recipe
    }

    /**
     * Open the recipe book GUI
     */
    public void open() {
        Inventory gui = createGUI();
        player.openInventory(gui);
        
        // Play open sound
        player.playSound(player.getLocation(), config.getOpenSound(), 1.0f, 1.0f);
    }

    /**
     * Create the GUI inventory with modern config-based approach
     */
    private Inventory createGUI() {
        // Calculate pagination
        int itemsPerPage = config.getItemsPerPage();
        int totalPages = (int) Math.ceil((double) visibleRecipes.size() / itemsPerPage);
        if (totalPages == 0) totalPages = 1; // At least one page
        
        // Create title with placeholders replaced
        String titleText = config.getTitle()
                .replace("{current_page}", String.valueOf(currentPage + 1))
                .replace("{total_pages}", String.valueOf(totalPages));
        Component titleComponent = TextUtils.parseAuto(titleText);
        
        // Create inventory with configured size and modern title
        Inventory gui = InventoryUtils.createInventory(config.getSize(), titleComponent);
        
        // Add filler items if enabled
        if (config.isFillerEnabled()) {
            addFillerItems(gui);
        }
        
        // Add recipes for current page
        populateRecipes(gui);
        
        // Add navigation items
        addNavigationItems(gui, totalPages);
        
        // Add info item
        addInfoItem(gui);
        
        return gui;
    }
    
    /**
     * Add filler items to empty slots
     */
    private void addFillerItems(Inventory gui) {
        Material fillerMaterial = config.getFillerMaterial();
        String fillerName = config.getFillerName();
        int customModelData = config.getFillerCustomModelData();
        
        ItemStack filler = InventoryUtils.builder(fillerMaterial)
                .displayNameMini(fillerName)
                .customModelData(customModelData > 0 ? customModelData : 0)
                .build();
        
        // Fill all slots initially (will be overwritten by actual items)
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, filler);
        }
    }
    
    /**
     * Populate the GUI with recipe items based on current page
     */
    private void populateRecipes(Inventory gui) {
        int itemsPerPage = config.getItemsPerPage();
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, visibleRecipes.size());
        
        // Add visible recipes for this page
        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            BrewingRecipe recipe = visibleRecipes.get(i);
            ItemStack recipeItem = createRecipeItem(recipe);
            gui.setItem(slot, recipeItem);
            slot++;
        }
    }

    /**
     * Create an item representing a recipe with proper state handling
     */
    private ItemStack createRecipeItem(BrewingRecipe recipe) {
        boolean discovered = plugin.getDiscoveryManager().canAccessRecipe(player, recipe.getId());
        boolean conditionsMet = recipe.getConditions().isEmpty() || 
                                recipe.checkConditions(player, player.getLocation());
        
        // Determine recipe state and create appropriate item
        if (discovered) {
            return createDiscoveredRecipeItem(recipe, conditionsMet);
        } else if (!conditionsMet) {
            return createConditionLockedItem(recipe);
        } else {
            return createLockedRecipeItem(recipe);
        }
    }
    
    /**
     * Create item for a discovered recipe
     */
    private ItemStack createDiscoveredRecipeItem(BrewingRecipe recipe, boolean conditionsMet) {
        // Use the actual result potion as the display item
        ItemStack item = recipe.createResultPotion();
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            // Apply configured name if set
            String configName = config.getDiscoveredRecipeName();
            if (configName != null && !configName.isEmpty()) {
                String recipeName = recipe.getResultName() != null ? recipe.getResultName() : recipe.getId();
                String displayName = configName.replace("{recipe_name}", recipeName);
                Component nameComponent = TextUtils.parseAuto(displayName);
                meta.displayName(nameComponent);
            }
            
            // Build lore from config
            List<Component> lore = new ArrayList<>();
            for (String loreLine : config.getDiscoveredRecipeLore()) {
                String processedLine = loreLine
                        .replace("{recipe_name}", recipe.getResultName() != null ? recipe.getResultName() : recipe.getId())
                        .replace("{base_potion}", formatMaterialName(recipe.getBasePotion()))
                        .replace("{ingredient}", recipe.getIngredient().getDisplayName())
                        .replace("{brew_time}", String.valueOf(recipe.getBrewTime() / 20));
                lore.add(TextUtils.parseAuto(processedLine));
            }
            
            // Add condition warning if not met
            if (!conditionsMet) {
                lore.add(Component.empty());
                lore.add(TextUtils.parseAuto("<red>⚠ Conditions not currently met</red>"));
            }
            
            meta.lore(lore);
            
            item.setItemMeta(meta);
        }
        
        return applyGlow(item, config.shouldDiscoveredRecipeGlow());
    }
    
    /**
     * Create item for a locked (undiscovered) recipe
     */
    private ItemStack createLockedRecipeItem(BrewingRecipe recipe) {
        Material material = config.getLockedRecipeMaterial();
        String name = config.getLockedRecipeName();
        List<String> loreLines = config.getLockedRecipeLore();
        int customModelData = config.getLockedRecipeCustomModelData();
        
        // Build item with config values
        InventoryUtils.ItemBuilder builder = InventoryUtils.builder(material)
                .displayNameMini(name);
        
        // Add lore
        List<Component> lore = new ArrayList<>();
        for (String line : loreLines) {
            lore.add(TextUtils.parseAuto(line));
        }
        builder.lore(lore);
        
        // Add custom model data if set
        if (customModelData > 0) {
            builder.customModelData(customModelData);
        }
        
        ItemStack item = builder.build();
        return applyGlow(item, config.shouldLockedRecipeGlow());
    }
    
    /**
     * Create item for a recipe with unmet conditions
     */
    private ItemStack createConditionLockedItem(BrewingRecipe recipe) {
        Material material = config.getConditionLockedMaterial();
        String name = config.getConditionLockedName();
        List<String> loreLines = config.getConditionLockedLore();
        int customModelData = config.getConditionLockedCustomModelData();
        
        // Build item with config values
        InventoryUtils.ItemBuilder builder = InventoryUtils.builder(material)
                .displayNameMini(name.replace("{recipe_name}", 
                        recipe.getResultName() != null ? recipe.getResultName() : recipe.getId()));
        
        // Add lore with condition details
        List<Component> lore = new ArrayList<>();
        for (String line : loreLines) {
            lore.add(TextUtils.parseAuto(line));
        }
        
        // Add condition list
        if (!recipe.getConditions().isEmpty()) {
            lore.add(Component.empty());
            lore.add(TextUtils.parseAuto("<yellow>Required Conditions:</yellow>"));
            for (var condition : recipe.getConditions()) {
                lore.add(TextUtils.parseAuto("<gray>• " + condition.getDescription() + "</gray>"));
            }
        }
        
        builder.lore(lore);
        
        // Add custom model data if set
        if (customModelData > 0) {
            builder.customModelData(customModelData);
        }
        
        ItemStack item = builder.build();
        return applyGlow(item, config.shouldConditionLockedGlow());
    }
    
    /**
     * Apply glow effect to an item (Paper 1.20.5+ feature)
     */
    private ItemStack applyGlow(ItemStack item, boolean shouldGlow) {
        if (!shouldGlow) return item;
        
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            try {
                meta.setEnchantmentGlintOverride(true);
                item.setItemMeta(meta);
            } catch (NoSuchMethodError e) {
                // Method not available in this server version, skip glow
            }
        }
        return item;
    }

    /**
     * Format material name for display (cached)
     */
    private String formatMaterialName(Material material) {
        return formattedMaterialCache.computeIfAbsent(material, m -> {
            String[] parts = m.name().split("_");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) sb.append(' ');
                String part = parts[i];
                sb.append(part.charAt(0)).append(part.substring(1).toLowerCase());
            }
            return sb.toString();
        });
    }

    /**
     * Add navigation items to the GUI using config
     */
    private void addNavigationItems(Inventory gui, int totalPages) {
        // Previous page button
        if (currentPage > 0) {
            Map<String, Object> prevData = config.getNavigationItem("previous-page");
            if ((boolean) prevData.getOrDefault("enabled", true)) {
                int slot = (int) prevData.getOrDefault("slot", 45);
                Material material = Material.valueOf((String) prevData.getOrDefault("material", "ARROW"));
                String name = ((String) prevData.getOrDefault("name", "<yellow>Previous Page</yellow>"))
                        .replace("{page}", String.valueOf(currentPage));
                @SuppressWarnings("unchecked")
                List<String> loreLines = (List<String>) prevData.getOrDefault("lore", new ArrayList<>());
                int customModelData = (int) prevData.getOrDefault("custom-model-data", 0);
                
                InventoryUtils.ItemBuilder builder = InventoryUtils.builder(material)
                        .displayNameMini(name);
                
                List<Component> lore = new ArrayList<>();
                for (String line : loreLines) {
                    lore.add(TextUtils.parseAuto(line.replace("{page}", String.valueOf(currentPage))));
                }
                builder.lore(lore);
                
                if (customModelData > 0) {
                    builder.customModelData(customModelData);
                }
                
                gui.setItem(slot, builder.build());
            }
        }

        // Next page button
        if (currentPage < totalPages - 1) {
            Map<String, Object> nextData = config.getNavigationItem("next-page");
            if ((boolean) nextData.getOrDefault("enabled", true)) {
                int slot = (int) nextData.getOrDefault("slot", 53);
                Material material = Material.valueOf((String) nextData.getOrDefault("material", "ARROW"));
                String name = ((String) nextData.getOrDefault("name", "<yellow>Next Page</yellow>"))
                        .replace("{page}", String.valueOf(currentPage + 2));
                @SuppressWarnings("unchecked")
                List<String> loreLines = (List<String>) nextData.getOrDefault("lore", new ArrayList<>());
                int customModelData = (int) nextData.getOrDefault("custom-model-data", 0);
                
                InventoryUtils.ItemBuilder builder = InventoryUtils.builder(material)
                        .displayNameMini(name);
                
                List<Component> lore = new ArrayList<>();
                for (String line : loreLines) {
                    lore.add(TextUtils.parseAuto(line.replace("{page}", String.valueOf(currentPage + 2))));
                }
                builder.lore(lore);
                
                if (customModelData > 0) {
                    builder.customModelData(customModelData);
                }
                
                gui.setItem(slot, builder.build());
            }
        }

        // Close button
        Map<String, Object> closeData = config.getNavigationItem("close");
        if ((boolean) closeData.getOrDefault("enabled", true)) {
            int slot = (int) closeData.getOrDefault("slot", 49);
            Material material = Material.valueOf((String) closeData.getOrDefault("material", "BARRIER"));
            String name = (String) closeData.getOrDefault("name", "<red>Close</red>");
            @SuppressWarnings("unchecked")
            List<String> loreLines = (List<String>) closeData.getOrDefault("lore", new ArrayList<>());
            int customModelData = (int) closeData.getOrDefault("custom-model-data", 0);
            
            InventoryUtils.ItemBuilder builder = InventoryUtils.builder(material)
                    .displayNameMini(name);
            
            List<Component> lore = new ArrayList<>();
            for (String line : loreLines) {
                lore.add(TextUtils.parseAuto(line));
            }
            builder.lore(lore);
            
            if (customModelData > 0) {
                builder.customModelData(customModelData);
            }
            
            gui.setItem(slot, builder.build());
        }
    }

    /**
     * Add info item to the GUI
     */
    private void addInfoItem(Inventory gui) {
        // Get info item config
        Map<String, Object> infoData = config.getNavigationItem("info");
        if (!(boolean) infoData.getOrDefault("enabled", true)) {
            return; // Info item disabled
        }
        
        int slot = (int) infoData.getOrDefault("slot", 4);
        Material material = Material.valueOf((String) infoData.getOrDefault("material", "BOOK"));
        String name = (String) infoData.getOrDefault("name", "<gold>Recipe Book</gold>");
        @SuppressWarnings("unchecked")
        List<String> loreLines = (List<String>) infoData.getOrDefault("lore", new ArrayList<>());
        int customModelData = (int) infoData.getOrDefault("custom-model-data", 0);
        
        // Count discovered recipes
        int discoveredCount = 0;
        for (BrewingRecipe recipe : allRecipes) {
            if (plugin.getDiscoveryManager().canAccessRecipe(player, recipe.getId())) {
                discoveredCount++;
            }
        }
        
        int totalRecipes = allRecipes.size();
        double percentage = totalRecipes > 0 ? (double) discoveredCount / totalRecipes * 100 : 0;
        
        // Build item with placeholders
        InventoryUtils.ItemBuilder builder = InventoryUtils.builder(material)
                .displayNameMini(name);
        
        List<Component> lore = new ArrayList<>();
        for (String line : loreLines) {
            String processedLine = line
                    .replace("{discovered}", String.valueOf(discoveredCount))
                    .replace("{total}", String.valueOf(totalRecipes))
                    .replace("{visible}", String.valueOf(visibleRecipes.size()))
                    .replace("{percentage}", String.format("%.1f", percentage));
            lore.add(TextUtils.parseAuto(processedLine));
        }
        builder.lore(lore);
        
        if (customModelData > 0) {
            builder.customModelData(customModelData);
        }
        
        gui.setItem(slot, builder.build());
    }

    /**
     * Handle GUI click events with config-based slots and sounds
     */
    public boolean handleClick(int slot, ItemStack clickedItem) {
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return false;
        }

        int itemsPerPage = config.getItemsPerPage();
        int totalPages = (int) Math.ceil((double) visibleRecipes.size() / itemsPerPage);
        if (totalPages == 0) totalPages = 1;
        
        // Get navigation slots from config
        int prevSlot = config.getNavigationSlot("previous-page");
        int nextSlot = config.getNavigationSlot("next-page");
        int closeSlot = config.getNavigationSlot("close");
        
        // Handle previous page
        if (slot == prevSlot && currentPage > 0) {
            currentPage--;
            player.playSound(player.getLocation(), config.getPageTurnSound(), 1.0f, 1.0f);
            open();
            return true;
        }

        // Handle next page
        if (slot == nextSlot && currentPage < totalPages - 1) {
            currentPage++;
            player.playSound(player.getLocation(), config.getPageTurnSound(), 1.0f, 1.0f);
            open();
            return true;
        }

        // Handle close button
        if (slot == closeSlot) {
            player.playSound(player.getLocation(), config.getCloseSound(), 1.0f, 1.0f);
            player.closeInventory();
            return true;
        }

        // Handle recipe clicks (only if within items per page range)
        if (slot < itemsPerPage) {
            int recipeIndex = currentPage * itemsPerPage + slot;
            if (recipeIndex < visibleRecipes.size()) {
                BrewingRecipe recipe = visibleRecipes.get(recipeIndex);
                
                // Check if recipe is discovered before opening details
                if (plugin.getDiscoveryManager().canAccessRecipe(player, recipe.getId())) {
                    player.playSound(player.getLocation(), config.getClickSound(), 1.0f, 1.0f);
                    openRecipeDetails(recipe);
                } else {
                    // Play locked sound for undiscovered recipes
                    player.playSound(player.getLocation(), config.getLockedSound(), 1.0f, 1.0f);
                }
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
