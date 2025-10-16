# BrewMasters

A Spigot/Paper plugin that allows you to define custom brewing and alchemy recipes for brewing stands with custom potion effects and colors.

## Features

### 🔥 Core Brewing System

- 🧪 **Custom Brewing Recipes** - Define your own brewing recipes with any base potion and ingredient
- 🎨 **Custom Colors** - Set custom RGB or hex colors for your potions
- ✨ **Custom Effects** - Add any combination of potion effects with custom durations and amplifiers
- 📝 **Custom Names & Lore** - Give your potions unique names and descriptions with color codes
- ⏱️ **Custom Brew Times** - Control how long each recipe takes to brew
- 💥 **Splash & Lingering Potions** - Full support for all potion types
- ✨ **Glowing Effect** - Make potions glow in the inventory
- 🎯 **Custom Model Data** - Support for resource pack custom models
- 🔄 **Hot Reload** - Reload configuration without restarting the server
- 🌟 **Unique Ingredients** - Use any material as an ingredient (soul sand, netherite, echo shards, etc.)
- 🎭 **MythicMobs & Crucible Support** - Use custom items from MythicMobs and Crucible as brewing ingredients
- 🎯 **Recipe Conditions** - Restrict recipes by biome, world, permission, time, weather, Y-level, and PlaceholderAPI
- 🔌 **PlaceholderAPI Support** - Advanced conditions using player stats, economy, ranks, and more
- ⚡ **Command Execution** - Run commands when potions are drunk or when effects expire
- 🔄 **Offline Effect Tracking** - Expire commands execute even if player logs out

### 🎯 Progression & Discovery

- 🔍 **Recipe Discovery System** - Players must discover recipes before using them
- 🎁 **Discovery Methods** - Random chance, achievements, first brew, permissions, and commands
- 📖 **Interactive Recipe Book GUI** - Beautiful GUI to browse discovered recipes
- 🏆 **Achievement System** - 30+ configurable achievements with rewards
- 🎯 **Achievement GUI** - Fully customizable achievement tracking interface
- ⛓️ **Brewing Chains** - Multi-step recipe sequences with chain-specific rewards
- ⚡ **Brewing Speed Modifiers** - Permission-based brew time multipliers

### 📊 Statistics & Integration

- 📈 **Comprehensive Statistics** - Track brewing activity for every player
- 🏅 **Brewing Ranks** - Automatic ranking system (Newcomer → Grandmaster)
- 🔢 **PlaceholderAPI Expansion** - 30+ placeholders for chat, scoreboards, holograms, etc.
- 📊 **Detailed Tracking** - Per-recipe brew counts, discovery progress, achievements, chains
- 🎮 **Third-Party Integration** - Works with FeatherBoard, TAB, DeluxeMenus, BossBarAPI, and more

## Requirements

- Java 17 or higher
- Spigot or Paper 1.20.1+ (may work on other versions)

## Optional Dependencies

- **MythicMobs** 5.6.1+ - Use MythicMobs items as brewing ingredients
- **MythicCrucible** 2.2.0+ - Use Crucible items as brewing ingredients
- **PlaceholderAPI** - Advanced recipe conditions

## Installation

1. Download the latest release JAR file
2. Place it in your server's `plugins` folder
3. Restart your server
4. Edit `plugins/BrewMasters/config.yml` to add your custom recipes
5. Run `/brewmasters reload` to load your recipes

## Building from Source

```bash
./gradlew build
```

The compiled JAR will be in `build/libs/BrewMasters-1.0.0.jar`

## Configuration

Edit `config.yml` to define your custom recipes:

```yaml
recipes:
  # Example with Soul Sand
  soul_wither:
    base-potion: AWKWARD_POTION
    ingredient: SOUL_SAND
    brew-time: 500
    result:
      name: "&8&lPotion of Withering"
      lore:
        - "&7Harness the power of souls"
        - "&7to inflict withering damage"
      color: "#2C1810"
      glowing: true
      effects:
        - type: WITHER
          duration: 600
          amplifier: 1
        - type: WEAKNESS
          duration: 1200
          amplifier: 0

  # Example with Netherite
  netherite_defense:
    base-potion: AWKWARD_POTION
    ingredient: NETHERITE_SCRAP
    brew-time: 1200
    result:
      name: "&4&lNetherite Fortification"
      lore:
        - "&7The ultimate defensive potion"
        - "&6&lLegendary"
      color: "#654321"
      glowing: true
      custom-model-data: 1
      effects:
        - type: RESISTANCE
          duration: 2400
          amplifier: 2
        - type: FIRE_RESISTANCE
          duration: 3600
          amplifier: 0
        - type: ABSORPTION
          duration: 2400
          amplifier: 3

  # Splash Potion Example
  splash_healing:
    base-potion: SPLASH_POTION
    ingredient: GLISTERING_MELON_SLICE
    brew-time: 400
    result:
      name: "&c&lSplash Potion of Healing"
      lore:
        - "&7Throw to heal nearby allies"
      color: "#FF2020"
      potion-type: SPLASH
      effects:
        - type: HEAL
          duration: 1
          amplifier: 1
```

