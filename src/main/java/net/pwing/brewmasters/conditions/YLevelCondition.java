package net.pwing.brewmasters.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class YLevelCondition implements BrewCondition {

    private final int minY;
    private final int maxY;

    public YLevelCondition(int minY, int maxY) {
        this.minY = minY;
        this.maxY = maxY;
    }

    @Override
    public boolean check(Player player, Location location) {
        int y = location.getBlockY();
        return y >= minY && y <= maxY;
    }

    @Override
    public String getDescription() {
        return "Y level must be between " + minY + " and " + maxY;
    }

    @Override
    public ConditionType getType() {
        return ConditionType.Y_LEVEL;
    }
}

