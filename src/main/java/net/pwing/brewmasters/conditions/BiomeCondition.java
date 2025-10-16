package net.pwing.brewmasters.conditions;

import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BiomeCondition implements BrewCondition {

    private final Set<Biome> allowedBiomes;
    private final boolean whitelist;

    public BiomeCondition(List<String> biomes, boolean whitelist) {
        this.allowedBiomes = new HashSet<>();
        this.whitelist = whitelist;

        for (String biomeName : biomes) {
            try {
                Biome biome = Biome.valueOf(biomeName.toUpperCase());
                allowedBiomes.add(biome);
            } catch (IllegalArgumentException e) {
                // Invalid biome name, skip
            }
        }
    }

    @Override
    public boolean check(Player player, Location location) {
        Biome currentBiome = location.getBlock().getBiome();

        if (whitelist) {
            // Must be in one of the allowed biomes
            return allowedBiomes.contains(currentBiome);
        } else {
            // Must NOT be in any of the blocked biomes
            return !allowedBiomes.contains(currentBiome);
        }
    }

    @Override
    public String getDescription() {
        String mode = whitelist ? "Must be in biome: " : "Cannot be in biome: ";
        return mode + String.join(", ", allowedBiomes.stream()
                .map(Biome::name)
                .toArray(String[]::new));
    }

    @Override
    public ConditionType getType() {
        return ConditionType.BIOME;
    }
}

