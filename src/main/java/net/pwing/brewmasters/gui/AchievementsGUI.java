package net.pwing.brewmasters.gui;

import net.pwing.brewmasters.BrewMasters;
import net.pwing.brewmasters.models.Achievement;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * GUI for displaying achievements to players
 */
public class AchievementsGUI {

    private final BrewMasters plugin;
    private final Player player;
    private final List<Achievement> achievements;
    private final Set<String> unlockedAchievements;
    private int currentPage;
    private final int achievementsPerPage = 28; // 7x4 grid with navigation

    public AchievementsGUI(BrewMasters plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.achievements = new ArrayList<>(plugin.getAchievementManager().getAllAchievements());
        this.unlockedAchievements = plugin.getAchievementManager().getUnlockedAchievements(player);
        this.currentPage = 0;
        
        // Sort achievements: unlocked first, then by type
        achievements.sort((a1, a2) -> {
            boolean unlocked1 = unlockedAchievements.contains(a1.getId());
            boolean unlocked2 = unlockedAchievements.contains(a2.getId());
            
            if (unlocked1 != unlocked2) {
                return unlocked1 ? -1 : 1; // Unlocked first
            }
            
            // Then by type
            int typeCompare = a1.getType().compareTo(a2.getType());
            if (typeCompare != 0) {
                return typeCompare;
            }
            
            // Finally by name
            return a1.getName().compareToIgnoreCase(a2.getName());
        });
    }

    /**
     * Open the achievements GUI
     */
    public void open() {
        Inventory gui = createGUI();
        player.openInventory(gui);
    }

    /**
     * Create the GUI inventory
     */
    private Inventory createGUI() {
        int totalPages = (int) Math.ceil((double) achievements.size() / achievementsPerPage);
        String title = ChatColor.GOLD + "Achievements " + ChatColor.GRAY + "(" + (currentPage + 1) + "/" + Math.max(1, totalPages) + ")";
        
        Inventory gui = Bukkit.createInventory(null, 54, title);

        // Add achievements for current page
        int startIndex = currentPage * achievementsPerPage;
        int endIndex = Math.min(startIndex + achievementsPerPage, achievements.size());

        for (int i = startIndex; i < endIndex; i++) {
            Achievement achievement = achievements.get(i);
            ItemStack achievementItem = createAchievementItem(achievement);
            gui.setItem(i - startIndex, achievementItem);
        }

        // Add navigation items
        addNavigationItems(gui, totalPages);

        // Add info item
        addInfoItem(gui);

        return gui;
    }

