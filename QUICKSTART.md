# BrewMasters Quick Start Guide

Get started with BrewMasters in 5 minutes!

## Installation

1. **Download** the plugin JAR file
2. **Place** it in your server's `plugins` folder
3. **Restart** your server
4. The plugin will create a `plugins/BrewMasters` folder with `config.yml`

## Your First Custom Recipe

Let's create a simple custom potion using **Soul Sand** as the ingredient!

### Step 1: Open the Config

Navigate to `plugins/BrewMasters/config.yml` and open it in a text editor.

### Step 2: Add Your Recipe

Add this recipe to the `recipes:` section:

```yaml
recipes:
  my_first_potion:
    base-potion: AWKWARD_POTION
    ingredient: SOUL_SAND
    brew-time: 400
    result:
      name: "&5My First Custom Potion"
      lore:
        - "&7This is my first custom brew!"
        - "&7Made with soul sand"
      color: "#9B30FF"
      effects:
        - type: SPEED
          duration: 1200
          amplifier: 0
        - type: JUMP_BOOST
          duration: 1200
          amplifier: 0
```

### Step 3: Reload the Plugin

In-game, run:
```
/brewmasters reload
```

You should see: `BrewMasters configuration reloaded! Loaded X recipes.`

### Step 4: Brew Your Potion!

1. **Get an Awkward Potion** (brew Nether Wart with a Water Bottle)
2. **Place it** in a brewing stand
3. **Add Soul Sand** as the ingredient
4. **Wait** for it to brew
5. **Enjoy** your custom potion!

## Testing Your Recipe

To verify your recipe works:

1. Run `/brewmasters list` to see all loaded recipes
2. Check that your recipe appears in the list
3. Try brewing it in-game

## Common Mistakes

❌ **Wrong material name**
```yaml
ingredient: soul_sand  # Wrong - use SOUL_SAND
```

✅ **Correct material name**
```yaml
ingredient: SOUL_SAND  # Correct - all caps with underscores
```

❌ **Wrong effect name**
```yaml
type: speed  # Wrong - use SPEED
```

✅ **Correct effect name**
```yaml
type: SPEED  # Correct - all caps
```

❌ **Duration in seconds**
```yaml
duration: 60  # This is only 3 seconds!
```

✅ **Duration in ticks**
```yaml
duration: 1200  # This is 60 seconds (1200 ticks ÷ 20 = 60 seconds)
```

## More Recipe Ideas

### Soul Sand Wither Potion
```yaml
soul_wither:
  base-potion: AWKWARD_POTION
  ingredient: SOUL_SAND
  result:
    name: "&8Potion of Withering"
    color: "#2C1810"
    effects:
      - type: WITHER
        duration: 600
        amplifier: 0
```

### Netherite Super Defense
```yaml
netherite_shield:
  base-potion: AWKWARD_POTION
  ingredient: NETHERITE_SCRAP
  brew-time: 800
  result:
    name: "&4Netherite Shield"
    color: "#654321"
    glowing: true
    effects:
      - type: RESISTANCE
        duration: 2400
        amplifier: 1
      - type: FIRE_RESISTANCE
        duration: 2400
        amplifier: 0
```

### Echo Shard Night Vision
```yaml
echo_sight:
  base-potion: AWKWARD_POTION
  ingredient: ECHO_SHARD
  result:
    name: "&bEcho Sight"
    color: "#008B8B"
    effects:
      - type: NIGHT_VISION
        duration: 4800
        amplifier: 0
```

## Understanding Durations

Duration is measured in **ticks**. There are **20 ticks per second**.

| Seconds | Ticks | Common Use |
|---------|-------|------------|
| 10s | 200 | Very short buff |
| 30s | 600 | Short buff |
| 1min | 1200 | Standard buff |
| 2min | 2400 | Long buff |
| 3min | 3600 | Extended buff |
| 5min | 6000 | Very long buff |
| 8min | 9600 | Maximum vanilla duration |

## Understanding Amplifiers

Amplifier determines the **level** of the effect:

| Amplifier | Level | Display |
|-----------|-------|---------|
| 0 | 1 | Effect I |
| 1 | 2 | Effect II |
| 2 | 3 | Effect III |
| 3 | 4 | Effect IV |

Example:
```yaml
- type: STRENGTH
  duration: 1200
  amplifier: 1  # This gives Strength II for 60 seconds
```

## Color Codes Quick Reference

### Text Colors (use & in names/lore)
- `&0` Black
- `&1` Dark Blue
- `&2` Dark Green
- `&3` Dark Aqua
- `&4` Dark Red
- `&5` Dark Purple
- `&6` Gold
- `&7` Gray
- `&8` Dark Gray
- `&9` Blue
- `&a` Green
- `&b` Aqua
- `&c` Red
- `&d` Light Purple
- `&e` Yellow
- `&f` White
- `&l` **Bold**

### Potion Colors (hex format)
Common colors for potions:
- `"#FF0000"` - Red (healing, strength)
- `"#0000FF"` - Blue (water breathing)
- `"#00FF00"` - Green (poison)
- `"#FFFF00"` - Yellow (absorption)
- `"#FF00FF"` - Magenta (regeneration)
- `"#FFA500"` - Orange (fire resistance)
- `"#800080"` - Purple (night vision)
- `"#2C1810"` - Dark brown (wither)

## Commands

- `/brewmasters reload` - Reload config (use after editing recipes)
- `/brewmasters list` - Show all loaded recipes
- `/brewmasters help` - Show help

Shortcuts: `/bm` or `/brew`

## Next Steps

1. ✅ Create your first recipe
2. ✅ Test it in-game
3. 📖 Read `RECIPE_GUIDE.md` for advanced features
4. 🎨 Check `example-recipes.yml` for more ideas
5. 🚀 Create your own unique potions!

## Need Help?

- **Recipe not working?** Check material names are in ALL_CAPS
- **Colors not showing?** Make sure to use quotes around hex colors
- **Effects not applying?** Verify effect names and check console for errors
- **Want more examples?** See `example-recipes.yml` and `RECIPE_GUIDE.md`

Happy Brewing! 🧪✨

