# BrewMasters Recipe Creation Guide

This guide will help you create custom brewing recipes for BrewMasters.

## Table of Contents
1. [Basic Recipe Structure](#basic-recipe-structure)
2. [Ingredient Formats](#ingredient-formats)
3. [Ingredient Ideas](#ingredient-ideas)
4. [Color Codes](#color-codes)
5. [Potion Effects](#potion-effects)
6. [Advanced Features](#advanced-features)
7. [Recipe Examples](#recipe-examples)

## Basic Recipe Structure

Every recipe follows this structure:

```yaml
recipes:
  recipe_id:                    # Unique identifier (use lowercase and underscores)
    base-potion: MATERIAL       # Starting potion type
    ingredient: INGREDIENT      # Item to brew with (see Ingredient Formats below)
    brew-time: 400              # Time in ticks (optional, default: 400)
    result:
      name: "&cName"            # Display name with color codes
      lore:                     # Description lines (optional)
        - "&7Line 1"
        - "&7Line 2"
      color: "#RRGGBB"          # Potion color (hex or r,g,b)
      potion-type: NORMAL       # NORMAL, SPLASH, or LINGERING (optional)
      glowing: false            # Make it glow (optional)
      custom-model-data: 0      # For resource packs (optional)
      effects:                  # List of potion effects
        - type: EFFECT_NAME
          duration: 600         # Duration in ticks (20 ticks = 1 second)
          amplifier: 0          # Level (0 = I, 1 = II, etc.)
```

## Ingredient Formats

BrewMasters supports three types of ingredients:

### Vanilla Minecraft Items
Use the material name in all caps:
```yaml
ingredient: SOUL_SAND           # Single item
ingredient: DIAMOND:3           # Requires 3 diamonds
ingredient: NETHERITE_SCRAP:5   # Requires 5 netherite scraps
```

### MythicMobs Items
Use the `mythic:` prefix with the MythicMobs item ID:
```yaml
ingredient: mythic:DRAGON_SCALE              # Single MythicMobs item
ingredient: mythic:MAGIC_DUST:2              # Requires 2 magic dust
ingredient: mythic:ANCIENT_ARTIFACT:10       # Requires 10 ancient artifacts
```

**Requirements:**
- MythicMobs 5.6.1+ must be installed
- The item ID must match exactly (case-sensitive)
- Check `/brewmasters integrations` to verify MythicMobs is detected

### Crucible Items
Use the `crucible:` prefix with the Crucible item ID:
```yaml
ingredient: crucible:WISDOM_CRYSTAL          # Single Crucible item
ingredient: crucible:ANCIENT_RUNE:5          # Requires 5 ancient runes
ingredient: crucible:ENCHANTED_GEM:3         # Requires 3 enchanted gems
```

**Requirements:**
- MythicCrucible 2.2.0+ must be installed
- The item ID must match exactly (case-sensitive)
- Check `/brewmasters integrations` to verify Crucible is detected

### Amount Specification
For all ingredient types, you can specify the required amount:
- Format: `INGREDIENT:amount`
- Default amount: 1
- Examples:
  - `DIAMOND:3` - Requires 3 diamonds
  - `mythic:DRAGON_SCALE:5` - Requires 5 dragon scales
  - `crucible:WISDOM_CRYSTAL:10` - Requires 10 wisdom crystals

## Ingredient Ideas

### Nether Materials
- **SOUL_SAND** - Wither effects, dark magic, necromancy
- **SOUL_SOIL** - Speed, spirit-based effects
- **NETHERITE_SCRAP** - Ultimate defense, legendary potions
- **CRYING_OBSIDIAN** - Powerful protection, rare effects
- **BLAZE_ROD** - Fire-based, strength potions
- **WITHER_ROSE** - Wither damage, death effects
- **MAGMA_CREAM** - Fire resistance
- **GHAST_TEAR** - Regeneration

### End Materials
- **ENDER_PEARL** - Teleportation, dimensional effects
- **CHORUS_FRUIT** - Teleportation, levitation
- **SHULKER_SHELL** - Levitation, protection
- **DRAGON_BREATH** - Powerful multi-effect potions

### Deep Dark Materials
- **ECHO_SHARD** - Ancient power, enhanced senses
- **SCULK** - Stealth, darkness, perception

### Ocean Materials
- **PRISMARINE_SHARD** - Water breathing, ocean effects
- **HEART_OF_THE_SEA** - Ultimate ocean mastery
- **NAUTILUS_SHELL** - Water-based effects
- **TURTLE_HELMET** - Slowness, water breathing

### Rare Materials
- **AMETHYST_SHARD** - Clarity, luck, precision
- **COPPER_INGOT** - Conductivity, speed
- **EMERALD** - Luck, fortune, trading
- **DIAMOND** - Powerful, rare effects
- **GOLD_INGOT** - Speed, healing
- **IRON_INGOT** - Resistance, defense

### Nature Materials
- **RABBIT_FOOT** - Jump boost, luck
- **PHANTOM_MEMBRANE** - Slow falling, flight
- **SPIDER_EYE** - Poison
- **FERMENTED_SPIDER_EYE** - Negative effects, corruption

### MythicMobs & Crucible Items
If you have MythicMobs or Crucible installed, you can use any custom items from those plugins:
```yaml
# MythicMobs examples
ingredient: mythic:DRAGON_SCALE
ingredient: mythic:MAGIC_DUST:2
ingredient: mythic:ANCIENT_ARTIFACT

# Crucible examples
ingredient: crucible:WISDOM_CRYSTAL
ingredient: crucible:ENCHANTED_GEM:3
ingredient: crucible:ANCIENT_RUNE:5
```

**Benefits:**
- Create unique recipes using your custom items
- Integrate brewing with your custom item economy
- Make legendary potions require legendary ingredients
- Perfect for RPG servers with custom items

## Color Codes

### Text Color Codes (use & symbol)
- `&0` - Black
- `&1` - Dark Blue
- `&2` - Dark Green
- `&3` - Dark Aqua
- `&4` - Dark Red
- `&5` - Dark Purple
- `&6` - Gold
- `&7` - Gray
- `&8` - Dark Gray
- `&9` - Blue
- `&a` - Green
- `&b` - Aqua
- `&c` - Red
- `&d` - Light Purple
- `&e` - Yellow
- `&f` - White

### Text Formatting
- `&l` - Bold
- `&m` - Strikethrough
- `&n` - Underline
- `&o` - Italic
- `&r` - Reset

### Potion Colors (hex format)
Use hex colors for potion liquid color:
- `"#FF0000"` - Red
- `"#00FF00"` - Green
- `"#0000FF"` - Blue
- `"#FFFF00"` - Yellow
- `"#FF00FF"` - Magenta
- `"#00FFFF"` - Cyan
- `"#FFA500"` - Orange
- `"#800080"` - Purple
- `"#FFC0CB"` - Pink
- `"#A52A2A"` - Brown

Or use RGB format: `"255,0,0"` for red

## Potion Effects

### Positive Effects
- **SPEED** - Increased movement speed
- **HASTE** - Faster mining/attacking
- **STRENGTH** - Increased melee damage
- **JUMP_BOOST** - Higher jumps
- **REGENERATION** - Health regeneration
- **RESISTANCE** - Damage reduction
- **FIRE_RESISTANCE** - Immunity to fire/lava
- **WATER_BREATHING** - Breathe underwater
- **INVISIBILITY** - Become invisible
- **NIGHT_VISION** - See in the dark
- **HEALTH_BOOST** - Extra hearts
- **ABSORPTION** - Extra yellow hearts
- **SATURATION** - Instant hunger restoration
- **GLOWING** - Outline effect
- **LUCK** - Better loot
- **SLOW_FALLING** - Fall slowly
- **CONDUIT_POWER** - Underwater mining speed
- **DOLPHINS_GRACE** - Faster swimming
- **HERO_OF_THE_VILLAGE** - Trading discounts

### Negative Effects
- **SLOWNESS** - Decreased movement speed
- **MINING_FATIGUE** - Slower mining
- **INSTANT_DAMAGE** - Instant damage
- **NAUSEA** - Screen wobble
- **BLINDNESS** - Darkness
- **HUNGER** - Faster hunger depletion
- **WEAKNESS** - Reduced melee damage
- **POISON** - Damage over time (doesn't kill)
- **WITHER** - Damage over time (can kill)
- **LEVITATION** - Float upward
- **UNLUCK** - Worse loot
- **BAD_OMEN** - Triggers raids
- **DARKNESS** - Darkness effect

### Instant Effects
For instant effects like HEAL or INSTANT_DAMAGE, set duration to 1:
```yaml
effects:
  - type: HEAL
    duration: 1
    amplifier: 0
```

## Advanced Features

### Splash Potions
```yaml
result:
  potion-type: SPLASH
```

### Lingering Potions
```yaml
result:
  potion-type: LINGERING
```

### Glowing Effect
Makes the potion glow in inventory (like enchanted items):
```yaml
result:
  glowing: true
```

### Custom Model Data
For use with resource packs:
```yaml
result:
  custom-model-data: 12345
```

### Multiple Effects
Combine multiple effects for powerful potions:
```yaml
effects:
  - type: STRENGTH
    duration: 1800
    amplifier: 1
  - type: RESISTANCE
    duration: 1800
    amplifier: 0
  - type: REGENERATION
    duration: 900
    amplifier: 0
```

## Recipe Examples

### Soul Sand Wither Potion
```yaml
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
```

### Netherite Ultimate Defense
```yaml
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
```

### Echo Shard Ancient Power
```yaml
echo_resonance:
  base-potion: AWKWARD_POTION
  ingredient: ECHO_SHARD
  brew-time: 1000
  result:
    name: "&b&lPotion of Echo Resonance"
    lore:
      - "&7Channel the power of the deep dark"
      - "&3&lMythic"
    color: "#008B8B"
    glowing: true
    effects:
      - type: NIGHT_VISION
        duration: 6000
        amplifier: 0
      - type: STRENGTH
        duration: 2400
        amplifier: 1
      - type: RESISTANCE
        duration: 2400
        amplifier: 0
```

### Splash Healing Potion
```yaml
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

## Tips

1. **Balance is Key** - Don't make potions too overpowered
2. **Thematic Ingredients** - Match ingredients to effects (soul sand = wither, ocean items = water breathing)
3. **Brew Time** - Rare/powerful potions should take longer (600-1200 ticks)
4. **Color Matching** - Choose colors that match the effect theme
5. **Lore Matters** - Good lore makes potions feel special
6. **Test Everything** - Always test your recipes in-game
7. **Use Glowing Sparingly** - Reserve it for legendary/rare potions
8. **Duration Balance** - 1200-3600 ticks (1-3 minutes) is usually good
9. **Amplifier Limits** - Keep amplifiers reasonable (0-2 for most effects)
10. **Multiple Effects** - 2-4 effects work well together, more can be overwhelming

## Common Issues

**Potion doesn't brew:**
- Check that material names are correct (all caps, underscores)
- Verify base-potion is a valid potion type
- Make sure recipe ID is unique

**Colors don't show:**
- Use hex format: `"#RRGGBB"` with quotes
- Or RGB format: `"r,g,b"` with quotes

**Effects don't work:**
- Check effect type names (all caps, underscores)
- Verify duration is in ticks (not seconds)
- Make sure amplifier is a number (0, 1, 2, etc.)

**Text colors don't appear:**
- Use `&` for color codes, not `§`
- Make sure to use quotes around text with color codes

## Need More Help?

Check `example-recipes.yml` for more recipe examples!

