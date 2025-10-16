# BrewMasters - Potion Commands Guide

This guide explains how to use the **drink-commands** and **expire-commands** features to execute commands when players drink custom potions or when their effects expire.

## Overview

BrewMasters allows you to execute commands at two key moments:
1. **When the potion is drunk** - Commands run immediately as the player consumes the potion
2. **When the effects expire** - Commands run when the potion's effects wear off

This provides incredible flexibility for creating custom mechanics, quests, temporary buffs, and more!

## Configuration

Add these sections to your recipe configuration:

```yaml
recipes:
  example_potion:
    base-potion: AWKWARD_POTION
    ingredient: DIAMOND
    brew-time: 600
    result:
      name: "&b&lElite Warrior Potion"
      lore:
        - "&7Grants temporary warrior status"
      color: "#00FFFF"
      effects:
        - type: STRENGTH
          duration: 1200  # 60 seconds
          amplifier: 2
        - type: SPEED
          duration: 1200
          amplifier: 1
    
    # Commands to run when player drinks the potion
    drink-commands:
      - "[console] lp user {player} parent addtemp warrior 60s"
      - "[player] me has become a warrior!"
      - "give {player} iron_sword 1"
    
    # Commands to run when the potion effects expire
    expire-commands:
      - "[console] lp user {player} parent remove warrior"
      - "tellraw {player} {\"text\":\"Your warrior status has expired!\",\"color\":\"red\"}"
```

## Command Syntax

### Command Prefixes

Commands can be executed by either the console or the player:

- **`[console]`** - Execute as console (recommended for most commands)
- **`[player]`** - Execute as the player who drank the potion
- **No prefix** - Defaults to console execution

### Available Placeholders

| Placeholder | Description | Example Output |
|------------|-------------|----------------|
| `{player}` | Player's username | `Steve` |
| `{uuid}` | Player's UUID | `069a79f4-44e9-4726-a5be-fca90e38aaf5` |
| `{recipe_id}` | Recipe ID | `elite_warrior` |
| `{recipe_name}` | Recipe display name | `Elite Warrior Potion` |

## Examples

### 1. Temporary Permission Groups

Grant a temporary permission group while the potion is active:

```yaml
vip_potion:
  base-potion: AWKWARD_POTION
  ingredient: EMERALD
  brew-time: 800
  result:
    name: "&6&lVIP Boost Potion"
    lore:
      - "&7Temporary VIP access!"
    color: "#FFD700"
    effects:
      - type: LUCK
        duration: 6000  # 5 minutes
        amplifier: 1
  
  drink-commands:
    - "[console] lp user {player} parent addtemp vip 5m"
    - "[console] tellraw {player} {\"text\":\"You now have VIP access for 5 minutes!\",\"color\":\"gold\"}"
  
  expire-commands:
    - "[console] lp user {player} parent remove vip"
    - "[console] playsound minecraft:entity.villager.no player {player} ~ ~ ~ 1 1"
```

### 2. Quest Progression

Integrate with quest plugins:

```yaml
quest_potion:
  base-potion: AWKWARD_POTION
  ingredient: ENDER_EYE
  brew-time: 1000
  result:
    name: "&5&lPotion of Enlightenment"
    lore:
      - "&7Advances your quest"
    color: "#9B30FF"
    effects:
      - type: GLOWING
        duration: 200  # 10 seconds
        amplifier: 0
  
  drink-commands:
    - "[console] quests nextstage {player} mystical_journey"
    - "[console] advancement grant {player} only custom:found_enlightenment"
    - "[player] me has achieved enlightenment!"
```

### 3. Economy Integration

Give or take money based on potion consumption:

```yaml
lucky_coin_potion:
  base-potion: AWKWARD_POTION
  ingredient: GOLD_BLOCK
  brew-time: 500
  result:
    name: "&e&lLucky Coin Potion"
    lore:
      - "&7Instant $1000 bonus!"
      - "&7But you'll pay it back..."
    color: "#FFD700"
    effects:
      - type: LUCK
        duration: 2400  # 2 minutes
        amplifier: 2
  
  drink-commands:
    - "[console] eco give {player} 1000"
    - "[console] tellraw {player} {\"text\":\"You gained $1000!\",\"color\":\"green\"}"
  
  expire-commands:
    - "[console] eco take {player} 1000"
    - "[console] tellraw {player} {\"text\":\"The loan has been collected!\",\"color\":\"red\"}"
```

### 4. Teleportation & Cooldowns

Create temporary teleport abilities:

```yaml
recall_potion:
  base-potion: AWKWARD_POTION
  ingredient: ENDER_PEARL
  brew-time: 600
  result:
    name: "&d&lRecall Potion"
    lore:
      - "&7Grants recall ability"
    color: "#DA70D6"
    effects:
      - type: SLOW_FALLING
        duration: 1200  # 60 seconds
        amplifier: 0
  
  drink-commands:
    - "[player] spawn"
    - "[console] playsound minecraft:entity.enderman.teleport player {player} ~ ~ ~ 1 0.5"
```

### 5. Boss Battle Mechanics

Create phase-based boss fight mechanics:

