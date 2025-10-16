# Changelog

All notable changes to BrewMasters will be documented in this file.

## [1.4.0] - 2025-10-16

### Added - Brewing Statistics & PlaceholderAPI Integration 📊

- **Comprehensive Statistics Tracking** - Track all brewing activity per player
  - Total potions brewed
  - Per-recipe brew counts
  - Recipes discovered count
  - Achievements unlocked count
  - Brewing chains completed
  - Custom statistics support

- **Brewing Rank System** - 10-tier automatic progression system
  - Newcomer (0-9 potions)
  - Beginner (10-49)
  - Novice (50-99)
  - Apprentice (100-249)
  - Skilled (250-499)
  - Proficient (500-999)
  - Adept (1,000-2,499)
  - Expert (2,500-4,999)
  - Master (5,000-9,999)
  - Grandmaster (10,000+)

- **PlaceholderAPI Expansion** - 35+ placeholders for brewing stats
  - General statistics (total brewed, recipes discovered, achievements)
  - Rank and progression (brewing rank, discovery %, achievement %)
  - Per-recipe statistics (brew count, discovery status)
  - Achievement checks (unlocked status)
  - Chain progress (completion status)
  - Active potion effect timers (time remaining, formatted time, active status)
  - Custom statistics support

- **Enhanced PlayerData Model** - Convenience methods for statistics
  - `getTotalPotionsBrewed()` - Total brewing count
  - `getTotalRecipesDiscovered()` - Discovery tracking
  - `getTotalAchievements()` - Achievement count
  - `getBrewingRank()` - Dynamic rank calculation
  - `getDiscoveryPercentage()` - Progress calculation
  - `getRecipeBrewCount()` - Per-recipe statistics
  - `getAchievementPercentage()` - Completion tracking

### Added - Potion Command Execution System ⚡

- **Drink Commands** - Execute commands when potions are consumed
  - Run immediately when player drinks potion
  - Support for console and player commands
  - Placeholder support for player data
  - Perfect for granting temporary permissions, teleportation, rewards

- **Expire Commands** - Execute commands when potion effects wear off
  - Automatically triggered when longest effect expires
  - Works even if player logs out (runs on next login)
  - Support for console and player commands
  - Perfect for removing permissions, debuffs, notifications

- **Active Effect Tracking** - Track active custom potion effects
  - Persistent storage in `active-effects.yml`
  - Automatic expiration checking (every second)
  - Offline player support - effects tracked while logged out
  - Time remaining calculations (seconds and MM:SS format)

- **Command Syntax Features**
  - `[console]` prefix - Execute as console (most reliable)
  - `[player]` prefix - Execute as player
  - No prefix - Defaults to console
  - Placeholders: `{player}`, `{uuid}`, `{recipe_id}`, `{recipe_name}`

- **PotionEffectManager** - New manager for effect tracking
  - Handles drink command execution
  - Handles expire command execution
  - Manages active effects across all players
  - Automatic cleanup and persistence

- **PotionDrinkListener** - Detects potion consumption
  - Matches consumed potions to recipes
  - Triggers command execution
  - Works with regular, splash, and lingering potions

- **Offline Effect Handling** - Robust logout/login support
  - Effects continue counting down while offline
  - Expire commands execute on login if expired
  - Prevents exploits and ensures consistency

### Added - Documentation

- **PLACEHOLDERS.md** - Complete PlaceholderAPI documentation
  - 35+ placeholder examples
  - Usage examples for popular plugins (FeatherBoard, TAB, DeluxeMenus, etc.)
  - Integration guides for chat, scoreboards, holograms, boss bars
  - Testing instructions

- **COMMANDS_GUIDE.md** - Comprehensive command system guide (400+ lines)
  - Complete syntax documentation
  - 10+ detailed real-world examples:
    - Temporary permission groups (VIP systems)
    - Warrior transformations
    - Economy integration (loans, rewards)
    - Arena teleportation systems
    - Quest progression
    - Parkour timers
    - Boss battle mechanics
    - Mining expeditions
    - Flight systems
  - PlaceholderAPI integration examples
  - Troubleshooting guide

- **example-command-recipes.yml** - 10 ready-to-use command recipes
  - VIP boost potion
  - Elite warrior transformation
  - Lucky fortune (economy)
  - Arena challenger
  - Enlightenment (quests)
  - Parkour master
  - Berserker rage
  - Creative flight
  - Miner's blessing

### Changed

- Updated `BrewingRecipe` to support drink-commands and expire-commands
- Updated `RecipeManager` to parse command lists from config
- Updated `PlayerListener` to handle expired effects on login
- Updated `BrewMastersExpansion` with 3 new placeholder types for active effects
- Updated `BrewMasters` main class to initialize PotionEffectManager
- Enhanced `PlayerData` with statistics convenience methods

