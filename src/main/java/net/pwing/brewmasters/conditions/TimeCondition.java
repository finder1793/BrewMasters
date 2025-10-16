package net.pwing.brewmasters.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TimeCondition implements BrewCondition {

    private final long minTime;
    private final long maxTime;

    public TimeCondition(long minTime, long maxTime) {
        this.minTime = minTime;
        this.maxTime = maxTime;
    }

    @Override
    public boolean check(Player player, Location location) {
        long worldTime = location.getWorld().getTime();

        // Handle wrap-around (e.g., 23000 to 1000)
        if (minTime <= maxTime) {
            return worldTime >= minTime && worldTime <= maxTime;
        } else {
            return worldTime >= minTime || worldTime <= maxTime;
        }
    }

    @Override
    public String getDescription() {
        return "Time must be between " + minTime + " and " + maxTime;
    }

    @Override
    public ConditionType getType() {
        return ConditionType.TIME;
    }
}

