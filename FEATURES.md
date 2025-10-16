# BrewMasters - Complete Feature List

## 🎯 NEW: Recipe Conditions System

Control when and where recipes can be brewed with powerful conditions!

### Condition Types

1. **Biome Conditions** - Restrict recipes to specific biomes
   - Whitelist or blacklist mode
   - Support for all Minecraft biomes
   - Example: Desert-only potions, Nether-only brews

2. **World Conditions** - Limit recipes to specific worlds
   - Whitelist or blacklist mode
   - Perfect for dimension-specific recipes
   - Example: Nether-only, End-only, or overworld-only

3. **Permission Conditions** - Require specific permissions
   - Great for VIP/donor recipes
   - Rank-based brewing
   - Example: Master alchemist recipes

4. **Time Conditions** - Restrict to specific times of day
   - Day/night cycles
   - Specific time ranges
   - Example: Moonlight potions, sunrise brews

5. **Weather Conditions** - Require specific weather
   - Clear, rain, thunder, or any storm
   - Example: Thunderstorm-only potions

6. **Y-Level Conditions** - Height-based restrictions
   - Deep underground recipes
   - High altitude brews
   - Example: Deep dark potions, mountain brews

7. **PlaceholderAPI Conditions** - Advanced conditions
   - Player level requirements
   - Economy balance checks
   - Rank/group requirements
   - Custom placeholder comparisons
   - 10+ comparison operators

### Combining Conditions

- Mix and match multiple conditions
- ALL conditions must be met
- Create complex, challenging recipes
- Example: "Must be in Nether, at night, with permission, and level 50+"

See **CONDITIONS_GUIDE.md** for complete documentation!

## Core Features

### ✅ Custom Brewing Recipes
- Define unlimited custom brewing recipes
- Use any Minecraft material as an ingredient
- Support for all base potion types (normal, splash, lingering)
- Configurable brewing times

### ✅ Potion Customization
- **Custom Names** - Set display names with full color code support
- **Custom Lore** - Add multiple lines of description with colors
- **Custom Colors** - RGB or hex color codes for potion liquid
- **Custom Effects** - Add any combination of potion effects
- **Effect Duration** - Control how long effects last (in ticks)
- **Effect Amplifier** - Set effect levels (I, II, III, etc.)

### ✅ Advanced Features
- **Splash Potions** - Full support for throwable potions
- **Lingering Potions** - Create area-of-effect cloud potions
- **Glowing Effect** - Make potions glow like enchanted items
- **Custom Model Data** - Support for resource pack custom models
- **Multiple Effects** - Combine multiple potion effects in one potion

### ✅ Unique Ingredients
Support for any Minecraft material including:
- **Vanilla Materials**:
  - **Nether Materials**: Soul Sand, Soul Soil, Netherite Scrap, Crying Obsidian, Wither Rose
  - **End Materials**: Ender Pearl, Chorus Fruit, Shulker Shell, Dragon Breath
  - **Deep Dark**: Echo Shard, Sculk
  - **Ocean**: Prismarine Shard, Heart of the Sea, Nautilus Shell
  - **Rare Materials**: Amethyst Shard, Diamond, Emerald, Gold, Iron
  - **Nature**: Rabbit Foot, Phantom Membrane, Spider Eye
  - And literally any other Minecraft item!
- **MythicMobs Items**: Use custom items from MythicMobs as brewing ingredients
- **Crucible Items**: Use custom items from MythicCrucible as brewing ingredients
- **Amount Support**: Require multiple items for powerful recipes (e.g., `DIAMOND:3`, `mythic:DRAGON_SCALE:5`)

### ✅ Configuration
- YAML-based configuration
- Hot reload without server restart
- Detailed error messages for invalid recipes
- Example recipes included
- Comprehensive documentation

### ✅ Commands & Permissions
- `/brewmasters reload` - Reload configuration
- `/brewmasters list` - List all recipes with details
- `/brewmasters integrations` - Show MythicMobs/Crucible integration status
- `/brewmasters help` - Show help
- Aliases: `/bm`, `/brew`
- Permission-based access control

## Technical Features

### Code Quality
- Clean, well-documented code
- Builder pattern for recipe creation
- Modular architecture
- Event-driven brewing system
- Efficient recipe lookup

### Compatibility
- Paper/Spigot 1.20.1+
- Java 17+
- Gradle build system
- Shadow plugin for dependency shading
- Optional integration with MythicMobs 5.6.1+
- Optional integration with MythicCrucible 2.2.0+
- Optional integration with PlaceholderAPI

### Performance
- Efficient recipe matching
- Minimal server overhead
- Async-safe operations
- No database required

## Recipe System

### Supported Base Potions
- `POTION` - Normal drinkable potions
- `SPLASH_POTION` - Throwable potions
- `LINGERING_POTION` - Area-effect potions
- `WATER_BOTTLE` - Start from water
- `AWKWARD_POTION` - Standard brewing base

