# BrewMasters PlaceholderAPI Support

BrewMasters provides comprehensive PlaceholderAPI integration for displaying brewing statistics in other plugins like chat formats, scoreboards, holograms, and more.

## Requirements

- **PlaceholderAPI** plugin installed on your server
- BrewMasters automatically registers its expansion when PlaceholderAPI is detected

## Available Placeholders

### General Statistics

| Placeholder | Description | Example Output |
|------------|-------------|----------------|
| `%brewmasters_total_brewed%` | Total potions brewed by player | `142` |
| `%brewmasters_potions_brewed%` | Alias for total_brewed | `142` |
| `%brewmasters_recipes_discovered%` | Number of recipes discovered | `23` |
| `%brewmasters_discovered%` | Alias for recipes_discovered | `23` |
| `%brewmasters_total_recipes%` | Total recipes available on server | `50` |
| `%brewmasters_achievements%` | Achievements unlocked by player | `12` |
| `%brewmasters_achievements_unlocked%` | Alias for achievements | `12` |
| `%brewmasters_total_achievements%` | Total achievements available | `20` |
| `%brewmasters_chains_completed%` | Number of brewing chains completed | `5` |
| `%brewmasters_total_chains%` | Total chains available on server | `8` |

### Ranking & Progression

| Placeholder | Description | Example Output |
|------------|-------------|----------------|
| `%brewmasters_rank%` | Player's brewing rank based on potions brewed | `Expert` |
| `%brewmasters_brewing_rank%` | Alias for rank | `Expert` |
| `%brewmasters_discovery_percent%` | Recipe discovery percentage | `46.0` |
| `%brewmasters_discovery_percentage%` | Alias for discovery_percent | `46.0` |
| `%brewmasters_achievement_percent%` | Achievement completion percentage | `60.0` |
| `%brewmasters_achievement_percentage%` | Alias for achievement_percent | `60.0` |

### Brewing Ranks

Ranks are automatically assigned based on total potions brewed:

| Rank | Potions Required |
|------|-----------------|
| Grandmaster | 10,000+ |
| Master | 5,000+ |
| Expert | 2,500+ |
| Adept | 1,000+ |
| Proficient | 500+ |
| Skilled | 250+ |
| Apprentice | 100+ |
| Novice | 50+ |
| Beginner | 10+ |
| Newcomer | 0-9 |

### Recipe-Specific Statistics

| Placeholder | Description | Example Output |
|------------|-------------|----------------|
| `%brewmasters_recipe_<recipe_id>%` | Times a specific recipe has been brewed | `%brewmasters_recipe_fire_resistance%` → `37` |
| `%brewmasters_discovered_<recipe_id>%` | Check if recipe is discovered (true/false) | `%brewmasters_discovered_speed_boost%` → `true` |

### Achievement Checks

| Placeholder | Description | Example Output |
|------------|-------------|----------------|
| `%brewmasters_achievement_<achievement_id>%` | Check if achievement is unlocked (true/false) | `%brewmasters_achievement_first_brew%` → `true` |

### Chain Progress

| Placeholder | Description | Example Output |
|------------|-------------|----------------|
| `%brewmasters_chain_<chain_id>%` | Check if chain is completed (true/false) | `%brewmasters_chain_elemental_master%` → `false` |

### Custom Statistics

| Placeholder | Description | Example Output |
|------------|-------------|----------------|
| `%brewmasters_stat_<stat_name>%` | Get any custom statistic value | `%brewmasters_stat_total_brewed%` → `142` |

### System Status

| Placeholder | Description | Example Output |
|------------|-------------|----------------|
| `%brewmasters_discovery_enabled%` | Check if discovery system is enabled | `true` |
| `%brewmasters_achievements_enabled%` | Check if achievements are enabled | `true` |

### Active Potion Effects

| Placeholder | Description | Example Output |
|------------|-------------|----------------|
| `%brewmasters_effect_time_<recipe_id>%` | Time remaining in seconds for active potion | `%brewmasters_effect_time_elite_warrior%` → `42` |
| `%brewmasters_effect_time_formatted_<recipe_id>%` | Time remaining formatted as MM:SS | `%brewmasters_effect_time_formatted_vip_potion%` → `3:24` |
| `%brewmasters_effect_active_<recipe_id>%` | Check if player has active potion effect (true/false) | `%brewmasters_effect_active_berserker_rage%` → `true` |

