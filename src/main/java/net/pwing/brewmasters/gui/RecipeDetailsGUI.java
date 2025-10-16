package net.pwing.brewmasters.gui;

import net.kyori.adventure.text.Component;
import net.pwing.brewmasters.BrewMasters;
import net.pwing.brewmasters.gui.config.RecipeDetailsGUIConfig;
import net.pwing.brewmasters.models.BrewingRecipe;
import net.pwing.brewmasters.utils.InventoryUtils;
import net.pwing.brewmasters.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * GUI for displaying detailed information about a specific recipe
 * Fully configurable with MiniMessage support and custom layouts
 */
@SuppressWarnings("deprecation")
public class RecipeDetailsGUI {

    private final BrewMasters plugin;
    private final Player player;
    private final BrewingRecipe recipe;
    private final RecipeBookGUI parentGUI;
    private final RecipeDetailsGUIConfig config;

    public RecipeDetailsGUI(BrewMasters plugin, Player player, BrewingRecipe recipe, RecipeBookGUI parentGUI) {
        this.plugin = plugin;
        this.player = player;
        this.recipe = recipe;
        this.parentGUI = parentGUI;
        this.config = plugin.getGUIConfigManager().getRecipeDetailsConfig();
    }

    /**
     * Open the recipe details GUI
     */
    public void open() {
        Inventory gui = createGUI();
        plugin.getGUIListener().openRecipeDetails(player, this);
        player.openInventory(gui);
        
        // Play open sound
        player.playSound(player.getLocation(), config.getOpenSound(), 1.0f, 1.0f);
    }

