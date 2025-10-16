package net.pwing.brewmasters.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorldCondition implements BrewCondition {

    private final Set<String> allowedWorlds;
    private final boolean whitelist;

    public WorldCondition(List<String> worlds, boolean whitelist) {
        this.allowedWorlds = new HashSet<>(worlds);
        this.whitelist = whitelist;
    }

    @Override
    public boolean check(Player player, Location location) {
        String worldName = location.getWorld().getName();

        if (whitelist) {
            // Must be in one of the allowed worlds
            return allowedWorlds.contains(worldName);
        } else {
            // Must NOT be in any of the blocked worlds
            return !allowedWorlds.contains(worldName);
        }
    }

    @Override
    public String getDescription() {
        String mode = whitelist ? "Must be in world: " : "Cannot be in world: ";
        return mode + String.join(", ", allowedWorlds);
    }

    @Override
    public ConditionType getType() {
        return ConditionType.WORLD;
    }
}