    /**
     * Create an item representing an achievement
     */
    private ItemStack createAchievementItem(Achievement achievement) {
        boolean isUnlocked = unlockedAchievements.contains(achievement.getId());
        
        // Use achievement icon or default based on unlock status
        Material iconMaterial = isUnlocked ? achievement.getIcon() : Material.GRAY_DYE;
        ItemStack item = new ItemStack(iconMaterial);
        ItemMeta meta = item.getItemMeta();
        
        if (meta != null) {
            // Set name with unlock status
            String nameColor = isUnlocked ? ChatColor.GOLD.toString() : ChatColor.GRAY.toString();
            String unlockIndicator = isUnlocked ? "🏆 " : "🔒 ";
            meta.setDisplayName(nameColor + unlockIndicator + ChatColor.stripColor(achievement.getName()));
            
            List<String> lore = new ArrayList<>();
            
            // Description
            lore.add(ChatColor.GRAY + achievement.getDescription());
            lore.add("");
            
            // Progress information
            if (isUnlocked) {
                lore.add(ChatColor.GREEN + "✓ Unlocked!");
            } else {
                // Show progress if applicable
                int progress = plugin.getAchievementManager().getAchievementProgress(player, achievement);
                int target = achievement.getTargetValue();
                
                if (target > 1) {
                    lore.add(ChatColor.YELLOW + "Progress: " + progress + "/" + target);
                    
                    // Progress bar
                    int barLength = 20;
                    int filledBars = Math.min(barLength, (int) ((double) progress / target * barLength));
                    StringBuilder progressBar = new StringBuilder(ChatColor.GREEN.toString());
                    
                    for (int i = 0; i < filledBars; i++) {
                        progressBar.append("█");
                    }
                    progressBar.append(ChatColor.GRAY);
                    for (int i = filledBars; i < barLength; i++) {
                        progressBar.append("█");
                    }
                    
                    lore.add(progressBar.toString());
                } else {
                    lore.add(ChatColor.RED + "Not unlocked");
                }
            }
            
            lore.add("");
            
            // Achievement type and trigger info
            lore.add(ChatColor.DARK_GRAY + "Type: " + formatEnumName(achievement.getType().name()));
            
            if (!achievement.isHidden() || isUnlocked) {
                lore.add(ChatColor.DARK_GRAY + "Requirement: " + getRequirementDescription(achievement));
            }
            
            // Reward information
            if (achievement.getReward() != null && (isUnlocked || !achievement.isHidden())) {
                lore.add("");
                lore.add(ChatColor.YELLOW + "Reward:");
                
                switch (achievement.getReward().getType()) {
                    case EXPERIENCE:
                        lore.add(ChatColor.GREEN + "• " + achievement.getReward().getExperience() + " Experience");
                        break;
                    case ITEMS:
                        lore.add(ChatColor.GREEN + "• " + achievement.getReward().getItems().size() + " Items");
                        break;
                    case COMMANDS:
                        lore.add(ChatColor.GREEN + "• Special Reward");
                        break;
                    case COMBINED:
                        lore.add(ChatColor.GREEN + "• Multiple Rewards");
                        break;
                }
            }
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        
        return item;
    }

    /**
     * Get requirement description for an achievement
     */
    private String getRequirementDescription(Achievement achievement) {
        switch (achievement.getTrigger()) {
            case FIRST_DISCOVERY:
                return "Discover your first recipe";
            case FIRST_BREW:
                return "Brew your first potion";
            case RECIPES_DISCOVERED:
                return "Discover " + achievement.getTargetValue() + " recipes";
            case POTIONS_BREWED:
                return "Brew " + achievement.getTargetValue() + " potions";
            case SPECIFIC_RECIPE_BREWED:
                return "Brew " + achievement.getTargetRecipe() + " " + achievement.getTargetValue() + " times";
            case RECIPE_SET_DISCOVERED:
                return "Discover specific recipe set";
            case MASTER_BREWER:
                return "Discover all recipes";
            default:
                return "Complete special task";
        }
    }

    /**
     * Format enum name for display
     */
    private String formatEnumName(String enumName) {
        return Arrays.stream(enumName.split("_"))
                .map(word -> word.charAt(0) + word.substring(1).toLowerCase())
                .reduce((a, b) -> a + " " + b)
                .orElse(enumName);
    }

    /**
     * Add navigation items to the GUI
     */
    private void addNavigationItems(Inventory gui, int totalPages) {
        // Previous page button
        if (currentPage > 0) {
            ItemStack prevButton = new ItemStack(Material.ARROW);
            ItemMeta prevMeta = prevButton.getItemMeta();
            if (prevMeta != null) {
                prevMeta.setDisplayName(ChatColor.YELLOW + "Previous Page");
                prevMeta.setLore(Arrays.asList(ChatColor.GRAY + "Go to page " + currentPage));
                prevButton.setItemMeta(prevMeta);
            }
            gui.setItem(45, prevButton);
        }

        // Next page button
        if (currentPage < totalPages - 1) {
            ItemStack nextButton = new ItemStack(Material.ARROW);
            ItemMeta nextMeta = nextButton.getItemMeta();
            if (nextMeta != null) {
                nextMeta.setDisplayName(ChatColor.YELLOW + "Next Page");
                nextMeta.setLore(Arrays.asList(ChatColor.GRAY + "Go to page " + (currentPage + 2)));
                nextButton.setItemMeta(nextMeta);
            }
            gui.setItem(53, nextButton);
        }

        // Close button
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName(ChatColor.RED + "Close");
            closeMeta.setLore(Arrays.asList(ChatColor.GRAY + "Close achievements"));
            closeButton.setItemMeta(closeMeta);
        }
        gui.setItem(49, closeButton);
    }

    /**
     * Add info item to the GUI
     */
    private void addInfoItem(Inventory gui) {
        ItemStack infoItem = new ItemStack(Material.NETHER_STAR);
        ItemMeta infoMeta = infoItem.getItemMeta();
        if (infoMeta != null) {
            infoMeta.setDisplayName(ChatColor.GOLD + "Achievement Progress");
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Unlocked: " + ChatColor.YELLOW + unlockedAchievements.size());
            lore.add(ChatColor.GRAY + "Total: " + ChatColor.YELLOW + achievements.size());
            
            double percentage = achievements.isEmpty() ? 0 : 
                (double) unlockedAchievements.size() / achievements.size() * 100;
            lore.add(ChatColor.GRAY + "Completion: " + ChatColor.YELLOW + String.format("%.1f%%", percentage));
            
            lore.add("");
            lore.add(ChatColor.GRAY + "Keep brewing to unlock more!");
            
            infoMeta.setLore(lore);
            infoItem.setItemMeta(infoMeta);
        }
        gui.setItem(4, infoItem);
    }

    /**
     * Handle GUI click events
     */
    public boolean handleClick(int slot, ItemStack clickedItem) {
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return false;
        }

        // Handle navigation
        if (slot == 45 && currentPage > 0) {
            // Previous page
            currentPage--;
            open();
            return true;
        }

        if (slot == 53 && currentPage < Math.ceil((double) achievements.size() / achievementsPerPage) - 1) {
            // Next page
            currentPage++;
            open();
            return true;
        }

        if (slot == 49) {
            // Close button
            player.closeInventory();
            return true;
        }

        return false;
    }
}
