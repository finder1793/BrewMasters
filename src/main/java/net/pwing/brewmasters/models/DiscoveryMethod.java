package net.pwing.brewmasters.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a method for discovering a recipe
 */
public class DiscoveryMethod {

    private final Type type;
    private final String material;
    private final String mobType;
    private final String permission;
    private final List<String> biomes;
    private final List<String> requiredRecipes;
    private final int amount;

    private DiscoveryMethod(Builder builder) {
        this.type = builder.type;
        this.material = builder.material;
        this.mobType = builder.mobType;
        this.permission = builder.permission;
        this.biomes = builder.biomes;
        this.requiredRecipes = builder.requiredRecipes;
        this.amount = builder.amount;
    }

    public Type getType() {
        return type;
    }

    public String getMaterial() {
        return material;
    }

    public String getMobType() {
        return mobType;
    }

    public String getPermission() {
        return permission;
    }

    public List<String> getBiomes() {
        return biomes;
    }

    public List<String> getRequiredRecipes() {
        return requiredRecipes;
    }

    public int getAmount() {
        return amount;
    }

    /**
     * Types of discovery methods
     */
    public enum Type {
        AUTOMATIC,      // Automatically discovered
        BIOME_VISIT,    // Visit specific biomes
        ITEM_CRAFT,     // Craft specific items
        ITEM_OBTAIN,    // Obtain specific items
        KILL_MOB,       // Kill specific mobs
        LEVEL_REACH,    // Reach specific level
        PERMISSION,     // Have specific permission
        RECIPE_BREW     // Brew other recipes first
    }

    /**
     * Builder for DiscoveryMethod
     */
    public static class Builder {
        private final Type type;
        private String material;
        private String mobType;
        private String permission;
        private List<String> biomes = new ArrayList<>();
        private List<String> requiredRecipes = new ArrayList<>();
        private int amount = 1;

        public Builder(Type type) {
            this.type = type;
        }

        public Builder material(String material) {
            this.material = material;
            return this;
        }

        public Builder mobType(String mobType) {
            this.mobType = mobType;
            return this;
        }

        public Builder permission(String permission) {
            this.permission = permission;
            return this;
        }

        public Builder biomes(List<String> biomes) {
            this.biomes = new ArrayList<>(biomes);
            return this;
        }

        public Builder requiredRecipes(List<String> requiredRecipes) {
            this.requiredRecipes = new ArrayList<>(requiredRecipes);
            return this;
        }

        public Builder amount(int amount) {
            this.amount = amount;
            return this;
        }

        public DiscoveryMethod build() {
            return new DiscoveryMethod(this);
        }
    }

    /**
     * Get a human-readable description of this discovery method
     */
    public String getDescription() {
        switch (type) {
            case AUTOMATIC:
                return "Automatically discovered";
                
            case BIOME_VISIT:
                return "Visit " + String.join(", ", biomes);
                
            case ITEM_CRAFT:
                return "Craft " + amount + "x " + material;
                
            case ITEM_OBTAIN:
                return "Obtain " + amount + "x " + material;
                
            case KILL_MOB:
                return "Kill " + amount + "x " + mobType;
                
            case LEVEL_REACH:
                return "Reach level " + amount;
                
            case PERMISSION:
                return "Have permission: " + permission;
                
            case RECIPE_BREW:
                String recipeText = requiredRecipes.size() == 1 ? 
                    requiredRecipes.get(0) : 
                    requiredRecipes.size() + " recipes";
                return "Brew " + recipeText + " " + amount + " times";
                
            default:
                return "Unknown discovery method";
        }
    }
}
