package net.pwing.brewmasters.models;

import net.pwing.brewmasters.conditions.BrewCondition;
import net.pwing.brewmasters.ingredients.BrewingIngredient;
import net.pwing.brewmasters.ingredients.VanillaIngredient;
import net.pwing.brewmasters.utils.ColorUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class BrewingRecipe {

    private final String id;
    private final Material basePotion;
    private final BrewingIngredient ingredient;
    private final String resultName;
    private final List<String> resultLore;
    private final Color color;
    private final List<PotionEffect> effects;
    private final int brewTime;
    private final PotionType potionType;
    private final boolean glowing;
    private final int customModelData;
    private final List<BrewCondition> conditions;

    private BrewingRecipe(Builder builder) {
        this.id = builder.id;
        this.basePotion = builder.basePotion;
        this.ingredient = builder.ingredient;
        this.resultName = builder.resultName;
        this.resultLore = builder.resultLore;
        this.color = builder.color;
        this.effects = builder.effects;
        this.brewTime = builder.brewTime;
        this.potionType = builder.potionType;
        this.glowing = builder.glowing;
        this.customModelData = builder.customModelData;
        this.conditions = builder.conditions;
    }

    public String getId() {
        return id;
    }

    public Material getBasePotion() {
        return basePotion;
    }

    public BrewingIngredient getIngredient() {
        return ingredient;
    }

    /**
     * Get the ingredient as a Material (for backwards compatibility)
     * @return The ingredient material, or null if not a vanilla ingredient
     */
    public Material getIngredientMaterial() {
        if (ingredient instanceof VanillaIngredient) {
            return ((VanillaIngredient) ingredient).getMaterial();
        }
        return null;
    }

    public String getResultName() {
        return resultName;
    }

    public List<String> getResultLore() {
        return resultLore;
    }

    public Color getColor() {
        return color;
    }

    public List<PotionEffect> getEffects() {
        return effects;
    }

    public int getBrewTime() {
        return brewTime;
    }

    public PotionType getPotionType() {
        return potionType;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public List<BrewCondition> getConditions() {
        return conditions;
    }

    /**
     * Check if all conditions are met for this recipe
     * 
     * @param player   The player brewing (can be null)
     * @param location The location of the brewing stand
     * @return true if all conditions are met
     */
    public boolean checkConditions(Player player, Location location) {
        if (conditions.isEmpty()) {
            return true;
        }

        for (BrewCondition condition : conditions) {
            if (!condition.check(player, location)) {
                return false;
            }
        }

        return true;
    }

    public ItemStack createResultPotion() {
        // Determine the material based on potion type
        Material potionMaterial;
        switch (potionType) {
            case SPLASH:
                potionMaterial = Material.SPLASH_POTION;
                break;
            case LINGERING:
                potionMaterial = Material.LINGERING_POTION;
                break;
            case NORMAL:
            default:
                potionMaterial = Material.POTION;
                break;
        }

        ItemStack potion = new ItemStack(potionMaterial);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();

        if (meta != null) {
            if (resultName != null && !resultName.isEmpty()) {
                meta.setDisplayName(ColorUtils.translate(resultName));
            }

            if (resultLore != null && !resultLore.isEmpty()) {
                meta.setLore(ColorUtils.translate(resultLore));
            }

            if (color != null) {
                meta.setColor(color);
            }

            for (PotionEffect effect : effects) {
                meta.addCustomEffect(effect, true);
            }

            if (customModelData > 0) {
                meta.setCustomModelData(customModelData);
            }

            potion.setItemMeta(meta);
        }

        // Add glowing effect if enabled
        if (glowing) {
            potion.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.LUCK, 1);
            ItemStack finalPotion = potion;
            org.bukkit.inventory.meta.ItemMeta itemMeta = finalPotion.getItemMeta();
            if (itemMeta != null) {
                itemMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
                finalPotion.setItemMeta(itemMeta);
            }
        }

        return potion;
    }

    public boolean matches(Material base, Material ingr) {
        if (this.basePotion != base) {
            return false;
        }

        // For backwards compatibility with vanilla ingredients
        if (ingredient instanceof VanillaIngredient) {
            return ((VanillaIngredient) ingredient).getMaterial() == ingr;
        }

        return false;
    }

    /**
     * Check if this recipe matches the given base potion and ingredient ItemStack
     * @param base The base potion material
     * @param ingredientItem The ingredient ItemStack
     * @return true if the recipe matches
     */
    public boolean matches(Material base, ItemStack ingredientItem) {
        if (this.basePotion != base) {
            return false;
        }

        return ingredient.matches(ingredientItem);
    }

    public enum PotionType {
        NORMAL,
        SPLASH,
        LINGERING
    }

    public static class Builder {
        private String id;
        private Material basePotion;
        private BrewingIngredient ingredient;
        private String resultName;
        private List<String> resultLore = new ArrayList<>();
        private Color color;
        private List<PotionEffect> effects = new ArrayList<>();
        private int brewTime = 400; // Default brewing time (20 seconds)
        private PotionType potionType = PotionType.NORMAL;
        private boolean glowing = false;
        private int customModelData = 0;
        private List<BrewCondition> conditions = new ArrayList<>();

        public Builder(String id) {
            this.id = id;
        }

        public Builder basePotion(Material basePotion) {
            this.basePotion = basePotion;
            return this;
        }

        public Builder ingredient(Material ingredient) {
            this.ingredient = new VanillaIngredient(ingredient, 1);
            return this;
        }

        public Builder ingredient(BrewingIngredient ingredient) {
            this.ingredient = ingredient;
            return this;
        }

        public Builder resultName(String resultName) {
            this.resultName = resultName;
            return this;
        }

        public Builder resultLore(List<String> resultLore) {
            this.resultLore = resultLore;
            return this;
        }

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder color(int r, int g, int b) {
            this.color = Color.fromRGB(r, g, b);
            return this;
        }

        public Builder addEffect(PotionEffect effect) {
            this.effects.add(effect);
            return this;
        }

        public Builder addEffect(PotionEffectType type, int duration, int amplifier) {
            this.effects.add(new PotionEffect(type, duration, amplifier));
            return this;
        }

        public Builder effects(List<PotionEffect> effects) {
            this.effects = effects;
            return this;
        }

        public Builder brewTime(int brewTime) {
            this.brewTime = brewTime;
            return this;
        }

        public Builder potionType(PotionType potionType) {
            this.potionType = potionType;
            return this;
        }

        public Builder glowing(boolean glowing) {
            this.glowing = glowing;
            return this;
        }

        public Builder customModelData(int customModelData) {
            this.customModelData = customModelData;
            return this;
        }

        public Builder addCondition(BrewCondition condition) {
            this.conditions.add(condition);
            return this;
        }

        public Builder conditions(List<BrewCondition> conditions) {
            this.conditions = conditions;
            return this;
        }

        public BrewingRecipe build() {
            if (basePotion == null || ingredient == null) {
                throw new IllegalStateException("Base potion and ingredient must be set");
            }
            return new BrewingRecipe(this);
        }
    }
}