### Technical

- **New Classes**: 4
  - `ActivePotionEffect` - Effect data model with time tracking
  - `PotionEffectManager` - Effect tracking and command execution
  - `PotionDrinkListener` - Potion consumption detection
  - `BrewMastersExpansion` - PlaceholderAPI integration

- **Effect Tracking**
  - Expiration checking runs every second (20 ticks)
  - Effect duration based on longest potion effect in recipe
  - Data persisted to `active-effects.yml`
  - Commands executed async to prevent lag

- **Statistics**
  - Automatic tracking of all brewing activity
  - Per-player data storage
  - Integration with existing PlayerData system
  - Real-time placeholder updates

### Plugin Statistics

- Lines of Code: ~11,000+ (was ~10,000+)
- Java Classes: 43 (was 40)
- Managers: 7 (added PotionEffectManager)
- Listeners: 4 (added PotionDrinkListener)
- Model Classes: 9 (added ActivePotionEffect)
- PlaceholderAPI Placeholders: 35+ (was 0)
- Documentation Pages: 9 (added PLACEHOLDERS.md, COMMANDS_GUIDE.md)

## [1.2.0] - 2025-10-16

### Added - MythicMobs & Crucible Integration 🎭

- **MythicMobs Support** - Use MythicMobs items as brewing ingredients
  - Format: `mythic:ITEM_ID` or `mythic:ITEM_ID:amount`
  - Example: `mythic:DRAGON_SCALE:3` requires 3 dragon scales
  - Soft dependency - works without MythicMobs installed
  - Compatible with MythicMobs 5.6.1+

- **Crucible Support** - Use Crucible items as brewing ingredients
  - Format: `crucible:ITEM_ID` or `crucible:ITEM_ID:amount`
  - Example: `crucible:WISDOM_CRYSTAL:5` requires 5 wisdom crystals
  - Soft dependency - works without Crucible installed
  - Compatible with MythicCrucible 2.2.0+

- **Ingredient Amount System** - Require multiple items for powerful recipes
  - Works with vanilla, MythicMobs, and Crucible ingredients
  - Format: `INGREDIENT:amount`
  - Examples: `DIAMOND:3`, `mythic:MAGIC_DUST:2`, `crucible:ANCIENT_RUNE:5`

- **Integration Status Command** - `/brewmasters integrations`
  - Shows which plugins are detected and available
  - Displays supported ingredient formats
  - Helps troubleshoot integration issues

### Added - Ingredient System Architecture

- **BrewingIngredient Interface** - Abstract base for all ingredient types
- **VanillaIngredient** - Handles vanilla Minecraft materials
- **MythicMobsIngredient** - Handles MythicMobs items with runtime detection
- **CrucibleIngredient** - Handles Crucible items with runtime detection
- **IngredientFactory** - Parses ingredient strings and creates appropriate types
- **IntegrationUtils** - Utility class for checking plugin availability

### Changed

- Updated `BrewingRecipe` to use `BrewingIngredient` instead of `Material`
- Updated `RecipeManager` to parse ingredients using `IngredientFactory`
- Updated `BrewingListener` to match ingredients using new system
- Updated `RecipeBookGUI` and `RecipeDetailsGUI` to display ingredient names correctly
- Added MythicMobs and Crucible to soft dependencies in plugin.yml
- Added Lumine Maven repository to build.gradle

### Documentation

- Updated **README.md** with MythicMobs/Crucible information
- Updated **FEATURES.md** with integration details
- Updated **RECIPE_GUIDE.md** with ingredient format section
- Updated **QUICKSTART.md** with MythicMobs/Crucible examples
- Added ingredient format examples to config.yml

### Technical

- Uses BukkitAdapter for converting MythicMobs AbstractItemStack to Bukkit ItemStack
- Runtime plugin detection using Class.forName() and plugin manager
- Graceful degradation when MythicMobs/Crucible not installed
- Proper error handling for missing items or invalid IDs

## [1.1.0] - 2025-10-14

### Added - Recipe Conditions System 🎯

- **Biome Conditions** - Restrict recipes to specific biomes (whitelist/blacklist)
- **World Conditions** - Limit recipes to specific worlds (whitelist/blacklist)
- **Permission Conditions** - Require specific permissions to brew recipes
- **Time Conditions** - Restrict brewing to specific times of day (day/night cycles)
- **Weather Conditions** - Require specific weather (clear, rain, thunder, any storm)
- **Y-Level Conditions** - Height-based restrictions (underground, high altitude, etc.)
- **PlaceholderAPI Conditions** - Advanced conditions using PlaceholderAPI
  - Player level requirements
  - Economy balance checks
  - Rank/group requirements
  - 10+ comparison operators (equals, greater_than, contains, etc.)
