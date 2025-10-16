# GUI Configuration Guide

## Overview
BrewMasters has a fully configurable GUI system with **separate YAML files** for each GUI! You can customize every aspect of the Recipe Book, Recipe Details, and Achievements GUIs.

## Features

✅ **Separate Config Files** - Each GUI has its own YAML file in `guis/` folder  
✅ **MiniMessage Support** - Use gradients, rainbow text, and hex colors  
✅ **Custom Model Data** - Support for resource pack custom models  
✅ **Permission Locking** - Lock recipes/pages behind permissions  
✅ **Condition Locking** - Use brewing conditions to lock recipes  
✅ **Discovery Hiding** - Hide undiscovered recipes  
✅ **Sound Effects** - Configurable sounds for all GUI interactions  
✅ **Flexible Layouts** - Change slot positions and GUI sizes  

## Configuration Files

**Location**: `guis/` folder in your plugin directory

- `recipe-book.yml` - Recipe book GUI configuration
- `recipe-details.yml` - Recipe details GUI configuration  
- `achievements.yml` - Achievements GUI configuration

### Recipe Book GUI (`guis/recipe-book.yml`)

```yaml
gui:
  title: "<gradient:aqua:blue>📖 Recipe Book</gradient>"
  size: 54  # Must be multiple of 9
  items-per-page: 28

# Discovered recipes
discovered-recipe:
  name: "<gradient:green:lime>{recipe_name}</gradient>"
  lore:
    - "<gray>Click to view recipe details"
    - ""
    - "<aqua>Brewing Time: <white>{brew_time}s"
  glow: true  # Enchantment glow effect

# Locked/undiscovered recipes
locked-recipe:
  material: BARRIER
  name: "<dark_gray>??? Locked Recipe"
  lore:
    - "<gray>You haven't discovered this recipe yet"
  custom-model-data: 0

# Permission-locked recipes
permission-locked-recipe:
  material: IRON_BARS
  name: "<red>🔒 Permission Required"
  lore:
    - "<gray>This recipe requires special permission"
    - ""
    - "<red>Permission: <white>{permission}"

# Condition-locked recipes
condition-locked-recipe:
  material: REDSTONE
  name: "<red>⚠ Conditions Not Met"
  lore:
    - "<gray>This recipe has special requirements"

# Visibility settings
visibility:
  hide-undiscovered: false  # Hide recipes not yet discovered
  hide-no-permission: false  # Hide recipes without permission
  hide-unmet-conditions: false  # Hide recipes with unmet conditions
  show-locked-placeholders: true  # Show placeholders for locked recipes

# Page permissions (optional)
page-permissions:
  enabled: false
  "2": "brewmasters.recipes.page2"
  "3": "brewmasters.recipes.page3"
```

### Recipe Details GUI (`guis/recipe-details.yml`)

```yaml
gui:
  title: "<gradient:dark_blue:blue>Recipe: {recipe_name}</gradient>"
  size: 54

# Customize slot positions
layout:
  result-slot: 22
  base-slot: 20
  ingredient-slot: 24
  brewing-stand-slot: 13
  arrow-1-slot: 21
  arrow-2-slot: 23
  arrow-3-slot: 31
  effects-slot: 15
  conditions-slot: 33
  type-info-slot: 11

# Brewing stand visualization
brewing-stand:
  material: BREWING_STAND
  name: "<gradient:gold:orange>⚗ Brewing Process</gradient>"
  lore:
    - "<gray>1. Place base potion in brewing stand"
    - "<gray>2. Add ingredient to top slot"
    - "<gray>3. Add blaze powder as fuel"
    - "<gray>4. Wait for brewing to complete"
    - ""
    - "<yellow>Brew Time: <white>{brew_time} seconds"
  custom-model-data: 0

# Effects panel
effects-panel:
  material: GLOWSTONE_DUST
  name: "<gradient:light_purple:purple>✨ Potion Effects</gradient>"
  lore-header:
    - "<gray>This potion grants:"
  effect-line: "<gray>• <white>{effect_name} {level} <dark_gray>({duration})"
  no-effects: "<gray>No special effects"

# Conditions panel
conditions-panel:
  material: REDSTONE
  name: "<gradient:red:dark_red>⚠ Brewing Conditions</gradient>"
  lore-header:
    - "<gray>This recipe requires:"
  condition-line: "<gray>• {condition_description}"
  no-conditions: "<green>No special conditions required"
```

### Navigation Items

```yaml
navigation:
  previous-page:
    slot: 45
    material: ARROW
    name: "<yellow>← Previous Page"
    lore:
      - "<gray>Go to page {previous_page}"
    custom-model-data: 0
  
  close:
    slot: 50
    material: BARRIER
    name: "<red>Close"
    custom-model-data: 0
```

### Global Settings

