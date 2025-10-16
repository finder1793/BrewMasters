package net.pwing.brewmasters.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Interface for recipe conditions that must be met for brewing to succeed
 */
public interface BrewCondition {

    /**
     * Check if the condition is met
     * @param player The player brewing (can be null for hoppers)
     * @param location The location of the brewing stand
     * @return true if the condition is met
     */
    boolean check(Player player, Location location);

    /**
     * Get a description of this condition for error messages
     * @return A human-readable description
     */
    String getDescription();

    /**
     * Get the type of this condition
     * @return The condition type
     */
    ConditionType getType();

    enum ConditionType {
        BIOME,
        WORLD,
        PERMISSION,
        TIME,
        WEATHER,
        Y_LEVEL,
        PLACEHOLDER,
        CUSTOM
    }
}

