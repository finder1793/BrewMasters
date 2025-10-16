package net.pwing.brewmasters.gui.config;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration for Recipe Book GUI
 */
public class RecipeBookGUIConfig {
    
    private final FileConfiguration config;
    
    public RecipeBookGUIConfig(FileConfiguration config) {
        this.config = config;
    }
    
    // ===== GUI Settings =====
    
    public String getTitle() {
        return config.getString("gui.title", "<gradient:aqua:blue>📖 Recipe Book</gradient>");
    }
    
    public int getSize() {
        return config.getInt("gui.size", 54);
    }
    
    public int getItemsPerPage() {
        return config.getInt("gui.items-per-page", 28);
    }
    
    // ===== Filler =====
    
    public boolean isFillerEnabled() {
        return config.getBoolean("filler.enabled", true);
    }
    
    public Material getFillerMaterial() {
        String mat = config.getString("filler.material", "GRAY_STAINED_GLASS_PANE");
        try {
            return Material.valueOf(mat);
        } catch (IllegalArgumentException e) {
            return Material.GRAY_STAINED_GLASS_PANE;
        }
    }
    
    public String getFillerName() {
        return config.getString("filler.name", " ");
    }
    
    public int getFillerCustomModelData() {
        return config.getInt("filler.custom-model-data", 0);
    }
    
    // ===== Discovered Recipe =====
    
    public String getDiscoveredRecipeName() {
        return config.getString("discovered-recipe.name", "<gradient:green:lime>{recipe_name}</gradient>");
    }
    
    public List<String> getDiscoveredRecipeLore() {
        return config.getStringList("discovered-recipe.lore");
    }
    
    public boolean shouldDiscoveredRecipeGlow() {
        return config.getBoolean("discovered-recipe.glow", true);
    }
    
    // ===== Locked Recipe =====
    
    public Material getLockedRecipeMaterial() {
        String mat = config.getString("locked-recipe.material", "BARRIER");
        try {
            return Material.valueOf(mat);
        } catch (IllegalArgumentException e) {
            return Material.BARRIER;
        }
    }
    
    public String getLockedRecipeName() {
        return config.getString("locked-recipe.name", "<dark_gray>??? Locked Recipe");
    }
    
    public List<String> getLockedRecipeLore() {
        return config.getStringList("locked-recipe.lore");
    }
    
    public int getLockedRecipeCustomModelData() {
        return config.getInt("locked-recipe.custom-model-data", 0);
    }
    
    public boolean shouldLockedRecipeGlow() {
        return config.getBoolean("locked-recipe.glow", false);
    }
    
    // ===== Permission Locked Recipe =====
    
    public Material getPermissionLockedMaterial() {
        String mat = config.getString("permission-locked-recipe.material", "IRON_BARS");
        try {
            return Material.valueOf(mat);
        } catch (IllegalArgumentException e) {
            return Material.IRON_BARS;
        }
    }
    
    public String getPermissionLockedName() {
        return config.getString("permission-locked-recipe.name", "<red>🔒 Permission Required");
    }
    
    public List<String> getPermissionLockedLore() {
        return config.getStringList("permission-locked-recipe.lore");
    }
    
    public int getPermissionLockedCustomModelData() {
        return config.getInt("permission-locked-recipe.custom-model-data", 0);
    }
    
    public boolean shouldPermissionLockedGlow() {
        return config.getBoolean("permission-locked-recipe.glow", false);
    }
    
    // ===== Condition Locked Recipe =====
    
    public Material getConditionLockedMaterial() {
        String mat = config.getString("condition-locked-recipe.material", "REDSTONE");
        try {
            return Material.valueOf(mat);
        } catch (IllegalArgumentException e) {
            return Material.REDSTONE;
        }
    }
    
    public String getConditionLockedName() {
        return config.getString("condition-locked-recipe.name", "<red>⚠ Conditions Not Met");
    }
    
    public List<String> getConditionLockedLore() {
        return config.getStringList("condition-locked-recipe.lore");
    }
    
    public int getConditionLockedCustomModelData() {
        return config.getInt("condition-locked-recipe.custom-model-data", 0);
    }
    
    public boolean shouldConditionLockedGlow() {
        return config.getBoolean("condition-locked-recipe.glow", false);
    }
    
    // ===== Visibility =====
    
    public boolean shouldHideUndiscovered() {
        return config.getBoolean("visibility.hide-undiscovered", false);
    }
    
    public boolean shouldHideNoPermission() {
        return config.getBoolean("visibility.hide-no-permission", false);
    }
    
    public boolean shouldHideUnmetConditions() {
        return config.getBoolean("visibility.hide-unmet-conditions", false);
    }
    
    public boolean shouldShowLockedPlaceholders() {
        return config.getBoolean("visibility.show-locked-placeholders", true);
    }
    
    // ===== Navigation =====
    
    public Map<String, Object> getNavigationItem(String key) {
        ConfigurationSection navSection = config.getConfigurationSection("navigation." + key);
        if (navSection == null) return new HashMap<>();
        
        Map<String, Object> data = new HashMap<>();
        data.put("slot", navSection.getInt("slot", 0));
        data.put("material", navSection.getString("material", "ARROW"));
        data.put("name", navSection.getString("name", ""));
        data.put("lore", navSection.getStringList("lore"));
        data.put("custom-model-data", navSection.getInt("custom-model-data", 0));
        data.put("enabled", navSection.getBoolean("enabled", true));
        data.put("permission", navSection.getString("permission", ""));
        
        return data;
    }
    
