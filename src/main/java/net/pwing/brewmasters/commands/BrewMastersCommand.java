package net.pwing.brewmasters.commands;

import net.pwing.brewmasters.BrewMasters;
import net.pwing.brewmasters.models.BrewingRecipe;
import net.pwing.brewmasters.models.BrewingChain;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class BrewMastersCommand implements CommandExecutor, TabCompleter {

    private final BrewMasters plugin;

    public BrewMastersCommand(BrewMasters plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (!sender.hasPermission("brewmasters.reload")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                plugin.reload();
                sender.sendMessage(ChatColor.GREEN + "BrewMasters configuration reloaded! Loaded " +
                        plugin.getRecipeManager().getRecipeCount() + " recipes.");
                return true;

            case "list":
                if (!sender.hasPermission("brewmasters.list")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                listRecipes(sender);
                return true;

            case "recipes":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
                    return true;
                }
                if (args.length > 1 && args[1].equalsIgnoreCase("gui")) {
                    plugin.getGUIListener().openRecipeBook((Player) sender);
                } else {
                    listDiscoveredRecipes((Player) sender);
                }
                return true;

            case "discover":
                if (!sender.hasPermission("brewmasters.admin")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /brewmasters discover <player> <recipe>");
                    return true;
                }
                handleDiscoverCommand(sender, args[1], args[2]);
                return true;

            case "achievements":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
                    return true;
                }
                if (args.length > 1 && args[1].equalsIgnoreCase("gui")) {
                    plugin.getGUIListener().openAchievements((Player) sender);
                } else {
                    listAchievements((Player) sender);
                }
                return true;

            case "speed":
                if (!sender.hasPermission("brewmasters.speed")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
                    return true;
                }
                handleSpeedCommand(sender, args);
                return true;

            case "chains":
                return handleChainsCommand(sender, args);

            case "chain":
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /brewmasters chain <chainId>");
                    return true;
                }
                return handleChainCommand(sender, args);

            case "help":
            default:
                sendHelp(sender);
                return true;
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== BrewMasters Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/brewmasters reload" + ChatColor.WHITE + " - Reload the configuration");
        sender.sendMessage(ChatColor.YELLOW + "/brewmasters list" + ChatColor.WHITE + " - List all custom recipes");
        sender.sendMessage(
                ChatColor.YELLOW + "/brewmasters recipes [gui]" + ChatColor.WHITE + " - View your discovered recipes");
        sender.sendMessage(
                ChatColor.YELLOW + "/brewmasters achievements [gui]" + ChatColor.WHITE + " - View your achievements");
        sender.sendMessage(ChatColor.YELLOW + "/brewmasters discover <player> <recipe>" + ChatColor.WHITE
                + " - Force discover a recipe (admin)");
        sender.sendMessage(ChatColor.YELLOW + "/brewmasters speed <set|remove|info> [multiplier]" + ChatColor.WHITE
                + " - Manage brewing stand speeds (admin)");
        sender.sendMessage(
                ChatColor.YELLOW + "/brewmasters chains" + ChatColor.WHITE + " - List available brewing chains");
        sender.sendMessage(ChatColor.YELLOW + "/brewmasters chain <chainId>" + ChatColor.WHITE
                + " - View chain details and progress");
        sender.sendMessage(ChatColor.YELLOW + "/brewmasters help" + ChatColor.WHITE + " - Show this help message");
    }

    private void listRecipes(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== Custom Brewing Recipes ===");

        if (plugin.getRecipeManager().getRecipeCount() == 0) {
            sender.sendMessage(ChatColor.YELLOW + "No custom recipes loaded.");
            return;
        }

        for (BrewingRecipe recipe : plugin.getRecipeManager().getAllRecipes()) {
            String potionType = "";
            switch (recipe.getPotionType()) {
                case SPLASH:
                    potionType = ChatColor.BLUE + "[SPLASH] ";
                    break;
                case LINGERING:
                    potionType = ChatColor.DARK_PURPLE + "[LINGERING] ";
                    break;
                default:
                    break;
            }

            String glowing = recipe.isGlowing() ? ChatColor.YELLOW + "✨ " : "";

            sender.sendMessage(ChatColor.YELLOW + recipe.getId() + ChatColor.WHITE + ": " +
                    ChatColor.AQUA + recipe.getBasePotion().name() + ChatColor.WHITE + " + " +
                    ChatColor.GREEN + recipe.getIngredient().getDisplayName() + ChatColor.WHITE + " → " +
                    potionType + glowing +
                    ChatColor.LIGHT_PURPLE
                    + (recipe.getResultName() != null ? ChatColor.stripColor(recipe.getResultName())
                            : "Custom Potion"));
        }

        sender.sendMessage(ChatColor.GRAY + "Total: " + plugin.getRecipeManager().getRecipeCount() + " recipes");
    }

    private void listDiscoveredRecipes(Player player) {
        Set<String> discoveredRecipes = plugin.getPlayerDataManager().getDiscoveredRecipes(player);

        player.sendMessage(ChatColor.GOLD + "=== Your Discovered Recipes ===");

        if (discoveredRecipes.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "You haven't discovered any recipes yet!");
            player.sendMessage(ChatColor.GRAY + "Explore the world and try brewing to discover new recipes!");
            return;
        }

        for (String recipeId : discoveredRecipes) {
            BrewingRecipe recipe = plugin.getRecipeManager().getRecipe(recipeId);
            if (recipe != null) {
                String potionType = "";
                switch (recipe.getPotionType()) {
                    case SPLASH:
                        potionType = ChatColor.BLUE + "[SPLASH] ";
                        break;
                    case LINGERING:
                        potionType = ChatColor.DARK_PURPLE + "[LINGERING] ";
                        break;
                    default:
                        break;
                }

                String glowing = recipe.isGlowing() ? ChatColor.YELLOW + "✨ " : "";

                player.sendMessage(ChatColor.YELLOW + recipe.getId() + ChatColor.WHITE + ": " +
                        ChatColor.AQUA + recipe.getBasePotion().name() + ChatColor.WHITE + " + " +
                        ChatColor.GREEN + recipe.getIngredient().getDisplayName() + ChatColor.WHITE + " → " +
                        potionType + glowing +
                        ChatColor.LIGHT_PURPLE
                        + (recipe.getResultName() != null ? ChatColor.stripColor(recipe.getResultName())
                                : "Custom Potion"));
            }
        }

        player.sendMessage(ChatColor.GRAY + "Discovered: " + discoveredRecipes.size() + " recipes");
    }

    private void handleDiscoverCommand(CommandSender sender, String playerName, String recipeId) {
        Player target = plugin.getServer().getPlayer(playerName);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found: " + playerName);
            return;
        }

        BrewingRecipe recipe = plugin.getRecipeManager().getRecipe(recipeId);
        if (recipe == null) {
            sender.sendMessage(ChatColor.RED + "Recipe not found: " + recipeId);
            return;
        }

        if (plugin.getDiscoveryManager().discoverRecipe(target, recipeId)) {
            sender.sendMessage(ChatColor.GREEN + "Recipe '" + recipeId + "' discovered for " + playerName);
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Player " + playerName + " already knows recipe '" + recipeId + "'");
        }
    }

    private void listAchievements(Player player) {
        if (!plugin.getAchievementManager().isAchievementsEnabled()) {
            player.sendMessage(ChatColor.RED + "Achievements are not enabled on this server.");
            return;
        }

        Set<String> unlockedAchievements = plugin.getAchievementManager().getUnlockedAchievements(player);

        player.sendMessage(ChatColor.GOLD + "=== Your Achievements ===");

        if (unlockedAchievements.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "You haven't unlocked any achievements yet!");
            player.sendMessage(ChatColor.GRAY + "Keep brewing to unlock achievements!");
            return;
        }

        for (String achievementId : unlockedAchievements) {
            net.pwing.brewmasters.models.Achievement achievement = plugin.getAchievementManager()
                    .getAchievement(achievementId);
            if (achievement != null) {
                player.sendMessage(ChatColor.GOLD + "🏆 " + ChatColor.YELLOW + achievement.getName());
                player.sendMessage(ChatColor.GRAY + "  " + achievement.getDescription());
            }
        }

        int totalAchievements = plugin.getAchievementManager().getAllAchievements().size();
        player.sendMessage(ChatColor.GRAY + "Unlocked: " + unlockedAchievements.size() + "/" + totalAchievements
                + " achievements");
    }

    private void handleSpeedCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return;
        }

        Player player = (Player) sender;

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /brewmasters speed <set|remove|info> [multiplier]");
            return;
        }

        String action = args[1].toLowerCase();

        switch (action) {
            case "info":
                showSpeedInfo(player);
                break;
            case "set":
                if (args.length < 3) {
                    sender.sendMessage(ChatColor.RED + "Usage: /brewmasters speed set <multiplier>");
                    return;
                }
                try {
                    double multiplier = Double.parseDouble(args[2]);
                    setBrewingStandSpeed(player, multiplier);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid multiplier. Please enter a number.");
                }
                break;
            case "remove":
                removeBrewingStandSpeed(player);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "Usage: /brewmasters speed <set|remove|info> [multiplier]");
                break;
        }
    }

    private void showSpeedInfo(Player player) {
        org.bukkit.block.Block targetBlock = player.getTargetBlock(null, 5);

        if (targetBlock.getType() != org.bukkit.Material.BREWING_STAND) {
            player.sendMessage(ChatColor.RED + "You must be looking at a brewing stand.");
            return;
        }

        org.bukkit.Location location = targetBlock.getLocation();

        if (!plugin.getBrewingSpeedManager().isSpeedSystemEnabled()) {
            player.sendMessage(ChatColor.YELLOW + "Brewing speed system is disabled.");
            return;
        }

        player.sendMessage(ChatColor.GOLD + "=== Brewing Stand Speed Info ===");

        // Show custom speed if set
        var speedData = plugin.getBrewingSpeedManager().getBrewingStandSpeed(location);
        if (speedData != null) {
            player.sendMessage(ChatColor.GREEN + "Custom Speed: " + speedData.getSpeedMultiplier() + "x");
            player.sendMessage(
                    ChatColor.GRAY + "Set by: " + plugin.getServer().getOfflinePlayer(speedData.getSetBy()).getName());
        } else {
            player.sendMessage(ChatColor.GRAY + "No custom speed set for this brewing stand.");
        }

        // Show example speed calculation for a recipe
        var recipes = plugin.getRecipeManager().getAllRecipes();
        if (!recipes.isEmpty()) {
            var exampleRecipe = recipes.iterator().next();
            String speedDesc = plugin.getBrewingSpeedManager().getSpeedDescription(exampleRecipe, player, location);
            player.sendMessage(ChatColor.YELLOW + "Example speed: " + speedDesc);
        }
    }

    private void setBrewingStandSpeed(Player player, double multiplier) {
        org.bukkit.block.Block targetBlock = player.getTargetBlock(null, 5);

        if (targetBlock.getType() != org.bukkit.Material.BREWING_STAND) {
            player.sendMessage(ChatColor.RED + "You must be looking at a brewing stand.");
            return;
        }

        if (multiplier <= 0) {
            player.sendMessage(ChatColor.RED + "Speed multiplier must be greater than 0.");
            return;
        }

        org.bukkit.Location location = targetBlock.getLocation();
        plugin.getBrewingSpeedManager().setBrewingStandSpeed(location, multiplier, player.getUniqueId());

        String speedType = multiplier < 1.0 ? "faster" : (multiplier > 1.0 ? "slower" : "normal");
        player.sendMessage(ChatColor.GREEN + "Set brewing stand speed to " + multiplier + "x (" + speedType + ")");
    }

    private void removeBrewingStandSpeed(Player player) {
        org.bukkit.block.Block targetBlock = player.getTargetBlock(null, 5);

        if (targetBlock.getType() != org.bukkit.Material.BREWING_STAND) {
            player.sendMessage(ChatColor.RED + "You must be looking at a brewing stand.");
            return;
        }

        org.bukkit.Location location = targetBlock.getLocation();
        plugin.getBrewingSpeedManager().removeBrewingStandSpeed(location);
        player.sendMessage(ChatColor.GREEN + "Removed custom speed from brewing stand.");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String input = args[0].toLowerCase();

            for (String option : Arrays.asList("reload", "list", "recipes", "achievements", "discover", "speed",
                    "chains", "chain", "help")) {
                if (option.startsWith(input)) {
                    completions.add(option);
                }
            }

            return completions;
        }

        return new ArrayList<>();
    }

    /**
     * Handle chains command - list all available brewing chains
     */
    private boolean handleChainsCommand(CommandSender sender, String[] args) {
        if (!plugin.getBrewingChainManager().isChainsEnabled()) {
            sender.sendMessage(ChatColor.RED + "Brewing chains are not enabled on this server.");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        Collection<BrewingChain> allChains = plugin.getBrewingChainManager().getAllChains();

        if (allChains.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + "No brewing chains are configured.");
            return true;
        }

        sender.sendMessage(ChatColor.GOLD + "=== Brewing Chains ===");

        for (BrewingChain chain : allChains) {
            double progress = plugin.getBrewingChainManager().getChainProgress(player, chain.getId());
            boolean completed = plugin.getBrewingChainManager().hasCompletedChain(player, chain.getId());

            String status;
            if (completed) {
                status = ChatColor.GREEN + "✓ COMPLETED";
            } else if (progress > 0) {
                status = ChatColor.YELLOW + String.format("%.1f%% Complete", progress * 100);
            } else {
                status = ChatColor.GRAY + "Not Started";
            }

            sender.sendMessage(ChatColor.AQUA + chain.getName() + ChatColor.WHITE + " - " + status);
            if (chain.getDescription() != null) {
                sender.sendMessage(ChatColor.GRAY + "  " + chain.getDescription());
            }
            sender.sendMessage(ChatColor.GRAY + "  Steps: " + chain.getSteps().size() +
                    " | Use: /brewmasters chain " + chain.getId());
        }

        return true;
    }

    /**
     * Handle chain command - show details for a specific chain
     */
    private boolean handleChainCommand(CommandSender sender, String[] args) {
        if (!plugin.getBrewingChainManager().isChainsEnabled()) {
            sender.sendMessage(ChatColor.RED + "Brewing chains are not enabled on this server.");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        String chainId = args[1];
        BrewingChain chain = plugin.getBrewingChainManager().getChain(chainId);

        if (chain == null) {
            sender.sendMessage(ChatColor.RED + "Chain '" + chainId + "' not found.");
            return true;
        }

        List<String> completedRecipes = plugin.getBrewingChainManager().getCompletedChainRecipes(player, chainId);
        boolean chainCompleted = plugin.getBrewingChainManager().hasCompletedChain(player, chainId);
        double progress = plugin.getBrewingChainManager().getChainProgress(player, chainId);

        sender.sendMessage(ChatColor.GOLD + "=== " + chain.getName() + " ===");

        if (chain.getDescription() != null) {
            sender.sendMessage(ChatColor.WHITE + chain.getDescription());
            sender.sendMessage("");
        }

        // Show progress
        String progressBar = createProgressBar(progress, 20);
        sender.sendMessage(ChatColor.AQUA + "Progress: " + progressBar + ChatColor.WHITE +
                String.format(" %.1f%%", progress * 100));

        if (chainCompleted) {
            sender.sendMessage(ChatColor.GREEN + "✓ Chain Completed!");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Completed: " + completedRecipes.size() + "/"
                    + chain.getSteps().size() + " steps");
        }

        sender.sendMessage("");
        sender.sendMessage(ChatColor.GOLD + "Chain Steps:");

        // Show each step
        for (int i = 0; i < chain.getSteps().size(); i++) {
            BrewingChain.ChainStep step = chain.getSteps().get(i);
            boolean stepCompleted = completedRecipes.contains(step.getRecipeId());

            String stepStatus;
            if (stepCompleted) {
                stepStatus = ChatColor.GREEN + "✓";
            } else if (chain.requiresOrder() && i > 0
                    && !completedRecipes.contains(chain.getSteps().get(i - 1).getRecipeId())) {
                stepStatus = ChatColor.RED + "✗ (Locked)";
            } else {
                stepStatus = ChatColor.YELLOW + "○";
            }

            String recipeName = getRecipeName(step.getRecipeId());
            sender.sendMessage(stepStatus + ChatColor.WHITE + " " + (i + 1) + ". " + recipeName);

            if (step.getDescription() != null) {
                sender.sendMessage(ChatColor.GRAY + "   " + step.getDescription());
            }
        }

        return true;
    }

    /**
     * Create a progress bar string
     */
    private String createProgressBar(double progress, int length) {
        int filled = (int) (progress * length);
        StringBuilder bar = new StringBuilder();

        bar.append(ChatColor.GREEN);
        for (int i = 0; i < filled; i++) {
            bar.append("█");
        }

        bar.append(ChatColor.GRAY);
        for (int i = filled; i < length; i++) {
            bar.append("█");
        }

        return bar.toString();
    }

    /**
     * Get display name for a recipe
     */
    private String getRecipeName(String recipeId) {
        var recipe = plugin.getRecipeManager().getRecipe(recipeId);
        if (recipe != null && recipe.getResultName() != null) {
            return ChatColor.stripColor(recipe.getResultName());
        }
        return recipeId;
    }
}
