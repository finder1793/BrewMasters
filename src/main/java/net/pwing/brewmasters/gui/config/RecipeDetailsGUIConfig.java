package net.pwing.brewmasters.gui.config;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration for Recipe Details GUI
 */
public class RecipeDetailsGUIConfig {
    
    private final FileConfiguration config;
    
    public RecipeDetailsGUIConfig(FileConfiguration config) {
        this.config = config;
    }
    
    // ===== GUI Settings =====
    
    public String getTitle() {
        return config.getString("gui.title", "<gradient:dark_blue:blue>Recipe: {recipe_name}</gradient>");
    }
    
    public int getSize() {
        return config.getInt("gui.size", 54);
    }
    
    // ===== Filler =====
    
    public boolean isFillerEnabled() {
        return config.getBoolean("filler.enabled", true);
    }
    
    public Material getFillerMaterial() {
        String mat = config.getString("filler.material", "BLACK_STAINED_GLASS_PANE");
        try {
            return Material.valueOf(mat);
        } catch (IllegalArgumentException e) {
            return Material.BLACK_STAINED_GLASS_PANE;
        }
    }
    
    public String getFillerName() {
        return config.getString("filler.name", " ");
    }
    
    public int getFillerCustomModelData() {
        return config.getInt("filler.custom-model-data", 0);
    }
    
    // ===== Layout Slots =====
    
    public int getResultSlot() {
        return config.getInt("layout.result-slot", 22);
    }
    
    public int getBaseSlot() {
        return config.getInt("layout.base-slot", 20);
    }
    
    public int getIngredientSlot() {
        return config.getInt("layout.ingredient-slot", 24);
    }
    
    public int getBrewingStandSlot() {
        return config.getInt("layout.brewing-stand-slot", 13);
    }
    
    public int getArrow1Slot() {
        return config.getInt("layout.arrow-1-slot", 21);
    }
    
    public int getArrow2Slot() {
        return config.getInt("layout.arrow-2-slot", 23);
    }
    
    public int getArrow3Slot() {
        return config.getInt("layout.arrow-3-slot", 31);
    }
    
    public int getEffectsSlot() {
        return config.getInt("layout.effects-slot", 15);
    }
    
    public int getConditionsSlot() {
        return config.getInt("layout.conditions-slot", 33);
    }
    
    public int getTypeInfoSlot() {
        return config.getInt("layout.type-info-slot", 11);
    }
    
    // ===== Panel Configurations =====
    
    public ConfigurationSection getBasePotionConfig() {
        return config.getConfigurationSection("base-potion");
    }
    
    public ConfigurationSection getIngredientConfig() {
        return config.getConfigurationSection("ingredient");
    }
    
    public ConfigurationSection getBrewingStandConfig() {
        return config.getConfigurationSection("brewing-stand");
    }
    
    public ConfigurationSection getProcessArrowsConfig() {
        return config.getConfigurationSection("process-arrows");
    }
    
    public ConfigurationSection getEffectsPanelConfig() {
        return config.getConfigurationSection("effects-panel");
    }
    
    public ConfigurationSection getConditionsPanelConfig() {
        return config.getConfigurationSection("conditions-panel");
    }
    
    public ConfigurationSection getTypePanelConfig() {
        return config.getConfigurationSection("type-panel");
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
    
    public Sound getBackSound() {
        String sound = config.getString("sounds.back", "ITEM_BOOK_PAGE_TURN");
        try {
            return Sound.valueOf(sound);
        } catch (IllegalArgumentException e) {
            return Sound.ITEM_BOOK_PAGE_TURN;
        }
    }
}
