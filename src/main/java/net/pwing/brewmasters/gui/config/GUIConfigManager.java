package net.pwing.brewmasters.gui.config;

import net.pwing.brewmasters.BrewMasters;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * Manages all GUI configurations
 */
public class GUIConfigManager {
    
    private final BrewMasters plugin;
    private final File guisFolder;
    
    private RecipeBookGUIConfig recipeBookConfig;
    private RecipeDetailsGUIConfig recipeDetailsConfig;
    private AchievementsGUIConfig achievementsConfig;
    
    public GUIConfigManager(BrewMasters plugin) {
        this.plugin = plugin;
        this.guisFolder = new File(plugin.getDataFolder(), "guis");
        
        // Create guis folder
        if (!guisFolder.exists()) {
            guisFolder.mkdirs();
        }
        
        loadConfigs();
    }
    
    /**
     * Load all GUI configurations
     */
    public void loadConfigs() {
        recipeBookConfig = new RecipeBookGUIConfig(loadConfig("recipe-book.yml"));
        recipeDetailsConfig = new RecipeDetailsGUIConfig(loadConfig("recipe-details.yml"));
        achievementsConfig = new AchievementsGUIConfig(loadConfig("achievements.yml"));
    }
    
    /**
     * Reload all configurations
     */
    public void reload() {
        loadConfigs();
    }
    
    /**
     * Load a specific GUI config file
     */
    private FileConfiguration loadConfig(String fileName) {
        File configFile = new File(guisFolder, fileName);
        
        // Create from resources if doesn't exist
        if (!configFile.exists()) {
            try {
                InputStream in = plugin.getResource("guis/" + fileName);
                if (in != null) {
                    Files.copy(in, configFile.toPath());
                    in.close();
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create " + fileName + ": " + e.getMessage());
            }
        }
        
        return YamlConfiguration.loadConfiguration(configFile);
    }
    
    public RecipeBookGUIConfig getRecipeBookConfig() {
        return recipeBookConfig;
    }
    
    public RecipeDetailsGUIConfig getRecipeDetailsConfig() {
        return recipeDetailsConfig;
    }
    
    public AchievementsGUIConfig getAchievementsConfig() {
        return achievementsConfig;
    }
}