```yaml
global:
  sounds:
    open-gui: BLOCK_CHEST_OPEN
    close-gui: BLOCK_CHEST_CLOSE
    click-item: UI_BUTTON_CLICK
    page-turn: ITEM_BOOK_PAGE_TURN
    error: ENTITY_VILLAGER_NO
  
  prevent-item-pickup: true
  close-on-click-outside: true
```

## Placeholders

### Recipe Book
- `{recipe_name}` - Recipe display name
- `{brew_time}` - Brewing time in seconds
- `{potion_type}` - NORMAL, SPLASH, or LINGERING
- `{current_page}` - Current page number
- `{total_pages}` - Total number of pages
- `{discovered_count}` - Number of discovered recipes
- `{total_count}` - Total number of recipes
- `{percentage}` - Completion percentage

### Recipe Details
- `{recipe_name}` - Recipe display name
- `{base_material}` - Base potion material
- `{ingredient_name}` - Ingredient display name
- `{brew_time}` - Brewing time
- `{potion_type}` - Potion type
- `{effect_name}` - Effect name (in lore)
- `{level}` - Effect level (in lore)
- `{duration}` - Effect duration (in lore)
- `{condition_description}` - Condition description (in lore)
- `{model_data}` - Custom model data value

### Achievements
- `{achievement_name}` - Achievement name
- `{achievement_description}` - Achievement description
- `{progress}` - Current progress
- `{target}` - Target value
- `{unlocked_count}` - Unlocked achievements
- `{total_count}` - Total achievements
- `{percentage}` - Completion percentage

### Special Placeholders
- `{permission}` - Required permission (in permission-locked recipes)
- `{condition_list}` - List of conditions (in condition-locked recipes)
- `{previous_page}` - Previous page number
- `{next_page}` - Next page number

## Custom Model Data

You can use custom model data for resource packs:

```yaml
locked-recipe:
  material: BARRIER
  name: "<dark_gray>??? Locked Recipe"
  custom-model-data: 100  # Your custom model ID
```

This allows you to have custom textures for:
- Locked recipes
- Navigation buttons
- Information panels
- Filler items
- Any GUI element!

## Examples

### Gradient Title with Emojis
```yaml
title: "<gradient:gold:yellow>✨ Recipe Book ✨</gradient>"
```

### Rainbow Navigation
```yaml
next-page:
  name: "<rainbow>Next Page →</rainbow>"
```

### Hex Color Lore
```yaml
lore:
  - "<#FF5555>Custom red color"
  - "<#00AAFF>Custom blue color"
```

### Custom Model Data for Resource Packs
```yaml
filler:
  material: GRAY_STAINED_GLASS_PANE
  name: " "
  custom-model-data: 1001  # Your custom texture
```

## Usage in Code

The GUIConfigManager provides access to all GUI configs:

```java
GUIConfigManager guiConfigManager = plugin.getGUIConfigManager();

// Access specific configs
RecipeBookGUIConfig recipeBook = guiConfigManager.getRecipeBookConfig();
RecipeDetailsGUIConfig recipeDetails = guiConfigManager.getRecipeDetailsConfig();
AchievementsGUIConfig achievements = guiConfigManager.getAchievementsConfig();

// Get title with MiniMessage support
String title = recipeBook.getTitle();

// Get custom model data
int customModelData = recipeBook.getLockedRecipeCustomModelData();

// Get visibility settings
boolean hideUndiscovered = recipeBook.shouldHideUndiscovered();
boolean hideNoPermission = recipeBook.shouldHideNoPermission();

// Get navigation item config
Map<String, Object> navItem = recipeBook.getNavigationItem("next-page");

// Play sounds
Sound openSound = recipeBook.getOpenSound();
player.playSound(player.getLocation(), openSound, 1.0f, 1.0f);

// Check page permissions
if (recipeBook.isPagePermissionsEnabled()) {
    String permission = recipeBook.getPagePermission(2);
    if (permission != null && !player.hasPermission(permission)) {
        // Player can't access page 2
    }
}
```

## Reloading

All GUI configurations reload automatically with `/brewmasters reload`:

```
/brewmasters reload
```

This will reload:
- All three GUI config files (recipe-book.yml, recipe-details.yml, achievements.yml)
- config.yml
- All recipes
- All achievements
- All chains
- Discovery methods
- Speed settings

## Tips

