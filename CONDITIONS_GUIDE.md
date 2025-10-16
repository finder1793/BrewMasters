# BrewMasters Conditions Guide

Conditions allow you to restrict when and where recipes can be brewed. This adds depth and challenge to your custom brewing system!

## Table of Contents
1. [Overview](#overview)
2. [Biome Conditions](#biome-conditions)
3. [World Conditions](#world-conditions)
4. [Permission Conditions](#permission-conditions)
5. [Time Conditions](#time-conditions)
6. [Weather Conditions](#weather-conditions)
7. [Y-Level Conditions](#y-level-conditions)
8. [PlaceholderAPI Conditions](#placeholderapi-conditions)
9. [Combining Conditions](#combining-conditions)
10. [Examples](#examples)

## Overview

Conditions are optional requirements that must be met for a recipe to work. If any condition fails, the brewing will not produce the custom potion.

### Basic Structure

```yaml
recipes:
  my_recipe:
    base-potion: AWKWARD_POTION
    ingredient: DIAMOND
    result:
      # ... result configuration ...
    conditions:
      # ... condition configuration ...
```

## Biome Conditions

Restrict brewing to specific biomes.

### Configuration

```yaml
conditions:
  biomes:
    - DESERT
    - DESERT_HILLS
    - BADLANDS
  biomes-whitelist: true  # true = must be in these biomes, false = cannot be in these biomes
```

### Available Biomes

Common biomes:
- `PLAINS`, `FOREST`, `BIRCH_FOREST`, `DARK_FOREST`
- `DESERT`, `DESERT_HILLS`
- `JUNGLE`, `JUNGLE_HILLS`, `BAMBOO_JUNGLE`
- `TAIGA`, `SNOWY_TAIGA`
- `SWAMP`, `MANGROVE_SWAMP`
- `SAVANNA`, `SAVANNA_PLATEAU`
- `BADLANDS`, `WOODED_BADLANDS`
- `MUSHROOM_FIELDS`
- `OCEAN`, `DEEP_OCEAN`, `WARM_OCEAN`, `COLD_OCEAN`
- `RIVER`, `FROZEN_RIVER`
- `BEACH`, `SNOWY_BEACH`
- `MOUNTAINS`, `SNOWY_PEAKS`, `JAGGED_PEAKS`
- `MEADOW`, `GROVE`, `SNOWY_SLOPES`
- `CHERRY_GROVE`
- `NETHER_WASTES`, `CRIMSON_FOREST`, `WARPED_FOREST`, `SOUL_SAND_VALLEY`, `BASALT_DELTAS`
- `THE_END`, `END_HIGHLANDS`, `END_MIDLANDS`, `END_BARRENS`
- `DEEP_DARK`, `DRIPSTONE_CAVES`, `LUSH_CAVES`

### Example: Desert-Only Potion

```yaml
desert_brew:
  base-potion: AWKWARD_POTION
  ingredient: SAND
  result:
    name: "&6Desert Essence"
    color: "#EDC9AF"
    effects:
      - type: FIRE_RESISTANCE
        duration: 3600
        amplifier: 0
  conditions:
    biomes:
      - DESERT
      - DESERT_HILLS
    biomes-whitelist: true
```

## World Conditions

Restrict brewing to specific worlds.

### Configuration

```yaml
conditions:
  worlds:
    - world
    - world_nether
    - world_the_end
  worlds-whitelist: true  # true = must be in these worlds, false = cannot be in these worlds
```

### Example: Nether-Only Potion

```yaml
nether_brew:
  base-potion: AWKWARD_POTION
  ingredient: NETHERRACK
  result:
    name: "&4Nether Essence"
    color: "#8B0000"
    effects:
      - type: FIRE_RESISTANCE
        duration: 6000
        amplifier: 0
  conditions:
    worlds:
      - world_nether
    worlds-whitelist: true
```

## Permission Conditions

Require players to have specific permissions.

### Configuration

```yaml
conditions:
  permission: brewmasters.recipe.master
  permission-required: true  # true = must have permission, false = must NOT have permission
```

### Example: Master Alchemist Recipe

```yaml
master_brew:
  base-potion: AWKWARD_POTION
  ingredient: NETHER_STAR
  result:
    name: "&5Master's Brew"
    color: "#9400D3"
    glowing: true
    effects:
      - type: REGENERATION
        duration: 2400
        amplifier: 2
  conditions:
    permission: brewmasters.recipe.master
    permission-required: true
```

## Time Conditions

Restrict brewing to specific times of day.

### Configuration

```yaml
conditions:
  time:
    min: 13000  # Start time (in ticks)
    max: 23000  # End time (in ticks)
```

### Minecraft Time Reference

| Time | Ticks | Description |
|------|-------|-------------|
| Dawn | 0 | Sunrise begins |
| Morning | 1000 | Full daylight |
| Noon | 6000 | Sun at highest point |
| Afternoon | 9000 | Sun descending |
| Dusk | 12000 | Sunset begins |
| Night | 13000 | Full darkness |
| Midnight | 18000 | Moon at highest point |
| Late Night | 23000 | Almost dawn |

### Example: Night-Only Potion

```yaml
moonlight_brew:
  base-potion: AWKWARD_POTION
  ingredient: GLOWSTONE_DUST
  result:
    name: "&eMoonlight Elixir"
    color: "#F0E68C"
    effects:
      - type: NIGHT_VISION
        duration: 9600
        amplifier: 0
  conditions:
    time:
      min: 13000  # Night
      max: 23000  # Before dawn
```

## Weather Conditions

Restrict brewing to specific weather conditions.

### Configuration

```yaml
conditions:
  weather: CLEAR  # CLEAR, RAIN, THUNDER, or ANY_STORM
```

### Weather Types

- `CLEAR` - No rain or thunder
- `RAIN` - Raining but not thundering
- `THUNDER` - Thunderstorm
- `ANY_STORM` - Either rain or thunder

### Example: Thunderstorm Potion

```yaml
storm_brew:
  base-potion: AWKWARD_POTION
  ingredient: LIGHTNING_ROD
  result:
    name: "&bStorm Brew"
    color: "#4682B4"
    glowing: true
    effects:
      - type: SPEED
        duration: 3600
        amplifier: 2
  conditions:
    weather: THUNDER
```

## Y-Level Conditions

Restrict brewing to specific height ranges.

### Configuration

```yaml
conditions:
  y-level:
    min: -64   # Minimum Y level
    max: 0     # Maximum Y level
```

### Y-Level Reference

| Range | Description |
|-------|-------------|
| -64 to -48 | Deepslate layer |
| -48 to 0 | Underground caves |
| 0 to 64 | Surface level |
| 64 to 128 | Above ground |
| 128 to 256 | High altitude |
| 256 to 320 | Build limit |

### Example: Deep Underground Potion

```yaml
deep_dark_brew:
  base-potion: AWKWARD_POTION
  ingredient: SCULK
  result:
    name: "&0Deep Dark Brew"
    color: "#0A0A0A"
    glowing: true
    effects:
      - type: NIGHT_VISION
        duration: 9600
        amplifier: 0
  conditions:
    y-level:
      min: -64
      max: 0
```

## PlaceholderAPI Conditions

Use PlaceholderAPI placeholders for advanced conditions.

**Requires**: PlaceholderAPI plugin installed

### Configuration

```yaml
conditions:
  placeholders:
    - placeholder: "%player_level%"
      operator: ">="
      value: "50"
    - placeholder: "%vault_eco_balance%"
      operator: ">"
      value: "10000"
```

### Supported Operators

| Operator | Aliases | Description | Example |
|----------|---------|-------------|---------|
| `equals` | `==`, `=` | Exact match | `value: "VIP"` |
| `not_equals` | `!=` | Not equal | `value: "Banned"` |
| `contains` | - | Contains text | `value: "Admin"` |
| `not_contains` | - | Doesn't contain | `value: "Guest"` |
| `greater_than` | `>` | Number greater | `value: "50"` |
| `less_than` | `<` | Number less | `value: "10"` |
| `greater_or_equal` | `>=` | Number >= | `value: "100"` |
| `less_or_equal` | `<=` | Number <= | `value: "5"` |
| `starts_with` | - | Starts with text | `value: "VIP"` |
| `ends_with` | - | Ends with text | `value: "_rank"` |

### Common Placeholders

**Player Placeholders:**
- `%player_name%` - Player name
- `%player_level%` - Experience level
- `%player_health%` - Current health
- `%player_food_level%` - Hunger level
- `%player_world%` - Current world

**Vault Placeholders** (requires Vault):
- `%vault_eco_balance%` - Money balance
- `%vault_rank%` - Player rank

**LuckPerms Placeholders** (requires LuckPerms):
- `%luckperms_prefix%` - Player prefix
- `%luckperms_suffix%` - Player suffix
- `%luckperms_primary_group%` - Primary group

### Example: Level-Based Recipe

```yaml
veteran_potion:
  base-potion: AWKWARD_POTION
  ingredient: EXPERIENCE_BOTTLE
  result:
    name: "&aVeteran's Potion"
    color: "#7FFF00"
    effects:
      - type: LUCK
        duration: 6000
        amplifier: 2
  conditions:
    placeholders:
      - placeholder: "%player_level%"
        operator: ">="
        value: "50"
```

### Example: Economy-Based Recipe

```yaml
rich_brew:
  base-potion: AWKWARD_POTION
  ingredient: EMERALD_BLOCK
  result:
    name: "&2Rich Man's Brew"
    color: "#50C878"
    effects:
      - type: LUCK
        duration: 6000
        amplifier: 3
  conditions:
    placeholders:
      - placeholder: "%vault_eco_balance%"
        operator: ">="
        value: "100000"
```

## Combining Conditions

You can combine multiple conditions! ALL conditions must be met.

### Example: Complex Recipe

```yaml
ultimate_brew:
  base-potion: AWKWARD_POTION
  ingredient: DRAGON_EGG
  result:
    name: "&5&lUltimate Brew"
    color: "#9400D3"
    glowing: true
    effects:
      - type: REGENERATION
        duration: 6000
        amplifier: 3
      - type: RESISTANCE
        duration: 6000
        amplifier: 3
      - type: STRENGTH
        duration: 6000
        amplifier: 3
  conditions:
    # Must be in The End
    worlds:
      - world_the_end
    worlds-whitelist: true
    # Must have permission
    permission: brewmasters.recipe.ultimate
    permission-required: true
    # Must be at night
    time:
      min: 13000
      max: 23000
    # Must be level 100+
    placeholders:
      - placeholder: "%player_level%"
        operator: ">="
        value: "100"
```

## Examples

### 1. Ocean Potion (Ocean Biomes Only)

```yaml
ocean_blessing:
  base-potion: AWKWARD_POTION
  ingredient: PRISMARINE_SHARD
  result:
    name: "&bOcean's Blessing"
    color: "#20B2AA"
    effects:
      - type: WATER_BREATHING
        duration: 9600
        amplifier: 0
  conditions:
    biomes:
      - OCEAN
      - DEEP_OCEAN
      - WARM_OCEAN
      - COLD_OCEAN
    biomes-whitelist: true
```

### 2. Mountain Potion (High Altitude)

```yaml
mountain_brew:
  base-potion: AWKWARD_POTION
  ingredient: SNOW_BLOCK
  result:
    name: "&fMountain Brew"
    color: "#FFFFFF"
    effects:
      - type: JUMP_BOOST
        duration: 3600
        amplifier: 2
  conditions:
    y-level:
      min: 128
      max: 320
```

### 3. VIP-Only Potion

```yaml
vip_elixir:
  base-potion: AWKWARD_POTION
  ingredient: DIAMOND_BLOCK
  result:
    name: "&bVIP Elixir"
    color: "#00FFFF"
    glowing: true
    effects:
      - type: LUCK
        duration: 6000
        amplifier: 2
  conditions:
    permission: brewmasters.vip
    permission-required: true
```

### 4. Full Moon Potion (Night + Clear Weather)

```yaml
full_moon_brew:
  base-potion: AWKWARD_POTION
  ingredient: GLOWSTONE
  result:
    name: "&eFullmoon Brew"
    color: "#FFD700"
    effects:
      - type: NIGHT_VISION
        duration: 9600
        amplifier: 0
  conditions:
    time:
      min: 18000  # Midnight
      max: 19000
    weather: CLEAR
```

## Tips

1. **Test Conditions** - Always test your conditions in-game
2. **Clear Messages** - Use lore to explain requirements
3. **Balance** - Harder conditions = better rewards
4. **Combine Wisely** - Too many conditions can be frustrating
5. **PlaceholderAPI** - Offers the most flexibility
6. **Permissions** - Great for donor/rank-based recipes
7. **Biomes** - Create thematic recipes
8. **Time/Weather** - Add challenge and rarity

## Troubleshooting

**Condition not working:**
- Check spelling of biome/world names (case-sensitive)
- Verify PlaceholderAPI is installed for placeholder conditions
- Check permission nodes are correct
- Test time values (use `/time query daytime`)

**PlaceholderAPI conditions failing:**
- Install PlaceholderAPI plugin
- Install required expansion (e.g., Player, Vault, LuckPerms)
- Use `/papi parse me %placeholder%` to test values

**Player not getting error message:**
- Player must be within 5 blocks of brewing stand
- Check if player has chat enabled

---

**Happy Conditional Brewing!** 🧪✨