    /**
     * Create the GUI inventory with config-based layout
     */
    private Inventory createGUI() {
        // Get recipe name for title
        String recipeName = recipe.getResultName() != null ? TextUtils.stripColor(recipe.getResultName())
                : recipe.getId();

        // Create title with placeholder
        String titleText = config.getTitle().replace("{recipe_name}", recipeName);
        Component titleComponent = TextUtils.parseAuto(titleText);
        
        // Create inventory with configured size
        Inventory gui = InventoryUtils.createInventory(config.getSize(), titleComponent);
        
        // Add filler items if enabled
        if (config.isFillerEnabled()) {
            addFillerItems(gui);
        }

        // Result potion (configured slot)
        ItemStack resultItem = recipe.createResultPotion();
        gui.setItem(config.getResultSlot(), resultItem);

        // Base potion (configured slot)
        ItemStack baseItem = createIngredientItem(recipe.getBasePotion(), "Base Potion",
                "This is the base potion", "required for this recipe");
        gui.setItem(config.getBaseSlot(), baseItem);

        // Ingredient (configured slot)
        ItemStack ingredientItem = recipe.getIngredient().getExampleItem();
        if (ingredientItem != null) {
            ItemMeta meta = ingredientItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.GREEN + "Ingredient");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "This ingredient is added");
                lore.add(ChatColor.GRAY + "to the base potion");
                meta.setLore(lore);
                ingredientItem.setItemMeta(meta);
            }
        } else {
            // Fallback if ingredient can't provide example item
            ingredientItem = new ItemStack(Material.BARRIER);
            ItemMeta meta = ingredientItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(ChatColor.RED + "Unknown Ingredient");
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + recipe.getIngredient().getDisplayName());
                meta.setLore(lore);
                ingredientItem.setItemMeta(meta);
            }
        }
        gui.setItem(config.getIngredientSlot(), ingredientItem);

        // Brewing process visualization (uses configured slots)
        addBrewingVisualization(gui);

        // Recipe information (uses configured slots)
        addRecipeInformation(gui);

        // Navigation (uses configured slots)
        addNavigationItems(gui);

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
     * Create an ingredient item with description
     */
    private ItemStack createIngredientItem(Material material, String title, String... description) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(ChatColor.YELLOW + title);

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + formatMaterialName(material));
            lore.add("");

            for (String line : description) {
                lore.add(ChatColor.GRAY + line);
            }

            meta.setLore(lore);
            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Add brewing process visualization using configured slots
     */
    private void addBrewingVisualization(Inventory gui) {
        // Brewing stand at configured slot
        ItemStack brewingStand = new ItemStack(Material.BREWING_STAND);
        ItemMeta standMeta = brewingStand.getItemMeta();
        if (standMeta != null) {
            standMeta.setDisplayName(ChatColor.GOLD + "Brewing Process");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "1. Place base potion in brewing stand");
            lore.add(ChatColor.GRAY + "2. Add ingredient to top slot");
            lore.add(ChatColor.GRAY + "3. Add blaze powder as fuel");
            lore.add(ChatColor.GRAY + "4. Wait for brewing to complete");

            if (recipe.getBrewTime() != 400) {
                lore.add("");
                lore.add(ChatColor.YELLOW + "Brew Time: " + (recipe.getBrewTime() / 20) + " seconds");
            }

            standMeta.setLore(lore);
            brewingStand.setItemMeta(standMeta);
        }
        gui.setItem(config.getBrewingStandSlot(), brewingStand);

        // Arrows showing process at configured slots
        ItemStack arrow1 = createArrow("Step 1", "Place base potion");
        ItemStack arrow2 = createArrow("Step 2", "Add ingredient");
        ItemStack arrow3 = createArrow("Step 3", "Result!");

        gui.setItem(config.getArrow1Slot(), arrow1);
        gui.setItem(config.getArrow2Slot(), arrow2);
        gui.setItem(config.getArrow3Slot(), arrow3);
    }

    /**
     * Create an arrow item for process visualization
     */
    private ItemStack createArrow(String title, String description) {
        ItemStack arrow = new ItemStack(Material.ARROW);
        ItemMeta meta = arrow.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GREEN + title);
            meta.setLore(Arrays.asList(ChatColor.GRAY + description));
            arrow.setItemMeta(meta);
        }
        return arrow;
    }

    /**
     * Add recipe information panel using configured slots
     */
    private void addRecipeInformation(Inventory gui) {
        // Effects information at configured slot
        if (!recipe.getEffects().isEmpty()) {
            ItemStack effectsItem = new ItemStack(Material.GLOWSTONE_DUST);
            ItemMeta effectsMeta = effectsItem.getItemMeta();
            if (effectsMeta != null) {
                effectsMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Potion Effects");

                List<String> lore = new ArrayList<>();
                for (PotionEffect effect : recipe.getEffects()) {
                    String effectName = formatEffectName(effect.getType().getName());
                    int duration = effect.getDuration() / 20; // Convert to seconds
                    int level = effect.getAmplifier() + 1;

                    lore.add(ChatColor.GRAY + "• " + ChatColor.WHITE + effectName +
                            " " + level + " (" + formatDuration(duration) + ")");
                }

                effectsMeta.setLore(lore);
                effectsItem.setItemMeta(effectsMeta);
            }
            gui.setItem(config.getEffectsSlot(), effectsItem);
        }

        // Conditions information at configured slot
        if (!recipe.getConditions().isEmpty()) {
            ItemStack conditionsItem = new ItemStack(Material.REDSTONE);
            ItemMeta conditionsMeta = conditionsItem.getItemMeta();
            if (conditionsMeta != null) {
                conditionsMeta.setDisplayName(ChatColor.RED + "Brewing Conditions");

                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "This recipe requires:");
                recipe.getConditions()
                        .forEach(condition -> lore.add(ChatColor.GRAY + "• " + condition.getDescription()));

                conditionsMeta.setLore(lore);
                conditionsItem.setItemMeta(conditionsMeta);
            }
            gui.setItem(config.getConditionsSlot(), conditionsItem);
        }

        // Recipe type information at configured slot
        ItemStack typeItem = new ItemStack(getTypeIcon());
        ItemMeta typeMeta = typeItem.getItemMeta();
        if (typeMeta != null) {
            typeMeta.setDisplayName(ChatColor.AQUA + "Recipe Type");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Type: " + ChatColor.WHITE + recipe.getPotionType().name());

            if (recipe.isGlowing()) {
                lore.add(ChatColor.YELLOW + "✨ Glowing Effect");
            }

            if (recipe.getCustomModelData() > 0) {
                lore.add(ChatColor.GRAY + "Custom Model: " + recipe.getCustomModelData());
            }

            typeMeta.setLore(lore);
            typeItem.setItemMeta(typeMeta);
        }
        gui.setItem(config.getTypeInfoSlot(), typeItem);
    }

    /**
     * Get icon for recipe type
     */
    private Material getTypeIcon() {
        switch (recipe.getPotionType()) {
            case SPLASH:
                return Material.SPLASH_POTION;
            case LINGERING:
                return Material.LINGERING_POTION;
            default:
                return Material.POTION;
        }
    }

    /**
     * Add navigation items using config
     */
    private void addNavigationItems(Inventory gui) {
        // Back button
        Map<String, Object> backData = config.getNavigationItem("back");
        int backSlot = (int) backData.getOrDefault("slot", 45);
        Material backMaterial = Material.valueOf((String) backData.getOrDefault("material", "ARROW"));
        String backName = (String) backData.getOrDefault("name", "<yellow>Back to Recipe Book</yellow>");
        @SuppressWarnings("unchecked")
        List<String> backLoreLines = (List<String>) backData.getOrDefault("lore", new ArrayList<>());
        int backCustomModelData = (int) backData.getOrDefault("custom-model-data", 0);
        
        InventoryUtils.ItemBuilder backBuilder = InventoryUtils.builder(backMaterial)
                .displayNameMini(backName);
        
        List<Component> backLore = new ArrayList<>();
        for (String line : backLoreLines) {
            backLore.add(TextUtils.parseAuto(line));
        }
        backBuilder.lore(backLore);
        
        if (backCustomModelData > 0) {
            backBuilder.customModelData(backCustomModelData);
        }
        
        gui.setItem(backSlot, backBuilder.build());

        // Close button
        Map<String, Object> closeData = config.getNavigationItem("close");
        int closeSlot = (int) closeData.getOrDefault("slot", 49);
        Material closeMaterial = Material.valueOf((String) closeData.getOrDefault("material", "BARRIER"));
        String closeName = (String) closeData.getOrDefault("name", "<red>Close</red>");
        @SuppressWarnings("unchecked")
        List<String> closeLoreLines = (List<String>) closeData.getOrDefault("lore", new ArrayList<>());
        int closeCustomModelData = (int) closeData.getOrDefault("custom-model-data", 0);
        
        InventoryUtils.ItemBuilder closeBuilder = InventoryUtils.builder(closeMaterial)
                .displayNameMini(closeName);
        
        List<Component> closeLore = new ArrayList<>();
        for (String line : closeLoreLines) {
            closeLore.add(TextUtils.parseAuto(line));
        }
        closeBuilder.lore(closeLore);
        
        if (closeCustomModelData > 0) {
            closeBuilder.customModelData(closeCustomModelData);
        }
        
        gui.setItem(closeSlot, closeBuilder.build());
    }

    /**
     * Handle GUI click events with config slots and sounds
     */
    public boolean handleClick(int slot, ItemStack clickedItem) {
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return false;
        }

        // Get navigation slots from config
        int backSlot = config.getNavigationSlot("back");
        int closeSlot = config.getNavigationSlot("close");

        if (slot == backSlot) {
            // Back button - return to recipe book
            player.playSound(player.getLocation(), config.getBackSound(), 1.0f, 1.0f);
            parentGUI.open();
            return true;
        }

        if (slot == closeSlot) {
            // Close button
            player.playSound(player.getLocation(), config.getCloseSound(), 1.0f, 1.0f);
            player.closeInventory();
            return true;
        }

        return false;
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
     * Format effect name for display
     */
    private String formatEffectName(String effectName) {
        return Arrays.stream(effectName.split("_"))
                .map(word -> word.charAt(0) + word.substring(1).toLowerCase())
                .reduce((a, b) -> a + " " + b)
                .orElse(effectName);
    }

    /**
     * Format duration for display
     */
    private String formatDuration(int seconds) {
        if (seconds < 60) {
            return seconds + "s";
        } else {
            int minutes = seconds / 60;
            int remainingSeconds = seconds % 60;
            return minutes + "m" + (remainingSeconds > 0 ? " " + remainingSeconds + "s" : "");
        }
    }
}
