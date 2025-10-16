package net.pwing.brewmasters.conditions;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlaceholderCondition implements BrewCondition {

    private final String placeholder;
    private final String operator;
    private final String value;
    private final boolean placeholderAPIAvailable;

    public PlaceholderCondition(String placeholder, String operator, String value) {
        this.placeholder = placeholder;
        this.operator = operator;
        this.value = value;
        this.placeholderAPIAvailable = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    @Override
    public boolean check(Player player, Location location) {
        if (!placeholderAPIAvailable) {
            return false;
        }

        if (player == null) {
            return false;
        }

        String parsedValue = PlaceholderAPI.setPlaceholders(player, placeholder);

        switch (operator.toLowerCase()) {
            case "equals":
            case "==":
            case "=":
                return parsedValue.equalsIgnoreCase(value);

            case "not_equals":
            case "!=":
                return !parsedValue.equalsIgnoreCase(value);

            case "contains":
                return parsedValue.toLowerCase().contains(value.toLowerCase());

            case "not_contains":
                return !parsedValue.toLowerCase().contains(value.toLowerCase());

            case "greater_than":
            case ">":
                try {
                    double parsed = Double.parseDouble(parsedValue);
                    double target = Double.parseDouble(value);
                    return parsed > target;
                } catch (NumberFormatException e) {
                    return false;
                }

            case "less_than":
            case "<":
                try {
                    double parsed = Double.parseDouble(parsedValue);
                    double target = Double.parseDouble(value);
                    return parsed < target;
                } catch (NumberFormatException e) {
                    return false;
                }

            case "greater_or_equal":
            case ">=":
                try {
                    double parsed = Double.parseDouble(parsedValue);
                    double target = Double.parseDouble(value);
                    return parsed >= target;
                } catch (NumberFormatException e) {
                    return false;
                }

            case "less_or_equal":
            case "<=":
                try {
                    double parsed = Double.parseDouble(parsedValue);
                    double target = Double.parseDouble(value);
                    return parsed <= target;
                } catch (NumberFormatException e) {
                    return false;
                }

            case "starts_with":
                return parsedValue.toLowerCase().startsWith(value.toLowerCase());

            case "ends_with":
                return parsedValue.toLowerCase().endsWith(value.toLowerCase());

            default:
                return false;
        }
    }

    @Override
    public String getDescription() {
        return "Placeholder " + placeholder + " " + operator + " " + value;
    }

    @Override
    public ConditionType getType() {
        return ConditionType.PLACEHOLDER;
    }
}

