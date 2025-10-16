package net.pwing.brewmasters.models;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a reward for completing an achievement
 */
public class AchievementReward {

    private final RewardType type;
    private final List<ItemStack> items;
    private final List<String> commands;
    private final int experience;
    private final String permission;
    private final String message;

    private AchievementReward(Builder builder) {
        this.type = builder.type;
        this.items = builder.items;
        this.commands = builder.commands;
        this.experience = builder.experience;
        this.permission = builder.permission;
        this.message = builder.message;
    }

    /**
     * Give the reward to a player
     */
    public void giveReward(Player player) {
        switch (type) {
            case ITEMS:
                giveItems(player);
                break;
            case COMMANDS:
                executeCommands(player);
                break;
            case EXPERIENCE:
                giveExperience(player);
                break;
            case PERMISSION:
                // Permission rewards would need a permission plugin integration
                break;
            case COMBINED:
                giveItems(player);
                executeCommands(player);
                giveExperience(player);
                break;
        }

        if (message != null && !message.isEmpty()) {
            player.sendMessage(message.replace("{player}", player.getName()));
        }
    }

    private void giveItems(Player player) {
        for (ItemStack item : items) {
            if (player.getInventory().firstEmpty() != -1) {
                player.getInventory().addItem(item);
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }
    }

    private void executeCommands(Player player) {
        for (String command : commands) {
            String processedCommand = command.replace("{player}", player.getName());
            player.getServer().dispatchCommand(player.getServer().getConsoleSender(), processedCommand);
        }
    }

    private void giveExperience(Player player) {
        if (experience > 0) {
            player.giveExp(experience);
        }
    }

    /**
     * Load reward from configuration
     */
    public static AchievementReward fromConfig(ConfigurationSection section) {
        Builder builder = new Builder();

        String typeStr = section.getString("type", "ITEMS");
        try {
            builder.type(RewardType.valueOf(typeStr.toUpperCase()));
        } catch (IllegalArgumentException e) {
            builder.type(RewardType.ITEMS);
        }

        // Load items
        if (section.contains("items")) {
            List<ItemStack> items = new ArrayList<>();
            for (Object itemObj : section.getList("items")) {
                if (itemObj instanceof ConfigurationSection) {
                    ConfigurationSection itemSection = (ConfigurationSection) itemObj;
                    ItemStack item = loadItemFromConfig(itemSection);
                    if (item != null) {
                        items.add(item);
                    }
                }
            }
            builder.items(items);
        }

        // Load commands
        builder.commands(section.getStringList("commands"));

        // Load experience
        builder.experience(section.getInt("experience", 0));

        // Load permission
        builder.permission(section.getString("permission"));

        // Load message
        builder.message(section.getString("message"));

        return builder.build();
    }

    private static ItemStack loadItemFromConfig(ConfigurationSection section) {
        String materialStr = section.getString("material");
        Material material = Material.matchMaterial(materialStr);
        if (material == null) {
            return null;
        }

        int amount = section.getInt("amount", 1);
        ItemStack item = new ItemStack(material, amount);

        if (section.contains("name") || section.contains("lore")) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                if (section.contains("name")) {
                    meta.displayName(net.pwing.brewmasters.utils.TextUtils.parseAuto(section.getString("name")));
                }
                if (section.contains("lore")) {
                    List<net.kyori.adventure.text.Component> loreComponents = new ArrayList<>();
                    for (String line : section.getStringList("lore")) {
                        loreComponents.add(net.pwing.brewmasters.utils.TextUtils.parseAuto(line));
                    }
                    meta.lore(loreComponents);
                }
                item.setItemMeta(meta);
            }
        }

        return item;
    }

    /**
     * Reward types
     */
    public enum RewardType {
        ITEMS,      // Give items
        COMMANDS,   // Execute commands
        EXPERIENCE, // Give experience
        PERMISSION, // Grant permission
        COMBINED    // Multiple reward types
    }

    // Getters
    public RewardType getType() { return type; }
    public List<ItemStack> getItems() { return items; }
    public List<String> getCommands() { return commands; }
    public int getExperience() { return experience; }
    public String getPermission() { return permission; }
    public String getMessage() { return message; }

    /**
     * Builder for AchievementReward
     */
    public static class Builder {
        private RewardType type = RewardType.ITEMS;
        private List<ItemStack> items = new ArrayList<>();
        private List<String> commands = new ArrayList<>();
        private int experience = 0;
        private String permission;
        private String message;

        public Builder type(RewardType type) { this.type = type; return this; }
        public Builder items(List<ItemStack> items) { this.items = new ArrayList<>(items); return this; }
        public Builder commands(List<String> commands) { this.commands = new ArrayList<>(commands); return this; }
        public Builder experience(int experience) { this.experience = experience; return this; }
        public Builder permission(String permission) { this.permission = permission; return this; }
        public Builder message(String message) { this.message = message; return this; }

        public AchievementReward build() {
            return new AchievementReward(this);
        }
    }
}
