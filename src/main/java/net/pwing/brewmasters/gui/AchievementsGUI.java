package net.pwing.brewmasters.gui;

import net.kyori.adventure.text.Component;
import net.pwing.brewmasters.BrewMasters;
import net.pwing.brewmasters.gui.config.AchievementsGUIConfig;
import net.pwing.brewmasters.models.Achievement;
import net.pwing.brewmasters.models.Achievement.AchievementType;
import net.pwing.brewmasters.utils.InventoryUtils;
import net.pwing.brewmasters.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

/**
 * GUI for displaying achievements to players
 * Fully configurable with MiniMessage support and visibility filtering
 */
@SuppressWarnings("deprecation")
public class AchievementsGUI {

    private final BrewMasters plugin;
    private final Player player;
    private final AchievementsGUIConfig config;
    private final List<Achievement> visibleAchievements;
    private final Set<String> unlockedAchievements;
    private int currentPage;
    private final Map<AchievementType, String> formattedTypeCache = new HashMap<>();

    public AchievementsGUI(BrewMasters plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.config = plugin.getGUIConfigManager().getAchievementsConfig();
        this.unlockedAchievements = plugin.getAchievementManager().getUnlockedAchievements(player);
        this.visibleAchievements = new ArrayList<>();
        this.currentPage = 0;
        
        loadAchievements();
    }
    
    /**
     * Load and filter achievements based on visibility settings
     */
    private void loadAchievements() {
        List<Achievement> allAchievements = new ArrayList<>(plugin.getAchievementManager().getAllAchievements());
        
        for (Achievement achievement : allAchievements) {
            // Check visibility settings
            boolean isUnlocked = unlockedAchievements.contains(achievement.getId());
            
            // Hide locked achievements if configured
            if (!isUnlocked && config.shouldHideLocked()) {
                continue;
            }
            
            // Hide secret achievements if configured and not unlocked
            if (achievement.isHidden() && !isUnlocked && config.shouldHideSecret()) {
                continue;
            }
            
            visibleAchievements.add(achievement);
        }
        
        // Sort achievements: unlocked first, then by type, then by name
        visibleAchievements.sort((a1, a2) -> {
            boolean unlocked1 = unlockedAchievements.contains(a1.getId());
            boolean unlocked2 = unlockedAchievements.contains(a2.getId());
            
            if (unlocked1 != unlocked2) {
                return unlocked1 ? -1 : 1; // Unlocked first
            }
            
            // Then by type
            int typeCompare = a1.getType().compareTo(a2.getType());
            if (typeCompare != 0) {
                return typeCompare;
            }
            
            // Finally by name
            return a1.getName().compareToIgnoreCase(a2.getName());
        });
    }

    /**
     * Open the achievements GUI
     */
    public void open() {
        Inventory gui = createGUI();
        player.openInventory(gui);
        
        // Play open sound
        player.playSound(player.getLocation(), config.getOpenSound(), 1.0f, 1.0f);
    }

    /**
     * Create the GUI inventory with config-based approach
     */
    private Inventory createGUI() {
        // Calculate pagination
        int itemsPerPage = config.getItemsPerPage();
        int totalPages = (int) Math.ceil((double) visibleAchievements.size() / itemsPerPage);
        if (totalPages == 0) totalPages = 1;
        
        // Create title with placeholders
        String titleText = config.getTitle()
                .replace("{current_page}", String.valueOf(currentPage + 1))
                .replace("{total_pages}", String.valueOf(totalPages));
        Component titleComponent = TextUtils.parseAuto(titleText);
        
        // Create inventory with configured size
        Inventory gui = InventoryUtils.createInventory(config.getSize(), titleComponent);
        
        // Add filler items if enabled
        if (config.isFillerEnabled()) {
            addFillerItems(gui);
        }

        // Add achievements for current page
        populateAchievements(gui);

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
        
        // Fill all slots initially
        for (int i = 0; i < gui.getSize(); i++) {
            gui.setItem(i, filler);
        }
    }
    
    /**
     * Populate the GUI with achievement items based on current page
     */
    private void populateAchievements(Inventory gui) {
        int itemsPerPage = config.getItemsPerPage();
        int startIndex = currentPage * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, visibleAchievements.size());
        
