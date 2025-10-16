package net.pwing.brewmasters.conditions;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WeatherCondition implements BrewCondition {

    private final WeatherType requiredWeather;

    public WeatherCondition(WeatherType requiredWeather) {
        this.requiredWeather = requiredWeather;
    }

    @Override
    public boolean check(Player player, Location location) {
        boolean isRaining = location.getWorld().hasStorm();
        boolean isThundering = location.getWorld().isThundering();

        switch (requiredWeather) {
            case CLEAR:
                return !isRaining && !isThundering;
            case RAIN:
                return isRaining && !isThundering;
            case THUNDER:
                return isThundering;
            case ANY_STORM:
                return isRaining || isThundering;
            default:
                return true;
        }
    }

    @Override
    public String getDescription() {
        return "Weather must be: " + requiredWeather.name();
    }

    @Override
    public ConditionType getType() {
        return ConditionType.WEATHER;
    }

    public enum WeatherType {
        CLEAR,
        RAIN,
        THUNDER,
        ANY_STORM
    }
}

