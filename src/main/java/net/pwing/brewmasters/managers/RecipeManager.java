package net.pwing.brewmasters.managers;

import net.pwing.brewmasters.BrewMasters;
import net.pwing.brewmasters.conditions.*;
import net.pwing.brewmasters.ingredients.BrewingIngredient;
import net.pwing.brewmasters.ingredients.IngredientFactory;
import net.pwing.brewmasters.models.BrewingRecipe;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class RecipeManager {

    private final BrewMasters plugin;
    private final Map<String, BrewingRecipe> recipes;
    // Index recipes by base potion for faster lookups
    private final Map<Material, List<BrewingRecipe>> recipesByBase;

    public RecipeManager(BrewMasters plugin) {
        this.plugin = plugin;
        this.recipes = new HashMap<>();
        this.recipesByBase = new HashMap<>();
    }

    public void loadRecipes() {
        recipes.clear();
        recipesByBase.clear();

        ConfigurationSection recipesSection = plugin.getConfig().getConfigurationSection("recipes");
        if (recipesSection == null) {
            plugin.getLogger().warning("No recipes found in config.yml");
            return;
        }

        for (String recipeId : recipesSection.getKeys(false)) {
            try {
                BrewingRecipe recipe = loadRecipe(recipeId, recipesSection.getConfigurationSection(recipeId));
                if (recipe != null) {
                    recipes.put(recipeId, recipe);
                    // Index by base potion
                    recipesByBase.computeIfAbsent(recipe.getBasePotion(), k -> new ArrayList<>()).add(recipe);
                    plugin.getLogger().info("Loaded recipe: " + recipeId);
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to load recipe '" + recipeId + "': " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private BrewingRecipe loadRecipe(String id, ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        // Parse base potion
        String basePotionStr = section.getString("base-potion");
        Material basePotion = Material.matchMaterial(basePotionStr);
        if (basePotion == null) {
            plugin.getLogger().warning("Invalid base potion material: " + basePotionStr);
            return null;
        }

        // Parse ingredient
        String ingredientStr = section.getString("ingredient");
        BrewingIngredient ingredient;
        try {
            ingredient = IngredientFactory.fromString(ingredientStr);
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid ingredient: " + ingredientStr + " - " + e.getMessage());
            return null;
        }

        BrewingRecipe.Builder builder = new BrewingRecipe.Builder(id)
                .basePotion(basePotion)
                .ingredient(ingredient);

        // Parse result name
        if (section.contains("result.name")) {
            builder.resultName(section.getString("result.name"));
        }

        // Parse result lore
        if (section.contains("result.lore")) {
            builder.resultLore(section.getStringList("result.lore"));
        }

        // Parse color
        if (section.contains("result.color")) {
            String colorStr = section.getString("result.color");
            Color color = parseColor(colorStr);
            if (color != null) {
                builder.color(color);
            }
        }

        // Parse effects
        if (section.contains("result.effects")) {
            List<Map<?, ?>> effectsList = section.getMapList("result.effects");
            for (Map<?, ?> effectMap : effectsList) {
                PotionEffect effect = parseEffect(effectMap);
                if (effect != null) {
                    builder.addEffect(effect);
                }
            }
        }

        // Parse brew time
        if (section.contains("brew-time")) {
            builder.brewTime(section.getInt("brew-time"));
        }

        // Parse potion type
        if (section.contains("result.potion-type")) {
            String typeStr = section.getString("result.potion-type").toUpperCase();
            try {
                BrewingRecipe.PotionType type = BrewingRecipe.PotionType.valueOf(typeStr);
                builder.potionType(type);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid potion type: " + typeStr + ", using NORMAL");
            }
        }

        // Parse glowing effect
        if (section.contains("result.glowing")) {
            builder.glowing(section.getBoolean("result.glowing"));
        }

        // Parse custom model data
        if (section.contains("result.custom-model-data")) {
            builder.customModelData(section.getInt("result.custom-model-data"));
        }
        
        // Parse drink commands
        if (section.contains("drink-commands")) {
            builder.drinkCommands(section.getStringList("drink-commands"));
        }
        
        // Parse expire commands
        if (section.contains("expire-commands")) {
            builder.expireCommands(section.getStringList("expire-commands"));
        }

        // Parse conditions
        if (section.contains("conditions")) {
            ConfigurationSection conditionsSection = section.getConfigurationSection("conditions");
            if (conditionsSection != null) {
                parseConditions(conditionsSection, builder);
            }
        }

        return builder.build();
    }

    private void parseConditions(ConfigurationSection section, BrewingRecipe.Builder builder) {
        // Biome conditions
        if (section.contains("biomes")) {
            List<String> biomes = section.getStringList("biomes");
            boolean whitelist = section.getBoolean("biomes-whitelist", true);
            builder.addCondition(new BiomeCondition(biomes, whitelist));
        }

        // World conditions
        if (section.contains("worlds")) {
            List<String> worlds = section.getStringList("worlds");
            boolean whitelist = section.getBoolean("worlds-whitelist", true);
            builder.addCondition(new WorldCondition(worlds, whitelist));
        }

        // Permission condition
        if (section.contains("permission")) {
            String permission = section.getString("permission");
            boolean required = section.getBoolean("permission-required", true);
            builder.addCondition(new PermissionCondition(permission, required));
        }

        // Time condition
        if (section.contains("time")) {
            ConfigurationSection timeSection = section.getConfigurationSection("time");
            if (timeSection != null) {
                long minTime = timeSection.getLong("min", 0);
                long maxTime = timeSection.getLong("max", 24000);
                builder.addCondition(new TimeCondition(minTime, maxTime));
            }
        }

        // Weather condition
        if (section.contains("weather")) {
            String weatherStr = section.getString("weather").toUpperCase();
            try {
                WeatherCondition.WeatherType weather = WeatherCondition.WeatherType.valueOf(weatherStr);
                builder.addCondition(new WeatherCondition(weather));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid weather type: " + weatherStr);
            }
        }

        // Y-level condition
        if (section.contains("y-level")) {
            ConfigurationSection ySection = section.getConfigurationSection("y-level");
            if (ySection != null) {
                int minY = ySection.getInt("min", -64);
                int maxY = ySection.getInt("max", 320);
                builder.addCondition(new YLevelCondition(minY, maxY));
            }
        }

        // PlaceholderAPI conditions
        if (section.contains("placeholders")) {
            List<Map<?, ?>> placeholders = section.getMapList("placeholders");
            for (Map<?, ?> placeholderMap : placeholders) {
                String placeholder = (String) placeholderMap.get("placeholder");
                String operator = (String) placeholderMap.get("operator");
                String value = String.valueOf(placeholderMap.get("value"));

                if (placeholder != null && operator != null && value != null) {
                    builder.addCondition(new PlaceholderCondition(placeholder, operator, value));
                }
            }
        }
    }

    private Color parseColor(String colorStr) {
        if (colorStr == null) {
            return null;
        }

        // Support hex format (#RRGGBB)
        if (colorStr.startsWith("#")) {
            try {
                int rgb = Integer.parseInt(colorStr.substring(1), 16);
                return Color.fromRGB(rgb);
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Invalid hex color: " + colorStr);
                return null;
            }
        }

        // Support RGB format (r,g,b)
        if (colorStr.contains(",")) {
            String[] parts = colorStr.split(",");
            if (parts.length == 3) {
                try {
                    int r = Integer.parseInt(parts[0].trim());
                    int g = Integer.parseInt(parts[1].trim());
                    int b = Integer.parseInt(parts[2].trim());
                    return Color.fromRGB(r, g, b);
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Invalid RGB color: " + colorStr);
                    return null;
                }
            }
        }

        return null;
    }

    private PotionEffect parseEffect(Map<?, ?> effectMap) {
        String typeStr = (String) effectMap.get("type");
        PotionEffectType type = PotionEffectType.getByName(typeStr);

        if (type == null) {
            plugin.getLogger().warning("Invalid potion effect type: " + typeStr);
            return null;
        }

        int duration = effectMap.containsKey("duration") ? (int) effectMap.get("duration") : 600;
        int amplifier = effectMap.containsKey("amplifier") ? (int) effectMap.get("amplifier") : 0;

        return new PotionEffect(type, duration, amplifier);
    }

    public BrewingRecipe findRecipe(Material basePotion, Material ingredient) {
        // Use indexed lookup for O(1) base potion matching
        List<BrewingRecipe> candidates = recipesByBase.get(basePotion);
        if (candidates == null) {
            return null;
        }
        
        for (BrewingRecipe recipe : candidates) {
            if (recipe.matches(basePotion, ingredient)) {
                return recipe;
            }
        }
        return null;
    }

    /**
     * Find a recipe that matches the given base potion and ingredient ItemStack
     * @param basePotion The base potion material
     * @param ingredientItem The ingredient ItemStack
     * @return The matching recipe, or null if none found
     */
    public BrewingRecipe findRecipe(Material basePotion, ItemStack ingredientItem) {
        // Use indexed lookup for O(1) base potion matching
        List<BrewingRecipe> candidates = recipesByBase.get(basePotion);
        if (candidates == null) {
            return null;
        }
        
        for (BrewingRecipe recipe : candidates) {
            if (recipe.matches(basePotion, ingredientItem)) {
                return recipe;
            }
        }
        return null;
    }

    public BrewingRecipe getRecipe(String recipeId) {
        return recipes.get(recipeId);
    }

    public Collection<BrewingRecipe> getAllRecipes() {
        return recipes.values();
    }

    public int getRecipeCount() {
        return recipes.size();
    }
}
