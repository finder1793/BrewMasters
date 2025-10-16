package net.pwing.brewmasters.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PermissionCondition implements BrewCondition {

    private final String permission;
    private final boolean required;

    public PermissionCondition(String permission, boolean required) {
        this.permission = permission;
        this.required = required;
    }

    @Override
    public boolean check(Player player, Location location) {
        if (player == null) {
            // Hopper brewing or no player - allow if permission not required
            return !required;
        }

        if (required) {
            return player.hasPermission(permission);
        } else {
            return !player.hasPermission(permission);
        }
    }

    @Override
    public String getDescription() {
        return required ? "Requires permission: " + permission : "Must not have permission: " + permission;
    }

    @Override
    public ConditionType getType() {
        return ConditionType.PERMISSION;
    }
}

