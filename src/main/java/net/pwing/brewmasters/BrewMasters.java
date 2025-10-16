package net.pwing.brewmasters;

import net.pwing.brewmasters.commands.BrewMastersCommand;
import net.pwing.brewmasters.gui.config.GUIConfigManager;
import net.pwing.brewmasters.integrations.BrewMastersExpansion;
import net.pwing.brewmasters.listeners.BrewingListener;
import net.pwing.brewmasters.listeners.GUIListener;
import net.pwing.brewmasters.listeners.PlayerListener;
import net.pwing.brewmasters.managers.RecipeManager;
import net.pwing.brewmasters.managers.PlayerDataManager;
import net.pwing.brewmasters.managers.DiscoveryManager;
import net.pwing.brewmasters.managers.AchievementManager;
import net.pwing.brewmasters.managers.BrewingSpeedManager;
import net.pwing.brewmasters.managers.BrewingChainManager;
import net.pwing.brewmasters.managers.PotionEffectManager;
import net.pwing.brewmasters.utils.IntegrationUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class BrewMasters extends JavaPlugin {

    private RecipeManager recipeManager;
    private PlayerDataManager playerDataManager;
    private DiscoveryManager discoveryManager;
    private AchievementManager achievementManager;
    private BrewingSpeedManager brewingSpeedManager;
    private BrewingChainManager brewingChainManager;
    private GUIListener guiListener;
    private GUIConfigManager guiConfigManager;
    private BrewMastersExpansion placeholderExpansion;
    private PotionEffectManager potionEffectManager;

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();

        // Initialize GUI config
        guiConfigManager = new GUIConfigManager(this);

        // Initialize managers
        playerDataManager = new PlayerDataManager(this);
        recipeManager = new RecipeManager(this);
        discoveryManager = new DiscoveryManager(this);
        achievementManager = new AchievementManager(this);
        brewingSpeedManager = new BrewingSpeedManager(this);
        brewingChainManager = new BrewingChainManager(this);
        potionEffectManager = new PotionEffectManager(this);

        recipeManager.loadRecipes();

        // Register listeners
        getServer().getPluginManager().registerEvents(new BrewingListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new net.pwing.brewmasters.listeners.PotionDrinkListener(this), this);

        // Initialize and register GUI listener
        guiListener = new GUIListener(this);
        getServer().getPluginManager().registerEvents(guiListener, this);

        // Register commands
        getCommand("brewmasters").setExecutor(new BrewMastersCommand(this));

        // Log integration status
        IntegrationUtils.logIntegrationStatus(this);
        
        // Register PlaceholderAPI expansion if available
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholderExpansion = new BrewMastersExpansion(this);
            if (placeholderExpansion.register()) {
                getLogger().info("PlaceholderAPI expansion registered successfully!");
            }
        }

        getLogger().info("BrewMasters has been enabled!");
        getLogger().info("Loaded " + recipeManager.getRecipeCount() + " custom brewing recipes.");
    }

    @Override
    public void onDisable() {
        if (playerDataManager != null) {
            playerDataManager.saveAllPlayerData();
        }
        if (potionEffectManager != null) {
            potionEffectManager.shutdown();
        }
        getLogger().info("BrewMasters has been disabled!");
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public DiscoveryManager getDiscoveryManager() {
        return discoveryManager;
    }

    public AchievementManager getAchievementManager() {
        return achievementManager;
    }

    public BrewingSpeedManager getBrewingSpeedManager() {
        return brewingSpeedManager;
    }

    public BrewingChainManager getBrewingChainManager() {
        return brewingChainManager;
    }

    public GUIListener getGUIListener() {
        return guiListener;
    }

    public GUIConfigManager getGUIConfigManager() {
        return guiConfigManager;
    }
    
    public PotionEffectManager getPotionEffectManager() {
        return potionEffectManager;
    }

    public void reload() {
        reloadConfig();
        guiConfigManager.reload();
        recipeManager.loadRecipes();
        discoveryManager.loadDiscoveryMethods();
        achievementManager.loadAchievements();
        brewingSpeedManager.loadSpeedSettings();
        brewingChainManager.loadChains();
    }
}