## Usage Examples

### Chat Format (with EssentialsChat)
```yaml
# In EssentialsChat config
format: '{DISPLAYNAME}&7: {MESSAGE} &8[&6{brewmasters_rank}&8]'
```

### Scoreboard (with FeatherBoard)
```yaml
board:
  title: '&6&lBREWING STATS'
  lines:
    - '&7Rank: &e%brewmasters_rank%'
    - '&7Potions: &a%brewmasters_potions_brewed%'
    - '&7Recipes: &b%brewmasters_recipes_discovered%&7/&3%brewmasters_total_recipes%'
    - '&7Discovery: &d%brewmasters_discovery_percent%%'
    - ''
    - '&7Achievements: &6%brewmasters_achievements%&7/&e%brewmasters_total_achievements%'
```

### Hologram (with DecentHolograms)
```yaml
lines:
  - '&6&lTOP BREWER'
  - '&e%player_name%'
  - '&7Rank: &a%brewmasters_brewing_rank%'
  - '&7Total Brewed: &b%brewmasters_total_brewed%'
```

### Tab List (with TAB)
```yaml
tablist-name-formatting:
  - condition: '%brewmasters_rank%=Grandmaster'
    text: '&6⚡ %player%'
  - condition: '%brewmasters_rank%=Master'
    text: '&e★ %player%'
```

### Custom Permission Check
You can use PlaceholderAPI's conditional placeholders with other plugins:
```
%brewmasters_discovered_fire_resistance% == true
%brewmasters_achievement_master_brewer% == true
%brewmasters_total_brewed% > 1000
```

### DeluxeMenus Example
```yaml
test_item:
  material: BREWING_STAND
  display_name: '&6&lYour Brewing Stats'
  lore:
    - '&7Rank: &e%brewmasters_rank%'
    - '&7Potions Brewed: &a%brewmasters_total_brewed%'
    - ''
    - '&7Recipes: &b%brewmasters_recipes_discovered%&7/&3%brewmasters_total_recipes%'
    - '&7Progress: &d%brewmasters_discovery_percent%%'
    - ''
    - '&7Achievements: &6%brewmasters_achievements%&7/&e%brewmasters_total_achievements%'
    - '&7Completion: &d%brewmasters_achievement_percent%%'
```

### BossBar (with BossBarAPI)
```yaml
bars:
  brewing_progress:
    message: '&6Brewing Progress: &e%brewmasters_discovery_percent%% &7| &aRank: %brewmasters_rank%'
    color: YELLOW
    style: SEGMENTED_10
```

## Advanced Usage

### Recipe-Specific Leaderboards
Track how many times players have brewed specific recipes:
```
%brewmasters_recipe_legendary_strength%
%brewmasters_recipe_fire_immunity%
%brewmasters_recipe_speed_demon%
```

### Conditional Display Based on Discovery
Show different messages based on whether a player has discovered a recipe:
```yaml
# Only show if discovered
%checkitem_mat:CONDITION==<condition:%brewmasters_discovered_legendary_strength% == true>%
```

### Chain Progress Tracking
Monitor player progress through brewing chains:
```yaml
lore:
  - '&7Elemental Chain: %brewmasters_chain_elemental_master%'
  - '&7Legendary Chain: %brewmasters_chain_legendary_brews%'
```

## Statistics Tracking

BrewMasters automatically tracks the following statistics:

- **total_brewed** - Total potions brewed
- **recipe_<id>_brewed** - Per-recipe brew count
- **chains_completed** - Total chains completed
- **chain_<id>_completed** - Per-chain completion count
- All custom statistics from achievements and events

## Testing Placeholders

Use the PlaceholderAPI parse command to test placeholders:
```
/papi parse me %brewmasters_total_brewed%
/papi parse me %brewmasters_rank%
/papi parse me %brewmasters_discovery_percent%
```

## Notes

- All placeholders are **player-specific** and require a player context
- Boolean values return `true` or `false` as strings
- Percentages are formatted to 1 decimal place
- Recipe IDs and achievement IDs are case-sensitive
- Placeholders update in real-time as players brew potions

## Support

For issues or questions about PlaceholderAPI integration:
1. Ensure PlaceholderAPI is installed and loaded
2. Check `/papi list` to verify BrewMasters expansion is registered
3. Use `/papi parse me <placeholder>` to test individual placeholders
4. Check server logs for any registration errors