### Supported Potion Effects
All vanilla Minecraft potion effects:
- **Positive**: Speed, Haste, Strength, Jump Boost, Regeneration, Resistance, Fire Resistance, Water Breathing, Invisibility, Night Vision, Health Boost, Absorption, Saturation, Glowing, Luck, Slow Falling, Conduit Power, Dolphins Grace, Hero of the Village
- **Negative**: Slowness, Mining Fatigue, Instant Damage, Nausea, Blindness, Hunger, Weakness, Poison, Wither, Levitation, Unluck, Bad Omen, Darkness
- **Instant**: Heal, Instant Damage

### Color System
- **Hex Colors**: `"#RRGGBB"` format
- **RGB Colors**: `"r,g,b"` format
- **Text Colors**: Minecraft color codes (`&a`, `&b`, etc.)
- **Formatting**: Bold, italic, underline, strikethrough

## Example Use Cases

### 1. Soul-Based Alchemy
Create dark magic potions using soul sand and soul soil:
```yaml
soul_wither:
  ingredient: SOUL_SAND
  effects:
    - type: WITHER
```

### 2. Legendary Potions
Use rare materials like netherite for powerful effects:
```yaml
netherite_defense:
  ingredient: NETHERITE_SCRAP
  glowing: true
  effects:
    - type: RESISTANCE
      amplifier: 2
```

### 3. Ancient Powers
Harness deep dark materials:
```yaml
echo_resonance:
  ingredient: ECHO_SHARD
  effects:
    - type: NIGHT_VISION
    - type: STRENGTH
```

### 4. Combat Potions
Create splash potions for PvP:
```yaml
splash_healing:
  base-potion: SPLASH_POTION
  potion-type: SPLASH
  effects:
    - type: HEAL
```

### 5. Utility Potions
Combine multiple effects for exploration:
```yaml
explorer_brew:
  effects:
    - type: NIGHT_VISION
    - type: SPEED
    - type: WATER_BREATHING
```

## Documentation

### Included Files
- **README.md** - Main documentation
- **QUICKSTART.md** - 5-minute getting started guide
- **RECIPE_GUIDE.md** - Comprehensive recipe creation guide
- **FEATURES.md** - This file, complete feature list
- **example-recipes.yml** - 20+ example recipes
- **LICENSE** - MIT License

### Example Recipes Included
1. Fire Resistance
2. Super Speed
3. Instant Health
4. Night Vision
5. Alchemist's Elixir (multi-effect)
6. Invisibility
7. Soul Wither (soul sand)
8. Soul Speed (soul soil)
9. Splash Healing
10. Lingering Strength
11. Ender Shift (ender pearl)
12. Netherite Defense (netherite scrap)
13. Echo Resonance (echo shard)
14. Amethyst Clarity (amethyst shard)
15. And many more in example-recipes.yml!

## Future Enhancement Ideas

Potential features for future versions:
- [x] Recipe discovery system
- [ ] Brewing achievements
- [x] Recipe books/GUI
- [ ] Brewing skill levels
- [ ] Particle effects on brewing
- [ ] Sound effects customization
- [ ] Brewing stand GUI customization
- [ ] Recipe categories/tags
- [ ] Import/export recipes
- [ ] Recipe sharing system
- [ ] Brewing statistics
- [x] Custom brewing stand speeds
- [x] Multi-step brewing chains
- [x] Conditional recipes (permissions, time, location)


## Plugin Statistics

- **Lines of Code**: ~3000+
- **Classes**: 36
- **Configuration Options**: 10+
- **Supported Effects**: 30+
- **Example Recipes**: 20+
- **Documentation Pages**: 5

## Support & Community

- **Author**: finder17
- **Group**: net.pwing
- **License**: MIT
- **Version**: 1.0.0
- **API Version**: 1.20

## Why BrewMasters?

### For Server Owners
- Easy to configure
- No database setup
- Hot reload support
- Extensive documentation
- Active development

### For Players
- Unique custom potions
- New brewing possibilities
- Thematic ingredients
- Beautiful potion designs
- Engaging gameplay

### For Developers
- Clean, readable code
- Well-documented API
- Modular design
- Easy to extend
- Open source (MIT)

## Getting Started

1. **Install** - Drop JAR in plugins folder
2. **Configure** - Edit config.yml
3. **Reload** - `/brewmasters reload`
4. **Brew** - Create your potions!

See **QUICKSTART.md** for detailed instructions.

## Advanced Usage

### Resource Pack Integration
Use `custom-model-data` to integrate with resource packs:
```yaml
result:
  custom-model-data: 12345
```

### Balanced Recipes
Tips for creating balanced recipes:
- Rare ingredients = powerful effects
- Long brew times = better potions
- Multiple effects = higher cost
- Glowing = legendary tier

### Thematic Design
Match ingredients to effects:
- Soul materials → Wither, dark effects
- Ocean materials → Water breathing
- Nether materials → Fire resistance
- End materials → Teleportation
- Deep dark → Stealth, perception

## Conclusion

BrewMasters provides a complete, flexible, and powerful custom brewing system for Minecraft servers. With support for any ingredient, custom effects, colors, and advanced features like splash/lingering potions, it opens up endless possibilities for unique alchemy systems.

Happy Brewing! 🧪✨