- **Condition Combining** - Mix multiple conditions (ALL must be met)
- **Player Feedback** - Players receive messages when conditions aren't met
- **Nearby Player Detection** - Automatically finds player within 5 blocks of brewing stand

### Added - Documentation

- **CONDITIONS_GUIDE.md** - Complete guide to all condition types with examples
- Updated **README.md** with conditions feature
- Updated **FEATURES.md** with conditions system overview
- Added 8 example conditional recipes to config.yml:
  - Nether-only recipe
  - Deep underground recipe
  - Night-time only recipe
  - Thunderstorm recipe
  - Biome-specific recipe (desert)
  - Permission-based recipe
  - PlaceholderAPI level requirement recipe

### Changed

- Updated `BrewingRecipe` model to support conditions
- Updated `RecipeManager` to parse conditions from config
- Updated `BrewingListener` to check conditions before brewing
- Added PlaceholderAPI as soft dependency in plugin.yml
- Added PlaceholderAPI repository to build.gradle

### Technical

- Created `BrewCondition` interface for extensible condition system
- Implemented 7 condition types with proper null handling
- Added condition parsing with error handling
- Graceful degradation when PlaceholderAPI is not installed

## [1.0.0] - 2025-01-XX

### Initial Release

#### Added
- ✨ Custom brewing recipe system
- 🎨 Custom potion colors (hex and RGB support)
- 📝 Custom potion names and lore with color codes
- ✨ Custom potion effects with configurable duration and amplifier
- 💥 Full support for splash potions
- 💥 Full support for lingering potions
- ✨ Glowing effect for legendary potions
- 🎯 Custom model data support for resource packs
- ⏱️ Configurable brew times
- 🔄 Hot reload command (`/brewmasters reload`)
- 📋 Recipe list command (`/brewmasters list`)
- ❓ Help command (`/brewmasters help`)
- 🔐 Permission-based access control
- 📖 Comprehensive documentation (README, QUICKSTART, RECIPE_GUIDE, FEATURES)
- 🎨 20+ example recipes including:
  - Soul Sand wither potions
  - Netherite defense potions
  - Echo Shard ancient power potions
  - Amethyst clarity potions
  - Splash and lingering variants
  - Multi-effect legendary potions
- 🛠️ Gradle build system with Shadow plugin
- 🎯 Paper/Spigot 1.20.1+ compatibility
- ☕ Java 17+ support

#### Features
- Recipe Manager for loading and managing recipes
- Brewing Listener for intercepting brewing events
- Color utility for translating color codes
- Builder pattern for recipe creation
- YAML-based configuration
- Support for any Minecraft material as ingredient
- Multiple potion effects per recipe
- Detailed error logging
- Tab completion for commands

#### Documentation
- README.md - Main documentation
- QUICKSTART.md - Quick start guide
- RECIPE_GUIDE.md - Comprehensive recipe creation guide
- FEATURES.md - Complete feature list
- CHANGELOG.md - This file
- example-recipes.yml - Additional recipe examples
- LICENSE - MIT License

#### Commands
- `/brewmasters reload` - Reload configuration
- `/brewmasters list` - List all custom recipes
- `/brewmasters help` - Show help message
- Aliases: `/bm`, `/brew`

#### Permissions
- `brewmasters.admin` - Access to all commands
- `brewmasters.reload` - Reload configuration
- `brewmasters.list` - List recipes

### Technical Details
- Group: net.pwing
- Author: finder17
- Version: 1.0.0
- API Version: 1.20
- Build System: Gradle 8.4
- Dependencies: Paper API 1.20.1, SnakeYAML 2.2

---

## Future Versions

### Planned Features
- Recipe discovery system
- Brewing achievements
- Recipe GUI/book
- Brewing skill progression
- Custom particle effects
- Custom sound effects
- Recipe categories
- Import/export functionality
- Brewing statistics
- Multi-step brewing chains

---

## Version Format

This project follows [Semantic Versioning](https://semver.org/):
- MAJOR version for incompatible API changes
- MINOR version for new functionality in a backwards compatible manner
- PATCH version for backwards compatible bug fixes

## Categories

- **Added** - New features
- **Changed** - Changes in existing functionality
- **Deprecated** - Soon-to-be removed features
- **Removed** - Removed features
- **Fixed** - Bug fixes
- **Security** - Security fixes