### Configuration Options

- **base-potion**: The potion type to start with (e.g., WATER_BOTTLE, AWKWARD_POTION, POTION, SPLASH_POTION, LINGERING_POTION)
- **ingredient**: The item to brew with
  - Vanilla: `MATERIAL_NAME` or `MATERIAL_NAME:amount` (e.g., `SOUL_SAND`, `DIAMOND:3`)
  - MythicMobs: `mythic:ITEM_ID` or `mythic:ITEM_ID:amount` (e.g., `mythic:DRAGON_SCALE`, `mythic:MAGIC_DUST:2`)
  - Crucible: `crucible:ITEM_ID` or `crucible:ITEM_ID:amount` (e.g., `crucible:WISDOM_CRYSTAL`, `crucible:ANCIENT_RUNE:5`)
- **brew-time**: Time in ticks (20 ticks = 1 second, default: 400)
- **result.name**: Display name with color codes (&)
- **result.lore**: List of lore lines with color codes
- **result.color**: Hex (#RRGGBB) or RGB (r,g,b) format
- **result.potion-type**: NORMAL, SPLASH, or LINGERING (default: NORMAL)
- **result.glowing**: true/false - Makes the potion glow in inventory (default: false)
- **result.custom-model-data**: Integer for custom resource pack models (optional)
- **result.effects**: List of potion effects
  - **type**: Effect type (SPEED, REGENERATION, etc.)
  - **duration**: Duration in ticks
  - **amplifier**: Effect level (0 = I, 1 = II, etc.)
- **conditions**: Optional conditions for the recipe (see CONDITIONS_GUIDE.md)
  - **biomes**: List of biomes where recipe works
  - **worlds**: List of worlds where recipe works
  - **permission**: Required permission
  - **time**: Time range (min/max in ticks)
  - **weather**: Required weather (CLEAR, RAIN, THUNDER, ANY_STORM)
  - **y-level**: Y-level range (min/max)
  - **placeholders**: PlaceholderAPI conditions

### Available Potion Effect Types

SPEED, SLOWNESS, HASTE, MINING_FATIGUE, STRENGTH, INSTANT_HEALTH, INSTANT_DAMAGE, JUMP_BOOST, NAUSEA, REGENERATION, RESISTANCE, FIRE_RESISTANCE, WATER_BREATHING, INVISIBILITY, BLINDNESS, NIGHT_VISION, HUNGER, WEAKNESS, POISON, WITHER, HEALTH_BOOST, ABSORPTION, SATURATION, GLOWING, LEVITATION, LUCK, UNLUCK, SLOW_FALLING, CONDUIT_POWER, DOLPHINS_GRACE, BAD_OMEN, HERO_OF_THE_VILLAGE, DARKNESS

## Commands

- `/brewmasters reload` - Reload the configuration
- `/brewmasters list` - List all custom recipes
- `/brewmasters recipes [gui]` - View discovered recipes (GUI optional)
- `/brewmasters achievements [gui]` - View achievements (GUI optional)
- `/brewmasters discover <player> <recipe>` - Force discover a recipe for a player
- `/brewmasters chains` - View brewing chains
- `/brewmasters integrations` - Show integration status (MythicMobs, Crucible)
- `/brewmasters help` - Show help message

Aliases: `/bm`, `/brew`

## Permissions

- `brewmasters.admin` - Access to all commands (default: op)
- `brewmasters.reload` - Reload configuration (default: op)
- `brewmasters.list` - List recipes (default: op)
- `brewmasters.recipes` - View discovered recipes (default: true)
- `brewmasters.achievements` - View achievements (default: true)
- `brewmasters.discover` - Force discover recipes (default: op)
- `brewmasters.speed.fast` - Brew 2x faster (default: false)
- `brewmasters.speed.veryfast` - Brew 4x faster (default: false)

## PlaceholderAPI Integration

BrewMasters provides 30+ placeholders for displaying brewing statistics. See [PLACEHOLDERS.md](PLACEHOLDERS.md) for full documentation.

### Quick Examples

```
%brewmasters_total_brewed% - Total potions brewed
%brewmasters_rank% - Brewing rank (Newcomer → Grandmaster)
%brewmasters_recipes_discovered% - Recipes discovered count
%brewmasters_discovery_percent% - Discovery percentage
%brewmasters_achievements% - Achievements unlocked
```

### Usage in Other Plugins

**Chat Format:**
```yaml
format: '{DISPLAYNAME}&7: {MESSAGE} &8[&6{brewmasters_rank}&8]'
```

**Scoreboard:**
```yaml
lines:
  - '&7Rank: &e%brewmasters_rank%'
  - '&7Brewed: &a%brewmasters_total_brewed%'
```

See [PLACEHOLDERS.md](PLACEHOLDERS.md) for complete list and examples.

## How It Works

1. Place a base potion in the brewing stand
2. Add the ingredient specified in your recipe
3. Wait for the brewing to complete
4. The custom potion with your effects and colors will be created!

## Support

For issues, questions, or suggestions, please open an issue on the GitHub repository.

## License

This project is open source and available under the MIT License.

## Author

Created by finder17

