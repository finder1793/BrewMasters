package net.pwing.brewmasters.gui.config;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration for Achievements GUI
 */
public class AchievementsGUIConfig {
    
    private final FileConfiguration config;
    
    public AchievementsGUIConfig(FileConfiguration config) {
        this.config = config;
    }
    
    // ===== GUI Settings =====
    
    public String getTitle() {
        return config.getString("gui.title", "<gradient:gold:yellow>🏆 Achievements</gradient>");
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
    
    // ===== Unlocked Achievement =====
    
    public String getUnlockedName() {
        return config.getString("unlocked.name", "<gradient:gold:yellow>{achievement_name}</gradient>");
    }
    
    public List<String> getUnlockedLore() {
        return config.getStringList("unlocked.lore");
    }
    
    public boolean shouldUnlockedGlow() {
        return config.getBoolean("unlocked.glow", true);
    }
    
    public int getUnlockedCustomModelData() {
        return config.getInt("unlocked.custom-model-data", 0);
    }
    
    // ===== Locked Achievement =====
    
    public String getLockedName() {
        return config.getString("locked.name", "<dark_gray>{achievement_name}");
    }
    
    public List<String> getLockedLore() {
        return config.getStringList("locked.lore");
    }
    
    public boolean shouldLockedGlow() {
        return config.getBoolean("locked.glow", false);
    }
    
    public int getLockedCustomModelData() {
        return config.getInt("locked.custom-model-data", 0);
    }
    
    // ===== Hidden Achievement =====
    
    public Material getHiddenMaterial() {
        String mat = config.getString("hidden.material", "BARRIER");
        try {
            return Material.valueOf(mat);
        } catch (IllegalArgumentException e) {
            return Material.BARRIER;
        }
    }
    
    public String getHiddenName() {
        return config.getString("hidden.name", "<dark_gray>??? Hidden Achievement");
    }
    
    public List<String> getHiddenLore() {
        return config.getStringList("hidden.lore");
    }
    
    public boolean shouldHiddenGlow() {
        return config.getBoolean("hidden.glow", false);
    }
    
    public int getHiddenCustomModelData() {
        return config.getInt("hidden.custom-model-data", 0);
    }
    
    // ===== Visibility =====
    
    public boolean shouldHideLocked() {
        return config.getBoolean("visibility.hide-locked", false);
    }
    
    public boolean shouldHideSecret() {
        return config.getBoolean("visibility.hide-secret", true);
    }
    
    public boolean shouldShowProgressWhenLocked() {
        return config.getBoolean("visibility.show-progress-when-locked", true);
    }
    
    // ===== Navigation =====
    
    public Map<String, Object> getNavigationItem(String key) {
        var navSection = config.getConfigurationSection("navigation." + key);
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
    
    public int getPageItemLimit(int page) {
        return config.getInt("per-page-limits." + page, -1);
    }
    
    public Map<Integer, Integer> getAllPageLimits() {
        Map<Integer, Integer> limits = new HashMap<>();
        
        if (!isPerPageLimitsEnabled()) {
            return limits;
        }
        
        var section = config.getConfigurationSection("per-page-limits");
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
    
    // ===== Achievement Page Assignments =====
    
    public boolean isAchievementPageAssignmentsEnabled() {
        return config.getBoolean("achievement-page-assignments.enabled", false);
    }
    
    public int getAchievementPageAssignment(String achievementId) {
        return config.getInt("achievement-page-assignments." + achievementId, -1);
    }
    
    public Map<String, Integer> getAllAchievementPageAssignments() {
        Map<String, Integer> assignments = new HashMap<>();
        
        if (!isAchievementPageAssignmentsEnabled()) {
            return assignments;
        }
        
        var section = config.getConfigurationSection("achievement-page-assignments");
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
    
    // ===== Achievement Slot Assignments =====
    
    public boolean isAchievementSlotAssignmentsEnabled() {
        return config.getBoolean("achievement-slot-assignments.enabled", false);
    }
    
    public int getAchievementSlotAssignment(String achievementId) {
        return config.getInt("achievement-slot-assignments." + achievementId, -1);
    }
    
    public Map<String, Integer> getAllAchievementSlotAssignments() {
        Map<String, Integer> assignments = new HashMap<>();
        
        if (!isAchievementSlotAssignmentsEnabled()) {
            return assignments;
        }
        
        var section = config.getConfigurationSection("achievement-slot-assignments");
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