        // Add visible achievements for this page
        int slot = 0;
        for (int i = startIndex; i < endIndex; i++) {
            Achievement achievement = visibleAchievements.get(i);
            ItemStack achievementItem = createAchievementItem(achievement);
            gui.setItem(slot, achievementItem);
            slot++;
        }
    }

    /**
     * Create an item representing an achievement with proper state handling
     */
    private ItemStack createAchievementItem(Achievement achievement) {
        boolean isUnlocked = unlockedAchievements.contains(achievement.getId());
        boolean isHidden = achievement.isHidden();
        
        // Determine achievement state and create appropriate item
        if (isUnlocked) {
            return createUnlockedAchievementItem(achievement);
        } else if (isHidden) {
            return createHiddenAchievementItem(achievement);
        } else {
            return createLockedAchievementItem(achievement);
        }
    }
    
    /**
     * Create item for an unlocked achievement
     */
    private ItemStack createUnlockedAchievementItem(Achievement achievement) {
        // Unlocked achievements use the achievement's icon
        Material material = achievement.getIcon();
        String name = config.getUnlockedName();
        List<String> loreLines = config.getUnlockedLore();
        int customModelData = config.getUnlockedCustomModelData();
        
        // Build item with config values
        InventoryUtils.ItemBuilder builder = InventoryUtils.builder(material)
                .displayNameMini(name
                        .replace("{achievement_name}", achievement.getName())
                        .replace("{achievement_type}", formatType(achievement.getType())));
        
        // Add lore with placeholders
        List<Component> lore = new ArrayList<>();
        for (String line : loreLines) {
            String processedLine = line
                    .replace("{achievement_name}", achievement.getName())
                    .replace("{achievement_description}", achievement.getDescription())
                    .replace("{achievement_type}", formatType(achievement.getType()))
                    .replace("{reward}", achievement.getReward() != null ? achievement.getReward().toString() : "None");
            lore.add(TextUtils.parseAuto(processedLine));
        }
        builder.lore(lore);
        
        // Add custom model data if set
        if (customModelData > 0) {
            builder.customModelData(customModelData);
        }
        
        ItemStack item = builder.build();
        return applyGlow(item, config.shouldUnlockedGlow());
    }
    
    /**
     * Create item for a locked achievement
     */
    private ItemStack createLockedAchievementItem(Achievement achievement) {
        // Locked achievements use a default gray dye icon
        Material material = Material.GRAY_DYE;
        String name = config.getLockedName();
        List<String> loreLines = config.getLockedLore();
        int customModelData = config.getLockedCustomModelData();
        
        // Build item with config values
        InventoryUtils.ItemBuilder builder = InventoryUtils.builder(material)
                .displayNameMini(name
                        .replace("{achievement_name}", achievement.getName())
                        .replace("{achievement_type}", formatType(achievement.getType())));
        
        // Add lore with placeholders
        List<Component> lore = new ArrayList<>();
        for (String line : loreLines) {
            String processedLine = line
                    .replace("{achievement_name}", achievement.getName())
                    .replace("{achievement_description}", achievement.getDescription())
                    .replace("{achievement_type}", formatType(achievement.getType()));
            lore.add(TextUtils.parseAuto(processedLine));
        }
        builder.lore(lore);
        
        // Add custom model data if set
        if (customModelData > 0) {
            builder.customModelData(customModelData);
        }
        
        ItemStack item = builder.build();
        return applyGlow(item, config.shouldLockedGlow());
    }
    
    /**
     * Create item for a hidden achievement
     */
    private ItemStack createHiddenAchievementItem(Achievement achievement) {
        Material material = config.getHiddenMaterial();
        String name = config.getHiddenName();
        List<String> loreLines = config.getHiddenLore();
        int customModelData = config.getHiddenCustomModelData();
        
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
        return applyGlow(item, config.shouldHiddenGlow());
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
     * Format achievement type for display (cached)
     */
    private String formatType(AchievementType type) {
        return formattedTypeCache.computeIfAbsent(type, t -> {
            String[] parts = t.name().split("_");
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
        Material material = Material.valueOf((String) infoData.getOrDefault("material", "NETHER_STAR"));
        String name = (String) infoData.getOrDefault("name", "<gold>Achievement Progress</gold>");
        @SuppressWarnings("unchecked")
        List<String> loreLines = (List<String>) infoData.getOrDefault("lore", new ArrayList<>());
        int customModelData = (int) infoData.getOrDefault("custom-model-data", 0);
        
        // Calculate statistics
        int totalAchievements = plugin.getAchievementManager().getAllAchievements().size();
        int unlockedCount = unlockedAchievements.size();
        int visibleCount = visibleAchievements.size();
        double percentage = totalAchievements > 0 ? (double) unlockedCount / totalAchievements * 100 : 0;
        
        // Build item with placeholders
        InventoryUtils.ItemBuilder builder = InventoryUtils.builder(material)
                .displayNameMini(name);
        
        List<Component> lore = new ArrayList<>();
        for (String line : loreLines) {
            String processedLine = line
                    .replace("{unlocked}", String.valueOf(unlockedCount))
                    .replace("{total}", String.valueOf(totalAchievements))
                    .replace("{visible}", String.valueOf(visibleCount))
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
        int totalPages = (int) Math.ceil((double) visibleAchievements.size() / itemsPerPage);
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

        return false;
    }
}
