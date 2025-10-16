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
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * GUI for displaying detailed information about a specific recipe
 */
public class RecipeDetailsGUI {

    private final BrewMasters plugin;
    private final Player player;
    private final BrewingRecipe recipe;
    private final RecipeBookGUI parentGUI;

    public RecipeDetailsGUI(BrewMasters plugin, Player player, BrewingRecipe recipe, RecipeBookGUI parentGUI) {
        this.plugin = plugin;
        this.player = player;
        this.recipe = recipe;
        this.parentGUI = parentGUI;
    }

    /**
     * Open the recipe details GUI
     */
    public void open() {
        Inventory gui = createGUI();
        plugin.getGUIListener().openRecipeDetails(player, this);
        player.openInventory(gui);
    }

    /**
     * Create the GUI inventory
     */
    private Inventory createGUI() {
        String recipeName = recipe.getResultName() != null ? ChatColor.stripColor(recipe.getResultName())
                : recipe.getId();

        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.DARK_BLUE + "Recipe: " + recipeName);

        // Result potion (center)
        ItemStack resultItem = recipe.createResultPotion();
        gui.setItem(22, resultItem);

        // Base potion (left)
        ItemStack baseItem = createIngredientItem(recipe.getBasePotion(), "Base Potion",
                "This is the base potion", "required for this recipe");
        gui.setItem(20, baseItem);

        // Ingredient (right)
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
        gui.setItem(24, ingredientItem);

        // Brewing process visualization
        addBrewingVisualization(gui);

        // Recipe information
        addRecipeInformation(gui);

        // Navigation
        addNavigationItems(gui);

        return gui;
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
     * Add brewing process visualization
     */
    private void addBrewingVisualization(Inventory gui) {
        // Brewing stand
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
        gui.setItem(13, brewingStand);

        // Arrows showing process
        ItemStack arrow1 = createArrow("Step 1", "Place base potion");
        ItemStack arrow2 = createArrow("Step 2", "Add ingredient");
        ItemStack arrow3 = createArrow("Step 3", "Result!");

        gui.setItem(21, arrow1);
        gui.setItem(23, arrow2);
        gui.setItem(31, arrow3);
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
     * Add recipe information panel
     */
    private void addRecipeInformation(Inventory gui) {
        // Effects information
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
            gui.setItem(15, effectsItem);
        }

        // Conditions information
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
            gui.setItem(33, conditionsItem);
        }

        // Recipe type information
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
        gui.setItem(11, typeItem);
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
     * Add navigation items
     */
    private void addNavigationItems(Inventory gui) {
        // Back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.YELLOW + "Back to Recipe Book");
            backMeta.setLore(Arrays.asList(ChatColor.GRAY + "Return to the recipe list"));
            backButton.setItemMeta(backMeta);
        }
        gui.setItem(45, backButton);

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
     * Handle GUI click events
     */
    public boolean handleClick(int slot, ItemStack clickedItem) {
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return false;
        }

        if (slot == 45) {
            // Back button
            parentGUI.open();
            return true;
        }

        if (slot == 49) {
            // Close button
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
