package net.pwing.brewmasters.models;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a multi-step brewing chain where recipes must be completed in sequence
 */
public class BrewingChain {

    private final String id;
    private final String name;
    private final String description;
    private final List<ChainStep> steps;
    private final boolean requiresOrder;
    private final ChainReward completionReward;

    private BrewingChain(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.steps = builder.steps;
        this.requiresOrder = builder.requiresOrder;
        this.completionReward = builder.completionReward;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<ChainStep> getSteps() {
        return steps;
    }

    public boolean requiresOrder() {
        return requiresOrder;
    }

    public ChainReward getCompletionReward() {
        return completionReward;
    }

    /**
     * Get the next step in the chain for a player
     */
    public ChainStep getNextStep(List<String> completedRecipes) {
        if (requiresOrder) {
            // Must complete steps in order
            for (ChainStep step : steps) {
                if (!completedRecipes.contains(step.getRecipeId())) {
                    return step;
                }
            }
        } else {
            // Can complete steps in any order
            for (ChainStep step : steps) {
                if (!completedRecipes.contains(step.getRecipeId())) {
                    return step;
                }
            }
        }
        return null; // Chain completed
    }

    /**
     * Check if the chain is completed
     */
    public boolean isCompleted(List<String> completedRecipes) {
        for (ChainStep step : steps) {
            if (!completedRecipes.contains(step.getRecipeId())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get completion progress (0.0 to 1.0)
     */
    public double getProgress(List<String> completedRecipes) {
        if (steps.isEmpty()) {
            return 1.0;
        }

        int completed = 0;
        for (ChainStep step : steps) {
            if (completedRecipes.contains(step.getRecipeId())) {
                completed++;
            }
        }

        return (double) completed / steps.size();
    }

    /**
     * Load a brewing chain from configuration
     */
    public static BrewingChain fromConfig(String id, ConfigurationSection section) {
        if (section == null) {
            return null;
        }

        Builder builder = new Builder(id);
        
        if (section.contains("name")) {
            builder.name(section.getString("name"));
        }
        
        if (section.contains("description")) {
            builder.description(section.getString("description"));
        }
        
        builder.requiresOrder(section.getBoolean("requires-order", true));

        // Load steps
        if (section.contains("steps")) {
            List<?> stepsList = section.getList("steps");
            if (stepsList != null) {
                for (Object stepObj : stepsList) {
                    if (stepObj instanceof ConfigurationSection) {
                        ConfigurationSection stepSection = (ConfigurationSection) stepObj;
                        ChainStep step = ChainStep.fromConfig(stepSection);
                        if (step != null) {
                            builder.addStep(step);
                        }
                    } else if (stepObj instanceof String) {
                        // Simple recipe ID
                        builder.addStep(new ChainStep((String) stepObj, null, null));
                    }
                }
            }
        }

        // Load completion reward
        if (section.contains("completion-reward")) {
            ConfigurationSection rewardSection = section.getConfigurationSection("completion-reward");
            ChainReward reward = ChainReward.fromConfig(rewardSection);
            builder.completionReward(reward);
        }

        return builder.build();
    }

    /**
     * Represents a single step in a brewing chain
     */
    public static class ChainStep {
        private final String recipeId;
        private final String description;
        private final ChainReward stepReward;

        public ChainStep(String recipeId, String description, ChainReward stepReward) {
            this.recipeId = recipeId;
            this.description = description;
            this.stepReward = stepReward;
        }

        public String getRecipeId() {
            return recipeId;
        }

        public String getDescription() {
            return description;
        }

        public ChainReward getStepReward() {
            return stepReward;
        }

        public static ChainStep fromConfig(ConfigurationSection section) {
            if (section == null) {
                return null;
            }

            String recipeId = section.getString("recipe");
            if (recipeId == null) {
                return null;
            }

            String description = section.getString("description");
            
            ChainReward reward = null;
            if (section.contains("reward")) {
                ConfigurationSection rewardSection = section.getConfigurationSection("reward");
                reward = ChainReward.fromConfig(rewardSection);
            }

            return new ChainStep(recipeId, description, reward);
        }
    }

    /**
     * Represents a reward for completing a chain or step
     */
    public static class ChainReward {
        private final int experience;
        private final List<String> commands;
        private final String message;

        public ChainReward(int experience, List<String> commands, String message) {
            this.experience = experience;
            this.commands = commands != null ? commands : new ArrayList<>();
            this.message = message;
        }

        public int getExperience() {
            return experience;
        }

        public List<String> getCommands() {
            return commands;
        }

        public String getMessage() {
            return message;
        }

        public static ChainReward fromConfig(ConfigurationSection section) {
            if (section == null) {
                return null;
            }

            int experience = section.getInt("experience", 0);
            List<String> commands = section.getStringList("commands");
            String message = section.getString("message");

            return new ChainReward(experience, commands, message);
        }
    }

    public static class Builder {
        private String id;
        private String name;
        private String description;
        private List<ChainStep> steps = new ArrayList<>();
        private boolean requiresOrder = true;
        private ChainReward completionReward;

        public Builder(String id) {
            this.id = id;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder requiresOrder(boolean requiresOrder) {
            this.requiresOrder = requiresOrder;
            return this;
        }

        public Builder addStep(ChainStep step) {
            this.steps.add(step);
            return this;
        }

        public Builder steps(List<ChainStep> steps) {
            this.steps = steps;
            return this;
        }

        public Builder completionReward(ChainReward reward) {
            this.completionReward = reward;
            return this;
        }

        public BrewingChain build() {
            if (id == null || steps.isEmpty()) {
                throw new IllegalStateException("Chain ID and steps must be set");
            }
            return new BrewingChain(this);
        }
    }
}
