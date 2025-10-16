package net.pwing.brewmasters.models;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a brewing achievement that players can unlock
 */
public class Achievement {

    private final String id;
    private final String name;
    private final String description;
    private final List<String> lore;
    private final AchievementType type;
    private final AchievementTrigger trigger;
    private final int targetValue;
    private final String targetRecipe;
    private final List<String> targetRecipes;
    private final Material icon;
    private final AchievementReward reward;
    private final boolean hidden;

    private Achievement(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.lore = builder.lore;
        this.type = builder.type;
        this.trigger = builder.trigger;
        this.targetValue = builder.targetValue;
        this.targetRecipe = builder.targetRecipe;
        this.targetRecipes = builder.targetRecipes;
        this.icon = builder.icon;
        this.reward = builder.reward;
        this.hidden = builder.hidden;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getLore() {
        return lore;
    }

    public AchievementType getType() {
        return type;
    }

    public AchievementTrigger getTrigger() {
        return trigger;
    }

    public int getTargetValue() {
        return targetValue;
    }

    public String getTargetRecipe() {
        return targetRecipe;
    }

    public List<String> getTargetRecipes() {
        return targetRecipes;
    }

    public Material getIcon() {
        return icon;
    }

    public AchievementReward getReward() {
        return reward;
    }

    public boolean isHidden() {
        return hidden;
    }

    /**
     * Achievement types
     */
    public enum AchievementType {
        MILESTONE, // General milestones
        DISCOVERY, // Recipe discovery related
        BREWING, // Brewing related
        COLLECTION, // Collection related
        SPECIAL // Special achievements
    }

    /**
     * Achievement triggers
     */
    public enum AchievementTrigger {
        RECIPES_DISCOVERED, // Discover X recipes
        POTIONS_BREWED, // Brew X potions
        SPECIFIC_RECIPE_BREWED, // Brew specific recipe X times
        RECIPE_SET_DISCOVERED, // Discover all recipes in a set
        FIRST_BREW, // First time brewing
        FIRST_DISCOVERY, // First recipe discovery
        MASTER_BREWER, // Discover all recipes
        CHAINS_COMPLETED, // Complete X brewing chains
        FIRST_CHAIN // First chain completion
    }

    /**
     * Load achievement from configuration
     */
    public static Achievement fromConfig(String id, ConfigurationSection section) {
        Builder builder = new Builder(id);

        builder.name(section.getString("name", id))
                .description(section.getString("description", ""))
                .lore(section.getStringList("lore"))
                .hidden(section.getBoolean("hidden", false));

        // Parse type
        String typeStr = section.getString("type", "MILESTONE");
        try {
            builder.type(AchievementType.valueOf(typeStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            builder.type(AchievementType.MILESTONE);
        }

        // Parse trigger
        String triggerStr = section.getString("trigger", "POTIONS_BREWED");
        try {
            builder.trigger(AchievementTrigger.valueOf(triggerStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            builder.trigger(AchievementTrigger.POTIONS_BREWED);
        }

        // Parse target values
        builder.targetValue(section.getInt("target-value", 1))
                .targetRecipe(section.getString("target-recipe"))
                .targetRecipes(section.getStringList("target-recipes"));

        // Parse icon
        String iconStr = section.getString("icon", "BREWING_STAND");
        Material icon = Material.matchMaterial(iconStr);
        builder.icon(icon != null ? icon : Material.BREWING_STAND);

        // Parse reward
        ConfigurationSection rewardSection = section.getConfigurationSection("reward");
        if (rewardSection != null) {
            builder.reward(AchievementReward.fromConfig(rewardSection));
        }

        return builder.build();
    }

    /**
     * Builder for Achievement
     */
    public static class Builder {
        private final String id;
        private String name;
        private String description = "";
        private List<String> lore = new ArrayList<>();
        private AchievementType type = AchievementType.MILESTONE;
        private AchievementTrigger trigger = AchievementTrigger.POTIONS_BREWED;
        private int targetValue = 1;
        private String targetRecipe;
        private List<String> targetRecipes = new ArrayList<>();
        private Material icon = Material.BREWING_STAND;
        private AchievementReward reward;
        private boolean hidden = false;

        public Builder(String id) {
            this.id = id;
            this.name = id;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder lore(List<String> lore) {
            this.lore = new ArrayList<>(lore);
            return this;
        }

        public Builder type(AchievementType type) {
            this.type = type;
            return this;
        }

        public Builder trigger(AchievementTrigger trigger) {
            this.trigger = trigger;
            return this;
        }

        public Builder targetValue(int targetValue) {
            this.targetValue = targetValue;
            return this;
        }

        public Builder targetRecipe(String targetRecipe) {
            this.targetRecipe = targetRecipe;
            return this;
        }

        public Builder targetRecipes(List<String> targetRecipes) {
            this.targetRecipes = new ArrayList<>(targetRecipes);
            return this;
        }

        public Builder icon(Material icon) {
            this.icon = icon;
            return this;
        }

        public Builder reward(AchievementReward reward) {
            this.reward = reward;
            return this;
        }

        public Builder hidden(boolean hidden) {
            this.hidden = hidden;
            return this;
        }

        public Achievement build() {
            return new Achievement(this);
        }
    }
}