```yaml
berserker_rage:
  base-potion: AWKWARD_POTION
  ingredient: WITHER_ROSE
  brew-time: 1200
  result:
    name: "&4&lBerserker Rage"
    lore:
      - "&7Immense power... at a cost"
    color: "#8B0000"
    effects:
      - type: STRENGTH
        duration: 600  # 30 seconds
        amplifier: 4
      - type: SPEED
        duration: 600
        amplifier: 2
  
  drink-commands:
    - "[console] particle minecraft:angry_villager {player} ~ ~1 ~ 0.5 0.5 0.5 0.1 50"
    - "[console] playsound minecraft:entity.ravager.roar player @a ~ ~ ~ 10 0.8"
    - "[console] tellraw @a {\"text\":\"{player} has entered berserker mode!\",\"color\":\"dark_red\",\"bold\":true}"
  
  expire-commands:
    - "[console] effect give {player} weakness 200 2"
    - "[console] effect give {player} slowness 200 1"
    - "[console] tellraw {player} {\"text\":\"The rage subsides... exhaustion sets in.\",\"color\":\"gray\"}"
```

### 6. Parkour Course Timers

Perfect for timed challenges:

```yaml
speed_runner:
  base-potion: AWKWARD_POTION
  ingredient: SUGAR
  brew-time: 400
  result:
    name: "&a&lSpeed Runner Potion"
    lore:
      - "&730-second speed boost"
      - "&7Perfect for parkour!"
    color: "#00FF00"
    effects:
      - type: SPEED
        duration: 600  # 30 seconds
        amplifier: 3
      - type: JUMP_BOOST
        duration: 600
        amplifier: 2
  
  drink-commands:
    - "[console] timer start {player} 30"
    - "[console] title {player} title {\"text\":\"GO!\",\"color\":\"green\",\"bold\":true}"
  
  expire-commands:
    - "[console] timer stop {player}"
    - "[console] title {player} subtitle {\"text\":\"Time's up!\",\"color\":\"red\"}"
```

### 7. Combat Tournament System

```yaml
gladiator_potion:
  base-potion: AWKWARD_POTION
  ingredient: NETHERITE_SCRAP
  brew-time: 1000
  result:
    name: "&c&lGladiator's Brew"
    lore:
      - "&7Enter the arena!"
    color: "#DC143C"
    effects:
      - type: STRENGTH
        duration: 2400  # 2 minutes
        amplifier: 1
      - type: RESISTANCE
        duration: 2400
        amplifier: 1
  
  drink-commands:
    - "[console] tp {player} -100 64 -100"
    - "[console] gamemode adventure {player}"
    - "[console] clear {player}"
    - "[console] give {player} iron_sword 1"
    - "[console] give {player} iron_chestplate 1"
    - "[console] scoreboard players set {player} InArena 1"
  
  expire-commands:
    - "[console] tp {player} spawn"
    - "[console] gamemode survival {player}"
    - "[console] scoreboard players set {player} InArena 0"
    - "[console] tellraw {player} {\"text\":\"Tournament time expired!\",\"color\":\"yellow\"}"
```

## PlaceholderAPI Integration

You can use PlaceholderAPI placeholders in your commands for even more power:

```yaml
level_based_potion:
  base-potion: AWKWARD_POTION
  ingredient: EXPERIENCE_BOTTLE
  brew-time: 600
  result:
    name: "&d&lExperience Multiplier"
    effects:
      - type: LUCK
        duration: 1200
        amplifier: 1
  
  drink-commands:
    - "[console] tellraw {player} {\"text\":\"XP boost activated at level %player_level%!\",\"color\":\"aqua\"}"
    - "[console] xpboost {player} 2.0 60"
```

## Active Effect Tracking

BrewMasters automatically tracks active potion effects and can be used with PlaceholderAPI:

### Available Placeholders

- `%brewmasters_effect_time_<recipe_id>%` - Time remaining in seconds
- `%brewmasters_effect_time_formatted_<recipe_id>%` - Time remaining as MM:SS
- `%brewmasters_effect_active_<recipe_id>%` - Returns true/false if effect is active

### Example Usage in Scoreboard

```yaml
scoreboard:
  lines:
    - "&7Active Buffs:"
    - "&6Warrior: %brewmasters_effect_time_formatted_elite_warrior%"
    - "&aVIP: %brewmasters_effect_time_formatted_vip_potion%"
```

## Logout Handling

If a player logs out while a potion effect is active:
- The effect timer continues counting down
- When the player logs back in, if the effect expired while offline, the **expire-commands** will run immediately
- This prevents exploits and ensures commands always execute properly

## Important Notes

### Best Practices

1. **Use console commands** for most operations (more reliable)
2. **Test your commands** before deploying to production
3. **Use JSON text** for better formatting in tellraw commands
4. **Match effect duration** with your command timing
5. **Consider permission requirements** for commands

### Security Considerations

- Commands run with console permissions - be careful!
- Validate placeholders are used correctly
- Test with non-op players
- Consider using command-specific permissions

### Timing

- Drink commands execute immediately
- Expire commands execute when the **longest** effect in the recipe expires
- Effect duration is in ticks (20 ticks = 1 second)
- Expire commands are tracked even if the player logs out

## Troubleshooting

### Commands not executing

1. Check console for errors
2. Verify command syntax is correct
3. Ensure required plugins are installed (e.g., LuckPerms for permissions)
4. Test commands manually in console first

### Expire commands not running

1. Ensure the recipe has at least one effect with a duration
2. Check the `active-effects.yml` file in the plugin folder
3. Verify the player stayed online for the effect duration

### Placeholders not working

1. Make sure PlaceholderAPI is installed
2. Verify the placeholder syntax is correct
3. Check if the required PAPI expansion is installed

## Examples Repository

For more examples, check out the `example-recipes.yml` file included with the plugin!

## Support

Need help? Check out our documentation or ask on our support channels!