    public int getNavigationSlot(String key) {
        return config.getInt("navigation." + key + ".slot", 0);
    }
    
    // ===== Sounds =====
    
    public Sound getOpenSound() {
        String sound = config.getString("sounds.open", "BLOCK_CHEST_OPEN");
        try {
            return Sound.valueOf(sound);
        } catch (IllegalArgumentException e) {
            return Sound.BLOCK_CHEST_OPEN;
        }
    }
    
    public Sound getCloseSound() {
        String sound = config.getString("sounds.close", "BLOCK_CHEST_CLOSE");
        try {
            return Sound.valueOf(sound);
        } catch (IllegalArgumentException e) {
            return Sound.BLOCK_CHEST_CLOSE;
        }
    }
    
    public Sound getClickSound() {
        String sound = config.getString("sounds.click", "UI_BUTTON_CLICK");
        try {
            return Sound.valueOf(sound);
        } catch (IllegalArgumentException e) {
            return Sound.UI_BUTTON_CLICK;
        }
    }
    
    public Sound getPageTurnSound() {
        String sound = config.getString("sounds.page-turn", "ITEM_BOOK_PAGE_TURN");
        try {
            return Sound.valueOf(sound);
        } catch (IllegalArgumentException e) {
            return Sound.ITEM_BOOK_PAGE_TURN;
        }
    }
    
    public Sound getLockedSound() {
        String sound = config.getString("sounds.locked", "ENTITY_VILLAGER_NO");
        try {
            return Sound.valueOf(sound);
        } catch (IllegalArgumentException e) {
            return Sound.ENTITY_VILLAGER_NO;
        }
    }
    
    // ===== Page Permissions =====
    
    public boolean isPagePermissionsEnabled() {
        return config.getBoolean("page-permissions.enabled", false);
    }
    
    public String getPagePermission(int page) {
        return config.getString("page-permissions." + page, null);
    }
    
    // ===== Per-Page Limits =====
    
    public boolean isPerPageLimitsEnabled() {
        return config.getBoolean("per-page-limits.enabled", false);
    }
    
    /**
     * Get the item limit for a specific page
     * @param page The page number (1-indexed)
     * @return The max items for this page, or -1 to use default
     */
    public int getPageItemLimit(int page) {
        return config.getInt("per-page-limits." + page, -1);
    }
    
    /**
     * Get all per-page limits
     * @return Map of page number to max items
     */
    public Map<Integer, Integer> getAllPageLimits() {
        Map<Integer, Integer> limits = new HashMap<>();
        
        if (!isPerPageLimitsEnabled()) {
            return limits;
        }
        
        ConfigurationSection section = config.getConfigurationSection("per-page-limits");
        if (section == null) {
            return limits;
        }
        
        for (String key : section.getKeys(false)) {
            if (!key.equals("enabled")) {
                try {
                    int page = Integer.parseInt(key);
                    int limit = section.getInt(key, -1);
                    if (limit > 0) {
                        limits.put(page, limit);
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid page numbers
                }
            }
        }
        
        return limits;
    }
    
    // ===== Recipe Page Assignments =====
    
    public boolean isRecipePageAssignmentsEnabled() {
        return config.getBoolean("recipe-page-assignments.enabled", false);
    }
    
    /**
     * Get the assigned page for a specific recipe
     * @param recipeId The recipe ID
     * @return The page number (1-indexed), or -1 if not assigned
     */
    public int getRecipePageAssignment(String recipeId) {
        return config.getInt("recipe-page-assignments." + recipeId, -1);
    }
    
    /**
     * Get all recipe page assignments
     * @return Map of recipe ID to page number
     */
    public Map<String, Integer> getAllRecipePageAssignments() {
        Map<String, Integer> assignments = new HashMap<>();
        
        if (!isRecipePageAssignmentsEnabled()) {
            return assignments;
        }
        
        ConfigurationSection section = config.getConfigurationSection("recipe-page-assignments");
        if (section == null) {
            return assignments;
        }
        
        for (String key : section.getKeys(false)) {
            if (!key.equals("enabled")) {
                int page = section.getInt(key, -1);
                if (page > 0) {
                    assignments.put(key, page);
                }
            }
        }
        
        return assignments;
    }
    
    // ===== Recipe Slot Assignments =====
    
    public boolean isRecipeSlotAssignmentsEnabled() {
        return config.getBoolean("recipe-slot-assignments.enabled", false);
    }
    
    /**
     * Get the assigned slot for a specific recipe
     * @param recipeId The recipe ID
     * @return The slot number (0-53), or -1 if not assigned
     */
    public int getRecipeSlotAssignment(String recipeId) {
        return config.getInt("recipe-slot-assignments." + recipeId, -1);
    }
    
    /**
     * Get all recipe slot assignments
     * @return Map of recipe ID to slot number
     */
    public Map<String, Integer> getAllRecipeSlotAssignments() {
        Map<String, Integer> assignments = new HashMap<>();
        
        if (!isRecipeSlotAssignmentsEnabled()) {
            return assignments;
        }
        
        ConfigurationSection section = config.getConfigurationSection("recipe-slot-assignments");
        if (section == null) {
            return assignments;
        }
        
        for (String key : section.getKeys(false)) {
            if (!key.equals("enabled")) {
                int slot = section.getInt(key, -1);
                if (slot >= 0 && slot < 54) {
                    assignments.put(key, slot);
                }
            }
        }
        
        return assignments;
    }
}