1. **Test Your Colors** - Use the [MiniMessage Web Viewer](https://webui.advntr.dev/) to preview your formatting

2. **Keep Sizes Consistent** - GUI sizes must be multiples of 9 (9, 18, 27, 36, 45, 54)

3. **Slot Numbers** - Slots are 0-indexed (0-53 for a 54-slot inventory)

4. **Custom Models** - Set to 0 to disable custom model data

5. **Sounds** - Use valid Bukkit Sound enum names

6. **Placeholders** - Not all placeholders work in all contexts - check the lists above

## Troubleshooting

**GUI doesn't update after editing config files?**
- Run `/brewmasters reload`
- Make sure you edited the correct file in the `guis/` folder

**Colors not showing?**
- Make sure you're using MiniMessage format: `<gradient:red:blue>Text</gradient>`
- Check for typos in color names

**Custom model data not working?**
- Ensure your resource pack is installed
- Verify the custom-model-data value matches your pack
- Set to 0 to disable

**Sounds not playing?**
- Check the Sound enum name is correct
- Sounds are case-sensitive

## Future Features

- Animation support for page transitions
- More customizable layouts
- Per-player GUI preferences
- GUI templates

## Advanced Features

### Permission-Based Recipe Locking

Recipes can require permissions to view:

```yaml
# In your recipe config
my_special_recipe:
  # ... recipe settings ...
  conditions:
    permission: "brewmasters.recipe.special"
    permission-required: true
```

If the player doesn't have the permission and `hide-no-permission: true`, the recipe won't show in the book.

### Condition-Based Recipe Locking

Recipes can require specific conditions (biome, time, weather, etc.):

```yaml
# In your recipe config
night_brew:
  # ... recipe settings ...
  conditions:
    time:
      min: 13000
      max: 23000
```

If conditions aren't met and `hide-unmet-conditions: true`, the recipe won't show.

### Discovery-Based Hiding

Hide recipes until players discover them:

```yaml
# In guis/recipe-book.yml
visibility:
  hide-undiscovered: true  # Only show discovered recipes
  show-locked-placeholders: false  # Don't show placeholders
```

### Page Permissions

Lock entire pages behind permissions:

```yaml
# In guis/recipe-book.yml
page-permissions:
  enabled: true
  "2": "brewmasters.recipes.advanced"  # Page 2 requires this permission
  "3": "brewmasters.recipes.expert"    # Page 3 requires this permission
```

### Per-Page Item Limits

Set different item limits for each page:

```yaml
# In guis/recipe-book.yml
per-page-limits:
  enabled: true
  "1": 12   # Page 1: Featured recipes (fewer items)
  "2": 28   # Page 2: Normal amount
  "3": 10   # Page 3: Legendary recipes (exclusive)
```

**Use Cases:**
- Featured/starter page with fewer items
- Exclusive legendary page
- Different layouts per page
- Create visual hierarchy

### Recipe Page Assignments

Assign specific recipes to specific pages:

```yaml
# In guis/recipe-book.yml
recipe-page-assignments:
  enabled: true
  
  # Basic recipes on page 1
  "fire_resistance": 1
  "super_speed": 1
  "instant_health": 1
  "night_vision": 1
  
  # Advanced recipes on page 2
  "alchemist_brew": 2
  "soul_wither": 2
  "ender_shift": 2
  
  # Legendary recipes on page 3 (with permission)
  "netherite_defense": 3
  "echo_resonance": 3
  "master_alchemist_brew": 3
```

**Benefits:**
- Organize recipes by theme/category
- Combine with page permissions for progression
- Control exactly what appears on each page
- Unassigned recipes auto-fill remaining slots

### Recipe Slot Assignments

Assign recipes to specific inventory slots for complete layout control:

```yaml
# In guis/recipe-book.yml
recipe-slot-assignments:
  enabled: true
  
  # Featured legendary in center
  "netherite_defense": 13
  
  # Create themed rows:
  # Row 1 (slots 0-8): Fire potions
  "fire_resistance": 0
  "nether_essence": 1
  "soul_wither": 2
  
  # Row 2 (slots 9-17): Water potions
  "instant_health": 9
  "super_speed": 10
  
  # Row 3 (slots 18-26): Special potions
  "alchemist_brew": 18
  "ender_shift": 19
```

**Benefits:**
- Complete control over recipe positioning
- Create custom layouts and designs
- Feature important recipes in prominent positions
- Organize by visual themes
- Slots 0-53 available (avoid navigation slots 45-53)

**Example Use Case:**
```yaml
# Page 1: Free for everyone (basic potions)
# Page 2: Requires "brewmasters.recipes.advanced" permission
# Page 3: Requires "brewmasters.recipes.legendary" permission

page-permissions:
  enabled: true
  "2": "brewmasters.recipes.advanced"
  "3": "brewmasters.recipes.legendary"

recipe-page-assignments:
  enabled: true
  # Assign legendary recipes to page 3
  "netherite_defense": 3
  "echo_resonance": 3
  # Assign advanced recipes to page 2
  "alchemist_brew": 2
  "soul_wither": 2
  # Page 1 gets all unassigned recipes automatically
```

---

**Created**: Part of the GUI Config System  
**Files**: `src/main/resources/guis/*.yml`  
**Config Classes**: `net.pwing.brewmasters.gui.config.*`
